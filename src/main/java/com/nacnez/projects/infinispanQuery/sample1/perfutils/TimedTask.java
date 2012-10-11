package com.nacnez.projects.infinispanQuery.sample1.perfutils;

public abstract class TimedTask {

	private String taskName;
	
	private Reporter reporter;
	
	private long finishedTimeInMilliSecs;

	public TimedTask(String taskName,Reporter reporter) {
		this.taskName = taskName;
		this.reporter = reporter;
	}
	
	public void execute() throws Exception {
        long fillStartTime = System.currentTimeMillis();
        doExecute();
		finishedTimeInMilliSecs = (System.currentTimeMillis()-fillStartTime);
		pushDataToReporter(getResult());
	}
	
	private void pushDataToReporter(String resultMessage) {
		TimedTaskOutput output = new TimedTaskOutput(taskName,finishedTimeInMilliSecs,resultMessage);
		reporter.collect(output);
	}
	
	protected String getResult() {
		return null; // Default implementation does nothing.
	}

	protected abstract void doExecute() throws Exception;
	
	public void setData(Object data) {
		
	}
	
	public Object getData() {
		return null;
	}

}
