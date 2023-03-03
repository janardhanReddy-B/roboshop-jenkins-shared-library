def call() {
  node() {

    stage ('Download Dependencies') {
      sh '''
        ls -l
        npm install
      '''
    }

  }
}