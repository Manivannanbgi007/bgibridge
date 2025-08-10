pipeline {
    agent any

    tools {
        maven 'Maven3'
    }

    environment {
        DOCKERHUB = credentials('dockerhub-credentials-id')
        IMAGE_NAME = "docker.io/manivannanbgi/bgibridge"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                echo "🔹 Running unit tests..."
                sh 'mvn -B clean verify'
            }
        }

        stage('Set Image Tag') {
            steps {
                script {
                    if (env.BRANCH_NAME == 'main' || env.BRANCH_NAME == 'master') {
                        env.IMAGE_TAG = 'latest'
                    } else {
                        def shortSha = env.GIT_COMMIT?.take(7) ?: sh(
                            script: 'git rev-parse --short HEAD',
                            returnStdout: true
                        ).trim()
                        env.IMAGE_TAG = "${env.BRANCH_NAME}-${shortSha}"
                    }
                }
                echo "📌 Using image tag: ${env.IMAGE_TAG}"
            }
        }

        stage('Build & Push with Jib') {
            steps {
                echo "🚀 Building and pushing Docker image with Jib..."
                sh """
                    mvn -B -DskipTests compile jib:build \
                        -Djib.to.image=${IMAGE_NAME}:${IMAGE_TAG} \
                        -Djib.to.auth.username=${DOCKERHUB_USR} \
                        -Djib.to.auth.password=${DOCKERHUB_PSW}
                """
            }
        }
    }

    post {
        success {
            echo "✅ Build and push successful: ${IMAGE_NAME}:${IMAGE_TAG}"
        }
        failure {
            echo "❌ Build failed!"
        }
    }
}
