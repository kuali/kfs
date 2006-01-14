/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.chart.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.test.KualiTestBaseWithSpring;

/**
 * This class tests the ObjectCode service.
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ObjectCodeServiceTest extends KualiTestBaseWithSpring {
    public static final String CHART_CODE = TestConstants.Data4.CHART_CODE;

    private ObjectCodeService objectCodeService;

    protected void setUp() throws Exception {
        super.setUp();

        objectCodeService = SpringServiceLocator.getObjectCodeService();
    }

    public void testPropertyUtilsDescribe() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        ObjectCode objectCode = new ObjectCode();
        Map boProps = PropertyUtils.describe(objectCode);
    }
    
    public void testFindById() {
        ObjectCode objectCode = objectCodeService.getByPrimaryId(new Integer(2004), CHART_CODE, "5000");
        assertNotNull(objectCode);
    }
    public void testFindById2() {
        ObjectCode objectCode = objectCodeService.getByPrimaryId(new Integer(2006), CHART_CODE, "none");
        assertNull(objectCode);
    }
    
    public void testObjectTypeRetrieval() {
        ObjectCode objectCode = objectCodeService.getByPrimaryId(new Integer(2004), CHART_CODE, "5000");
        assertNotNull("ObjectType Object should be valid.", objectCode.getFinancialObjectType());
        assertEquals("Object Type should be EE",objectCode.getFinancialObjectType().getCode(),"EX");
    }
    
    public void testObjectSubTypeRetrieval() {
        ObjectCode objectCode = objectCodeService.getByPrimaryId(new Integer(2004), CHART_CODE, "5000");
        assertNotNull("ObjSubTyp Object should be valid.", objectCode.getFinancialObjectSubType());
        assertEquals("Object Type","NA",objectCode.getFinancialObjectSubType().getCode());
    }
    public void testBudgetAggregationCodeRetrieval() {
        ObjectCode objectCode = objectCodeService.getByPrimaryId(new Integer(2004), CHART_CODE, "5000");
        assertNotNull("BudgetAggregationCode Object should be valid.", objectCode.getFinancialBudgetAggregation());
        assertEquals("Budget Aggregation Code should be something",objectCode.getFinancialBudgetAggregation().getCode(),"O");
    }    
    public void testMandatoryTransferEliminationCodeRetrieval() {
      ObjectCode objectCode = objectCodeService.getByPrimaryId(new Integer(2004), CHART_CODE, "5000");
      assertNotNull("MandatoryTransferEliminationCode Object should be valid.", objectCode.getFinObjMandatoryTrnfrelim());
      assertEquals("Mandatory Transfer Elimination Code should be something",objectCode.getFinObjMandatoryTrnfrelim().getCode(),"N");
    }
    public void testFederalFundedCodeRetrieval() {
      ObjectCode objectCode = objectCodeService.getByPrimaryId(new Integer(2004), CHART_CODE, "5000");
      assertNotNull("FederalFundedCode Object should be valid.", objectCode.getFinancialFederalFunded());
      assertEquals("Federal Funded Code should be something",objectCode.getFinancialFederalFunded().getCode(),"N");
    }
}
