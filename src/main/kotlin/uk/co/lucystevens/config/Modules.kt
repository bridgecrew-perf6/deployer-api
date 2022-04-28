package uk.co.lucystevens.config

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
import org.koin.dsl.module
import org.ktorm.database.Database
import org.ktorm.support.postgresql.PostgreSqlDialect
import uk.co.lucystevens.api.AdminApi
import uk.co.lucystevens.api.DockerApi
import uk.co.lucystevens.api.RouteController
import uk.co.lucystevens.cli.AppRunner
import uk.co.lucystevens.services.IdGenerator
import uk.co.lucystevens.services.docker.DockerService
import uk.co.lucystevens.services.docker.jobs.DockerJobService
import uk.co.lucystevens.services.secrets.SecretService
import java.time.Clock
import java.time.Duration
import kotlin.random.Random

object Modules {

    private val utils = module {
        single { AppRunner(get()) }
        single { Config() }
        single { IdGenerator() }
        single<Clock> { Clock.systemDefaultZone() }
        single<Random> { Random.Default }
    }

    private val apis = module {
        single { AdminApi(get()) }
        single { RouteController(get(), get(), get()) }
    }

    private val secrets = module {
        single { SecretService() }
    }

    private val docker = module {
        single<DockerClientConfig> {
            DefaultDockerClientConfig.createDefaultConfigBuilder().build()
        }
        single { setupDockerHttpClient(get(), get()) }
        single<DockerClient> { DockerClientImpl.getInstance(get(), get()) }
        single { DockerService(get(), get(), get()) }
        single { DockerApi(get(), get()) }
        single { DockerJobService(get(), get()) }
    }

    private fun setupDockerHttpClient(config: Config, dockerClientConfig: DockerClientConfig): DockerHttpClient =
        ApacheDockerHttpClient.Builder()
            .dockerHost(dockerClientConfig.dockerHost)
            .sslConfig(dockerClientConfig.sslConfig)
            .maxConnections(config.getDockerMaxConnections())
            .connectionTimeout(Duration.ofSeconds(config.getDockerConnectionTimeout()))
            .responseTimeout(Duration.ofSeconds(config.getDockerResponseTimeout()))
            .build()

    private val daos = module {
        single { setupDatabase(get()) }
    }

    private fun setupDatabase(config: Config): Database {
        return Database.connect(
            url = config.getDatabaseUrl(),
            driver = config.getDatabaseDriver(),
            user = config.getDatabaseUsername(),
            password = config.getDatabasePassword(),
            dialect = PostgreSqlDialect()
        )
    }


    /**
    Define other modules for each part of the application, then add them to the allModules value
     **/

    internal val allModules = listOf(
        utils,
        apis,
        docker,
        secrets
        //daos
    )

}