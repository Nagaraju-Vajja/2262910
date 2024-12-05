package com.macys.uop.order.ordercollectorchestrator.service;

import com.macys.uop.order.ordercollectorchestrator.model.ErrorProcessorRequest;
import java.util.concurrent.ExecutionException;

public interface IErrorprocessorService {
	String retryLater(ErrorProcessorRequest errorRequest) throws ExecutionException, InterruptedException;
}
