package net.leanix.vsm.gitlab.broker.connector.application

import io.github.oshai.kotlinlogging.KotlinLogging
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.logs.application.LoggingService
import net.leanix.vsm.gitlab.broker.logs.domain.AdminLog
import net.leanix.vsm.gitlab.broker.logs.domain.ConfigFieldError
import net.leanix.vsm.gitlab.broker.logs.domain.IntegrationConfigLog
import net.leanix.vsm.gitlab.broker.logs.domain.LogLevel
import net.leanix.vsm.gitlab.broker.logs.domain.LogStatus
import net.leanix.vsm.gitlab.broker.logs.domain.StatusLog
import net.leanix.vsm.gitlab.broker.logs.domain.TestResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import java.util.Locale

open class BaseConnectorService {

    @Autowired
    lateinit var loggingService: LoggingService

    @Autowired
    lateinit var messageSource: MessageSource

    private val logger = KotlinLogging.logger {}

    fun logFailedStatus(message: String? = "empty message", assignment: GitLabAssignment) {
        logger.error { message }
        loggingService.sendStatusLog(
            StatusLog(assignment.runId, assignment.configurationId, LogStatus.FAILED, message),
        )
    }

    fun logInfoStatus(message: String? = "", status: LogStatus, assignment: GitLabAssignment) {
        logger.info { message }
        loggingService.sendStatusLog(
            StatusLog(assignment.runId, assignment.configurationId, status, message),
        )
    }

    fun logInfoMessages(code: String, arguments: Array<Any>, assignment: GitLabAssignment) {
        val message = messageSource.getMessage(
            code,
            arguments,
            Locale.ENGLISH,
        )
        loggingService.sendAdminLog(
            AdminLog(
                runId = assignment.runId,
                configurationId = assignment.configurationId,
                subject = LogLevel.INFO.toString(),
                level = LogLevel.INFO,
                message = message,
            ),
        )
    }

    fun logFailedMessages(code: String, arguments: Array<Any>, assignment: GitLabAssignment) {
        val message = messageSource.getMessage(
            code,
            arguments,
            Locale.ENGLISH,
        )
        loggingService.sendAdminLog(
            AdminLog(
                runId = assignment.runId,
                configurationId = assignment.configurationId,
                subject = LogLevel.ERROR.toString(),
                level = LogLevel.ERROR,
                message = message,
            ),
        )
    }

    fun logIntegrationConfigError(
        field: String,
        error: String,
        assignment: GitLabAssignment,
    ) {
        loggingService.sendIntegrationConfigLog(
            IntegrationConfigLog(
                runId = assignment.runId,
                configurationId = assignment.configurationId,
                errors = listOf(
                    ConfigFieldError(
                        field,
                        error,
                    ),
                ),
                status = TestResult.FAILED,
            ),
        )
    }

    fun logIntegrationConfigSuccess(
        assignment: GitLabAssignment,
    ) {
        loggingService.sendIntegrationConfigLog(
            IntegrationConfigLog(
                runId = assignment.runId,
                configurationId = assignment.configurationId,
                status = TestResult.SUCCESSFUL,
            ),
        )
    }
}
