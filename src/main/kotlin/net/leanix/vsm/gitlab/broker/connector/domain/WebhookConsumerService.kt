package net.leanix.vsm.gitlab.broker.connector.domain

interface WebhookConsumerService {
    fun consumeWebhookEvent(payloadToken: String?, payload: String)
}