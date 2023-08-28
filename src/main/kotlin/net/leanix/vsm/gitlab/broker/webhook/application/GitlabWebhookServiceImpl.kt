package net.leanix.vsm.gitlab.broker.webhook.application

import net.leanix.vsm.gitlab.broker.webhook.adapter.feign.leanixWebhookPath
import net.leanix.vsm.gitlab.broker.webhook.domain.GitlabWebhook
import net.leanix.vsm.gitlab.broker.webhook.domain.WebhookProvider
import net.leanix.vsm.gitlab.broker.webhook.domain.WebhookService
import org.springframework.stereotype.Service

@Service
class GitlabWebhookServiceImpl(
    private val webhookProvider: WebhookProvider
) : WebhookService {

    override fun registerWebhook(): GitlabWebhook {
        val webhook = webhookProvider.createWebhook()

        webhookProvider.getAllWebhooks()
            .filter { it.url.contains(leanixWebhookPath) && it.id != webhook.id }
            .forEach { webhookProvider.deleteWebhook(it.id) }

        return webhook
    }
}
