package com.trevorism.gcloud.webapi.controller

import com.trevorism.gcloud.model.Repository
import com.trevorism.gcloud.model.SecretRequest
import com.trevorism.gcloud.model.WorkflowRequest
import com.trevorism.gcloud.model.WorkflowResponse
import com.trevorism.gcloud.model.WorkflowStatus
import com.trevorism.gcloud.service.GithubService
import com.trevorism.secure.Roles
import com.trevorism.secure.Secure

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.inject.Inject

@Controller("/repo")
class RepoController {

    @Inject
    private GithubService githubService

    @Tag(name = "Repo Operations")
    @Operation(summary = "Lists all repos **Secure")
    @Get(value = "/", produces = MediaType.APPLICATION_JSON)
    @Secure(Roles.SYSTEM)
    List<Repository> list() {
        githubService.listRepos()
    }

    @Tag(name = "Repo Operations")
    @Operation(summary = "Creates a new repo **Secure")
    @Post(value = "/", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    @Secure(Roles.SYSTEM)
    Repository createRepository(@Body Repository repository) {
        githubService.createRepo(repository)
    }

    @Tag(name = "Repo Operations")
    @Operation(summary = "Gets a repository **Secure")
    @Get(value = "/{name}", produces = MediaType.APPLICATION_JSON)
    @Secure(Roles.SYSTEM)
    Repository getRepository(String name) {
        githubService.getRepo(name)
    }

    @Tag(name = "Repo Operations")
    @Operation(summary = "Deletes a repository **Secure")
    @Secure(Roles.ADMIN)
    @Delete(value = "{name}", produces = MediaType.APPLICATION_JSON)
    boolean deleteRepository(String name) {
        githubService.deleteRepo(name)
    }

    @Tag(name = "Repo Operations")
    @Operation(summary = "Invoke github workflow **Secure")
    @Post(value = "/{name}/workflow", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    @Secure(value = Roles.SYSTEM, allowInternal = true)
    WorkflowResponse invokeWorkflow(String name, @Body WorkflowRequest request) {
        githubService.invokeWorkflow(name, request)
        return new WorkflowResponse("/${name}/workflow/${request.yamlName}")
    }

    @Tag(name = "Repo Operations")
    @Operation(summary = "Get github workflow status **Secure")
    @Get(value = "/{name}/workflow/{yaml}", produces = MediaType.APPLICATION_JSON)
    @Secure(value = Roles.SYSTEM, allowInternal = true)
    WorkflowStatus getWorkflowStatus(String name, String yaml) {
        githubService.getWorkflowStatus(name, yaml)
    }

    @Tag(name = "Repo Operations")
    @Operation(summary = "Rerun the last github action **Secure")
    @Put(value = "/{name}", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    @Secure(Roles.SYSTEM)
    boolean runlastGithubAction(String name) {
        githubService.rerunLastGithubAction(name)
    }

    @Tag(name = "Repo Operations")
    @Operation(summary = "Create or update a secret **Secure")
    @Put(value = "/{name}/secret", produces = MediaType.APPLICATION_JSON, consumes = MediaType.APPLICATION_JSON)
    @Secure(Roles.SYSTEM)
    String setGithubSecret(String name, @Body SecretRequest request) {
        boolean result = githubService.setGithubSecret(name, request.secretName, request.secretValue)
        if(result){
            return name
        }
        else{
            throw new RuntimeException("Unable to set secret named: ${name}")
        }
    }

    @Tag(name = "Repo Operations")
    @Operation(summary = "Get latest release for the current repo")
    @Get(value = "/{name}/release", produces = MediaType.TEXT_PLAIN)
    String getLatestRelease(String name) {
        githubService.getLatestRelease(name)
    }

}
