pipeline {
    agent any

    parameters {
        choice(
            name: 'TEST_SUITE',
            choices: ['sanity', 'regression', 'nightly', 'mix'],
            description: 'Select test suite to run'
        )
        booleanParam(
            name: 'CLEAN_BUILD',
            defaultValue: true,
            description: 'Clean before building'
        )
        booleanParam(
            name: 'SKIP_TESTS',
            defaultValue: false,
            description: 'Skip test execution'
        )
    }

    environment {
        MAVEN_OPTS = '-Xmx1024m -Xms512m'
    }

    stages {
        stage('Checkout') {
            steps {
                echo '📥 Cloning repository from GitHub...'
                git branch: 'main', url: 'https://github.com/Dasuni-mg/jenkins_practice.git'
                echo "📁 Workspace: ${env.WORKSPACE}"
            }
        }

        stage('Prepare') {
            steps {
                echo "🧪 Selected test suite: ${params.TEST_SUITE}"

                // Check Java and Maven versions
                bat 'java -version'
                bat 'mvn --version'

                // Clean if CLEAN_BUILD is true
                script {
                    if (params.CLEAN_BUILD) {
                        echo '🧹 Cleaning workspace...'
                        bat 'mvn clean'
                    }
                }

                // Compile
                echo '🔨 Compiling project...'
                bat 'mvn compile test-compile'
            }
        }

        stage('Test') {
            when {
                expression { params.SKIP_TESTS == false }
            }
            steps {
                script {
                    echo "🚀 Executing ${params.TEST_SUITE} test suite..."

                    // Build the Maven command with backslashes for Windows
                    def mvnCommand = 'mvn test'
                    mvnCommand += " -DsuiteXmlFile=testng\\${params.TEST_SUITE}.xml"
                    mvnCommand += ' -Dtestng.showSummary=true'
                    mvnCommand += ' -DskipTests=false'

                    echo "📝 Command: ${mvnCommand}"
                    bat mvnCommand
                }
            }
        }

        stage('Report') {
            steps {
                echo '📊 Generating test reports...'

                // Generate surefire reports
                bat 'mvn surefire-report:report'

                // Create summary report
                bat """
                echo "=== Test Execution Summary ===" > test-summary.txt
                echo "Project: jenkins-practice" >> test-summary.txt
                echo "Test Suite: ${params.TEST_SUITE}" >> test-summary.txt
                echo "Date: %DATE% %TIME%" >> test-summary.txt
                echo "" >> test-summary.txt

                if exist target\\surefire-reports\\TEST-TestSuite.txt (
                    type target\\surefire-reports\\TEST-TestSuite.txt >> test-summary.txt
                )
                """

                echo '📦 Archiving artifacts...'
                archiveArtifacts artifacts: 'target/surefire-reports/**/*, test-summary.txt', fingerprint: true

                // Publish JUnit test results
                junit 'target/surefire-reports/*.xml'
            }
        }
    }

    post {
        always {
            echo "📋 Pipeline completed with status: ${currentBuild.currentResult}"

            // Clean up temporary files
            bat 'if exist test-summary.txt del test-summary.txt'

            script {
                if (currentBuild.currentResult == 'SUCCESS') {
                    echo '✅ Pipeline executed successfully!'
                } else if (currentBuild.currentResult == 'FAILURE') {
                    echo '❌ Pipeline failed! Check logs for details.'
                }
            }
        }
        success {
            emailext (
                subject: "Pipeline Success: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                body: """The pipeline ${env.JOB_NAME} - Build #${env.BUILD_NUMBER} completed successfully.

                View the build: ${env.BUILD_URL}""",
                to: 'your-email@example.com'
            )
        }
        failure {
            emailext (
                subject: "Pipeline Failed: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                body: """The pipeline ${env.JOB_NAME} - Build #${env.BUILD_NUMBER} failed.

                View the build: ${env.BUILD_URL}""",
                to: 'your-email@example.com'
            )
        }
    }
}