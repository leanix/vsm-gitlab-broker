package net.leanix.vsm.gitlab.broker.shared.cache

import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment

object AssignmentsCache {

    private val assigmentCache: MutableMap<String, GitLabAssignment> = mutableMapOf()

    fun addAll(newAssignments: List<GitLabAssignment>) {
        newAssignments.forEach { assignment -> assigmentCache[assignment.connectorConfiguration.orgName] = assignment }
    }

    fun get(key: String): GitLabAssignment? {
        return assigmentCache[key]
    }

    fun getAll(): Map<String, GitLabAssignment> {
        return assigmentCache
    }

    fun deleteAll() {
        assigmentCache.clear()
    }
}
