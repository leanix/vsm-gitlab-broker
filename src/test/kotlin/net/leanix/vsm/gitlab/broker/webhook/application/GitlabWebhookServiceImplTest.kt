package net.leanix.vsm.gitlab.broker.webhook.application

import net.leanix.vsm.gitlab.broker.webhook.adapter.feign.LEANIX_WEBHOOK_PATH
import net.leanix.vsm.gitlab.broker.webhook.domain.GitlabWebhook
import net.leanix.vsm.gitlab.broker.webhook.domain.WebhookProvider
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.util.Date

class GitlabWebhookServiceImplTest {

    private val webhookProvider = mock(WebhookProvider::class.java)
    private val subject = GitlabWebhookServiceImpl(webhookProvider)

    @Test
    fun `given a new one webhook is created successfully when registerWebhook then delete all other webhooks`() {
        `when`(webhookProvider.createWebhook()).thenReturn(dummyGitlabWebhookDto(id = 1))
        `when`(webhookProvider.getAllWebhooks()).thenReturn(
            listOf(
                dummyGitlabWebhookDto(id = 1),
                dummyGitlabWebhookDto(id = 2)
            )
        )

        subject.registerWebhook()

        verify(webhookProvider, times(0)).deleteWebhook(eq(1))
        verify(webhookProvider).deleteWebhook(eq(2))
    }
}

fun dummyGitlabWebhookDto(id: Int) = GitlabWebhook(
    id = id,
    url = "https://gitlab.example.com$LEANIX_WEBHOOK_PATH",
    createdAt = Date(),
    pushEvents = true,
    tagPushEvents = false,
    mergeRequestsEvents = true,
    repositoryUpdateEvents = true,
    enableSSLVerification = false
)
