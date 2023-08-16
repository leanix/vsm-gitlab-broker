package net.leanix.vsm.gitlab.broker.shared.auth.application

import net.leanix.vsm.gitlab.broker.shared.Constants.API_USER
import net.leanix.vsm.gitlab.broker.shared.auth.adapter.feign.AuthClient
import net.leanix.vsm.gitlab.broker.shared.properties.GitLabOnPremProperties
import org.springframework.stereotype.Service
import java.util.*

@Service
class GetBearerToken(
    private val authClient: AuthClient,
    private val vsmProperties: GitLabOnPremProperties
) {

    operator fun invoke(): String {
        return authClient.getToken(
            authorization = getBasicAuthHeader(),
            body = "grant_type=client_credentials"
        ).accessToken
    }

    private fun getBasicAuthHeader(): String =
        "Basic " + Base64.getEncoder().encodeToString(
            "$API_USER:${vsmProperties.apiUserToken}".toByteArray()
        )
}
