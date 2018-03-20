import jenkins.model.*
import jenkins.security.s2m.*
import hudson.security.csrf.DefaultCrumbIssuer
import jenkins.model.JenkinsLocationConfiguration

def instance = Jenkins.getInstance()
instance.setQuietPeriod(0)
instance.setNumExecutors(0)
instance.getDescriptor("jenkins.CLI").get().setEnabled(false)
instance.getInjector().getInstance(AdminWhitelistRule.class).setMasterKillSwitch(false)
Set<String> agentProtocolsList = ['JNLP4-connect', 'Ping']
instance.setAgentProtocols(agentProtocolsList)
def env = System.getenv()
int port = env['JENKINS_SLAVE_AGENT_PORT'].toInteger()
instance.setSlaveAgentPort(port)
if(instance.getCrumbIssuer() == null) {
    instance.setCrumbIssuer(new DefaultCrumbIssuer(true))
}
instance.save()

jlc = new JenkinsLocationConfiguration().get()
jlc.setUrl(env['JENKINS_URL'])
jlc.save()
