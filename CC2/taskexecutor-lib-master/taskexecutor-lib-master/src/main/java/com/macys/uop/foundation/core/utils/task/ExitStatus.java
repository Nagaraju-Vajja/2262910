package com.macys.uop.foundation.core.utils.task;

public class ExitStatus {
	private final String exitCode;

	private final String exitDescription;
	
	public ExitStatus(String exitCode) {
		this(exitCode, "");
	}
	
	public ExitStatus(String exitCode, String exitDescription) {
		super();
		this.exitCode = exitCode;
		this.exitDescription = exitDescription == null ? "" : exitDescription;
	}
	public String getExitCode() {
		return exitCode;
	}
	public String getExitDescription() {
		return exitDescription;
	}
}
