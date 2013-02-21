package plastic.criteria

import static plastic.criteria.PlasticCriteria.*

import org.junit.Before

@Mock([Artist, Portrait])
class PlasticCriteriaTests extends CriteriaDocTests{

	@Before
	void setUp(){
		mockCriteria([Artist, Portrait])
	}
}
