    pipeline{
            agent any
     stages{
        stage("Checkout"){
            steps{
                    git branch: 'main', credentialsId: 'git_hub_cred_id', url: 'https://github.com/jayachandra350/NodeApp.git'

            }
        }
        stage("Build Docker Image"){
            steps{

             sh """
             docker build -t mynodeapp:${BUILD_NUMBER} .
             """
            }
        }
        stage("Push to ecr registry"){
            steps{
                sh """
                export AWS_ACCESS_KEY_ID=AKIA6ODU4VFVFSZSWCHT && export AWS_SECRET_ACCESS_KEY=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
                aws ecr-public get-login-password --region us-east-1 | docker login --username AWS --password-stdin 992382593386.dkr.ecr.ap-south-1.amazonaws.com/my-node-app
                docker tag mynodeapp:${BUILD_NUMBER} 992382593386.dkr.ecr.ap-south-1.amazonaws.com/my-node-app:${BUILD_NUMBER}
                docker push 992382593386.dkr.ecr.ap-south-1.amazonaws.com/my-node-app:${BUILD_NUMBER}
                """
            }
        }
        
       
    
        }

        }
