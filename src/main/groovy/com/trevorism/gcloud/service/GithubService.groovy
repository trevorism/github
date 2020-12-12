package com.trevorism.gcloud.service

import com.trevorism.gcloud.model.Repository

interface GithubService {

    List<Repository> listRepos()
    Repository createRepo(Repository repository)
    Repository getRepo(String repositoryName)
    boolean deleteRepo(String repositoryName)
}