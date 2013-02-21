PlasticCriteria
===================

From FakePlasticCriteria renamed to just PlasticCriteria.

The project is licensed under the WTFPL.
http://sam.zoy.org/wtfpl/COPYING


# Installation

For grails 1.3.x

```
grails install-plugin plastic-criteria
```

Grails 2.0.x edit your <your-project>/grails-app/conf/BuildConfig.groovy

```groovy
    // (...)
    plugins {
        // (...) another plugins

        // add this line
        test ":plastic-criteria:0.6"
    }
    // (...)
```

# Sample usage

```groovy
package plastic.test

import grails.test.mixin.*

// import mockCriteria() static method
import static plastic.criteria.PlasticCriteria.*

@TestFor(Product)
class ProductTests {

    void testSomething() {
		new Product(name: 'Foo', value: 10).save()
		new Product(name: 'Foo', value: 20).save()
		new Product(name: 'Bar', value: 200).save()
		new Product(name: 'Bar', value: 100).save()

		// replace default criteria mock
		mockCriteria([Product])

		def results = Product.withCriteria{
			projections{
				groupProperty('name') // now you have groupProperty
				sum('value')
			}
		}

		assert [['Foo', 30 ], ['Bar', 300]] == results
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

## Committers
<a href="https://twitter.com/fabiooshiro">Sr. Oshiro</a>,
<a href="http://www.facebook.com/MaxMustang23">Max Mustang</a>,
<a href="https://twitter.com/dtuler">Danilo Tuler</a>
