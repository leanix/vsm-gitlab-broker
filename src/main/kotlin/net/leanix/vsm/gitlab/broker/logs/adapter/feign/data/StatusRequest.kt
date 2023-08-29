package net.leanix.vsm.gitlab.broker.logs.adapter.feign.data

import jakarta.validation.constraints.NotNull
import net.leanix.vsm.gitlab.broker.logs.domain.LogStatus
import net.leanix.vsm.gitlab.broker.logs.domain.StatusLog
import java.util.UUID

data class StatusRequest(
    @field:NotNull(message = "Field \"runId\" cannot be empty")
    val runId: UUID?,
    @field:NotNull(message = "Field \"configurationId\" configurationId be empty")
    val configurationId: UUID?,
    @field:NotNull(message = "Field \"status\" cannot be empty")
    val status: LogStatus?,
    val message: String? = null
) {
    companion object {
        fun fromDomain(status: StatusLog): StatusRequest {
            return StatusRequest(
                runId = status.runId,
                status = status.status,
                message = status.message,
                configurationId = status.configurationId
            )
        }
    }
}
