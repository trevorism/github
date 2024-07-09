package com.trevorism.gcloud.model

class GithubWorkflowRequest {

    Map<String,String> inputs
    String ref

    GithubWorkflowRequest(String branchName, Map<String,String> inputs){
        ref = branchName
        this.inputs = inputs
    }

}
