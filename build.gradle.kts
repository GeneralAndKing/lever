import org.jetbrains.gradle.ext.compiler
import org.jetbrains.gradle.ext.settings

plugins {
  idea
  id("java")
  id("io.spring.dependency-management") version "1.1.0"
  id("org.springframework.boot") version "3.0.0"
  id("org.asciidoctor.jvm.convert") version "3.3.2"
  id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.7"
}

group = "wiki.lever"
version = "1.0-SNAPSHOT"
java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}
idea {
  project {
    settings {
      compiler {
        javac {
          javacAdditionalOptions += "-parameters"
        }
      }
    }
  }
}

repositories {
  mavenCentral()
  maven { url = uri("https://maven.aliyun.com/nexus/content/groups/public/") }
  maven { url = uri("https://repo.spring.io/milestone") }
  google()
}

val asciidoctorExtensions: Configuration by configurations.creating
val testcontainersVersion by extra { "1.17.6" }
val jsonSchemaVersion by extra { "4.28.0" }
val snippetsDir by extra { file("build/generated-snippets") }
extra["snippetsDir"] = file("build/generated-snippets")

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-cache")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-data-redis")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  implementation("jakarta.validation:jakarta.validation-api")
  implementation("org.apache.commons:commons-lang3")
  implementation("org.apache.commons:commons-collections4:4.4")
  implementation("com.github.victools:jsonschema-generator:$jsonSchemaVersion")
  implementation("com.github.victools:jsonschema-module-jackson:$jsonSchemaVersion")
  implementation("com.github.victools:jsonschema-module-jakarta-validation:$jsonSchemaVersion")
  implementation(group = "com.querydsl", name = "querydsl-jpa", classifier = "jakarta")
  compileOnly("org.projectlombok:lombok")
  developmentOnly("org.springframework.boot:spring-boot-devtools")
  runtimeOnly("mysql:mysql-connector-java")
  asciidoctorExtensions("org.springframework.restdocs:spring-restdocs-asciidoctor")
  annotationProcessor(
    group = "com.querydsl", name = "querydsl-apt", classifier = "jakarta"
  )
  annotationProcessor("org.projectlombok:lombok")
  annotationProcessor("jakarta.persistence:jakarta.persistence-api")
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
  testCompileOnly("org.projectlombok:lombok")
  testAnnotationProcessor("org.projectlombok:lombok")
  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("org.springframework.restdocs:spring-restdocs-restassured")
  testImplementation("org.testcontainers:mysql")
}

dependencyManagement {
  imports {
    mavenBom("org.testcontainers:testcontainers-bom:${testcontainersVersion}")
  }
}


tasks.withType<JavaCompile> {
  options.compilerArgs.addAll(listOf("-parameters"))
}

tasks.test {
  doFirst { delete(snippetsDir) }
  outputs.dir(snippetsDir)
  useJUnitPlatform()
}

// https://github.com/spring-io/start.spring.io/issues/676#issuecomment-859641317
tasks.asciidoctor {
  doFirst {
    delete(outputDir)
    copy {
      from(snippetsDir)
      into(sourceDir)
    }
  }
  dependsOn(tasks.test)
  configurations(asciidoctorExtensions.name)
  setOutputDir(file("src/main/resources/static/docs"))
  attributes(
    mapOf(
      "snippets" to snippetsDir,
      "source-highlighter" to "highlight.js"
    )
  )
}

tasks.bootJar {
  dependsOn(tasks.asciidoctor)
  dependsOn(tasks.withType<Copy>())
}