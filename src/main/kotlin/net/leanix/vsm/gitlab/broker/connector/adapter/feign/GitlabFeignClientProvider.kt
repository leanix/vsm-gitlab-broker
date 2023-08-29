package net.leanix.vsm.gitlab.broker.connector.adapter.feign

import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.GitlabUser
import net.leanix.vsm.gitlab.broker.shared.exception.InvalidToken
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class GitlabFeignClientProvider(
    private val gitlabClient: GitlabClient
) : GitlabClientProvider {

    private val logger = LoggerFactory.getLogger(GitlabFeignClientProvider::class.java)

    override fun getCurrentUser(): GitlabUser {
        val user = kotlin.runCatching { gitlabClient.getCurrentUser() }
            .onFailure {
                logger.error("Invalid token, could not get current user")
                throw InvalidToken()
            }.getOrThrow()
        return runCatching { gitlabClient.getUserById(user.id) }
            .onFailure { logger.error("Could not get user with id ${user.id}") }
            .getOrThrow()
    }

    override fun getProjectByName(orgName: String) {
        runCatching {
            gitlabClient.getProjectByNameWithNamespace(
                URLEncoder.encode(orgName, StandardCharsets.UTF_8.toString())
            )
        }
            .onFailure { logger.error("Could not get org info for $orgName") }
            .getOrThrow()
    }
}
