package com.nacnez.projects.infinispanQuery.sample1.perfutils;

public class TimedTaskOutput {
	private String taskName;
	private long executionTime;
	private String resultMessage;
	
	public TimedTaskOutput() {
		
	}
	
	public TimedTaskOutput(String taskName, long executionTime, String resultMessage) {
		super();
		this.taskName = taskName;
		this.executionTime = executionTime;
		this.setResultMessage(resultMessage);
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public long getExecutionTime() {
		return executionTime;
	}
	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}
}
