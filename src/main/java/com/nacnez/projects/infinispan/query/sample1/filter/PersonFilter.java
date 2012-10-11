package com.nacnez.projects.infinispan.query.sample1.filter;

import com.nacnez.projects.infinispan.query.sample1.model.Person;

public interface PersonFilter {
	
	boolean applicable(Person p);

}
