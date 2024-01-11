#!/usr/bin/env groovy
@Library("product-pipelines-shared-library") _
// Automated release, promotion and dependencies
properties([
  // Include the automated release parameters for the build
  release.addParams(),
  // Dependencies of the project that should trigger builds
  dependencies([])
])

// Performs release promotion.  No other stages will be run
if (params.MODE == "PROMOTE") {
  release.promote(params.VERSION_TO_PROMOTE) { infrapool, sourceVersion, targetVersion, assetDirectory ->
    // Any assets from sourceVersion Github release are available in assetDirectory
    // Any version number updates from sourceVersion to targetVersion occur here
    // Any publishing of targetVersion artifacts occur here
    // Anything added to assetDirectory will be attached to the Github Release

    //Note: assetDirectory is on the infrapool agent, not the local Jenkins agent.
    // Pass assetDirectory through to publish.sh as an env var.
   
  }
  return
}

pipeline {
  agent { label 'conjur-enterprise-common-agent' }

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
    stage ("Skip build if triggering job didn't create a release") {
      when {
        expression {
          MODE == "SKIP"
        }
      }
      steps {
        script {
          currentBuild.result = 'ABORTED'
          error("Aborting build because this build was triggered from upstream, but no release was built")
        }
      }
    }

    stage('Get InfraPool ExecutorV2 Agent(s)') {
      steps{
        script {
          // Request ExecutorV2 agents for 1 hour(s)
          INFRAPOOL_EXECUTORV2_AGENTS = getInfraPoolAgent(type: "ExecutorV2", quantity: 1, duration: 1)
          INFRAPOOL_EXECUTORV2_AGENT_0 = INFRAPOOL_EXECUTORV2_AGENTS[0]
          infrapool = infraPoolConnect(INFRAPOOL_EXECUTORV2_AGENT_0, {})
        }
      }
    }
   
    stage('Update conjur-intro submodule is checked out') {
      steps {
          sh 'git submodule update --init --recursive'
      }
    }
    // Generates a VERSION file based on the current build number and latest version in CHANGELOG.md
    stage('Validate Changelog and set version') {
      steps {
        script {
          updateVersion(infrapool, "CHANGELOG.md","${BUILD_NUMBER}")
        }
      }
    }
    stage('Mule Build') {
      steps {
        script {
          infrapool.agentSh './build_tool_image.sh'
          infrapool.agentSh './build_package.sh'
        }
      }
    }

    stage('docker image conjur-mule-image') {
      steps {
        script {
          infrapool.agentSh './deploy_mule.sh'
          infrapool.agentSh 'summon ./start_mule'
        }
      }
    }

    stage('Conjur Mule test stage enterprise') {
      steps {
        script {
          //  infrapool.agentSh './test'
          //  infrapool.agentSh './start_oss'
          infrapool.agentSh './start_enterprise'
        }
      }
    }

    stage('Conjur Mule test stage oss') {
      steps {
        script {
          //  infrapool.agentSh './test'
          infrapool.agentSh './start_oss'
        }
      }
      post {
        always {
          script {
            infrapool.agentArchiveArtifacts artifacts: 'target/*.jar'
            infrapool.agentStash name: 'surefire-reports-oss', includes: 'target/surefire-reports/*.xml'
            unstash 'surefire-reports-oss'
            junit 'target/surefire-reports/*.xml'
          }
        }
      }
    }

    stage('Release') {
      when {
        expression {
          MODE == "RELEASE"
        }
      }

      steps {
        script {
          release(infrapool, { billOfMaterialsDirectory, assetDirectory ->
            // Publish release artifacts to all the appropriate locations
            // Copy any artifacts to assetDirectory to attach them to the Github release
            infrapool.agentSh "mkdir -p target; cp target/*.jar ${assetDirectory}"
           //INFRAPOOL_EXECUTORV2_AGENT_0.agentSh "ASSET_DIR=\"${assetDirectory}\" summon ./publish.sh"
          })
        }
      }
    }
  }
  post {
    always {
      releaseInfraPoolAgent()
    }
  }
}
