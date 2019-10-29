import groovy.transform.Field
/**
 * 01/24/2019 - HHM
 */

@Field
static argDesc = [
    name: 'azureDeploy',
    description: 'Azure Deployment, Install azure CLI and execute deployment against a given resource.',
    args: [
        scriptName: [
            description: 'Name of the deployment script to invoke.',
        ],
        scriptParameters: [
            description: 'Optional parameters to pass to the deployment script.',
            default: '',
        ],
        credentials: [
            description: 'Required Jenkins credentials to use for the deployment.  The environment variables "USER" and "PASS" will be set to the username and password, respectively.',
        ],
        containerBuild: [
            description: 'Set containerBuild to true if the build process is container based. Default to false.',
            default: false,
            validate: { it.toBoolean() },

        ],
		environmentName: [
            description: 'Name of the enviroment to be deployed to.  Example DLAB01, QLAB01, Prod etc..',
			default: 'Not_Reported',			
        ],
    ],
]

def call(body) 
{
    library 'pipeline-common'
    def config = demoCommon.parseArgs(argDesc, body)
 } 
/**
* Function that takes care of Azure CLI installation and makes the CLI available for execution of azure deployment commands
**/
def executeScript(def scriptName, def scriptParams, def credentials,  def isContainerBuild,def config) {
    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: credentials, usernameVariable: 'USER', passwordVariable: 'PASS']]) {
        milestone()
            sh "chmod +x ./${scriptName}"  //RUN SHELL COMMAND
            if (!isContainerBuild) {
			 echo "Loading library script aws_deploy_Functions.sh"
             def awsScript = libraryResource "scripts/aws_deploy_Functions.sh"
             def awsTool = sh "${awsScript}"             
             withEnv(["AWS_Tool=${awsTool}","PATH=${awsTool}:${env.PATH}"]){
	  	          sh "./${scriptName} ${scriptParams}" //RUN SHELL COMMAND
		     }
            } else {
                sh "./${scriptName} ${scriptParams}" //RUN SHELL COMMAND
            }
            milestone()        
    }
}
