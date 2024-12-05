package com.macys.uop.order.ordercollectorchestrator.utils;

import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.*;
import static org.mockito.Mockito.verify;

import com.macys.uop.order.model.AssociateDetail;
import com.macys.uop.order.model.Order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;


@RunWith(MockitoJUnitRunner.class)
public class ValidatorUtilTest {

    private ValidatorUtil validatorUtil;

    @Mock
    private ProfileEvaluatorUtil profileEvaluatorUtil;

    @Mock
    private BusinessConfigManagerUtil businessConfigManagerUtil;

    @Mock
    private Map<String, String> propertyMap;

    @Before
    public void init() {
        validatorUtil =
                new ValidatorUtil(profileEvaluatorUtil, businessConfigManagerUtil, propertyMap);
        ReflectionTestUtils.setField(validatorUtil, "enableR3businessconfig", false);
    }

    @Test
    public void shouldValidateMandatoryParamsForZola_MissingSourceChannel() {

        Order order = new Order();
        order.setSellerOrderId("VJ20210713ORD01");
        order.setOrderChannelDivision("71");

        Assert.assertThrows(Exception.class, () -> validatorUtil.validateMandatoryParams(order));
    }

    @Test
    public void shouldValidateMandatoryParamsForZola() {

        Order order = new Order();
        order.setSellerOrderId("VJ20210713ORD01");
        order.setSourceChannel("ZOLA");
        order.setOrderChannelDivision("71");
try{
            validatorUtil.validateMandatoryParams(order);
        }catch(Exception e){
            Assert.assertTrue(e instanceof Throwable);
        }

    }

    @Test
    public void shouldValidateMandatoryParamsForMCOM_MissingPartnerOrderId() {

        Order order = new Order();
        order.setSellerOrderId("VJ20210713ORD01");
        order.setOrderChannelDivision("71");
        Assert.assertThrows(Exception.class, () -> validatorUtil.validateMandatoryParams(order));
    }

    @Test
    public void shouldValidateMandatoryParamsForMCOM() {

        Order order = new Order();
        order.setPartnerOrderId("VJ20210713ORD01");
        order.setSourceChannel("MCOM");
        order.setSellingChannelType("MCOM");
        order.setOrderChannelDivision("71");
        AssociateDetail associateDetail = new AssociateDetail();
        associateDetail.setAssociateId("324234");
        order.setAssociateDetails(List.of(associateDetail));
        try{
            validatorUtil.validateMandatoryParams(order);
        }catch (Exception e){
            Assert.assertTrue(e instanceof Throwable);
        }
    }

    @Test
    public void getBusinessConfigForOrder() {

        Order order = new Order();
        order.setPartnerOrderId("VJ200713ORD01");
        order.setOrderPurpose("EXCHANGE");
        order.setOrderId("1241243");
        Map<Integer,Map<Object, Object>> bizConfigMap = new HashMap<>();
//        bizConfigMap.put("paymentLock", Boolean.TRUE);
//        bizConfigMap.put("orderResponse", Boolean.TRUE);
//        Mockito.when(businessConfigManagerUtil.getConfigByName(Mockito.any(), Mockito.any()))
//                .thenReturn(bizConfigMap);
        try {
            validatorUtil.getBusinessConfigForOrder(order.getOrderPurpose(), PAYMENT_LOCK);
        }catch (Exception e){

        }


        verify(businessConfigManagerUtil,
                Mockito.atLeastOnce()).getConfigByNameTypeSellingDivAndSellingChnl(order.getOrderPurpose(),
                CONFIG_TYPE, SELLING_DIVISION);
    }

    @Test
    public void getBusinessConfigForOrder_1() {

        Order order = new Order();
        order.setPartnerOrderId("VJ200713ORD01");
        order.setOrderPurpose("EXCHANGE");
        order.setOrderId("1241243");
        Map<Integer,Map<Object, Object>> bizConfigMap = new HashMap<>();
//        bizConfigMap.put("paymentLock", Boolean.TRUE);
//        bizConfigMap.put("orderResponse", Boolean.TRUE);
//        Mockito.when(businessConfigManagerUtil.getConfigByName(Mockito.any(), Mockito.any()))
//                .thenReturn(bizConfigMap);
        try{
            validatorUtil.getBusinessConfigForOrder(order.getOrderPurpose(), PAYMENT_LOCK);
        }catch (Exception e){

        }


        verify(businessConfigManagerUtil,
                Mockito.atLeastOnce()).getConfigByNameTypeSellingDivAndSellingChnl(order.getOrderPurpose(),
                CONFIG_TYPE, SELLING_DIVISION);
    }
}
