package com.macys.uop.foundation.core.utils.future;

import static com.macys.uop.foundation.core.utils.Constant.BEAN_ID_DEFAULT_ASYNC_EXECUTOR;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.macys.uop.foundation.core.utils.test.TestContextUtil;
import com.macys.uop.foundation.core.utils.trace.ExecutorServiceConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { FutureUtils.class, ExecutorServiceConfiguration.class })
public class FutureUtilsTest implements TestContextUtil {

	@Autowired
	@Qualifier(BEAN_ID_DEFAULT_ASYNC_EXECUTOR)
	private Executor executor;

	@Autowired
	private FutureUtils futureUtils;

	@Before
	public void beforeTest() {
		initContext();
	}

	@After
	public void afterTest() {
		clearContext();
	}

	@Test
	public void testToCompletableFuture() throws InterruptedException, ExecutionException {
		CompletableFuture<String> cFuture = futureUtils.toCompletableFuture(() -> "", executor);
		cFuture.complete("hello");
		Assert.assertEquals("hello", cFuture.get());
	}

	@Test
	public void testToCompletableFutureWithOnlySupplier() throws InterruptedException, ExecutionException {
		CompletableFuture<String> cFuture = futureUtils.toCompletableFuture(() -> "");
		cFuture.complete("hello");
		Assert.assertEquals("hello", cFuture.get());
	}

	@Test
	public void testToCompletedFuture() throws InterruptedException, ExecutionException {
		CompletableFuture<String> cFuture = futureUtils.toCompletedFuture("hello");
		Assert.assertEquals("hello", cFuture.get());
	}

	@Test
	public void testJoinCompletableFutures() {
		CompletableFuture<String> cfOne = futureUtils.toCompletedFuture("one");
		CompletableFuture<String> cfTwo = futureUtils.toCompletedFuture("two");
		List<Object> resultList = futureUtils.joinCompletableFutures(cfOne, cfTwo);

		Assert.assertEquals(2, resultList.size());
	}

	@Test
	public void testJoinCompletableFuturesEmpty() {
		CompletableFuture<?>[] array = null;
		List<Object> resultList = futureUtils.joinCompletableFutures(array);

		Assert.assertEquals(0, resultList.size());
	}

	@Test
	public void testJoinCompletableFuturesException() {
		CompletableFuture<String> cFuture = futureUtils.toCompletableFuture(() -> "test");
		cFuture.completeExceptionally(new RuntimeException("Custom Exception"));
		try {
			futureUtils.joinCompletableFutures(cFuture);
		} catch (Exception e) {
			Assert.assertEquals("java.lang.RuntimeException: Custom Exception", e.getMessage());
		}
	}

	@Test
	public void testJoinCompletableFuturesList() {
		CompletableFuture<String> cfOne = futureUtils.toCompletedFuture("one");
		CompletableFuture<String> cfTwo = futureUtils.toCompletedFuture("two");
		List<CompletableFuture<?>> futures = new ArrayList<>();
		futures.add(cfOne);
		futures.add(cfTwo);

		List<Object> resultList = futureUtils.joinCompletableFutures(futures);

		Assert.assertEquals(2, resultList.size());

	}

	@Test
	public void testJoinCompletableFuturesListEmpty() {
		List<CompletableFuture<?>> futures = new ArrayList<>();
		List<Object> resultList = futureUtils.joinCompletableFutures(futures);

		Assert.assertEquals(0, resultList.size());

	}

	@Test
	public void testAsyncExecute() {
		Map<String, String> contextMap = new HashMap<>();
		contextMap.put("key", "value");
		MDC.setContextMap(contextMap);

		Supplier<LocalDateTime> s1 = () -> LocalDateTime.now();
		Supplier<LocalDateTime> s2 = () -> LocalDateTime.now();
		List<Object> resultList = futureUtils.asyncExecute(executor, s1, s2);

		Assert.assertEquals(2, resultList.size());
	}

	@Test
	public void testAsyncExecuteEmptySuppliers() {
		Supplier<?>[] array = null;
		List<Object> resultList = futureUtils.asyncExecute(executor, array);

		Assert.assertEquals(0, resultList.size());
	}
	
	@Test
	public void testAsyncExecuteNullExecutor() {
		Map<String, String> contextMap = new HashMap<>();
		contextMap.put("key", "value");
		MDC.setContextMap(contextMap);

		Supplier<LocalDateTime> s1 = () -> LocalDateTime.now();
		Supplier<LocalDateTime> s2 = () -> LocalDateTime.now();
		
		Executor executor1=null;
		
		List<Object> resultList = futureUtils.asyncExecute(executor1, s1, s2);

		Assert.assertEquals(2, resultList.size());
	}

	@Test
	public void testAsyncExecuteSuppliersArray() {
		Map<String, String> contextMap = new HashMap<>();
		contextMap.put("key", "value");
		MDC.setContextMap(contextMap);

		Supplier<LocalDateTime> s1 = () -> LocalDateTime.now();
		Supplier<LocalDateTime> s2 = () -> LocalDateTime.now();
		List<Object> resultList = futureUtils.asyncExecute(s1, s2);

		Assert.assertEquals(2, resultList.size());
	}

	@Test
	public void testAsyncExecuteWithSuppliersAsListAndExecutor() {
		Map<String, String> contextMap = new HashMap<>();
		contextMap.put("key", "value");
		MDC.setContextMap(contextMap);

		Supplier<LocalDateTime> s1 = () -> LocalDateTime.now();
		Supplier<LocalDateTime> s2 = () -> LocalDateTime.now();

		List<Supplier<?>> list = new ArrayList<>();
		list.add(s1);
		list.add(s2);

		List<Object> resultList = futureUtils.asyncExecute(executor, list);

		Assert.assertEquals(2, resultList.size());
	}
	
	@Test
	public void testAsyncExecuteWithSuppliersAndNullExecutor() {
		Map<String, String> contextMap = new HashMap<>();
		contextMap.put("key", "value");
		MDC.setContextMap(contextMap);

		Supplier<LocalDateTime> s1 = () -> LocalDateTime.now();
		Supplier<LocalDateTime> s2 = () -> LocalDateTime.now();

		List<Supplier<?>> list = new ArrayList<>();
		list.add(s1);
		list.add(s2);

		List<Object> resultList = futureUtils.asyncExecute(null, list);

		Assert.assertEquals(2, resultList.size());
	}

	@Test
	public void testAsyncExecuteWithSuppliersEmptyAndExecutor() {
		List<Supplier<?>> list = new ArrayList<>();
		List<Object> resultList = futureUtils.asyncExecute(executor, list);

		Assert.assertEquals(0, resultList.size());
	}

	@Test
	public void testAsyncExecuteWithSuppliersAsList() {
		Map<String, String> contextMap = new HashMap<>();
		contextMap.put("key", "value");
		MDC.setContextMap(contextMap);

		Supplier<LocalDateTime> s1 = () -> LocalDateTime.now();
		Supplier<LocalDateTime> s2 = () -> LocalDateTime.now();
		List<Supplier<?>> list = new ArrayList<>();
		list.add(s1);
		list.add(s2);

		List<Object> resultList = futureUtils.asyncExecute(list);

		Assert.assertEquals(2, resultList.size());
	}

}
