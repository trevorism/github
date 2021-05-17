package com.trevorism.gcloud.webapi.controller

import com.trevorism.gcloud.model.Repository
import com.trevorism.gcloud.service.DefaultGithubService
import com.trevorism.gcloud.service.GithubService
import com.trevorism.secure.Roles
import com.trevorism.secure.Secure
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation

import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Api("Repo Operations")
@Path("repo")
class RepoController {

    private GithubService githubService = new DefaultGithubService()

    @ApiOperation(value = "Lists all repos **Secure")
    @Secure(Roles.USER)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<Repository> list() {
        githubService.listRepos()
    }

    @ApiOperation(value = "Creates a new repo **Secure")
    @POST
    @Secure(Roles.SYSTEM)
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    Repository createRepository(Repository repository) {
        githubService.createRepo(repository)
    }

    @ApiOperation(value = "Deletes a repository **Secure")
    @Secure(Roles.USER)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{name}")
    Repository getRepository(@PathParam("name") String name) {
        githubService.getRepo(name)
    }

    @ApiOperation(value = "Deletes a repository **Secure")
    @Secure(Roles.ADMIN)
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{name}")
    boolean deleteRepository(@PathParam("name") String name) {
        githubService.deleteRepo(name)
    }
}
