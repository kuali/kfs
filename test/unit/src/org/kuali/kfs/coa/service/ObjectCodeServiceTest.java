/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.chart.service;

import static org.kuali.kfs.util.SpringServiceLocator.getObjectCodeService;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.test.RequiresSpringContext;

/**
 * This class tests the ObjectCode service.
 * 
 * 
 */
@RequiresSpringContext
public class ObjectCodeServiceTest extends KualiTestBase {
    public static final String CHART_CODE = TestConstants.Data4.CHART_CODE;

    public void testPropertyUtilsDescribe() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        ObjectCode objectCode = new ObjectCode();
        Map boProps = PropertyUtils.describe(objectCode);
    }

    public void testGetYersList() {
        ObjectCode objectCode = new ObjectCode();
        // objectCode = ObjectCodeDao.
        List list = getObjectCodeService().getYearList("BL", "5050");
        assertNotNull("interface garuentee not returning Null", list);

        assertTrue("expect more than one result", list.size() > 0);

    }

    public void testGetYersListEmpty() {
        ObjectCode objectCode = new ObjectCode();
        // objectCode = ObjectCodeDao.
        List list = getObjectCodeService().getYearList("BL", "asdfasdf");
        assertNotNull("interface garuentee not returning Null", list);
        assertTrue("expect more than one result", list.size() == 0);

    }


    public void testFindById() {
        ObjectCode objectCode = getObjectCodeService().getByPrimaryId(new Integer(2004), CHART_CODE, "5000");
        assertNotNull(objectCode);
    }

    public void testFindById2() {
        ObjectCode objectCode = getObjectCodeService().getByPrimaryId(new Integer(2006), CHART_CODE, "none");
        assertNull(objectCode);
    }

    public void testObjectTypeRetrieval() {
        ObjectCode objectCode = getObjectCodeService().getByPrimaryId(new Integer(2004), CHART_CODE, "5000");
        assertTrue("ObjectType Object should be valid.", ObjectUtils.isNotNull(objectCode.getFinancialObjectType()));
        assertEquals("Object Type should be EE", objectCode.getFinancialObjectType().getCode(), "EX");
    }

    public void testObjectSubTypeRetrieval() {
        ObjectCode objectCode = getObjectCodeService().getByPrimaryId(new Integer(2004), CHART_CODE, "5000");
        assertTrue("ObjSubTyp Object should be valid.", ObjectUtils.isNotNull(objectCode.getFinancialObjectSubType()));
        assertEquals("Object Type", "NA", objectCode.getFinancialObjectSubType().getCode());
    }

    public void testBudgetAggregationCodeRetrieval() {
        ObjectCode objectCode = getObjectCodeService().getByPrimaryId(new Integer(2004), CHART_CODE, "5000");
        assertTrue("BudgetAggregationCode Object should be valid.", ObjectUtils.isNotNull(objectCode.getFinancialBudgetAggregation()));
        assertEquals("Budget Aggregation Code should be something", objectCode.getFinancialBudgetAggregation().getCode(), "O");
    }

    public void testMandatoryTransferEliminationCodeRetrieval() {
        ObjectCode objectCode = getObjectCodeService().getByPrimaryId(new Integer(2004), CHART_CODE, "5000");
        assertTrue("MandatoryTransferEliminationCode Object should be valid.", ObjectUtils.isNotNull(objectCode.getFinObjMandatoryTrnfrelim()));
        assertEquals("Mandatory Transfer Elimination Code should be something", objectCode.getFinObjMandatoryTrnfrelim().getCode(), "N");
    }

    public void testFederalFundedCodeRetrieval() {
        ObjectCode objectCode = getObjectCodeService().getByPrimaryId(new Integer(2004), CHART_CODE, "5000");
        assertTrue("FederalFundedCode Object should be valid.", ObjectUtils.isNotNull(objectCode.getFinancialFederalFunded()));
        assertEquals("Federal Funded Code should be something", objectCode.getFinancialFederalFunded().getCode(), "N");
    }
}
