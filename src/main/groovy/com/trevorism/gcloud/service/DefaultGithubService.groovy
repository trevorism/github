package com.trevorism.gcloud.service

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.trevorism.gcloud.model.Repository
import com.trevorism.http.headers.HeadersHttpClient
import com.trevorism.http.headers.HeadersJsonHttpClient
import com.trevorism.http.util.ResponseUtils
import com.trevorism.secure.PropertiesProvider
import org.apache.http.client.methods.CloseableHttpResponse

import java.lang.reflect.Type

class DefaultGithubService implements GithubService {

    private static final String BASE_GITHUB_URL = "https://api.github.com"
    private HeadersHttpClient httpClient = new HeadersJsonHttpClient()
    private Gson gson = new Gson()
    private PropertiesProvider propertiesProvider = new PropertiesProvider()

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

    private def createAuthHeader() {
        ["Authorization": "token ${propertiesProvider.getProperty('accessToken')}".toString()]
    }
}
