package com.nacnez.projects.infinispan.query.sample1;

import java.io.Serializable;
import java.util.Set;

import org.infinispan.Cache;
import org.infinispan.distexec.DistributedCallable;
import org.infinispan.remoting.transport.Address;

import com.nacnez.projects.infinispan.query.sample1.filter.PersonFilter;
import com.nacnez.projects.infinispan.query.sample1.model.Person;

public class PersonCountCallable implements
		DistributedCallable<String, Person, Integer>, Serializable {

	private static final long serialVersionUID = 1L;

	private Cache<String, Person> cache;

	private PersonFilter filter;

	public PersonCountCallable(PersonFilter filter) {
		this.filter = filter;
	}

	@Override
	public Integer call() throws Exception {
		int count = 0;
		for (String key : cache.keySet()) {
//			if (cache.getAdvancedCache().getDistributionManager().getLocality(key).isLocal()) { // Somehow this did not work
			Address expectedAddr = cache.getAdvancedCache().getDistributionManager().getPrimaryLocation(key);
			Address currAddr = cache.getAdvancedCache().getCacheManager().getAddress();
			if (expectedAddr.equals(currAddr)) {
				Person p = cache.get(key);
				if (filter.applicable(p)) {
					count++;
				}
			}
		}
		return count;
	}

	@Override
	public void setEnvironment(Cache<String, Person> cache,
			Set<String> inputKeys) {
		this.cache = cache;
	}

}
