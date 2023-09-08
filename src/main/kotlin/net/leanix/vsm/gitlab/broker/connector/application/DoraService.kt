package net.leanix.vsm.gitlab.broker.connector.application

import net.leanix.vsm.gitlab.broker.connector.domain.DoraProvider
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.connector.domain.GitlabProvider
import net.leanix.vsm.gitlab.broker.connector.domain.Repository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class DoraService(
    private val gitlabProvider: GitlabProvider,
    private val doraProvider: DoraProvider,
    @Value("\${leanix.vsm.dora.total-days:30}") val periodInDays: Long = 0
) {

    private val logger = LoggerFactory.getLogger(DoraService::class.java)

    @Async
    fun generateDoraEvents(repository: Repository, assignment: GitLabAssignment) {
        val periodInDaysInString = LocalDate.now().minusDays(periodInDays).toString()
        gitlabProvider.getDoraRawData(repository, periodInDaysInString)
            .takeIf { it.isNotEmpty() }
            ?.forEach {
                doraProvider.saveDora(it, assignment, repository)
            }
            ?: {
                logger.info(
                    "Repository does not have any valid pull requests for DORA metrics. " +
                        "Repository: ${repository.name}"
                )
            }
    }
}
