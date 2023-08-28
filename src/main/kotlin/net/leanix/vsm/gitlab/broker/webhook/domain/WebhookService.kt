package net.leanix.vsm.gitlab.broker.webhook.domain

interface WebhookService {
    fun registerWebhook(): GitlabWebhook
}
