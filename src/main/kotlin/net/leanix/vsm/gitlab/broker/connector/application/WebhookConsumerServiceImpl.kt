package net.leanix.vsm.gitlab.broker.connector.application

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.leanix.vsm.gitlab.broker.connector.domain.MergeRequest
import net.leanix.vsm.gitlab.broker.connector.domain.ProjectCreated
import net.leanix.vsm.gitlab.broker.connector.domain.WebhookConsumerService
import net.leanix.vsm.gitlab.broker.connector.domain.WebhookEventType
import net.leanix.vsm.gitlab.broker.connector.json.computeWebhookEventType
import net.leanix.vsm.gitlab.broker.shared.exception.GitlabTokenException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class WebhookConsumerServiceImpl(
    @Value("\${leanix.gitlab.leanix-id}") private val leanixId: String
) : WebhookConsumerService {

    val mapper: ObjectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    override fun consumeWebhookEvent(payloadToken: String?, payload: String) {

        // validate token
        if (payloadToken?.equals(leanixId) == true) {
            throw GitlabTokenException(payloadToken)
        }

        // figure out event type
        when (computeWebhookEventType(payload)) {
            WebhookEventType.REPOSITORY -> processProjectCreated(payload)
            WebhookEventType.MERGE_REQUEST -> println()
        }
    }

    private fun processProjectCreated(payload: String) {
        val project = mapper.readValue<ProjectCreated>(payload)
        println(project)
    }

    private fun processMergeRequest(payload: String) {
        val mergeRequest = mapper.readValue<MergeRequest>(payload)
        println(mergeRequest)
    }
}