def call() {
  node () {
    ansiColor('xterm') {
      stage('Terraform INIT') {
        sh "terraform init"
      }

      stage('Terrafrom plan') {
        sh "terraform plan"
      }

      stage('Terrafrom Apply') {
        sh "terraform apply -auto-approve"
      }
    }
  }
}