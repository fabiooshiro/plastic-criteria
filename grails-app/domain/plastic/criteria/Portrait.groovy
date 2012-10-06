package plastic.criteria;

public class Portrait {

	static belongsTo = [artist: Artist]
	
	String name
	
	BigDecimal value
	
	BigDecimal lastSoldPrice
	
	String color
	
	String toString(){
		"${name} (id: ${id})"
	}
	
	static constraints = {
		color nullable: true
		value nullable: true
		lastSoldPrice nullable: true
	}
	
}
