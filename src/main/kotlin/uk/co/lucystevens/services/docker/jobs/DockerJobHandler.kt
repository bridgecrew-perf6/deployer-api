package uk.co.lucystevens.services.docker.jobs

import com.github.dockerjava.api.async.ResultCallback
import uk.co.lucystevens.api.dto.docker.jobs.JobStatus

class DockerJobHandler<T>(
    private val parser: (T) -> String
) : ResultCallback.Adapter<T>() {

    val responses = mutableListOf<String>()
    var status = JobStatus.PENDING
    var error: Throwable? = null


    override fun onNext(response: T?) {
        response?.let { responses.add(parser.invoke(it)) }
    }

    override fun onComplete() {
        super.onComplete()
        status = JobStatus.COMPLETE
    }

    override fun onError(throwable: Throwable?) {
        error = throwable
        status = JobStatus.FAILED
        super.onError(throwable)
    }
}