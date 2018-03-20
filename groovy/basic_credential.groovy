import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import org.jenkinsci.plugins.plaincredentials.*
import org.jenkinsci.plugins.plaincredentials.impl.*
import hudson.util.Secret
import com.github.kostyasha.yad.credentials.DockerRegistryAuthCredentials

def env = System.getenv()
def ssh_username = env['GITHUB_SSH_USERNAME']
def access_token = env['GITHUB_PERSONAL_ACCESS_TOKEN']
def shared_secret = env['GITHUB_WEBHOOK_SHARED_SECRET']
def docker_registry_username = env['DOCKER_REGISTRY_AUTH_USERNAME']
def docker_registry_password = env['DOCKER_REGISTRY_AUTH_PASSWORD']

def credentialstore = SystemCredentialsProvider.getInstance().getStore()

Credentials pat_userpass = (Credentials) new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL,
                                                                   "github_token_username_password",
                                                                   "This credential include github auth token",
                                                                   "github_user",
                                                                   access_token)
credentialstore.addCredentials(Domain.global(), pat_userpass)

Credentials pat_secrettext = (Credentials) new StringCredentialsImpl(CredentialsScope.GLOBAL,
                                                                 "github_token_secret_text",
                                                                 "This credential include github auth token",
                                                                 Secret.fromString(access_token))
credentialstore.addCredentials(Domain.global(), pat_secrettext)

Credentials wss_secrettext = (Credentials) new StringCredentialsImpl(CredentialsScope.GLOBAL,
                                                                 "webhook_shared_secret",
                                                                 "This credential include webhook shared secret",
                                                                 Secret.fromString(shared_secret))
credentialstore.addCredentials(Domain.global(), wss_secrettext)

Credentials registry = (Credentials) new DockerRegistryAuthCredentials(CredentialsScope.GLOBAL,
                                                                       "docker_registry_username_password",
                                                                       "This credential include docker registry auth info",
                                                                       docker_registry_username,
                                                                       docker_registry_password,
                                                                       "")
credentialstore.addCredentials(Domain.global(), registry)

Credentials ssh = (Credentials) new BasicSSHUserPrivateKey(CredentialsScope.GLOBAL,
                                                           'github_ssh_credential',
                                                           ssh_username,
                                                           new BasicSSHUserPrivateKey.UsersPrivateKeySource(),
                                                           "",
                                                           "This credential include github ssh credential")
credentialstore.addCredentials(Domain.global(), ssh)
