package net.leanix.vsm.gitlab.broker.connector.adapter.feign

import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.RunState
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.UpdateRunStateRequest
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.connector.domain.RunProvider
import net.leanix.vsm.gitlab.broker.shared.Constants.GITLAB_ENTERPRISE_CONNECTOR
import org.springframework.stereotype.Component

@Component
class RunFeignProvider(private val vsmClient: VsmClient) : RunProvider {

    override fun updateRun(assignment: GitLabAssignment, runState: RunState, message: String?) {
        vsmClient.updateRunState(
            runId = assignment.runId,
            UpdateRunStateRequest(
                state = RunState.FINISHED,
                workspaceId = assignment.workspaceId.toString(),
                connector = GITLAB_ENTERPRISE_CONNECTOR,
                orgName = assignment.connectorConfiguration.orgName,
                message = message
            )
        )
    }
}
