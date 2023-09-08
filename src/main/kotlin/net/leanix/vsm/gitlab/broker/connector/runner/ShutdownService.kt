package net.leanix.vsm.gitlab.broker.connector.runner

import jakarta.annotation.PreDestroy
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.RunState
import net.leanix.vsm.gitlab.broker.connector.domain.RunProvider
import net.leanix.vsm.gitlab.broker.shared.cache.AssignmentsCache
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ShutdownService(private val runProvider: RunProvider) {

    private val logger = LoggerFactory.getLogger(ShutdownService::class.java)

    @PreDestroy
    fun onDestroy() {
        logger.info("Shutting down gitlab on-prem")
        if (AssignmentsCache.getAll().isEmpty()) {
            logger.info("Shutting down gitlab broker before receiving any assignment")
        } else {
            AssignmentsCache.getAll().values.forEach { assignment ->
                runProvider.updateRun(
                    runState = RunState.FINISHED,
                    assignment = assignment,
                    message = "gracefully stopped GitLab broker"
                )
            }
        }
    }
}
