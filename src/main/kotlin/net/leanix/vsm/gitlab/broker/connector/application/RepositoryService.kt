package net.leanix.vsm.gitlab.broker.connector.application

import io.github.oshai.kotlinlogging.KotlinLogging
import net.leanix.vsm.gitlab.broker.connector.domain.EventType
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.connector.domain.GitlabProvider
import net.leanix.vsm.gitlab.broker.connector.domain.Repository
import net.leanix.vsm.gitlab.broker.connector.domain.RepositoryProvider
import net.leanix.vsm.gitlab.broker.shared.exception.VsmException
import org.springframework.stereotype.Service

@Service
class RepositoryService(
    private val gitlabProvider: GitlabProvider,
    private val repositoryProvider: RepositoryProvider
) {

    private val logger = KotlinLogging.logger {}

    fun importAllRepositories(assignment: GitLabAssignment) {
        gitlabProvider
            .getAllRepositories(assignment)
            .onSuccess {
                it.forEach { repository ->
                    save(repository, assignment, EventType.STATE)
                }
                logger.info { "repositories imported" }
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
            is VsmException.NoRepositoriesFound -> {
                logger.error(exception.cause) { "failed ${exception.cause}" }
            }
        }
    }
}
