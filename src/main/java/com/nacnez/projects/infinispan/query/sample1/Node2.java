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
		// waitForClusterToForm();

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cache.clear();

		// cache.getAdvancedCache().

		
		
		// Add a listener so that we can see the puts to this node
		cache.addListener(new LoggingListener());

		// Put a few entries into the cache so that we can see them distribution
		// to the other nodes
		Collection<Person> data = createData(50);

		int generatedDataBasedExpectedCount = 0;
		List<String> personIds = new ArrayList<String>();
		PersonFilter pf = new PersonCityFilter("Bangalore");

		// Directory directory = new InfinispanDirectory(cache);

		for (Person p : data) {
			personIds.add(p.getUniqueId());
			if (pf.applicable(p)) {
				generatedDataBasedExpectedCount++;
			}
			cache.put(p.getUniqueId(), p);
			// directory.
		}

		// System.out.println("Total cache size: " + cache.size());

		// SearchManager searchManager = org.infinispan.query.Search
		// .getSearchManager(cache);
		// QueryBuilder qb =
		// searchManager.getSearchFactory().buildQueryBuilder()
		// .forEntity(Person.class).get();
		// Query query = qb.keyword().onFields("city").matching("Bangalore")
		// .createQuery();
		// CacheQuery cacheQuery = searchManager.getQuery(query);
		// int qCount = cacheQuery.getResultSize();
		// List<Object> found = cacheQuery.list();
		Reporter reporter = new StdOutReporter(true);

//		doDistributedTaskQuery(cache);
		executeQuery(reporter,QUERY_REPEATS,cache);
		System.out.println("Expected Result: "
				+ generatedDataBasedExpectedCount);

		// cache.clear();
	}

	private void doDistributedTaskQuery(Cache<String, Person> cache)
			throws InterruptedException, ExecutionException {
		DistributedExecutorService des = new DefaultExecutorService(cache);
		PersonCountCallable pcc = new PersonCountCallable(new PersonCityFilter(
				"Bangalore"));
		// List<Future<Integer>> results =
		// des.submitEverywhere(pcc,personIds.toArray());
		long queryStartTime = System.currentTimeMillis();
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

		long finishedTimeInMilliSecs = (System.currentTimeMillis() - queryStartTime);

		System.out.println("Distributed task count output: " + personCount
				+ " completed in " + finishedTimeInMilliSecs + " millisecs.");
	}

	@Override
	protected int getNodeId() {
		return 2;
	}

	private void executeQuery(Reporter reporter, int queryRepeats, final Cache<String, Person> cache )
			throws Exception {
		for (int i = 0; i < queryRepeats; i++) {
			TimedTask distributedTaskQuery = new TimedTask(
					"DistributedTask Query", reporter) {
				@Override
				protected void doExecute() throws Exception {
					doDistributedTaskQuery(cache);
				}
			};
			distributedTaskQuery.execute();
		}
	}

}
