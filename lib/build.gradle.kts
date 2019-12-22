import com.google.protobuf.gradle.*

plugins {
    java
    kotlin("jvm")
    id("maven-publish")
    id("com.google.protobuf") version "0.8.10"
    //id("org.jetbrains.dokka") version "0.10.0"
}

group = "wtf.votebot"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

val grpcVersion = "1.26.0"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.google.protobuf:protobuf-java:3.6.1")
    implementation("io.grpc:grpc-stub:$grpcVersion")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-netty:$grpcVersion")
    if (JavaVersion.current().isJava9Compatible) {
        implementation("javax.annotation:javax.annotation-api:1.3.1")
    }
}

sourceSets {
    main {
        java {
            srcDirs("build/generated/source/proto/main/grpc", "build/generated/source/proto/main/java")
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.6.1"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.15.1"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
            }
        }
    }
}

//task<Copy>("syncProtofiles") {
//    from(
//            "build/generated/source/proto/main/grpc/wtf/votebot/control_plane/lib",
//            "build/generated/source/proto/main/java/wtf/votebot/control_plane/lib"
//    )
//    into("src/main/java/wtf/votebot/control_plane/lib/proto/")
//    dependsOn("generateProto")
//}

val dokkaJar by tasks.creating(Jar::class)

val sourcesJar by tasks.creating(Jar::class)

tasks {
//        outputFormat = "html"
//        outputDirectory = "$buildDir/javadoc"
//
//        disableAutoconfiguration = false
//
//        configuration {
//            moduleName = "lib"
//
//            // Use to include or exclude non public members.
//            includeNonPublic = false
//
//            // Do not output deprecated members. Applies globally, can be overridden by packageOptions
//            skipDeprecated = false
//
//            // Emit warnings about not documented members. Applies globally, also can be overridden by packageOptions
//            reportUndocumented = true
//
//            // Do not create index pages for empty packages
//            skipEmptyPackages = true
//
//            // This is a list of platform names that will be shown in the final result. See the "Platforms" section of this readme
//            targets = listOf("JVM")
//
//            // Platform used for code analysis. See the "Platforms" section of this readme
//            platform = "JVM"
//
//            // Specifies the location of the project source code on the Web.
//            // If provided, Dokka generates "source" links for each declaration.
//            // Repeat for multiple mappings
//            sourceLink {
//                // Unix based directory relative path to the root of the project (where you execute gradle respectively).
//                path = "src/main/kotlin" // or simply "./"
//
//                // URL showing where the source code can be accessed through the web browser
//                url = "https://github.com/cy6erGn0m/vertx3-lang-kotlin/blob/master/src/main/kotlin" //remove src/main/kotlin if you use "./" above
//
//                // Suffix which is used to append the line number to the URL. Use #L for GitHub
//                lineSuffix = "#L"
//            }
//
//            // Used for linking to JDK documentation
//            jdkVersion = 11
//
//        }
//    }

    "sourcesJar"(Jar::class) {
        archiveClassifier.set("sources")
        destinationDirectory.set(buildDir)
        from(sourceSets["main"].allSource)
    }

//    "dokkaJar"(Jar::class) {
//        group = JavaBasePlugin.DOCUMENTATION_GROUP
//        archiveClassifier.set("javadoc")
//        destinationDirectory.set(buildDir)
//        from(dokka)
//    }

    "jar"(Jar::class) {
        destinationDirectory.set(buildDir)
    }

    task("buildArtifacts") {
        dependsOn(sourcesJar, dokkaJar, jar)
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/VoteBot/control-plane-service")
            credentials {
                username = System.getenv("USERNAME")
                password = System.getenv("PASSWORD")

            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
            artifact(sourcesJar)
            pom {
                groupId = "wtf.votebot.control_plane"
                artifactId = "lib"
            }
        }
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

