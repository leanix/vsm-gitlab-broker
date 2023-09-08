package net.leanix.vsm.gitlab.broker.connector.adapter.graphql

import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.expediagroup.graphql.client.types.GraphQLClientError
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import net.leanix.githubbroker.connector.adapter.graphql.parser.LanguageParser
import net.leanix.gitlabbroker.connector.adapter.graphql.data.AllGroupsQuery
import net.leanix.gitlabbroker.connector.adapter.graphql.data.ProjectByPathQuery
import net.leanix.gitlabbroker.connector.adapter.graphql.data.PullRequestsForProjectQuery
import net.leanix.gitlabbroker.connector.adapter.graphql.data.allgroupsquery.ProjectConnection
import net.leanix.gitlabbroker.connector.adapter.graphql.data.projectbypathquery.Project
import net.leanix.gitlabbroker.connector.adapter.graphql.data.pullrequestsforprojectquery.MergeRequest
import net.leanix.vsm.githubbroker.connector.domain.Author
import net.leanix.vsm.githubbroker.connector.domain.Commit
import net.leanix.vsm.githubbroker.connector.domain.Dora
import net.leanix.vsm.githubbroker.connector.domain.PullRequest
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.connector.domain.GitlabProvider
import net.leanix.vsm.gitlab.broker.connector.domain.Language
import net.leanix.vsm.gitlab.broker.connector.domain.Repository
import net.leanix.vsm.gitlab.broker.shared.exception.GraphqlException
import net.leanix.vsm.gitlab.broker.shared.exception.NoRepositoriesFound
import net.leanix.vsm.gitlab.broker.shared.properties.GitLabOnPremProperties
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class GitlabGraphqlProvider(private val gitLabOnPremProperties: GitLabOnPremProperties) : GitlabProvider {

    private val logger = KotlinLogging.logger {}

    private val client = GraphQLWebClient(
        url = gitLabOnPremProperties.gitlabUrl + "/api/graphql",
        builder = WebClient.builder()
            .defaultHeaders {
                it.set(HttpHeaders.AUTHORIZATION, "Bearer ${gitLabOnPremProperties.gitlabToken}")
            }
    )

    override fun getAllRepositories(assignment: GitLabAssignment): Result<List<Repository>> {
        var cursor: String? = null
        var hasNext: Boolean
        val repositories = mutableListOf<Repository>()
        do {
            val query = AllGroupsQuery(
                AllGroupsQuery.Variables(
                    group = assignment.connectorConfiguration.orgName,
                    pageCount = 10,
                    cursor = cursor
                )
            )
            val response = executeQuery(query)
            if (response.errors != null && response.errors?.isNotEmpty() == true) {
                return Result.failure(GraphqlException(makeErrorString(response.errors!!)))
            } else {
                repositories += parseProjects(
                    response.data?.group?.projects
                )
                hasNext = response.data?.group?.projects?.pageInfo?.hasNextPage ?: false
                cursor = response.data?.group?.projects?.pageInfo?.endCursor
            }
        } while (hasNext)

        return Result.success(repositories)
    }

    override fun getRepositoryByPath(
        nameWithNamespace: String
    ) =
        executeQuery(ProjectByPathQuery(ProjectByPathQuery.Variables(fullPath = nameWithNamespace)))
            .data
            ?.project
            ?.toRepository()
            ?: throw GraphqlException("No gitlab project found at path: $nameWithNamespace")

    override fun getDoraRawData(
        repository: Repository,
        periodInDaysInString: String
    ) =
        PullRequestsForProjectQuery.Variables(
            fullPath = "${repository.groupName}/${repository.path}",
            defaultBranch = repository.defaultBranch
        )
            .let { PullRequestsForProjectQuery(it) }
            .let { executeQuery(it) }
            .data
            ?.project
            ?.mergeRequests
            ?.nodes
            ?.filterNotNull()
            ?.map { Dora(repository.name, repository.url, it.toPullRequest()) }
            ?: throw GraphqlException("No gitlab project found at path: ${repository.groupName}/${repository.path}")

    private fun parseProjects(
        repositories: ProjectConnection?
    ): List<Repository> {
        return if (repositories?.nodes != null && repositories.nodes.isNotEmpty()) {
            repositories.nodes.filterNotNull()
                .map { project ->
                    Repository(
                        id = project.id,
                        name = project.name,
                        description = project.description,
                        archived = project.archived,
                        url = project.webUrl!!,
                        visibility = project.visibility,
                        languages = LanguageParser.parse(project.languages),
                        tags = project.topics,
                        defaultBranch = project.repository?.rootRef ?: "empty-branch",
                        groupName = project.group!!.fullPath,
                        path = project.path
                    )
                }
        } else {
            logger.info { "Zero repositories found" }
            throw NoRepositoriesFound()
        }
    }

    private fun <T : Any> executeQuery(query: GraphQLClientRequest<T>): GraphQLClientResponse<T> {
        return runBlocking {
            client.execute(query)
        }
    }
}

fun Project.toRepository() = Repository(
    id = id,
    name = name,
    description = description,
    archived = archived,
    url = webUrl!!,
    visibility = visibility,
    languages = languages?.map { Language(it.name, it.name, it.share!!) },
    tags = topics,
    defaultBranch = repository?.rootRef ?: "empty-branch",
    groupName = group!!.fullPath,
    path = path
)

fun MergeRequest.toPullRequest() = PullRequest(
    id = id,
    baseRefName = targetBranch,
    mergedAt = mergedAt.toString(),
    commits = commits
        ?.nodes
        ?.filterNotNull()
        ?.map {
            Commit(
                id = it.id,
                changeTime = it.authoredDate.toString(),
                author = Author(it.author?.name ?: "", it.authorEmail ?: "", it.author?.username)
            )
        }
        ?: emptyList()
)

fun makeErrorString(errors: List<GraphQLClientError>) = errors.map { error -> error.message }
    .joinToString { s -> s }
