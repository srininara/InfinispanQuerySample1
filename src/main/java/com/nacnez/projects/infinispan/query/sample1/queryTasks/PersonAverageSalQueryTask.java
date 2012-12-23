package com.nacnez.projects.infinispan.query.sample1.queryTasks;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.infinispan.Cache;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;

import com.nacnez.projects.grid.model.Person;
import com.nacnez.projects.infinispan.query.sample1.filter.LadyFiveDigitSalaryGettersFilter;
import com.nacnez.util.microbenchmarktool.TimedTask;
import com.nacnez.util.microbenchmarktool.core.PrototypeTimedTask;

public class PersonAverageSalQueryTask extends PrototypeTimedTask {

	private static final long serialVersionUID = 1L;
	String output;
	Cache<String, Person> cache;
	
	
	public PersonAverageSalQueryTask(Cache<String, Person> cache) {
		super("DistributedTask Query Lady Average Salary");
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
		return new PersonAverageSalQueryTask(cache);
	}
	
	private String doDistributedTaskQuery()
			throws InterruptedException, ExecutionException {
		DistributedExecutorService des = new DefaultExecutorService(cache);
		PersonSalaryCallable pcc = new PersonSalaryCallable(new LadyFiveDigitSalaryGettersFilter());
		List<Future<Double>> results = des.submitEverywhere(pcc);
		int returnCount = 0;
		Double avgSal = new Double(0);
		for (Future<Double> f : results) {
			returnCount ++;
			avgSal= avgSal+ f.get();
		}
		return "Count: " + (avgSal/returnCount);
	}


}
