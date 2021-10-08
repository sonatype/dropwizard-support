@Library(['private-pipeline-library', 'jenkins-shared']) _

Set<String> deployBranches = ['main', 'master', 'release-1.x']

def prepare = {
  // branch-name is encoded in multibranch-pipeline job names as the last element seperated by '/'
  def branchName = currentBuild.fullProjectName.split('/').last()

  def propertyList = []

  // disable concurrent builds for deploy branches
  if (branchName in deployBranches) {
    propertyList << disableConcurrentBuilds()
  }

  properties(propertyList)
}

prepare()

mavenSnapshotPipeline(
  mavenVersion: 'Maven 3.6.x',
  javaVersion: 'OpenJDK 11',
  mavenOptions: '-Dit -Dbuild.notes="b:${BRANCH_NAME}, j:${JOB_NAME}, n:#${BUILD_NUMBER}"',
  usePublicSettingsXmlFile: true,
  useEventSpy: false,
  deployCondition: { return gitBranch(env) in deployBranches },
  testResults: [ '**/target/*-reports/*.xml' ],
  iqPolicyEvaluation: { stage ->
    nexusPolicyEvaluation iqStage: stage, iqApplication: 'dropwizard-support',
      iqScanPatterns: [[scanPattern: 'scan_nothing']]
  }
)
