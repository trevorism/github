package com.trevorism.gcloud.model


import com.google.gson.annotations.SerializedName
import groovy.transform.ToString

@ToString
class Repository {

    String id
    String name
    String description
    @SerializedName(value = "private")
    boolean notPublic
    Date created_at
    Date updated_at
    Date pushed_at
    String default_branch
    String language
    String full_name
}
