package net.leanix.vsm.gitlab.broker.connector.adapter.graphql

import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import kotlinx.coroutines.runBlocking
import net.leanix.githubbroker.connector.adapter.graphql.data.AllGroupsQuery
import net.leanix.githubbroker.connector.adapter.graphql.data.allgroupsquery.ProjectConnection
import net.leanix.vsm.gitlab.broker.connector.adapter.graphql.parser.LanguageParser
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.connector.domain.GitlabProvider
import net.leanix.vsm.gitlab.broker.connector.domain.Repository
import net.leanix.vsm.gitlab.broker.shared.exception.VsmException
import net.leanix.vsm.gitlab.broker.shared.exception.VsmException.GraphqlException
import net.leanix.vsm.gitlab.broker.shared.properties.GitLabOnPremProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class GitlabGraphqlProvider(private val gitLabOnPremProperties: GitLabOnPremProperties) : GitlabProvider {

    private val logger: Logger = LoggerFactory.getLogger(GitlabGraphqlProvider::class.java)

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
                return Result.failure(
                    GraphqlException(
                        response.errors!!.map { error -> error.message }
                            .joinToString { s -> s },
                    )
                )
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
                    )
                }
        } else {
            logger.info("Zero repositories found")
            throw VsmException.NoRepositoriesFound()
        }
    }

    private fun <T : Any> executeQuery(query: GraphQLClientRequest<T>): GraphQLClientResponse<T> {
        return runBlocking {
            client.execute(query)
        }
    }
}
