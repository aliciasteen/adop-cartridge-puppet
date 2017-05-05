
// Folders
def workspaceFolderName = "${WORKSPACE_NAME}"
def projectFolderName = "${PROJECT_NAME}"
def projectScmNamespace = "${SCM_NAMESPACE}"

// Variables
def puppetControlRepoBranch = 'master'
def puppetControlRepoUrl = 'ssh://jenkins@gerrit:29418/${GERRIT_PROJECT}'

def buildAppJob = pipelineJob(projectFolderName + "/PuppetPipeline")

pipelineJob('PuppetPipeline') {
    definition {
        cps {
            script("""
                // Pipeline
                node('docker') {
                  deleteDir()

                  // Clone puppet control reop
                  stage('Clone')  {
                    git branch: puppetControlRepoBranch, credentialsId: "adop-jenkins-master", url: puppetControlRepoUrl;
                  }

                  // Run puppet lint, puppet validate and lint yaml files
                  stage('Validate') {
                    sh '''docker run -t --rm \\\\
                     -v jenkins_slave_home:/jenkins_slave_home/ \\\\
                     puppet:0.0.1 \\\\
                     /jenkins_slave_home/$JOB_NAME/puppet/validate.sh /jenkins_slave_home/$JOB_NAME/'''
                  }

                  // Get user token
                  stage('Get token') {
                    sh ' curl -k -X POST -H \'Content-Type: application/json\' -d \'{"login": "admin", "password": "password", "lifetime":"1d"}\' https://##ip##/rbac-api/v1/auth/token'
                  }

                  // Noop Deploy control repo to puppet
                  stage('Noop Deploy') {
                    puppet.credentials 'pe-access-token' 
                    puppet.codeDeploy 'production', credentials: 'pe-access-token'
                    puppet.job 'production', concurrency: 10, noop: true, credentialsId: 'pe-access-token'
                  }

                    // Noop Deploy control repo to puppet
                  stage('Deploy') {
                    puppet.credentials 'pe-access-token' 
                    puppet.codeDeploy 'production', credentials: 'pe-access-token'
                    puppet.job 'production', concurrency: 10, noop: false, credentialsId: 'pe-access-token'
                  }
                }
              """.stripIndent())
        }
    }
}