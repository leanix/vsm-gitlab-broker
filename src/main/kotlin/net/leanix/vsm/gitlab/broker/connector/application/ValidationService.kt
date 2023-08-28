package net.leanix.vsm.gitlab.broker.connector.application

import feign.FeignException
import net.leanix.vsm.gitlab.broker.connector.adapter.feign.GitlabClientProvider
import net.leanix.vsm.gitlab.broker.connector.domain.GitLabAssignment
import net.leanix.vsm.gitlab.broker.shared.exception.VsmException
import org.springframework.stereotype.Component
import java.net.URLEncoder

@Component
class ValidationService(
    private val gitlabClientProvider: GitlabClientProvider
) : BaseConnectorService() {

    fun validateConfiguration(gitLabAssignment: GitLabAssignment) {
        val orgName = gitLabAssignment.connectorConfiguration.orgName
        runCatching {
            validateUserAccess()
            validateOrgName(gitLabAssignment.connectorConfiguration.orgName)
        }.onSuccess {
            logInfoMessages("vsm.configuration.validation.successful", arrayOf(orgName), gitLabAssignment)
        }.onFailure { exception ->
            handleExceptions(exception, orgName, gitLabAssignment)
        }
    }

    private fun validateUserAccess() {
        run {
            val user = gitlabClientProvider.getCurrentUser()
            if (user.isAdmin != true) throw VsmException.AccessLevelValidationFailed()
        }
    }

    private fun validateOrgName(orgName: String) {
        runCatching {
            gitlabClientProvider.getOrg(URLEncoder.encode(orgName, "UTF-8"))
        }.onFailure {
            throw VsmException.OrgNameValidationFailed()
        }
    }

    private fun handleExceptions(
        exception: Throwable,
        orgName: String,
        gitLabAssignment: GitLabAssignment
    ) {
        when (exception) {
            is VsmException.InvalidToken -> {
                logFailedMessages("vsm.configuration.invalid_token", arrayOf(orgName), gitLabAssignment)
            }

            is VsmException.AccessLevelValidationFailed -> {
                logFailedMessages("vsm.configuration.access_level", arrayOf(orgName), gitLabAssignment)
            }

            is VsmException.OrgNameValidationFailed -> {
                logFailedMessages("vsm.configuration.invalid_org_name", arrayOf(orgName), gitLabAssignment)
            }

            is FeignException -> {
                logFailedMessages("vsm.configuration.validation.failed", arrayOf(orgName), gitLabAssignment)
            }
        }
        logFailedStatus(exception.message, gitLabAssignment.runId)
        throw exception
    }
}