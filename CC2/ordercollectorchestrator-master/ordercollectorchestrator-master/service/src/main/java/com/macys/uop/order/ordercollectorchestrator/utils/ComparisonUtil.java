package com.macys.uop.order.ordercollectorchestrator.utils;




import com.macys.uop.order.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
public class ComparisonUtil {


    private final Order previousDetails = new Order();
    private final Order changedDetails = new Order();

    /**
     * Response mapping
     * @param oldValues
     * @param newValues
     * @return List<Order>
     */
    public  List<Order> responseMapping(Order oldValues, Order newValues)  {
        List<Order> list = new ArrayList<>();
        if (oldValues.getProfileId() == null && newValues.getProfileId() != null) {
            previousDetails.setProfileId(oldValues.getProfileId());
            changedDetails.setProfileId(newValues.getProfileId());
        }

        if (oldValues.getProfileVersion() == null && newValues.getProfileVersion() != null) {
            previousDetails.setProfileVersion(oldValues.getProfileVersion());
            changedDetails.setProfileVersion(newValues.getProfileVersion());
        }


        list.add(previousDetails);
        list.add(changedDetails);

        return list;

    }
}
