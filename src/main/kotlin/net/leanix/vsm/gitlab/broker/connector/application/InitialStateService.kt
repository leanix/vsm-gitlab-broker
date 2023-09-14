package net.leanix.vsm.gitlab.broker.connector.application

import io.github.oshai.kotlinlogging.KotlinLogging
import net.leanix.vsm.gitlab.broker.connector.domain.CommandEventAction
import net.leanix.vsm.gitlab.broker.connector.domain.CommandProvider
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import org.springframework.stereotype.Service

@Service
class InitialStateService(
    private val repositoryService: RepositoryService,
    private val commandProvider: CommandProvider,
    private val doraService: DoraService
) : BaseConnectorService() {

    private val logger = KotlinLogging.logger {}

    fun initState(assignments: List<GitLabAssignment>) {
        logger.info { "Started get initial state" }

        val successAssignments = mutableListOf<GitLabAssignment>()
        val failedAssignments = mutableListOf<GitLabAssignment>()

        assignments.forEach { assignment ->
            runCatching {
                logger.info {
                    "Received assignment for ${assignment.connectorConfiguration.orgName} " +
                        "with configuration id: ${assignment.configurationId} and with run id: ${assignment.runId}"
                }
                repositoryService
                    .importAllRepositories(assignment)
                    .forEach { repository ->
                        doraService.generateDoraEvents(repository, assignment)
                    }
            }.onSuccess {
                successAssignments.add(assignment)
                logger.info { "Successfully processed assignment => configurationId: ${assignment.configurationId}, runId: ${assignment.runId}" }
                commandProvider.sendCommand(assignment, CommandEventAction.FINISHED)
                logger.info { "command sent with action: CommandEventAction.FINISHED for runId: ${assignment.runId}" }
            }.onFailure { e ->
                failedAssignments.add(assignment)
                logger.error(e) { "Failed to process assignment => configurationId: ${assignment.configurationId}, runId: ${assignment.runId}: ${e.message}" }
                logFailedStatus("Failed to get initial state. Error: ${e.message}", assignment)
            }
        }

        successAssignments.forEach { commandProvider.sendCommand(it, CommandEventAction.FINISHED) }
        failedAssignments.forEach { commandProvider.sendCommand(it, CommandEventAction.FAILED) }
    }
}
