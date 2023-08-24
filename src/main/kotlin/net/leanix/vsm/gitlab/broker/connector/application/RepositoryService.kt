package net.leanix.vsm.gitlab.broker.connector.application

import io.github.oshai.kotlinlogging.KotlinLogging
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.connector.domain.GitlabProvider
import org.springframework.stereotype.Service

@Service
class RepositoryService(private val gitlabProvider: GitlabProvider) {

    private val logger = KotlinLogging.logger {}

    fun importAllRepositories(assignment: GitLabAssignment) {
        gitlabProvider
            .getAllRepositories(assignment)
            .onSuccess {
                logger.info { "repositories imported" }
            }
            .onFailure {
                logger.error(it.cause) { "failed ${it.cause}" }
            }
    }
}
