package plastic.criteria;

import static org.junit.Assert.*;
import static plastic.criteria.PlasticCriteria.*;
import grails.test.mixin.Mock;

import org.junit.Before;
import org.junit.Test;

@Mock([Artist, Portrait])
public class PlasticCriteriaTests extends CriteriaDocTests{

	@Before
	void setUp(){
		mockCriteria([Artist, Portrait])
	}
	
}
