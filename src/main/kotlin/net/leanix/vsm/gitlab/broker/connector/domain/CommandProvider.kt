package net.leanix.vsm.gitlab.broker.connector.domain

interface CommandProvider {

    fun sendCommand(assignment: GitLabAssignment, action: CommandEventAction)
}
