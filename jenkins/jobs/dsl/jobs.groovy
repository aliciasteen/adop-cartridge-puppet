// Folders
def workspaceFolderName = "${WORKSPACE_NAME}"
def projectFolderName = "${PROJECT_NAME}"
def projectScmNamespace = "${SCM_NAMESPACE}"

// Variables
def puppetControlRepoBranch = 'development'
def puppetControlRepo = 'adop-cartridge-puppet-control-repo'

def buildAppJob = pipelineJob(projectFolderName + "/PuppetPipeline")

buildAppJob.with {
    environmentVariables {
      env("puppetControlRepoBranch", "development")
      env("projectFolderName", 'ssh://jenkins@gerrit:29418/${GERRIT_PROJECT}')
    }
    triggers {
      gerrit {
        events {
          refUpdated()
        }
        project(projectScmNamespace + puppetControlRepo, "master")
        configure { node ->
          node / serverName('ADOP Gerrit')
        }
      }
    }
    definition {
        cps {
            script("""
                // Pipeline
                node('docker') {
                  deleteDir()

                  // Clone puppet control reop
                  stage('Clone')  {
                    git branch: puppetControlRepoBranch, credentialsId: "adop-jenkins-master", url: 'ssh://jenkins@gerrit:29418/${projectScmNamespace}/${puppetControlRepo}';
                  }

                  // Run puppet lint, puppet validate and lint yaml files
                  stage('Validate') {
                    sh '''docker run -t --rm \\\\
                     -v jenkins_slave_home:/jenkins_slave_home/ \\\\
                     puppet/puppet-agent-alpine:latest \\\\
                     /jenkins_slave_home/$JOB_NAME/scripts/puppet_validate.sh /jenkins_slave_home/$JOB_NAME/'''
                  }

                  // Get user token
                  stage('Get token') {
                    sh ''' curl -k -X POST -H \'Content-Type: application/json\' -d \'{\"login\": \"admin\", \"password\": \"admin@1\", \"lifetime\":\"1d\"}\' https://##ip##/rbac-api/v1/auth/token'''
                    // Update pe-access-token with token value
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
