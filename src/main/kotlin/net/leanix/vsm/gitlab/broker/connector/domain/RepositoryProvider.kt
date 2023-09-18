package net.leanix.vsm.gitlab.broker.connector.domain

interface RepositoryProvider {

    fun save(repository: Repository, assignment: GitLabAssignment, eventType: EventType)
    fun saveAll(repositories: List<Repository>, assignment: GitLabAssignment, eventType: EventType)
}
