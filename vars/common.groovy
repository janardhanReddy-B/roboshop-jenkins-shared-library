def pipelineinit() {
  stage('Initiate Repo') {
    sh 'rm -rf *'
    git branch: 'main', url: "https://github.com/janardhanReddy-B/${COMPONENT}.git"
  }

}

def publishArtifacts() {
  env.ENV ="dev"
  stage('prepare Artifacts') {
    if (env.APP_TYPE == "nodejs") {
      sh '''
        zip -r ${ENV}-${COMPONENT}-${TAG_NAME}.zip node_modules server.js
       '''
    }
    if (env.APP_TYPE == "maven") {
      sh '''
        cp target/${COMPONENT}-1.0.jar ${COMPONENT}.jar
        zip -r ${ENV}-${COMPONENT}-${TAG_NAME}.zip ${COMPONENT}.jar
       '''
    }
    if (env.APP_TYPE == "python") {
      sh '''
        zip -r ${ENV}-${COMPONENT}-${TAG_NAME}.zip *.py ${COMPONENT}.ini requirements.txt
       '''
    }
    if (env.APP_TYPE == "nginx") {
      sh '''
        cd static
        zip -r ../${ENV}-${COMPONENT}-${TAG_NAME}.zip *
       '''
    }
  }

  stage('push Artifacts to nexus') {
    withCredentials([usernamePassword(credentialsId: 'NEXUSP', passwordVariable: 'pass', usernameVariable: 'user')]) {
      sh """
        curl -v -u admin:admin123 --upload-file ${ENV}-${COMPONENT}-${TAG_NAME}.zip http://172.31.7.163:8081/repository/${COMPONENT}/${ENV}-${COMPONENT}-${TAG_NAME}.zip 
      """
    }
  }
  stage(' Deploy to Dev Env')
  build job: 'deploy-to-any-env', parameters: [string(name: 'COMPONENT', value: "${COMPONENT}"), string(name: 'ENV', value: "${ENV}"), string(name: 'APP_VERSION', value: "${TAG_NAME}")]

  stage('Run Somke Tests') {
    sh "echo Somke Tests"
  }

  promoteRelease("dev","qa")
}

def promoteRelease(SOURCE_ENV,ENV) {
  withCredentials([usernamePassword(credentialsId: 'NEXUS', passwordVariable: 'pass', usernameVariable: 'user')]) {
    sh """
      cp ${SOURCE_ENV}-${COMPONENT}-${TAG_NAME}.zip ${ENV}-${COMPONENT}-${TAG_NAME}.zip
      curl -v -u ${user}:${pass} --upload-file ${ENV}-${COMPONENT}-${TAG_NAME}.zip http://172.31.7.163:8081/repository/${COMPONENT}/${ENV}-${COMPONENT}-${TAG_NAME}.zip 
      """
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