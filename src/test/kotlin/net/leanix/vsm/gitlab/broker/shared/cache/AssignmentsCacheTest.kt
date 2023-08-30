package net.leanix.vsm.gitlab.broker.shared.cache

import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabConfiguration
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID.randomUUID

class AssignmentsCacheTest {

    @BeforeEach
    fun refreshCache() {
        AssignmentsCache.deleteAll()
        AssignmentsCache.addAll(
            listOf(
                GitLabAssignment(randomUUID(), randomUUID(), randomUUID(), GitLabConfiguration("finance/special-ops")),
            )
        )
    }

    @Test
    fun `should return GitlabAssignment when namespace = orgName`() {
        val result = AssignmentsCache.get("finance/special-ops")
        assertAll(
            { assertNotNull(result) },
            { assertEquals("finance/special-ops", result!!.connectorConfiguration.orgName) }
        )
    }

    @Test
    fun `should return GitlabAssignment when  orgName starts with namespace`() {
        val result = AssignmentsCache.get("finance/special-ops/onlineshop")
        assertAll(
            { assertNotNull(result) },
            { assertEquals("finance/special-ops", result!!.connectorConfiguration.orgName) }
        )
    }

    @Test
    fun `should return GitlabAssignment when  orgName starts with namespace plus subgroup`() {
        val result = AssignmentsCache.get("finance/special-ops/elite-ops/elite-spring")
        assertAll(
            { assertNotNull(result) },
            { assertEquals("finance/special-ops", result!!.connectorConfiguration.orgName) }
        )
    }

    @Test
    fun `should return null when orgName does not namespace subgroup`() {
        val result = AssignmentsCache.get("finance")
        assertNull(result)
    }
}
