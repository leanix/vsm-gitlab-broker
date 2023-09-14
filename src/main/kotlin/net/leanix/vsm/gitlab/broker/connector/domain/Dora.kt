package net.leanix.vsm.githubbroker.connector.domain

data class Dora(
    val repositoryName: String,
    val repositoryUrl: String,
    val pullRequest: PullRequest
)

data class PullRequest(
    val id: String,
    val baseRefName: String,
    val mergedAt: String,
    val commits: List<Commit> = emptyList()
)

data class Commit(
    val id: String,
    val changeTime: String,
    val author: Author
)

data class Author(
    val name: String,
    val email: String,
    val username: String?
)
