package com.nacnez.projects.infinispanQuery.sample1.perfutils;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractReporter {

	private Map<String,List<TimedTaskOutput>> mappedOutputs = new HashMap<String,List<TimedTaskOutput>>();
	protected boolean reportProgress;
	protected PrintWriter writer;

	public AbstractReporter() {
		super();
	}

	public void collect(TimedTaskOutput output) {
		List<TimedTaskOutput> outputs = mappedOutputs.get(output.getTaskName()); 
		if (outputs == null) {
			mappedOutputs.put(output.getTaskName(), new ArrayList<TimedTaskOutput>());
			outputs = mappedOutputs.get(output.getTaskName());
		}
		outputs.add(output);
		if (reportProgress) {
			printTaskOutput(output);
		}
	}

	public void report() throws Exception {
		
		for(String taskName : mappedOutputs.keySet()) {
			int count = 0;
			long sum = 0;
			TimedTaskOutput currOutput = null;
			for (TimedTaskOutput output : mappedOutputs.get(taskName)) {
				count++;
				sum += output.getExecutionTime();
				currOutput = output;
				if(!reportProgress) {
					printTaskOutput(output);
				}
			}
			if (count>1) {
				printAverage(currOutput,sum,count);
			}
		}
		
		writer.close();
	}

	private void printAverage(TimedTaskOutput output, long sum, int count) {
		long average = sum/count;
		StringBuilder msgBuilder = new StringBuilder();
		msgBuilder.append(output.getTaskName());
		msgBuilder.append(" - Average time taken is ");
		msgBuilder.append(average);
		msgBuilder.append(" milli seconds for ");
		msgBuilder.append(count);
		msgBuilder.append(" number of runs");
		writer.println(msgBuilder.toString());
		writer.flush();
	}

	private void printTaskOutput(TimedTaskOutput output) {
		StringBuilder msgBuilder = new StringBuilder();
		msgBuilder.append(output.getTaskName());
		msgBuilder.append(" completed in ");
		msgBuilder.append(output.getExecutionTime());
		msgBuilder.append(" milli seconds with result as - ");
		msgBuilder.append(output.getResultMessage());
		writer.println(msgBuilder.toString());
		writer.flush();
	}

}