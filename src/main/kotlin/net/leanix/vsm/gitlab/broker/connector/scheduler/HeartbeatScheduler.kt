package net.leanix.vsm.gitlab.broker.connector.scheduler

import net.leanix.vsm.gitlab.broker.connector.adapter.feign.VsmClient
import net.leanix.vsm.gitlab.broker.shared.cache.AssignmentsCache
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class HeartbeatScheduler(
    private val vsmClient: VsmClient
) {

    @Scheduled(fixedRate = 60000) // 1 minute
    fun heartbeat() {
        AssignmentsCache.getAll().values.forEach { assigment ->
            println("Sending heartbeat for runId: ${assigment.runId}")
            vsmClient.heartbeat(assigment.runId.toString())
        }
    }
}
