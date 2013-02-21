grails.project.work.dir = 'target'
grails.project.target.level = 1.6

grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		grailsCentral()
		mavenLocal()
		mavenCentral()
	}

	dependencies {
		compile 'javassist:javassist:3.12.1.GA'
	}

	plugins {
		build ':release:2.2.0', ':rest-client-builder:1.0.3', {
			export = false
		}

		runtime ":hibernate:$grailsVersion", {
			export = false
		}
	}
}
