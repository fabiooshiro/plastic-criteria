package plastic.criteria

class Artist {

	String name

	String toString(){
		name
	}

	static hasMany = [portraits: Portrait]
}
