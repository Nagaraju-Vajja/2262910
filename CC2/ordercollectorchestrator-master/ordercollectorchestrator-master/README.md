***Service Name:*** Order Collect orchestrator

**Service Description:** Order Collect Orchestrator is the entry point for order collect service. 
 This Service is used to orchestrate order collect and enrich order data and publish to order sourcing.

***Endpoints:****

1: **POST:** /v1/orders
2: **POST:** /v1/lock/orders

Internal calls:
* POST Request to Ordercollect service and get Order Object as response.
* POST Request to orderenrichment service and get Order Object as response.
* Publish to Sourcing – Publish the enriched order to order sourcing topic.


**Consumer Service:** 
This is starting point for order creation flow. It receives messages from pubsub which has been published from the McHub.

**Produce:** 
 Post message to PubSub -Topic - ordercreation_onsuccess_dev

**Technical Details:**
1. Project Type - Composite Service
2. Subscriber 
        pull__collectorder_request_dev__collectorder_create
3. Other UOP Calls
       POST: Order Collect Uri = https://oms-dev.devops.fds.com/ordercollect/v1/orders/collect   
       POST: Lock Manager Uri = https://oms-dev.devops.fds.com/lockmanager/v1/orders/locks
       POST: Order Enrich Uri=https://oms-dev.devops.fds.com/orderenrichment/v1/orders/enrich
       GET: Lock Manager Uri = https://oms-dev.devops.fds.com/lockmanager/v1/orders/locks

4. Publish Topics     ordercreation_onsuccess_{env}
5. Code Flow
1. Receives PubSub message from subscription.
2. Duplicate message check done by MessageStore Need Retry
3. If it’s not duplicate message then call profileevaluator. (Library) 
4. Get Profiled order by passing order to profileevaluator. Need Retry
5. Call Order collect service by passing the profiled order response. - Order Collect – 
EventLog, Calculate Proration, Persist address, generate orderid from orderid-lib, persist/create order.
6. Create FRAUD_LOCK for SITE/MSA orders.
7. Check is Enrich Feature enabled for the given order.
8. If Enrich Feature enabled then call order enrichment service by passing the order collect response.
9. Publish the enriched object to ordercreation_onsuccess_<env> topic.

** Steps to install and run OrderCollect in local:**


* Set GOOGLE_APPLICATION_CREDENTIALS environment variable to path of credentials json file
* Clone and do a mvn clean install op-foundation
* Clone and do a mvn clean install op-parent
* Clone and do a mvn clean install uopcommoncontracts
* Maven clean install Ordercollectorchestrator and run as spring boot project 
*
