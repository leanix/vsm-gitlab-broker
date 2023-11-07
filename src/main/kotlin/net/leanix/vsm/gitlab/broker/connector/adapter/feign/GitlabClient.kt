package net.leanix.vsm.gitlab.broker.connector.adapter.feign

import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.GitlabGroup
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.GitlabUser
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.GitlabVersionResponse
import net.leanix.vsm.gitlab.broker.shared.auth.adapter.feign.GitlabFeignClientConfiguration
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "gitlab",
    url = "\${leanix.vsm.connector.gitlab-url}/api/v4",
    configuration = [GitlabFeignClientConfiguration::class]
)
interface GitlabClient {
    @GetMapping("/user")
    fun getCurrentUser(): GitlabUser

    @GetMapping("/groups")
    fun getAllGroups(
        @RequestParam("top_level_only") topLevelOnly: Boolean = true
    ): List<GitlabGroup>

    @GetMapping("/version")
    fun getVersion(): GitlabVersionResponse
}
