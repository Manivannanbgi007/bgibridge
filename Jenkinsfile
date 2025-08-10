pipeline {
    agent any

    tools {
        maven 'maven-3.8.6'
    }

    environment {
        DOCKERHUB = credentials('dockerhubpassword1')
        IMAGE_NAME = "https://hub.docker.com/manivannanbgi/bgibridge"
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
                bat 'mvn -B clean verify'
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
                bat """
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
