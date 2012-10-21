package plastic.criteria;

public class CriteriaDocTests {

	void testGroupProperty() {
		def pablo = new Artist(name: 'Pablo').save()
		def salvador = new Artist(name: 'Salvador').save()
		new Portrait(artist: pablo, name: "Les Demoiselles d'Avignon", value: 10.00).save()
		new Portrait(artist: pablo, name: "Les Noces de Pierrette", value: 22.00, color: 'blue').save()
		new Portrait(artist: salvador, name: "The Persistence of Memory", value: 20.00).save()
		def artistValue = Portrait.withCriteria{
			projections{
				sum('value')
				groupProperty('artist')
			}
		}
		assert [[32.00, pablo], [20.00, salvador]] ==  artistValue
	}
	
	void test2xGroupProperty(){
		def pablo = new Artist(name: 'Pablo').save()
		def salvador = new Artist(name: 'Salvador').save()
		new Portrait(artist: pablo, name: "Les Demoiselles d'Avignon 1", value: 10.00).save()
		new Portrait(artist: pablo, name: "Les Demoiselles d'Avignon 2", value: 10.00).save()
		new Portrait(artist: pablo, name: "Les Demoiselles d'Avignon 3", value: 10.00).save()
		new Portrait(artist: salvador, name: "The Persistence of Memory 1", value: 20.00).save()
		new Portrait(artist: salvador, name: "The Persistence of Memory 2", value: 20.00).save()
		new Portrait(artist: salvador, name: "The Persistence of Memory 3", value: 20.00).save()
		def artistValue = Portrait.withCriteria{
			projections{
				groupProperty('value')
				groupProperty('artist')
			}
		}
		assert [[10.00, pablo], [20.00, salvador]] ==  artistValue
	}
	
	void testAnd(){
		def pablo = new Artist(name: 'Pablo').save()
		new Portrait(artist: pablo, name: "Les Demoiselles d'Avignon", value: 10.00, color: 'orange').save()
		new Portrait(artist: pablo, name: "Les Noces de Pierrette", value: 22.00, color: 'blue').save()

		def portraits = Portrait.withCriteria{
			artist{
				eq('name', 'Pablo')
			}
			eq('color', 'blue')
		}
	
		assert 1 == portraits.size()
		assert "Les Noces de Pierrette" == portraits[0].name
	}
	
	void testOr(){
		def artitst = new Artist(name: 'Brilhante').save()
		def plastic1 = new Portrait(artist: artitst, name: 'Soleil levant').save()
		def plastic2 = new Portrait(artist: artitst, name: 'The Madonna of Port Lligat').save()
		def plastic3 = new Portrait(artist: artitst, name: "Les Demoiselles d'Avignon").save()
		def ls = Portrait.withCriteria{
			or{
				eq('name', 'Soleil levant')
				eq('name', 'The Madonna of Port Lligat')
			}
		}
		
		assert [plastic1, plastic2] == ls
	}

	void testAvg(){
		def artitst = new Artist(name: 'Brilhante').save()
		new Portrait(artist: artitst, name: 'Soleil levant', value: 1.0).save()
		new Portrait(artist: artitst, name: 'The Madonna of Port Lligat', value: 2.0).save()
		new Portrait(artist: artitst, name: "Les Demoiselles d'Avignon", value: 3.0).save()
		def average = Portrait.createCriteria().get{
			projections{
				avg('value')
			}
		}
		assert 2.0 == average
	}
	
	void testSum(){
		def artitst = new Artist(name: 'Brilhante').save()
		new Portrait(artist: artitst, name: 'Soleil levant', value: 1.0).save()
		new Portrait(artist: artitst, name: 'The Madonna of Port Lligat', value: 2.0).save()
		new Portrait(artist: artitst, name: "Les Demoiselles d'Avignon", value: 3.0).save()
		def total = Portrait.createCriteria().get{
			projections{
				sum('value')
			}
		}
		assert 6.0 == total
	}
	
	void testMin(){
		def artitst = new Artist(name: 'Brilhante').save()
		new Portrait(artist: artitst, name: 'Soleil levant', value: 1.0).save()
		new Portrait(artist: artitst, name: 'The Madonna of Port Lligat', value: 2.0).save()
		new Portrait(artist: artitst, name: "Les Demoiselles d'Avignon", value: 3.0).save()
		def res = Portrait.createCriteria().get{
			projections{
				min('value')
			}
		}
		assert 1.0 == res
	}
	
	void testMax(){
		def artitst = new Artist(name: 'Brilhante').save()
		new Portrait(artist: artitst, name: 'Soleil levant', value: 1.0).save()
		new Portrait(artist: artitst, name: 'The Madonna of Port Lligat', value: 2.0).save()
		new Portrait(artist: artitst, name: "Les Demoiselles d'Avignon", value: 3.0).save()
		def res = Portrait.createCriteria().get{
			projections{
				max('value')
			}
		}
		assert 3.0 == res
	}
	
	void testIgnoreCase(){
		def artitst = new Artist(name: 'Brilhante').save()
		def a = new Portrait(artist: artitst, name: 'Soleil levant', value: 1.0).save()
		def b = new Portrait(artist: artitst, name: 'Soleil Levant', value: 1.0).save()
		def c = new Portrait(artist: artitst, name: 'The Madonna of Port Lligat', value: 1.0).save()
		def results = Portrait.withCriteria{
			eq('name', 'SOLEIL LEVANT', [ignoreCase: true])
		}
		
		assert [a, b] == results
	}
	
	void testLike(){
		def artitst = new Artist(name: 'Brilhante').save()
		def a = new Portrait(artist: artitst, name: 'Soleil levant', value: 1.0).save()
		def b = new Portrait(artist: artitst, name: 'Soleil Levant', value: 1.0).save()
		def c = new Portrait(artist: artitst, name: 'The Madonna of Port Lligat', value: 1.0).save()
		def results = Portrait.withCriteria{
			like('name', 'Soleil%')
		}
		assert [a, b] == results
	}
	
	void testNot(){
		def artitst = new Artist(name: 'Brilhante').save()
		def a = new Portrait(artist: artitst, name: 'Soleil levant', value: 1.0).save()
		def b = new Portrait(artist: artitst, name: 'Soleil Levant', value: 1.0).save()
		def c = new Portrait(artist: artitst, name: 'The Madonna of Port Lligat', value: 1.0).save()
		def results = Portrait.withCriteria{
			not{
				like('name', 'Soleil%')
			}
		}
		assert [c] == results
	}
	
	void testMissingMethodException(){
		try{
			Portrait.withCriteria{
				myMissingMethod('name', 'Bach')
			}
			fail('where is that method?')
		}catch(MissingMethodException e){
			assert e.message?.contains('.myMissingMethod()')
		}
	}
	
	void testRowCount(){
		def artitst = new Artist(name: 'Brilhante').save()
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
		def b = new Portrait(artist: monet, name: 'Soleil Levant 2', value: 1.0).save()
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

	void testBetween(){
		def artitst = new Artist(name: 'Brilhante').save()
		def a = new Portrait(artist: artitst, name: 'Soleil levant', value: 1.0).save()
		def b = new Portrait(artist: artitst, name: 'Monalisa', value: 10.0).save()
		def c = new Portrait(artist: artitst, name: 'The Madonna of Port Lligat', value: 5.0).save()
		def res = Portrait.withCriteria{
			between('value', 1.0, 7.0)
		}
		assert 2 == res.size()
		assert a == res[0]
		assert c == res[1]
	}
	
	void testEqProperty(){
		def artitst = new Artist(name: 'Brilhante').save()
		def soleill = new Portrait(value: 20.0, lastSoldPrice: 10.0, name: 'Soleil levant',artist: artitst ).save()
		def monalis = new Portrait(value: 10.0, lastSoldPrice: 10.0, name: 'Monalisa', artist: artitst).save()
		def madonna = new Portrait(value: 15.0, lastSoldPrice: 15.0, name: 'The Madonna of Port Lligat', artist: artitst).save()
		def res = Portrait.withCriteria{
			eqProperty('value', 'lastSoldPrice')
		}
		assert 2 == res.size()
		assert monalis == res.first()
		assert madonna == res.last()
	}

	void testGeProperty(){
		def artitst = new Artist(name: 'Brilhante').save()
		def soleill = new Portrait(value: 20.0, lastSoldPrice: 10.0, name: 'Soleil levant',artist: artitst ).save()
		def monalis = new Portrait(value: 10.0, lastSoldPrice: 9.0, name: 'Monalisa', artist: artitst).save()
		def madonna = new Portrait(value: 15.0, lastSoldPrice: 19.0, name: 'The Madonna of Port Lligat', artist: artitst).save()
		def res = Portrait.withCriteria{
			geProperty('lastSoldPrice', 'value')
		}

		assert 1 == res.size()
		assert madonna == res.first()
	}

	void testLeProperty(){
		def artitst = new Artist(name: 'Brilhante').save()
		def soleill = new Portrait(value: 20.0, lastSoldPrice: 40.0, name: 'Soleil levant',artist: artitst ).save()
		def monalis = new Portrait(value: 10.0, lastSoldPrice: 9.0, name: 'Monalisa', artist: artitst).save()
		def madonna = new Portrait(value: 15.0, lastSoldPrice: 50.0, name: 'The Madonna of Port Lligat', artist: artitst).save()
		def res = Portrait.withCriteria{
			leProperty('lastSoldPrice','value')
		}

		assert 1 == res.size()
		assert monalis == res.first()
	}


	void testNeProperty(){
		def artitst = new Artist(name: 'Brilhante').save()
		def soleill = new Portrait(value: 20.0, lastSoldPrice: 40.0, name: 'Soleil levant',artist: artitst ).save()
		def monalis = new Portrait(value: 10.0, lastSoldPrice: 10.0, name: 'Monalisa', artist: artitst).save()
		def res = Portrait.withCriteria{
			neProperty('value','lastSoldPrice')
		}

		assert 1 == res.size()
		assert soleill == res.first()
	}

	void testGtProperty(){
		def artitst = new Artist(name: 'Brilhante').save()
		def soleill = new Portrait(value: 20.0, lastSoldPrice: 40.0, name: 'Soleil levant',artist: artitst ).save()
		def monalis = new Portrait(value: 10.0, lastSoldPrice: 19.0, name: 'Monalisa', artist: artitst).save()
		def res = Portrait.withCriteria{
			gtProperty('lastSoldPrice', 'value')
		}

		assert 2 == res.size()
		assert soleill == res.first()
		assert monalis == res.last()
	}

	void testLtProperty(){
		def artitst = new Artist(name: 'Brilhante').save()
		def soleill = new Portrait(value: 20.0, lastSoldPrice: 40.0, name: 'Soleil levant',artist: artitst ).save()
		def monalis = new Portrait(value: 30.0, lastSoldPrice: 19.0, name: 'Monalisa', artist: artitst).save()
		def res = Portrait.withCriteria{
			ltProperty('value', 'lastSoldPrice')
		}

		assert 1 == res.size()
		assert soleill == res.first()
	}
	
	void testProjectionProperty(){
		def monet = new Artist(name: 'Monet').save()
		
		new Portrait(artist: monet, name: 'Soleil levant 1', value: 1.0).save()
		new Portrait(artist: monet, name: 'Soleil levant 2', value: 1.0).save()
		new Portrait(artist: monet, name: 'Soleil levant 3', value: 1.0).save()
		
		def rs = Portrait.withCriteria {
			projections {
				property('artist')
			}
		}
		
		assert 3 == rs.size()
		rs.each{
			assert it instanceof Artist
			assert it.name == 'Monet'
		}
	}
	
	void testProjectionProperties(){
		def monet = new Artist(name: 'Monet').save()
		
		new Portrait(artist: monet, name: 'Soleil levant 1', value: 1.0).save()
		new Portrait(artist: monet, name: 'Soleil levant 2', value: 1.0).save()
		new Portrait(artist: monet, name: 'Soleil levant 3', value: 1.0).save()
		
		def rs = Portrait.withCriteria {
			projections {
				property('artist')
				property('value')
			}
		}
		
		assert 3 == rs.size()
		rs.each{
			assert it[0] instanceof Artist
			assert it[0].name == 'Monet'
			assert it[1] == 1.0
		}
	}
	
	void testProjectionPropertyAndSum(){
		def monet = new Artist(name: 'Monet').save()
		
		new Portrait(artist: monet, name: 'Soleil levant 1', value: 1.0).save()
		new Portrait(artist: monet, name: 'Soleil levant 2', value: 1.0).save()
		new Portrait(artist: monet, name: 'Soleil levant 3', value: 1.0).save()
		
		def rs = Portrait.withCriteria {
			projections {
				property('artist')
				sum('value')
			}
		}
		
		assert 1 == rs.size()
		assert [[monet, 3.0]] == rs
	}
	
	void testOrderBy(){
		def a = new Artist(name: 'Andreas Achenbach').save()
		def c = new Artist(name: 'Constance Gordon-Cumming').save()
		def b = new Artist(name: 'Botero').save()
		new Portrait(artist: a, name: "Clearing Up—Coast of Sicily").save()
		new Portrait(artist: c, name: "Indian Life at Mirror Lake").save()
		new Portrait(artist: c, name: "Temporary Chimneys and Fire Fountains").save()
		new Portrait(artist: b, name: "Botero's Cat").save()
		def artistList = Portrait.withCriteria{
			artist{
				order('name', 'asc')
			}
			projections{
				artist{
					distinct('name')
				}
			}
		}
		assert ['Andreas Achenbach', 'Botero', 'Constance Gordon-Cumming'] == artistList
	}
	
}
