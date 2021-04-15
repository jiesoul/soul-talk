pipeline {
  agent any
  stages {
    stage('api') {
      parallel {
        stage('api') {
          steps {
            dir(path: 'server')
            sh 'docker build -t soul-talk-api:latest .'
          }
        }

        stage('client-back') {
          steps {
            dir(path: 'client-back')
          }
        }

        stage('client') {
          steps {
            dir(path: 'client')
          }
        }

      }
    }

  }
}