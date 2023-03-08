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
    if (env.APP_TYPE == "maven") {
      sh '''
        cp target/${COMPONENT}-1.0.jar ${COMPONENT}.jar
        zip -r ${COMPONENT}-${TAG_NAME}.zip ${COMPONENT}.jar
       '''
    }
    if (env.APP_TYPE == "python") {
      sh '''
        zip -r ${COMPONENT}-${TAG_NAME}.zip *.py ${COMPONENT}.ini requirements.txt
       '''
    }
    if (env.APP_TYPE == "nginx") {
      sh '''
        cd static
        zip -r ../${COMPONENT}-${TAG_NAME}.zip *
       '''
    }
  }

  stage('push Artifacts to nexus') {
    withCredentials([usernamePassword(credentialsId: 'NEXUSP', passwordVariable: 'pass', usernameVariable: 'user')]) {
      sh '''
        curl -v -u admin:admin123 --upload-file ${COMPONENT}-${TAG_NAME}.zip http://172.31.7.163:8081/repository/${COMPONENT}/${COMPONENT}-${TAG_NAME}.zip 
      '''
    }
  }
}

def codeChecks() {
  stage('Quality Checks & unit tests') {
    parallel([
      QualityChecks: {
        withCredentials([usernamePassword(credentialsId: 'SONAR', passwordVariable: 'pass', usernameVariable: 'user')]) {
           //sh "sonar-scanner -Dsonar.projectKey=${COMPONENT} -Dsonar.host.url=http://172.31.8.54:9000 -Dsonar.login=${user} -Dsonar.password=${pass} "
          //sh "sonar-quality-gate.sh ${user} ${pass} 172.31.8.54 ${COMPONENT}"
          echo "code Analysis"
        }
      },
      unitTests: {
        unitTests()
      }
    ])

  }
}

def unitTests() {
    if (env.APP_TYPE == "nodejs") {
      sh '''
        # npm run test
        echo Run test cases
       '''
    }
    if (env.APP_TYPE == "maven") {
      sh '''
        # mvn test
        echo Run test cases
       '''
    }
    if (env.APP_TYPE == "python") {
      sh '''
        # python -m unittest
        echo Run test Cases
       '''
    }
    if (env.APP_TYPE == "nginx") {
      sh '''
        # npm run test
        echo Run test cases
       '''
    }
}