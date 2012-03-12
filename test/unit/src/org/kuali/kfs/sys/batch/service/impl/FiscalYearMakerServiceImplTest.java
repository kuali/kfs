/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.sys.batch.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.batch.dataaccess.FiscalYearMaker;
import org.kuali.kfs.sys.batch.dataaccess.FiscalYearMakersDao;
import org.kuali.kfs.sys.businessobject.FiscalYearBasedBusinessObject;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.FiscalYearMakerFixture;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.KualiModuleService;

@ConfigureContext
public class FiscalYearMakerServiceImplTest extends KualiTestBase {
    private FiscalYearMakerServiceImpl fiscalYearMakerService;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        fiscalYearMakerService = new FiscalYearMakerServiceImpl();
        fiscalYearMakerService.setFiscalYearMakersDao(SpringContext.getBean(FiscalYearMakersDao.class));
        fiscalYearMakerService.setParameterService(SpringContext.getBean(ParameterService.class));
        fiscalYearMakerService.setKualiModuleService(SpringContext.getBean(KualiModuleService.class));
    }

    public final void NORUN_testRunProcess() throws Exception {
        fiscalYearMakerService.runProcess();
    }

    /**
     * Tests the current spring configuration is valid by getting the copy order which will perform validation
     */
    public final void testGetFiscalYearMakerHelpersConfiguration() throws Exception {
        fiscalYearMakerService.initialize();

        getFiscalYearMakerHelpersInCopyOrderTest();
    }

    /**
     * Tests the default fiscal year maker implementation getFiscalYearMakerHelpersInCopyOrder method with a valid setup
     * (parent-child configuration)
     */
    public final void testGetFiscalYearMakerHelpersInCopyOrder_valid() throws Exception {
        // get valid FYM list and set in service
        List<FiscalYearMaker> fiscalYearMakers = FiscalYearMakerFixture.getFiscalYearMakerList_valid();
        fiscalYearMakerService.setFiscalYearMakers(fiscalYearMakers);

        getFiscalYearMakerHelpersInCopyOrderTest();
    }

    /**
     * Tests the default fiscal year maker implementation getFiscalYearMakerHelpersInCopyOrder method with a valid setup
     * (parent-child configuration)
     */
    private final void getFiscalYearMakerHelpersInCopyOrderTest() throws Exception {
        // get the fym list in copy order
        List<FiscalYearMaker> fiscalYearMakersCopyOrder = fiscalYearMakerService.getFiscalYearMakerHelpersInCopyOrder();

        assertEquals("Copy order size does not match maker list size", fiscalYearMakerService.getFiscalYearMakers().size(), fiscalYearMakersCopyOrder.size());

        // turn list of fym into list of classes
        List<Class<? extends FiscalYearBasedBusinessObject>> classCopyOrder = new ArrayList<Class<? extends FiscalYearBasedBusinessObject>>();
        for (FiscalYearMaker fiscalYearMaker : fiscalYearMakersCopyOrder) {
            classCopyOrder.add(fiscalYearMaker.getBusinessObjectClass());
        }

        // verify for each child its parents appear in copy list with a lower index (thus will be copied first)
        for (FiscalYearMaker fiscalYearMaker : fiscalYearMakerService.getFiscalYearMakers()) {
            Class<? extends FiscalYearBasedBusinessObject> child = fiscalYearMaker.getBusinessObjectClass();

            Set<Class<? extends FiscalYearBasedBusinessObject>> parents = fiscalYearMaker.getParentClasses();
            for (Class<? extends FiscalYearBasedBusinessObject> parent : parents) {
                String msg = String.format("Parent class %s not in copy list before child class %s", parent.getName(), child.getName());
                assertTrue(msg, classCopyOrder.indexOf(parent) < classCopyOrder.indexOf(child));
            }
        }
    }

    /**
     * Tests the default fiscal year maker implementation validateFiscalYearMakerConfiguration method with a null business object
     * class. Service should throw an exception
     */
    public final void testValidateFiscalYearMakerConfiguration_nullBusinessObjectClass() throws Exception {
        List<FiscalYearMaker> fiscalYearMakers = FiscalYearMakerFixture.getFiscalYearMakerList_nullBusinessObjectClass();
        fiscalYearMakerService.setFiscalYearMakers(fiscalYearMakers);

        boolean failedAsExpected = false;
        try {
            fiscalYearMakerService.validateFiscalYearMakerConfiguration();
        }
        catch (RuntimeException e) {
            failedAsExpected = true;
        }

        assertTrue("Exception not thrown for missing parent configuration", failedAsExpected);
    }

    /**
     * Tests the default fiscal year maker implementation validateFiscalYearMakerConfiguration method with two fiscal year maker
     * implementations for the same business object class. Service should throw an exception
     */
    public final void testValidateFiscalYearMakerConfiguration_duplicateBusinessObjectClass() throws Exception {
        List<FiscalYearMaker> fiscalYearMakers = FiscalYearMakerFixture.getFiscalYearMakerList_duplicateBusinessObjectClass();
        fiscalYearMakerService.setFiscalYearMakers(fiscalYearMakers);

        boolean failedAsExpected = false;
        try {
            fiscalYearMakerService.validateFiscalYearMakerConfiguration();
        }
        catch (RuntimeException e) {
            failedAsExpected = true;
        }

        assertTrue("Exception not thrown for missing parent configuration", failedAsExpected);
    }

    /**
     * Tests the default fiscal year maker implementation validateFiscalYearMakerConfiguration method with a parent that is not in
     * copy list. Service should throw an exception
     */
    public final void testValidateFiscalYearMakerConfiguration_missingParent() throws Exception {
        List<FiscalYearMaker> fiscalYearMakers = FiscalYearMakerFixture.getFiscalYearMakerList_missingParent();
        fiscalYearMakerService.setFiscalYearMakers(fiscalYearMakers);

        boolean failedAsExpected = false;
        try {
            fiscalYearMakerService.validateFiscalYearMakerConfiguration();
        }
        catch (RuntimeException e) {
            failedAsExpected = true;
        }

        assertTrue("Exception not thrown for missing parent configuration", failedAsExpected);
    }

    /**
     * Tests the default fiscal year maker implementation getFiscalYearMakerHelpersInCopyOrder method with a circular setup
     * (parent-child configuration). Service should throw an exception
     */
    public final void testGetFiscalYearMakerHelpersInCopyOrder_circular1() throws Exception {
        // get circular FYM list and set in service
        List<FiscalYearMaker> fiscalYearMakers = FiscalYearMakerFixture.getFiscalYearMakerList_circular1();
        fiscalYearMakerService.setFiscalYearMakers(fiscalYearMakers);

        testCircular();
    }

    /**
     * Tests the default fiscal year maker implementation getFiscalYearMakerHelpersInCopyOrder method with a circular setup
     * (parent-child configuration). Service should throw an exception
     */
    public final void testGetFiscalYearMakerHelpersInCopyOrder_circular2() throws Exception {
        // get circular FYM list and set in service
        List<FiscalYearMaker> fiscalYearMakers = FiscalYearMakerFixture.getFiscalYearMakerList_circular2();
        fiscalYearMakerService.setFiscalYearMakers(fiscalYearMakers);

        testCircular();
    }

    /**
     * Helper method to call getFiscalYearMakerHelpersInCopyOrder and verify an exception was thrown. Service should be setup with
     * maker list that has a circular reference
     */
    public final void testCircular() throws Exception {
        // call getFiscalYearMakerHelpersInCopyOrder which will validate the setup and should throw exception
        boolean failedAsExpected = false;
        try {
            List<FiscalYearMaker> fiscalYearMakersCopyOrder = fiscalYearMakerService.getFiscalYearMakerHelpersInCopyOrder();
        }
        catch (RuntimeException e) {
            failedAsExpected = true;
        }

        assertTrue("Exception not thrown for circular reference", failedAsExpected);
    }

}
