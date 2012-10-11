package com.nacnez.projects.infinispanQuery.sample1.perfutils;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class StdOutReporter extends AbstractReporter implements Reporter {


	public StdOutReporter(boolean reportProgress)
			throws Exception {
		this.reportProgress = reportProgress;
		this.writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
	}

}
