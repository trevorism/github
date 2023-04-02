package com.trevorism.gcloud.model

import com.google.gson.Gson
import org.junit.jupiter.api.Test

class GithubWorkflowRequestTest {

    @Test
    void testGsonOnConstructor_testYmlUnitTest(){
        Gson gson = new Gson()
        GithubWorkflowRequest request = new GithubWorkflowRequest("test.yml","master",true)
        String json = gson.toJson(request)
        assert "{\"inputs\":{\"TEST_TYPE\":\"unit\"},\"ref\":\"master\"}" == json
    }

    @Test
    void testGsonOnConstructor_testYmlAcceptanceTest(){
        Gson gson = new Gson()
        GithubWorkflowRequest request = new GithubWorkflowRequest("test.yml","master",false)
        String json = gson.toJson(request)
        assert "{\"inputs\":{\"TEST_TYPE\":\"acceptance\"},\"ref\":\"master\"}" == json
    }

    @Test
    void testGsonOnConstructor_deployYml(){
        Gson gson = new Gson()
        GithubWorkflowRequest request = new GithubWorkflowRequest("deploy.yml","master",false)
        String json = gson.toJson(request)
        assert "{\"inputs\":{},\"ref\":\"master\"}" == json
    }
}
