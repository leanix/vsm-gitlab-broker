package net.leanix.vsm.gitlab.broker.connector.scheduler

import net.leanix.vsm.gitlab.broker.connector.adapter.feign.VsmClient
import net.leanix.vsm.gitlab.broker.connector.application.AssignmentService
import net.leanix.vsm.gitlab.broker.connector.application.InitialStateService
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabConfiguration
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabHeartbeatResponse
import net.leanix.vsm.gitlab.broker.connector.domain.RunProvider
import net.leanix.vsm.gitlab.broker.shared.cache.AssignmentsCache
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import java.util.UUID
import java.util.UUID.randomUUID

class HeartbeatSchedulerTest {

    private val vsmClient = mock(VsmClient::class.java)
    private val assignmentService = mock(AssignmentService::class.java)
    private val initialStateService = mock(InitialStateService::class.java)
    private val runProvider = mock(RunProvider::class.java)
    private val subject = HeartbeatScheduler(vsmClient, assignmentService, initialStateService, runProvider)
    private val runId: UUID = randomUUID()

    @BeforeEach
    fun setupAssignmentCache() {
        AssignmentsCache.deleteAll()
        AssignmentsCache.addAll(listOf(getGitlabAssignment()))
    }

    @Test
    fun `should re-fetch assignments when new config available`() {
        `when`(vsmClient.heartbeat(runId.toString())).thenReturn(GitLabHeartbeatResponse("OK", true))

        subject.heartbeat()

        verify(assignmentService).getAssignments()
    }

    @Test
    fun `should not re-fetch assignments when no new config available`() {
        `when`(vsmClient.heartbeat(runId.toString())).thenReturn(GitLabHeartbeatResponse("OK", false))

        subject.heartbeat()

        verifyNoInteractions(assignmentService)
    }

    private fun getGitlabAssignment() = GitLabAssignment(runId, randomUUID(), randomUUID(), GitLabConfiguration(""))
}
