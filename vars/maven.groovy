def call() {
  node() {

    common.pipelineinit()

    stage('Build package') {
      sh '''
        mvn clean package
      '''
    }

    if( env.BRANCH_NAME == env.TAG_NAME )
    {
      common.publishArtifacts()

    }

  }
}