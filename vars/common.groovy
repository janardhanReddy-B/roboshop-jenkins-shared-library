def pipelineinit() {
  stage('Initiate Repo') {
    sh 'rm -rf *'
    git branch: 'main', url: 'https://github.com/janardhanReddy-B/cart.git'
  }

}