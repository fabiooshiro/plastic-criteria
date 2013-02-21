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
		build(":release:2.0.0") {
			export = false
		}
	}
}
