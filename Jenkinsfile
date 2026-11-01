pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = 'rammmmu/finsecure-pay'
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
                sh './mvnw clean compile'
            }
        }

        stage('Test') {
            steps {
                // Runs JUnit 5 tests.If any test fails, pipeline stops here.
                sh './mvnw test'
            }
        }

        stage('Package') {
            steps {
                sh './mvnw package -DskipTests'
            }
        }

        stage('Security Checks') {
            steps {
                echo 'Running static analysis and security checks...'
                // Non-blocking security check or plug your SonarQube/Trivy scanner here
                sh './mvnw verify -DskipTests || true'
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
                sh "kubectl set image deployment/finsecure-deployment finsecure-pay=${env.DOCKER_IMAGE}:${env.IMAGE_TAG}"
                sh 'kubectl rollout status deployment/finsecure-deployment'
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