package net.leanix.vsm.gitlab.broker.connector.adapter.feign

import io.github.oshai.kotlinlogging.KotlinLogging
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.CommandRequest
import net.leanix.vsm.gitlab.broker.connector.domain.CommandEventAction
import net.leanix.vsm.gitlab.broker.connector.domain.CommandProvider
import net.leanix.vsm.gitlab.broker.connector.domain.EventType
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import org.springframework.stereotype.Component

@Component
class CommandFeignProvider(private val vsmClient: VsmClient) : CommandProvider {

    private val logger = KotlinLogging.logger {}

    override fun sendCommand(assignment: GitLabAssignment, action: CommandEventAction) {
        val command = CommandRequest(
            type = EventType.COMMAND.type,
            action = action.action,
            scope = buildScope(assignment)
        )
        vsmClient.sendCommand(command)
        logger.info {
            "Command sent with type ${EventType.COMMAND.type}, action ${action.action} " +
                "for run ${assignment.runId} and config ${assignment.configurationId}"
        }
    }

    private fun buildScope(assignment: GitLabAssignment) =
        "workspace/${assignment.workspaceId}/configuration/${assignment.configurationId}"
}
