package com.nacnez.projects.infinispan.query.sample1.filter;

import com.nacnez.projects.grid.model.Person;

public class IndiaClosePersonFilter implements PersonFilter {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean applicable(Person p) {
		Double longitude = p.getAddress().getCurrentLocation().getLongitude();
		Double lowVal = new Double(60);
		Double hiVal = new Double(100);
		return longitude.compareTo(lowVal)>=0 && longitude.compareTo(hiVal)<=0;
	}


}
