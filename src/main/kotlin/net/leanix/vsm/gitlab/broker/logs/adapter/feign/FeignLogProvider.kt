package net.leanix.vsm.gitlab.broker.logs.adapter.feign

import feign.FeignException
import net.leanix.vsm.gitlab.broker.logs.adapter.feign.data.AdminRequest
import net.leanix.vsm.gitlab.broker.logs.adapter.feign.data.IntegrationConfigLogRequest
import net.leanix.vsm.gitlab.broker.logs.adapter.feign.data.StatusRequest
import net.leanix.vsm.gitlab.broker.logs.domain.AdminLog
import net.leanix.vsm.gitlab.broker.logs.domain.IntegrationConfigLog
import net.leanix.vsm.gitlab.broker.logs.domain.LogProvider
import net.leanix.vsm.gitlab.broker.logs.domain.StatusLog
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

private const val FAILED_TO_SEND_STATUS_LOG = "Failed to send Status Log"
private const val FAILED_TO_SEND_ADMIN_LOG = "Failed to send Admin Log"
private const val FAILED_TO_SEND_INTEGRATION_CONFIG_LOG = "Failed to send integration config Log"

@Component
class FeignLogProvider(
    private val loggingClient: LoggingClient
) : LogProvider {

    private val logger: Logger = LoggerFactory.getLogger(FeignLogProvider::class.java)

    override fun sendAdminLog(adminLog: AdminLog) {
        try {
            loggingClient.sendAdminLog(AdminRequest.fromDomain(adminLog))
        } catch (e: FeignException) {
            val message = "$FAILED_TO_SEND_ADMIN_LOG, ${e.message}"
            logger.error(message)
        }
    }

    override fun sendStatusLog(statusLog: StatusLog) {
        try {
            loggingClient.sendStatusLog(StatusRequest.fromDomain(statusLog))
        } catch (e: FeignException) {
            val message = "$FAILED_TO_SEND_STATUS_LOG, ${e.message}"
            logger.error(message)
        }
    }

    override fun sendIntegrationConfigLog(integrationConfigLog: IntegrationConfigLog) {
        try {
            loggingClient.sendIntegrationConfigLog(IntegrationConfigLogRequest.fromDomain(integrationConfigLog))
        } catch (e: FeignException) {
            val message = "$FAILED_TO_SEND_INTEGRATION_CONFIG_LOG, ${e.message}"
            logger.error(message)
        }
    }
}
