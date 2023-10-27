package net.leanix.vsm.gitlab.broker.shared.exception

sealed class VsmException(message: String? = null) : RuntimeException(message)

class NoRepositoriesFound : VsmException()

class GraphqlException(message: String?) : VsmException(message)

class InvalidToken : VsmException()

class AccessLevelValidationFailed : VsmException()

class GroupNameValidationFailed(groupName: String) : VsmException("Invalid group name: $groupName")

class GitlabTokenException(token: String?) : VsmException(message = "Invalid gitlab payload token: $token")

class GitlabPayloadNotSupportedException :
    VsmException(message = "Payload is neither for project creation nor for merge request being merged")

class NamespaceNotMatchException(namespace: String) :
    VsmException(message = "Namespace '$namespace' does not match any group in AssignmentCache")

class GitlabVersionNotSupportedException(gitlabVersion: String) :
    VsmException(
        message = "GitLab version $gitlabVersion is not supported. Version 15.0 and onwards are supported. " +
                "Broker will shut down now."
    )
