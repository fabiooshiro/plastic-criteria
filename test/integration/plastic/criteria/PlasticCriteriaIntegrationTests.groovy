package plastic.criteria;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PlasticCriteriaIntegrationTests {

	def artitst
	
	@Before
	void setUp(){
		artitst = new Artist(name: 'Brilhante').save()
	}
	
	@Test
	public void test() {
	}

	void testRowCount(){
		def a = new Portrait(artist: artitst, name: 'Soleil levant', value: 1.0).save()
		def b = new Portrait(artist: artitst, name: 'Soleil Levant', value: 1.0).save()
		def c = new Portrait(artist: artitst, name: 'The Madonna of Port Lligat', value: 1.0).save()
		def res = Portrait.withCriteria{
			projections{
				rowCount()
			}
		}
		assert 3 == res[0]
	}
	
	void testRowCountAndGroupProperty(){
		def monet = new Artist(name: 'Monet').save()
		def salvador = new Artist(name: 'Salvador').save()
		def a = new Portrait(artist: monet, name: 'Soleil levant', value: 1.0).save()
		def b = new Portrait(artist: monet, name: 'Soleil Levant', value: 1.0).save()
		def c = new Portrait(artist: salvador, name: 'The Madonna of Port Lligat', value: 1.0).save()
		def res = Portrait.withCriteria{
			projections{
				rowCount()
				groupProperty('artist')
			}
		}
		assert 2 == res.find{ it[1].name == 'Monet' }[0]
		assert 1 == res.find{ it[1].name == 'Salvador' }[0]
	}
}
