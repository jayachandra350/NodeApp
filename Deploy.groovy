pipeline {
    agent any
    environment {
        AWS_REGION = 'ap-south-1' // Replace with your AWS region
        ECR_REPO = 'public.ecr.aws/v2k5k1u2/mynodeapp' // Replace with your ECR repository URI
        EC2_HOST = '13.201.69.101' // Replace with your EC2 public IP or hostname
        //DOCKER_TAG = 'latest' // Replace with your Docker image tag
       // SSH_KEY_CREDENTIALS = 'ec2-ssh-key' // Jenkins credentials ID for the EC2 SSH private key
    }
    stages {
        stage('Deploy Docker Image on EC2') {
            steps {
                    script {
                       withCredentials([usernamePassword(credentialsId: 'AWS_CREDS', 
                                                     usernameVariable: 'AWS_ACCESS_KEY_ID', 
                                                     passwordVariable: 'AWS_SECRET_ACCESS_KEY'),
                                     sshagent(credentialsId: 'ec2-ssh-key')]) {
                        // Use SSH credentials to log into EC2 and run commands there
                        sh """
                        # Log in to AWS ECR using AWS CLI
                        ssh ${EC2_HOST} <<EOF
                        # Set the AWS credentials as environment variables on EC2
                        export AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}
                        export AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}
                        
                        # Log in to ECR using the AWS CLI
                        aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REPO}
                        
                        # Pull the Docker image from ECR
                        docker pull ${ECR_REPO}:latest
                        
                        # Optionally, run the Docker container (if required)
                        docker run -d -p 5000:5000 ${ECR_REPO}:latest
                        EOF
                        """
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
