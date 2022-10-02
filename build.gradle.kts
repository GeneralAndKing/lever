plugins {
  idea
  id("java")
  id("io.spring.dependency-management") version "1.0.13.RELEASE"
  id("org.springframework.boot") version "3.0.0-M5"
  id("org.asciidoctor.jvm.convert") version "3.3.2"
}

group = "wiki.lever"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

repositories {
  maven { url = uri("https://maven.aliyun.com/nexus/content/groups/public/") }
  maven { url = uri("https://repo.spring.io/milestone") }
  mavenCentral()
}

val asciidoctorExtensions: Configuration by configurations.creating
val testcontainersVersion by extra { "1.17.3" }
val snippetsDir by extra { file("build/generated-snippets") }
extra["snippetsDir"] = file("build/generated-snippets")

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//    implementation("org.springframework.boot:spring-boot-starter-data-redis")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  implementation("org.apache.commons:commons-lang3")
  implementation("org.apache.commons:commons-collections4:4.4")
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
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("org.springframework.restdocs:spring-restdocs-restassured")
  testImplementation("org.testcontainers:junit-jupiter")
  testImplementation("org.testcontainers:mysql")
}

dependencyManagement {
  imports {
    mavenBom("org.testcontainers:testcontainers-bom:${testcontainersVersion}")
  }
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