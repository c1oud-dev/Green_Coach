plugins {
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("jvm")           version "2.1.21"      // ← Spring/Kotlin 연동을 위해
	kotlin("plugin.spring") version "2.1.21"      // ← JPA용 open 클래스 지원
	kotlin("plugin.jpa")    version "2.1.21"
}

group = "com.greencoach"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webflux")      // WebClient
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")         // Kotlin JSON 처리
	implementation("org.jetbrains.kotlin:kotlin-reflect")                        // Spring/Kotlin 리플렉션
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.3")   // 코루틴 ⇄ 리액터 변환
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.0")   // Reactor Kotlin 확장
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	implementation("org.springframework.boot:spring-boot-starter-mail") // (이메일 실제 전송 시)

	compileOnly("org.projectlombok:lombok")

	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")

	runtimeOnly("com.mysql:mysql-connector-j")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	annotationProcessor("org.projectlombok:lombok")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.mockito.kotlin:mockito-kotlin:4.+")
	testImplementation("org.mockito:mockito-inline:5.+")
	testImplementation("io.projectreactor:reactor-test")

	testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
	testImplementation("io.projectreactor:reactor-test:3.6.4")
	testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
	testImplementation("org.jetbrains.kotlin:kotlin-test") // assertEquals 등

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<Test> {
	useJUnitPlatform()
}
