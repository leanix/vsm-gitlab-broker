package net.leanix.vsm.gitlab.broker.connector.adapter.graphql

import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.runBlocking
import net.leanix.vsm.gitlab.broker.shared.properties.GitLabOnPremProperties
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import kotlin.reflect.KClass

@Component
class BasicGraphQLClient(private val gitLabOnPremProperties: GitLabOnPremProperties) {

    private val objectMapper = ObjectMapper()

    private val client = GraphQLWebClient(
        url = gitLabOnPremProperties.gitlabUrl + "/api/graphql",
        builder = WebClient.builder()
            .defaultHeaders {
                it.set(HttpHeaders.AUTHORIZATION, "Bearer ${gitLabOnPremProperties.gitlabToken}")
            }
    )

    @PostConstruct
    fun dummy() {
        execute(
            query = """
                query AllGroupsQuery(${'$'}group: ID!, ${'$'}pageCount: Int!, ${'$'}cursor: String) {
                    group(fullPath: ${'$'}group) {
                        id
                        name
                        projects(first: ${'$'}pageCount, after: ${'$'}cursor, includeSubgroups: true) {
                            pageInfo {
                                hasNextPage
                                endCursor
                            }
                            nodes {
                                name
                                path
                                id
                                archived
                                visibility
                                topics
                                webUrl
                                description
                                lastActivityAt
                                languages {
                                    name
                                    share
                                }
                                repository {
                                    diskPath
                                    rootRef
                                }
                                group {
                                    fullPath
                                }
                            }
                        }
                    }
                }
            """.trimIndent(),
            variables = mapOf(
                "pageCount" to 10,
                "cursor" to null,
                "group" to "cider"
            )
        )
    }


    fun execute(
        query: String,
        variables: Map<String, Any?>
    ): Any? {
        val pagingNeeded = variables.containsKey("pageCount")
        var cursor: String? = null
        val resultList = mutableListOf<Any?>()

        do {
            val result = runBlocking {
                client.execute(
                    BasicGraphQLClientRequest(
                        query = query,
                        variables = cursor?.let {
                            val variableMap = variables.toMutableMap()
                            variableMap.put("cursor", cursor)
                            variableMap
                        } ?: variables
                    )
                )
            }

            var hasNextPage = false
            if (pagingNeeded) {
                val pageInfoNode = objectMapper
                    .readTree(objectMapper.writeValueAsString(result.data))
                    .findValue("pageInfo")
                hasNextPage = pageInfoNode.get("hasNextPage").booleanValue()
                cursor = pageInfoNode.get("hasNextPage").textValue()
            }

            resultList.add(result.data)
        } while (hasNextPage)

        return if (pagingNeeded) resultList else resultList.get(0)
    }
}

class BasicGraphQLClientRequest(
    override val query: String,
    override val variables: Map<String, Any?>,
) : GraphQLClientRequest<Any> {
    override fun responseType(): KClass<Any> = Any::class

}
