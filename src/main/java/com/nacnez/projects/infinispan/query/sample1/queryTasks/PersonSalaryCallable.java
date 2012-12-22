package com.nacnez.projects.infinispan.query.sample1.queryTasks;

import java.io.Serializable;
import java.util.Set;

import org.infinispan.Cache;
import org.infinispan.distexec.DistributedCallable;
import org.infinispan.remoting.transport.Address;

import com.nacnez.projects.grid.model.Person;
import com.nacnez.projects.infinispan.query.sample1.filter.PersonFilter;

public class PersonSalaryCallable implements
		DistributedCallable<String, Person, Double>, Serializable {

	private static final long serialVersionUID = 1L;

	private Cache<String, Person> cache;

	private PersonFilter filter;

	public PersonSalaryCallable(PersonFilter filter) {
		this.filter = filter;
	}

	@Override
	public Double call() throws Exception {
		Double sal = new Double(0.0);
		int count =0;
		for (String key : cache.keySet()) {
//			if (cache.getAdvancedCache().getDistributionManager().getLocality(key).isLocal()) { // Somehow this did not work
			Address expectedAddr = cache.getAdvancedCache().getDistributionManager().getPrimaryLocation(key);
			Address currAddr = cache.getAdvancedCache().getCacheManager().getAddress();
			if (expectedAddr.equals(currAddr)) {
				Person p = cache.get(key);
				if (filter.applicable(p)) {
					sal = sal + p.getIncome();
					count++;
				}
			}
		}
		return sal/count;
	}

	@Override
	public void setEnvironment(Cache<String, Person> cache,
			Set<String> inputKeys) {
		this.cache = cache;
	}

}
