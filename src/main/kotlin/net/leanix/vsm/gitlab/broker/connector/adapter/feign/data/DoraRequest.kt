package net.leanix.vsm.gitlab.broker.connector.adapter.feign.data

import net.leanix.vsm.githubbroker.connector.domain.PullRequest
import java.util.UUID

data class DoraRequest(
    val repositoryId: String,
    val repositoryName: String,
    val repositoryUrl: String,
    val connectorType: String,
    val orgName: String,
    val runId: UUID,
    val configurationId: UUID,
    val pullRequest: PullRequest
)
