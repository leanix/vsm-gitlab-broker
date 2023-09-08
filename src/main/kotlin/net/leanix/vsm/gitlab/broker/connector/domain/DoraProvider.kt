package net.leanix.vsm.gitlab.broker.connector.domain

import net.leanix.vsm.githubbroker.connector.domain.Dora

interface DoraProvider {
    fun saveDora(dora: Dora, assignment: GitLabAssignment, repository: Repository)
}
