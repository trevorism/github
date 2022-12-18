package com.trevorism.gcloud.model

class GithubWorkflowRequest {

    Inputs inputs
    String ref

    GithubWorkflowRequest(String branchName, boolean testType){
        ref = branchName
        inputs = new Inputs()
        inputs.TEST_TYPE = testType ? "acceptance" : "unit"
    }

    class Inputs {
        String TEST_TYPE
    }

}
