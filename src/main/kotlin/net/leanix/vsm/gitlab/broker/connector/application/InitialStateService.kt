package net.leanix.vsm.gitlab.broker.connector.application

import io.github.oshai.kotlinlogging.KotlinLogging
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.GitlabClientProvider
import net.leanix.vsm.gitlab.broker.connector.domain.CommandEventAction
import net.leanix.vsm.gitlab.broker.connector.domain.CommandProvider
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.shared.exception.GitlabVersionNotSupportedException
import org.springframework.stereotype.Service
import kotlin.system.exitProcess

@Service
class InitialStateService(
    private val repositoryService: RepositoryService,
    private val commandProvider: CommandProvider,
    private val doraService: DoraService,
    private val validationService: ValidationService,
    private val gitlabClientProvider: GitlabClientProvider
) : BaseConnectorService() {

    private val logger = KotlinLogging.logger {}

    fun initState(assignments: List<GitLabAssignment>) {
        logger.info { "Started get initial state" }

        validateGitlabVersion(assignments)

        val successAssignments = mutableListOf<GitLabAssignment>()
        val failedAssignments = mutableListOf<GitLabAssignment>()

        assignments.forEach { assignment ->
            runCatching {
                logger.info {
                    "Received assignment for ${assignment.connectorConfiguration.orgName} " +
                            "with configuration id: ${assignment.configurationId} and with run id: ${assignment.runId}"
                }
                validationService.validateConfiguration(assignment)
                repositoryService
                    .importAllRepositories(assignment)
                    .forEach { repository ->
                        doraService.generateDoraEvents(repository, assignment)
                    }
            }.onSuccess {
                successAssignments.add(assignment)
                logger.info {
                    "Successfully processed assignment => " +
                            "configurationId: ${assignment.configurationId}, runId: ${assignment.runId}"
                }
            }.onFailure { e ->
                failedAssignments.add(assignment)
                logger.error(e) {
                    "Failed to process assignment => " +
                            "configurationId: ${assignment.configurationId}, runId: ${assignment.runId}: ${e.message}"
                }
                logFailedStatus("Failed to get initial state. Error: ${e.message}", assignment)
            }
        }

        successAssignments.forEach { commandProvider.sendCommand(it, CommandEventAction.FINISHED) }
        failedAssignments.forEach { commandProvider.sendCommand(it, CommandEventAction.FAILED) }
    }

    private fun validateGitlabVersion(assignments: List<GitLabAssignment>) {
        runCatching {
            val gitlabVersion = gitlabClientProvider.getVersion()
            val gitlabVersionInt = gitlabVersion.split('.')[0].toInt()
            if (gitlabVersionInt < 15) {
                throw GitlabVersionNotSupportedException(gitlabVersion)
            }
        }.onFailure { exception ->
            if (exception is GitlabVersionNotSupportedException) {
                runCatching {
                    assignments.forEach { assignment ->
                        runCatching {
                            logFailedStatus(exception.message, assignment)
                        }.onFailure {
                            logger.warn { "Failed to log failed status for run ${assignment.runId}" }
                        }
                    }
                }
                exit()
            }
        }
    }

    fun exit() {
        exitProcess(1)
    }
}
