package plastic.criteria;

public class Portrait {

	static belongsTo = [artist: Artist]
	
	String name
	
	BigDecimal value
	
	String color
	
	static constraints = {
		color nullable: true
		value nullable: true
	}
	
}
