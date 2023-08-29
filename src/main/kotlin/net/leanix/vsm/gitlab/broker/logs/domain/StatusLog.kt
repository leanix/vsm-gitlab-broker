package net.leanix.vsm.gitlab.broker.logs.domain

import java.util.UUID

data class StatusLog(
    val runId: UUID,
    val configurationId: UUID,
    val status: LogStatus,
    val message: String?
)
