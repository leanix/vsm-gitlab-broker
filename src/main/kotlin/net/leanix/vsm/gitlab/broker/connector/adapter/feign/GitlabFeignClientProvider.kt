package net.leanix.vsm.gitlab.broker.connector.adapter.feign

import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.GitlabGroup
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.GitlabUser
import net.leanix.vsm.gitlab.broker.shared.exception.InvalidToken
import net.leanix.vsm.gitlab.broker.shared.exception.OrgNameValidationFailed
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GitlabFeignClientProvider(
    private val gitlabClient: GitlabClient
) : GitlabClientProvider {

    private val logger = LoggerFactory.getLogger(GitlabFeignClientProvider::class.java)

    override fun getCurrentUser(): GitlabUser {
        return runCatching { gitlabClient.getCurrentUser() }.onFailure {
            logger.error("Invalid token, could not get current user")
            throw InvalidToken()
        }.getOrThrow()
    }

    override fun getGroupByFullPath(fullPath: String): GitlabGroup? {
        return runCatching {
            gitlabClient.getAllGroups().first { it.fullPath == fullPath }
        }.onFailure { throw OrgNameValidationFailed() }.getOrThrow()
    }
}
