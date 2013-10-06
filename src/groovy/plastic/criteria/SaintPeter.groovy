package plastic.criteria

class SaintPeter{
	
	def phrases = []

	def tell(str){
		println str
		phrases.add(str.toString())
	}

	def didYouSay(str){
		return phrases.contains(str.toString())
	}

}
