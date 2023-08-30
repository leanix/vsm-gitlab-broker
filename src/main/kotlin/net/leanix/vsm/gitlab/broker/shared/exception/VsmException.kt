package net.leanix.vsm.gitlab.broker.shared.exception

sealed class VsmException(message: String? = null) : RuntimeException(message)

class NoRepositoriesFound : VsmException()

class GraphqlException(message: String?) : VsmException(message)

class InvalidToken : VsmException()

class AccessLevelValidationFailed : VsmException()

class OrgNameValidationFailed : VsmException()

class GitlabTokenException(token: String?) : VsmException(message = "Invalid gitlab payload token: $token")

class GitlabPayloadNotSupportedException :
    VsmException(message = "Payload is neither for project creation nor for merge request being merged")

class NamespaceNotFoundInCacheException(namespace: String) :
    VsmException(message = "Namespace '$namespace' not found on AssignmentCache")
