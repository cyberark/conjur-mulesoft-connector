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
    env.ASSET_DIR=assetDirectory

    infrapool.agentSh """
      set -xuo pipefail
      git checkout "v${sourceVersion}"
      echo -n "${targetVersion}" > VERSION
      cp VERSION VERSION.original
      ./build_tool_image.sh
      ./build_package.sh
    """
    infrapool.agentSh "ASSET_DIR=\"${assetDirectory}\" summon ./publish.sh"
  }

  release.copyEnterpriseRelease(params.VERSION_TO_PROMOTE)
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

    stage('Conjur Mule test stage oss') {
      steps {
        script {
          //  infrapool.agentSh './test'
          //  infrapool.agentSh './start_oss'
          infrapool.agentSh './deploy_mule.sh'
          infrapool.agentSh './start_oss'
        }
      }
    }

    stage('Conjur Mule test stage enterprise') {
      steps {
        script {
          //  infrapool.agentSh './test'
          infrapool.agentSh './start_enterprise'
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

    stage('Report Test Coverage to Codacy'){
      steps {
        script {
          dir('target/site/jacoco'){
            infrapool.agentStash name: 'jacoco', includes: 'jacaco.xml'
            unstash 'jacoco'
            codacy action: 'reportCoverage', filePath: "jacoco.xml"
          }
          infrapool.agentStash name: 'target-site-jacoco', includes: 'target/site/jacoco/*.xml'
          unstash 'target-site-jacoco'
          publishHTML (target : [allowMissing: false,
            alwaysLinkToLastBuild: false,
            keepAll: true,
            reportDir: 'target/site/jacoco/',
            reportFiles: 'index.html',
            reportName: 'Coverage Report',
            reportTitles: 'Mulesoft Code Coverage Jacoco report'])
        }
      }
    }

    stage('Conjur Mule test stage cloud') {
      stages {
        stage('Create a Tenant') {
          steps {
            script {
              TENANT = getConjurCloudTenant()
            }
          }
        }
        stage('Authenticate') {
          steps {
            script {
              def id_token = getConjurCloudTenant.tokens(
                infrapool: infrapool,
                identity_url: "${TENANT.identity_information.idaptive_tenant_fqdn}",
                username: "${TENANT.login_name}"
              )

              def conj_token = getConjurCloudTenant.tokens(
                infrapool: infrapool,
                conjur_url: "${TENANT.conjur_cloud_url}",
                identity_token: "${id_token}"
                )

              env.conj_token = conj_token
            }
          }
        }
        stage('Run tests against Tenant') {
          environment {
            INFRAPOOL_CONJUR_APPLIANCE_URL="${TENANT.conjur_cloud_url}"
            INFRAPOOL_CONJUR_AUTHN_LOGIN="${TENANT.login_name}"
            INFRAPOOL_CONJUR_AUTHN_TOKEN="${env.conj_token}"
            INFRAPOOL_TEST_CLOUD=true
          }
          steps {
            script {
              infrapool.agentSh './start_mule'
            }
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
            infrapool.agentSh "ASSET_DIR=\"${assetDirectory}\" summon ./publish.sh"
          })
        }
      }
    }
  }
  post {
    always {
      script {
            deleteConjurCloudTenant("${TENANT.id}")
      }
      releaseInfraPoolAgent(".infrapool/release_agents")
    }
  }
}
