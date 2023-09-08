package net.leanix.vsm.gitlab.broker.webhook.domain

interface WebhookProvider {

    fun getAllWebhooks(): List<GitlabWebhook>
    fun deleteWebhook(webhookId: Int)
    fun createWebhook(): GitlabWebhook?
}
