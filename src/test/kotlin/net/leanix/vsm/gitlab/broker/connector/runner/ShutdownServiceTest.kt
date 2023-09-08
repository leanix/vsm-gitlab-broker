package net.leanix.vsm.gitlab.broker.connector.runner

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.data.RunState
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabConfiguration
import net.leanix.vsm.gitlab.broker.connector.domain.RunProvider
import net.leanix.vsm.gitlab.broker.shared.cache.AssignmentsCache
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ShutdownServiceTest {

    private val runProvider: RunProvider = mockk(relaxed = true)
    private val shutdownService = ShutdownService(runProvider)

    @BeforeEach
    fun setup() {
        clearAllMocks()
        AssignmentsCache.deleteAll()
    }

    @Test
    fun `test onDestroy with empty assignment cache`() {
        shutdownService.onDestroy()

        // Assert no interactions with the run status provider
        verify(exactly = 0) { runProvider.updateRun(any(), any(), any()) }
    }

    @Test
    fun `test onDestroy with assignments`() {
        val assignment = GitLabAssignment(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            GitLabConfiguration(
                orgName = "orgName"
            )
        )
        AssignmentsCache.addAll(listOf(assignment))

//        val runStateSlot = slot<UpdateRunStateRequest>()

        every { runProvider.updateRun(any(), any(), any()) } answers { }

        shutdownService.onDestroy()

        verify {
            runProvider.updateRun(
                eq(assignment),
                eq(RunState.FINISHED),
                eq("gracefully stopped GitLab broker")
            )
        }
    }
}
