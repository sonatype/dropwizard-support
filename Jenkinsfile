#!groovy
library('private-pipeline-library')
library('jenkins-shared')

// only deploy on specific branches
def deployBranches = [ 'main' ]
// include release-*.x branches for deploy
if (BRANCH_NAME.startsWith('release-') && BRANCH_NAME.endsWith('.x')) {
  deployBranches << BRANCH_NAME
}

def propertyList = [
  parameters([
    booleanParam(name: 'testsuite', defaultValue: false, description: 'Enable integration testsuite'),
    booleanParam(name: 'policy', defaultValue: false, description: 'Enable IQ policy evaluation')
  ])
]

// disable concurrent builds for any deploy branches
if (BRANCH_NAME in deployBranches) {
  propertyList << disableConcurrentBuilds()
}

properties(propertyList)

def mavenOptions = [
  "-Dbuild.notes='b:${BRANCH_NAME}, j:${JOB_NAME}, n:#${BUILD_NUMBER}'"
]

def mavenProfiles = [
  'zion'
]
if (params.testsuite) {
  mavenProfiles << 'it'
}

mavenPipeline(
  javaVersion: 'OpenJDK 11',
  useMvnw: true,
  mavenStandardOptions: '--errors --strict-checksums --fail-fast -Dmaven.test.failure.ignore',
  usePublicSettingsXmlFile: true,
  useEventSpy: false,
  deployCondition: { BRANCH_NAME in deployBranches },
  mavenOptions: mavenOptions.join(' '),
  mavenProfiles: mavenProfiles,
  testResults: ['**/target/*-reports/*.xml'],
  runFeatureBranchPolicyEvaluations: true,
  iqPolicyEvaluation: { stage ->
    if (params.policy) {
      nexusPolicyEvaluation(
        iqStage: stage,
        iqApplication: 'dropwizard-support',
        failBuildOnNetworkError: true,
        iqScanPatterns: [
          [scanPattern: '**/target/sonatype-clm/module.xml']
        ]
      )
    }
  },
  notificationSender: {
    notifyChat(currentBuild: currentBuild, env: env, room: 'ossindex-alerts')
  }
)
