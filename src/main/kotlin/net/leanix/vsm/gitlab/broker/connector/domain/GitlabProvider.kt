package net.leanix.vsm.gitlab.broker.connector.domain

interface GitlabProvider {

    fun getAllRepositories(assignment: GitLabAssignment): Result<List<Repository>>
}
