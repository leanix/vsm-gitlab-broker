package net.leanix.vsm.gitlab.broker.connector.domain

data class Repository(
    val id: String,
    val name: String,
    val description: String?,
    val url: String,
    val archived: Boolean?,
    val visibility: String?,
    val languages: List<Language>?,
    val tags: List<String>?,
    val defaultBranch: String,
    val groupName: String
)
