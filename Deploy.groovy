pipeline {
    agent any
    environment {
        AWS_REGION = 'ap-south-1' // Replace with your AWS region
        ECR_REPO = 'public.ecr.aws/v2k5k1u2/mynodeapp' // Replace with your ECR repository URI
        EC2_HOST = '13.201.69.101' // Replace with your EC2 public IP or hostname
        DOCKER_IMAGE = 'latest' // Replace with your Docker image tag
        SSH_KEY_CREDENTIALS = 'ec2-ssh-key' // Jenkins credentials ID for the EC2 SSH private key
    }
    stages {
        stage('Pull Docker Image on EC2') {
            steps {
                sshagent(credentials: [SSH_KEY_CREDENTIALS]) {
                    script {
                        sh '''
                        echo "Connecting to EC2 and pulling Docker image from ECR..."
                        
                        ssh -o StrictHostKeyChecking=no ${EC2_HOST} << EOF
                        # Login to AWS ECR
                        aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REPO}
                        
                        # Pull the Docker image
                        docker pull ${ECR_REPO}:${DOCKER_IMAGE}

                        # Stop the currently running container (if any)
                        docker stop my-app || true && docker rm my-app || true

                        # Run the new container
                        docker run -d --name my-app -p 5050:5050 ${ECR_REPO}:${DOCKER_IMAGE}
                        EOF
                        '''
                    }
                }
            }
        }
    }
    post {
        success {
            echo 'Docker image deployed successfully on EC2!'
        }
        failure {
            echo 'Failed to deploy Docker image on EC2.'
        }
    }
}
