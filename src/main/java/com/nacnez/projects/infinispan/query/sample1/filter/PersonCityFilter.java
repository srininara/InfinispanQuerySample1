package com.nacnez.projects.infinispan.query.sample1.filter;

import java.io.Serializable;

import com.nacnez.projects.grid.model.Person;

public class PersonCityFilter implements PersonFilter, Serializable {
	
	private static final long serialVersionUID = 1L;

	private String city;
	
	public PersonCityFilter(String city) {
		super();
		this.city = city;
	}



	@Override
	public boolean applicable(Person p) {
		return p.getCity().equals(city);
	}

}
