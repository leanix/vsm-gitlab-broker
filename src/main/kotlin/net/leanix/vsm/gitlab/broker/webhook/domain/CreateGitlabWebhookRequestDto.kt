package net.leanix.vsm.gitlab.broker.webhook.domain

import com.nimbusds.jose.shaded.gson.annotations.SerializedName

data class CreateGitlabWebhookRequestDto(
    val url: String,
    val token: String,
    @SerializedName("push_events")
    val pushEvents: Boolean,
    @SerializedName("tag_push_events")
    val tagPushEvents: Boolean,
    @SerializedName("merge_requests_events")
    val mergeRequestsEvents: Boolean,
    @SerializedName("repository_update_events")
    val repositoryUpdateEvents: Boolean,
    @SerializedName("enable_ssl_verification")
    val enableSSLVerification: Boolean,
)
