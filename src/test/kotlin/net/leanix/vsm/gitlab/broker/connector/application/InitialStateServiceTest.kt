package net.leanix.vsm.gitlab.broker.connector.application

import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verifyOrder
import net.leanix.vsm.gitlab.broker.connector.domain.CommandEventAction
import net.leanix.vsm.gitlab.broker.connector.domain.CommandProvider
import net.leanix.vsm.gitlab.broker.connector.shared.getGitlabAssignment
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InitialStateServiceTest {

    private val repositoryService = mockk<RepositoryService>()
    private val commandProvider = mockk<CommandProvider>()
    private val initialStateService = spyk<InitialStateService>(InitialStateService(repositoryService, commandProvider))

    @BeforeEach
    fun setup() {
        every { initialStateService.logFailedStatus(any(), any()) } returns Unit
        every { commandProvider.sendCommand(any(), any()) } returns Unit
    }

    @Test
    fun `should send correct command for successful and failed assignments`() {
        val assignmentToSucceed = getGitlabAssignment()
        val assignmentToFail = getGitlabAssignment()

        every { repositoryService.importAllRepositories(assignmentToFail) } throws Exception("some error")
        every { repositoryService.importAllRepositories(assignmentToSucceed) } returns Unit

        initialStateService.initState(listOf(assignmentToSucceed, assignmentToFail))

        verifyOrder {
            repositoryService.importAllRepositories(assignmentToSucceed)
            repositoryService.importAllRepositories(assignmentToFail)

            commandProvider.sendCommand(assignmentToSucceed, CommandEventAction.FINISHED)
            commandProvider.sendCommand(assignmentToFail, CommandEventAction.FAILED)
        }
    }
}
