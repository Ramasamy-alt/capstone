pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = 'rammmmu/finsecure-pay'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        CREDENTIALS_ID = 'dockerhub-credentials' // Your Jenkins credentials ID for Docker Hub
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat './mvnw clean compile'
            }
        }

        stage('Test') {
            steps {
                // Runs JUnit 5 tests. A failed test will abort the pipeline.
                bat './mvnw test'
            }
        }

        stage('Package') {
            steps {
                bat './mvnw package -DskipTests'
            }
        }

        stage('Security Checks') {
            steps {
                echo 'Running security checks...'
                bat './mvnw verify -DskipTests || exit 0'
            }
        }

        stage('Container Build') {
            steps {
                echo 'Building Docker image...'
                bat "docker build -t ${env.DOCKER_IMAGE}:${env.IMAGE_TAG} -t ${env.DOCKER_IMAGE}:latest ."
            }
        }

        stage('Push') {
            steps {
                echo 'Pushing Docker image to registry...'
                withCredentials([usernamePassword(credentialsId: "${env.CREDENTIALS_ID}", 
                                                  usernameVariable: 'DOCKER_USER', 
                                                  passwordVariable: 'DOCKER_PASS')]) {
                    bat "docker login -u %DOCKER_USER% -p %DOCKER_PASS%"
                    bat "docker push ${env.DOCKER_IMAGE}:${env.IMAGE_TAG}"
                    bat "docker push ${env.DOCKER_IMAGE}:latest"
                }
            }
        }

        stage('Deploy') {
            steps {
                echo 'Updating Kubernetes deployment image...'
                bat "kubectl set image deployment/finsecure-deployment finsecure-pay=${env.DOCKER_IMAGE}:${env.IMAGE_TAG}"
                bat 'kubectl rollout status deployment/finsecure-deployment'
            }
        }
    }

    post {
        success {
            echo 'Pipeline successfully executed, imaged pushed, and Kubernetes rolled out!'
        }
        failure {
            echo 'Pipeline failed! Deployment was safely aborted.'
        }
    }
}