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
package org.kuali.kfs.module.endow.batch.service.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.kfs;

import org.kuali.kfs.module.endow.batch.service.PooledFundControlTransactionsService;
import org.kuali.kfs.module.endow.businessobject.PooledFundControl;
import org.kuali.kfs.module.endow.fixture.PooledFundControlTransactionFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.kns.util.KualiDecimal;

@ConfigureContext(session = kfs)
public class PooledFundControlTransactionsServiceImplTest extends KualiTestBase {

    protected PooledFundControlTransactionsServiceImpl pooledFundControlTransactionsService;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception { 
        super.setUp();        
        pooledFundControlTransactionsService = (PooledFundControlTransactionsServiceImpl) TestUtils.getUnproxiedService("mockPooledFundControlTransactionsService");
        pooledFundControlTransactionsService.initializeReports();
    }
    
    /**
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        pooledFundControlTransactionsService = null;
    }

    /**
     * Validates CreateECDD()
     */
    public void testCreateECDD() {
        PooledFundControlTransactionFixture pooledFundControlTransactionFixture = PooledFundControlTransactionFixture.ECDD_DATA;
        PooledFundControl pooledFundControl = pooledFundControlTransactionFixture.createPooledFundControl();
        KualiDecimal totalAmount = pooledFundControlTransactionFixture.getTotalAmount();
        String paramDescriptionName = pooledFundControlTransactionFixture.getParamDescriptionName();
        String securityLineType = pooledFundControlTransactionFixture.getSecurityLineType();
        String paramNoRouteInd = pooledFundControlTransactionFixture.getParamNoRouteInd();
        String incomePrincipalIndicator = pooledFundControlTransactionFixture.getIncomePrincipalIndicator();
        assertTrue("Failed to create ECDD", pooledFundControlTransactionsService.createECDD(pooledFundControl, totalAmount, paramDescriptionName, securityLineType, paramNoRouteInd, incomePrincipalIndicator));
    }

    /**
     * Validates CreateECI()
     */
    public void testCreateECI() {
        PooledFundControlTransactionFixture pooledFundControlTransactionFixture = PooledFundControlTransactionFixture.ECI_DATA;
        PooledFundControl pooledFundControl = pooledFundControlTransactionFixture.createPooledFundControl();
        KualiDecimal totalAmount = pooledFundControlTransactionFixture.getTotalAmount();
        String paramDescriptionName = pooledFundControlTransactionFixture.getParamDescriptionName();
        String securityLineType = pooledFundControlTransactionFixture.getSecurityLineType();
        String paramNoRouteInd = pooledFundControlTransactionFixture.getParamNoRouteInd();
        String incomePrincipalIndicator = pooledFundControlTransactionFixture.getIncomePrincipalIndicator();
        assertTrue("Failed to create ECI", pooledFundControlTransactionsService.createECI(pooledFundControl, totalAmount, paramDescriptionName, securityLineType, paramNoRouteInd, incomePrincipalIndicator));
    }
}
