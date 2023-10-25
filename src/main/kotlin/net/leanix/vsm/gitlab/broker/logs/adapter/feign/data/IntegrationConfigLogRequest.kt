package net.leanix.vsm.gitlab.broker.logs.adapter.feign.data

import jakarta.validation.constraints.NotNull
import net.leanix.vsm.gitlab.broker.logs.domain.ConfigFieldError
import net.leanix.vsm.gitlab.broker.logs.domain.IntegrationConfigLog
import net.leanix.vsm.gitlab.broker.shared.Constants.GITLAB_ENTERPRISE_CONNECTOR
import java.util.UUID

data class IntegrationConfigLogRequest(
    @field:NotNull(message = "Field \"runId\" cannot be empty")
    val runId: UUID?,
    @field:NotNull(message = "Field \"configurationId\" configurationId be empty")
    val configurationId: UUID?,
    val integrationName: String,
    val errors: List<ConfigFieldError>,
    val status: String = "ERROR",
) {
    companion object {
        fun fromDomain(integrationConfigLog: IntegrationConfigLog): IntegrationConfigLogRequest {
            return IntegrationConfigLogRequest(
                runId = integrationConfigLog.runId,
                configurationId = integrationConfigLog.configurationId,
                integrationName = GITLAB_ENTERPRISE_CONNECTOR,
                errors = integrationConfigLog.errors,
                status = integrationConfigLog.status.toString(),
            )
        }
    }
}
