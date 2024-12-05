package com.macys.uop.order.ordercollectorchestrator.service;

import com.macys.uop.common.omconfig.api.ErrorDetails;
import com.macys.uop.foundation.core.utils.CommonStatusCode;
import com.macys.uop.order.model.*;
import com.macys.uop.order.ordercollectorchestrator.model.OrderError;
import com.macys.uop.order.ordercollectorchestrator.utils.OrderErrorCodes;
import com.macys.uop.order.ordercollectorchestrator.utils.OrderValidationErrorCodes;
import com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.macys.uop.foundation.core.utils.Constant.ORDERID_HDR;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrderErrorCodes.VALIDATE_ORDER_ERROR;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrderTransaction.ORDER_ACKNOWLEDGE;
import static com.macys.uop.order.ordercollectorchestrator.utils.OrdercollectorchestratorConstants.*;

@Slf4j
@RequiredArgsConstructor
public abstract class OrderValidations implements OrdercollectorchestratorUtil{

    private final ICollectorderResponsePublishService collectorderResponsePublishService;
    private final IEventLogService eventLogService;
    protected abstract void validate(Order order) throws ExecutionException, InterruptedException;

    public void publishAndThrowError(OrderValidationErrorCodes orderValidationErrorCodes, Order order) throws ExecutionException, InterruptedException {
        OrderError orderError = new OrderError();
        Object reservationId = ObjectUtils.isNotEmpty(order.getOrderLines())? order.getOrderLines().stream().
                filter(o1-> ObjectUtils.isNotEmpty(o1.getReservationId())).map(OrderLine::getReservationId).findFirst().orElse(null)
                :null;

        String refId = StringUtils.isNotEmpty(order.getOrderId())
                ? order.getOrderId()
                : StringUtils.isNotEmpty(order.getPartnerOrderId()) ? order.getPartnerOrderId()
                : ObjectUtils.isNotEmpty(reservationId)? String.valueOf(reservationId) : ORDER_NA_REF_ID;
        String refType = StringUtils.isNotEmpty(order.getOrderId())? ORDERID_HDR:StringUtils.isNotEmpty(order.getPartnerOrderId()) ? PARTNER_ORDER_ID
                : ObjectUtils.isNotEmpty(reservationId)? RESERVATION_ID: ORDERID_HDR;
        List<ErrorDetails> detailsList = new ArrayList<ErrorDetails>();
        ErrorDetails details = new ErrorDetails();
        String additionalInfo = "Mandatory Fields Validation Failure - " + orderValidationErrorCodes.getDescription();
        details.setErrorDescription(additionalInfo);
        details.setErrorCode(Integer.valueOf(OrderErrorCodes.MANDATORY_FIELDS_VALIDATION.getCode()));
        getErrorLogMessageBuilder(CommonStatusCode.BAD_REQUEST_BODY.getCode(), CommonStatusCode.BAD_REQUEST_BODY.getDescription(), additionalInfo, log).build().logAsError();
        detailsList.add(details);
        orderError.setApplicationName(SERVICENAME);
        com.macys.uop.order.ordercollectorchestrator.model.Error err = new com.macys.uop.order.ordercollectorchestrator.model.Error();
        err.setErrorDetailsList(detailsList);
        orderError.setError(err);
        if (ObjectUtils.isNotEmpty(orderError) && (ObjectUtils.isEmpty(orderError.getOrder()))) {
            collectorderResponsePublishService.publish(order, orderError);
            eventLogService.createEventLog(order,orderError, OUTBOUND,
                    ORDER_ACKNOWLEDGE, refType, refId, getDefaultHttpHeaders().toSingleValueMap());
            throwBusinessError(VALIDATE_ORDER_ERROR.getStepName(), orderValidationErrorCodes.getDescription(), getDefaultHttpHeaders(),
                    OrderValidationErrorCodes.getNameByValue(orderValidationErrorCodes.getCode()),refId,refType);
        }
    }
    public void validateShippingInfo(PersonInfo shipInfo, Order order) throws ExecutionException, InterruptedException {
        if (ObjectUtils.isNotEmpty(shipInfo)) {
            if(StringUtils.isEmpty(shipInfo.getAddressLine1())) {
                publishAndThrowError(OrderValidationErrorCodes.SHIIPINGINFOS_ADDRESSLINE1_MISSING, order);
            }
            if(StringUtils.isEmpty(shipInfo.getCity())){
                publishAndThrowError(OrderValidationErrorCodes.SHIIPINGINFOS_CITY_MISSING, order);
            }
            if(StringUtils.isEmpty(shipInfo.getCountry())){
                publishAndThrowError(OrderValidationErrorCodes.SHIIPINGINFOS_COUNTRY_MISSING, order);
            }
            if(StringUtils.isEmpty(shipInfo.getFirstName())){
                publishAndThrowError(OrderValidationErrorCodes.SHIIPINGINFOS_FIRSTNAME_MISSING, order);
            }
            if(StringUtils.isEmpty(shipInfo.getLastName())){
                publishAndThrowError(OrderValidationErrorCodes.SHIIPINGINFOS_LASTNAME_MISSING, order);
            }
            if(StringUtils.isEmpty(shipInfo.getZipCode())){
                publishAndThrowError(OrderValidationErrorCodes.SHIIPINGINFOS_ZIPCODE_MISSING, order);
            }
            if(StringUtils.isEmpty(shipInfo.getState())){
                publishAndThrowError(OrderValidationErrorCodes.SHIIPINGINFOS_STATE_MISSING, order);
            }
        }
    }

}
