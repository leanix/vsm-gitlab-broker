package net.leanix.vsm.gitlab.broker.connector.adapter.feign.data

data class CommandRequest(
    val type: String,
    val scope: String,
    val action: String
)
