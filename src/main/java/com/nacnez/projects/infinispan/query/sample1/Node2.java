/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other
 * contributors as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a full listing of
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.nacnez.projects.infinispan.query.sample1;

import static com.nacnez.projects.infinispan.query.sample1.DataCreator.createData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.infinispan.Cache;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;

import com.nacnez.projects.infinispan.query.sample1.filter.PersonCityFilter;
import com.nacnez.projects.infinispan.query.sample1.filter.PersonFilter;
import com.nacnez.projects.infinispan.query.sample1.model.Person;
import com.nacnez.projects.infinispanQuery.sample1.perfutils.Reporter;
import com.nacnez.projects.infinispanQuery.sample1.perfutils.StdOutReporter;
import com.nacnez.projects.infinispanQuery.sample1.perfutils.TimedTask;

public class Node2 extends AbstractNode {

	private static final int QUERY_REPEATS = 10;

	public static void main(String[] args) throws Exception {
		new Node2().run();
	}

	public void run() throws Exception {
		Cache<String, Person> cache = getCacheManager().getCache("Person");

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cache.clear();

		// Add a listener so that we can see the puts to this node
		cache.addListener(new LoggingListener());

		// Put a few entries into the cache so that we can see them distribution
		// to the other nodes
		Collection<Person> data = createData(12000);

		int generatedDataBasedExpectedCount = 0;
		int totalCount = 0;
		List<String> personIds = new ArrayList<String>();
		PersonFilter pf = new PersonCityFilter("Bangalore");

		System.out.println("Started Loading the grid");
		
		for (Person p : data) {
			personIds.add(p.getUniqueId());
			if (pf.applicable(p)) {
				generatedDataBasedExpectedCount++;
			}
			cache.put(p.getUniqueId(), p);
			totalCount++;
			if (totalCount%500==0) {
				System.out.println("500 more added to Grid!");
			}
		}
		System.out.println("Expected Result: " + generatedDataBasedExpectedCount);

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		
		Reporter reporter = new StdOutReporter(true);

		executeQuery(reporter, QUERY_REPEATS, cache);

		reporter.report();
//		getCacheManager().getTransport().stop();
//		getCacheManager().stop();
	}

	private void executeQuery(Reporter reporter, int queryRepeats,
			final Cache<String, Person> cache) throws Exception {
		for (int i = 0; i < queryRepeats; i++) {
			TimedTask distributedTaskQuery = new TimedTask(
					"DistributedTask Query", reporter) {
				String output;
				@Override
				protected void doExecute() throws Exception {
					output = doDistributedTaskQuery(cache);
				}
				
				@Override
				protected String getResult() {
					return output;
				}
				
			};
			distributedTaskQuery.execute();
		}
		
	}

	private String doDistributedTaskQuery(Cache<String, Person> cache)
			throws InterruptedException, ExecutionException {
		DistributedExecutorService des = new DefaultExecutorService(cache);
		PersonCountCallable pcc = new PersonCountCallable(new PersonCityFilter(
				"Bangalore"));
		List<Future<Integer>> results = des.submitEverywhere(pcc);
		int personCount = 0;
		int distCount = 0;
		for (Future<Integer> f : results) {
			distCount += f.get();
			int replicationCount = cache.getCacheConfiguration().clustering()
					.hash().numOwners();
			personCount = distCount / replicationCount;
			personCount = (distCount % 2 == 0) ? personCount : personCount + 1;
		}
		return "Count: " + personCount;
	}

	@Override
	protected int getNodeId() {
		return 2;
	}


}
