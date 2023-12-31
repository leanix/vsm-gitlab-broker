package net.leanix.vsm.gitlab.broker.connector.domain

interface GitlabProvider {

    fun getAllRepositories(assignment: GitLabAssignment): Result<List<Repository>>

    fun getRepositoryByPath(nameWithNamespace: String, gitlabAssignment: GitLabAssignment): Repository

    fun getMergeRequestsForRepository(repository: Repository, periodInDays: String): List<Dora>
}
