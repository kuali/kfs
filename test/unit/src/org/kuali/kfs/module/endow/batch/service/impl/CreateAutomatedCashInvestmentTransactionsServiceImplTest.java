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

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.AutomatedCashInvestmentModel;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.GLLink;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidGeneralLedgerAccount;
import org.kuali.kfs.module.endow.businessobject.PooledFundControl;
import org.kuali.kfs.module.endow.businessobject.RegistrationCode;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;
import org.kuali.kfs.module.endow.document.AssetDecreaseDocument;
import org.kuali.kfs.module.endow.document.AssetIncreaseDocument;
import org.kuali.kfs.module.endow.document.service.UpdateAssetDecreaseDocumentTaxLotsService;
import org.kuali.kfs.module.endow.fixture.ClassCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionDocumentFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionLineFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionSecurityFixture;
import org.kuali.kfs.module.endow.fixture.GLLinkFixture;
import org.kuali.kfs.module.endow.fixture.HoldingTaxLotFixture;
import org.kuali.kfs.module.endow.fixture.HoldingTaxLotRebalanceFixture;
import org.kuali.kfs.module.endow.fixture.KemIdFixture;
import org.kuali.kfs.module.endow.fixture.KemidGeneralLedgerAccountFixture;
import org.kuali.kfs.module.endow.fixture.RegistrationCodeFixture;
import org.kuali.kfs.module.endow.fixture.SecurityFixture;
import org.kuali.kfs.module.endow.fixture.SecurityReportingGroupFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;

@ConfigureContext(session = kfs)
public class CreateAutomatedCashInvestmentTransactionsServiceImplTest extends KualiTestBase {

    private AssetIncreaseDocument assetIncreaseDocument = null;
    private AssetDecreaseDocument assetDecreaseDocument = null;
    private CreateAutomatedCashInvestmentTransactionsServiceImpl createAutomatedCashInvestmentTransactionsService;
    private UpdateAssetDecreaseDocumentTaxLotsService assetDecreaseDocumentTaxLotsService;

    private Security security;
    private AutomatedCashInvestmentModel aciModel;
    private PooledFundControl pooledFundControl;
    private RegistrationCode registrationCode;
    private EndowmentTransactionCode endowmentTransactionCode; 
    private KEMID kemid;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception { 
        super.setUp();        

        createAutomatedCashInvestmentTransactionsService = (CreateAutomatedCashInvestmentTransactionsServiceImpl) TestUtils.getUnproxiedService("mockCreateAutomatedCashInvestmentTransactionsService");
        assetDecreaseDocumentTaxLotsService = SpringContext.getBean(UpdateAssetDecreaseDocumentTaxLotsService.class);

        SecurityReportingGroup reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        endowmentTransactionCode = EndowmentTransactionCodeFixture.ASSET_TRANSACTION_CODE_2.createEndowmentTransactionCode();                
        ClassCode classCode = ClassCodeFixture.ASSET_CLASS_CODE.createClassCodeRecord();
        
        security = SecurityFixture.ENDOWMENT_ASSET_SECURITY_RECORD.createSecurityRecord();
        registrationCode = RegistrationCodeFixture.REGISTRATION_CODE_RECORD.createRegistrationCode();
        kemid = KemIdFixture.ALLOW_TRAN_KEMID_RECORD.createKemidRecord();        
        GLLink glLink = GLLinkFixture.GL_LINK_BL_CHART.createGLLink();
        KemidGeneralLedgerAccount generalLedgerAccount = KemidGeneralLedgerAccountFixture.KEMID_GL_ACCOUNT_FOR_ASSET.createKemidGeneralLedgerAccount();
        kemid.getKemidGeneralLedgerAccounts().add(generalLedgerAccount);
        endowmentTransactionCode.getGlLinks().add(glLink);
    }
    
    /**
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        createAutomatedCashInvestmentTransactionsService = null;
        assetDecreaseDocumentTaxLotsService = null;
        super.tearDown();        
    }

    /**
     * validates performCleanUpForAssetIncrease(), which creates EAI
     */
    public void testPerformCleanUpForAssetIncrease() {     
        
        EndowmentTargetTransactionLine targetTransactionLine = (EndowmentTargetTransactionLine)EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_FOR_EAI.createEndowmentTransactionLine(false);
        targetTransactionLine.setKemid(kemid.getKemid());
        targetTransactionLine.setKemidObj(kemid);
        targetTransactionLine.setTransactionLineTypeCode(EndowConstants.TRANSACTION_LINE_TYPE_TARGET);
        
        assetIncreaseDocument = (AssetIncreaseDocument) EndowmentTransactionDocumentFixture.ENDOWMENT_TRANSACTIONAL_DOCUMENT_REQUIRED_FIELDS_RECORD.createEndowmentTransactionDocument(AssetIncreaseDocument.class);
        assetIncreaseDocument.getDocumentHeader().setDocumentNumber(assetIncreaseDocument.getDocumentNumber());
        assetIncreaseDocument.getDocumentHeader().setDocumentDescription("Asset Increase Document Test");
        assetIncreaseDocument.setTransactionSourceTypeCode("A");
        assetIncreaseDocument.setTargetTransactionSecurity(EndowmentTransactionSecurityFixture.ENDOWMENT_TRANSACTIONAL_SECURITY_REQUIRED_FIELDS_RECORD.createEndowmentTransactionSecurity(false));
        assetIncreaseDocument.getTargetTransactionSecurity().setSecurityLineTypeCode("T");
        assetIncreaseDocument.getTargetTransactionSecurity().setRegistrationCode(registrationCode.getCode());
        assetIncreaseDocument.addTargetTransactionLine(targetTransactionLine);
        
        assertTrue(createAutomatedCashInvestmentTransactionsService.performCleanUpForAssetIncrease(false, assetIncreaseDocument));
    }
      
    /**
     * validates performCleanUpForAssetDecrease(), which creates EAD
     */
    public void testPerformCleanUpForAssetDecrease() {
    
        EndowmentSourceTransactionLine sourceTransactionLine = (EndowmentSourceTransactionLine)EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_FOR_EAD.createEndowmentTransactionLine(true);
        sourceTransactionLine.setKemid(kemid.getKemid());
        sourceTransactionLine.setKemidObj(kemid);
        sourceTransactionLine.setTransactionLineTypeCode(EndowConstants.TRANSACTION_LINE_TYPE_SOURCE);
        HoldingTaxLotRebalanceFixture.HOLDING_TAX_LOT_REBALANCE_RECORD_FOR_EAD.createHoldingTaxLotRebalanceRecord();
        HoldingTaxLot holdingTaxLot = HoldingTaxLotFixture.HOLDING_TAX_LOT_RECORD_FOR_EAD.createHoldingTaxLotRecord();   
        
        assetDecreaseDocument = (AssetDecreaseDocument) EndowmentTransactionDocumentFixture.ENDOWMENT_TRANSACTIONAL_DOCUMENT_REQUIRED_FIELDS_RECORD.createEndowmentTransactionDocument(AssetDecreaseDocument.class);
        assetDecreaseDocument.getDocumentHeader().setDocumentNumber(assetDecreaseDocument.getDocumentNumber());
        assetDecreaseDocument.getDocumentHeader().setDocumentDescription("Asset Decrease Document Test");
        assetDecreaseDocument.setTransactionSourceTypeCode("A");
        assetDecreaseDocument.setSourceTransactionSecurity(EndowmentTransactionSecurityFixture.ENDOWMENT_TRANSACTIONAL_SECURITY_REQUIRED_FIELDS_RECORD.createEndowmentTransactionSecurity(true));
        assetDecreaseDocument.getSourceTransactionSecurity().setSecurityLineTypeCode("F");
        assetDecreaseDocument.getSourceTransactionSecurity().setRegistrationCode(registrationCode.getCode());
        assetDecreaseDocument.addSourceTransactionLine(sourceTransactionLine);
        
        assetDecreaseDocumentTaxLotsService.updateTransactionLineTaxLots(assetDecreaseDocument, sourceTransactionLine);
        
        assertTrue(createAutomatedCashInvestmentTransactionsService.performCleanUpForAssetDecrease(false, assetDecreaseDocument));
    }

}
