package net.leanix.vsm.gitlab.broker.connector.runner

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import org.awaitility.kotlin.await
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock

@SpringBootTest(properties = ["application.runner.enabled=true"])
@AutoConfigureWireMock(port = 6666)
class InitialStateRunnerTest {

    @Test
    fun `it should get the assignments`() {
        await.untilAsserted {
            WireMock.verify(
                1,
                getRequestedFor(
                    urlEqualTo(
                        "/gitlab-on-prem/assignments"
                    )
                )
            )

            WireMock.verify(
                    2,
                    WireMock.postRequestedFor(urlEqualTo("/api/graphql"))
                            .withRequestBody(WireMock.containing("AllGroupsQuery"))

            )
        }
    }
}
