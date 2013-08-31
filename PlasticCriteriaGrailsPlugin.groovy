class PlasticCriteriaGrailsPlugin {
	def version = "1.2"
	def grailsVersion = "1.3 > *"
	def pluginExcludes = [
		'grails-app/domain/**',
		'src/groovy/plastic/criteria/CriteriaDocTests.groovy'
	]

	def title = "Plastic Criteria Plugin"
	def author = "Fabio Issamu Oshiro"
	def authorEmail = ""
	def description = 'Mock Grails Criteria for Unit Tests'
	def documentation = "http://grails.org/plugin/plastic-criteria"

//	def license = "WTFPL"
	def organization = [ name: "Investtools", url: "http://www.investtools.com.br/" ]
	def issueManagement = [ system: "GitHub", url: "https://github.com/fabiooshiro/plastic-criteria/issues" ]
	def scm = [ url: "https://github.com/fabiooshiro/plastic-criteria" ]
}
