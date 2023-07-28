---
title: GitLab Enterprise
excerpt: Out-of-the-box Source Code Repository Integration
category: 62b439a44f5a3e003566e740
---

## Introduction

> :bulb:**Early Access **
>
> This integration is in early access. To find more information about the release stages of our integrations, see [Release Stages](https://docs-vsm.leanix.net/docs/release-stages).

The LeanIX VSM GitLab Repository integration offers an easy way to auto-discover all your services from your on-premise GitLab Enterprise instance. Based on this VSM's mapping inbox allows you to easily sift through all the stale information from GitLab to decide, which services are really useful to your organization and hence should be part of your service catalog. This will help you to maintain a high standard of data quality when you subsequently map your services to their individual teams to create clear team ownership.

![](https://files.readme.io/539f1a5-image.png)

##### Integrate with GitLab Enterprise to:

- Automatically discover your services to build your company-wide service catalog
- Map team ownership to have clear software governance in place
- Automatically get Change & Release events for your DORA metrics

## Setup

The integration runs as a dockerized agent to continuously fetch your GitLab data and pass it into VSM. See the technical details on the [project's page.](https://github.com/leanix/vsm-gitlab-broker).


[block:embed]
{
"html": false,
"url": "https://github.com/leanix/vsm-gitlab-broker",
"title": "VSM GitLab Broker",
"favicon": "https://cdn.icon-icons.com/icons2/2415/PNG/512/gitlab_original_logo_icon_146503.png",
"image": "https://cdn.icon-icons.com/icons2/2415/PNG/512/gitlab_original_logo_icon_146503.png",
"provider": "gitlab.com",
"href": "https://github.com/leanix/vsm-gitlab-broker"
}
[/block]





