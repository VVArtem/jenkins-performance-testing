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
        BASE_URL = "${params.TARGET_PROTOCOL}://${params.TARGET_HOST}/"
        
        REPORT_NAME = "build-${env.BUILD_NUMBER}"
    }

    stages 
    {
        stage('JMeter Test') 
        {
            when { expression { return params.RUN_JMETER } }
            agent 
            {
                docker 
                {
                    image 'justb4/jmeter:5.5'
                    args "--network ${params.DOCKER_NETWORK} --entrypoint='' -u root"
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
                            reportName: "JMeter Report ${env.BUILD_NUMBER}"
                        ])
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
                        reportName: "Gatling Report ${env.BUILD_NUMBER}"
                    ])
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
                    args "--network ${params.DOCKER_NETWORK} --entrypoint='' -u root -v npm-cache:/root/.npm"
                }
            }

            steps 
            {
                dir('lighthouse') 
                {
                    echo "LIGHTHOUSE: Installing dependencies"
                    sh "npm install puppeteer lighthouse csv-parse"

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
                            
                            withEnv
                            ([
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
                        reportName: "Lighthouse Report ${env.BUILD_NUMBER}"
                    ])
                    archiveArtifacts artifacts: 'lighthouse/**/*.html', allowEmptyArchive: true
                }
            }
        }
    }
}