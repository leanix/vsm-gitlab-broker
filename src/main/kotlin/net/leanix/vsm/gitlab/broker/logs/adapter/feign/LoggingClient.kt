package net.leanix.vsm.gitlab.broker.logs.adapter.feign

import net.leanix.vsm.gitlab.broker.logs.adapter.feign.data.AdminRequest
import net.leanix.vsm.gitlab.broker.logs.adapter.feign.data.StatusRequest
import net.leanix.vsm.gitlab.broker.shared.auth.adapter.feign.config.MtmFeignClientConfiguration
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "loggingClient",
    url = "\${leanix.vsm.events-broker.base-url}",
    configuration = [MtmFeignClientConfiguration::class]
)
interface LoggingClient {
    @PostMapping(value = ["/logs/status"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun sendStatusLog(@RequestBody request: StatusRequest)

    @PostMapping(value = ["/logs/admin"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun sendAdminLog(@RequestBody request: AdminRequest)
}
