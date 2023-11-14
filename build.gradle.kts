@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlinx.serialization)
    id("maven-publish")
}

group = "io.github.forceload"
version = "0.0.1"

val packageName = group

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations {
            all { kotlinOptions.jvmTarget = "17" }
            val test = getByName("test")

            tasks.register<Jar>("botJar") {
                doFirst {
                    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
                    val main by kotlin.jvm().compilations.getting

                    manifest {
                        attributes("Main-Class" to "$packageName.discordkt.LauncherKt")
                    }

                    from(
                        main.output.classesDirs, test.output,
                        main.runtimeDependencyFiles.files.filter { it.name.endsWith("jar") }.map { zipTree(it) }
                    )
                }
            }

            tasks.register<Jar>("libJar") {
                doFirst {
                    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
                    val main by kotlin.jvm().compilations.getting

                    manifest {
                        attributes("Main-Class" to "$packageName.discordkt.LauncherKt")
                    }

                    from(
                        main.output.classesDirs,
                        main.runtimeDependencyFiles.files.filter { it.name.endsWith("jar") }.map { zipTree(it) }
                    )
                }
            }

            tasks.withType<Jar> {
                outputs.upToDateWhen { false }
            }
        }

        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    /*js(BOTH) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
    }*/

    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }


    sourceSets {
        val ktor_version: String by project

        val commonMain by getting {
            dependencies {
                implementation(libs.ktor.client.core)

                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.coroutines)

                implementation(kotlin("stdlib-common"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                // implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.2.1")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.ktor.client.cio)
            }
        }

        val jvmTest by getting
        // val jsMain by getting
        // val jsTest by getting
        val nativeMain by getting {
            dependencies {
                implementation(libs.ktor.client.curl)
            }
        }
        val nativeTest by getting
    }
}
