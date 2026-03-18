pipeline {
    agent any
    
    parameters {
        booleanParam(name: 'RUN_JMETER', defaultValue: true, description: 'Run JMeter test?')
        booleanParam(name: 'RUN_GATLING', defaultValue: true, description: 'Run Gatling test?')
        booleanParam(name: 'RUN_LIGHTHOUSE', defaultValue: true, description: 'Run Lighthouse test?')
        
        string(name: 'USERS', defaultValue: '5', description: 'Amount of virtual users')
        string(name: 'DURATION', defaultValue: '60', description: 'Backend test duration')
        string(name: 'LH_ITERATIONS', defaultValue: '1', description: 'Number of iterations in Lighthouse')
    }

    stages {
        stage('JMeter Test') {
            when { expression { return params.RUN_JMETER } }
            steps {
                dir('jmeter') {
                    sh """
                        rm -rf results/* reports/*
                        mkdir -p results reports
            
                        /opt/apache-jmeter-5.6.3/bin/jmeter -n \
                            -t scripts/Essentials.jmx \
                            -l results/test_result.jtl \
                            -Jusers=${params.USERS} \
                            -Jduration=${params.DURATION} \
                            -e -o reports
                    """
                }
            }
            post {
                always {
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'jmeter/reports',
                        reportFiles: 'index.html',
                        reportName: 'JMeter Report'
                    ])
                }
            }
        }
        
        stage('Gatling Test') {
            when { expression { return params.RUN_GATLING } }
            environment {
                JAVA_HOME = '/usr/lib/jvm/java-1.17.0-openjdk-amd64'
                PATH = "${JAVA_HOME}/bin:${env.PATH}"
            }
            steps {
                dir('gatling') {
                    sh """
                        mvn clean gatling:test \
                            -Dgatling.simulationClass=simulation.Simulation1 \
                            -Dusers=${params.USERS} \
                            -Dduration=${params.DURATION} \
                            -DloadType=closed
                    """
                }
            }
            post {
                always {
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'gatling/target/gatling',
                        reportFiles: '**/index.html',
                        reportName: 'Gatling Report'
                    ])
                }
            }
        }
        
        stage('Lighthouse Test') {
            when { expression { return params.RUN_LIGHTHOUSE } }
            steps {
                dir('lighthouse') {
                    sh """
                        npm install puppeteer lighthouse csv-parse
                        rm -f *.html
                    """
                    script {
                        int iterations = params.LH_ITERATIONS.toInteger()
                        for (int i = 1; i <= iterations; i++) {
                            echo "Starting iteration ${i} of ${iterations}"
                            
                            withEnv(["ITERATION=${i}"]) {
                                sh "node lighthouse-script.js"
                            }
                        }
                    }
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: '*.html', fingerprint: true

                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'lighthouse',
                        reportFiles: 'user-flow-1.report.html',
                        reportName: 'Lighthouse Report'
                    ])
                }
            }
        }
    }
}