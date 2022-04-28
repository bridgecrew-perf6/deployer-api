import com.avast.gradle.dockercompose.ComposeSettings

plugins {
    kotlin("jvm") version "1.6.10"

    application
    `maven-publish`

    id("com.avast.gradle.docker-compose") version "0.14.9"
    id("uk.co.lukestevens.plugins.release-helper") version "0.1.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenLocal()
    mavenCentral()
}

group = "uk.co.lucystevens"

sourceSets {
    create("integrationTest") {
        compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
        runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
    }
}

val integrationTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}

val ktormVersion = "3.4.1"
val koinVersion= "3.1.5"
val dockerJavaVersion = "3.2.13"
val jacksonVersion = "2.13.1"

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.ktorm:ktorm-core:$ktormVersion")
    implementation("org.ktorm:ktorm-support-postgresql:$ktormVersion")
    implementation("org.postgresql:postgresql:42.3.3")

    implementation("io.javalin:javalin:4.3.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("io.insert-koin:koin-core:$koinVersion")

    // jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

    // docker-java
    implementation("com.github.docker-java:docker-java-core:$dockerJavaVersion")
    implementation("com.github.docker-java:docker-java-transport-httpclient5:$dockerJavaVersion")

    // TODO replace with actual impl
    implementation("org.slf4j:slf4j-simple:1.7.36")

    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.12.3")
    //testImplementation("io.insert-koin:koin-test:$koinVersion")
}

application {
    mainClass.set("uk.co.lucystevens.LauncherKt")
}

/**
 *  Tasks
 */
configure<ComposeSettings> {
    startedServices.set(listOf("local-db"))
    forceRecreate.set(true)
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes("Main-Class" to application.mainClass.get())
    }
}

tasks.shadowJar {
    archiveBaseName.set("DeployerApi")
    archiveClassifier.set("")
}

val integrationTest = task<Test>("integrationTest") {
    useJUnitPlatform()
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    outputs.upToDateWhen { false }
    mustRunAfter(tasks.composeUp)
}

val integrationTestWithDockerCompose = task("integrationTestWithDockerCompose") {
    dependsOn(integrationTest)
    integrationTest.mustRunAfter(tasks.composeUp)
}

dockerCompose.isRequiredBy(integrationTest)
