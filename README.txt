== What's this? ==
A very simple command line HTTP client with OAuth support, written in Groovy.

== Getting started ==
1. obtain consumer key/secret (if you're using twitter, start here: https://twitter.com/apps)
2. create/edit your config (see twitter.groovy.example)

=== 2 legged OAuth ===
Create a config like this (you have to know the requestor user's container specific id):

consumerKey="myKey"
consumerSecret="mySecret"
requestorId="123456"

== Running from source ==
1. install groovy
2. create config (example: twitter.groovy.example)
3. run GenericClient

example:
groovy  -cp src:lib/oauth-20090825.jar:lib/oauth-httpclient4-20090913.jar:lib/httpclient-4.0.1.jar src/GenericClient.groovy [arguments]

== Arguments ==
config method url [post data] [content type] 

e.g:
twitter.groovy GET http://twitter.com/direct_messages.json

== Compiling standalone ==
1. install groovy and gant
2. gant

== Running standalone ==
java -jar bin/goauthclient.jar [arguments]
(Jar can also be downloaded from http://github.com/salomvary/goauthclient/downloads)
