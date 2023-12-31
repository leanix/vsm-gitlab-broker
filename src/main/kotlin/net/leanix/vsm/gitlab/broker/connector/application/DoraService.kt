package net.leanix.vsm.gitlab.broker.connector.application

import io.github.oshai.kotlinlogging.KotlinLogging
import net.leanix.vsm.gitlab.broker.connector.domain.DoraProvider
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.connector.domain.GitlabProvider
import net.leanix.vsm.gitlab.broker.connector.domain.Repository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class DoraService(
    private val gitlabProvider: GitlabProvider,
    private val doraProvider: DoraProvider,
    @Value("\${leanix.vsm.dora.total-days:30}") val periodInDays: Long = 0
) : BaseConnectorService() {

    private val logger = KotlinLogging.logger {}

    fun generateDoraEvents(repository: Repository, assignment: GitLabAssignment) {
        val periodInDaysInString = LocalDate.now().minusDays(periodInDays).toString()
        runCatching {
            gitlabProvider
                .getMergeRequestsForRepository(repository, periodInDaysInString)
                .takeIf { it.isNotEmpty() }
                ?.forEach { doraProvider.saveDora(it, assignment, repository) }
                ?: {
                    logger.warn {
                        "Repository does not have any valid pull requests for DORA metrics. " +
                            "Repository: ${repository.name}"
                    }
                }
        }.onFailure {
            logger.error { "Failed to generate DORA for repository: ${repository.name}: ${it.message}" }
            val reason = it.message ?: "unknown reason"
            logFailedMessages(
                code = "vsm.dora.failed",
                arguments = arrayOf(repository.name, reason),
                assignment = assignment
            )
        }.onSuccess {
            logger.info { "DORA generated for repository ${repository.name}" }
            logInfoMessages(
                code = "vsm.dora.success",
                arguments = arrayOf(repository.name),
                assignment = assignment
            )
        }
    }
}
