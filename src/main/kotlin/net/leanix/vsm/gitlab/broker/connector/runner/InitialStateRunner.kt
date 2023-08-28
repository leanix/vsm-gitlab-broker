package net.leanix.vsm.gitlab.broker.connector.runner

import io.github.oshai.kotlinlogging.KotlinLogging
import net.leanix.vsm.gitlab.broker.connector.application.AssignmentService
import net.leanix.vsm.gitlab.broker.connector.application.InitialStateService
import net.leanix.vsm.gitlab.broker.shared.cache.AssignmentsCache
import net.leanix.vsm.gitlab.broker.webhook.domain.WebhookService
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class InitialStateRunner(
    private val assignmentService: AssignmentService,
    private val initialStateService: InitialStateService,
    private val webhookService: WebhookService
) : ApplicationRunner {

    private val logger = KotlinLogging.logger {}

    override fun run(args: ApplicationArguments?) {
        logger.info { "Started to get initial state" }
//        fetchAssignments()
        setupWebhook()
    }

    private fun fetchAssignments() {
        runCatching {
            assignmentService.getAssignments()?.let {
                initialStateService.initState(it)
            }
        }.onSuccess {
            logger.info { "Cached ${AssignmentsCache.getAll().size} assignments" }
        }.onFailure { e ->
            logger.error(e) { "Failed to get initial state: ${e.message}" }
        }
    }

    private fun setupWebhook() {
        runCatching {
            webhookService.registerWebhook()
        }.onSuccess {
            logger.info { "webhook registered successfully" }
        }.onFailure {
            logger.error(it) { "webhook registration failed" }
        }
    }
}
