# vsm-gitlab-broker
VSM GitLab Broker is used to establish the communication between VSM SaaS Application and GitLab Enterprise 
on premise deployments that are not publicly accessible from the internet.


## Usage

The VSM GitLab Broker is published as a Docker image. The configuration is performed with environment variables as
described below.

To use the Broker client with a GitLab Enterprise deployment, run `docker pull leanixacrpublic.azurecr.io/vsm-gitlab-broker` tag. The following environment variables are mandatory to configure the Broker client:

- `LEANIX_DOMAIN` - the LeanIX domain, obtained from your LeanIX url (example if your workspace is located at `https://my-company.leanix.net` then the domain is `my-company`).
- `LEANIX_API_TOKEN` - the LeanIX token, obtained from your admin panel. :warning: Make sure the api token has `ADMIN`rights.


#### Command-line arguments

You can run the docker container by providing the relevant configuration:

```console
docker run --pull=always --restart=always \
           -p 8080:8080 \
           -e LEANIX_DOMAIN=<region>.leanix.net \
           -e LEANIX_API_TOKEN=<technical_user-token>\
        leanixacrpublic.azurecr.io/vsm-gitlab-broker
```