package com.trevorism.gcloud.service

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.goterl.lazysodium.LazySodiumJava
import com.goterl.lazysodium.SodiumJava
import com.goterl.lazysodium.utils.Key
import com.trevorism.ClasspathBasedPropertiesProvider
import com.trevorism.PropertiesProvider
import com.trevorism.gcloud.model.EncryptedSecret
import com.trevorism.gcloud.model.GithubWorkflowRequest
import com.trevorism.gcloud.model.Repository
import com.trevorism.gcloud.model.WorkflowRequest
import com.trevorism.gcloud.model.WorkflowStatus
import com.trevorism.http.HeadersHttpResponse
import com.trevorism.http.HttpClient
import com.trevorism.http.JsonHttpClient
import groovy.json.JsonSlurper

import java.lang.reflect.Type
import java.nio.charset.StandardCharsets

class DefaultGithubService implements GithubService {

    private static final String BASE_GITHUB_URL = "https://api.github.com"
    private HttpClient httpClient = new JsonHttpClient()
    private Gson gson = new Gson()
    private PropertiesProvider propertiesProvider = new ClasspathBasedPropertiesProvider()

    @Override
    List<Repository> listRepos() {
        HeadersHttpResponse response = httpClient.get("${BASE_GITHUB_URL}/user/repos?per_page=100", createAuthHeader())
        String json = response.value
        Type type = TypeToken.getParameterized(List.class, Repository).getType()
        gson.fromJson(json, type)
    }

    @Override
    Repository createRepo(Repository repository) {
        String json = gson.toJson(repository)
        HeadersHttpResponse response = httpClient.post("${BASE_GITHUB_URL}/user/repos", json, createAuthHeader())
        String responseJson = response.value
        gson.fromJson(responseJson, Repository)
    }

    @Override
    Repository getRepo(String repositoryName) {
        HeadersHttpResponse response = httpClient.get("${BASE_GITHUB_URL}/repos/trevorism/${repositoryName}", createAuthHeader())
        String responseJson = response.value
        gson.fromJson(responseJson, Repository)
    }

    @Override
    boolean deleteRepo(String repositoryName) {
        HeadersHttpResponse response = httpClient.delete("${BASE_GITHUB_URL}/repos/trevorism/${repositoryName}", createAuthHeader())
        return true
    }

    @Override
    boolean rerunLastGithubAction(String repositoryName) {
        HeadersHttpResponse response = httpClient.get("${BASE_GITHUB_URL}/repos/trevorism/${repositoryName}/actions/runs", createAuthHeader())
        def responseObject = new JsonSlurper().parseText(response.value)
        def sortedRuns = responseObject["workflow_runs"].sort {
            it["created_at"]
        }
        if (!sortedRuns) {
            return false
        }
        def runId = sortedRuns.last()["id"]
        httpClient.post("${BASE_GITHUB_URL}/repos/trevorism/${repositoryName}/actions/runs/${runId}/rerun", "{}", createAuthHeader())
        return true
    }

    @Override
    void setGithubSecret(String repositoryName, String secretName, String secretValue) {
        HeadersHttpResponse response = httpClient.get("${BASE_GITHUB_URL}/repos/trevorism/${repositoryName}/actions/secrets/public-key", createAuthHeader())
        def responseObject = new JsonSlurper().parseText(response.value)
        String keyId = responseObject["key_id"]
        String secretEncryptedBin = encryptSecret(secretValue, responseObject["key"])

        EncryptedSecret encryptedSecret = new EncryptedSecret(key_id: keyId, encrypted_value: secretEncryptedBin)
        String json = gson.toJson(encryptedSecret)
        httpClient.put("${BASE_GITHUB_URL}/repos/trevorism/${repositoryName}/actions/secrets/${secretName}", json, createAuthHeader())
    }

    private String encryptSecret(String secretValue, String keyString) {
        LazySodiumJava lazySodium = new LazySodiumJava(new SodiumJava(), StandardCharsets.UTF_8)
        String hexSecret = lazySodium.cryptoBoxSealEasy(secretValue, Key.fromBytes(Base64.decoder.decode(keyString)))
        def secretByteArray = lazySodium.sodiumHex2Bin(hexSecret)
        String secretEncryptedBin = new String(Base64.encoder.encode(secretByteArray), StandardCharsets.UTF_8)
        secretEncryptedBin
    }

    @Override
    String getLatestRelease(String repositoryName) {
        HeadersHttpResponse response = httpClient.get("${BASE_GITHUB_URL}/repos/trevorism/${repositoryName}/releases/latest", createAuthHeader())
        String responseJson = response.value
        def responseObject = new JsonSlurper().parseText(responseJson)
        if (!responseObject.containsKey("name"))
            return null
        return responseObject["name"]
    }

    @Override
    void invokeWorkflow(String repoName, WorkflowRequest request) {
        String json = gson.toJson(new GithubWorkflowRequest(request.yamlName, request.branchName, request.testType))
        HeadersHttpResponse response = httpClient.post("${BASE_GITHUB_URL}/repos/trevorism/${repoName}/actions/workflows/${request.yamlName}/dispatches", json, createAuthHeader())
    }

    @Override
    WorkflowStatus getWorkflowStatus(String repositoryName, String yamlName) {
        HeadersHttpResponse response = httpClient.get("${BASE_GITHUB_URL}/repos/trevorism/${repositoryName}/actions/workflows/${yamlName}/runs", createAuthHeader())
        String responseJson = response.value
        def responseObject = new JsonSlurper().parseText(responseJson)
        def sortedRuns = responseObject["workflow_runs"].sort {
            it["created_at"]
        }
        def latestRun = sortedRuns.last()

        return new WorkflowStatus(workflowId: latestRun["workflow_id"], runId: latestRun["id"], state: latestRun["status"],
                result: latestRun["conclusion"], event: latestRun["event"], createdAt: latestRun["created_at"], updatedAt: latestRun["updated_at"])
    }

    private def createAuthHeader() {
        ["Authorization": "token ${propertiesProvider.getProperty('accessToken')}".toString()]
    }
}
