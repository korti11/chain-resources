pipeline {
    agent {
        docker {
            image 'gradle:jdk8'
            args '-v dependencies:/home/gradle/.gradle'
        }
    }

    environment {
        GITHUB_CREDS = credentials('github')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build'
            }
        }

        stage('Maven publish') {
            steps {
                sh './gradlew publish'
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'build/libs/**/*.jar', fingerprint: true
        }
    }

}