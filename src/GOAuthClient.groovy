import net.oauth.*
import net.oauth.http.*
import net.oauth.client.*
import net.oauth.client.httpclient4.*
import org.apache.http.params.HttpProtocolParams
import org.apache.http.HttpVersion

class GOAuthClient {
  def props
  def conf
  OAuthAccessor accessor
  OAuthServiceProvider serviceProvider
  OAuthConsumer consumer
  OAuthServiceProvider provider
  def client = new OAuthClient(new HttpClient4())  

  GOAuthClient() {
    conf = 'oauth.groovy'
  }

  GOAuthClient(conf) {
    this.conf = conf

    //twitter protocol fix
    //this is for httpclient3
    client.httpParameters.disableExpectContinue = true
    //FIXME this doesn't work for httpclient4:
    //HttpProtocolParams.setUseExpectContinue(client.httpParameters, false);
    System.err.println "httpParameters"+client.httpParameters

    this.props = new ConfigSlurper().parse(new File(this.conf).toURL())
    System.err.println "Props: "+props

    if(! props.requestorId) {
      provider = new OAuthServiceProvider(
        props.requestUrl, 
        props.authorizationUrl, 
        props.accessUrl
      )
    } else {
      //2 legged doesn't need this
      provider = null
    }

    consumer = new OAuthConsumer(
      null, 
      props.consumerKey, 
      props.consumerSecret,
      provider
    )

    accessor = new OAuthAccessor(consumer)

    if(! props.requestorId) { //2 legged does't need token
      if(! props.accessToken) {
        getToken()
      } else {
        accessor.accessToken = props.accessToken
        accessor.tokenSecret = props.tokenSecret
        System.err.println "Using access token: "+accessor.accessToken
      }
    } else {
        System.err.println "Using 2 legged authentication, xoauth_requestor_id="+props.requestorId
    }
  }

  def getToken() {
    //request
    def params; 
    if(this.props.callbackUrl) {
      System.err.println "Using OAuth 1.0a"
      params = [new OAuth.Parameter(OAuth.OAUTH_CALLBACK, this.props.callbackUrl)];
    } else {
      System.err.println "Using OAuth 1.0"
    }
    def requestTokenResponse = client.getRequestTokenResponse(accessor, null, params)
    if(this.props.callbackUrl) {
      System.err.println "OAuth callback confirmed: "+requestTokenResponse.getParameter(OAuth.OAUTH_CALLBACK_CONFIRMED)
    }
    System.err.println "Got request token: "+accessor.requestToken

    //authorize
    def authorizeMessage = accessor.newRequestMessage(
      "GET",
      accessor.consumer.serviceProvider.userAuthorizationURL,
      [new OAuth.Parameter('oauth_token', accessor.requestToken)]
    )
    def httpMessage = authorizeMessage.toHttpRequest(OAuthClient.ParameterStyle.QUERY_STRING)
    println "To get access token, paste this in a browser: \n"+httpMessage.url
    print "After authorizing, "+(this.props.callbackUrl ? "enter pin code below" :  "press enter")+"\n>"
    def verifier = new BufferedReader(new InputStreamReader(System.in)).readLine().trim()    
    if(verifier) {
      System.err.println "Got verifier: "+verifier
    }

    //access
    def accessMessage = client.getAccessToken(
      accessor, "GET", 
      (verifier == null) ? null : OAuth.newList(OAuth.OAUTH_VERIFIER, verifier)
    )

    System.err.println "Got message "+accessMessage
    
    props.accessToken = accessMessage.getParameter('oauth_token')
    props.tokenSecret = accessMessage.getParameter('oauth_token_secret')
    props.userId = accessMessage.getParameter('user_id')
    props.screenName = accessMessage.getParameter('screen_name')
    System.err.println "Got accessToken: "+props.accessToken
    System.err.println "    tokenSecret: "+props.tokenSecret
    System.err.println "    userId: "+props.userId
    System.err.println "    screenName: "+props.screenName

    props.writeTo(new FileWriter(this.conf))

  }

  def sendRequest(method="GET", url, params, body=null, contentType=null) {
    try {
      def p = params.collect {
        k,v -> new OAuth.Parameter(k,v) 
      };
      if(this.props.requestorId) { //use 2 legged
        p.add(new OAuth.Parameter('xoauth_requestor_id', this.props.requestorId))
      }
      if(! body) {
        return client.invoke(accessor, method, url, p)
      } else if(["POST","PUT"].contains(method)){
        def message = accessor.newRequestMessage(method, url, p, 
          body instanceof String ? new ByteArrayInputStream(body.getBytes()) : body
        )
        if(contentType) {
          message.getHeaders().add(new OAuth.Parameter(HttpMessage.CONTENT_TYPE, contentType))
        }
        return client.invoke(message,ParameterStyle.AUTHORIZATION_HEADER)
      }
    } catch(OAuthProblemException e) {
      System.err.println "OAuth exception msg: ${e.message}\nproblem: ${e.problem}\nparams: ${e.parameters}";
    }
  }
}

// vim:ts=2:sw=2:et:
