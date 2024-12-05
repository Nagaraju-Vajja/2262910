package com.macys.uop.foundation.core.utils.future;

import static com.macys.uop.foundation.core.utils.Constant.BEAN_ID_DEFAULT_ASYNC_EXECUTOR;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * This class helps in making parallel calls during service orchestration. 
 * <br>
 * It hides CompletableFuture and Executor implementation from developers 
 * and provides simple, easy to understand APIs for parallel execution.
 *
 */
@Component
public class FutureUtils {

	@Autowired
	@Qualifier(BEAN_ID_DEFAULT_ASYNC_EXECUTOR)
	private Executor executor;
	
	/**
	 * Converts a Supplier to CompletableFuture using Executor
	 * 
	 * @param <T> Type of Supplier
	 * @param supplier Supplier
	 * @param executor Executor
	 * @return CompletableFuture of type T
	 * 
	 */
	public <T> CompletableFuture<T> toCompletableFuture(Supplier<T> supplier, Executor executor) {
		return CompletableFuture.supplyAsync(supplier, executor);
	}

	/**
	 * Converts a Supplier to CompletableFuture using default Executor {@link CompletableFuture#asyncPool}
	 * 
	 * @param <T> Type of Supplier
	 * @param supplier Supplier
	 * 
	 * @return CompletableFuture of type T
	 */
	public <T> CompletableFuture<T> toCompletableFuture(Supplier<T> supplier) {
		return CompletableFuture.supplyAsync(supplier);
	}

	/**
	 * Creates CompletableFuture from Payload
	 * 
	 * @param <T> Type of Data
	 * @param data 
	 * 
	 * @return CompletableFuture
	 */
	public <T> CompletableFuture<T> toCompletedFuture(T data) {
		return CompletableFuture.completedFuture(data);
	}

	/**
	 * Completes all CompletableFutures through join
	 * 
	 * @param completableFutures Array of CompletableFuture
	 * 
	 * @return 
	 * 		1. List<Object> in normal scenario. 
	 * 		2. In case of any completion exception, CompletableFutureCompletionRuntimeException is thrown
	 * 		3. Returns empty list in case completableFutures is null or blank 
	 */
	public List<Object> joinCompletableFutures(CompletableFuture<?>... completableFutures) {
		if (completableFutures != null && completableFutures.length > 0) {
			List<Object> results = new ArrayList<>();
			CompletableFuture.allOf(completableFutures).whenComplete((v, ex) -> {
				if (ex == null) {
					Arrays.asList(completableFutures).forEach(cf -> results.add(cf.getNow(null)));
				} else {
					throw new CompletableFutureCompletionRuntimeException(ex);
				}
			}).join();

			return results;
		}
		return Collections.emptyList();
	}

	/**
	 * Joins the list of CompletableFutures through allOf and return result
	 * 
	 * @param futures List<CompletableFuture<?>>
	 * 
	 * @return List<Object>
	 */
	public List<Object> joinCompletableFutures(List<CompletableFuture<?>> futures) {
		if (futures != null && !futures.isEmpty()) {
			return joinCompletableFutures(futures.toArray(new CompletableFuture[futures.size()]));
		}
		return Collections.emptyList();
	}

	/**
	 * Joins the array of CompletableFutures with the provided executor through allOf and return result
	 * 
	 * @param executor Executor
	 * @param suppliers Supplier<?>...
	 * 
	 * @return  List<Object>
	 */
	public List<Object> asyncExecute(Executor executor, Supplier<?>... suppliers) {
		if (suppliers != null && suppliers.length > 0) {

			return joinCompletableFutures(Arrays.asList(suppliers).stream()
					.map(supplier -> (executor != null ? toCompletableFuture(supplier, executor)
							: toCompletableFuture(supplier)))
					.collect(Collectors.toList()));
		}
		return Collections.emptyList();
	}

	/**
	 * Joins the array of suppliers and return result
	 * 
	 * @param suppliers Supplier<?>...
	 * 
	 * @return List<Object>
	 */
	public List<Object> asyncExecute(Supplier<?>... suppliers) {
		return asyncExecute(executor, suppliers);
	}

	/**
	 * Joins the list of CompletableFutures with the provided executor through allOf and return result
	 * 
	 * @param executor Executor
	 * @param suppliers List<Supplier<?>>
	 * 
	 * @return List<Object>
	 */
	public List<Object> asyncExecute(Executor executor, List<Supplier<?>> suppliers) {
		if (suppliers != null && !suppliers.isEmpty()) {
			return joinCompletableFutures(
					suppliers.stream().map(supplier -> (executor != null ? toCompletableFuture(supplier, executor)
							: toCompletableFuture(supplier))).collect(Collectors.toList()));
		}
		return Collections.emptyList();
	}

	/**
	 * Joins the list of suppliers and return result
	 * 
	 * @param suppliers List<Supplier<?>>
	 * 
	 * @return List<Object>
	 */
	public List<Object> asyncExecute(List<Supplier<?>> suppliers) {
		return asyncExecute(executor, suppliers);
	}

}
