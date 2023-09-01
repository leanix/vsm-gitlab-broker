package net.leanix.vsm.gitlab.broker.connector.application

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.leanix.vsm.gitlab.broker.connector.domain.EventType
import net.leanix.vsm.gitlab.broker.connector.domain.GitlabProvider
import net.leanix.vsm.gitlab.broker.connector.domain.MergeRequest
import net.leanix.vsm.gitlab.broker.connector.domain.ProjectChange
import net.leanix.vsm.gitlab.broker.connector.domain.RepositoryProvider
import net.leanix.vsm.gitlab.broker.connector.domain.WebhookEventType
import net.leanix.vsm.gitlab.broker.connector.domain.getNamespace
import net.leanix.vsm.gitlab.broker.shared.cache.AssignmentsCache
import net.leanix.vsm.gitlab.broker.shared.exception.GitlabPayloadNotSupportedException
import net.leanix.vsm.gitlab.broker.shared.exception.GitlabTokenException
import net.leanix.vsm.gitlab.broker.shared.exception.NamespaceNotMatchException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

val PROJECT_EVENTS = listOf("project_create", "project_update", "project_rename", "project_transfer")
val mapper: ObjectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

@Service
class WebhookConsumerService(
    @Value("\${leanix.vsm.connector.api-user-token}") private val apiUserToken: String,
    private val repositoryProvider: RepositoryProvider,
    private val gitlabProvider: GitlabProvider,
) : BaseConnectorService() {

    fun consumeWebhookEvent(payloadToken: String?, payload: String) {
        if (payloadToken == null || payloadToken != apiUserToken) {
            throw GitlabTokenException(payloadToken)
        }

        when (computeWebhookEventType(payload)) {
            WebhookEventType.REPOSITORY -> processProject(payload)
            WebhookEventType.MERGE_REQUEST -> processMergeRequest(payload)
        }
    }

    private fun processProject(payload: String) {
        val project = mapper.readValue<ProjectChange>(payload)

        AssignmentsCache.get(project.getNamespace())
            ?.also { gitlabAssignment ->
                runCatching {
                    repositoryProvider.save(
                        gitlabProvider.getRepositoryByPath(project.pathWithNamespace),
                        gitlabAssignment,
                        EventType.CHANGE
                    )
                }.onSuccess {
                    logInfoMessages("vsm.repos.imported", arrayOf(project.pathWithNamespace), gitlabAssignment)
                }.onFailure {
                    logFailedStatus("Error processing project: ${it.message}", gitlabAssignment)
                    throw it
                }
            }
            ?: throw NamespaceNotMatchException(project.getNamespace())
    }

    @Suppress("ForbiddenComment")
    private fun processMergeRequest(payload: String) {
        val mergeRequest = mapper.readValue<MergeRequest>(payload)
        // TODO: will be implemented later when we decide if we are fetching DORA metrics directly from Gitlab or not
        println(mergeRequest)
    }

    companion object {

        fun computeWebhookEventType(payload: String): WebhookEventType {
            val node = mapper.readTree(payload)

            return if (PROJECT_EVENTS.contains(node.at("/event_name").asText())) {
                WebhookEventType.REPOSITORY
            } else if (
                node.at("/event_type").asText() == "merge_request" &&
                node.at("/object_attributes/action").asText() == "merge" &&
                node.path("/project/default_branch").asText()
                == node.path("/object_attributes/target_branch").asText()
            ) {
                WebhookEventType.MERGE_REQUEST
            } else {
                throw GitlabPayloadNotSupportedException()
            }
        }
    }
}
