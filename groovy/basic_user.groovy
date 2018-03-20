import jenkins.model.*
import hudson.security.*
import hudson.model.User

def instance = Jenkins.getInstance()
def hudsonRealm = new HudsonPrivateSecurityRealm(false)
def env = System.getenv()
def admin_user = env['JENKINS_ADMIN_USER']
def admin_user_password = env['JENKINS_ADMIN_PASSWORD']
hudsonRealm.createAccount(admin_user, admin_user_password)
instance.setSecurityRealm(hudsonRealm)

def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(true)
instance.setAuthorizationStrategy(strategy)

instance.save()
