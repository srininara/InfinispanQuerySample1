package com.nacnez.projects.infinispan.query.sample1.queryTasks;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.infinispan.Cache;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;

import com.nacnez.projects.grid.model.Person;
import com.nacnez.projects.infinispan.query.sample1.filter.PersonCityFilter;
import com.nacnez.util.microbenchmarktool.TimedTask;
import com.nacnez.util.microbenchmarktool.core.PrototypeTimedTask;

public class BangalorePersonQueryTask extends PrototypeTimedTask {

	private static final long serialVersionUID = 1L;
	String output;
	Cache<String, Person> cache;
	
	
	public BangalorePersonQueryTask(Cache<String, Person> cache) {
		super("DistributedTask Query For Persons with City Bangalore");
		this.cache = cache;
	}

	@Override
	public boolean idemPotent() {
		return true;
	}
	
	@Override
	public void doTask() {
		try {
			output = doDistributedTaskQuery();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}
	
	@Override
	public String completedExecutionMessage() {
		return output;
	}
	
	@Override
	public TimedTask clone() {
		return new BangalorePersonQueryTask(cache);
	}
	
	private String doDistributedTaskQuery()
			throws InterruptedException, ExecutionException {
		DistributedExecutorService des = new DefaultExecutorService(cache);
		PersonCallable pcc = new PersonCallable(new PersonCityFilter(
				"Bangalore"));
		List<Future<Collection<Person>>> results = des.submitEverywhere(pcc);
		int personCount = 0;
		for (Future<Collection<Person>> f : results) {
			personCount += f.get().size();
		}
		return "Count: " + personCount;
	}


}
