package com.trevorism.gcloud.service

import com.trevorism.gcloud.model.Repository
import com.trevorism.gcloud.model.WorkflowRequest
import com.trevorism.gcloud.model.WorkflowStatus

interface GithubService {

    List<Repository> listRepos()
    Repository createRepo(Repository repository)
    Repository getRepo(String repositoryName)
    boolean deleteRepo(String repositoryName)

    String getLatestRelease(String repositoryName)
    boolean rerunLastGithubAction(String repositoryName)
    void setGithubSecret(String repositoryName, String secretName, String secretValue)
    void invokeWorkflow(String repositoryName, WorkflowRequest request)

    WorkflowStatus getWorkflowStatus(String repositoryName, String yamlName)
}