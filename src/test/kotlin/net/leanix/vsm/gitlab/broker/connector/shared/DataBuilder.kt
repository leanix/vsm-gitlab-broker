package net.leanix.vsm.gitlab.broker.connector.shared

import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.GitlabUser
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabConfiguration
import java.util.*

object DataBuilder {

    fun getGitlabAssignment() = GitLabAssignment(
        runId = UUID.randomUUID(),
        workspaceId = UUID.randomUUID(),
        configurationId = UUID.randomUUID(),
        connectorConfiguration = GitLabConfiguration("org-1")
    )

    fun getGitlabCurrentUser() = GitlabUser(
        id = 1,
        isAdmin = null
    )

    fun getGitlabUser(isAdmin: Boolean) = GitlabUser(
        id = 1,
        isAdmin = isAdmin
    )
}
