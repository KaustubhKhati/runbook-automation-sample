plugins {
	java
	id("org.springframework.boot") version "3.5.6"
	id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
}

group = "io.github.kaustubhkhati"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring AI, Langraph for Runbook Automation"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.3.3")
        mavenBom("org.springframework.ai:spring-ai-bom:1.0.0-M6")
        mavenBom("org.bsc.langgraph4j:langgraph4j-bom:1.6.4")
    }
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("com.zaxxer:HikariCP:5.1.0")

    // JDBC
    implementation("org.xerial:sqlite-jdbc:3.45.2.0")

    // Spring AI with Azure OpenAI
    implementation("org.springframework.ai:spring-ai-azure-openai-spring-boot-starter")
    implementation("org.springframework.ai:spring-ai-core")
    implementation("org.springframework.ai:spring-ai-azure-openai")
    implementation("org.springframework.ai:spring-ai-qdrant-store")

    // LangGraph4j (keep for your graph execution logic)
    implementation("org.bsc.langgraph4j:langgraph4j-core")

    // YAML support
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")

    // Observability
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.apache.commons:commons-collections4:4.5.0")
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")

    // Condition (MVEL)
//    implementation("org.mvel:mvel2:2.5.2.Final")

    //Utils
    implementation("org.commonmark:commonmark:0.26.0")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.4.4")
}


tasks.withType<Test> {
	useJUnitPlatform()
}
