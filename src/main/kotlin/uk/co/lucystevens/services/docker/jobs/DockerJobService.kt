package uk.co.lucystevens.services.docker.jobs

import uk.co.lucystevens.services.IdGenerator
import java.time.Clock
import java.time.LocalDateTime

class DockerJobService(
    private val idGenerator: IdGenerator,
    private val clock: Clock
) {

    private val jobs = mutableMapOf<String, TypedDockerJob<*>>()

    fun <T> registerJob(handler: DockerJobHandler<T>): TypedDockerJob<T> {
        return TypedDockerJob(
            idGenerator.generateId(),
            handler,
            LocalDateTime.now(clock)
        ).apply {
            jobs[id] = this
        }
    }

    fun getJob(id: String): DockerJob? {
        return jobs[id]
    }

    fun cancelJob(id: String) {
        jobs[id]?.handler?.onComplete()
        jobs.remove(id)
    }

    // TODO this should run every hour (?) to remove old jobs
    fun trimJobs(){
        jobs.values.removeIf {
            it.createdDateTime.isBefore(LocalDateTime.now(clock).minusHours(1))
        }
    }

}