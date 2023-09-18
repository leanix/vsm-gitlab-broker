package net.leanix.vsm.gitlab.broker.connector.application

import io.github.oshai.kotlinlogging.KotlinLogging
import net.leanix.vsm.gitlab.broker.connector.domain.EventType
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.connector.domain.GitlabProvider
import net.leanix.vsm.gitlab.broker.connector.domain.Repository
import net.leanix.vsm.gitlab.broker.connector.domain.RepositoryProvider
import net.leanix.vsm.gitlab.broker.logs.domain.LogStatus
import net.leanix.vsm.gitlab.broker.shared.exception.NoRepositoriesFound
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class RepositoryService(
    private val gitlabProvider: GitlabProvider,
    private val repositoryProvider: RepositoryProvider,
    @Value("\${leanix.vsm.events-broker.batch-size:10}") private val batchSize: Int
) : BaseConnectorService() {

    private val logger = KotlinLogging.logger {}

    fun importAllRepositories(assignment: GitLabAssignment): List<Repository> {
        logInfoStatus(
            assignment = assignment,
            status = LogStatus.IN_PROGRESS,
        )
        return gitlabProvider
            .getAllRepositories(assignment)
            .onSuccess {
                logInfoMessages("vsm.repos.total", arrayOf(it.size), assignment)
                logger.info { "Pulled ${it.size} repos. (run: ${assignment.runId})" }
                saveAll(it, assignment, EventType.STATE)
                logInfoStatus(
                    assignment = assignment,
                    status = LogStatus.SUCCESSFUL,
                )
            }
            .onFailure {
                handleExceptions(it, assignment)
                throw it
            }.getOrThrow()
    }

    fun saveAll(repositories: List<Repository>, assignment: GitLabAssignment, eventType: EventType) {
        repositories
            .takeIf { it.isNotEmpty() }
            ?.chunked(batchSize)
            ?.forEach { batch ->
                runCatching {
                    repositoryProvider.saveAll(batch, assignment, eventType)
                }.onFailure {
                    logger.error {
                        "Error pushing ${batch.size} repos " +
                            "(run: ${assignment.runId}: ${it.message})"
                    }
                }.onSuccess {
                    logger.info {
                        "Successfully pushed ${batch.size} repos " +
                            "(run: ${assignment.runId})"
                    }
                }
            }
    }

    private fun handleExceptions(exception: Throwable, assignment: GitLabAssignment) {
        when (exception) {
            is NoRepositoriesFound -> {
                logFailedMessages("vsm.repos.not_found", arrayOf(assignment.connectorConfiguration.orgName), assignment)
            }
        }
    }
}
