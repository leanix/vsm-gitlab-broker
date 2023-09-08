# vsm-gitlab-broker
VSM GitLab Broker is used to establish the communication between VSM SaaS Application and GitLab Enterprise 
on premise deployments that are not publicly accessible from the internet.


<h2 align="center">Table of Contents </h2>

1. [Usage](#usage)
    1. [Personal Access Token](#personal-access-token)
    2. [Command-line arguments](#command-line-arguments)
3. [Broker Architecture](#broker-architecture)

---


## Usage

The VSM GitLab Broker is published as a Docker image. The configuration is performed with environment variables as
described below.

To use the Broker client with a GitLab Enterprise deployment, run `docker pull leanixacrpublic.azurecr.io/vsm-gitlab-broker` tag. The following environment variables are mandatory to configure the Broker client:

- `LEANIX_DOMAIN` - the LeanIX domain, obtained from your LeanIX url (example if your workspace is located at `https://my-company.leanix.net` then the domain is `my-company`).
- `LEANIX_API_TOKEN` - the LeanIX token, obtained from your admin panel. :warning: Make sure the api token has `ADMIN`rights.
- `GITLAB_TOKEN` - a [personal access token](#personal-access-token) with `api` scope.
- `GITLAB_URL` - the hostname of your GitLab deployment, such as `https://gl.domain.com`. This must include the protocol of the GitLab deployment (http vs https), default is `http`.
- `GITLAB_WEBHOOK_CREATION` - a boolean switch to turn on the webhook capability of the broker. When set to false, the broker won't place any webhook and will just run on a 1x day schedule. Default: `false`.
- `GITLAB_WEBHOOK_URL` - public endpoint which resolves to gitlab-on-prem-broker.

### Personal Access Token
As part of the setup the vsm-broker requires a personal access token (PAT) with according rights to run effectively. For more details on how to create the PAT, see [GitLab's documentation](https://docs.gitlab.com/16.1/ee/user/profile/personal_access_tokens.html#personal-access-token-scopes).
It is important to note that the PAT must belong to a user with admin access as this is necessary for the broker to create webhooks.

The following scopes are required:
Gitlab Scope  | VSM Usage
------------- | -------------
`api`    | To read repository data and manage the webhook on system-level
`read_user`    | To verify the current user's permissions

### Command-line arguments

You can run the docker container by providing the relevant configuration:

```console
docker run --pull=always --restart=always \
           -p 8080:8080 \
           -e LEANIX_DOMAIN=<region>.leanix.net \
           -e LEANIX_TECHNICAL_USER_TOKEN=<technical_user-token>\
           -e GITLAB_TOKEN=<secret-gitlab-token> \
           -e GITLAB_URL=<GitLab Ent URL(https://gl.domain.com)> \
           -e GITLAB_WEBHOOK_CREATION=<true or false> \
           -e GITLAB_WEBHOOK_URL=<GitLab Broker URL> \
        leanixacrpublic.azurecr.io/vsm-gitlab-broker
```

## Broker Architecture