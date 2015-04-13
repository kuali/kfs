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
package org.kuali.kfs.module.ld.service;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.module.ld.businessobject.BenefitsCalculation;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KualiTestConstants.TestConstants.BenefitsCalculationServiceImplTest;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * junit test for LaborBenefitsCalculationServiceImplTest
 */
@ConfigureContext
public class LaborBenefitsCalculationServiceTest extends KualiTestBase {

    private Properties properties;
    private String fieldNames;
    private String deliminator;
    private List<String> keyFieldList;
    private Map fieldValues;

    private LaborBenefitsCalculationService laborBenefitsCalculationService;
    private BusinessObjectService businessObjectService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        laborBenefitsCalculationService = SpringContext.getBean(LaborBenefitsCalculationService.class);
    }

    /**
     * Test get benefits calculation method returns valid for BenefitsCalculation
     * 
     * @throws Exception
     */
    public void testGetBenefitsCalculation_valid() throws Exception {
        BenefitsCalculation benefitsCalculation = laborBenefitsCalculationService.getBenefitsCalculation(Integer.valueOf(BenefitsCalculationServiceImplTest.FISCAL_YEAR), BenefitsCalculationServiceImplTest.CHART, BenefitsCalculationServiceImplTest.POSITION_TYPE_CODE);
        super.assertNotNull("Expected valid Fiscal Year Chart and Position and result was null", benefitsCalculation);
    }
}
