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
import static com.nacnez.util.microbenchmarktool.MicroBenchmarkTool.newStatRichSimpleFileOutputReporter;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.infinispan.Cache;

import com.nacnez.projects.grid.model.Person;
import com.nacnez.projects.infinispan.query.sample1.queryTasks.BangalorePersonCountQueryTask;
import com.nacnez.projects.infinispan.query.sample1.queryTasks.BangalorePersonQueryTask;
import com.nacnez.projects.infinispan.query.sample1.queryTasks.IndiaPersonQueryTask;
import com.nacnez.projects.infinispan.query.sample1.queryTasks.PersonAverageSalQueryTask;
import com.nacnez.util.microbenchmarktool.TimedTask;

public class Node2 extends AbstractNode {

	public static final String REPORT_BASE_PATH = "/home/narayasr/MyRoot/WorkArea/PerfBenchmarks/infinispan/";

	private static final String TXT_EXTN = ".txt";

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

		for (int i = 0; i < 6; i++) {
			doMeasure(cache, fullFileName("Test-4Nodes-" + ((i + 1) * 5000)
					+ " - "));
		}

	}

	private void doMeasure(Cache<String, Person> cache, String fileName) {
		Collection<Person> data = createData(5000);

		for (Person p : data) {
			cache.put(p.getUniqueId(), p);
		}

		newSimpleExecutor().with(newStatRichSimpleFileOutputReporter(fileName))
				.execute(createBangalorePersonCountQuery(cache), 50).report();
		newSimpleExecutor().with(newStatRichSimpleFileOutputReporter(fileName))
				.execute(createBangalorePersonQuery(cache), 50).report();
		newSimpleExecutor().with(newStatRichSimpleFileOutputReporter(fileName))
				.execute(createLadyAvgSalQuery(cache), 50).report();
		newSimpleExecutor().with(newStatRichSimpleFileOutputReporter(fileName))
				.execute(createGMTPersonQuery(cache), 50).report();
	}

	private String fullFileName(String basicName) {
		return REPORT_BASE_PATH + basicName + nowInString() + TXT_EXTN;
	}

	public static String nowInString() {
		return new SimpleDateFormat("yyyyMMddhhmmssSSS").format(new Date());
	}

	private TimedTask createBangalorePersonCountQuery(
			Cache<String, Person> cache) {
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
