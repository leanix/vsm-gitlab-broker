package net.leanix.vsm.gitlab.broker.connector.adapter.feign.data

import com.fasterxml.jackson.annotation.JsonProperty

class GitlabGroup(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("full_path")
    val fullPath: String
)
