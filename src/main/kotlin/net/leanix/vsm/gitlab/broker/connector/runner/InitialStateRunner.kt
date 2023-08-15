package net.leanix.vsm.gitlab.broker.connector.runner

import net.leanix.vsm.gitlab.broker.connector.applicaiton.AssignmentService
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.shared.cache.AssignmentsCache
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class InitialStateRunner(
    private val assignmentService: AssignmentService,
) : ApplicationRunner {

    private val logger: Logger = LoggerFactory.getLogger(InitialStateRunner::class.java)

    override fun run(args: ApplicationArguments?) {
        logger.info("Started to get initial state")
        runCatching {
            getAssignments()?.forEach { assignment ->
                logger.info(
                    "Received assignment for ${assignment.connectorConfiguration.orgName} " +
                        "with configuration id: ${assignment.configurationId} and with run id: ${assignment.runId}"
                )
            }
        }.onSuccess {
            logger.info("Cached ${AssignmentsCache.getAll().size} assignments")
        }.onFailure { e ->
            logger.error("Failed to get initial state", e)
        }
    }

    private fun getAssignments(): List<GitLabAssignment>? {
        kotlin.runCatching {
            val assignments = assignmentService.getAssignments()
            AssignmentsCache.addAll(assignments)
            return assignments
        }.onFailure {
            logger.error("Failed to get initial state. No assignment found for this workspace id")
        }
        return null
    }
}
