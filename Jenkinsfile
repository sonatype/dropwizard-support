@Library(['private-pipeline-library', 'jenkins-shared']) _

properties([
  disableConcurrentBuilds()
])

mavenSnapshotPipeline(
  mavenVersion: 'Maven 3.6.x',
  javaVersion: 'OpenJDK 11',
  mavenOptions: '-Dit -Dbuild.notes="b:${BRANCH_NAME}, j:${JOB_NAME}, n:#${BUILD_NUMBER}"',
  usePublicSettingsXmlFile: true,
  useEventSpy: false,
  deployCondition: {
    return gitBranch(env) in ['main', 'master', 'release-1.x']
  },
  testResults: [ '**/target/*-reports/*.xml' ],
  iqPolicyEvaluation: { stage ->
    nexusPolicyEvaluation iqStage: stage, iqApplication: 'dropwizard-support',
      iqScanPatterns: [[scanPattern: 'scan_nothing']]
  }
)
