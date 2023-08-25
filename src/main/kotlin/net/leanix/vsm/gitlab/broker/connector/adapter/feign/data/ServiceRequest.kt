package net.leanix.vsm.gitlab.broker.connector.adapter.feign.data

import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.connector.domain.Language
import net.leanix.vsm.gitlab.broker.connector.domain.Repository
import net.leanix.vsm.gitlab.broker.shared.Constants
import java.util.UUID

data class ServiceRequest(
    val id: String,
    val runId: UUID,
    val configurationId: UUID?,
    val source: String,
    val name: String,
    val description: String?,
    val url: String?,
    val archived: Boolean?,
    val visibility: String?,
    val organizationName: String,
    val languages: List<Language>? = null,
    val labels: List<Tag>? = null,
    val contributors: List<String>? = null
) {
    companion object {
        fun fromDomain(repository: Repository, assignment: GitLabAssignment) = ServiceRequest(
            id = repository.id,
            runId = assignment.runId,
            configurationId = assignment.configurationId,
            source = Constants.GITLAB_ENTERPRISE,
            name = repository.name,
            description = repository.description,
            url = repository.url,
            archived = repository.archived,
            visibility = repository.visibility,
            languages = repository.languages,
            labels = repository.tags?.map { Tag(it, it) },
            contributors = emptyList(),
            organizationName = assignment.connectorConfiguration.orgName
        )
    }
}

data class Tag(
    val id: String,
    val name: String
)
