package com.nacnez.projects.infinispanQuery.sample1.perfutils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

public class FileReporter extends AbstractReporter implements Reporter {

	private String fileName;
	public FileReporter(String fileName, boolean reportProgress) throws Exception {
		this.fileName = fileName;
		this.reportProgress = reportProgress;
		this.writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
	}


}
