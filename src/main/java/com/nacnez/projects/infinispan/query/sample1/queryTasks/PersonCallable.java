package com.nacnez.projects.infinispan.query.sample1.queryTasks;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.infinispan.Cache;
import org.infinispan.distexec.DistributedCallable;
import org.infinispan.remoting.transport.Address;

import com.nacnez.projects.grid.model.Person;
import com.nacnez.projects.infinispan.query.sample1.filter.PersonFilter;

public class PersonCallable implements
		DistributedCallable<String, Person, Collection<Person>>, Serializable {

	private static final long serialVersionUID = 1L;

	private Cache<String, Person> cache;

	private PersonFilter filter;

	public PersonCallable(PersonFilter filter) {
		this.filter = filter;
	}

	@Override
	public Collection<Person> call() throws Exception {
		Collection<Person> persons = new HashSet<Person>();
		for (String key : cache.keySet()) {
//			if (cache.getAdvancedCache().getDistributionManager().getLocality(key).isLocal()) { // Somehow this did not work
			Address expectedAddr = cache.getAdvancedCache().getDistributionManager().getPrimaryLocation(key);
			Address currAddr = cache.getAdvancedCache().getCacheManager().getAddress();
			if (expectedAddr.equals(currAddr)) {
				Person p = cache.get(key);
				if (filter.applicable(p)) {
					persons.add(p);
				}
			}
		}
		return persons;
	}

	@Override
	public void setEnvironment(Cache<String, Person> cache,
			Set<String> inputKeys) {
		this.cache = cache;
	}

}
