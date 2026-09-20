package com.trevorism.gcloud.webapi.controller

import com.trevorism.AppVersion
import org.junit.jupiter.api.Test

/**
 * @author tbrooks
 */
class RootControllerTest {

    @Test
    void testRootControllerEndpoints(){
        RootController rootController = new RootController()
        assert rootController.index().getBody().get().contains("ping")
        assert rootController.index().getBody().get().contains("help")
    }

    @Test
    void testRootControllerPing(){
        RootController rootController = new RootController()
        assert rootController.ping() == "pong"
    }

    @Test
    void testVersionEndpoint() {
        RootController rootController = new RootController()
        assert rootController.version() == AppVersion.SEMVER
    }
}
