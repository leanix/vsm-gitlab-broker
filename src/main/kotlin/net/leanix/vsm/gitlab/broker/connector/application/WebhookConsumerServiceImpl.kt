package net.leanix.vsm.gitlab.broker.connector.application

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.leanix.vsm.gitlab.broker.connector.domain.EventType
import net.leanix.vsm.gitlab.broker.connector.domain.MergeRequest
import net.leanix.vsm.gitlab.broker.connector.domain.ProjectCreated
import net.leanix.vsm.gitlab.broker.connector.domain.RepositoryProvider
import net.leanix.vsm.gitlab.broker.connector.domain.WebhookConsumerService
import net.leanix.vsm.gitlab.broker.connector.domain.WebhookEventType
import net.leanix.vsm.gitlab.broker.connector.domain.getNamespace
import net.leanix.vsm.gitlab.broker.connector.domain.toRepository
import net.leanix.vsm.gitlab.broker.shared.cache.AssignmentsCache
import net.leanix.vsm.gitlab.broker.shared.exception.GitlabPayloadNotSupportedException
import net.leanix.vsm.gitlab.broker.shared.exception.GitlabTokenException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

val mapper: ObjectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

@Service
class WebhookConsumerServiceImpl(
    @Value("\${leanix.gitlab.leanix-id}") private val leanixId: String,
    @Value("\${leanix.gitlab.base-url}") private val gitlabUrl: String,
    private val repositoryProvider: RepositoryProvider
) : WebhookConsumerService {

    override fun consumeWebhookEvent(payloadToken: String?, payload: String) {

        if (payloadToken == null || payloadToken != leanixId) {
            throw GitlabTokenException(payloadToken)
        }

        when (computeWebhookEventType(payload)) {
            WebhookEventType.REPOSITORY -> processProjectCreated(payload)
            WebhookEventType.MERGE_REQUEST -> processMergeRequest(payload)
        }
    }

    private fun processProjectCreated(payload: String) {
        val project = mapper.readValue<ProjectCreated>(payload)

        AssignmentsCache.get(project.getNamespace())
            ?.also { repositoryProvider.save(project.toRepository(gitlabUrl), it, EventType.CHANGE) }
    }

    private fun processMergeRequest(payload: String) {
        val mergeRequest = mapper.readValue<MergeRequest>(payload)
        println(mergeRequest)
    }

    companion object {

        fun computeWebhookEventType(payload: String): WebhookEventType {
            val node = mapper.readTree(payload)

            return if (node.at("/event_name").asText() == "project_create") WebhookEventType.REPOSITORY
            else if (
                node.at("/event_type").asText() == "merge_request"
                && node.at("/object_attributes/action").asText() == "merge"
                && node.path("/project/default_branch").asText()
                    == node.path("/object_attributes/target_branch").asText()
            ) WebhookEventType.MERGE_REQUEST
            else throw GitlabPayloadNotSupportedException()
        }
    }
}