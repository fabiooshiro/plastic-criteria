package plastic.criteria

import static org.junit.Assert.*

import org.hibernate.FetchMode
import org.hibernate.criterion.CriteriaSpecification

class CriteriaDocTests {

	void testGroupProperty() {
		def pablo = new Artist(name: 'Pablo').save()
		def salvador = new Artist(name: 'Salvador').save()
		new Portrait(artist: pablo, name: "Les Demoiselles d'Avignon", value: 10.00).save()
		new Portrait(artist: pablo, name: "Les Noces de Pierrette", value: 22.00, color: 'blue').save()
		new Portrait(artist: salvador, name: "The Persistence of Memory", value: 20.00).save()
		def artistValue = Portrait.withCriteria {
			projections {
				sum('value')
				groupProperty('artist')
			}
		}
		assert [[32.00, pablo], [20.00, salvador]] ==  artistValue
	}

	void test2xGroupProperty() {
		def pablo = new Artist(name: 'Pablo').save()
		def salvador = new Artist(name: 'Salvador').save()
		new Portrait(artist: pablo, name: "Les Demoiselles d'Avignon 1", value: 10.00).save()
		new Portrait(artist: pablo, name: "Les Demoiselles d'Avignon 2", value: 10.00).save()
		new Portrait(artist: pablo, name: "Les Demoiselles d'Avignon 3", value: 10.00).save()
		new Portrait(artist: salvador, name: "The Persistence of Memory 1", value: 20.00).save()
		new Portrait(artist: salvador, name: "The Persistence of Memory 2", value: 20.00).save()
		new Portrait(artist: salvador, name: "The Persistence of Memory 3", value: 20.00).save()
		def artistValue = Portrait.withCriteria {
			projections {
				groupProperty('value')
				groupProperty('artist')
			}
		}
		assert [[10.00, pablo], [20.00, salvador]].toSet() ==  artistValue.toSet()
	}

	void testAnd() {
		def pablo = new Artist(name: 'Pablo').save()
		new Portrait(artist: pablo, name: "Les Demoiselles d'Avignon", value: 10.00, color: 'orange').save()
		new Portrait(artist: pablo, name: "Les Noces de Pierrette", value: 22.00, color: 'blue').save()

		def portraits = Portrait.withCriteria {
			artist {
				eq('name', 'Pablo')
			}
			eq('color', 'blue')
		}

		assert 1 == portraits.size()
		assert "Les Noces de Pierrette" == portraits[0].name
	}

	void testOr() {
		def artitst = new Artist(name: 'Brilhante').save()
		def plastic1 = new Portrait(artist: artitst, name: 'Soleil levant').save()
		def plastic2 = new Portrait(artist: artitst, name: 'The Madonna of Port Lligat').save()
		def plastic3 = new Portrait(artist: artitst, name: "Les Demoiselles d'Avignon").save()
		def ls = Portrait.withCriteria {
			or {
				eq('name', 'Soleil levant')
				eq('name', 'The Madonna of Port Lligat')
			}
		}

		assert [plastic1, plastic2] == ls
	}

	void testAvg() {
		def artitst = new Artist(name: 'Brilhante').save()
		new Portrait(artist: artitst, name: 'Soleil levant', value: 1.0).save()
		new Portrait(artist: artitst, name: 'The Madonna of Port Lligat', value: 2.0).save()
		new Portrait(artist: artitst, name: "Les Demoiselles d'Avignon", value: 3.0).save()
		def average = Portrait.createCriteria().get {
			projections {
				avg('value')
			}
		}
		assert 2.0 == average
	}

	void testSum() {
		def artitst = new Artist(name: 'Brilhante').save()
		new Portrait(artist: artitst, name: 'Soleil levant', value: 1.0).save()
		new Portrait(artist: artitst, name: 'The Madonna of Port Lligat', value: 2.0).save()
		new Portrait(artist: artitst, name: "Les Demoiselles d'Avignon", value: 3.0).save()
		def total = Portrait.createCriteria().get {
			projections {
				sum('value')
			}
		}
		assert 6.0 == total
	}

	void testMin() {
		def artitst = new Artist(name: 'Brilhante').save()
		new Portrait(artist: artitst, name: 'Soleil levant', value: 1.0).save()
		new Portrait(artist: artitst, name: 'The Madonna of Port Lligat', value: 2.0).save()
		new Portrait(artist: artitst, name: "Les Demoiselles d'Avignon", value: 3.0).save()
		def res = Portrait.createCriteria().get {
			projections {
				min('value')
			}
		}
		assert 1.0 == res
	}

	void testMax() {
		def artitst = new Artist(name: 'Brilhante').save()
		new Portrait(artist: artitst, name: 'Soleil levant', value: 1.0).save()
		new Portrait(artist: artitst, name: 'The Madonna of Port Lligat', value: 2.0).save()
		new Portrait(artist: artitst, name: "Les Demoiselles d'Avignon", value: 3.0).save()
		def res = Portrait.createCriteria().get {
			projections {
				max('value')
			}
		}
		assert 3.0 == res
	}

	void testIgnoreCase() {
		def artitst = new Artist(name: 'Brilhante').save()
		def a = new Portrait(artist: artitst, name: 'Soleil levant', value: 1.0).save()
		def b = new Portrait(artist: artitst, name: 'Soleil Levant', value: 1.0).save()
		def c = new Portrait(artist: artitst, name: 'The Madonna of Port Lligat', value: 1.0).save()
		def results = Portrait.withCriteria {
			eq('name', 'SOLEIL LEVANT', [ignoreCase: true])
		}

		assert [a, b] == results
	}

	void testLike() {
		def artitst = new Artist(name: 'Brilhante').save()
		def a = new Portrait(artist: artitst, name: 'Soleil levant', value: 1.0).save()
		def b = new Portrait(artist: artitst, name: 'Soleil Levant', value: 1.0).save()
		def c = new Portrait(artist: artitst, name: 'The Madonna of Port Lligat', value: 1.0).save()
		def results = Portrait.withCriteria {
			like('name', 'Soleil%')
		}
		assert [a, b] == results
	}

	void testNot() {
		def artitst = new Artist(name: 'Brilhante').save()
		def a = new Portrait(artist: artitst, name: 'Soleil levant', value: 1.0).save()
		def b = new Portrait(artist: artitst, name: 'Soleil Levant', value: 1.0).save()
		def c = new Portrait(artist: artitst, name: 'The Madonna of Port Lligat', value: 1.0).save()
		def results = Portrait.withCriteria {
			not {
				like('name', 'Soleil%')
			}
		}
		assert [c] == results
	}

	void testMissingMethodException() {
		try {
			Portrait.withCriteria {
				myMissingMethod('name', 'Bach')
			}
			fail('where is that method?')
		} catch(MissingMethodException e) {
			assert e.message?.contains('.myMissingMethod()')
		}
	}

	void testRowCount() {
		def artitst = new Artist(name: 'Brilhante').save()
		def a = new Portrait(artist: artitst, name: 'Soleil levant', value: 1.0).save()
		def b = new Portrait(artist: artitst, name: 'Soleil Levant', value: 1.0).save()
		def c = new Portrait(artist: artitst, name: 'The Madonna of Port Lligat', value: 1.0).save()
		def res = Portrait.withCriteria {
			projections {
				rowCount()
			}
		}
		assert 3 == res[0]
	}

	// not working in H2
	void xtestRowCountAndGroupProperty() {
		def monet = new Artist(name: 'Monet').save()
		def salvador = new Artist(name: 'Salvador').save()
		def a = new Portrait(artist: monet, name: 'Soleil levant', value: 1.0).save()
		def b = new Portrait(artist: monet, name: 'Soleil Levant 2', value: 1.0).save()
		def c = new Portrait(artist: salvador, name: 'The Madonna of Port Lligat', value: 1.0).save()
		def res = Portrait.withCriteria {
			projections {
				rowCount()
				groupProperty('artist')
			}
		}
		assert 2 == res.find{ it[1].name == 'Monet' }[0]
		assert 1 == res.find{ it[1].name == 'Salvador' }[0]
	}

	void testBetween() {
		def artitst = new Artist(name: 'Brilhante').save()
		def a = new Portrait(artist: artitst, name: 'Soleil levant', value: 1.0).save()
		def b = new Portrait(artist: artitst, name: 'Monalisa', value: 10.0).save()
		def c = new Portrait(artist: artitst, name: 'The Madonna of Port Lligat', value: 5.0).save()
		def res = Portrait.withCriteria {
			between('value', 1.0, 7.0)
		}
		assert 2 == res.size()
		assert a == res[0]
		assert c == res[1]
	}

	void testEqProperty() {
		def artitst = new Artist(name: 'Brilhante').save()
		def soleill = new Portrait(value: 20.0, lastSoldPrice: 10.0, name: 'Soleil levant',artist: artitst ).save()
		def monalis = new Portrait(value: 10.0, lastSoldPrice: 10.0, name: 'Monalisa', artist: artitst).save()
		def madonna = new Portrait(value: 15.0, lastSoldPrice: 15.0, name: 'The Madonna of Port Lligat', artist: artitst).save()
		def res = Portrait.withCriteria {
			eqProperty('value', 'lastSoldPrice')
		}
		assert 2 == res.size()
		assert monalis == res.first()
		assert madonna == res.last()
	}

	void testGeProperty() {
		def artitst = new Artist(name: 'Brilhante').save()
		def soleill = new Portrait(value: 20.0, lastSoldPrice: 10.0, name: 'Soleil levant',artist: artitst ).save()
		def monalis = new Portrait(value: 10.0, lastSoldPrice: 9.0, name: 'Monalisa', artist: artitst).save()
		def madonna = new Portrait(value: 15.0, lastSoldPrice: 19.0, name: 'The Madonna of Port Lligat', artist: artitst).save()
		def res = Portrait.withCriteria {
			geProperty('lastSoldPrice', 'value')
		}

		assert 1 == res.size()
		assert madonna == res.first()
	}

	void testLeProperty() {
		def artitst = new Artist(name: 'Brilhante').save()
		def soleill = new Portrait(value: 20.0, lastSoldPrice: 40.0, name: 'Soleil levant',artist: artitst ).save()
		def monalis = new Portrait(value: 10.0, lastSoldPrice: 9.0, name: 'Monalisa', artist: artitst).save()
		def madonna = new Portrait(value: 15.0, lastSoldPrice: 50.0, name: 'The Madonna of Port Lligat', artist: artitst).save()
		def res = Portrait.withCriteria {
			leProperty('lastSoldPrice','value')
		}

		assert 1 == res.size()
		assert monalis == res.first()
	}


	void testNeProperty() {
		def artitst = new Artist(name: 'Brilhante').save()
		def soleill = new Portrait(value: 20.0, lastSoldPrice: 40.0, name: 'Soleil levant',artist: artitst ).save()
		def monalis = new Portrait(value: 10.0, lastSoldPrice: 10.0, name: 'Monalisa', artist: artitst).save()
		def res = Portrait.withCriteria {
			neProperty('value','lastSoldPrice')
		}

		assert 1 == res.size()
		assert soleill == res.first()
	}

	void testGtProperty() {
		def artitst = new Artist(name: 'Brilhante').save()
		def soleill = new Portrait(value: 20.0, lastSoldPrice: 40.0, name: 'Soleil levant',artist: artitst ).save()
		def monalis = new Portrait(value: 10.0, lastSoldPrice: 19.0, name: 'Monalisa', artist: artitst).save()
		def res = Portrait.withCriteria {
			gtProperty('lastSoldPrice', 'value')
		}

		assert 2 == res.size()
		assert soleill == res.first()
		assert monalis == res.last()
	}

	void testLtProperty() {
		def artitst = new Artist(name: 'Brilhante').save()
		def soleill = new Portrait(value: 20.0, lastSoldPrice: 40.0, name: 'Soleil levant',artist: artitst ).save()
		def monalis = new Portrait(value: 30.0, lastSoldPrice: 19.0, name: 'Monalisa', artist: artitst).save()
		def res = Portrait.withCriteria {
			ltProperty('value', 'lastSoldPrice')
		}

		assert 1 == res.size()
		assert soleill == res.first()
	}

	void testProjectionProperty() {
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
		rs.each {
			assert it instanceof Artist
			assert it.name == 'Monet'
		}
	}

	void testProjectionProperties() {
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
		rs.each {
			assert it[0] instanceof Artist
			assert it[0].name == 'Monet'
			assert it[1] == 1.0
		}
	}

	void testProjectionPropertyAndSum() {
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

	void testOrderBy() {
		def a = new Artist(name: 'Andreas Achenbach').save()
		def c = new Artist(name: 'Constance Gordon-Cumming').save()
		def b = new Artist(name: 'Botero').save()
		new Portrait(artist: a, name: "Clearing Up—Coast of Sicily").save()
		new Portrait(artist: c, name: "Indian Life at Mirror Lake").save()
		new Portrait(artist: c, name: "Temporary Chimneys and Fire Fountains").save()
		new Portrait(artist: b, name: "Botero's Cat").save()
		def artistList = Portrait.withCriteria {
			artist {
				order('name', 'asc')
			}
			projections {
				artist {
					distinct('name')
				}
			}
		}
		assert ['Andreas Achenbach', 'Botero', 'Constance Gordon-Cumming'] == artistList
	}

	// version 0.5
	void testDistinctWithArrayParam() {
		def b = new Artist(name: 'Tomie Oshiro').save()
		new Portrait(artist: b, color: 'Ame', name: 'Cat').save() // Ame == yellow
		new Portrait(artist: b, color: 'Blue', name: 'Fox').save()
		new Portrait(artist: b, color: 'Ame', name: 'Cat').save()
		new Portrait(artist: b, color: 'Blue', name: 'Cat').save()
		def artistList = Portrait.withCriteria{
			projections {
				distinct(['color', 'name'])
			}
		}
		assert ([
			['Ame', 'Cat'],
			['Blue', 'Fox'],
			['Blue', 'Cat'],
		] as Set) == artistList as Set
	}

	void test_sum_null() {
		def monet = new Artist(name: 'Monet').save()

		new Portrait(artist: monet, name: 'Soleil levant 1').save()
		new Portrait(artist: monet, name: 'Soleil levant 2').save()
		new Portrait(artist: monet, name: 'Soleil levant 3').save()

		def rs = Portrait.withCriteria {
			projections {
				property('artist')
				sum('value')
			}
		}

		assert 1 == rs.size()
		assert [[monet, null]] == rs
	}

	void test_sum_with_null() {
		def monet = new Artist(name: 'Monet').save()

		new Portrait(artist: monet, name: 'Soleil levant 1').save()
		new Portrait(artist: monet, name: 'Soleil levant 2', value: 1.1).save()
		new Portrait(artist: monet, name: 'Soleil levant 3').save()

		def rs = Portrait.withCriteria {
			projections {
				property('artist')
				sum('value')
			}
		}

		assert 1 == rs.size()
		assert [[monet, 1.1]] == rs
	}

	// version 0.6
	void test_fetch_mode() {
		def monet = new Artist(name: 'Monet').save()
		new Portrait(artist: monet, name: 'Soleil levant 1').save()
		def rs = Portrait.withCriteria {
			eq('artist', monet)
	        fetchMode('artist', FetchMode.JOIN)
    	}
    	assert 1 == rs.size()
	}

	void test_unique_result() {
		def monet = new Artist(name: 'Monet').save()
		def portrait = new Portrait(artist: monet, name: 'Soleil levant 1').save()
		def result = Portrait.withCriteria {
			eq('artist', monet)
	        uniqueResult = true
    	}
    	assert result == portrait
	}

	void test_unique_result_exception() {
		def monet = new Artist(name: 'Monet').save()
		new Portrait(artist: monet, name: 'Soleil levant 1').save()
		new Portrait(artist: monet, name: 'Soleil levant 1').save()
		try {
			Portrait.withCriteria {
				eq('artist', monet)
		        uniqueResult = true
	    	}
	    	fail("should throw an exception")
    	} catch (org.hibernate.NonUniqueResultException e) {
    		// ok
    	}
	}

	void test_unique_result_null() {
		def monet = new Artist(name: 'Monet').save()
		def res = Portrait.withCriteria {
			eq('artist', monet)
	        uniqueResult = true
    	}
	    assert res == null
	}

	void test_plastic_criteria_over_arrayList() {
		def ls = [
			[name: 'monet', bestPlace: [name: 'Japanese Bridge']],
			[name: 'salvador', bestPlace: [name: 'Catalunya']],
		]
		def rs = new PlasticCriteria(ls).list {
			bestPlace {
				eq('name', 'Japanese Bridge')
			}
		}
		assert 1 == rs.size()
		assert 'Japanese Bridge' == rs[0].bestPlace.name
	}

	// version 0.7
	void test_list_params_max() {
		def monet = new Artist(name: 'Monet').save()
		new Portrait(artist: monet, name: 'Soleil levant 1').save()
		new Portrait(artist: monet, name: 'Soleil levant 2').save()
		new Portrait(artist: monet, name: 'Soleil levant 3').save()
		new Portrait(artist: monet, name: 'Soleil levant 4').save()
		new Portrait(artist: monet, name: 'Soleil levant 5').save()
		new Portrait(artist: monet, name: 'Soleil levant 6').save()
		new Portrait(artist: monet, name: 'Soleil levant 7').save()
		def rs = Portrait.createCriteria().list([max: 3]) {
			eq('artist', monet)
		}
		assert 3 == rs.size()
	}

	void test_list_params_max_and_offset() {
		def monet = new Artist(name: 'Monet').save()
		new Portrait(artist: monet, name: 'Soleil levant 1').save()
		new Portrait(artist: monet, name: 'Soleil levant 2').save()
		new Portrait(artist: monet, name: 'Soleil levant 3').save()
		new Portrait(artist: monet, name: 'Soleil levant 4').save()
		new Portrait(artist: monet, name: 'Soleil levant 5').save()
		new Portrait(artist: monet, name: 'Soleil levant 6').save()
		new Portrait(artist: monet, name: 'Soleil levant 7').save()
		def rs = Portrait.createCriteria().list([max: 3, offset: 2]) {
			eq('artist', monet)
		}
		assert 3 == rs.size()
		assert 'Soleil levant 3' == rs[0].name
		assert 'Soleil levant 4' == rs[1].name
		assert 'Soleil levant 5' == rs[2].name
	}

	void test_list_params_sort() {
		def monet = new Artist(name: 'Monet').save()
		new Portrait(artist: monet, name: 'Soleil levant 2').save()
		new Portrait(artist: monet, name: 'Soleil levant 1').save()
		new Portrait(artist: monet, name: 'Soleil levant 3').save()
		def rs = Portrait.createCriteria().list([sort: 'name']) {
			eq('artist', monet)
		}
		assert 'Soleil levant 1' == rs[0].name
		assert 'Soleil levant 2' == rs[1].name
		assert 'Soleil levant 3' == rs[2].name
	}

	void test_list_params_sort_order() {
		def monet = new Artist(name: 'Monet').save()
		new Portrait(artist: monet, name: 'Soleil levant 2').save()
		new Portrait(artist: monet, name: 'Soleil levant 3').save()
		new Portrait(artist: monet, name: 'Soleil levant 1').save()
		def rs = Portrait.createCriteria().list([sort: 'name', order: 'desc']) {
			eq('artist', monet)
		}
		assert 'Soleil levant 3' == rs[0].name
		assert 'Soleil levant 2' == rs[1].name
		assert 'Soleil levant 1' == rs[2].name
	}

	// version 0.8
	void test_bug_list_size_smaller_than_max_results() {
		def monet = new Artist(name: 'Monet').save()
		new Portrait(artist: monet, name: 'Soleil levant 2').save()
		new Portrait(artist: monet, name: 'Soleil levant 3').save()
		new Portrait(artist: monet, name: 'Soleil levant 1').save()
		def rs = Portrait.createCriteria().list([sort: 'name', order: 'desc']) {
			maxResults(5)
		}
		assert 3 == rs.size()
	}

	void test_list_size_smaller_than_offset_case1() {
		def monet = new Artist(name: 'Monet').save()
		new Portrait(artist: monet, name: 'Soleil levant 1').save()
		def rs = Portrait.createCriteria().list([max: 3, offset: 5]) {
			eq('artist', monet)
		}
		assert 0 == rs.size()
	}

	void test_list_size_smaller_than_offset_case2() {
		def monet = new Artist(name: 'Monet').save()
		new Portrait(artist: monet, name: 'Soleil levant 1').save()
		new Portrait(artist: monet, name: 'Soleil levant 2').save()
		def rs = Portrait.createCriteria().list([max: 3, offset: 1]) {
			eq('artist', monet)
		}
		assert 1 == rs.size()
		assert 'Soleil levant 2' == rs[0].name
	}

	void test_list_size_smaller_than_offset_case3(){
		def monet = new Artist(name: 'Monet').save()
		new Portrait(artist: monet, name: 'Soleil levant 1').save()
		new Portrait(artist: monet, name: 'Soleil levant 2').save()
		def rs = Portrait.createCriteria().list([max: 3, offset: 2]){
			eq('artist', monet)
		}
		assert 0 == rs.size()
	}

	// version 0.9
	void test_nested_object() {
		def paris = new City(name: 'Paris').save()
		def monet = new Artist(name: 'Monet', city: paris).save()
		new Portrait(artist: monet, name: 'Soleil levant 1').save()

		def rio = new City(name: 'Rio de Janeiro').save()
		def portinari = new Artist(name: 'Portinari', city: rio).save()
		new Portrait(artist: portinari, name: 'Retirantes').save()
		new Portrait(artist: portinari, name: 'Paisagem de Brodowski').save()

		def diCavalcanti = new Artist(name: 'Di Cavalcanti', city: rio).save()
		new Portrait(artist: diCavalcanti, name: 'Autorretrato Com Mulata').save()
		
		def rs = Portrait.withCriteria {
			artist {
				city {
					eq('name', 'Rio de Janeiro')
				}
			}
			order('name', 'asc')
		}
		assert 3 == rs.size()
		assert ['Autorretrato Com Mulata', 'Paisagem de Brodowski', 'Retirantes'] == rs.name
	}

	// version 1.0
	void test_bugfix_Negative_array_index_too_large_for_array_size_0() {
		Portrait.createCriteria().get {
			maxResults(1)
		}
	}

	// version 1.2
	void test_createAlias() {
		def paris = new City(name: 'Paris').save()
		def monet = new Artist(name: 'Monet', city: paris).save()
		new Portrait(artist: monet, name: 'Soleil levant 1').save()

		def rio = new City(name: 'Rio de Janeiro').save()
		def portinari = new Artist(name: 'Portinari', city: rio).save()
		new Portrait(artist: portinari, name: 'Retirantes').save()
		new Portrait(artist: portinari, name: 'Paisagem de Brodowski').save()

		def diCavalcanti = new Artist(name: 'Di Cavalcanti', city: rio).save()
		new Portrait(artist: diCavalcanti, name: 'Autorretrato Com Mulata').save()
		
		def rs = Portrait.withCriteria {
			createAlias('artist', 'genius')
			eq('genius.city', rio)
			order('name', 'asc')
		}
		assert 3 == rs.size()
		assert ['Autorretrato Com Mulata', 'Paisagem de Brodowski', 'Retirantes'] == rs.name
	}

	// version 1.3
	void test_avg_division_undefined() {
		def artitst = new Artist(name: 'Brilhante').save()
		new Portrait(artist: artitst, name: 'Soleil levant', value: 1.0).save()
		new Portrait(artist: artitst, name: 'The Madonna of Port Lligat', value: 2.0).save()
		new Portrait(artist: artitst, name: "Les Demoiselles d'Avignon", value: 3.0).save()
		def average = Portrait.createCriteria().get {
			eq('name', 'not exists')
			projections {
				avg('value')
			}
		}
		assert null == average
	}

	// version 1.4
	void test_setReadOnly() {
		def artitst = new Artist(name: 'Brilhante').save()
		new Portrait(artist: artitst, name: 'Soleil levant', value: 1.0).save()
		new Portrait(artist: artitst, name: 'The Madonna of Port Lligat', value: 2.0).save()
		new Portrait(artist: artitst, name: "Les Demoiselles d'Avignon", value: 3.0).save()
		def list = Portrait.withCriteria {
			setReadOnly(true)
		}
		assert 3 == list.size()
	}

	// version 1.4.1
	void test_fix_null_pointer_in_getProperty() {
		def monet = null
		new Portrait(artist: monet, name: 'Soleil levant 1').save()

		def rio = new City(name: 'Rio de Janeiro').save()
		def portinari = new Artist(name: 'Portinari', city: rio).save()
		new Portrait(artist: portinari, name: 'Retirantes').save()
		new Portrait(artist: portinari, name: 'Paisagem de Brodowski').save()

		def diCavalcanti = new Artist(name: 'Di Cavalcanti', city: rio).save()
		new Portrait(artist: diCavalcanti, name: 'Autorretrato Com Mulata').save()
		
		assert 4 == Portrait.count()
		def rs = Portrait.withCriteria {
			artist {
				city {
					eq('name', 'Rio de Janeiro')
				}
			}
			order('name', 'asc')
		}
		assert 3 == rs.size()
		assert ['Autorretrato Com Mulata', 'Paisagem de Brodowski', 'Retirantes'] == rs.name
	}

	// version 1.5
	void test_listDistinct() {
		def salvador = new Artist(name: 'Salvador').save()
		new Portrait(artist: salvador, name: 'The Madonna of Port Lligat', value: 2.0).save()
		new Portrait(artist: salvador, name: "The Persistence of Memory", value: 20.00).save()

		def pablo = new Artist(name: 'Pablo').save()
		new Portrait(artist: pablo, name: "Les Demoiselles d'Avignon", value: 10.00).save()
		def list = Portrait.createCriteria().listDistinct {
			artist {
				eq('name', 'Salvador')
			}
		}
		assert 2 == list.size()
	}

	// version 1.5.1
	void test_inList() {
		def portinari = new Artist(name: 'Portinari').save()
		new Portrait(artist: portinari, name: 'Retirantes').save()
		new Portrait(artist: portinari, name: 'Paisagem de Brodowski').save()

		def salvador = new Artist(name: 'Salvador').save()
		new Portrait(artist: salvador, name: 'The Madonna of Port Lligat', value: 2.0).save()
		new Portrait(artist: salvador, name: "The Persistence of Memory", value: 20.00).save()

		def pablo = new Artist(name: 'Pablo').save()
		new Portrait(artist: pablo, name: "Les Demoiselles d'Avignon", value: 10.00).save()
		def list = Portrait.withCriteria {
			inList('artist', [pablo, salvador])
		}
		assert list.size() == 3
	}

    // version 1.5.2
    void testCreateAliasJoins() {
        def portinari = new Artist(name: "Portinari").save()
        def leftJoinList = Artist.withCriteria {
            createAlias("portraits", "ps", CriteriaSpecification.LEFT_JOIN) 
            eq("name", portinari.name)
        }
        assert leftJoinList.size() == 1
        def innerJoinList = Artist.withCriteria {
            createAlias("portraits", "ps", CriteriaSpecification.INNER_JOIN)
            eq("name", portinari.name)
        }
        assert innerJoinList.size() == 0
        // No join specified, defaults to inner join anyway
        def defaultJoinList = Artist.withCriteria {
            createAlias("portraits", "ps")
            eq("name", portinari.name)
        }
        assert defaultJoinList.size() == 0
    }

    // version 1.5.2
    void testCriteriaCount() {
        def portinari = new Artist(name: 'Portinari').save()
        new Portrait(artist: portinari, name: 'Retirantes').save()
        new Portrait(artist: portinari, name: 'Paisagem de Brodowski').save()
        def list = Portrait.withCriteria {
            artist {
                eq("name", portinari.name)
            }
        }
        assert list.size() == 2
        def count = Portrait.createCriteria().count {
            artist {
                eq("name", portinari.name)
            }
        }
        assert count == 2
    }

    // version 1.5.3
    void testEmptinessRestriction() {
        def portinari = new Artist(name: 'Portinari').save()
        new Portrait(artist: portinari, name: 'Retirantes').save()
        new Portrait(artist: portinari, name: 'Paisagem de Brodowski').save()
        def pablo = new Artist(name: 'Pablo').save()
        def result1 = Artist.withCriteria {
            isEmpty("portraits")
        }
        assert result1.size() == 1
        assert result1[0] == pablo
        def result2 = Artist.withCriteria {
            isNotEmpty("portraits")
        }
        assert result2.size() == 1
        assert result2[0] == portinari
    }

    // version 1.5.4
    void testEqForCollections() {
        def portinari = new Artist(name: 'Portinari').save()
        new Portrait(artist: portinari, name: 'Retirantes').save()
        new Portrait(artist: portinari, name: 'Paisagem de Brodowski').save()
        def pablo = new Artist(name: 'Pablo').save()
        new Portrait(artist: pablo, name: 'Les Demoiselles d’Avignon').save()
        new Portrait(artist: pablo, name: 'Asleep').save()

        def result1 = Artist.withCriteria {
            createAlias 'portraits', 'portraits'
            eq 'portraits.name', 'something'
        }
        assert result1.size() == 0

        def result2 = Artist.withCriteria {
            createAlias 'portraits', 'portraits'
            eq 'portraits.name', 'Retirantes'
        }
        assert result2.size() == 1
        assert result2[0] == portinari

        def result3 = Artist.withCriteria {
            createAlias 'portraits', 'portraits'
            eq 'portraits.name', 'asleep', [ignoreCase: true]
        }
        assert result3.size() == 1
        assert result3[0] == pablo
    }
}
