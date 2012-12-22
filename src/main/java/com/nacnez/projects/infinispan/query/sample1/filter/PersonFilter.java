package com.nacnez.projects.infinispan.query.sample1.filter;

import java.io.Serializable;

import com.nacnez.projects.grid.model.Person;

public interface PersonFilter extends Serializable {
	
	boolean applicable(Person p);

}
