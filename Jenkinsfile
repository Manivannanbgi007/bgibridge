pipeline {
  agent any

  tools {
    // Use the exact name from Manage Jenkins -> Global Tool Configuration
    maven 'maven-3.8.6'
  }

  environment {
    // Use the credentials ID you already have in Jenkins
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
          // If BRANCH_NAME is not provided (single-branch pipeline), fallback to 'local'
          def branch = env.BRANCH_NAME ?: 'local'
          // Prefer GIT_COMMIT injected by Jenkins; fall back to git if missing
          def shortSha = (env.GIT_COMMIT ? env.GIT_COMMIT.take(7) : sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim())
          if (branch == 'main' || branch == 'master') {
            env.IMAGE_TAG = 'latest'
          } else if (branch == 'local') {
            // for single-branch job runs, give a readable tag
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
        echo "🚀 Building and pushing Docker image with Jib..."

        // Use withCredentials to bind docker username/password to shell environment variables.
        // Use triple-single-quote + Groovy concatenation to avoid interpolating secret values in Groovy strings.
        withCredentials([usernamePassword(credentialsId: 'dockerhubpassword1', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
          // Compose the mvn command string safely
          def imageRef = IMAGE_NAME + ':' + env.IMAGE_TAG

          bat (
            '''mvn -B -DskipTests compile jib:build \
-Djib.to.image=''' + imageRef +
            ''' \
-Djib.to.auth.username=$DOCKER_USER \
-Djib.to.auth.password=$DOCKER_PASS'''
          )
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
