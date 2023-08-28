package net.leanix.vsm.gitlab.broker.connector.adapter.feign

import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.GitlabUser
import net.leanix.vsm.gitlab.broker.shared.exception.VsmException
import org.slf4j.LoggerFactory

class GitlabClientProvider(
    private val gitlabClient: GitlabClient
) {

    private val logger = LoggerFactory.getLogger(GitlabClientProvider::class.java)

    fun getCurrentUser(): GitlabUser {
        val user = kotlin.runCatching { gitlabClient.getCurrentUser() }
            .onFailure {
                logger.error("Invalid token, could not get current user")
                throw VsmException.InvalidToken()
            }.getOrThrow()
        return runCatching { gitlabClient.getUserById(user.id) }
            .onFailure { logger.error("Could not get user with id ${user.id}") }
            .getOrThrow()
    }

    fun getOrg(orgName: String) {
        runCatching { gitlabClient.getProjectByNameWithNamespace(orgName) }
            .onFailure { logger.error("Could not get org info for $orgName") }
            .getOrThrow()
    }
}
