package net.leanix.vsm.gitlab.broker.connector.application

import feign.FeignException
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.GitlabClientProvider
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.shared.exception.AccessLevelValidationFailed
import net.leanix.vsm.gitlab.broker.shared.exception.InvalidToken
import net.leanix.vsm.gitlab.broker.shared.exception.OrgNameValidationFailed
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ValidationService(
    private val gitlabClientProvider: GitlabClientProvider,
    @Value("\${leanix.gitlab.webhook-url}") private val gitlabWebhookUrl: String,
) : BaseConnectorService() {

    fun validateConfiguration(gitLabAssignment: GitLabAssignment) {
        val orgName = gitLabAssignment.connectorConfiguration.orgName
        runCatching {
            if (gitlabWebhookUrl.isNotBlank()) {
                validateUserAccess()
            }
            validateGroupPath(orgName)
        }.onSuccess {
            logInfoMessages("vsm.configuration.validation.successful", arrayOf(orgName), gitLabAssignment)
        }.onFailure { exception ->
            handleExceptions(exception, orgName, gitLabAssignment)
        }
    }

    private fun validateUserAccess() {
        if (gitlabClientProvider.getCurrentUser().isAdmin != true) throw AccessLevelValidationFailed()
    }

    private fun validateGroupPath(fullPath: String) {
        if (gitlabClientProvider.getGroupByFullPath(fullPath) == null) throw OrgNameValidationFailed(fullPath)
    }

    private fun handleExceptions(
        exception: Throwable,
        orgName: String,
        gitLabAssignment: GitLabAssignment
    ) {
        when (exception) {
            is InvalidToken -> {
                logFailedMessages("vsm.configuration.invalid_token", arrayOf(orgName), gitLabAssignment)
            }

            is AccessLevelValidationFailed -> {
                logFailedMessages("vsm.configuration.access_level", arrayOf(orgName), gitLabAssignment)
            }

            is OrgNameValidationFailed -> {
                logFailedMessages("vsm.configuration.invalid_org_name", arrayOf(orgName), gitLabAssignment)
                logIntegrationConfigError("orgNames", "Invalid organization name: $orgName", gitLabAssignment)
            }

            is FeignException -> {
                logFailedMessages("vsm.configuration.validation.failed", arrayOf(orgName), gitLabAssignment)
            }
        }
        logFailedStatus(exception.message, gitLabAssignment)
        throw exception
    }
}
