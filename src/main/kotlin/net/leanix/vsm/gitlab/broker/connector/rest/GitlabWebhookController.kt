package net.leanix.vsm.gitlab.broker.connector.rest

import net.leanix.vsm.gitlab.broker.connector.application.WebhookConsumerService
import net.leanix.vsm.gitlab.broker.webhook.adapter.feign.LEANIX_WEBHOOK_PATH
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
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
        @RequestHeader("X-Gitlab-Token", required = false) payloadToken: String?,
        @RequestBody payload: String
    ) {
        runCatching {
            webhookService.consumeWebhookEvent(payloadToken, payload)
        }.onFailure {
            logger.error("Error consuming gitlab event: ${it.message}")
        }
    }
}
