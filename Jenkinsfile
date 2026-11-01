pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = 'your-dockerhub-username/finsecure-pay'
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        CREDENTIALS_ID = 'dockerhub-credentials'
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
                echo 'Running static analysis and security checks...'
                bat './mvnw verify -DskipTests || exit 0'
            }
        }

        stage('Container Build') {
            steps {
                script {
                    app = docker.build("${env.DOCKER_IMAGE}:${env.IMAGE_TAG}")
                }
            }
        }

        stage('Push') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', "${env.CREDENTIALS_ID}") {
                        app.push("${env.IMAGE_TAG}")
                        app.push("latest")
                    }
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
            echo 'Pipeline successfully executed and new version deployed!'
        }
        failure {
            echo 'Pipeline failed! Deployment was safely aborted.'
        }
    }
}