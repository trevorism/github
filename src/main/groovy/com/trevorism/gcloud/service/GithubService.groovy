package com.trevorism.gcloud.service

import com.trevorism.gcloud.model.Repository
import com.trevorism.gcloud.model.WorkflowRequest

interface GithubService {

    List<Repository> listRepos()
    Repository createRepo(Repository repository)
    Repository getRepo(String repositoryName)
    boolean deleteRepo(String repositoryName)

    boolean rerunLastGithubAction(String repositoryName)
    void setGithubSecret(String repositoryName, String secretName, String secretValue)
    String getLatestRelease(String repositoryName)
    void invokeWorkflow(WorkflowRequest request)
}