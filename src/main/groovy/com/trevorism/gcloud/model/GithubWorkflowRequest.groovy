package com.trevorism.gcloud.model

class GithubWorkflowRequest {

    Inputs inputs
    String ref

    GithubWorkflowRequest(String yamlName, String branchName, String testType){
        ref = branchName
        inputs = new Inputs()
        if(yamlName.toLowerCase() == "test.yml"){
            inputs.TEST_TYPE = testType
        }
    }

    class Inputs {
        String TEST_TYPE
    }

}
