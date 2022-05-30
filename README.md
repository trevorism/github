# github 
![Jenkins](https://img.shields.io/jenkins/build/http/trevorism-build.eastus.cloudapp.azure.com/github)
![Jenkins Coverage](https://img.shields.io/jenkins/coverage/jacoco/http/trevorism-build.eastus.cloudapp.azure.com/github)
![GitHub last commit](https://img.shields.io/github/last-commit/trevorism/github)
![GitHub language count](https://img.shields.io/github/languages/count/trevorism/github)
![GitHub top language](https://img.shields.io/github/languages/top/trevorism/github)

A webapp that wraps the github API. This helps with automated creation and management of github repos

Current version: 0.2.3

Deployed to [Github](http://github.project.trevorism.com)

Uses a personal access token to authenticate. Create a property in secrets.properties
```properties
clientId=...
clientSecret=...
accessToken=<personal access token>
```