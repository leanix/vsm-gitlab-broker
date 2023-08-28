package net.leanix.vsm.gitlab.broker.connector.domain

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Date

data class ProjectCreated(
    @JsonProperty("project_id")
    val id: Int,
    val eventName: String,
    val name: String,
    val path: String,
    @JsonProperty("path_with_namespace")
    val pathWithNamespace: String,
    @JsonProperty("project_visibility")
    val projectVisibility: String,
)

data class MergeRequest(
    val project: Project,
    @JsonProperty("object_attributes")
    val objectAttributes: ObjectAttributes,
)

data class Project(
    val id: Int,
    val name: String,
    @JsonProperty("web_url")
    val webURL: String,
    val namespace: String,
    @JsonProperty("path_with_namespace")
    val pathWithNamespace: String,
    @JsonProperty("default_branch")
    val defaultBranch: String,
)

data class ObjectAttributes(
    val description: String,
    val created_at: Date,
    val source_branch: String,
    val target_branch: String,
    val title: String,
    val updated_at: String,
    val last_commit: GitlabCommit,
    val state: String,
    val action: String,
)

data class GitlabCommit(
    val id: String,
    val message: String,
    val title: String,
)
