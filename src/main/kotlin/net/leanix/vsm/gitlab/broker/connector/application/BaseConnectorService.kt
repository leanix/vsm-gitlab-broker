package net.leanix.vsm.gitlab.broker.connector.application

import io.github.oshai.kotlinlogging.KotlinLogging
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.logs.application.LoggingService
import net.leanix.vsm.gitlab.broker.logs.domain.AdminLog
import net.leanix.vsm.gitlab.broker.logs.domain.LogLevel
import net.leanix.vsm.gitlab.broker.logs.domain.LogStatus
import net.leanix.vsm.gitlab.broker.logs.domain.StatusLog
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import java.util.Locale
import java.util.UUID

open class BaseConnectorService {

    @Autowired
    private lateinit var loggingService: LoggingService

    @Autowired
    private lateinit var messageSource: MessageSource

    private val logger = KotlinLogging.logger {}

    fun logFailedStatus(message: String? = "empty message", runId: UUID) {
        logger.error { message }
        loggingService.sendStatusLog(
            StatusLog(runId, LogStatus.FAILED, message)
        )
    }

    fun logInfoStatus(message: String? = "", runId: UUID, status: LogStatus) {
        logger.info { message }
        loggingService.sendStatusLog(
            StatusLog(runId, status, message)
        )
    }

    fun logInfoMessages(code: String, arguments: Array<Any>, assignment: GitLabAssignment) {
        val message = messageSource.getMessage(
            code,
            arguments,
            Locale.ENGLISH
        )
        loggingService.sendAdminLog(
            AdminLog(
                runId = assignment.runId,
                configurationId = assignment.configurationId,
                subject = LogLevel.INFO.toString(),
                level = LogLevel.INFO,
                message = message
            )
        )
    }

    fun logFailedMessages(code: String, arguments: Array<Any>, assignment: GitLabAssignment) {
        val message = messageSource.getMessage(
            code,
            arguments,
            Locale.ENGLISH
        )
        loggingService.sendAdminLog(
            AdminLog(
                runId = assignment.runId,
                configurationId = assignment.configurationId,
                subject = LogLevel.ERROR.toString(),
                level = LogLevel.ERROR,
                message = message
            )
        )
    }
}
