package net.leanix.vsm.gitlab.broker.connector.domain

import net.leanix.vsm.githubbroker.connector.domain.Dora

interface GitlabProvider {

    fun getAllRepositories(assignment: GitLabAssignment): Result<List<Repository>>

    fun getRepositoryByPath(nameWithNamespace: String): Repository

    fun getDoraRawData(repository: Repository, periodInDaysInString: String): List<Dora>
}
