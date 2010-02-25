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

== How to use RSA-SHA1 signature method ==
1. Generate key:

openssl genrsa mykey.rsa
openssl rsa -in mykey.rsa -pubout -out mykey.rsa.pub

2. Add private key to your config:

rsaPrivateKey="""\
-----BEGIN RSA PRIVATE KEY-----
MIIBOgIBAAJBAMd43P9h62A/36zo9Oio4cXOaQxxbvRAFMb9Z+UmG4eyU6uoz2h6
4eKfi1ZHjhg9UfUVogbxmS+3v7+ZnOv5TOECAwEAAQJBAIyUlcw2ypgu7AkEtXYz
b8rkygxG9ZeWgg4sEcjhro1W1YbctiWJ4cefu7IKJNB+Z9AgkLHI+kHNwcNIBIDb
XEECIQDxKP07RYV5LQ028yrny5XckgXek4jn7CS53d3sIn6oXQIhANO/KRQzoWjg
1u2eQIFZYlol2O6PFyC4el8+jNiT6p5VAiAmRewSmRD6z0SWoPcBPJU3IVGfJmkG
7bpSaTzMOhlB3QIgBL/e7gMSdcXFTGMmU0BsAlWi/gbbn/JDXGuMwTbgJlkCIC0i
I8j2GyRQCrUFMXSTfENHUfDnxebdBdqzkLEebD+V
-----END RSA PRIVATE KEY-----
"""

3. register public key at Service Provider
