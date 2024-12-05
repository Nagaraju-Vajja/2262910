package com.macys.uop.order.ordercollectorchestrator.predicate;

import com.macys.uop.foundation.core.utils.predicate.BaseRecordFailurePredicate;

public class OrdercollectorchestratorAckMchubFailurePredicate extends BaseRecordFailurePredicate {

    @Override
    protected String getCBName() {
        return "ordercollectorchestratorackmchub-cb";
    }

}
