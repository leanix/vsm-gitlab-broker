package net.leanix.vsm.gitlab.broker.connector.shared

import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.GitlabGroup
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.GitlabUser
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabConfiguration
import java.util.UUID

fun getGitlabAssignment() = GitLabAssignment(
    runId = UUID.randomUUID(),
    workspaceId = UUID.randomUUID(),
    configurationId = UUID.randomUUID(),
    connectorConfiguration = GitLabConfiguration("group-1")
)

fun getGitlabCurrentUser(isAdmin: Boolean) = GitlabUser(
    id = 1,
    isAdmin = isAdmin
)

fun getAllGroups() = listOf(
    GitlabGroup(
        id = 1,
        fullPath = "group-1",
    ),
    GitlabGroup(
        id = 2,
        fullPath = "group-2",
    )
)
