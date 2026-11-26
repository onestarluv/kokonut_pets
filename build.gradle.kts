plugins {
    java
}

group = "fr.nocsy"
description = "에고버스 펫 플러그인"

base {
    archivesName = "Kokonut_Pets"
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/releases")
}

dependencies {
    // 채팅 안내는 Core MessageUtil 로 보낸다.
    compileOnly(project(":Kokonut-Core"))
    compileOnly("org.projectlombok:lombok:1.18.44")
    annotationProcessor("org.projectlombok:lombok:1.18.44")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    compileOnly("io.papermc.paper:paper-api:26.3.build.231-stable")
    compileOnly("me.clip:placeholderapi:2.12.3")
    compileOnly("net.luckperms:api:5.5")

    compileOnly(files(
        providers.gradleProperty("mythicMobsJar").orElse("D:/egoverse/home1/plugins/MythicMobsPremium-5.14.0.jar"),
        providers.gradleProperty("nexoJar").orElse("D:/egoverse/home1/plugins/nexo-1.29.1.jar"),
        providers.gradleProperty("worldEditJar").orElse("D:/egoverse/home1/plugins/FastAsyncWorldEdit-Paper-2.16.1.jar"),
    ))

    val root = rootProject.layout.projectDirectory
    val model = root.dir("Kokonut_Model/api")
    compileOnly(files(
        model.file("build/libs/bettermodel-api-3.7.0.jar"),
        model.file("bukkit-api/build/libs/bettermodel-bukkit-api-3.7.0.jar"),
    ).builtBy(":buildModel"))
    // core/plugin 은 여러 작업이 JAR를 모으는 폴더라 Guard 자체 산출물을 참조한다.
    compileOnly(files(root.file("Kokonut_Guard/worldguard-bukkit/build/libs/Kokonut_Guard-cbt-1.0.jar")).builtBy(":buildGuard"))
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.processResources {
    val properties = mapOf("version" to project.version)
    inputs.properties(properties)
    filesMatching("plugin.yml") { expand(properties) }
}
