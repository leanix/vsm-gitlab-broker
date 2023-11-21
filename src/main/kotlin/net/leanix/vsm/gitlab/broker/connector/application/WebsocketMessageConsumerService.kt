package net.leanix.vsm.gitlab.broker.connector.application

import io.github.oshai.kotlinlogging.KotlinLogging
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.VsmClient
import net.leanix.vsm.gitlab.broker.connector.adapter.graphql.BasicGraphQLClient
import net.leanix.vsm.gitlab.broker.connector.domain.EventType
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.logs.domain.LogStatus
import net.leanix.vsm.gitlab.broker.shared.Constants
import org.springframework.stereotype.Component
import java.util.UUID

data class WebSocketMessageData(
    val type: String,
    val successMessage: String,
    val failureMessage: String,
    val query: String,
    val variables: Map<String, Any?>,
    val statusLoggingEnabled: Boolean,
    val adminLoggingEnabled: Boolean
)

@Component
class WebsocketMessageConsumerService(
    private val basicGraphQLClient: BasicGraphQLClient,
    private val vsmClient: VsmClient
) : BaseConnectorService() {

    private val logger = KotlinLogging.logger {}

    fun consume(
        assignment: GitLabAssignment
    ) {
        if (assignment.webSocketMessageData == null) return

        logger.info { "Processing message of type ${assignment.webSocketMessageData.type}" }
        // improvement may want to add some validations here before executing query

        logInfoStatusIfNeeded(assignment.webSocketMessageData.statusLoggingEnabled, assignment)
        val queryResult = executeQuery(assignment.webSocketMessageData.query, assignment.webSocketMessageData.variables)

        queryResult
            .getOrNull()
            ?.let {
                if (it.second) {
                    it.first as List<*>
                } else {
                    listOf(it.first)
                }
            }
            ?.filterNotNull()
            ?.forEach {
                vsmClient.saveServiceV2(
                    eventType = EventType.STATE.type,
                    dummyRequest = DummyRequest(
                        type = assignment.webSocketMessageData.type,
                        orgName = assignment.connectorConfiguration.orgName,
                        runId = assignment.runId,
                        configurationId = assignment.configurationId,
                        data = it,
                        source = Constants.GITLAB_ENTERPRISE
                    )
                ).also {
                    logger.info { "response received: $it" }
                }
            }

        performResultLogging(
            queryResult,
            assignment.webSocketMessageData.statusLoggingEnabled,
            assignment.webSocketMessageData.adminLoggingEnabled,
            assignment.webSocketMessageData.successMessage,
            assignment.webSocketMessageData.failureMessage,
            assignment
        )
    }

    private fun logInfoStatusIfNeeded(statusLoggingEnabled: Boolean, assignment: GitLabAssignment) {
        if (statusLoggingEnabled) {
            logInfoStatus(
                assignment = assignment,
                status = LogStatus.IN_PROGRESS,
            )
        }
    }

    fun executeQuery(
        query: String,
        variables: Map<String, Any?>
    ) =
        runCatching {
            basicGraphQLClient.query(query, variables)
        }

    private fun performResultLogging(
        result: Result<Any?>,
        statusLoggingEnabled: Boolean,
        adminLoggingEnabled: Boolean,
        successMessage: String,
        failureMessage: String,
        assignment: GitLabAssignment
    ) {
        if (result.isFailure) {
            if (statusLoggingEnabled) {
                logInfoStatus(
                    assignment = assignment,
                    status = LogStatus.FAILED,
                    message = failureMessage
                )
            }
        } else {
            if (adminLoggingEnabled) {
                logInfoMessages(arguments = arrayOf(), assignment = assignment, message = successMessage)
            }

            if (statusLoggingEnabled) {
                logInfoStatus(assignment = assignment, status = LogStatus.SUCCESSFUL)
            }
        }
    }
}

data class DummyRequest(
    val type: String,
    val orgName: String,
    val runId: UUID,
    val configurationId: UUID,
    val data: Any,
    val source: String
)