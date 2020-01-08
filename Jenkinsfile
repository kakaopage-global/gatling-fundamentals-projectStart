pipeline {
    agent any
    stages {



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

            if (isUnix()) {
                sh "./gradlew --no-daemon --refresh-dependencies build"
            } else {
                bat "gradlew.bat --init-script init.gradle --no-daemon --refresh-dependencies clean ${snapShotTask} build distZip publish ${skipTest}"
            }

        }

    }
}