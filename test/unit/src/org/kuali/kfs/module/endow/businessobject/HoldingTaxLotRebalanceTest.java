/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;

@ConfigureContext(session = khuntley)
public class HoldingTaxLotRebalanceTest extends KualiTestBase {

    private BusinessObjectService businessObjectService;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
    }
    
    @ConfigureContext(shouldCommitTransactions = false)
    public void testOJBConfiguration() throws Exception {

        HoldingTaxLotRebalance htlr = new HoldingTaxLotRebalance();
        htlr.setIncomePrincipalIndicator("I");
        htlr.setRegistrationCode("01P");
        htlr.setSecurityId("9128273E0");
        htlr.setKemid("099PLTF013");
        
        htlr.setTotalUnits(new BigDecimal(22.0000));
        htlr.setTotalLotNumber(new BigDecimal(1));
        htlr.setTotalCost(new BigDecimal(1.00));
        
        businessObjectService.save(htlr);
        
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(HoldingTaxLotRebalance.INCOME_PRINCIPAL_INDICATOR, "I");
        primaryKeys.put(HoldingTaxLotRebalance.REGISTRATION_CODE, "01P");
        primaryKeys.put(HoldingTaxLotRebalance.SECURITY_ID, "9128273E0");
        primaryKeys.put(HoldingTaxLotRebalance.KEMID, "099PLTF013");
        
        HoldingTaxLotRebalance newHtlr = (HoldingTaxLotRebalance) businessObjectService.findByPrimaryKey(HoldingTaxLotRebalance.class, primaryKeys);
        
        assertTrue(newHtlr.getKemid().equals(htlr.getKemid()));
        assertTrue(newHtlr.getIncomePrincipalIndicator().equals(htlr.getIncomePrincipalIndicator()));
        assertTrue(newHtlr.getRegistrationCode().equals(htlr.getRegistrationCode()));
        assertTrue(newHtlr.getSecurityId().equals(htlr.getSecurityId()));
    }
}
