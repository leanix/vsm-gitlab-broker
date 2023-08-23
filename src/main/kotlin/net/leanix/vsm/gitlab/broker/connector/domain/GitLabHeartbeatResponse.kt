package net.leanix.vsm.gitlab.broker.connector.domain

data class GitLabHeartbeatResponse(
    val status: String,
    val newConfigAvailable: Boolean
)
