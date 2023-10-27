package net.leanix.vsm.gitlab.broker.connector.application

import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verifyOrder
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.GitlabClientProvider
import net.leanix.vsm.gitlab.broker.connector.domain.CommandEventAction
import net.leanix.vsm.gitlab.broker.connector.domain.CommandProvider
import net.leanix.vsm.gitlab.broker.connector.shared.getGitlabAssignment
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class InitialStateServiceTest {

    private val repositoryService = mockk<RepositoryService>()
    private val commandProvider = mockk<CommandProvider>()
    private val doraService = mockk<DoraService>()
    private val validationService = mockk<ValidationService>()
    private val gitlabClientProvider = mockk<GitlabClientProvider>()
    private val initialStateService =
        spyk<InitialStateService>(
            InitialStateService(
                repositoryService,
                commandProvider,
                doraService,
                validationService,
                gitlabClientProvider
            )
        )

    @BeforeEach
    fun setup() {
        every { initialStateService.logFailedStatus(any(), any()) } returns Unit
        every { initialStateService.exit() } returns Unit
        every { commandProvider.sendCommand(any(), any()) } returns Unit
        every { doraService.generateDoraEvents(any(), any()) } returns Unit
        every { validationService.validateConfiguration(any()) } returns Unit
        every { gitlabClientProvider.getVersion() } returns "15.0.0"
    }

    @Test
    fun `should send correct command for successful and failed assignments`() {
        val assignmentToSucceed = getGitlabAssignment()
        val assignmentToFail = getGitlabAssignment()
        val repository = getRepository(assignmentToSucceed.connectorConfiguration.orgName)

        every { repositoryService.importAllRepositories(assignmentToFail) } throws Exception("some error")
        every { repositoryService.importAllRepositories(assignmentToSucceed) } returns listOf(repository)

        initialStateService.initState(listOf(assignmentToSucceed, assignmentToFail))

        verifyOrder {
            repositoryService.importAllRepositories(assignmentToSucceed)
            doraService.generateDoraEvents(repository, assignmentToSucceed)
            repositoryService.importAllRepositories(assignmentToFail)

            commandProvider.sendCommand(assignmentToSucceed, CommandEventAction.FINISHED)
            commandProvider.sendCommand(assignmentToFail, CommandEventAction.FAILED)
        }
    }

    @Test
    fun `should exit application if gitlab version less than 15`() {
        every { gitlabClientProvider.getVersion() } returns "14.2.1-ee"

        val assignment = getGitlabAssignment()
        initialStateService.initState(listOf(assignment))

        verifyOrder {
            initialStateService.logFailedStatus(
                eq(
                    "GitLab version 14.2.1-ee is not supported. Version 15.0 and onwards are supported. " +
                        "Broker will shut down now."
                ),
                assignment
            )
            initialStateService.exit()
        }
    }
}
