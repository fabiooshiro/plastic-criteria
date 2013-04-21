package plastic.criteria

class Artist {

	String name

	City city

	String toString(){
		name
	}

	static hasMany = [portraits: Portrait]

	static constraints = {
		city nullable: true
	}
}
