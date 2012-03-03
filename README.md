PlasticCriteria
===================

From FakePlasticCriteria renamed to just PlasticCriteria.

The project is licensed under the WTFPL.
http://sam.zoy.org/wtfpl/COPYING


# Installation

Download latest version https://github.com/fabiooshiro/plastic-criteria/downloads
and run

```
grails install-plugin grails-plastic-criteria-0.1.zip
```

# Sample usage

```groovy
package plastic.test

import grails.test.mixin.*
import org.junit.*
import static plastic.criteria.PlasticCriteria.*;

@TestFor(Product)
class ProductTests {
  
    void testSomething() {
		new Product(name: 'Foo', value: 10).save()
		new Product(name: 'Foo', value: 20).save()
		new Product(name: 'Bar', value: 200).save()
		new Product(name: 'Bar', value: 100).save()
		
		mockCriteria([Product])
		
		def results = Product.withCriteria{
			projections{
				sum('value')
				groupProperty('name')
			}
		}
		
		assert [[30, 'Foo'], [300, 'Bar']]
    }
}

```
just
```
import static plastic.criteria.PlasticCriteria.*;
```
and 
```
mockCriteria([Product])
```
