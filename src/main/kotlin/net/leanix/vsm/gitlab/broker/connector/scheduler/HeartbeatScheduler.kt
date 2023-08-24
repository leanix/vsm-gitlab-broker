package net.leanix.vsm.gitlab.broker.connector.scheduler

import net.leanix.vsm.gitlab.broker.connector.adapter.feign.VsmClient
import net.leanix.vsm.gitlab.broker.connector.applicaiton.AssignmentService
import net.leanix.vsm.gitlab.broker.shared.cache.AssignmentsCache
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class HeartbeatScheduler(
    private val vsmClient: VsmClient,
    private val assignmentService: AssignmentService
) {

    private val logger = LoggerFactory.getLogger(HeartbeatScheduler::class.java)

    @Scheduled(fixedRateString = "\${leanix.heartbeat.interval}")
    @Suppress("ForbiddenComment")
    fun heartbeat() {
        AssignmentsCache.getAll().values.forEach { assignment ->
            logger.info("Sending heartbeat for runId: ${assignment.runId}")
            vsmClient.heartbeat(assignment.runId.toString())
                .takeIf { it.newConfigAvailable }
                ?.also {
                    assignmentService.getAssignments()
                    // TODO: here we need to re-fetch everything for this config
                    // remove @Suppress from function definition
                }
        }
    }
}
