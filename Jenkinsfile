#!/usr/bin/env groovy
@Library('product-pipelines-shared-library') _
// Automated release, promotion and dependencies
properties([
  // Include the automated release parameters for the build
  release.addParams(),
  // Dependencies of the project that should trigger builds
  dependencies([])
])

// Performs release promotion.  No other stages will be run
if (params.MODE == 'PROMOTE') {
  release.promote(params.VERSION_TO_PROMOTE) { sourceVersion, targetVersion, assetDirectory ->
    // Any assets from sourceVersion Github release are available in assetDirectory
    // Any version number updates from sourceVersion to targetVersion occur here
    // Any publishing of targetVersion artifacts occur here
    // Anything added to assetDirectory will be attached to the Github Release

    sh """
      set -xuo pipefail
      git checkout "v${sourceVersion}"
      echo -n "${targetVersion}" > VERSION
      cp VERSION VERSION.original
      ./build_tool_image.sh
      ./build_package.sh
    """
    sh "ASSET_DIR=\"${assetDirectory}\" summon ./publish.sh"
  }

  release.copyEnterpriseRelease(params.VERSION_TO_PROMOTE)
  return
}

pipeline {
  agent { label 'conjur-enterprise-AmznDocker' }

  options {
    timestamps()
    buildDiscarder(logRotator(numToKeepStr: '30'))
  }

  triggers {
    cron(getDailyCronString())
  }

  environment {
    // Sets the MODE to the specified or autocalculated value as appropriate
    MODE = release.canonicalizeMode()
  }

  stages {
    // Aborts any builds triggered by another project that wouldn't include any changes
    stage("Skip build if triggering job didn't create a release") {
      when {
        expression {
          MODE == 'SKIP'
        }
      }
      steps {
        script {
          currentBuild.result = 'ABORTED'
          error('Aborting build because this build was triggered from upstream, but no release was built')
        }
      }
    }

    // Generates a VERSION file based on the current build number and latest version in CHANGELOG.md
    stage('Validate Changelog and set version') {
      steps {
        script {
          updateVersion('CHANGELOG.md', "${BUILD_NUMBER}")
        }
      }
    }

    stage('Mule Build') {
      steps {
        script {
          sh './build_tool_image.sh'
          sh './build_package.sh'
        }
      }
    }

    stage('Run Unit Tests & Report Coverage') {
      steps {
        script {
          sh './run_unit_tests.sh'
        }
      }
      post {
        always {
          script {
            archiveArtifacts artifacts: 'target/*.jar'
            junit 'target/surefire-reports/*.xml'
          }
        }
      }
    }

    stage('Release') {
      when {
        expression {
          MODE == 'RELEASE'
        }
      }

      steps {
        script {
          release({ billOfMaterialsDirectory, assetDirectory ->
            // Publish release artifacts to all the appropriate locations
            // Copy any artifacts to assetDirectory to attach them to the Github release
            sh "ASSET_DIR=\"${assetDirectory}\" summon ./publish.sh"
          })
        }
      }
    }
  }
}