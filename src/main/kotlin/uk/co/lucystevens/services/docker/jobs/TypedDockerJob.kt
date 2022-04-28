package uk.co.lucystevens.services.docker.jobs

import uk.co.lucystevens.api.dto.docker.jobs.DockerJobResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class TypedDockerJob<T> (
    val id: String,
    val handler: DockerJobHandler<T>,
    val createdDateTime: LocalDateTime
): DockerJob {
    override fun toDto(): DockerJobResponse = DockerJobResponse(
        id = id,
        status = handler.status,
        createdDate = createdDateTime.format(DateTimeFormatter.ISO_DATE_TIME),
        messages = handler.responses
    )

}
