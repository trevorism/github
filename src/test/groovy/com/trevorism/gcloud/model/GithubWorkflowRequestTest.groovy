package com.trevorism.gcloud.model

import com.google.gson.Gson
import org.junit.jupiter.api.Test

class GithubWorkflowRequestTest {

    @Test
    void testGsonOnConstructor_testYmlUnitTest(){
        Gson gson = new Gson()
        GithubWorkflowRequest request = new GithubWorkflowRequest("master",["TEST_TYPE":"unit"])
        String json = gson.toJson(request)
        assert "{\"inputs\":{\"TEST_TYPE\":\"unit\"},\"ref\":\"master\"}" == json
    }

    @Test
    void testGsonOnConstructor_testYmlAcceptanceTest(){
        Gson gson = new Gson()
        GithubWorkflowRequest request = new GithubWorkflowRequest("master",["TEST_TYPE":"cucumber"])
        String json = gson.toJson(request)
        assert "{\"inputs\":{\"TEST_TYPE\":\"cucumber\"},\"ref\":\"master\"}" == json
    }

    @Test
    void testGsonOnConstructor_deployYml(){
        Gson gson = new Gson()
        GithubWorkflowRequest request = new GithubWorkflowRequest("master",[:])
        String json = gson.toJson(request)
        assert "{\"inputs\":{},\"ref\":\"master\"}" == json
    }
}
