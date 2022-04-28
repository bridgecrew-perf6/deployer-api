package uk.co.lucystevens.services.docker.jobs

import uk.co.lucystevens.api.dto.docker.jobs.DockerJobResponse

interface DockerJob {
    fun toDto(): DockerJobResponse
}