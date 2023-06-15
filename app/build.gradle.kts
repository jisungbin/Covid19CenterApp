/*
 * Designed and developed by Ji Sungbin 2023.
 *
 * Licensed under the MIT.
 * Please see full license: https://github.com/jisungbin/Covid19CenterApp/blob/main/LICENSE
 */

@file:Suppress("INLINE_FROM_HIGHER_PLATFORM", "UnstableApiUsage")

import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("android") version libs.versions.kotlin.core
  id("com.android.application") version libs.versions.gradle.android
  alias(libs.plugins.kotlin.detekt)
  alias(libs.plugins.kotlin.ktlint)
  alias(libs.plugins.gradle.dependency.handler.extensions)
}

android {
  namespace = "io.github.jisungbin.covid19center"
  compileSdk = 33

  defaultConfig {
    minSdk = 24
    targetSdk = 33
    versionCode = 1
    versionName = "1.0.0"
  }

  buildFeatures {
    compose = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
  }

  sourceSets {
    getByName("main").java.srcDir("src/main/kotlin")
    getByName("test").java.srcDir("src/test/kotlin")
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }

  lint {
    checkTestSources = true
  }

  testOptions {
    unitTests.all { test ->
      test.useJUnitPlatform()

      if (!test.name.contains("debug", ignoreCase = true)) {
        test.enabled = false
      }
    }
  }

  kotlin {
    jvmToolchain(17)
  }
}

detekt {
  parallel = true
  buildUponDefaultConfig = true
  toolVersion = libs.versions.kotlin.detekt.get()
  config.setFrom(files("$rootDir/detekt-config.yml"))
}

ktlint {
  version.set(rootProject.libs.versions.kotlin.ktlint.source.get())
  android.set(true)
  verbose.set(true)
}

tasks.withType<Test>().configureEach {
  // https://stackoverflow.com/a/36178581/14299073
  outputs.upToDateWhen { false }
  testLogging {
    events = setOf(
      TestLogEvent.PASSED,
      TestLogEvent.SKIPPED,
      TestLogEvent.FAILED,
    )
  }
  afterSuite(
    KotlinClosure2<TestDescriptor, TestResult, Unit>({ desc, result ->
      if (desc.parent == null) { // will match the outermost suite
        val output = "Results: ${result.resultType} " +
          "(${result.testCount} tests, " +
          "${result.successfulTestCount} passed, " +
          "${result.failedTestCount} failed, " +
          "${result.skippedTestCount} skipped)"
        println(output)
      }
    })
  )
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = freeCompilerArgs + listOf(
      "-opt-in=kotlin.OptIn",
      "-opt-in=kotlin.RequiresOptIn",
    )
    freeCompilerArgs = freeCompilerArgs + listOf(
      "-P",
      "plugin:androidx.compose.compiler.plugins.kotlin:liveLiteralsEnabled=false",
    )
  }
}

dependencies {
  implementations(
    libs.compose.uiutil,
    libs.compose.activity,
    libs.bundles.compose,
  )
  detektPlugins(libs.detekt.plugin.formatting)
}
