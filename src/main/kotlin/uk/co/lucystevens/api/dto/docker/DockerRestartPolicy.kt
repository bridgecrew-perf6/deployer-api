package uk.co.lucystevens.api.dto.docker

import com.github.dockerjava.api.model.RestartPolicy

enum class DockerRestartPolicy(val restartPolicy: RestartPolicy) {

    NEVER(RestartPolicy.noRestart()),
    ON_FAILURE(RestartPolicy.onFailureRestart(5)),
    ALWAYS(RestartPolicy.alwaysRestart()),
    UNLESS_STOPPED(RestartPolicy.unlessStoppedRestart())

}