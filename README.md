# vsm-gitlab-broker
VSM GitLab Broker is used to establish the communication between VSM SaaS Application and GitLab Enterprise 
on premise deployments that are not publicly accessible from the internet.


## Usage

The VSM GitLab Broker is published as a Docker image. The configuration is performed with environment variables as
described below.

To use the Broker client with a GitLab Enterprise deployment, run `docker pull leanixacrpublic.azurecr.io/vsm-gitlab-broker` tag. The following environment variables are mandatory to configure the Broker client:

- `LEANIX_DOMAIN` - the LeanIX domain, obtained from your LeanIX url (example if your workspace is located at `https://my-company.leanix.net` then the domain is `my-company`).
- `LEANIX_API_TOKEN` - the LeanIX token, obtained from your admin panel. :warning: Make sure the api token has `ADMIN`rights.
- `GITLAB_TOKEN` - a [personal access token](#personal-access-token) with `api` scope.
- `GITLAB_URL` - the hostname of your GitLab deployment, such as `https://ghe.domain.com`. This must include the protocol (http vs https) of the GitLab deployment.

### Personal Access Token
As part of the setup the vsm-broker requires a personal access token (PAT) with according rights to run effectively. For more details on how to create the PAT, see [GitLab's documentation](https://docs.gitlab.com/16.1/ee/user/profile/personal_access_tokens.html#personal-access-token-scopes).

The following scopes are required:
Gitlab Scope  | VSM Usage
------------- | -------------
`api`    | To read repository data and manage the webhook on system-level

#### Command-line arguments

You can run the docker container by providing the relevant configuration:

```console
docker run --pull=always --restart=always \
           -p 8080:8080 \
           -e LEANIX_DOMAIN=<region>.leanix.net \
           -e LEANIX_TECHNICAL_USER_TOKEN=<technical_user-token>\
           -e GITLAB_TOKEN=<secret-gitlab-token> \
           -e GITLAB_URL=<GitHub Ent URL(https://ghe.domain.com)> \
        leanixacrpublic.azurecr.io/vsm-gitlab-broker
```