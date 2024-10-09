import org.jetbrains.intellij.platform.gradle.tasks.aware.IntelliJPlatformVersionAware
import org.jetbrains.intellij.platform.gradle.utils.PlatformJavaVersions
import org.jetbrains.intellij.platform.gradle.utils.PlatformKotlinVersions
import org.jetbrains.intellij.platform.gradle.utils.Version
import java.io.ByteArrayInputStream
import java.util.Properties

plugins {
    //`kotlin-dsl`
    alias(libs.plugins.intelliJPlatformBase)
}

// hack to enable `libs.versions.toml` in precompiled script plugins
// see https://github.com/gradle/gradle/issues/15383
dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}


repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}


dependencies {
    intellijPlatform {
        val mainProperties = providers.fileContents(layout.projectDirectory.file("../gradle.properties")).asBytes.get().let { bytes -> Properties().apply { load(ByteArrayInputStream(bytes)) } }
        create(mainProperties.getProperty("platformType"), mainProperties.getProperty("platformVersion"))
    }
}


java {
    toolchain {
        languageVersion.set(
            provider { (tasks.matching { it is IntelliJPlatformVersionAware }.first() as IntelliJPlatformVersionAware).productInfo.buildNumber }
                .map(Version::parse)
                .map { buildNumber -> PlatformJavaVersions.entries.first { buildNumber >= it.key }.value }
                .map { javaVersion -> JavaLanguageVersion.of(javaVersion.majorVersion) }
        )
    }
}

dependencies {
    api(provider {
        val platformKotlinVersion = (tasks.matching { it is IntelliJPlatformVersionAware }.first() as IntelliJPlatformVersionAware).productInfo.buildNumber
            .let(Version::parse)
            .let { buildNumber -> PlatformKotlinVersions.entries.first { buildNumber >= it.key }.value.toString() }
        return@provider createPluginNotation("org.jetbrains.kotlin.jvm", platformKotlinVersion)
    })
}


fun createPluginNotation(pluginId: String, version: String) = "${pluginId}:${pluginId}.gradle.plugin:${version}"

val Provider<PluginDependency>.notation: Provider<String>
    get() = map { createPluginNotation(it.pluginId, it.versionString) }

val PluginDependency.versionString: String
    get() = version.run {
        requiredVersion.ifEmpty { null }
            ?: strictVersion.ifEmpty { null }
            ?: preferredVersion
    }

