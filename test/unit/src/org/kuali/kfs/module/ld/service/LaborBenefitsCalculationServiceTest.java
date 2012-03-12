/*
 * Copyright 2007-2008 The Kuali Foundation
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
