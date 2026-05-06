import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification

buildscript {
    repositories {
        val artifactoryUser = System.getenv("JFROG_ARTIFACTORY_CREDENTIALS_USR")
            ?: System.getProperty("artifactory_user")
            ?: System.getenv("ARTIFACTORY_USER")
            ?: ""
        val artifactoryPassword = System.getenv("JFROG_ARTIFACTORY_CREDENTIALS_PSW")
            ?: System.getProperty("artifactory_password")
            ?: System.getenv("ARTIFACTORY_PASSWORD")
            ?: ""
        maven {
            url = uri("https://tala.jfrog.io/artifactory/maven-central-virtual/")
            if (artifactoryUser.isNotEmpty()) {
                credentials {
                    username = artifactoryUser
                    password = artifactoryPassword
                }
            }
        }
        gradlePluginPortal()
    }
}

plugins {
    groovy
    java
    `maven-publish`
    jacoco
    id("com.jfrog.artifactory") version "4.33.1"
}

group = "co.tala.performance.flo"
version = "0.1.8"

println("version:$version")

val artifactory_user: String by project
val artifactory_password: String by project
val artifactory_contextUrl: String by project

val sourcesJar by tasks.registering(Jar::class) {
    dependsOn("classes")
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}

publishing {
    publications {
        create<MavenPublication>("floRunna") {
            from(components["java"])
            artifact(sourcesJar)
        }
    }
}

repositories {
    maven {
        url = uri("https://tala.jfrog.io/artifactory/maven-central-virtual/")
        credentials {
            username = artifactory_user
            password = artifactory_password
        }
        content {
            excludeGroupByRegex("co\\.tala.*")
        }
    }
}

artifactory {
    setContextUrl(artifactory_contextUrl)
    publish {
        repository {
            setRepoKey("maven-snapshot-local")
            setUsername(artifactory_user)
            setPassword(artifactory_password)
            setMavenCompatible(true)
        }
        defaults {
            publications("floRunna")
            setPublishArtifacts(true)
        }
    }
    resolve {
        repository {
            setRepoKey("maven-snapshot-virtual")
            setUsername(artifactory_user)
            setPassword(artifactory_password)
            setMavenCompatible(true)
        }
    }
}

dependencies {
    implementation("org.jfrog.buildinfo:build-info-extractor-gradle:5.0.0")
    implementation("org.apache.groovy:groovy-all:4.0.21")
    implementation("org.apache.groovy:groovy-json:4.0.30")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.spockframework:spock-core:2.3-groovy-4.0")
}

val fatJar by tasks.registering(Jar::class) {
    manifest {
        attributes(
            "Implementation-Title" to "Flo-Runna Fat Jar",
            "Implementation-Version" to archiveVersion.get()
        )
    }
    archiveBaseName.set("${project.name}-all")
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get())
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
    configure<JacocoTaskExtension> {
        destinationFile = file("$buildDir/jacoco/jacocoTest.exec")
        classDumpDir = file("$buildDir/jacoco/classpathdumps")
    }
    finalizedBy(
        tasks.jacocoTestReport,
        tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification")
    )
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(false)
        csv.required.set(false)
        html.outputLocation.set(file("$buildDir/jacocoHtml"))
    }
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) { exclude("nothing") }
        })
    )
}

tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) { exclude("nothing") }
        })
    )
    violationRules {
        rule {
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.90".toBigDecimal()
            }
        }
    }
}
