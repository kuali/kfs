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
package org.kuali.kfs.module.endow.batch.service.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.kfs;

import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.GLLink;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidGeneralLedgerAccount;
import org.kuali.kfs.module.endow.businessobject.PooledFundControl;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;
import org.kuali.kfs.module.endow.fixture.ClassCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionCodeFixture;
import org.kuali.kfs.module.endow.fixture.GLLinkFixture;
import org.kuali.kfs.module.endow.fixture.KemIdFixture;
import org.kuali.kfs.module.endow.fixture.KemidGeneralLedgerAccountFixture;
import org.kuali.kfs.module.endow.fixture.PooledFundControlTransactionFixture;
import org.kuali.kfs.module.endow.fixture.RegistrationCodeFixture;
import org.kuali.kfs.module.endow.fixture.SecurityFixture;
import org.kuali.kfs.module.endow.fixture.SecurityReportingGroupFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;

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
        
        SecurityReportingGroup reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        EndowmentTransactionCode endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();                
        ClassCode classCode = ClassCodeFixture.ASSET_CLASS_CODE.createClassCodeRecord();
        
        Security security1 = SecurityFixture.ENDOWMENT_ASSET_INCOME_SECURITY_RECORD.createSecurityRecord();
        Security security2 = SecurityFixture.ENDOWMENT_ASSET_PRINCIPAL_SECURITY_RECORD.createSecurityRecord();
        RegistrationCodeFixture.ASSET_REGISTRATION_CODE_RECORD_1.createRegistrationCode();
        RegistrationCodeFixture.ASSET_REGISTRATION_CODE_RECORD_2.createRegistrationCode();

        KEMID kemid1 = KemIdFixture.CSM_KEMID_RECORD_1.createKemidRecord();
        KEMID kemid2 = KemIdFixture.CSM_KEMID_RECORD_2.createKemidRecord();
      
        GLLink glLink = GLLinkFixture.GL_LINK_BL_CHART.createGLLink();
        KemidGeneralLedgerAccount generalLedgerAccount1 = KemidGeneralLedgerAccountFixture.KEMID_GL_ACCOUNT_FOR_ASSET1.createKemidGeneralLedgerAccount();
        KemidGeneralLedgerAccount generalLedgerAccount2 = KemidGeneralLedgerAccountFixture.KEMID_GL_ACCOUNT_FOR_ASSET2.createKemidGeneralLedgerAccount();
        kemid1.getKemidGeneralLedgerAccounts().add(generalLedgerAccount1);
        kemid2.getKemidGeneralLedgerAccounts().add(generalLedgerAccount2);
        
        endowmentTransactionCode.getGlLinks().add(glLink);
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
        PooledFundControlTransactionFixture pooledFundControlTransactionFixture = PooledFundControlTransactionFixture.ASSET_PURCHASE_DATA;
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
        PooledFundControlTransactionFixture pooledFundControlTransactionFixture = PooledFundControlTransactionFixture.ASSET_INCOME_DATA;
        PooledFundControl pooledFundControl = pooledFundControlTransactionFixture.createPooledFundControl();
        KualiDecimal totalAmount = pooledFundControlTransactionFixture.getTotalAmount();
        String paramDescriptionName = pooledFundControlTransactionFixture.getParamDescriptionName();
        String securityLineType = pooledFundControlTransactionFixture.getSecurityLineType();
        String paramNoRouteInd = pooledFundControlTransactionFixture.getParamNoRouteInd();
        String incomePrincipalIndicator = pooledFundControlTransactionFixture.getIncomePrincipalIndicator();
        assertTrue("Failed to create ECI", pooledFundControlTransactionsService.createECI(pooledFundControl, totalAmount, paramDescriptionName, securityLineType, paramNoRouteInd, incomePrincipalIndicator));
    }
}
