#!groovy
@Library('atlas_shared')
import groovy.json.JsonOutput

def buildNumber = env.BUILD_NUMBER
def branchName = env.BRANCH_NAME
println "buildNumber: ${buildNumber}"
println "branchName: ${branchName}"
def isMaster = env.BRANCH_NAME == "master"
println "isMaster: ${isMaster}"

pipeline {
    agent {
        kubernetes {
            label "build-florunna-${BUILD_NUMBER}"
            defaultContainer 'jnlp'
            yaml """
apiVersion: v1
kind: Pod
metadata:
  labels:
    some-label: "build-httpclient-${BUILD_NUMBER}"
spec:
  containers:
  - name: gradle
    image: gradle:jdk10
    command:
    - cat
    tty: true
    volumeMounts:
    - mountPath: /var/run/docker.sock
      name: docker-socket
  - name: tools
    image: 004384079765.dkr.ecr.us-west-2.amazonaws.com/devops/jenkins-slave:v1.19
    command:
    - cat
    tty: true
    volumeMounts:
    - mountPath: /var/run/docker.sock
      name: docker-socket
  volumes:
  - name: docker-socket
    hostPath:
      path: /var/run/docker.sock
      type: File
"""
        }
    }
    environment {
        JFROG_ARTIFACTORY_CREDENTIALS = credentials('55bdcd8c-e944-41dc-a5cc-9c3aa5616bdc')
        GITHUB_ACCESS_TOKEN = credentials('3ba79c7b-80de-4765-9f6c-959d0c83c492')
        GITHUB_COMMON_CREDS = credentials('b14ed09f-ca8d-4fa9-853b-8c2e0288d438')
    }

    stages {
        stage('Checkout') {

            steps {
                checkout scm
            }
        }
        stage("Gradle Build") {
            //This step also runs the unit tests
            steps {
                container('gradle') {
                    sh '''
                        export ORG_GRADLE_PROJECT_artifactory_user=$JFROG_ARTIFACTORY_CREDENTIALS_USR \
                        && export ORG_GRADLE_PROJECT_artifactory_password=$JFROG_ARTIFACTORY_CREDENTIALS_PSW \
                        && ./gradlew clean build
                    '''
                }
            }
        }

        stage("Gradle Publish Artifactory") {
            steps {
                container('gradle') {
                    script {
                        if (isMaster) {
                            sh '''
                            export ORG_GRADLE_PROJECT_artifactory_user=$JFROG_ARTIFACTORY_CREDENTIALS_USR \
                            && export ORG_GRADLE_PROJECT_artifactory_password=$JFROG_ARTIFACTORY_CREDENTIALS_PSW \
                            && ./gradlew artifactoryPublish
                        '''
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            //Publish Test Results
            archiveArtifacts(artifacts: 'build/reports/tests/test/**')
            publishHTML(target: [
                    allowMissing         : false,
                    alwaysLinkToLastBuild: false,
                    keepAll              : true,
                    reportDir            : 'build/reports/tests/test',
                    reportFiles          : 'index.html',
                    reportName           : "Test Report"
            ])

            //Publish Spock Report
            archiveArtifacts(artifacts: 'build/spock-reports/**')
            publishHTML(target: [
                    allowMissing         : false,
                    alwaysLinkToLastBuild: false,
                    keepAll              : true,
                    reportDir            : 'build/spock-reports',
                    reportFiles          : 'index.html',
                    reportName           : "Spock Report"
            ])

            //Publish Code Coverage
            archiveArtifacts(artifacts: 'build/jacocoHtml/**')
            publishHTML(target: [
                    allowMissing         : false,
                    alwaysLinkToLastBuild: false,
                    keepAll              : true,
                    reportDir            : 'build/jacocoHtml',
                    reportFiles          : 'index.html',
                    reportName           : "Code Coverage Report"
            ])

            //Notify Slack
            container('tools') {
                slack_notify()
            }
        }
    }
}
