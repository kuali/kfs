/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.coa.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class tests the ObjectCode service.
 */
@ConfigureContext
public class ObjectCodeServiceTest extends KualiTestBase {
    public static final String CHART_CODE = TestConstants.Data4.CHART_CODE;

    public void testPropertyUtilsDescribe() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        ObjectCode objectCode = new ObjectCode();
        Map boProps = PropertyUtils.describe(objectCode);
    }

    public void testGetYersList() {
        List list = SpringContext.getBean(ObjectCodeService.class).getYearList("BL", "5050");
        assertNotNull("interface garuentee not returning Null", list);

        assertTrue("expect more than one result", list.size() > 0);

    }

    public void testGetYersListEmpty() {
        List list = SpringContext.getBean(ObjectCodeService.class).getYearList("BL", "asdfasdf");
        assertNotNull("interface garuentee not returning Null", list);
        assertTrue("expect more than one result", list.size() == 0);

    }


    public void testFindById() {
        ObjectCode objectCode = SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(TestUtils.getFiscalYearForTesting(), CHART_CODE, "5000");
        assertNotNull(objectCode);
    }

    public void testFindById2() {
        ObjectCode objectCode = SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(TestUtils.getFiscalYearForTesting(), CHART_CODE, "none");
        assertNull(objectCode);
    }

    public void testObjectTypeRetrieval() {
        ObjectCode objectCode = SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(TestUtils.getFiscalYearForTesting(), CHART_CODE, "5000");
        assertTrue("ObjectType Object should be valid.", ObjectUtils.isNotNull(objectCode.getFinancialObjectType()));
        assertEquals("Object Type should be EE", objectCode.getFinancialObjectType().getCode(), "EX");
    }

    public void testObjectSubTypeRetrieval() {
        ObjectCode objectCode = SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(TestUtils.getFiscalYearForTesting(), CHART_CODE, "5000");
        assertTrue("ObjSubTyp Object should be valid.", ObjectUtils.isNotNull(objectCode.getFinancialObjectSubType()));
        assertEquals("Object Type", "NA", objectCode.getFinancialObjectSubType().getCode());
    }

    public void testBudgetAggregationCodeRetrieval() {
        ObjectCode objectCode = SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(TestUtils.getFiscalYearForTesting(), CHART_CODE, "5000");
        assertTrue("BudgetAggregationCode Object should be valid.", ObjectUtils.isNotNull(objectCode.getFinancialBudgetAggregation()));
        assertEquals("Budget Aggregation Code should be something", objectCode.getFinancialBudgetAggregation().getCode(), "O");
    }

    public void testMandatoryTransferEliminationCodeRetrieval() {
        ObjectCode objectCode = SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(TestUtils.getFiscalYearForTesting(), CHART_CODE, "5000");
        assertTrue("MandatoryTransferEliminationCode Object should be valid.", ObjectUtils.isNotNull(objectCode.getFinObjMandatoryTrnfrelim()));
        assertEquals("Mandatory Transfer Elimination Code should be something", objectCode.getFinObjMandatoryTrnfrelim().getCode(), "N");
    }

    public void testFederalFundedCodeRetrieval() {
        ObjectCode objectCode = SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(TestUtils.getFiscalYearForTesting(), CHART_CODE, "5000");
        assertTrue("FederalFundedCode Object should be valid.", ObjectUtils.isNotNull(objectCode.getFinancialFederalFunded()));
        assertEquals("Federal Funded Code should be something", objectCode.getFinancialFederalFunded().getCode(), "N");
    }
}
