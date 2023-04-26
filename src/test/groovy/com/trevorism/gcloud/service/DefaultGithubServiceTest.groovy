package com.trevorism.gcloud.service

import com.google.gson.Gson
import com.trevorism.PropertiesProvider
import com.trevorism.gcloud.model.Repository
import com.trevorism.gcloud.model.WorkflowRequest
import com.trevorism.http.HeadersHttpResponse
import com.trevorism.http.HttpClient
import org.junit.jupiter.api.Test

class DefaultGithubServiceTest {

    private GithubService githubService = new DefaultGithubService()
    private Gson gson = new Gson()

    @Test
    void testListRepos() {
        String json = gson.toJson([new Repository(name: "zzUnitTest", notPublic: false)])
        githubService.httpClient = [get: { url, headers -> createCloseableHttpResponse(json) }] as HttpClient
        githubService.propertiesProvider = [getProperty: {x -> ""}] as PropertiesProvider
        def result = githubService.listRepos()
        assert result
        assert result[0]
        assert result[0].name == "zzUnitTest"
    }

    @Test
    void testGetRepo() {
        String json = gson.toJson(new Repository(name: "zzUnitTest", notPublic: false))
        githubService.httpClient = [get: { url, headers -> createCloseableHttpResponse(json) }] as HttpClient
        githubService.propertiesProvider = [getProperty: {x -> ""}] as PropertiesProvider
        def result = githubService.getRepo("zzUnitTest")
        assert result
        assert result.name == "zzUnitTest"
    }

    @Test
    void testCreateRepo() {
        Repository repository = new Repository(name: "zzUnitTest", notPublic: false)
        String json = gson.toJson(repository)
        githubService.httpClient = [post: { url, body, headers -> createCloseableHttpResponse(json) }] as HttpClient
        githubService.propertiesProvider = [getProperty: {x -> ""}] as PropertiesProvider
        def result = githubService.createRepo(repository)
        assert result
        assert result.name == "zzUnitTest"
    }

    @Test
    void testDeleteRepo() {
        githubService.httpClient = [delete: { url, headers -> createCloseableHttpResponse("", 204) }] as HttpClient
        githubService.propertiesProvider = [getProperty: {x -> ""}] as PropertiesProvider
        assert githubService.deleteRepo("zzUnitTest")
    }

    @Test
    void testInvokeWorkflow(){
        githubService.httpClient = [post: { url, body, headers -> createCloseableHttpResponse("", 204) }] as HttpClient
        githubService.propertiesProvider = [getProperty: {x -> ""}] as PropertiesProvider
        githubService.invokeWorkflow("test", new WorkflowRequest(testType: "unit"))
        assert true
    }

    @Test
    void testGetWorkflowStatus(){
        String json = gson.toJson(["workflow_runs": [[id:123,workflow_id:234,event:"push",status:"queued",conclusion:"success"]]])
        githubService.httpClient = [get: { url, headers -> createCloseableHttpResponse(json) }] as HttpClient
        githubService.propertiesProvider = [getProperty: {x -> ""}] as PropertiesProvider

        def workflowStatus = githubService.getWorkflowStatus("testing", "test.yml")
        assert workflowStatus.runId == 123
        assert workflowStatus.workflowId == 234
        assert workflowStatus.event == "push"
        assert workflowStatus.state == "queued"
        assert workflowStatus.result == "success"
    }

    private static HeadersHttpResponse createCloseableHttpResponse(String responseString, int statusCode = 200) {
        return new HeadersHttpResponse(responseString,[:])
    }
}
