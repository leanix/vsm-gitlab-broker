import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.2"
    id("io.spring.dependency-management") version "1.1.2"
    id("io.gitlab.arturbosch.detekt") version "1.23.0"
    id("org.cyclonedx.bom") version "1.7.4"
    id("com.expediagroup.graphql") version "6.4.0"
    kotlin("jvm") version "1.8.21"
    kotlin("plugin.spring") version "1.8.21"
    id("jacoco")
}

group = "net.leanix.vsm"
version = "v1.1.1"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

val springCloudVersion: String by extra("2022.0.3")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.expediagroup:graphql-kotlin-spring-client:6.4.0")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.cloud:spring-cloud-starter-contract-stub-runner")
    testImplementation("org.awaitility:awaitility-kotlin:4.2.0")
    testImplementation("com.ninja-squad:springmockk:4.0.2")

    developmentOnly("io.netty:netty-resolver-dns-native-macos:4.1.85.Final") {
        artifact {
            classifier = "osx-aarch_64"
        }
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

detekt {
    autoCorrect = true
    parallel = true
    buildUponDefaultConfig = true
    dependencies {
        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.0")
    }
}

tasks.cyclonedxBom {
    setDestination(project.file("."))
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        xml.outputLocation.set(File("${project.buildDir}/jacocoXml/jacocoTestReport.xml"))
    }
}

tasks.processResources {
    doLast {
        file("build/resources/main/gradle.properties").writeText("version=${project.version}")
    }
}

graphql {
    client {
        schemaFile = file("${project.projectDir}/src/main/resources/schemas/gitlab_schema.graphql")
        packageName = "net.leanix.gitlabbroker.connector.adapter.graphql.data"
        queryFileDirectory = "${project.projectDir}/src/main/resources/queries"
    }
}
