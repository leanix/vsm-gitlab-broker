package net.leanix.vsm.gitlab.broker.connector.adapter.feign

import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.CommandRequest
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.DeleteServiceRequest
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.DoraRequest
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.ServiceRequest
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.UpdateRunStateRequest
import net.leanix.vsm.gitlab.broker.connector.application.DummyRequest
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabHeartbeatResponse
import net.leanix.vsm.gitlab.broker.shared.Constants.EVENT_TYPE_HEADER
import net.leanix.vsm.gitlab.broker.shared.auth.adapter.feign.config.MtmFeignClientConfiguration
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

@FeignClient(
    name = "vsmClient",
    url = "\${leanix.vsm.events-broker.base-url}",
    configuration = [MtmFeignClientConfiguration::class],
)
interface VsmClient {

    @GetMapping("/gitlab-on-prem/assignments")
    fun getAssignments(): List<GitLabAssignment>

    @PutMapping("/gitlab-on-prem/health/heartbeat")
    fun heartbeat(@RequestParam("runId") runId: String): GitLabHeartbeatResponse

    @PostMapping("/services")
    fun saveService(
        @RequestHeader(name = EVENT_TYPE_HEADER) eventType: String,
        @RequestBody serviceRequest: ServiceRequest,
    )

    @PostMapping("/v2/services")
    fun saveServiceV2(
        @RequestHeader(name = EVENT_TYPE_HEADER) eventType: String,
        @RequestBody dummyRequest: DummyRequest,
    ): String?

    @PostMapping("/services/bulk")
    fun bulkSaveServices(
        @RequestHeader(name = EVENT_TYPE_HEADER) eventType: String,
        @RequestBody serviceRequest: List<ServiceRequest>,
    )

    @PostMapping("/commands")
    fun sendCommand(
        @RequestBody commandRequest: CommandRequest,
    )

    @PutMapping("/run/status")
    fun updateRunState(
        @RequestParam("runId") runId: UUID,
        @RequestBody runState: UpdateRunStateRequest,
    )

    @PostMapping("/dora")
    fun saveDora(doraRequest: DoraRequest)

    @DeleteMapping("/services")
    fun deleteService(@RequestBody deleteServiceRequest: DeleteServiceRequest)
}
