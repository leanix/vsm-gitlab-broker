package net.leanix.vsm.gitlab.broker.connector.domain

import java.util.UUID

data class GitLabAssignment(
    val runId: UUID,
    val workspaceId: UUID,
    val configurationId: UUID,
    val connectorConfiguration: GitLabConfiguration
)

data class GitLabConfiguration(
    val orgName: String,
)
