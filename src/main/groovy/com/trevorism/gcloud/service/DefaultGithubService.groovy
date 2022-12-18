package com.trevorism.gcloud.service

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.goterl.lazysodium.LazySodiumJava
import com.goterl.lazysodium.SodiumJava
import com.goterl.lazysodium.utils.Key
import com.trevorism.gcloud.model.EncryptedSecret
import com.trevorism.gcloud.model.GithubWorkflowRequest
import com.trevorism.gcloud.model.Repository
import com.trevorism.gcloud.model.WorkflowRequest
import com.trevorism.http.headers.HeadersHttpClient
import com.trevorism.http.headers.HeadersJsonHttpClient
import com.trevorism.http.util.ResponseUtils
import com.trevorism.secure.ClasspathBasedPropertiesProvider
import com.trevorism.secure.PropertiesProvider
import groovy.json.JsonSlurper
import org.apache.http.client.methods.CloseableHttpResponse

import java.lang.reflect.Type
import java.nio.charset.StandardCharsets

class DefaultGithubService implements GithubService {

    private static final String BASE_GITHUB_URL = "https://api.github.com"
    private HeadersHttpClient httpClient = new HeadersJsonHttpClient()
    private Gson gson = new Gson()
    private PropertiesProvider propertiesProvider = new ClasspathBasedPropertiesProvider()

    @Override
    List<Repository> listRepos() {
        CloseableHttpResponse response = httpClient.get("${BASE_GITHUB_URL}/user/repos?per_page=100", createAuthHeader())
        String json = ResponseUtils.getEntity(response)
        Type type = TypeToken.getParameterized(List.class, Repository).getType()
        gson.fromJson(json, type)
    }

    @Override
    Repository createRepo(Repository repository) {
        String json = gson.toJson(repository)
        CloseableHttpResponse response = httpClient.post("${BASE_GITHUB_URL}/user/repos", json, createAuthHeader())
        String responseJson = ResponseUtils.getEntity(response)
        gson.fromJson(responseJson, Repository)
    }

    @Override
    Repository getRepo(String repositoryName) {
        CloseableHttpResponse response = httpClient.get("${BASE_GITHUB_URL}/repos/trevorism/${repositoryName}", createAuthHeader())
        String responseJson = ResponseUtils.getEntity(response)
        gson.fromJson(responseJson, Repository)
    }

    @Override
    boolean deleteRepo(String repositoryName) {
        CloseableHttpResponse response = httpClient.delete("${BASE_GITHUB_URL}/repos/trevorism/${repositoryName}", createAuthHeader())
        response.getStatusLine().statusCode == 204
    }

    @Override
    boolean rerunLastGithubAction(String repositoryName) {
        CloseableHttpResponse response = httpClient.get("${BASE_GITHUB_URL}/repos/trevorism/${repositoryName}/actions/runs", createAuthHeader())
        def responseObject = new JsonSlurper().parseText(ResponseUtils.getEntity(response))
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
        CloseableHttpResponse response = httpClient.get("${BASE_GITHUB_URL}/repos/trevorism/${repositoryName}/actions/secrets/public-key", createAuthHeader())
        def responseObject = new JsonSlurper().parseText(ResponseUtils.getEntity(response))
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
        CloseableHttpResponse response = httpClient.get("${BASE_GITHUB_URL}/repos/trevorism/${repositoryName}/releases/latest", createAuthHeader())
        String responseJson = ResponseUtils.getEntity(response)
        def responseObject = new JsonSlurper().parseText(responseJson)
        if (!responseObject.containsKey("name"))
            return null
        return responseObject["name"]
    }

    @Override
    void invokeWorkflow(WorkflowRequest request) {
        String json = gson.toJson(new GithubWorkflowRequest(request.branchName, request.unitTest))
        CloseableHttpResponse response = httpClient.post("${BASE_GITHUB_URL}/repos/trevorism/${request.repoName}/actions/workflows/test.yml/dispatches", json, createAuthHeader())
        ResponseUtils.closeSilently(response)
    }

    private def createAuthHeader() {
        ["Authorization": "token ${propertiesProvider.getProperty('accessToken')}".toString()]
    }
}
