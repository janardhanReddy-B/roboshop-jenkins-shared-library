def pipelineinit() {
  stage('Initiate Repo') {
    sh 'rm -rf *'
    git branch: 'main', url: "https://github.com/janardhanReddy-B/${COMPONENT}.git"
  }

}

def publishArtifacts() {
  stage('prepare Artifacts') {
    if (env.APP_TYPE == "nodejs") {

      sh '''
        zip -r ${COMPONENT}-${TAG_NAME}.zip node_modules server.js
       '''
    }
  }
}


