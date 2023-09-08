package net.leanix.vsm.gitlab.broker.connector.adapter.feign

import net.leanix.vsm.githubbroker.connector.domain.Dora
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.DoraRequest
import net.leanix.vsm.gitlab.broker.connector.domain.DoraProvider
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.connector.domain.Repository
import net.leanix.vsm.gitlab.broker.shared.Constants.GITLAB_ENTERPRISE_CONNECTOR
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FeignDoraProvider(
    private val vsmClient: VsmClient
) : DoraProvider {

    private val logger = LoggerFactory.getLogger(FeignDoraProvider::class.java)
    override fun saveDora(dora: Dora, assignment: GitLabAssignment, repository: Repository) {
        kotlin.runCatching {
            vsmClient.saveDora(
                DoraRequest(
                    repositoryId = repository.id,
                    repositoryName = repository.name,
                    repositoryUrl = repository.url,
                    connectorType = GITLAB_ENTERPRISE_CONNECTOR,
                    orgName = assignment.connectorConfiguration.orgName,
                    runId = assignment.runId,
                    configurationId = assignment.configurationId,
                    pullRequest = dora.pullRequest
                )
            )
        }.onFailure { logger.error("Failed to save dora events: ${dora.repositoryName}", it) }
    }
}
