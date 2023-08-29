package net.leanix.vsm.gitlab.broker.connector.runner

import net.leanix.vsm.gitlab.broker.connector.application.AssignmentService
import net.leanix.vsm.gitlab.broker.connector.application.ValidationService
import net.leanix.vsm.gitlab.broker.shared.cache.AssignmentsCache
import net.leanix.vsm.gitlab.broker.webhook.domain.WebhookService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class InitialStateRunner(
    private val assignmentService: AssignmentService,
    private val validationService: ValidationService,
    private val webhookService: WebhookService
) : ApplicationRunner {

    private val logger: Logger = LoggerFactory.getLogger(InitialStateRunner::class.java)

    override fun run(args: ApplicationArguments?) {
        logger.info("Started to get initial state")
        fetchAndValidateAssignments()
        setupWebhook()
    }

    private fun fetchAndValidateAssignments() {
        runCatching {
            assignmentService.getAssignments()?.forEach { assignment ->
                logger.info(
                    "Received assignment for ${assignment.connectorConfiguration.orgName} " +
                        "with configuration id: ${assignment.configurationId} and with run id: ${assignment.runId}"
                )
                validationService.validateConfiguration(assignment)
            }
        }.onSuccess {
            logger.info("Cached ${AssignmentsCache.getAll().size} assignments")
        }.onFailure { e ->
            logger.error("Failed to get initial state", e)
        }
    }

    private fun setupWebhook() {
        runCatching {
            webhookService.registerWebhook()
        }.onSuccess {
            logger.info("webhook registered successfully")
        }.onFailure {
            logger.info("webhook registration failed", it)
        }
    }
}
