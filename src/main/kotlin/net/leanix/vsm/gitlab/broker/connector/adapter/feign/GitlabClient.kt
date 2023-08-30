package net.leanix.vsm.gitlab.broker.connector.adapter.feign

import jakarta.websocket.server.PathParam
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.GitlabUser
import net.leanix.vsm.gitlab.broker.shared.auth.adapter.feign.GitlabFeignClientConfiguration
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping

@FeignClient(
    name = "gitlab",
    url = "\${leanix.gitlab.base-url}",
    configuration = [GitlabFeignClientConfiguration::class]
)
interface GitlabClient {
    @GetMapping("/user")
    fun getCurrentUser(): GitlabUser

    @GetMapping("/users/{userId}")
    fun getUserById(@PathParam("userId") userId: Int): GitlabUser

    @GetMapping("/groups/{groupPath}")
    fun getGroupByPath(@PathParam("groupPath") groupPath: String)
}
