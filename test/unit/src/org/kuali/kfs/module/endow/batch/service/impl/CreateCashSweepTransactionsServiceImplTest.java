/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.batch.service.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.kfs;

import org.kuali.kfs.module.endow.businessobject.AutomatedCashInvestmentModel;
import org.kuali.kfs.module.endow.businessobject.CashSweepModel;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.GLLink;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLotRebalance;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidGeneralLedgerAccount;
import org.kuali.kfs.module.endow.businessobject.PooledFundControl;
import org.kuali.kfs.module.endow.businessobject.RegistrationCode;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;
import org.kuali.kfs.module.endow.document.AssetDecreaseDocument;
import org.kuali.kfs.module.endow.document.AssetIncreaseDocument;
import org.kuali.kfs.module.endow.fixture.CashSweepModelFixture;
import org.kuali.kfs.module.endow.fixture.ClassCodeFixture;
import org.kuali.kfs.module.endow.fixture.CurrentCashFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionCodeFixture;
import org.kuali.kfs.module.endow.fixture.GLLinkFixture;
import org.kuali.kfs.module.endow.fixture.HoldingTaxLotFixture;
import org.kuali.kfs.module.endow.fixture.HoldingTaxLotRebalanceFixture;
import org.kuali.kfs.module.endow.fixture.KemIdFixture;
import org.kuali.kfs.module.endow.fixture.KemidGeneralLedgerAccountFixture;
import org.kuali.kfs.module.endow.fixture.PooledFundControlTransactionFixture;
import org.kuali.kfs.module.endow.fixture.RegistrationCodeFixture;
import org.kuali.kfs.module.endow.fixture.SecurityFixture;
import org.kuali.kfs.module.endow.fixture.SecurityReportingGroupFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.krad.service.BusinessObjectService;

@ConfigureContext(session = kfs)
public class CreateCashSweepTransactionsServiceImplTest extends KualiTestBase {

    private AssetIncreaseDocument assetIncreaseDocument = null;
    private AssetDecreaseDocument assetDecreaseDocument = null;
    private CreateCashSweepTransactionsServiceImpl createCashSweepTransactionsService;

    private Security security1;
    private Security security2;
    private AutomatedCashInvestmentModel aciModel;
    private PooledFundControl pooledFundControl;
    private RegistrationCode registrationCode;
    private EndowmentTransactionCode endowmentTransactionCode; 
    private KEMID kemid1;
    private KEMID kemid2;
    private CashSweepModel cashSweepModelSale;
    private CashSweepModel cashSweepModelPurchase;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception { 
        super.setUp();        

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        createCashSweepTransactionsService = (CreateCashSweepTransactionsServiceImpl) TestUtils.getUnproxiedService("mockCreateCashSweepTransactionsService");

        SecurityReportingGroup reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        endowmentTransactionCode = EndowmentTransactionCodeFixture.ASSET_TRANSACTION_CODE_2.createEndowmentTransactionCode();                
        ClassCode classCode = ClassCodeFixture.ASSET_CLASS_CODE.createClassCodeRecord();
        
        security1 = SecurityFixture.ENDOWMENT_ASSET_INCOME_SECURITY_RECORD.createSecurityRecord();
        security2 = SecurityFixture.ENDOWMENT_ASSET_PRINCIPAL_SECURITY_RECORD.createSecurityRecord();
        RegistrationCodeFixture.ASSET_REGISTRATION_CODE_RECORD_1.createRegistrationCode();
        RegistrationCodeFixture.ASSET_REGISTRATION_CODE_RECORD_2.createRegistrationCode();

        kemid1 = KemIdFixture.CSM_KEMID_RECORD_1.createKemidRecord();
        kemid2 = KemIdFixture.CSM_KEMID_RECORD_2.createKemidRecord();
      
        GLLink glLink = GLLinkFixture.GL_LINK_BL_CHART.createGLLink();
        KemidGeneralLedgerAccount generalLedgerAccount1 = KemidGeneralLedgerAccountFixture.KEMID_GL_ACCOUNT_FOR_ASSET1.createKemidGeneralLedgerAccount();
        KemidGeneralLedgerAccount generalLedgerAccount2 = KemidGeneralLedgerAccountFixture.KEMID_GL_ACCOUNT_FOR_ASSET2.createKemidGeneralLedgerAccount();
        kemid1.getKemidGeneralLedgerAccounts().add(generalLedgerAccount1);
        kemid2.getKemidGeneralLedgerAccounts().add(generalLedgerAccount2);
        
        endowmentTransactionCode.getGlLinks().add(glLink);

        PooledFundControl p1 = PooledFundControlTransactionFixture.ASSET_INCOME_DATA.createSavePooledFundControl();
        PooledFundControl p2 = PooledFundControlTransactionFixture.ASSET_PURCHASE_DATA.createSavePooledFundControl();
        
        cashSweepModelSale = CashSweepModelFixture.PRINCIPAL_SALE_DATA.createCashSweepModel();
        cashSweepModelPurchase = CashSweepModelFixture.PRINCIPAL_PURCHASE_DATA.createCashSweepModel();
        
        kemid1.setCashSweepModelId(cashSweepModelSale.getCashSweepModelID());   
        businessObjectService.save(kemid1);
        kemid2.setCashSweepModelId(cashSweepModelPurchase.getCashSweepModelID());
        businessObjectService.save(kemid2);
        
        CurrentCashFixture.PRINCIPAL_SALE_ASSET_RECORD.createKemidCurrentCashRecord();
        CurrentCashFixture.PRINCIPAL_PURCHASE_ASSET_RECORD.createKemidCurrentCashRecord();
        
        HoldingTaxLotRebalance hr1 = HoldingTaxLotRebalanceFixture.HOLDING_TAX_LOT_REBALANCE_RECORD_FOR_SALE1.createHoldingTaxLotRebalanceRecord();
        HoldingTaxLotRebalance hr2 = HoldingTaxLotRebalanceFixture.HOLDING_TAX_LOT_REBALANCE_RECORD_FOR_SALE2.createHoldingTaxLotRebalanceRecord();
        HoldingTaxLot h1 = HoldingTaxLotFixture.HOLDING_TAX_LOT_RECORD_FOR_SALE1.createHoldingTaxLotRecord();
        HoldingTaxLot h2 = HoldingTaxLotFixture.HOLDING_TAX_LOT_RECORD_FOR_SALE2.createHoldingTaxLotRecord(); 
    }
    
    /**
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        createCashSweepTransactionsService = null;
        super.tearDown();        
    }
    
    /**
     * validates processIncomeSweepPurchases()
     */
    public void testProcessIncomeSweepPurchases() {
        assertTrue(createCashSweepTransactionsService.processIncomeSweepPurchases(cashSweepModelPurchase));
    }
    
    /**
     * validates processIncomeSweepSales()
     */
    public void testProcessIncomeSweepSales() {
        assertTrue(createCashSweepTransactionsService.processIncomeSweepSales(cashSweepModelSale));
    }
    /**
     * validates processPrincipalSweepPurchases()
     */
    public void testProcessPrincipalSweepPurchases() {
        assertTrue(createCashSweepTransactionsService.processPrincipalSweepPurchases(cashSweepModelPurchase));
    }

    /**
     * validates processPrincipalSweepSale()
     */
    public void testProcessPrincipalSweepSale() {     
        assertTrue(createCashSweepTransactionsService.processPrincipalSweepSale(cashSweepModelSale));
    }
          
}
