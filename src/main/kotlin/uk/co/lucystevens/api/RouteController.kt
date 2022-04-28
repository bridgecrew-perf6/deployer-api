package uk.co.lucystevens.api

import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import uk.co.lucystevens.config.Config

class RouteController(
    private val dockerApi: DockerApi,
    private val adminApi: AdminApi,
    private val config: Config
    ) {

    fun start(){
        val app = Javalin.create().start(config.getAppPort())

        // all api routes should be secured with a token
        app.routes {
            path("api") {
                before { adminApi.validateToken(it) }
                path("docker"){
                    path("images"){
                        get { dockerApi.listImages(it) }
                        put { dockerApi.pullImage(it) }
                        path("status/{jobId}") {
                            get { dockerApi.getImageJob(it) }
                        }
                    }
                    path("containers"){
                        get { dockerApi.listContainers(it) }
                        post { dockerApi.startContainer(it) }
                        path("{containerId}") {
                            delete { dockerApi.stopContainer(it) }
                            path("logs") {
                                get { dockerApi.getContainerLogs(it) }
                            }
                        }
                    }
                }
            }
        }
    }
}