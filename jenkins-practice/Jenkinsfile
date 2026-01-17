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
        PROJECT_DIR = 'F:/QA/jenkins-practice'
        MAVEN_OPTS = '-Xmx1024m -Xms512m'
    }

    stages {
        stage('Prepare') {
            steps {
                echo "📋 Running on node: ${env.NODE_NAME}"
                echo "📁 Project directory: ${env.PROJECT_DIR}"
                echo "🧪 Selected test suite: ${params.TEST_SUITE}"

                // Check Java and Maven versions
                bat 'java -version'
                bat 'mvn --version'

                dir(env.PROJECT_DIR) {
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
        }

        stage('Test') {
            when {
                expression { params.SKIP_TESTS == false }
            }
            steps {
                dir(env.PROJECT_DIR) {
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
        }

        stage('Report') {
            steps {
                dir(env.PROJECT_DIR) {
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
                    archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true

                    // Publish JUnit test results
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
    }

    post {
        always {
            echo "📋 Pipeline completed with status: ${currentBuild.currentResult}"

            dir(env.PROJECT_DIR) {
                // Clean up temporary files
                bat 'if exist test-summary.txt del test-summary.txt'
            }

            script {
                if (currentBuild.currentResult == 'SUCCESS') {
                    echo '✅ Pipeline executed successfully!'
                } else if (currentBuild.currentResult == 'FAILURE') {
                    echo '❌ Pipeline failed! Check logs for details.'
                }
            }
        }
    }
}