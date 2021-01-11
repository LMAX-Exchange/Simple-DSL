Simple-DSL
==========

![Java CI with Maven](https://github.com/LMAX-Exchange/Simple-DSL/workflows/Java%20CI%20with%20Maven/badge.svg)
[![License](https://img.shields.io/github/license/LMAX-Exchange/Simple-DSL)](https://github.com/LMAX-Exchange/Simple-DSL/blob/master/LICENCE.txt)


Simple-DSL is a style for writing acceptance tests used at LMAX Exchange that aims to balance human and machine readability. The intention is that developers and non-developers alike can easily read
and understand an acceptance test, and developer IDEs can understand enough of an acceptance test to support useful, but not necessarily comprehensive searching, refactoring and name completion.

The Simple-DSL library provides one component of the DSL for writing acceptance tests - focusing on parsing arguments to methods. The rest of the DSL is heavily
dependent on the system under test but the [wiki](https://github.com/LMAX-Exchange/Simple-DSL/wiki) provides a number of patterns that have proven successful in
building DSLs that stand the test of time.


### Example

A simple test case for placing an order on an exchange might look like:

    package com.lmax.exchange.acceptance.test.api;

    import com.lmax.exchange.acceptance.dsl.DslTestCase;
    import org.junit.Before;
    import org.junit.Test;

    public class PlaceOrderAcceptanceTest extends DslTestCase
    {
        @Before
        public void setup()
        {
            adminAPI.createInstrument("name: FTSE100");
            registrationAPI.createUser("Bob");

            publicAPI.login("Bob");
        }

        @Test
        public void shouldReceiveCancellationMessageAfterCancellingAnOrder()
        {
            publicAPI.placeOrder("FTSE100", "side: buy", "quantity: 10", "price: 5000", "expectedStatus: UNMATCHED");
        }
    }

The top level variables <code>adminAPI</code>, <code>registrationAPI</code> and <code>publicAPI</code> are provided by the <code>DslTestCase</code> class and represent three key gateways different
types of users use to access the system. This is the type of test that results from applying the various patterns described on the [wiki](https://github.com/LMAX-Exchange/Simple-DSL/wiki). However,
the Simple-DSL library is focused on verifying and parsing the string arguments that are passed to each method. The implementation of publicAPI.placeOrder would look something like:

    public void placeOrder(String... args) {
        DslParams params = new DslParams(args,
                                         new RequiredParam("instrument"),
                                         new RequiredParam("side").setAllowedValues("buy", "sell"),
                                         new RequiredParam("quantity"),
                                         new OptionalParam("price"),
                                         new OptionalParam("expectedStatus").setAllowedValues("REJECTED", "UNMATCHED", "MATCHED").setDefault("MATCHED"));

        long instrumentId = testContext.getInstrumentId(params.value("instrument"));
        BigDecimal quantity = params.valueAsBigDecimal("quantity");
        boolean buy = params.value("side").equals(buy);
        PublicApiDriver driver = getDriver();
        String orderId = driver.placeOrder(instrumentId, quantity, buy);
        driver.waitForOrderStatus(orderId, params.value("expectedStatus"));
    }

Creating the <code>DslParams</code> object defines which parameters are accepted by the method, which are required, what values are allowed and any default values. The <code>params</code> object
can then be used to retrieve the values in a variety of forms and uses the driver layer to interact with the system under test to actually place the order. If the steps required to place an order
change in the future, the driver or the way this method uses it can be adapted without needing to change every test that places an order.

### Other Resources

 * The [wiki](https://github.com/LMAX-Exchange/Simple-DSL/wiki) provides further examples and patterns to build out a DSL using Simple-DSL.
 * The [Testing@LMAX series](https://www.symphonious.net/testing-at-lmax/) provides further detail on the approach we take to testing.
