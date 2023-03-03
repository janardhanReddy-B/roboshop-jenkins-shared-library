def call() {
  node() {

    common.pipelineinit()

    stage('Build package') {
      sh '''
        mvn clean package
      '''
    }

  }
}