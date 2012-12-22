package com.nacnez.projects.infinispan.query.sample1.queryTasks;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.infinispan.Cache;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;

import com.nacnez.projects.grid.model.Person;
import com.nacnez.util.microbenchmarktool.TimedTask;
import com.nacnez.util.microbenchmarktool.core.PrototypeTimedTask;

public class TimeWastingTask extends PrototypeTimedTask  {

	private static final long serialVersionUID = 1L;
	String output;
	Cache<String, Person> cache;


	public TimeWastingTask(Cache<String, Person> cache) {
		super("Time waste Query");
		this.cache = cache;
	}

	@Override
	public boolean idemPotent() {
		return false;
	}
	
	@Override
	public void doTask() {
		try {
			output = doTimeWasteTask();
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
		return new TimeWastingTask(cache);
	}
	
	private String doTimeWasteTask()
			throws InterruptedException, ExecutionException {
		DistributedExecutorService des = new DefaultExecutorService(cache);
		TimeWastingCallable pcc = new TimeWastingCallable();
		List<Future<Integer>> results = des.submitEverywhere(pcc);
		int personCount = 0;
		for (Future<Integer> f : results) {
			try {
				personCount += f.get(1, TimeUnit.SECONDS);
				System.out.println("Hmmm...");
			} catch(TimeoutException te) {
				System.out.println("Time out");
				f.cancel(true);
				System.out.println("Cancelled");
			}
		}
		return "Count: " + personCount;
	}

}
