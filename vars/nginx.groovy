def call() {
  node() {

    common.pipelineinit()


    if( env.BRANCH_NAME == env.TAG_NAME )
    {
      common.publishArtifacts()

    }

  }
}