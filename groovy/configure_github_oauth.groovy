/*
   Copyright (c) 2015-2018 Sam Gleske - https://github.com/samrocketman/jenkins-bootstrap-jervis
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
   */

/*
   Configures GitHub as the security realm from the GitHub Authentication
   Plugin (github-oauth).
   github-oauth 0.29
 */

import hudson.security.SecurityRealm
import org.jenkinsci.plugins.GithubSecurityRealm
import net.sf.json.JSONObject
import jenkins.model.*
import org.jenkinsci.plugins.GithubAuthorizationStrategy
import hudson.security.AuthorizationStrategy

def env = System.getenv()
github_realm = [
   web_uri: env['GITHUB_URI'],
   api_uri: env['GITHUB_API_URI'],
   client_id: env['GITHUB_OAUTH_CLIENT_ID'],
   client_secret: env['GITHUB_OAUTH_CLIENT_SECRET']
]

if(!binding.hasVariable('github_realm')) {
    github_realm = [:]
}

if(!(github_realm instanceof Map)) {
    throw new Exception('github_realm must be a Map.')
}

github_realm = github_realm as JSONObject

String githubWebUri = github_realm.optString('web_uri', GithubSecurityRealm.DEFAULT_WEB_URI)
String githubApiUri = github_realm.optString('api_uri', GithubSecurityRealm.DEFAULT_API_URI)
String oauthScopes = github_realm.optString('oauth_scopes', GithubSecurityRealm.DEFAULT_OAUTH_SCOPES)
String clientID = github_realm.optString('client_id')
String clientSecret = github_realm.optString('client_secret')

if(!Jenkins.instance.isQuietingDown()) {
    if(clientID && clientSecret) {
        SecurityRealm github_realm = new GithubSecurityRealm(githubWebUri, githubApiUri, clientID, clientSecret, oauthScopes)
        //check for equality, no need to modify the runtime if no settings changed
        if(!github_realm.equals(Jenkins.instance.getSecurityRealm())) {
            Jenkins.instance.setSecurityRealm(github_realm)
            Jenkins.instance.save()
            println 'Security realm configuration has changed.  Configured GitHub security realm.'
        } else {
            println 'Nothing changed.  GitHub security realm already configured.'
        }
    }
} else {
    println 'Shutdown mode enabled.  Configure GitHub security realm SKIPPED.'
}


//permissions are ordered similar to web UI
//Admin User Names
String adminUserNames = env['JENKINS_ADMIN_USER']
//Participant in Organization
String organizationNames = env['GITHUB_OAUTH_ORGANIZATION']
//Use Github repository permissions
boolean useRepositoryPermissions = false
//Grant READ permissions to all Authenticated Users
boolean authenticatedUserReadPermission = true
//Grant CREATE Job permissions to all Authenticated Users
boolean authenticatedUserCreateJobPermission = false
//Grant READ permissions for /github-webhook
boolean allowGithubWebHookPermission = true
//Grant READ permissions for /cc.xml
boolean allowCcTrayPermission = false
//Grant READ permissions for Anonymous Users
boolean allowAnonymousReadPermission = true
//Grant ViewStatus permissions for Anonymous Users
boolean allowAnonymousJobStatusPermission = true

AuthorizationStrategy github_authorization = new GithubAuthorizationStrategy(adminUserNames,
    authenticatedUserReadPermission,
    useRepositoryPermissions,
    authenticatedUserCreateJobPermission,
    organizationNames,
    allowGithubWebHookPermission,
    allowCcTrayPermission,
    allowAnonymousReadPermission,
    allowAnonymousJobStatusPermission)

//check for equality, no need to modify the runtime if no settings changed
if(!github_authorization.equals(Jenkins.instance.getAuthorizationStrategy())) {
    Jenkins.instance.setAuthorizationStrategy(github_authorization)
    Jenkins.instance.save()
}
