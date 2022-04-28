package uk.co.lucystevens.api

import io.javalin.http.Context
import io.javalin.http.NotFoundResponse
import uk.co.lucystevens.api.dto.docker.DockerContainerRequest
import uk.co.lucystevens.api.dto.docker.DockerImageRequest
import uk.co.lucystevens.api.dto.docker.DockerImageResponse
import uk.co.lucystevens.services.docker.DockerService
import java.time.Clock
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class DockerApi(
    private val dockerService: DockerService,
    private val clock: Clock
    ) {

    fun pullImage(ctx: Context) {
        val dto = ctx.bodyAsClass<DockerImageRequest>()
        val job = dockerService.pullImage(dto.name, dto.tag)
        ctx.json(job.toDto())
    }

    fun getImageJob(ctx: Context) {
        val jobId = ctx.pathParam("jobId")
        val job = dockerService.getJob(jobId)?:
            throw NotFoundResponse("Job $jobId not found")
        ctx.json(job.toDto())
    }

    fun listImages(ctx: Context) {
        ctx.json(dockerService.listImages()
            .filterNot { it.repoTags.isNullOrEmpty() }.map {
                val nameParts = it.repoTags[0].split(":")
                val createdAt = LocalDateTime.ofEpochSecond(it.created, 0, ZoneOffset.UTC)
                DockerImageResponse(
                    it.id,
                    nameParts[0],
                    nameParts[1],
                    createdAt.format(DateTimeFormatter.ISO_DATE_TIME))
            }
            .filterNot { it.name == "<none>" }
        )
    }

    fun startContainer(ctx: Context) {
        val dto = ctx.bodyAsClass<DockerContainerRequest>()
        ctx.json(dockerService.runContainer(dto))
    }

    fun listContainers(ctx: Context) {
        ctx.json(dockerService.listContainers())
    }

    fun stopContainer(ctx: Context) {
        dockerService.stopContainer(ctx.pathParam("containerId"))
    }

    fun getContainerLogs(ctx: Context) {
        val since = ctx.queryParam("since")?.let {
            LocalDateTime.parse(it)
        }?: LocalDateTime.now(clock).minusDays(1)

        val until = ctx.queryParam("until")?.let {
            LocalDateTime.parse(it)
        }?:LocalDateTime.now(clock)

        ctx.json(
            dockerService.getContainerLogs(ctx.pathParam("containerId"), since, until)
        )

    }

}