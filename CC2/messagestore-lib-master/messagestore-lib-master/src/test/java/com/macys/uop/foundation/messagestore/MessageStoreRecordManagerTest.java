package com.macys.uop.foundation.messagestore;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.gcp.data.spanner.core.SpannerTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MessageStoreRecordManagerImpl.class)
public class MessageStoreRecordManagerTest {

	@Autowired
	private MessageStoreRecordManagerImpl deleteMessageStoreRecordImpl;
	
	@MockBean
	private SpannerTemplate spannerTemplate;
	
	@Test
	public void testDeleteMessageStore() {
		
		Mockito.when(spannerTemplate.executeDmlStatement(Mockito.any())).thenReturn(1L);
		long rowsDeleted = deleteMessageStoreRecordImpl.deleteMessageStoreRecord("1", "1", "1");
		assertEquals(1, rowsDeleted);
	}

}
