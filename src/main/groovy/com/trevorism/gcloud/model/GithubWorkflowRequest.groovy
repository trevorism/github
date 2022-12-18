package com.trevorism.gcloud.model

class GithubWorkflowRequest {

    Inputs inputs
    String ref

    GithubWorkflowRequest(String yamlName, String branchName, boolean unitTests){
        ref = branchName
        inputs = new Inputs()
        if(yamlName.toLowerCase() == "test.yml"){
            inputs.TEST_TYPE = unitTests ? "unit" : "acceptance"
        }
    }

    class Inputs {
        String TEST_TYPE
    }

}
