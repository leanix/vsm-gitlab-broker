package net.leanix.vsm.gitlab.broker.shared.exception

sealed class VsmException(message: String? = null) : RuntimeException(message) {

    class InvalidToken : VsmException()

    class AccessLevelValidationFailed : VsmException()

    class OrgNameValidationFailed : VsmException()
}
