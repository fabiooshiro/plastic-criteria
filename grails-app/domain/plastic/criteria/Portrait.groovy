package plastic.criteria;

public class Portrait {

	static belongsTo = [artist: Artist]
	
	String name
	
	BigDecimal value
	
	String color
	
	String toString(){
		"${name} (id: ${id})"
	}
	
	static constraints = {
		color nullable: true
		value nullable: true
	}
	
}
