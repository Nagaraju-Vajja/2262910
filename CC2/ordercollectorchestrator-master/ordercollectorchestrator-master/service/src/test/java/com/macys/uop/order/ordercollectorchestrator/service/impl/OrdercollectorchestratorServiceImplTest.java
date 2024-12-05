package com.macys.uop.order.ordercollectorchestrator.service.impl;

import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.FAILED_STATE;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.LOCK_MANAGER;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.ORDER_ENRICHMENT;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.RETRY;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.zalando.problem.Status.BAD_REQUEST;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.macys.uop.common.commonlib.Status;
import com.macys.uop.foundation.core.utils.Constant;
import com.macys.uop.foundation.core.utils.execution.ServiceContextUtil;
import com.macys.uop.foundation.core.utils.json.JsonUtils;
import com.macys.uop.foundation.core.utils.task.StepExecutionListener;
import com.macys.uop.foundation.core.utils.task.TaskExecutionListener;
import com.macys.uop.foundation.core.utils.task.support.EventStateService;
import com.macys.uop.foundation.core.utils.test.TestContextUtil;
import com.macys.uop.order.model.Lock;
import com.macys.uop.order.model.Order;
import com.macys.uop.order.model.OrderLine;
import com.macys.uop.order.model.Profile;
import com.macys.uop.order.ordercollectorchestrator.service.*;
import com.macys.uop.order.ordercollectorchestrator.utils.ComparisonUtil;
import com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorUtil;
import com.macys.uop.order.ordercollectorchestrator.utils.ProfileEvaluatorUtil;
import com.macys.uop.order.ordercollectorchestrator.utils.TestUtils;
import com.macys.uop.order.ordercollectorchestrator.utils.ValidatorUtil;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;


@RunWith(SpringRunner.class)
@SpringBootTest(classes=OrdercollectorchestratorServiceImpl.class)
public class OrdercollectorchestratorServiceImplTest implements TestContextUtil,
    ServiceContextUtil, OrdercollectorchestratorUtil {

	@Autowired
    private IOrdercollectorchestratorService orderCollectOrchestratorService;

	@MockBean
    private ValidatorUtil validatorUtil;

	@MockBean
    private ProfileEvaluatorUtil profileEvaluatorUtil;

    @MockBean
    private IOrdercollectService ordercollectService;
    
    @MockBean
    private ILockmanagerService lockmanagerService;

    @MockBean
    private IOrderenrichmentService orderenrichmentService;
    
    @MockBean
    private IEventLogService eventLogService;

    @MockBean
    private ComparisonUtil comparisonUtil;

    @MockBean
    private IErrorprocessorService errorprocessorService;

    @MockBean
    private JsonUtils jsonUtils;

    @MockBean
    private IOrderCreationSuccessPublishHelper orderCreationOnSuccessPublisher;

    @MockBean 
    private IOrderFraudacknowledgmentPublishHelper orderFraudack;

    @MockBean
    private EventStateService eventStateService;

    @MockBean
    private TaskExecutionListener taskExecutionListener;

    @MockBean
    private StepExecutionListener stepExecutionListener;
    @MockBean
    private ICollectorderResponsePublishService collectorderResponsePublishService;
    @MockBean
    private CommonOrderValidations commonOrderValidations;

    @MockBean
    private IPickUpOrderEnhanceServiceImpl pickUpOrderEnhanceServiceImpl;


    Order orderRequest;

    @Before
    public void beforeTest() {

        initContext();
        HttpHeaders headers = new HttpHeaders();
        headers.add(Constant.MESSAGEID_HDR, "1");
        headers.add(Constant.ORDERID_HDR, "2");
        headers.add(Constant.CLIENTID_HDR, "3");
        headers.add(Constant.CORRELATIONID_HDR, "4");
        headers.add(Constant.EXECUTIONID_HDR, "5");
        getServiceRequestContext().setApplicationName("ordercollectorchestrator");
        getServiceRequestContext().setHeaders(headers);

        Mockito.when(eventStateService.getTaskListener()).thenReturn(taskExecutionListener);
        Mockito.when(eventStateService.getStepListener()).thenReturn(stepExecutionListener);
        Mockito.when(validatorUtil.getProperty(Mockito.anyString())).thenReturn("ORDER_LOGEVENT,ORDER_VALIDATE,ORDER_STAMPPROFILE,ORDER_ENHANCEBOSSPLUS,ORDER_CREATE,ORDER_LOCK,ORDER_ENRICH,ORDER_PUBLISHCREATESUCCESS");

    }

    private HttpHeaders getRetryHeaders(String failedService) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(Constant.MESSAGEID_HDR, "1");
        headers.add(Constant.ORDERID_HDR, "2");
        headers.add(Constant.CLIENTID_HDR, "3");
        headers.add(Constant.CORRELATIONID_HDR, "4");
        headers.add(RETRY, "true");
        headers.add(FAILED_STATE, failedService);

        return headers;
    }

    @After
    public void afterTest() {
        clearContext();
    }

    @Test
    public void testCollectOrder() throws ExecutionException, InterruptedException {

        ReflectionTestUtils.setField(orderCollectOrchestratorService, "killSwitchR3", true);
        Order orderExpected=new Order();
        orderExpected.setOrderId("OrderId");
        orderExpected.setProfileId("ZOLA");
        orderExpected.setProfileVersion("1");

        HashMap<String, String> rules = new HashMap<String, String>();
        rules.put("order.enrichment", "true");

        Profile profile = new Profile();
        profile.setProfileId("profileId");
        profile.setProfileRules(rules);
        orderExpected.setProfiles(new ArrayList<>(Collections.singletonList(profile)));

        Order orderObj1=new Order();
        Order orderObj2=new Order();
        List<Order> listorder=new ArrayList<>();
        listorder.add(orderObj1);
        listorder.add(orderObj2);
        orderExpected.setMaxStatusCode("1000");
        orderExpected.setMinStatusCode("1000");
        orderExpected.setOrderStatus("CREATED");
        Mockito.when(comparisonUtil.responseMapping(Mockito.any(Order.class),Mockito.any(Order.class))).thenReturn(listorder);
        //Mockito.when(profileEvaluatorUtil.evaluateProfile(Mockito.anyString(), Mockito.any(), Mockito.anyString())).thenReturn(orderExpected);
        Mockito.when(ordercollectService.collectOrder(Mockito.any(Order.class), Mockito.any(HttpHeaders.class))).thenReturn(orderExpected);
        Mockito.when(orderenrichmentService.enrichOrder(Mockito.any(Order.class), Mockito.any(HttpHeaders.class))).thenReturn(
            orderExpected);
        Mockito.when(orderCreationOnSuccessPublisher.publish(Mockito.any(Order.class), Mockito.<Map>any())).thenReturn("String");
        //com.macys.uop.common.commonlib.Status response = (Status) orderCollectOrchestratorService.collectOrder(TestUtils.getOrderReqest());
        assertThrows(Exception.class, ()->orderCollectOrchestratorService.collectOrder(TestUtils.getOrderReqest()));
    }

    @Test
    public void testCollectOrderException() throws ExecutionException, InterruptedException {

        Order orderExpected=new Order();
        orderExpected.setOrderId("OrderId");
        orderExpected.setProfileId("ZOLA");
        orderExpected.setProfileVersion("1");

        HashMap<String, String> rules = new HashMap<String, String>();
        rules.put("order.enrichment", "true");

        Profile profile = new Profile();
        profile.setProfileId("profileId");
        profile.setProfileRules(rules);
        orderExpected.setProfiles(new ArrayList<>(Collections.singletonList(profile)));

        List<Order> list = List.of(new Order(), orderExpected);

        //Mockito.when(profileEvaluatorUtil.evaluateProfile(Mockito.anyString(), Mockito.any(), Mockito.anyString())).thenReturn(orderExpected);
        Mockito.when(comparisonUtil.responseMapping(Mockito.<Order>any(), Mockito.<Order>any())).thenReturn(list);
        Mockito.when(ordercollectService.collectOrder(Mockito.any(Order.class), Mockito.any(HttpHeaders.class))).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        Assert.assertThrows(Exception.class, () -> {
            com.macys.uop.common.commonlib.Status response = (Status)orderCollectOrchestratorService.collectOrder(TestUtils.getOrderReqest());
            assertNotNull(response);
            assertEquals("200", response.getResponseCode());
        });
    }

    @Test
    public void testCollectOrderException_ThrowProblem() throws ExecutionException, InterruptedException {

        final ThrowableProblem problem = Problem.builder()
            .withType(URI.create("http://localhost/abc"))
            .withTitle("Failed")
            .withStatus(BAD_REQUEST)
            .build();

        Order orderExpected=new Order();
        orderExpected.setOrderId("OrderId");
        orderExpected.setProfileId("ZOLA");
        orderExpected.setProfileVersion("1");

        HashMap<String, String> rules = new HashMap<String, String>();
        rules.put("order.enrichment", "true");

        Profile profile = new Profile();
        profile.setProfileId("profileId");
        profile.setProfileRules(rules);
        orderExpected.setProfiles(new ArrayList<>(Collections.singletonList(profile)));

        //Mockito.when(profileEvaluatorUtil.evaluateProfile(Mockito.anyString(), Mockito.any(), Mockito.anyString())).thenReturn(orderExpected);
        Mockito.when(ordercollectService.collectOrder(Mockito.any(Order.class), Mockito.any(HttpHeaders.class)))
            .thenThrow(problem);

        Assert.assertThrows(Exception.class, () -> {
            com.macys.uop.common.commonlib.Status response = (Status)orderCollectOrchestratorService.collectOrder(TestUtils.getOrderReqest());
            assertNotNull(response);
            assertEquals("400", response.getResponseCode());
        });
    }

    @Test
    public void testCollectOrderException_400() throws ExecutionException, InterruptedException {

        Order orderExpected=new Order();
        orderExpected.setOrderId("OrderId");
        orderExpected.setProfileId("ZOLA");
        orderExpected.setProfileVersion("1");

        HashMap<String, String> rules = new HashMap<String, String>();
        rules.put("order.enrichment", "true");

        Profile profile = new Profile();
        profile.setProfileId("profileId");
        profile.setProfileRules(rules);
        orderExpected.setProfiles(new ArrayList<>(Collections.singletonList(profile)));

        //Mockito.when(profileEvaluatorUtil.evaluateProfile(Mockito.anyString(), Mockito.any(), Mockito.anyString())).thenReturn(orderExpected);
        Mockito.when(ordercollectService.collectOrder(Mockito.any(Order.class), Mockito.any(HttpHeaders.class)))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Mandatory Fields Missing"));

        Assert.assertThrows(Exception.class, () -> {
            com.macys.uop.common.commonlib.Status response = (Status)orderCollectOrchestratorService.collectOrder(TestUtils.getOrderReqest());
            assertNull(response);
            assertEquals("400", response.getResponseCode());
        });
    }

    @Test
    public void testCheckResponse() throws ExecutionException, InterruptedException {

    	Order order = new Order();
    	List<OrderLine> orderLines = new ArrayList<>();
    	OrderLine orderLine = new OrderLine();
    	orderLine.setFulfillmentType("test");
    	order.setOrderId("123");
    	order.setSourceChannel("MCOM");
        order.setSellingChannelType("MCOM");
    	orderLines.add(orderLine);
    	order.setOrderLines(orderLines);
        com.macys.uop.common.commonlib.Status response = (Status)orderCollectOrchestratorService.checkResponse(order);

        assertNotNull(response);
        assertEquals("200", response.getResponseCode());
    }
    
    @Test
    public void testCheckResponse_false() throws ExecutionException, InterruptedException {

    	Order order = new Order();
    	List<OrderLine> orderLines = new ArrayList<>();
    	OrderLine orderLine = new OrderLine();
    	orderLine.setFulfillmentType("test");
    	order.setOrderId("123");
    	order.setSourceChannel("MCOM");
        order.setSellingChannelType("MCOM");
        orderLines.add(orderLine);
    	order.setOrderLines(orderLines);
    	com.macys.uop.common.commonlib.Status response = (Status)orderCollectOrchestratorService.checkResponse(order);

        assertNotNull(response);
        assertEquals("200", response.getResponseCode());
    }

    @Test
    public void testCheckResponse_Exception() throws ExecutionException, InterruptedException {

        Order order = new Order();
        List<OrderLine> orderLines = new ArrayList<>();
        OrderLine orderLine = new OrderLine();
        orderLine.setFulfillmentType("test");
        order.setOrderId("123");
        order.setSourceChannel("MCOM");
        orderLines.add(orderLine);
        order.setOrderLines(orderLines);
        Mockito.when(orderFraudack.publish(Mockito.<Order>any(), Mockito.anyMap())).thenThrow(new InterruptedException("Interrupted"));
        Assert.assertThrows(Exception.class, () -> orderCollectOrchestratorService.checkResponse(order));

    }

    @Test
    public void testCollectOrder_Lock() throws ExecutionException, InterruptedException {

        Order orderExpected=new Order();
        orderExpected.setOrderId("OrderId");
        orderExpected.setProfileId("ZOLA");
        orderExpected.setProfileVersion("1");

        HashMap<String, String> rules = new HashMap<String, String>();
        rules.put("order.enrichment", "true");

        Profile profile = new Profile();
        profile.setProfileId("profileId");
        profile.setProfileRules(rules);
        orderExpected.setProfiles(new ArrayList<>(Collections.singletonList(profile)));

        Order orderObj1=new Order();
        Order orderObj2=new Order();
        List<Order> listorder=new ArrayList<>();
        listorder.add(orderObj1);
        listorder.add(orderObj2);
        orderExpected.setMaxStatusCode("1000");
        orderExpected.setMinStatusCode("1000");
        orderExpected.setOrderStatus("CREATED");

        getServiceRequestContext().setHeaders(getRetryHeaders(LOCK_MANAGER));
        Mockito.when(comparisonUtil.responseMapping(Mockito.any(Order.class),Mockito.any(Order.class))).thenReturn(listorder);
        //Mockito.when(profileEvaluatorUtil.evaluateProfile(Mockito.anyString(), Mockito.any(), Mockito.anyString())).thenReturn(orderExpected);
        Mockito.when(ordercollectService.collectOrder(Mockito.any(Order.class), Mockito.any(HttpHeaders.class))).thenReturn(orderExpected);
        Mockito.when(orderenrichmentService.enrichOrder(Mockito.any(Order.class), Mockito.any(HttpHeaders.class))).thenReturn(
            orderExpected);
        Mockito.when(orderCreationOnSuccessPublisher.publish(Mockito.any(Order.class), Mockito.<Map>any())).thenReturn("String");
        //com.macys.uop.common.commonlib.Status response = (Status)orderCollectOrchestratorService.collectOrder(TestUtils.getOrderReqest());
        assertThrows(Exception.class, ()->orderCollectOrchestratorService.collectOrder(TestUtils.getOrderReqest()));
//        assertNotNull(response);
//        assertEquals("200", response.getResponseCode());
    }

    @Test
    public void testCollectOrder_Enrich() throws ExecutionException, InterruptedException {

        Order orderExpected=new Order();
        orderExpected.setOrderId("OrderId");
        orderExpected.setProfileId("ZOLA");
        orderExpected.setProfileVersion("1");

        HashMap<String, String> rules = new HashMap<String, String>();
        rules.put("order.enrichment", "true");

        Profile profile = new Profile();
        profile.setProfileId("profileId");
        profile.setProfileRules(rules);
        orderExpected.setProfiles(new ArrayList<>(Collections.singletonList(profile)));

        Order orderObj1=new Order();
        Order orderObj2=new Order();
        List<Order> listorder=new ArrayList<>();
        listorder.add(orderObj1);
        listorder.add(orderObj2);
        orderExpected.setMaxStatusCode("1000");
        orderExpected.setMinStatusCode("1000");
        orderExpected.setOrderStatus("CREATED");

        getServiceRequestContext().setHeaders(getRetryHeaders(ORDER_ENRICHMENT));
        Mockito.when(comparisonUtil.responseMapping(Mockito.any(Order.class),Mockito.any(Order.class))).thenReturn(listorder);
        //Mockito.when(profileEvaluatorUtil.evaluateProfile(Mockito.anyString(), Mockito.any(), Mockito.anyString())).thenReturn(orderExpected);
        Mockito.when(ordercollectService.collectOrder(Mockito.any(Order.class), Mockito.any(HttpHeaders.class))).thenReturn(orderExpected);
        Mockito.when(orderenrichmentService.enrichOrder(Mockito.any(Order.class), Mockito.any(HttpHeaders.class))).thenReturn(
            orderExpected);
        Mockito.when(orderCreationOnSuccessPublisher.publish(Mockito.any(Order.class), Mockito.<Map>any())).thenReturn("String");
        assertThrows(Exception.class, ()->orderCollectOrchestratorService.collectOrder(TestUtils.getOrderReqest()));
    }

    @Test
    public void testCollectOrderMBCheckout() throws ExecutionException, InterruptedException {

        ReflectionTestUtils.setField(orderCollectOrchestratorService, "killSwitchR3", true);
        Order orderExpected=new Order();
        orderExpected.setOrderId("OrderId");
        orderExpected.setProfileId("ZOLA");
        orderExpected.setProfileVersion("1");
        orderExpected.setSourceSystem("MCHECKOUT");
        HashMap<String, String> rules = new HashMap<String, String>();
        rules.put("order.enrichment", "true");
        List<Lock> listLock = new ArrayList<>();
        Lock lockRequest = TestUtils.getCreateLockReqest1();
        listLock.add(lockRequest);
        Profile profile = new Profile();
        profile.setProfileId("profileId");
        profile.setProfileRules(rules);
        orderExpected.setProfiles(new ArrayList<>(Collections.singletonList(profile)));
        orderExpected.setLocks(listLock);
        Order orderObj1=new Order();
        Order orderObj2=new Order();
        List<Order> listorder=new ArrayList<>();
        listorder.add(orderObj1);
        listorder.add(orderObj2);
        orderExpected.setMaxStatusCode("1000");
        orderExpected.setMinStatusCode("1000");
        orderExpected.setOrderStatus("CREATED");
        Mockito.when(comparisonUtil.responseMapping(Mockito.any(Order.class),Mockito.any(Order.class))).thenReturn(listorder);
        //Mockito.when(profileEvaluatorUtil.evaluateProfile(Mockito.anyString(), Mockito.any(), Mockito.anyString())).thenReturn(orderExpected);
        Mockito.when(ordercollectService.collectOrder(Mockito.any(Order.class), Mockito.any(HttpHeaders.class))).thenReturn(orderExpected);
        Mockito.when(orderenrichmentService.enrichOrder(Mockito.any(Order.class), Mockito.any(HttpHeaders.class))).thenReturn(
                orderExpected);
        Mockito.when(orderCreationOnSuccessPublisher.publish(Mockito.any(Order.class), Mockito.<Map>any())).thenReturn("String");
        //com.macys.uop.common.commonlib.Status response = (Status) orderCollectOrchestratorService.collectOrder(TestUtils.getOrderReqest());
        assertThrows(Exception.class, ()->orderCollectOrchestratorService.collectOrder(TestUtils.getOrderReqest()));
    }

    @Test
    public void testCollectOrderMBCheckoutForMissingFields() throws ExecutionException, InterruptedException {

        ReflectionTestUtils.setField(orderCollectOrchestratorService, "killSwitchR3", true);
        Order orderInput=TestUtils.getOrderReqestForMissingFields();
        Order orderExpected=TestUtils.getOrderReqestForMissingFieldsOutPut();
        HashMap<String, String> rules = new HashMap<String, String>();
        rules.put("order.enrichment", "true");
        List<Lock> listLock = new ArrayList<>();
        Lock lockRequest = TestUtils.getCreateLockReqest1();
        listLock.add(lockRequest);
        orderExpected.setLocks(listLock);
        Order orderObj1=new Order();
        Order orderObj2=new Order();
        List<Order> listorder=new ArrayList<>();
        listorder.add(orderObj1);
        listorder.add(orderObj2);
        Mockito.when(comparisonUtil.responseMapping(Mockito.any(Order.class),Mockito.any(Order.class))).thenReturn(listorder);
        //Mockito.when(profileEvaluatorUtil.evaluateProfile(Mockito.anyString(), Mockito.any(), Mockito.anyString())).thenReturn(orderExpected);
        Mockito.when(ordercollectService.collectOrder(Mockito.any(Order.class), Mockito.any(HttpHeaders.class))).thenReturn(orderExpected);
        Mockito.when(orderenrichmentService.enrichOrder(Mockito.any(Order.class), Mockito.any(HttpHeaders.class))).thenReturn(
                orderExpected);
        Mockito.when(orderCreationOnSuccessPublisher.publish(Mockito.any(Order.class), Mockito.<Map>any())).thenReturn("String");
        Assert.assertThrows(Exception.class, () -> {
            com.macys.uop.common.commonlib.Status response = (Status)orderCollectOrchestratorService.collectOrder(TestUtils.getOrderReqestForMissingFields());
            assertNotNull(response);
            assertEquals("200", response.getResponseCode());
        });

    }

    @Test
    public void testCollectOrderMBCheckoutForMandatoryFields_partnerOrderId() throws ExecutionException, InterruptedException {

        ReflectionTestUtils.setField(orderCollectOrchestratorService, "killSwitchR3", true);
        Order orderInput=TestUtils.getOrderReqestForMissingFields();
        orderInput.setPartnerOrderId(null);
        Order orderExpected=new Order();
        HashMap<String, String> rules = new HashMap<String, String>();
        rules.put("order.enrichment", "true");
        List<Lock> listLock = new ArrayList<>();
        Lock lockRequest = TestUtils.getCreateLockReqest1();
        listLock.add(lockRequest);
        orderExpected.setLocks(listLock);
        Order orderObj1=new Order();
        Order orderObj2=new Order();
        List<Order> listorder=new ArrayList<>();
        listorder.add(orderObj1);
        listorder.add(orderObj2);
        Mockito.when(comparisonUtil.responseMapping(Mockito.any(Order.class),Mockito.any(Order.class))).thenReturn(listorder);
        //Mockito.when(profileEvaluatorUtil.evaluateProfile(Mockito.anyString(), Mockito.any(), Mockito.anyString())).thenReturn(orderExpected);
        Mockito.when(ordercollectService.collectOrder(Mockito.any(Order.class), Mockito.any(HttpHeaders.class))).thenReturn(orderExpected);
        Mockito.when(orderenrichmentService.enrichOrder(Mockito.any(Order.class), Mockito.any(HttpHeaders.class))).thenReturn(
                orderExpected);
        Mockito.when(orderCreationOnSuccessPublisher.publish(Mockito.any(Order.class), Mockito.<Map>any())).thenReturn("String");
        Assert.assertThrows(Exception.class, () -> {
            com.macys.uop.common.commonlib.Status response = (Status)orderCollectOrchestratorService.collectOrder(orderInput);
            assertNotNull(response);
            assertEquals("400", response.getResponseCode());
        });

    }

    @Test
    public void testCollectOrderMBCheckoutForMandatoryFields_orderLines() throws ExecutionException, InterruptedException {

        ReflectionTestUtils.setField(orderCollectOrchestratorService, "killSwitchR3", true);
        Order orderInput=TestUtils.getOrderReqestForMissingFields();
        orderInput.setOrderLines(null);
        Order orderExpected=new Order();
        HashMap<String, String> rules = new HashMap<String, String>();
        rules.put("order.enrichment", "true");
        List<Lock> listLock = new ArrayList<>();
        Lock lockRequest = TestUtils.getCreateLockReqest1();
        listLock.add(lockRequest);
        orderExpected.setLocks(listLock);
        Order orderObj1=new Order();
        Order orderObj2=new Order();
        List<Order> listorder=new ArrayList<>();
        listorder.add(orderObj1);
        listorder.add(orderObj2);
        Mockito.when(comparisonUtil.responseMapping(Mockito.any(Order.class),Mockito.any(Order.class))).thenReturn(listorder);
        //Mockito.when(profileEvaluatorUtil.evaluateProfile(Mockito.anyString(), Mockito.any(), Mockito.anyString())).thenReturn(orderExpected);
        Mockito.when(ordercollectService.collectOrder(Mockito.any(Order.class), Mockito.any(HttpHeaders.class))).thenReturn(orderExpected);
        Mockito.when(orderenrichmentService.enrichOrder(Mockito.any(Order.class), Mockito.any(HttpHeaders.class))).thenReturn(
                orderExpected);
        Mockito.when(orderCreationOnSuccessPublisher.publish(Mockito.any(Order.class), Mockito.<Map>any())).thenReturn("String");
        Assert.assertThrows(Exception.class, () -> {
            com.macys.uop.common.commonlib.Status response = (Status)orderCollectOrchestratorService.collectOrder(orderInput);
            assertNotNull(response);
            assertEquals("400", response.getResponseCode());
        });

    }

    @Test
    public void testCollectOrderEnrichPickUpOrder() throws ExecutionException, InterruptedException, JsonProcessingException {

        ReflectionTestUtils.setField(orderCollectOrchestratorService, "killSwitchR3", false);
        Order orderExpected = new Order();
        orderExpected.setOrderId("OrderId");
        orderExpected.setProfileId("POOL");
        orderExpected.setProfileVersion("1");

        HashMap<String, String> rules = new HashMap<String, String>();
        rules.put("order.enrichment", "true");

        Profile profile = new Profile();
        profile.setProfileId("profileId");
        profile.setProfileRules(rules);
        orderExpected.setProfiles(new ArrayList<>(Collections.singletonList(profile)));

        Order orderObj1 = new Order();
        Order orderObj2 = new Order();
        List<Order> listorder = new ArrayList<>();
        listorder.add(orderObj1);
        listorder.add(orderObj2);
        orderExpected.setMaxStatusCode("1000");
        orderExpected.setMinStatusCode("1000");
        orderExpected.setOrderStatus("CREATED");
        Mockito.when(comparisonUtil.responseMapping(Mockito.any(Order.class), Mockito.any(Order.class))).thenReturn(listorder);
        Mockito.when(profileEvaluatorUtil.evaluateProfileV2(Mockito.anyString(), Mockito.any(), Mockito.anyString())).thenReturn(orderExpected);
        Mockito.doNothing().when(pickUpOrderEnhanceServiceImpl).enrichPickUpOrder(Mockito.any());
        Mockito.when(ordercollectService.collectOrder(Mockito.any(Order.class), Mockito.any(HttpHeaders.class))).thenReturn(orderExpected);
        Mockito.when(orderenrichmentService.enrichOrder(Mockito.any(Order.class), Mockito.any(HttpHeaders.class))).thenReturn(orderExpected);
        Mockito.when(orderCreationOnSuccessPublisher.publish(Mockito.any(Order.class), Mockito.<Map>any())).thenReturn("String");
        Order orderRequest = TestUtils.getOrderReqest();
        OrderLine orderLine = orderRequest.getOrderLines().get(0);
        orderLine.setFulfillmentType("BOSS");
        orderLine.getOrderLineStatuses().get(0).setPickupLocation("6230");
        orderRequest.setOrderLines(Collections.singletonList(orderLine));
        orderExpected.setOrderLines(Collections.singletonList(orderLine));
        orderCollectOrchestratorService.collectOrder(orderRequest);
        Mockito.verify(pickUpOrderEnhanceServiceImpl, Mockito.times(1)).enrichPickUpOrder(Mockito.any(Order.class));
    }
}
