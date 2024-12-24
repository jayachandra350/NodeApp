pipeline {
    agent any
    environment {
        AWS_REGION = 'ap-south-1' // Replace with your AWS region
        ECR_REPO = 'public.ecr.aws/v2k5k1u2/mynodeapp' // Replace with your ECR repository URI
        EC2_HOST = '15.206.124.114 ' // Replace with your EC2 public IP or hostname
        //DOCKER_TAG = 'latest' // Replace with your Docker image tag
       // SSH_KEY_CREDENTIALS = 'ec2-ssh-key' // Jenkins credentials ID for the EC2 SSH private key
    }
    stages {
        stage('Deploy Docker Image on EC2') {
            steps {
                    script {
                      
                                    
                        // Use SSH credentials to log into EC2 and run commands ther
                        sh """
                        # Log in to AWS ECR using AWS CLI
                        #ssh -T -o  StrictHostKeyChecking=no ${EC2_HOST} <<EOF
                        # Set the AWS credentials as environment variables on EC2
                        
                        
                        # Log in to ECR using the AWS CLI
                        docker login ${ECR_REPO}
                        
                        # Pull the Docker image from ECR
                        docker pull ${ECR_REPO}:${IMAGE_TAG}
                        
                        # Optionally, run the Docker container (if required)
                        docker run -d -p 5000:5000 ${ECR_REPO}:${IMAGE_TAG}
                        
                        """
                    
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
