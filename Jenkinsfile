@Library(['private-pipeline-library', 'jenkins-shared']) _

mavenSnapshotPipeline(
  mavenVersion: 'Maven 3.6.x',
  javaVersion: 'Java 8',
  mavenOptions: '-Dit -Dbuild.notes="b:${BRANCH_NAME}, j:${JOB_NAME}, n:#${BUILD_NUMBER}"',
  usePublicSettingsXmlFile: true,
  useEventSpy: false,
  testResults: [ '**/target/*-reports/*.xml' ],
  iqPolicyEvaluation: { stage ->
    nexusPolicyEvaluation iqStage: stage, iqApplication: 'goodies',
      iqScanPatterns: [[scanPattern: 'scan_nothing']]
  }
)
