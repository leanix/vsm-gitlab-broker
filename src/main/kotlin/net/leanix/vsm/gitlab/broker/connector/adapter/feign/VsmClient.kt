package net.leanix.vsm.gitlab.broker.connector.adapter.feign

import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.CommandRequest
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.ServiceRequest
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabHeartbeatResponse
import net.leanix.vsm.gitlab.broker.shared.Constants.EVENT_TYPE_HEADER
import net.leanix.vsm.gitlab.broker.shared.auth.adapter.feign.config.MtmFeignClientConfiguration
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "vsmClient",
    url = "\${leanix.vsm.events-broker.base-url}",
    configuration = [MtmFeignClientConfiguration::class]
)
interface VsmClient {

    @GetMapping("/gitlab-on-prem/assignments")
    fun getAssignments(): List<GitLabAssignment>

    @PutMapping("/gitlab-on-prem/heartbeat")
    fun heartbeat(@RequestParam("runId") runId: String): GitLabHeartbeatResponse

    @PostMapping("/services")
    fun saveService(
        @RequestHeader(name = EVENT_TYPE_HEADER) eventType: String,
        @RequestBody serviceRequest: ServiceRequest,
    )

    @PostMapping("/commands")
    fun sendCommand(
        @RequestBody commandRequest: CommandRequest,
    )
}
