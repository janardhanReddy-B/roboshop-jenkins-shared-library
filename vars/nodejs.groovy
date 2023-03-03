def call() {
  node() {

    common.pipelineinit()

    stage('Download Dependencies') {
      sh '''
        ls -ltr
        npm install
      '''
    }

  }
}