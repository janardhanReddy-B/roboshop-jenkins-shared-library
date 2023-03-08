def call() {
  node() {

    common.pipelineinit()

    common.codeChecks()

    if( env.BRANCH_NAME == env.TAG_NAME )
    {
      common.publishArtifacts()

    }

  }
}