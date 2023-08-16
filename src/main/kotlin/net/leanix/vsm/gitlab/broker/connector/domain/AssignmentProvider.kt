package net.leanix.vsm.gitlab.broker.connector.domain

interface AssignmentProvider {
    fun getAssignments(): Result<List<GitLabAssignment>>
}
