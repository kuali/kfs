/*
 * Copyright 2010 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.tem.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.web.bean.TravelReimbursementMvcWrapperBean;
import org.kuali.kfs.module.tem.document.web.struts.AddActualExpenseEvent;
import org.kuali.kfs.module.tem.test.infrastructure.TemUnitTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.service.impl.KualiRuleServiceImpl;

/**
 * Tests execution of the {@link AddActualExpenseEvent}
 *
 */
public class AddActualExpenseEventTest extends TemUnitTestBase {
    private AddActualExpenseEvent event;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        event = SpringContext.getBean(AddActualExpenseEvent.class);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Basic test for update on the {@link ExpensesChangeEvent}. <code>ruleService</code> is overridden on the event
     * because we do not want to get into integration tests. Just test the code of the service. For the same reason,
     * the <code>travelReimbursementService</code> is also overridden. That's what all the {@link #overrideService()}
     * calls are about.
     *
     * A {@link Proxy} is then created for the wrapper so we can implement parts of the wrapper we use during the test and only
     * those.
     *
     */
	@Test
	public void testUpdate1() throws Exception {
        // Preparing the test
        try {
            overrideService(event, "ruleService",
                            new KualiRuleServiceImpl() {
                                @Override
                                public boolean applyRules(final KualiDocumentEvent event) {
                                    return true;
                                }
                            });
        }
        catch(Exception e) {
            e.printStackTrace();
            throw e;
        }

        // Test expense!
        final ActualExpense testExpense = new ActualExpense();
        testExpense.setTravelExpenseTypeCodeId(new Long(1));

        final TravelReimbursementDocument testDocument = new TravelReimbursementDocument() {
            };
        testDocument.setActualExpenses(new ArrayList<ActualExpense>());

        final ArrayList<ActualExpense> testNewLines = new ArrayList<ActualExpense>();

        final TravelReimbursementMvcWrapperBean testBean = (TravelReimbursementMvcWrapperBean)
            Proxy.newProxyInstance(getClass().getClassLoader(),
                                   new Class[] { TravelReimbursementMvcWrapperBean.class },
                                   new InvocationHandler() {
                                       @Override
                                    public Object invoke(Object proxy, Method method, Object ... args) {
                                           if (method.getName().equals("getNewActualExpenseLine")) {
                                               return testExpense;
                                           }
                                           if (method.getName().equals("getTravelReimbursementDocument")) {
                                               return testDocument;
                                           }
                                           if (method.getName().equals("setCalculated")) {
                                           }
                                           return null;
                                       }
                                   });
        // Done preparing the test
        // Run actual stuff
        event.update(null, testBean);

        assertTrue(testNewLines.size() == 1);
        assertTrue(testDocument.getActualExpenses().size() == 1);
        assertTrue(testDocument.getActualExpenses().get(0).equals(testExpense));
	}

    // End of test methods

    protected void overrideService(final Object overriddenObj,
                                   final String toOverride,
                                   final Object newService) throws Exception {
        Class serviceClass = newService.getClass();
        if (serviceClass.isAnonymousClass()) {
            serviceClass = serviceClass.getSuperclass();
            for (final Class interf : serviceClass.getInterfaces()) {
                if (serviceClass.getSimpleName().indexOf(interf.getSimpleName()) > -1) {
                    serviceClass = interf;
                }
            }
        }

        final Method method = overriddenObj.getClass().getDeclaredMethod(getSetterName(toOverride), new Class[] {serviceClass});
        method.setAccessible(true);
        method.invoke(overriddenObj, newService);
    }

    protected String getSetterName(final String propertyName) {
        final String uc = propertyName.substring(0, 1).toUpperCase();
        final String retval = "set" + uc + propertyName.substring(1);
        return retval;
    }

}
