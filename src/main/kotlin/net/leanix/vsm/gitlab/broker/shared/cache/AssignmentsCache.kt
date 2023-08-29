package net.leanix.vsm.gitlab.broker.shared.cache

import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment

object AssignmentsCache {

    private val assignmentCache: MutableMap<String, GitLabAssignment> = mutableMapOf()

    fun addAll(newAssignments: List<GitLabAssignment>) {
        newAssignments.forEach { assignment -> assignmentCache[assignment.connectorConfiguration.orgName] = assignment }
    }

    fun get(key: String): GitLabAssignment? {
        return assignmentCache[key]
    }

    fun getAll(): Map<String, GitLabAssignment> {
        return assignmentCache
    }

    fun deleteAll() {
        assignmentCache.clear()
    }
}
