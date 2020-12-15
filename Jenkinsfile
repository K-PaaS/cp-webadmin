pipeline {
	environment {
        IMAGE_NAME = "container-platform-webadmin"
        REGISTRY_HARBOR_CREDENTIAL = 'harbor-credential'
        REGISTRY_DOCKER_URL = "15.164.129.57:5000"
        REGISTRY_HARBOR_URL = "15.164.129.57:8090"
        PROJECT_NAME = "container-platform"
	}
	agent any
	stages {
		stage('Cloning Github') {
			steps {
				git branch: 'dev', url: 'https://github.com/PaaS-TA/paas-ta-container-platform-webadmin'
			}
		}
		stage('sed') {
		    steps {
		        sh 'head -5 WebContent/resources/js/common.js'
		        sh 'sed -i "s/15.164.214.190:30333/\'+window.location.host.substr(0,window.location.host.indexOf(\':\',0)+1)+\'30333/g" WebContent/resources/js/common.js'
		        sh 'sed -i "s/file:\\/\\/\\/D:\\/_Work\\/PaaS-TA\\/svn\\/container\\/admin\\//http:\\/\\/52.79.235.113:32080\\//g" WebContent/resources/js/common.js'
		        sh 'head -5 WebContent/resources/js/common.js'
		   }
		}
		stage('Environment') {
            parallel {
                stage('wrapper') {
                    steps {
                        sh 'gradle wrapper'
                    }
                }
                stage('display') {
                    steps {
                        sh 'ls -la'
                    }
                }
            }
        }
		stage('copy config') {
			steps {
				sh 'cp /var/lib/jenkins/workspace/config/webuser/application.yml src/main/resources/application.yml'
			}
		}
		stage('Clean Build') {
            steps {
                sh './gradlew clean'
            }
        }
		stage('Build Jar') {
            steps {
                sh './gradlew build'
            }
        }
		stage('Building image') {
			steps{
				script {
					dockerImage = docker.build REGISTRY_DOCKER_URL+"/"+IMAGE_NAME+":latest"
					dockerVersionedImage = docker.build REGISTRY_DOCKER_URL+"/"+IMAGE_NAME+":$BUILD_NUMBER"
					harborImage = docker.build REGISTRY_HARBOR_URL+"/"+PROJECT_NAME+"/"+IMAGE_NAME+":latest"
                    harborVersionedImage = docker.build REGISTRY_HARBOR_URL+"/"+PROJECT_NAME+"/"+IMAGE_NAME+":$BUILD_NUMBER"
				}
			}
		}
		stage('Deploy Image') {
			steps{
				script {
					docker.withRegistry("http://"+REGISTRY_DOCKER_URL)
					{
						dockerImage.push()
						dockerVersionedImage.push()
					}
					docker.withRegistry("http://"+REGISTRY_HARBOR_URL, REGISTRY_HARBOR_CREDENTIAL)
                    {
                        harborImage.push()
                        harborVersionedImage.push()
                    }
				}
			}
		}
		stage('Kubernetes deploy') {
			steps {
				kubernetesDeploy (
					configs: "yaml/Deployment.yaml", 
					kubeconfigId: 'Kubernetes-api-credential-cp-dev', 
					enableConfigSubstitution: true
				)
			}
		}
		stage('Remove Unused docker image') {
			steps{
				echo "REGISTRY_DOCKER_URL: $REGISTRY_DOCKER_URL"
                echo "REGISTRY_HARBOR_URL: $REGISTRY_HARBOR_URL"
                echo "PROJECT_NAME: $PROJECT_NAME"
                echo "IMAGE_NAME: $IMAGE_NAME"
                echo "BUILD_NUMBER: $BUILD_NUMBER"
                sh "docker rmi $REGISTRY_DOCKER_URL/$IMAGE_NAME:latest"
                sh "docker rmi $REGISTRY_DOCKER_URL/$IMAGE_NAME:$BUILD_NUMBER"
                sh "docker rmi $REGISTRY_HARBOR_URL/$PROJECT_NAME/$IMAGE_NAME:latest"
                sh "docker rmi $REGISTRY_HARBOR_URL/$PROJECT_NAME/$IMAGE_NAME:$BUILD_NUMBER"
			}
		}
	}
}
