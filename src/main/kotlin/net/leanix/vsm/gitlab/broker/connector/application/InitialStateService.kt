package net.leanix.vsm.gitlab.broker.connector.application

import io.github.oshai.kotlinlogging.KotlinLogging
import net.leanix.vsm.gitlab.broker.connector.domain.CommandEventAction
import net.leanix.vsm.gitlab.broker.connector.domain.CommandProvider
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import org.springframework.stereotype.Service

@Service
class InitialStateService(
    private val repositoryService: RepositoryService,
    private val commandProvider: CommandProvider
) : BaseConnectorService() {

    private val logger = KotlinLogging.logger {}

    fun initState(assignments: List<GitLabAssignment>) {
        logger.info { "Started get initial state" }

        assignments.forEach { assignment ->
            runCatching {
                logger.info {
                    "Received assignment for ${assignment.connectorConfiguration.orgName} " +
                        "with configuration id: ${assignment.configurationId} and with run id: ${assignment.runId}"
                }
                repositoryService.importAllRepositories(assignment)
            }.onSuccess {
                commandProvider.sendCommand(assignment, CommandEventAction.FINISHED)
            }.onFailure { e ->
                logger.error(e) { "Failed to get initial state" }
                logFailedStatus("Failed to get initial state. Error: ${e.message}", assignment.runId)
                commandProvider.sendCommand(assignment, CommandEventAction.FAILED)
            }
        }
    }
}
