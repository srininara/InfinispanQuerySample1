package com.nacnez.projects.infinispan.query.sample1.queryTasks;

import java.io.Serializable;
import java.util.Random;
import java.util.Set;

import org.infinispan.Cache;
import org.infinispan.distexec.DistributedCallable;

import com.nacnez.projects.grid.model.Person;

public class TimeWastingCallable implements
DistributedCallable<String, Person, Integer>, Serializable {

	private static final long serialVersionUID = 1L;

	private Cache<String, Person> cache;
	private Random random = new Random();

	@Override
	public Integer call() throws Exception {
		int count = 0;
		int loopLength = random.nextInt(50);
		System.out.println("Loop length: "+ loopLength+ " ; thread id: "+Thread.currentThread().getId());
		for (int i=0;i<loopLength;i++) {
			Thread.sleep(500);
			System.out.println("Loop Counter: "+ i + " ; thread id: "+Thread.currentThread().getId()+" ; size: " + cache.size());
		}
		return count;
	}

	@Override
	public void setEnvironment(Cache<String, Person> cache,
			Set<String> inputKeys) {
		this.cache = cache;
	}

}
