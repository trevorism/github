package com.trevorism.gcloud

import com.google.gson.Gson
import com.trevorism.https.AppClientSecureHttpClient
import com.trevorism.https.SecureHttpClient
import io.cucumber.groovy.EN
import io.cucumber.groovy.Hooks

/**
 * @author tbrooks
 */

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

def responseObjects

When(/the list of repositories is requested/) {  ->
    SecureHttpClient secureHttpClient = new AppClientSecureHttpClient()
    String responseJson = secureHttpClient.get("https://github.project.trevorism.com/repo")
    Gson gson = new Gson()
    responseObjects = gson.fromJson(responseJson, List.class)
}

Then(/over {int} repositories are returned/) { Integer minSize ->
    assert responseObjects.size() > minSize
}

Then(/less than {int} are returned/) { Integer maxSize ->
    assert responseObjects.size() < maxSize
}