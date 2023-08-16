package net.leanix.vsm.gitlab.broker.connector.adapter.feign

import net.leanix.vsm.gitlab.broker.connector.domain.AssignmentProvider
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import org.springframework.stereotype.Component

@Component
class FeignAssignmentProvider(private val vsmClient: VsmClient) : AssignmentProvider {
    override fun getAssignments(): Result<List<GitLabAssignment>> {
        return kotlin.runCatching {
            vsmClient.getAssignments()
        }
    }
}
