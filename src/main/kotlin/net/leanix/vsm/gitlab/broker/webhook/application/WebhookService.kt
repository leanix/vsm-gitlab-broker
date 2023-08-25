package net.leanix.vsm.gitlab.broker.webhook.application

import net.leanix.vsm.gitlab.broker.webhook.adapter.feign.WebhookProvider
import net.leanix.vsm.gitlab.broker.webhook.domain.GitlabWebhookDto
import org.springframework.stereotype.Service

interface WebhookService {
    fun registerWebhook(): GitlabWebhookDto
}

@Service
class GitlabWebhookServiceImpl(
    private val webhookProvider: WebhookProvider
) : WebhookService {

    override fun registerWebhook(): GitlabWebhookDto {
        val webhook = webhookProvider.createWebhook()

        webhookProvider.getAllWebhooks()
            .filter { it.id != webhook.id }
            .forEach { webhookProvider.deleteWebhook(it.id) }

        return webhook
    }
}
