def conf = this.args[0]
def method = this.args[1]
def url = this.args[2]
def data = this.args.length > 3 ? this.args[3] : null
def contentType = this.args.length > 4 ? this.args[4] : null

def client = new GOAuthClient(conf)

if(data) {
	def f = new File(data)
	if(f.exists()) {
		data = new FileInputStream(f)
	}
}
def response = client.sendRequest(method, url, [], data, contentType)

def body = response?.readBodyAsString()

if(body) {
	System.err.println "Got response:"
	println body
} else {
	System.err.println "Empty response body"
}
