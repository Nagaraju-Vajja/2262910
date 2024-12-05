package com.macys.uop.foundation.core.utils.task;

/**
 * This is a way to inject external code to the command runner to determine if a
 * command should be run or not. Implement this interface and pass it as a
 * strategy to the command runner. The command runner implementation should have
 * the capability to run this strategy or it would not work
 * 
 * 
 * @author BH16963
 */
interface ShouldStepRunDecider {

	boolean decide(StepExecution stepExecution);
}
