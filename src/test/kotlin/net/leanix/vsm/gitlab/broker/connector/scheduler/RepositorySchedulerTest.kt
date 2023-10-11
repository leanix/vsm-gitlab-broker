package net.leanix.vsm.gitlab.broker.connector.scheduler

import com.github.tomakehurst.wiremock.client.WireMock
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabConfiguration
import net.leanix.vsm.gitlab.broker.shared.cache.AssignmentsCache
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import java.util.*

@SpringBootTest(properties = ["application.runner.enabled=false"])
@AutoConfigureWireMock(port = 6666)
class RepositorySchedulerTest {

    @Autowired
    private lateinit var repositoryScheduler: RepositoryScheduler

    @Test
    fun `should scheduler worked`() {
        WireMock.resetAllRequests()
        AssignmentsCache.deleteAll()
        AssignmentsCache.addAll(
            listOf(
                GitLabAssignment(
                    UUID.randomUUID(),
                    UUID.fromString("38718fc9-d106-47a5-a25c-e4e595c8c2d4"),
                    UUID.fromString("a7a74e83-dde9-48a0-8b0d-c74f954671fb"),
                    GitLabConfiguration("gitlab"),
                ),
            ),
        )

        repositoryScheduler.getAllRepositories()

        WireMock.verify(1, WireMock.postRequestedFor(WireMock.urlEqualTo("/services/bulk")))
        WireMock.verify(6, WireMock.postRequestedFor(WireMock.urlEqualTo("/dora")))
        WireMock.verify(1, WireMock.postRequestedFor(WireMock.urlEqualTo("/commands")))
    }
}
