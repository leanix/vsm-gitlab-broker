package net.leanix.vsm.gitlab.broker.webhook.adapter.feign

import net.leanix.vsm.gitlab.broker.shared.auth.adapter.feign.GitlabFeignClientConfiguration
import net.leanix.vsm.gitlab.broker.webhook.domain.GitlabWebhook
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "gitlabWebhookClient",
    url = "\${leanix.vsm.connector.gitlab-url}/api/v4/hooks",
    configuration = [GitlabFeignClientConfiguration::class]
)
interface GitlabWebhookClient {

    @GetMapping
    fun getAllWebhooks(): List<GitlabWebhook>

    @DeleteMapping("/{webhookId}")
    fun deleteWebhook(@PathVariable("webhookId") webhookId: Int)

    @Suppress("LongParameterList")
    @PostMapping("/")
    fun createWebhook(
        @RequestParam("url") url: String,
        @RequestParam("token") token: String,
        @RequestParam("push_events") receivePushEvents: Boolean,
        @RequestParam("tag_push_events") receiveTagPushEvents: Boolean,
        @RequestParam("merge_requests_events") receiveMergeRequestEvents: Boolean,
        @RequestParam("repository_update_events") receiveRepositoryUpdateEvents: Boolean,
        @RequestParam("enable_ssl_verification") enableSSLVerification: Boolean,
    ): GitlabWebhook
}
