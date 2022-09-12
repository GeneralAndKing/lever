plugins {
  idea
  id("java")
  id("io.spring.dependency-management") version "1.0.13.RELEASE"
  id("org.springframework.boot") version "3.0.0-M4"
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
  mavenCentral()
  maven { url = uri("https://repo.spring.io/milestone") }
}

val testcontainersVersion by extra { "1.17.3" }

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//    implementation("org.springframework.boot:spring-boot-starter-data-redis")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  implementation("org.apache.commons:commons-lang3")
  implementation(group = "com.querydsl", name = "querydsl-jpa", classifier = "jakarta")
  compileOnly("org.projectlombok:lombok")
  developmentOnly("org.springframework.boot:spring-boot-devtools")
  runtimeOnly("mysql:mysql-connector-java")
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
  testImplementation("org.testcontainers:junit-jupiter")
  testImplementation("org.testcontainers:mysql")
}

dependencyManagement {
  imports {
    mavenBom("org.testcontainers:testcontainers-bom:${testcontainersVersion}")
  }
}

tasks.getByName<Test>("test") {
  useJUnitPlatform()
}