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

  stage ('push Artifacts to nexus') {
    withCredentials([usernamePassword(credentialsId: 'NEXUSP', passwordVariable: 'pass', usernameVariable: 'user')]) {
      sh '''
        curl -v -u ${user}:${pass} --upload-file ${COMPONENT}-${TAG_NAME}.zip http://172.31.7.163:8081/repository/${COMPONENT}/${COMPONENT}-${TAG_NAME}.zip 
      '''
    }
  }
}


