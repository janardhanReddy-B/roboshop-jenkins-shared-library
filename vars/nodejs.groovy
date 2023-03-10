def call() {
  node() {

    common.pipelineinit()

    stage('Download Dependencies') {
      sh '''
        ls -ltr
        npm install
      '''
    }

    common.codeChecks()

    if( env.BRANCH_NAME == env.TAG_NAME )
    {
      common.publishArtifacts()

    }

  }
}