package uk.co.lucystevens.api.dto.docker

data class DockerEnvVariable(
    val key: String,
    val value: String,
    val type: DockerEnvVariableType = DockerEnvVariableType.PLAIN
)
