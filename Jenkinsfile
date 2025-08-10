pipeline {
  agent any

  tools {
    maven 'maven-3.8.6' // Matches Jenkins Maven tool name
  }

  environment {
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
        echo "🔹 Running Maven build & tests..."
        bat 'mvn -B clean verify'
      }
    }

    stage('Set Image Tag') {
      steps {
        script {
          def branch = env.BRANCH_NAME ?: 'local'
          def shortSha = (env.GIT_COMMIT ? env.GIT_COMMIT.take(7) :
                          bat(script: 'git rev-parse --short HEAD', returnStdout: true).trim())

          if (branch == 'main' || branch == 'master') {
            env.IMAGE_TAG = 'latest'
          } else if (branch == 'local') {
            env.IMAGE_TAG = "local-${shortSha}"
          } else {
            env.IMAGE_TAG = "${branch}-${shortSha}"
          }
        }
        echo "📌 Using image tag: ${env.IMAGE_TAG}"
      }
    }

    stage('Build & Push with Jib') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerhubpassword1', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
          script {
            def imageRef = "${IMAGE_NAME}:${env.IMAGE_TAG}"
            bat """
              mvn -B -DskipTests compile jib:build \
                -Djib.to.image=${imageRef} \
                -Djib.to.auth.username=${DOCKER_USER} \
                -Djib.to.auth.password=${DOCKER_PASS}
            """
          }
        }
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
