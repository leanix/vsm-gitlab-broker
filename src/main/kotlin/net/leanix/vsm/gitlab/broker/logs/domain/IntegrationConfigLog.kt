package net.leanix.vsm.gitlab.broker.logs.domain

import java.util.UUID

data class IntegrationConfigLog(
    val runId: UUID,
    val configurationId: UUID,
    val field: String,
    val error: String
)
