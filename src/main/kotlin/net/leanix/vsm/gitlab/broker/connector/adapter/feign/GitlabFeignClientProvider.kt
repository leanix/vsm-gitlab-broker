package net.leanix.vsm.gitlab.broker.connector.adapter.feign

import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.GitlabUser
import net.leanix.vsm.gitlab.broker.shared.exception.InvalidToken
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

    override fun getGroupByFullPath(
        fullPath: String
    ) =
        gitlabClient
            .getAllGroups()
            .firstOrNull { it.fullPath.equals(fullPath, true) }
}
