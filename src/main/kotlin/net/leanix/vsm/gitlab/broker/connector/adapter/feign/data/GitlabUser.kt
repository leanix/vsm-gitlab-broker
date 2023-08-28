package net.leanix.vsm.gitlab.broker.connector.adapter.feign.data

import com.fasterxml.jackson.annotation.JsonProperty

class GitlabUser(
    @JsonProperty("id")
    val id: Int,
    @JsonProperty("is_admin")
    val isAdmin: Boolean?
)
