package com.nacnez.projects.infinispanQuery.sample1.perfutils;

public interface Reporter {

	void collect(TimedTaskOutput output);
	
	void report() throws Exception;
}
