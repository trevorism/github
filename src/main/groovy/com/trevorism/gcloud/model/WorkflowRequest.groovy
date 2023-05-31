package com.trevorism.gcloud.model

class WorkflowRequest {

    String branchName = "master"
    String yamlName = "deploy.yml"
    Map<String, String> workflowInputs = [:]
}
