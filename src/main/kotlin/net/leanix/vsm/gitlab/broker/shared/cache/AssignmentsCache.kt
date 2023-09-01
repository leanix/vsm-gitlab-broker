package net.leanix.vsm.gitlab.broker.shared.cache

import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment

object AssignmentsCache {

    private val assignmentCache: MutableMap<Group, GitLabAssignment> = mutableMapOf()

    fun addAll(newAssignments: List<GitLabAssignment>) {
        newAssignments.forEach { assignment -> assignmentCache[assignment.connectorConfiguration.orgName] = assignment }
    }

    fun get(namespace: String): GitLabAssignment? {
        return assignmentCache.entries.firstOrNull { it.key.matchesNamespace(namespace) }?.value
    }

    fun getAll(): Map<String, GitLabAssignment> {
        return assignmentCache
    }

    fun deleteAll() {
        assignmentCache.clear()
    }
}

typealias Group = String

fun Group.matchesNamespace(
    namespace: String
) =
    namespace == this || (namespace.startsWith(this) && namespace.elementAtOrNull(this.length) == '/')
