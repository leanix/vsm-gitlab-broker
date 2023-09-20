package net.leanix.vsm.gitlab.broker.connector.adapter.feign

import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.ServiceRequest
import net.leanix.vsm.gitlab.broker.connector.domain.EventType
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.connector.domain.Repository
import net.leanix.vsm.gitlab.broker.connector.domain.RepositoryProvider
import org.springframework.stereotype.Component

@Component
class RepositoryFeignProvider(private val vsmClient: VsmClient) : RepositoryProvider {
    override fun save(repository: Repository, assignment: GitLabAssignment, eventType: EventType) =
        vsmClient.saveService(eventType.type, ServiceRequest.fromDomain(repository, assignment))

    override fun saveAll(
        repositories: List<Repository>,
        assignment: GitLabAssignment,
        eventType: EventType
    ) =
        repositories
            .map { ServiceRequest.fromDomain(it, assignment) }
            .let { vsmClient.bulkSaveServices(eventType.type, it) }
}
