package net.leanix.vsm.gitlab.broker.connector.adapter.feign

import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.CommandRequest
import net.leanix.vsm.gitlab.broker.connector.domain.CommandEventAction
import net.leanix.vsm.gitlab.broker.connector.domain.CommandProvider
import net.leanix.vsm.gitlab.broker.connector.domain.EventType
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import org.springframework.stereotype.Component

@Component
class CommandFeignProvider(private val vsmClient: VsmClient) : CommandProvider {

    override fun sendCommand(assignment: GitLabAssignment, action: CommandEventAction) {
        val command = CommandRequest(
            type = EventType.COMMAND.type,
            action = action.action,
            scope = buildScope(assignment)
        )
        vsmClient.sendCommand(command)
    }

    private fun buildScope(assignment: GitLabAssignment) =
        "workspace/${assignment.workspaceId}/configuration/${assignment.configurationId}"
}
