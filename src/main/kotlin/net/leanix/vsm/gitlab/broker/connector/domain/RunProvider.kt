package net.leanix.vsm.gitlab.broker.connector.domain

import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.RunState

interface RunProvider {

    fun updateRun(assignment: GitLabAssignment, runState: RunState, message: String?)
}
