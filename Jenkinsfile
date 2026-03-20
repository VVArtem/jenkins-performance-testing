def getTimestamp() 
{
    return new Date().format('dd-MM-yyyy_HH-mm', TimeZone.getTimeZone('UTC'))
}

pipeline 
{
    agent none
    
    parameters 
    {
        booleanParam(name: 'RUN_JMETER', defaultValue: true, description: 'Run JMeter test?')
        booleanParam(name: 'RUN_GATLING', defaultValue: true, description: 'Run Gatling test?')
        booleanParam(name: 'RUN_LIGHTHOUSE', defaultValue: true, description: 'Run Lighthouse test?')
        
        string(name: 'USERS', defaultValue: '5', description: 'Amount of virtual users')
        string(name: 'DURATION', defaultValue: '60', description: 'Backend test duration')
        string(name: 'LH_ITERATIONS', defaultValue: '1', description: 'Number of iterations in Lighthouse')

        choice(name: 'TARGET_PROTOCOL', choices: ['http', 'https'], description: 'Protocol to use')
        string(name: 'TARGET_HOST', defaultValue: '127.0.0.1', description: 'Target server hostname or IP')
        string(name: 'TARGET_PORT', defaultValue: '80', description: 'Target server port')
        choice(name: 'DOCKER_NETWORK', choices: ['host', 'pte-network', 'bridge'], description: 'Docker network mode')
    }

    environment 
    {
        GIT_REPO = "https://github.com/VVArtem/jenkins-performance-testing.git"

        BASE_URL = "${params.TARGET_PROTOCOL}://${params.TARGET_HOST}/"

        REPORT_TIMESTAMP = "${getTimestamp()}"
        REPORT_NAME = "build-${env.BUILD_NUMBER}_${env.REPORT_TIMESTAMP}"
    }

    stages 
    {
        stage('Checkout Git') {
            agent any
            steps {
                git branch: 'main', url: "${env.GIT_REPO}"        
            }
        }

        stage('JMeter Test') 
        {
            when { expression { return params.RUN_JMETER } }
            agent 
            {
                docker 
                {
                    image 'justb4/jmeter:5.5'
                    args "--network ${params.DOCKER_NETWORK} --entrypoint=''"
                }
            }
            steps 
            {
                dir('jmeter') 
                {
                    echo "JMETER: Preparing directories"
                    sh "rm -rf results/* reports/* && mkdir -p results reports"

                    echo "JMETER: Running simulation (Target: ${env.BASE_URL})"
                    script 
                    {
                        def jmeterReportName = "results_${env.REPORT_NAME}"
                        sh """
                            jmeter -n \
                                -t scripts/Essentials.jmx \
                                -l results/${jmeterReportName}.jtl \
                                -Jusers=${params.USERS} \
                                -Jduration=${params.DURATION} \
                                -Jprotocol=${params.TARGET_PROTOCOL} \
                                -Jhost=${params.TARGET_HOST} \
                                -Jport=${params.TARGET_PORT} \
                                -e -o reports/${jmeterReportName}
                        """
                    }

                    echo "JMETER: Archiving report"
                    sh "tar -czf jmeter-report-${env.REPORT_NAME}.tar.gz -C reports/results_${env.REPORT_NAME}"
                }
            }
            post 
            {
                always 
                {
                    script 
                    {
                        def jmeterReportName = "results_${env.REPORT_NAME}"
                        publishHTML([
                            allowMissing: false,
                            alwaysLinkToLastBuild: true,
                            keepAll: true,
                            reportDir: "jmeter/reports/${jmeterReportName}",
                            reportFiles: 'index.html',
                            reportName: "JMeter Report ${env.REPORT_NAME}"
                        ])

                        archiveArtifacts artifacts: 'jmeter/*.tar.gz', allowEmptyArchive: true
                    }
                }
            }
        }

        stage('Gatling Test') 
        {
            when { expression { return params.RUN_GATLING } }
            agent 
            {
                docker 
                {
                    image 'maven:3.9-eclipse-temurin-17-alpine'
                    args "--network ${params.DOCKER_NETWORK} -v maven-cache:/root/.m2"
                }
            }
            steps 
            {
                dir('gatling') 
                {
                    echo "GATLING: Running simulation (Target: ${env.BASE_URL})"
                    sh """
                        mvn clean gatling:test \
                            -Dmaven.repo.local=.m2/repository \
                            -Dgatling.simulationClass=simulation.Simulation1 \
                            -Dusers=${params.USERS} \
                            -Dduration=${params.DURATION} \
                            -DappBaseUrl=${env.BASE_URL} \
                            -DloadType=closed
                    """

                    echo "GATLING: Archiving reports"
                    sh "tar -czf gatling-report-${env.REPORT_NAME}.tar.gz -C target/gatling"
                }
            }
            post 
            {
                always 
                {
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'gatling/target/gatling',
                        reportFiles: '**/index.html',
                        reportName: "Gatling Report ${env.REPORT_NAME}"
                    ])

                    archiveArtifacts artifacts: 'gatling/*.tar.gz', allowEmptyArchive: true
                }
            }
        }
        
        stage('Lighthouse Test') 
        {
            when { expression { return params.RUN_LIGHTHOUSE } }
            agent 
            {
                docker 
                {
                    image 'femtopixel/google-lighthouse'
                    args "--network ${params.DOCKER_NETWORK} --entrypoint='' -e HOME=/tmp"
                }
            }

            steps 
            {
                dir('lighthouse') 
                {
                    echo "LIGHTHOUSE: Installing dependencies"
                    sh "npm install puppeteer lighthouse csv-parse --no-cache"

                    echo "LIGHTHOUSE: Cleaning old reports"
                    sh "rm -rf iteration-*"

                    echo "LIGHTHOUSE: Running iterations"
                    script 
                    {
                        int iterations = params.LH_ITERATIONS.toInteger()

                        for (int i = 1; i <= iterations; i++) 
                        {

                            def lhReportFileName = "lh_report_${env.REPORT_NAME}_iter_${i}.html"
                            sh "mkdir -p iteration-${i}"
                            
                            withEnv([
                                "REPORT_PATH=iteration-${i}/${lhReportFileName}", 
                                "TARGET_URL=${env.BASE_URL}"
                            ]) 
                            {
                                sh "node lighthouse-script.js"
                            }
                        }
                    }
                }
            }

            post 
            {
                always 
                {
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'lighthouse',
                        reportFiles: '**/lh_report_*.html',
                        reportName: "Lighthouse Report ${env.REPORT_NAME}"
                    ])
                    archiveArtifacts artifacts: 'lighthouse/iteration-*/*.html', allowEmptyArchive: true
                }
            }
        }
    }
}