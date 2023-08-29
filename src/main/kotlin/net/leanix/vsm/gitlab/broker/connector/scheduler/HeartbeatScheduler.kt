package net.leanix.vsm.gitlab.broker.connector.scheduler

import net.leanix.vsm.gitlab.broker.connector.adapter.feign.VsmClient
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.RunState
import net.leanix.vsm.gitlab.broker.connector.application.AssignmentService
import net.leanix.vsm.gitlab.broker.connector.application.InitialStateService
import net.leanix.vsm.gitlab.broker.connector.domain.RunProvider
import net.leanix.vsm.gitlab.broker.shared.cache.AssignmentsCache
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class HeartbeatScheduler(
    private val vsmClient: VsmClient,
    private val assignmentService: AssignmentService,
    private val initialStateService: InitialStateService,
    private val runProvider: RunProvider
) {

    private val logger = LoggerFactory.getLogger(HeartbeatScheduler::class.java)

//    @Scheduled(fixedRateString = "\${leanix.heartbeat.interval}")
    fun heartbeat() {
        val assignments = AssignmentsCache.getAll()
        var newConfigAvailable = false
        assignments.values.forEach { assignment ->
            logger.info("Sending heartbeat for runId: ${assignment.runId}")
            vsmClient.heartbeat(assignment.runId.toString())
                .takeIf { it.newConfigAvailable }
                ?.also {
                    newConfigAvailable = true
                }
        }

        if (newConfigAvailable) {
            assignmentService.getAssignments()?.let {
                initialStateService.initState(it)
            }
            assignments.values.forEach {
                runProvider.updateRun(
                    it,
                    RunState.FINISHED,
                    "Finished run after update"
                )
            }
        }
    }
}
