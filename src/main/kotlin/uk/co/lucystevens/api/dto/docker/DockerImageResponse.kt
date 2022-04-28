package uk.co.lucystevens.api.dto.docker

import java.time.LocalDateTime

data class DockerImageResponse(
    val id: String,
    val name: String,
    val tag: String,
    val createdAt: String
)
