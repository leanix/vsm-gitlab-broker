package net.leanix.vsm.gitlab.broker.webhook.adapter.feign

import net.leanix.vsm.gitlab.broker.connector.application.AssignmentService
import net.leanix.vsm.gitlab.broker.webhook.domain.GitlabWebhook
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

interface WebhookProvider {
    fun getAllWebhooks(): List<GitlabWebhook>
    fun deleteWebhook(webhookId: Int)
    fun createWebhook(): GitlabWebhook
}

@Component
class GitlabWebhookProvider(
    private val webhookClient: GitlabWebhookClient,
    @Value("\${leanix.gitlab.webhook-url}") private val gitlabWebhookUrl: String,
    @Value("\${leanix.gitlab.leanix-id}") private val leanixId: String,
    @Value("\${leanix.gitlab.enable-ssl-verification}") private val enableSSLVerification: Boolean,
) : WebhookProvider {

    private val logger = LoggerFactory.getLogger(AssignmentService::class.java)

    override fun getAllWebhooks(): List<GitlabWebhook> {
        return kotlin.runCatching {
            webhookClient.getAllWebhooks()
        }.onSuccess {
            logger.info("Webhooks fetched. size: ${it.size}")
        }.onFailure {
            logger.error("Error while fetching webhooks: ${it.message}")
        }.getOrThrow()
    }

    override fun deleteWebhook(webhookId: Int) {
        return kotlin.runCatching {
            webhookClient.deleteWebhook(webhookId)
        }.onSuccess {
            logger.info("Webhooks deleted for id: $webhookId")
        }.onFailure {
            logger.error("Error while deleting webhook with id $webhookId: ${it.message}")
        }.getOrThrow()
    }

    override fun createWebhook(): GitlabWebhook {
        return kotlin.runCatching {
            webhookClient.createWebhook(
                url = "$gitlabWebhookUrl/webhook",
                token = leanixId,
                receivePushEvents = true,
                receiveTagPushEvents = false,
                receiveMergeRequestEvents = true,
                receiveRepositoryUpdateEvents = true,
                enableSSLVerification = enableSSLVerification
            )
        }.onSuccess {
            logger.info("Webhook created with id ${it.id}")
        }.onFailure {
            logger.error("Error creating webhook: ${it.message}")
        }.getOrThrow()
    }
}
