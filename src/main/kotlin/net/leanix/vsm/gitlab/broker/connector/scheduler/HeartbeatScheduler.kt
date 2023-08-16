package net.leanix.vsm.gitlab.broker.connector.scheduler

import net.leanix.vsm.gitlab.broker.connector.adapter.feign.VsmClient
import net.leanix.vsm.gitlab.broker.shared.cache.AssignmentsCache
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class HeartbeatScheduler(
    private val vsmClient: VsmClient
) {

    private val logger = LoggerFactory.getLogger(HeartbeatScheduler::class.java)

    @Scheduled(fixedRate = 300000) // 5 minute
    fun heartbeat() {
        AssignmentsCache.getAll().values.forEach { assigment ->
            logger.info("Sending heartbeat for runId: ${assigment.runId}")
            vsmClient.heartbeat(assigment.runId.toString())
        }
    }
}
