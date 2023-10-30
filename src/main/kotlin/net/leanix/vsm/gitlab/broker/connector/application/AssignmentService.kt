package net.leanix.vsm.gitlab.broker.connector.application

import io.github.oshai.kotlinlogging.KotlinLogging
import net.leanix.vsm.gitlab.broker.connector.domain.AssignmentProvider
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.shared.cache.AssignmentsCache
import org.springframework.stereotype.Service

@Service
class AssignmentService(
    private val assignmentProvider: AssignmentProvider
) {

    private val logger = KotlinLogging.logger {}

    fun getAssignments(): List<GitLabAssignment>? {
        kotlin.runCatching {
            val assignments = assignmentProvider.getAssignments().onFailure {
                logger.error {
                    "Failed to retrieve assignment list, " +
                        "please make sure you are running one instance of GitLab Broker"
                }
            }.onSuccess {
                logger.info { "Assignment list retrieved with success with ${it.size} assignments" }
            }.getOrThrow()

            AssignmentsCache.deleteAll()
            AssignmentsCache.addAll(assignments)
            return assignments
        }
        return null
    }
}
