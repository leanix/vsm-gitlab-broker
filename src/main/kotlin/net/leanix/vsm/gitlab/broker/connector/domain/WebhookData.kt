package net.leanix.vsm.gitlab.broker.connector.domain

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.Date

data class ProjectCreated(
    @JsonProperty("project_id")
    val id: Int,
    @JsonProperty("event_name")
    val eventName: String,
    val name: String,
    val path: String,
    @JsonProperty("path_with_namespace")
    val pathWithNamespace: String,
    @JsonProperty("project_visibility")
    val projectVisibility: String,
)

fun ProjectCreated.getNamespace() = pathWithNamespace.substringBefore("/$name")

fun ProjectCreated.toRepository(gitlabUrl: String) = Repository(
    id = id.toString(),
    name = name,
    description = null,
    archived = false,
    url = "$gitlabUrl/$pathWithNamespace",
    visibility = projectVisibility,
    languages = null,
    tags = null,
    defaultBranch = "empty-branch",
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
    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss z")
    val createdAt: Date,
    @JsonProperty("source_branch")
    val sourceBranch: String,
    @JsonProperty("target_branch")
    val targetBranch: String,
    val title: String,
    @JsonProperty("updated_at")
    val updatedAt: String,
    @JsonProperty("last_commit")
    val lastCommit: GitlabCommit,
    val state: String,
    val action: String,
)

data class GitlabCommit(
    val id: String,
    val message: String,
    val title: String,
)
