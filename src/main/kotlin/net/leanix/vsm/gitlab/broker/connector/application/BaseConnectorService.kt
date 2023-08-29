package net.leanix.vsm.gitlab.broker.connector.application

import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.logs.application.LoggingService
import net.leanix.vsm.gitlab.broker.logs.domain.AdminLog
import net.leanix.vsm.gitlab.broker.logs.domain.LogLevel
import net.leanix.vsm.gitlab.broker.logs.domain.LogStatus
import net.leanix.vsm.gitlab.broker.logs.domain.StatusLog
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import java.util.*

open class BaseConnectorService {

    @Autowired
    lateinit var loggingService: LoggingService

    @Autowired
    lateinit var messageSource: MessageSource

    private val logger = LoggerFactory.getLogger(BaseConnectorService::class.java)

    fun logFailedStatus(message: String? = "empty message", runId: UUID) {
        logger.error(message)
        loggingService.sendStatusLog(
            StatusLog(runId, LogStatus.FAILED, message)
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
