package net.leanix.vsm.gitlab.broker.connector.adapter.feign.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class UpdateRunStateRequest(
    val state: RunState,
    val workspaceId: String? = null,
    val connector: String? = null,
    val orgName: String? = null,
    val message: String? = null,
    val region: String? = null
)

enum class RunState {
    RUNNING,
    FINISHED,
}
