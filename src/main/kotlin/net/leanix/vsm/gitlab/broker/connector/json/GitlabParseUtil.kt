package net.leanix.vsm.gitlab.broker.connector.json

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import net.leanix.vsm.gitlab.broker.connector.domain.WebhookEventType
import net.leanix.vsm.gitlab.broker.shared.exception.GitlabPayloadNotSupportedException

val mapper: ObjectMapper = jacksonObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

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