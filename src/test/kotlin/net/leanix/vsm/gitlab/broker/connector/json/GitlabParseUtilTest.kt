package net.leanix.vsm.gitlab.broker.connector.json

import net.leanix.vsm.gitlab.broker.connector.domain.WebhookEventType
import net.leanix.vsm.gitlab.broker.shared.exception.GitlabPayloadNotSupportedException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GitlabParseUtilTest {

    @Test
    fun `should return webhook event type REPOSITORY when event_name=project_create in payload`() {
        val payload = this::class.java.getResource("/webhook_calls/project_created.json")!!.readText()

        val result = computeWebhookEventType(payload)

        assertEquals(WebhookEventType.REPOSITORY, result)
    }

    @Test
    fun `should return webhook event type MERGE_REQUEST when object_kind=merge_request and action=merged in payload`() {
        val payload = this::class.java.getResource("/webhook_calls/merge_request_merged.json")!!.readText()

        val result = computeWebhookEventType(payload)

        assertEquals(WebhookEventType.MERGE_REQUEST, result)
    }

    @Test
    fun `should throw GitlabPayloadNotSupportedException when object_kind=merge_request and action!=merged in payload`() {
        val payload = this::class.java.getResource("/webhook_calls/merge_request_opened.json")!!.readText()

        assertThrows<GitlabPayloadNotSupportedException> { computeWebhookEventType(payload) }
    }

    @Test
    fun `should throw GitlabPayloadNotSupportedException when payload has no supported fields`() {

        assertThrows<GitlabPayloadNotSupportedException> {
            computeWebhookEventType("{ \"dummy_key\": \"dummy value\" }")
        }
    }
}
