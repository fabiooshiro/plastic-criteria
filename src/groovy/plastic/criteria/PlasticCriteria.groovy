package plastic.criteria;

public class PlasticCriteria {
	def _clazz
	def _maxRes
	def _props = []
	def _hasCalcProp = false
	def _groupProps = []
	def _orders = []
	def _distinctProp
	def _leCriticalList = [tp: 'and', ls: []]
	def _prefix = ''
	
	def _instanceValue
	def _criteriaValue
	def _critOptions
	def theImplementations = [
		"le":{ _instanceValue <= _criteriaValue },
		"lt":{ _instanceValue  < _criteriaValue },
		"gt":{ _instanceValue  > _criteriaValue },
		"ge":{ _instanceValue >= _criteriaValue },
		"eq":{ (_critOptions?.ignoreCase) ? _instanceValue?.toLowerCase() == _criteriaValue?.toLowerCase()	: _instanceValue == _criteriaValue },
		"in":{ _instanceValue in _criteriaValue },
		"ne":{ _instanceValue != _criteriaValue },
		"ilike":{ ('' + _instanceValue).toLowerCase() ==~ _criteriaValue.replace('%','.*').toLowerCase() },
		"like":{ ('' + _instanceValue) ==~ _criteriaValue.replace('%','.*') },
		"isNull":{ _instanceValue == null },
		"isNotNull":{ _instanceValue != null },
		"eqProperty":{ _instanceValue == _criteriaValue },
		"geProperty":{ _instanceValue >= _criteriaValue },
		"leProperty":{ _instanceValue <= _criteriaValue},
		"neProperty":{ _instanceValue != _criteriaValue },
		"gtProperty":{ _instanceValue > _criteriaValue },
		"ltProperty":{ _instanceValue < _criteriaValue }
	]
	
	public PlasticCriteria(clazz){
		this._clazz = clazz
	}
	
	public PlasticCriteria(clazz, pref){
		_prefix = pref + '.'
	}
	
	static void mockCriteria(List clazzes){
		clazzes.each{
			mockCriteria(it)
		}	
	}
	
	static void mockCriteria(Class clazz){
		clazz.metaClass.'static'.withCriteria = { Closure cls ->
			new PlasticCriteria(clazz).list(cls)
		}
		clazz.metaClass.'static'.createCriteria = {
			new PlasticCriteria(clazz)
		}
	}

	def between(prop, firstValue, secondValue){
		ge(prop, firstValue)
		le(prop, secondValue)
	}
	
	def listDistinct(cls){
		
	}
	
	def maxResults(limit){
		_maxRes = limit
	}
	
	def distinct(prop){
		_distinctProp = prop
	}
	
	def property(prop){
		_props.add(prop)
	}
	
	def min(prop){
		_hasCalcProp = true
		_props.add("min $prop")
	}
	
	def max(prop){
		_hasCalcProp = true
		_props.add("max $prop")
	}
	
	def sum(prop){
		_hasCalcProp = true
		_props.add("sum $prop")
	}
	
	def avg(prop){
		_hasCalcProp = true
		_props.add("avg $prop")
	}
	
	def groupProperty(prop){
		_groupProps.add(prop)
		_props.add(prop)
	}
	
	def rowCount(){
		_hasCalcProp = true
		_props.add("rowCount ")
	}
	
	def projections(clos){
		clos.delegate = this
		clos()
	}
	
	def order(prop, order){
		_orders.add("${prop} ${order}")
	}
	
	def methodMissing(String name, args){
		if(theImplementations.containsKey(name)){
			_leCriticalList.ls.add([criteriaName: name, prop: _prefix + args[0], val: ((args.length > 1) ? args[1] : 'null'), opt: ((args.length > 2) ? args[2] : [:])])
		}else{
			if(!args || !(args[0] instanceof Closure)) throw new MissingMethodException(name, this.class, args)
			def fc = new PlasticCriteria(this._clazz, name)
			args[0].resolveStrategy = Closure.DELEGATE_FIRST
			args[0].delegate = fc
			args[0]()
			fc._leCriticalList.ls.each{ v ->
				this._leCriticalList.ls.add(v)
			}
			fc._orders.each{
				this._orders.add((name + '.' + it)) 
			}
			if(fc._distinctProp){
				this._distinctProp = name + '.' + fc._distinctProp
			}
		}
	}
	
	def theAndOrNotPush(tp, clos){
		def thePersistenceOfMemory = _leCriticalList
		_leCriticalList = [tp: tp, ls: []]
		thePersistenceOfMemory.ls.add(_leCriticalList)
		clos.delegate = this
		clos()
		_leCriticalList = thePersistenceOfMemory
	}
	
	def and(clos){
		theAndOrNotPush('and', clos)
	}
	
	def or(clos){
		theAndOrNotPush('or', clos)
	}
	
	def not(clos){
		theAndOrNotPush('not', clos)
	}
	
	def list(clos){
		clos.delegate = this
		clos()
		def ls = _filteredList()
		if(_props){
			def rs = []
			def extractProps = { vls ->
				def rsItem = []
				_props.each{ prop ->
					if(prop.startsWith('sum ')){
						def isAllNull = true
						def sumResult = vls.sum(0.0){
							def anValue = it."${prop.substring(4)}"
							isAllNull = isAllNull && anValue == null
							anValue?:0.0
						}
						rsItem.add(isAllNull ? null : sumResult)
					}else if(prop.startsWith('rowCount ')){
						rsItem.add(vls.size())
					}else if(prop.startsWith('avg ')){
						rsItem.add(vls.sum(0.0){it."${prop.substring(4)}"} / vls.size())
					}else if(prop.startsWith('min ')){
						def min
						vls.each{ if(min == null || it."${prop.substring(4)}" < min) min = it."${prop.substring(4)}" }
						rsItem.add(min)
					}else if(prop.startsWith('max ')){
						def max
						vls.each{ if(max == null || it."${prop.substring(4)}" > max) max = it."${prop.substring(4)}" }
						rsItem.add(max)
					}else{
						def gp = vls.first()
						rsItem << _getProp(gp, prop)
					}
				}
				rs.add(_props.size() == 1 ? rsItem[0] : rsItem)
			}
			if(_groupProps){
				ls.groupBy{ item ->
					_groupProps.collect{ groupProp ->
						_getProp(item, groupProp)
					}
				}.each{ k, vls ->
					extractProps(vls)
				}
			}else{
				if(_hasCalcProp){
					extractProps(ls)
				}else{
					ls.each{ extractProps([it]) }
				}
			}
			ls = rs
		} else if(_distinctProp){
			def rs = []
			ls.each{
				if(!rs.contains(_getProp(it, _distinctProp))) rs.add(_getProp(it, _distinctProp))
			}
			ls = rs
		}
		return ls
	}
	
	def _runCriteria(cri, obj){
		if(cri.criteriaName.endsWith('Property')){
			_criteriaValue = obj."${cri.val}"
		}else{
			_criteriaValue = cri.val
		}
		_instanceValue = _getProp(obj, cri.prop)
		_critOptions = cri.opt
		return theImplementations[cri.criteriaName]()
	}
	
	def knokinOnHeavensDoor(criList, obj){
		def gotoParadise
		if(criList.tp == 'and'){
			gotoParadise = !criList.ls.any{ it.criteriaName ? !_runCriteria(it, obj) : !knokinOnHeavensDoor(it, obj) }
		} else if(criList.tp == 'or'){
			gotoParadise = criList.ls.any{ it.criteriaName ? _runCriteria(it, obj) : knokinOnHeavensDoor(it, obj) }
		} else if(criList.tp == 'not'){
			gotoParadise = !knokinOnHeavensDoor([tp: 'and', ls: criList.ls], obj)
		} else{
			throw new RuntimeException("Operation '${criList.tp}' not implemented.")
		}
		return gotoParadise
	}

	def __getProperty(obj, propertyName){
		def res = obj
		propertyName.split('\\.').each{ res = it == 'class' ? res.class.name : res."$it" }
		return res
	}

	def _getProp(obj, propertyName){
		def res
		if(propertyName instanceof List){
			res = propertyName.collect{ pname ->
				__getProperty(obj, pname)
			}
		} else {
			res = __getProperty(obj, propertyName)
		}
		return res
	}
	
	def _filteredList(){
		def r = []
		_clazz.list().each{ obj ->
			if(knokinOnHeavensDoor(_leCriticalList, obj)){
				r.add(obj)
			}
		}
		_orders.each{
			def arr = it.split(' ')
			def prop = arr[0]
			def order = arr[1]
			r.sort{ a, b->
				try{
					if(order == 'asc'){
						return _getProp(a, prop).compareTo(_getProp(b, prop))
					} else {
						return _getProp(b, prop).compareTo(_getProp(a, prop))
					}
				}catch(e){
					return 0
				}
			}
		}
		return r
	}
	
	def get(clos){
		def ls = list(clos)
		return ls ? ls.first() : null
	}
}
