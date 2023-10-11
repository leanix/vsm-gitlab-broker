# vsm-gitlab-broker

>💡 **Early Access** <br>
This integration is currently in early access. To find more information about the release stages of our integrations, see [Release Stages](https://docs-vsm.leanix.net/docs/release-stages).

VSM GitLab Broker is used to establish the communication between VSM SaaS Application and GitLab Enterprise 
on premise deployments that are not publicly accessible from the internet.


<h2 align="center">Table of Contents </h2>

1. [Usage](#usage)
   1. [Personal Access Token](#personal-access-token)
   2. [Command-line arguments](#command-line-arguments)
2. [Troubleshooting](#troubleshooting)
   1. [Using a Proxy](#using-over-a-http-proxy-system)
   2. [Using with M1 chips](#using-amd64-images-on-apple-m1)
3. [Release Process](#release-process)
4. [Broker Architecture](#broker-architecture)

---


## Usage

> ⚠️ The current setup only allows for one running container replica. To prevent any unintended data interferences in your workspace, please only run a single instance of the container at any point.

The VSM GitLab Broker is published as a Docker image. The configuration is performed with environment variables as
described below.

To use the Broker client with a GitLab Enterprise deployment, run `docker pull leanixacrpublic.azurecr.io/vsm-gitlab-broker` tag. The following environment variables are mandatory to configure the Broker client:

- `LEANIX_DOMAIN` - the LeanIX domain, obtained from your LeanIX url (example if your workspace is located at `https://my-company.leanix.net` then the domain is `my-company`).
- `LEANIX_API_TOKEN` - the LeanIX token, obtained from your admin panel. :warning: Make sure the api token has `ADMIN`rights.
- `GITLAB_TOKEN` - a [personal access token](#personal-access-token) with `api` scope.
- `GITLAB_URL` - the hostname of your GitLab deployment, such as `https://gl.domain.com`. This must include the protocol of the GitLab deployment (http vs https), default is `http`.
- `GITLAB_WEBHOOK_URL` - public endpoint which resolves to gitlab-on-prem-broker. When not set, the broker won't place any webhooks.

## Webhook mode (recommended)
The `GITLAB_WEBHOOK_URL` is the callback URL for webhook events sent from the GitLab instance, it is the host address where the agent will be reachable in your network. Please make sure 
### Personal Access Token 
The PAT token requires the **ADMIN role** (i.e. the person creating the PAT token is admin), as we will register system hooks to allow us to listen to repo changes.

The following scopes are required for the webhook mode:
Gitlab Scope  | VSM Usage
------------- | -------------
`api`    | To read repository data and manage the webhook on system-level
`read_user`    | We validate the PAT token to come from an ADMIN to be able to create webhooks for all selected groups (e.g. we check `"is_admin":true` in this [endpoint](https://docs.gitlab.com/ee/api/users.html#for-administrators-free-self-2))

## Without Webhooks (Scheduled)
You can disable webhooks by leaving `GITLAB_WEBHOOK_URL` empty.
In this mode the agent will only provide a (1x day) daily update of data to VSM.  Hence we encourage you to switch to the webhook-based setup eventually for production

### Personal Access Token 
The PAT token requires at least **XXX role**.
The following scopes are required for the scheduled mode:
Gitlab Scope  | VSM Usage
------------- | -------------
`api`    | To read repository data and manage the webhook on system-level
`read_user`    | We validate the PAT token to come from an ADMIN to be able to create webhooks for all selected groups (e.g. we check `"is_admin":true` in this [endpoint](https://docs.gitlab.com/ee/api/users.html#for-administrators-free-self-2))

### Switching from Scheduled Mode to Webhook mode
For trialing the GitLab Integration you might start by a less permissive scheduled mode. For production rollout you might then want to reap the benefits from real-time updates i.e. webhooks. Below is what you'll need to do to transform your schedule-based setup to webhook-based.

1. Stop the container running the schedule based configuration 
2. Running the same docker command as under #1, but adding a valid `GITLAB_WEBHOOK_URL` 
example:
```
docker run --pull=always --restart=always \
           -p 8080:8080 \
           -e LEANIX_DOMAIN=<region>.leanix.net \
           -e LEANIX_TECHNICAL_USER_TOKEN=<technical_user-token>\
           -e GITLAB_TOKEN=<secret-gitlab-token> \
           -e GITLAB_URL=<GitLab base URL(https://gl.domain.com)> \
           -e GITLAB_WEBHOOK_URL= https://acme.vsm-gitlab-broker:7000 \
        leanixacrpublic.azurecr.io/vsm-gitlab-broker
```



> ℹ️ **[Group Access Tokens](https://docs.gitlab.com/ee/user/group/settings/group_access_tokens.html)** <br>
Today we do not support [Group Access tokens](https://docs.gitlab.com/ee/user/group/settings/group_access_tokens.html). So the only way to set up the integration is via a PAT token as described above. Should you see the need for Group Access Token, feel free to reach out with your use case.


### Command-line arguments

You can run the docker container by providing the relevant configuration:

```console
docker run --pull=always --restart=always \
           -p 8080:8080 \
           -e LEANIX_DOMAIN=<region>.leanix.net \
           -e LEANIX_TECHNICAL_USER_TOKEN=<technical_user-token>\
           -e GITLAB_TOKEN=<secret-gitlab-token> \
           -e GITLAB_URL=<GitLab base URL(https://gl.domain.com)> \
           -e GITLAB_WEBHOOK_URL=<GitLab Broker URL> \
        leanixacrpublic.azurecr.io/vsm-gitlab-broker
```

### Troubleshooting

#### Using over a http proxy system

Add the following properties on the command:

```console
docker run 
           ...
           -e JAVA_OPTS="-Dhttp.proxyHost=<HTTP_HOST> -Dhttp.proxyPort=<HTTP_PORT> -Dhttp.proxyUser=<PROXY_USER> -Dhttp.proxyPassword=<PROXY_PASS> -Dhttps.proxyHost=<HTTPS_HOST> -Dhttps.proxyPort=<HTTPS_PORT> -Dhttps.proxyUser=<PROXY_USER> -Dhttps.proxyPassword=<PROXY_PASS>" \
        leanixacrpublic.azurecr.io/vsm-gitlab-broker
```

#### Using amd64 images on Apple M1

Just run the container by providing the following command:

```console

docker run --platform linux/amd64 \
           ...
        leanixacrpublic.azurecr.io/vsm-gitlab-broker
```

## Release Process
In order to provide a excellent experience with the agent, we are using a three-pronged release process. Any change we undertake can be classified into one of the three categories:
1. **Major**
   These are releases that change the brokers behavior fundamentally or are significant feature addition. Examples could be supporting a new domain API GitLab released. As per SemVer nomenclature these wil bump the version like so `v1.0.0` > `v2.0.0`.
2. **Minor**
   These are releases that add non-breaking feature increments. Examples could be: adding new API calls to fetch further data for use in VSM. As per SemVer nomenclature these wil bump the version like so `v1.0.0` > `v1.1.0`.

3. **Patch**
   These are releases that entail hotfixes, non-breaking updates to underlying libraries. As per SemVer nomenclature these wil bump the version like so `v1.0.0` > `v1.0.1`.

With every new release you will find the details of what the release entails in the [releases tab](https://github.com/leanix/vsm-gitlab-broker/releases).

Should there be any open questions feel free to open an [issue](https://github.com/leanix/vsm-gitlab-broker/issues) 📮

## Broker Architecture
1. The integration (vsm-gitlab-broker) is packaged as a docker container which shall be deployed on the customer premises

2. The container runs a live service, which runs continuously
   - it exposes a health endpoint on path `/actuator/health` which can be used to check the health of the service

3. On startup:
   - the service will reach out to VSM to fetch the configured GitLab group

   - the service will then call the GitLab instance to fetch relevant GitLab data

   - the service will on startup place webhooks on the GitLab instance to listen to events emitted from in-scope GitLab groups to update VSM.

   - the service ensures that there will always only be one webhook registered

4. At runtime:
   - as stated under 3) the service will listen to webhook events after the initial setup

   - to account for any intermittent interruptions (e.g. network issues, docker container failure etc.) between the agent and the GitLab instance, the service will do a full scan every week to ensure eventual consistency in VSM