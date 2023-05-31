package com.trevorism.gcloud.model

class WorkflowResponse {

    String statusUrl

    WorkflowResponse(){}

    WorkflowResponse(String status){
        this.statusUrl = status
    }
}
