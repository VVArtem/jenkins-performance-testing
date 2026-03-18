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

    environment {
        TARGET_PROTOCOL = "http"
        TARGET_HOST     = "127.0.0.1"
        TARGET_PORT     = "80"
        JM_PATH = "/opt/apache-jmeter-5.6.3/bin/jmeter"
        JAVA_HOME = '/usr/lib/jvm/java-1.17.0-openjdk-amd64'
        PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
        
        REPORT_NAME = "build-${env.BUILD_NUMBER}"

        BASE_URL = "${env.TARGET_PROTOCOL}://${env.TARGET_HOST}"
    }

    stages {
        stage('JMeter Test') {
            when { expression { return params.RUN_JMETER } }
            steps {
                dir('jmeter') {
                    script {
                        def jmeterReportName = "results_${env.REPORT_NAME}"
                        sh """
                            rm -rf results/* reports/*
                            mkdir -p results reports
                
                            ${env.JM_PATH} -n \
                                -t scripts/Essentials.jmx \
                                -l results/${jmeterReportName}.jtl \
                                -Jusers=${params.USERS} \
                                -Jduration=${params.DURATION} \
                                -Jprotocol=${env.TARGET_PROTOCOL} \
                                -Jhost=${env.TARGET_HOST} \
                                -Jport=${env.TARGET_PORT} \
                                -e -o reports/${jmeterReportName}
                        """
                    }
                }
            }
            post {
                always {
                    script {
                        def jmeterReportName = "results_${env.REPORT_NAME}"
                        publishHTML([
                            allowMissing: false,
                            alwaysLinkToLastBuild: true,
                            keepAll: true,
                            reportDir: "jmeter/reports/${jmeterReportName}",
                            reportFiles: 'index.html',
                            reportName: "JMeter Report ${env.BUILD_NUMBER}"
                        ])
                    }
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
                            -DappBaseUrl=${env.BASE_URL} \
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
                        rm -rf iteration-*
                    """
                    script {
                        int iterations = params.LH_ITERATIONS.toInteger()
                        for (int i = 1; i <= iterations; i++) {
                            def lhReportFileName = "lh_report_${env.REPORT_NAME}_iter_${i}.html"
                            sh "mkdir -p iteration-${i}"
                            
                            withEnv([
                                "REPORT_PATH=iteration-${i}/${lhReportFileName}", 
                                "TARGET_URL=${env.BASE_URL}"
                            ]) {
                                sh "node lighthouse-script.js"
                            }
                        }
                    }
                }
            }
            post {
                always {
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'lighthouse',
                        reportFiles: '**/lh_report_*.html',
                        reportName: "Lighthouse Report ${env.BUILD_NUMBER}"
                    ])
                    archiveArtifacts artifacts: 'lighthouse/**/*.html', allowEmptyArchive: true
                }
            }
        }
    }
}