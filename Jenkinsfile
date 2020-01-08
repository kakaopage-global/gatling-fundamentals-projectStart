pipeline {
    agent any
    stages {

        def rtGradle = Artifactory.newGradleBuild()
        def buildInfo

        stage('Build Stage') {
            steps {
                sh 'echo "Hello World"'
                sh '''
                    echo "Multiline shell steps works too"
                    ls -lah
                '''
            }
        }

        stage('Gradle Build Stage') {
            rtGradle.tool = "Gradle-5.1"
            buildInfo = rtGradle.run buildFile: 'build.gradle', tasks: 'build'

        }

    }
}