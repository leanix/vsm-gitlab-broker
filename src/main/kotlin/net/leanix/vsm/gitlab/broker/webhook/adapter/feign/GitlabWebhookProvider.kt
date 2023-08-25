package net.leanix.vsm.gitlab.broker.webhook.adapter.feign

import net.leanix.vsm.gitlab.broker.connector.application.AssignmentService
import net.leanix.vsm.gitlab.broker.webhook.domain.GitlabWebhookDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

interface WebhookProvider {
    fun getAllWebhooks(): List<GitlabWebhookDto>
    fun deleteWebhook(webhookId: Int)
    fun createWebhook(): GitlabWebhookDto
}

@Component
class GitlabWebhookProvider(
    private val webhookClient: GitlabWebhookClient,
    @Value("\${leanix.gitlab.webhook-url}") private val gitlabWebhookUrl: String,
    @Value("\${leanix.gitlab.leanix-id}") private val leanixId: String
) : WebhookProvider {

    private val logger = LoggerFactory.getLogger(AssignmentService::class.java)
    override fun getAllWebhooks(): List<GitlabWebhookDto> {
        return kotlin.runCatching {
            webhookClient.getAllWebhooks()
        }.onSuccess {
            logger.info("Webhooks fetched. size: ${it.size}")
        }.getOrThrow()
    }

    override fun deleteWebhook(webhookId: Int) {
        return kotlin.runCatching {
            webhookClient.deleteWebhook(webhookId)
        }.onSuccess {
            logger.info("Webhooks deleted for id: $webhookId")
        }.getOrThrow()
    }

    override fun createWebhook(): GitlabWebhookDto {
        return kotlin.runCatching {
            webhookClient.createWebhook(
                url = gitlabWebhookUrl,
                token = leanixId,
                receivePushEvents = true,
                receiveTagPushEvents = false,
                receiveMergeRequestEvents = true,
                receiveRepositoryUpdateEvents = true,
                enableSSLVerification = false
            )
        }.onSuccess {
            logger.info("Webhook created with id ${it.id}")
        }.getOrThrow()
    }
}
