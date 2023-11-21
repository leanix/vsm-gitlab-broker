package net.leanix.vsm.gitlab.broker.connector.domain

import net.leanix.vsm.gitlab.broker.connector.application.WebSocketMessageData
import java.util.UUID

data class GitLabAssignment(
    val runId: UUID,
    val workspaceId: UUID,
    val configurationId: UUID,
    val connectorConfiguration: GitLabConfiguration,
    val webSocketMessageData: WebSocketMessageData?
)

data class GitLabConfiguration(
    val orgName: String,
)
