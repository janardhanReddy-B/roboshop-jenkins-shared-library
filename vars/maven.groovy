def call() {
  node() {

    common.pipelineinit()

    stage('Build package') {
      sh '''
        mvn clean package
      '''
    }
    common.codeChecks()

    if( env.BRANCH_NAME == env.TAG_NAME )
    {
      common.publishArtifacts()

    }

  }
}