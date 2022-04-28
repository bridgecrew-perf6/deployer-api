package uk.co.lucystevens.api.dto.docker

data class DockerContainerRequest(
    val image: DockerImageRequest,
    val portBindings: List<String>,
    val name: String?,
    val envVars: List<DockerEnvVariable>,
    val restartPolicy: DockerRestartPolicy = DockerRestartPolicy.NEVER
)