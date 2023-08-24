package net.leanix.vsm.gitlab.broker.connector.application

import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class InitialStateService(val repositoryService: RepositoryService) {

    private val logger: Logger = LoggerFactory.getLogger(InitialStateService::class.java)

    fun initState(assignments: List<GitLabAssignment>) {
        logger.info("Started get initial state")

        runCatching {
            assignments.forEach {
                logger.info(
                    "Received assignment for ${it.connectorConfiguration.orgName} " +
                        "with configuration id: ${it.configurationId} and with run id: ${it.runId}"
                )
                repositoryService.importAllRepositories(it)
            }
        }
    }
}
