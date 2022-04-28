package uk.co.lucystevens.services.docker

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.command.CreateContainerResponse
import com.github.dockerjava.api.model.Container
import com.github.dockerjava.api.model.HostConfig
import com.github.dockerjava.api.model.Image
import com.github.dockerjava.api.model.PortBinding
import com.github.dockerjava.api.model.PullResponseItem
import uk.co.lucystevens.api.dto.docker.DockerContainerRequest
import uk.co.lucystevens.api.dto.docker.DockerEnvVariableType
import uk.co.lucystevens.services.docker.jobs.DockerJob
import uk.co.lucystevens.services.docker.jobs.TypedDockerJob
import uk.co.lucystevens.services.docker.jobs.DockerJobHandler
import uk.co.lucystevens.services.docker.jobs.DockerJobService
import uk.co.lucystevens.services.secrets.SecretService
import java.time.LocalDateTime
import java.time.ZoneOffset

class DockerService(
    private val dockerClient: DockerClient,
    private val dockerJobService: DockerJobService,
    private val secretService: SecretService
) {

    // TODO Make this async
    fun pullImage(name: String, tag: String): TypedDockerJob<PullResponseItem> {
        val handler = dockerClient.pullImageCmd("$name:$tag").exec(DockerJobHandler {
            "${it.status} ${it.progress}"
        })
        return dockerJobService.registerJob(handler)
    }

    fun getJob(id: String): DockerJob? {
        return dockerJobService.getJob(id)
    }

    fun listImages(): List<Image>{
        return dockerClient.listImagesCmd().exec()
    }

    fun listContainers(): List<Container> {
        return dockerClient.listContainersCmd().exec()
    }

    fun runContainer(container: DockerContainerRequest): CreateContainerResponse {
        val hostConfig = HostConfig.newHostConfig()
            .withPortBindings(container.portBindings.map { PortBinding.parse(it) })
            .withRestartPolicy(container.restartPolicy.restartPolicy)

        val command = dockerClient.createContainerCmd("${container.image.name}:${container.image.tag}")
            .withHostConfig(hostConfig)
            .withEnv(container.envVars.map {
                when(it.type){
                    DockerEnvVariableType.SECRET -> "${it.key}=${secretService.getSecretValue(it.value)}"
                    DockerEnvVariableType.PLAIN -> "${it.key}=${it.value}"
                }
            })

        container.name?.let { command.withName(it) }

        return command.exec().apply {
            dockerClient.startContainerCmd(id).exec()
        }
    }

    fun stopContainer(containerId: String){
        dockerClient.stopContainerCmd(containerId).exec()
    }

    fun getContainerLogs(containerId: String, since: LocalDateTime, until: LocalDateTime): List<String> {
        val handler = dockerClient.logContainerCmd(containerId)
            .withStdOut(true)
            .withStdErr(true)
            .withSince(since.toEpochSecond(ZoneOffset.UTC).toInt())
            .withUntil(until.toEpochSecond(ZoneOffset.UTC).toInt())
            .exec(DockerJobHandler { it.toString() })
        handler.awaitCompletion().close()
        return handler.responses
    }
}