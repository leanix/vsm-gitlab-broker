package net.leanix.vsm.gitlab.broker.connector.domain

interface DoraProvider {
    fun saveDora(dora: Dora, assignment: GitLabAssignment, repository: Repository)
}
