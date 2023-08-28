package net.leanix.vsm.gitlab.broker.webhook.domain

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Date

data class GitlabWebhook(
    val id: Int,
    val url: String,
    @JsonProperty("created_at")
    val createdAt: Date,
    @JsonProperty("push_events")
    val pushEvents: Boolean,
    @JsonProperty("tag_push_events")
    val tagPushEvents: Boolean,
    @JsonProperty("merge_requests_events")
    val mergeRequestsEvents: Boolean,
    @JsonProperty("repository_update_events")
    val repositoryUpdateEvents: Boolean,
    @JsonProperty("enable_ssl_verification")
    val enableSSLVerification: Boolean,
)
