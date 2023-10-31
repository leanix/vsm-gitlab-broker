package net.leanix.vsm.gitlab.broker.connector.adapter.feign.data

data class GitlabVersionResponse(
    val version: String,
    val revision: String,
)
