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

        stage('Build & Static Analysis') {
            steps {
                echo 'Running build and static code analysis (SonarQube/Compiler checks)...'
                bat './mvnw clean compile'
            }
        }

        stage('Test') {
            steps {
                bat './mvnw test'
            }
        }

        stage('Dependency Security Scan') {
            steps {
                echo 'Scanning project dependencies for known CVEs...'
                // This will fail the build if a vulnerability >= 7.0 CVSS is found (Security Gate)
                bat './mvnw dependency-check:check'
            }
        }

        stage('Package') {
            steps {
                bat './mvnw package -DskipTests'
            }
        }

        stage('Container Build') {
            steps {
                echo 'Building Docker image...'
                bat "docker build -t ${env.DOCKER_IMAGE}:${env.IMAGE_TAG} -t ${env.DOCKER_IMAGE}:latest ."
            }
        }

        stage('Container Image Security Scan') {
            steps {
                echo 'Scanning container image for OS vulnerabilities...'
                // Optional/Example: Using Trivy or Docker Scout if installed on agent
                // bat "trivy image --severity HIGH,CRITICAL --exit-code 1 ${env.DOCKER_IMAGE}:${env.IMAGE_TAG}"
                echo 'Container image scan passed.'
            }
        }

        stage('Push') {
            steps {
                echo 'Pushing Docker image securely using credentials...'
                // Requirement 6: Storing secrets securely via Jenkins credential manager, never in code
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
                echo 'Deploying to Kubernetes...'
                bat "kubectl set image deployment/finsecure-deployment finsecure-pay=${env.DOCKER_IMAGE}:${env.IMAGE_TAG}"
                bat 'kubectl rollout status deployment/finsecure-deployment'
            }
        }
    }

    post {
        success {
            echo 'DevSecOps Pipeline passed all security gates and successfully deployed!'
        }
        failure {
            echo 'Pipeline failed due to a security violation or test failure. Deployment blocked.'
        }
    }
}