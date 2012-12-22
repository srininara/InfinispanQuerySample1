package com.nacnez.projects.infinispan.query.sample1.filter;

import com.nacnez.projects.grid.model.Person;

public class LadyFiveDigitSalaryGettersFilter implements PersonFilter {

	private static final long serialVersionUID = 1L;
	private static final String FEMALE = "Female";

	@Override
	public boolean applicable(Person p) {
		return (FEMALE.equals(p.getGender()) && p.getIncome().compareTo(new Double(10000))>=0);
	}

}
