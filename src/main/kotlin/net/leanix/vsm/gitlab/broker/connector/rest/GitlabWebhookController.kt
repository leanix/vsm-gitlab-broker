package net.leanix.vsm.gitlab.broker.connector.rest

import net.leanix.vsm.gitlab.broker.connector.domain.WebhookConsumerService
import net.leanix.vsm.gitlab.broker.webhook.adapter.feign.LEANIX_WEBHOOK_PATH
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(LEANIX_WEBHOOK_PATH)
class GitlabWebhookController(
    private val webhookService: WebhookConsumerService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun webhook(
        @RequestHeader("X-Gitlab-Instance", required = false) instance: String?,
        @RequestHeader("X-Gitlab-Webhook-UUID", required = false) webhookUUID: String?,
        @RequestHeader("X-Gitlab-Event", required = false) event: String?,
        @RequestHeader("X-Gitlab-Event-UUID", required = false) eventUUID: String?,
        @RequestHeader("X-Gitlab-Token", required = false) payloadToken: String?,
        @RequestBody payload: String
    ) {
        logger.info("instance: $instance")
        logger.info("webhookUUID: $webhookUUID")
        logger.info("event: $event")
        logger.info("eventUUID: $eventUUID")
        logger.info("payloadToken: $payloadToken")
        logger.info("payload: $payload")

        runCatching {
            webhookService.consumeWebhookEvent(payloadToken, payload)
        }.onFailure {
            logger.error("Error consuming github event: ${it.message}")
            logger.error("Error consuming github event", it)
        }
    }

    @GetMapping
    fun health() = "OK"
}