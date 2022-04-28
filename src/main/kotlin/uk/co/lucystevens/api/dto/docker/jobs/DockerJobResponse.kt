package uk.co.lucystevens.api.dto.docker.jobs

data class DockerJobResponse(
    val id: String,
    val status: JobStatus,
    val createdDate: String,
    val messages: List<String>?
)