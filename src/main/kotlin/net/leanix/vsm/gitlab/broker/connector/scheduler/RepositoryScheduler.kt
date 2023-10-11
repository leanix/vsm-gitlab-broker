package net.leanix.vsm.gitlab.broker.connector.scheduler

import net.leanix.vsm.gitlab.broker.connector.application.InitialStateService
import net.leanix.vsm.gitlab.broker.shared.cache.AssignmentsCache
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class RepositoryScheduler(
    private val initialStateService: InitialStateService,
) {

    private val logger = LoggerFactory.getLogger(RepositoryScheduler::class.java)

    @Scheduled(cron = "\${leanix.vsm.schedule:0 0 4 * * *}")
    fun getAllRepositories() {
        logger.info("Started repository scheduler")
        initialStateService.initState(AssignmentsCache.getAll().values.toList())
        logger.info("Finished repository scheduler")
    }
}
