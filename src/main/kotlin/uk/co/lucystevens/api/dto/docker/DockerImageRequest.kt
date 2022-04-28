package uk.co.lucystevens.api.dto.docker

data class DockerImageRequest(
    val name: String,
    val tag: String,
)
