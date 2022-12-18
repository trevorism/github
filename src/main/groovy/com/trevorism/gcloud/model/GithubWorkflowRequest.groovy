package com.trevorism.gcloud.model

class GithubWorkflowRequest {

    Inputs inputs
    String ref

    GithubWorkflowRequest(String branchName, boolean unitTests){
        ref = branchName
        inputs = new Inputs()
        inputs.TEST_TYPE = unitTests ? "unit" : "acceptance"
    }

    class Inputs {
        String TEST_TYPE
    }

}
