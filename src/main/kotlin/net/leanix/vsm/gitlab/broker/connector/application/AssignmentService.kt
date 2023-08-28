package net.leanix.vsm.gitlab.broker.connector.application

import net.leanix.vsm.gitlab.broker.connector.domain.AssignmentProvider
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.shared.cache.AssignmentsCache
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AssignmentService(
    private val assignmentProvider: AssignmentProvider
) {

    private val logger = LoggerFactory.getLogger(AssignmentService::class.java)

    fun getAssignments(): List<GitLabAssignment>? {
        kotlin.runCatching {
            val assignments = assignmentProvider.getAssignments().onFailure {
                logger.error("Failed to retrieve assignment list: ", it)
            }.onSuccess {
                logger.info("Assignment list retrieved with success with ${it.size} assignments")
            }.getOrThrow()

            AssignmentsCache.deleteAll()
            AssignmentsCache.addAll(assignments)
            return assignments
        }
        return null
    }
}
