package net.leanix.vsm.gitlab.broker.logs.domain

import java.util.UUID

data class AdminLog(
    val runId: UUID,
    val configurationId: UUID,
    val subject: String,
    val level: LogLevel,
    val message: String?
)
