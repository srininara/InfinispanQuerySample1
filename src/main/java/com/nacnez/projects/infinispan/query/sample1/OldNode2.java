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

import static com.nacnez.projects.grid.modelCreator.DataCreator.createData;
import static com.nacnez.util.microbenchmarktool.MicroBenchmarkTool.newSimpleExecutor;
import static com.nacnez.util.microbenchmarktool.MicroBenchmarkTool.newStandardOutputReporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.infinispan.Cache;

import com.nacnez.projects.grid.model.Person;
import com.nacnez.projects.infinispan.query.sample1.filter.IndiaClosePersonFilter;
import com.nacnez.projects.infinispan.query.sample1.filter.LadyFiveDigitSalaryGettersFilter;
import com.nacnez.projects.infinispan.query.sample1.filter.PersonCityFilter;
import com.nacnez.projects.infinispan.query.sample1.filter.PersonFilter;
import com.nacnez.projects.infinispan.query.sample1.queryTasks.BangalorePersonCountQueryTask;
import com.nacnez.projects.infinispan.query.sample1.queryTasks.BangalorePersonQueryTask;
import com.nacnez.projects.infinispan.query.sample1.queryTasks.IndiaPersonQueryTask;
import com.nacnez.projects.infinispan.query.sample1.queryTasks.PersonAverageSalQueryTask;
import com.nacnez.projects.infinispan.query.sample1.queryTasks.TimeWastingTask;
import com.nacnez.util.microbenchmarktool.TimedTask;

public class OldNode2 extends AbstractNode {


	public static void main(String[] args) throws Exception {
		new OldNode2().run();
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
		Collection<Person> data = createData(1000);

		int generatedDataBasedExpectedCountCity = 0;
		int generatedDataBasedExpectedCountLadyAvg = 0;
		Double generatedDataBasedExpectedSalLadyAvg = new Double(0.0);
		int generatedDataBasedExpectedCountGMT = 0;
		int totalCount = 0;
		List<String> personIds = new ArrayList<String>();
		PersonFilter pf = new PersonCityFilter("Bangalore");
		PersonFilter pfl = new LadyFiveDigitSalaryGettersFilter();
		PersonFilter pfg = new IndiaClosePersonFilter();
		

		System.out.println("Started Loading the grid");
		
		for (Person p : data) {
			personIds.add(p.getUniqueId());
			if (pf.applicable(p)) {
				generatedDataBasedExpectedCountCity++;
			}
			if (pfl.applicable(p)) {
				generatedDataBasedExpectedSalLadyAvg = generatedDataBasedExpectedSalLadyAvg +p.getIncome();
				generatedDataBasedExpectedCountLadyAvg++;
			}
			if (pfg.applicable(p)) {
				generatedDataBasedExpectedCountGMT++;
			}
			cache.put(p.getUniqueId(), p);
			totalCount++;
			if (totalCount%1000==0) {
				System.out.println("1000 more added to Grid!");
			}
		}

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

//		newSimpleExecutor().with(newStatRichSimpleStandardOutputReporter()).execute(createBangalorePersonCountQuery(cache), 50).report();
//		newSimpleExecutor().with(newStatRichSimpleStandardOutputReporter()).execute(createBangalorePersonQuery(cache), 50).report();
//		newSimpleExecutor().with(newStatRichSimpleStandardOutputReporter()).execute(createGMTPersonQuery(cache), 50).report();
//		newSimpleExecutor().with(newStatRichSimpleStandardOutputReporter()).execute(createLadyAvgSalQuery(cache), 50).report();

//		System.out.println("Expected Result Bangalore : " + generatedDataBasedExpectedCountCity);
//		newSimpleExecutor().with(newStatRichStandardOutputReporter()).execute(createBangalorePersonCountQuery(cache), 50).report();
//		System.out.println("Expected Result  Bangalore : " + generatedDataBasedExpectedCountCity);
//		newSimpleExecutor().with(newStatRichStandardOutputReporter()).execute(createBangalorePersonQuery(cache), 50).report();
//		System.out.println("Expected Result GMT: " + generatedDataBasedExpectedCountGMT);
//		newSimpleExecutor().with(newStatRichStandardOutputReporter()).execute(createGMTPersonQuery(cache), 50).report();
//		System.out.println("Expected Result Lady Avg: " + generatedDataBasedExpectedSalLadyAvg/generatedDataBasedExpectedCountLadyAvg);
//		System.out.println("Expected Result Lady Sal Sum: " + generatedDataBasedExpectedSalLadyAvg);
//		System.out.println("Expected Result Lady Count: " + generatedDataBasedExpectedCountLadyAvg);
//
//		newSimpleExecutor().with(newStatRichStandardOutputReporter()).execute(createLadyAvgSalQuery(cache), 50).report();
		
		newSimpleExecutor().with(newStandardOutputReporter()).execute(createTimeWasteTask(cache), 1).report();

	}

	private TimedTask createTimeWasteTask(Cache<String, Person> cache) {
		return new TimeWastingTask(cache);		
	}

	private TimedTask createBangalorePersonCountQuery(Cache<String, Person> cache) {
		return new BangalorePersonCountQueryTask(cache);		
	}

	private TimedTask createBangalorePersonQuery(Cache<String, Person> cache) {
		return new BangalorePersonQueryTask(cache);		
	}

	private TimedTask createGMTPersonQuery(Cache<String, Person> cache) {
		return new IndiaPersonQueryTask(cache);		
	}

	private TimedTask createLadyAvgSalQuery(Cache<String, Person> cache) {
		return new PersonAverageSalQueryTask(cache);		
	}

	
	@Override
	protected int getNodeId() {
		return 2;
	}


}
