pipeline {
    agent any

    environment {
        DOCKERHUB_USER = credentials('manivannanbgi') // Jenkins credential ID
        DOCKERHUB_PASS = credentials('Bgi@5555') // Jenkins credential ID
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/your/repo.git'
            }
        }

        stage('Build & Push Docker Image') {
            steps {
                sh """
                ./mvnw compile jib:build \
                    -Djib.to.auth.username=$DOCKERHUB_USER \
                    -Djib.to.auth.password=$DOCKERHUB_PASS
                """
            }
        }
    }

    post {
        success {
            echo 'Docker image built & pushed successfully!'
        }
        failure {
            echo 'Build failed!'
        }
    }
}
