package net.leanix.vsm.gitlab.broker.connector.scheduler

import net.leanix.vsm.gitlab.broker.connector.adapter.feign.VsmClient
import net.leanix.vsm.gitlab.broker.connector.application.AssignmentService
import net.leanix.vsm.gitlab.broker.connector.application.InitialStateService
import net.leanix.vsm.gitlab.broker.shared.cache.AssignmentsCache
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class HeartbeatScheduler(
    private val vsmClient: VsmClient,
    private val assignmentService: AssignmentService,
    private val initialStateService: InitialStateService
) {

    private val logger = LoggerFactory.getLogger(HeartbeatScheduler::class.java)

    @Scheduled(fixedRateString = "\${leanix.heartbeat.interval}")
    fun heartbeat() {
        AssignmentsCache.getAll().values.forEach { assignment ->
            logger.info("Sending heartbeat for runId: ${assignment.runId}")
            vsmClient.heartbeat(assignment.runId.toString())
                .takeIf { it.newConfigAvailable }
                ?.also {
                    assignmentService.getAssignments()?.let {
                        initialStateService.initState(it)
                    }
                }
        }
    }
}
