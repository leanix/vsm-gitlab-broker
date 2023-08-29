package net.leanix.vsm.gitlab.broker.connector.application

import io.github.oshai.kotlinlogging.KotlinLogging
import net.leanix.vsm.gitlab.broker.connector.domain.EventType
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.connector.domain.GitlabProvider
import net.leanix.vsm.gitlab.broker.connector.domain.Repository
import net.leanix.vsm.gitlab.broker.connector.domain.RepositoryProvider
import net.leanix.vsm.gitlab.broker.logs.domain.LogStatus
import net.leanix.vsm.gitlab.broker.shared.exception.NoRepositoriesFound
import org.springframework.stereotype.Service

@Service
class RepositoryService(
    private val gitlabProvider: GitlabProvider,
    private val repositoryProvider: RepositoryProvider
) : BaseConnectorService() {

    private val logger = KotlinLogging.logger {}

    fun importAllRepositories(assignment: GitLabAssignment) {
        logInfoStatus(runId = assignment.runId, status = LogStatus.IN_PROGRESS)
        gitlabProvider
            .getAllRepositories(assignment)
            .onSuccess {
                logInfoMessages("vsm.repos.total", arrayOf(it.size), assignment)
                it.forEach { repository ->
                    save(repository, assignment, EventType.STATE)
                }
                logInfoStatus(runId = assignment.runId, status = LogStatus.SUCCESSFUL)
            }
            .onFailure {
                handleExceptions(it, assignment)
                throw it
            }
    }

    fun save(repository: Repository, assignment: GitLabAssignment, eventType: EventType) {
        kotlin.runCatching {
            repositoryProvider.save(repository, assignment, eventType)
        }.onFailure {
            logger.error(it) { "Failed save service" }
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
