/*
 * Copyright 2010 The Kuali Foundation.
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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.EndowTestConstants;
import org.kuali.kfs.module.endow.batch.CreateGainLossDistributionTransactionsStep;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLineBase;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine;
import org.kuali.kfs.module.endow.businessobject.GLLink;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLotRebalance;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidGeneralLedgerAccount;
import org.kuali.kfs.module.endow.businessobject.PooledFundControl;
import org.kuali.kfs.module.endow.businessobject.PooledFundValue;
import org.kuali.kfs.module.endow.businessobject.RegistrationCode;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;
import org.kuali.kfs.module.endow.document.HoldingAdjustmentDocument;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.fixture.ClassCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionCodeFixture;
import org.kuali.kfs.module.endow.fixture.GLLinkFixture;
import org.kuali.kfs.module.endow.fixture.HoldingTaxLotFixture;
import org.kuali.kfs.module.endow.fixture.HoldingTaxLotRebalanceFixture;
import org.kuali.kfs.module.endow.fixture.KemIdFixture;
import org.kuali.kfs.module.endow.fixture.KemidGeneralLedgerAccountFixture;
import org.kuali.kfs.module.endow.fixture.PooledFundControlTransactionFixture;
import org.kuali.kfs.module.endow.fixture.PooledFundValueFixture;
import org.kuali.kfs.module.endow.fixture.RegistrationCodeFixture;
import org.kuali.kfs.module.endow.fixture.SecurityFixture;
import org.kuali.kfs.module.endow.fixture.SecurityReportingGroupFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

@ConfigureContext(session = kfs, shouldCommitTransactions=true)
public class CreateCapitalGainLossDistributionTransactionServiceImplTest extends KualiTestBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreateCapitalGainLossDistributionTransactionServiceImplTest.class);

    private Security security;
    private HoldingTaxLotService holdingTaxLotService;
    private CreateGainLossDistributionTransactionsServiceImpl createGainLossDistributionTransactionsServiceImpl;
    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private PooledFundValue pooledFundValue;
    private PooledFundControl pooledFundControl;
    private ParameterService parameterService;
    private HoldingTaxLotRebalance holdingTaxLotRebalance;
    private HoldingTaxLot holdingTaxLot;
    private EndowmentTransactionCode endowmentTransactionCode1;
    private EndowmentTransactionCode endowmentTransactionCode2;
    private KemidGeneralLedgerAccount kemidGeneralLedgerAccount;
    private GLLink gLLink;
    private ClassCode classCode;
    private EndowmentTransactionCode endowmentTransactionCode;
    private SecurityReportingGroup reportingGroup;
    private KEMID kemid;
    private RegistrationCode registrationCode;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        dateTimeService = SpringContext.getBean(DateTimeService.class);
        parameterService = SpringContext.getBean(ParameterService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        holdingTaxLotService = SpringContext.getBean(HoldingTaxLotService.class);
        createGainLossDistributionTransactionsServiceImpl = (CreateGainLossDistributionTransactionsServiceImpl) TestUtils.getUnproxiedService("mockCreateGainLossTransactionsService");

        createDataFixtures();        
    }

    @Override
    protected void tearDown() throws Exception {
        removeDataFixtures();
        
        super.tearDown();
    }

    /**
     * method to setup the required business objects to test the methods.
     */
    
    private void createDataFixtures() {

        // setup dummy data
        registrationCode = RegistrationCodeFixture.REGISTRATION_CODE_RECORD_COMMITTED.createRegistrationCode();
        kemid = KemIdFixture.ALLOW_TRAN_KEMID_RECORD_COMMITTED.createKemidRecord();
        reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP_COMMITTED.createSecurityReportingGroup();
        endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE_COMMITTED.createEndowmentTransactionCode();
        classCode = ClassCodeFixture.CAPITAL_GAIN_LOSS_COMMITTED.createClassCodeRecord();
        security = SecurityFixture.ACTIVE_SECURITY_COMMITTED.createSecurityRecord();
        security.setClassCode(classCode);
        security.setUnitsHeld(BigDecimal.valueOf(1000));
        //save the security with changes made..
        businessObjectService.save(security);
        
        pooledFundControl = PooledFundControlTransactionFixture.CAPITAL_GAIN_LOSS_DISTRIBUTION_TRANSACTION_COMMITTED.createPooledFundControl();
        businessObjectService.save(pooledFundControl);
        
        pooledFundValue = PooledFundValueFixture.CAPITAL_GAIN_LOSS_DISTRIBUTION_TRANSACTION_COMMITTED.createPooledFundValue();
        pooledFundValue.refreshNonUpdateableReferences();
        
        // need to insert into END_HLDG_TAX_LOT_REBAL_T TABLE because of constraints....
        holdingTaxLotRebalance = HoldingTaxLotRebalanceFixture.HOLDING_TAX_LOT_REBALANCE_RECORD_FOR_GAIN_LOSS_COMMITTED.createHoldingTaxLotRebalanceRecord();

        holdingTaxLot = HoldingTaxLotFixture.HOLDING_TAX_LOT_RECORD_FOR_GAIN_LOSS_COMMITTED.createHoldingTaxLotRecord();
        kemidGeneralLedgerAccount = KemidGeneralLedgerAccountFixture.KEMID_GL_ACCOUNT_FOR_GAIN_LOSS_TRANSACTIONS_COMMITTED.createKemidGeneralLedgerAccount();
        
        endowmentTransactionCode1 = EndowmentTransactionCodeFixture.EXPENSE_TRANSACTION_CODE_COMMITTED.createEndowmentTransactionCode();
        gLLink = GLLinkFixture.GL_LINK_BL_CHART_COMMITTED.createGLLink();
    }

    /**
     * method to remove the added business objects.
     */
    private void removeDataFixtures() {

        // remove dummy data
        businessObjectService.delete(gLLink);
        businessObjectService.delete(endowmentTransactionCode1);
        businessObjectService.delete(kemidGeneralLedgerAccount);
        businessObjectService.delete(holdingTaxLot);
        businessObjectService.delete(holdingTaxLotRebalance);
        businessObjectService.delete(pooledFundValue);        
        businessObjectService.delete(pooledFundControl);
        
        businessObjectService.delete(security);
        businessObjectService.delete(classCode);
        businessObjectService.delete(endowmentTransactionCode);
        businessObjectService.delete(reportingGroup);
        businessObjectService.delete(kemid);        
        businessObjectService.delete(registrationCode);
    }
    
    /**
     * method to test groupTaxLotsByRegistrationCode() in the impl class
     * @see org.kuali.kfs.module.endow.batch.service.impl.CreateGainLossDistributionTransactionsServiceImpl#groupTaxLotsByRegistrationCode(List)
     */
    public void testGroupTaxLotsByRegistrationCode() {
        LOG.info("method testGroupTaxLotsByRegistrationCode() entered.");
        
        List<HoldingTaxLot> taxLots = SpringContext.getBean(HoldingTaxLotService.class).getTaxLotsPerSecurityIDWithUnitsGreaterThanZero(pooledFundControl.getPooledSecurityID());
        
        Map<String, List<HoldingTaxLot>> regCodeMap = createGainLossDistributionTransactionsServiceImpl.groupTaxLotsByRegistrationCode(taxLots);
        for (String registrationCode : regCodeMap.keySet()) {
            assertTrue("Unable to create group tax lots based on registration code.", EndowTestConstants.TEST_REGISTRATION_CD_COMMITTED.equalsIgnoreCase(registrationCode));
        }
        
        LOG.info("method testGroupTaxLotsByRegistrationCode() exited.");        
    }
    
    /**
     * test method generateHoldingAdjustmentDocument() in the impl class
     * @see org.kuali.kfs.module.endow.batch.service.impl.CreateGainLossDistributionTransactionsServiceImpl#generateHoldingAdjustmentDocument(boolean, String)
     */
    public void testGenerateHoldingAdjustmentDocument() {
        LOG.info("method testGenerateHoldingAdjustmentDocument() entered.");
        
        createGainLossDistributionTransactionsServiceImpl.initializeReportLines("test", pooledFundValue.getPooledSecurityID());
        
        //short term
        HoldingAdjustmentDocument had1 = createGainLossDistributionTransactionsServiceImpl.generateHoldingAdjustmentDocument(true, pooledFundValue.getPooledSecurityID());
        if (ObjectUtils.isNotNull(had1)) {
            assertTrue("The description for Short Term does not match.", had1.getDocumentHeader().getDocumentDescription().equals(parameterService.getParameterValueAsString(CreateGainLossDistributionTransactionsStep.class, EndowParameterKeyConstants.SHORT_TERM_GAIN_LOSS_DESCRIPTION)));
        }
        removeDocumentFromTables(had1.getDocumentNumber());
        
        //long term
        HoldingAdjustmentDocument had2 = createGainLossDistributionTransactionsServiceImpl.generateHoldingAdjustmentDocument(false, pooledFundValue.getPooledSecurityID());
        if (ObjectUtils.isNotNull(had2)) {
            assertTrue("The description for Short Term does not match.", had2.getDocumentHeader().getDocumentDescription().equals(parameterService.getParameterValueAsString(CreateGainLossDistributionTransactionsStep.class, EndowParameterKeyConstants.LONG_TERM_GAIN_LOSS_DESCRIPTION)));
        }   
        removeDocumentFromTables(had2.getDocumentNumber());
        
        LOG.info("method testGenerateHoldingAdjustmentDocument() exited.");        
    }
    
    /**
     * test method addSecurityDetails() in the impl class
     * @see org.kuali.kfs.module.endow.batch.service.impl.CreateGainLossDistributionTransactionsServiceImpl#addSecurityDetails(HoldingAdjustmentDocument, String, String)
     */
    public void testAddSecurityDetails() {
        LOG.info("method testAddSecurityDetails() entered.");
        
        createGainLossDistributionTransactionsServiceImpl.initializeReportLines("test", pooledFundValue.getPooledSecurityID());
        
        HoldingAdjustmentDocument had1 = createGainLossDistributionTransactionsServiceImpl.generateHoldingAdjustmentDocument(true, pooledFundValue.getPooledSecurityID());
        if (ObjectUtils.isNotNull(had1)) {
            assertTrue("The description for Short Term does not match.", had1.getDocumentHeader().getDocumentDescription().equals(parameterService.getParameterValueAsString(CreateGainLossDistributionTransactionsStep.class, EndowParameterKeyConstants.SHORT_TERM_GAIN_LOSS_DESCRIPTION)));
        }
        createGainLossDistributionTransactionsServiceImpl.addSecurityDetails(had1, pooledFundValue.getPooledSecurityID(), holdingTaxLot.getRegistrationCode());
        assertTrue("Problem adding security details.", had1.getSourceTransactionSecurity().getRegistrationCode().equalsIgnoreCase(EndowTestConstants.TEST_REGISTRATION_CD_COMMITTED));
        
        removeDocumentFromTables(had1.getDocumentNumber());
        
        LOG.info("method testAddSecurityDetails() exited.");        
    }
    
    /**
     * test method testAddTransactionLine_ShortTerm_Loss_ZeroAmount.
     * The addTransactionLine method should skip the complete method and just return false.
     * @see org.kuali.kfs.module.endow.batch.service.impl.CreateGainLossDistributionTransactionsServiceImpl#addTransactionLine(boolean, HoldingAdjustmentDocument, HoldingTaxLot, PooledFundValue)
     */
    public void testAddTransactionLine_ShortTerm_Loss_ZeroAmount() {
        LOG.info("method testAddTransactionLine_ShortTerm_Loss_ZeroAmount() entered.");
        
        //short term case 1 = isLoss = true
        pooledFundValue.setShortTermGainLossDistributionPerUnit(BigDecimal.ZERO);
        
        HoldingAdjustmentDocument had = createGainLossDistributionTransactionsServiceImpl.generateHoldingAdjustmentDocument(true, pooledFundValue.getPooledSecurityID());
        createGainLossDistributionTransactionsServiceImpl.addSecurityDetails(had, pooledFundValue.getPooledSecurityID(), holdingTaxLot.getRegistrationCode());

        boolean rulePassed = createGainLossDistributionTransactionsServiceImpl.addTransactionLine(true, had, holdingTaxLot, pooledFundValue);
        assertFalse("The add transaction line should have been skipped.", rulePassed);
        
        removeDocumentFromTables(had.getDocumentNumber());
        
        LOG.info("method testAddTransactionLine_ShortTerm_Loss_ZeroAmount() exited.");        
    }
    
    /**
     * test method testAddTransactionLine_ShortTerm_Loss_NonZeroAmount to create holdinghistoryvalueadjustment
     * document for short term loss.  The value from pooled fund value shortTermGainLossDistributionPerUnit
     * will be negated and added to the transaction(source) line and added to the document.
     * The document will also be run again the business validation rules for add event.
     * @see org.kuali.kfs.module.endow.batch.service.impl.CreateGainLossDistributionTransactionsServiceImpl#addTransactionLine(boolean, HoldingAdjustmentDocument, HoldingTaxLot, PooledFundValue)
     */
    public void testAddTransactionLine_ShortTerm_Loss_NonZeroAmount() {
        LOG.info("method testAddTransactionLine_ShortTerm_Loss_NonZeroAmount() entered.");
        
        createGainLossDistributionTransactionsServiceImpl.initializeReportLines("test", pooledFundValue.getPooledSecurityID());
        
        BigDecimal unitAmount = BigDecimal.valueOf(1000.89);
        
        //short term case 1 = isLoss = true
        pooledFundValue.setShortTermGainLossDistributionPerUnit(unitAmount.negate());
        
        HoldingAdjustmentDocument had = createGainLossDistributionTransactionsServiceImpl.generateHoldingAdjustmentDocument(true, pooledFundValue.getPooledSecurityID());
        createGainLossDistributionTransactionsServiceImpl.addSecurityDetails(had, pooledFundValue.getPooledSecurityID(), holdingTaxLot.getRegistrationCode());

        boolean rulePassed = createGainLossDistributionTransactionsServiceImpl.addTransactionLine(true, had, holdingTaxLot, pooledFundValue);
        assertTrue("Business Rules did not pass.", rulePassed);

        if (rulePassed) {
            assertTrue(had.getSourceTransactionLine(0).getUnitAdjustmentAmount().compareTo(unitAmount)== 0);
        }
        
        removeDocumentFromTables(had.getDocumentNumber());
        
        LOG.info("method testAddTransactionLine_ShortTerm_Loss_NonZeroAmount() exited.");        
    }

    /**
     * test method testAddTransactionLine_ShortTerm_Gain_ZeroAmount
     * The addTransactionLine method should skip the complete method and just return false.
     * @see org.kuali.kfs.module.endow.batch.service.impl.CreateGainLossDistributionTransactionsServiceImpl#addTransactionLine(boolean, HoldingAdjustmentDocument, HoldingTaxLot, PooledFundValue)
     */
    public void testAddTransactionLine_ShortTerm_Gain_ZeroAmount() {
        LOG.info("method testAddTransactionLine_ShortTerm_Gain_ZeroAmount() entered.");
        
        pooledFundValue.setShortTermGainLossDistributionPerUnit(BigDecimal.ZERO);
        
        //short term case 2 and gain
        HoldingAdjustmentDocument had = createGainLossDistributionTransactionsServiceImpl.generateHoldingAdjustmentDocument(true, pooledFundValue.getPooledSecurityID());
        createGainLossDistributionTransactionsServiceImpl.addSecurityDetails(had, pooledFundValue.getPooledSecurityID(), holdingTaxLot.getRegistrationCode());

        boolean rulePassed = createGainLossDistributionTransactionsServiceImpl.addTransactionLine(true, had, holdingTaxLot, pooledFundValue);
        assertFalse("The add transaction line should have been skipped.", rulePassed);
        
        removeDocumentFromTables(had.getDocumentNumber());
        
        LOG.info("method testAddTransactionLine_ShortTerm_Gain_ZeroAmount() exited.");        
    }
    
    /**
     * test method testAddTransactionLine_ShortTerm_Gain_NonZeroAmount to create holdinghistoryvalueadjustment
     * document for short term loss.  The value from pooled fund value shortTermGainLossDistributionPerUnit
     * will be negated and added to the transaction(source) line and added to the document.
     * The document will also be run again the business validation rules for add event.
     * @see org.kuali.kfs.module.endow.batch.service.impl.CreateGainLossDistributionTransactionsServiceImpl#addTransactionLine(boolean, HoldingAdjustmentDocument, HoldingTaxLot, PooledFundValue)
     */
    public void testAddTransactionLine_ShortTerm_Gain_NonZeroAmount() {
        LOG.info("method testAddTransactionLine_ShortTerm_Gain_NonZeroAmount() entered.");
        
        createGainLossDistributionTransactionsServiceImpl.initializeReportLines("test", pooledFundValue.getPooledSecurityID());
        
        BigDecimal unitAmount = BigDecimal.valueOf(2000.12);
        
        //short term case 1 = isLoss = true
        pooledFundValue.setShortTermGainLossDistributionPerUnit(unitAmount);
        
        HoldingAdjustmentDocument had = createGainLossDistributionTransactionsServiceImpl.generateHoldingAdjustmentDocument(true, pooledFundValue.getPooledSecurityID());
        createGainLossDistributionTransactionsServiceImpl.addSecurityDetails(had, pooledFundValue.getPooledSecurityID(), holdingTaxLot.getRegistrationCode());

        boolean rulePassed = createGainLossDistributionTransactionsServiceImpl.addTransactionLine(true, had, holdingTaxLot, pooledFundValue);
        assertTrue("Business Rules did not pass.", rulePassed);

        if (rulePassed) {
            assertTrue(had.getTargetTransactionLine(0).getUnitAdjustmentAmount().compareTo(unitAmount)== 0);
        }
        
        removeDocumentFromTables(had.getDocumentNumber());
        
        LOG.info("method testAddTransactionLine_ShortTerm_Gain_NonZeroAmount() exited.");        
    }
    
    /**
     * test method testAddTransactionLine_LongTerm_Loss_ZeroAmount.
     * The addTransactionLine method should skip the complete method and just return false.
     * @see org.kuali.kfs.module.endow.batch.service.impl.CreateGainLossDistributionTransactionsServiceImpl#addTransactionLine(boolean, HoldingAdjustmentDocument, HoldingTaxLot, PooledFundValue)
     */
    public void testAddTransactionLine_LongTerm_Loss_ZeroAmount() {
        LOG.info("method testAddTransactionLine_LongTerm_Loss_ZeroAmount() entered.");
        
        //short term case 1 = isLoss = true
        pooledFundValue.setLongTermGainLossDistributionPerUnit(BigDecimal.ZERO);
        
        HoldingAdjustmentDocument had = createGainLossDistributionTransactionsServiceImpl.generateHoldingAdjustmentDocument(false, pooledFundValue.getPooledSecurityID());
        createGainLossDistributionTransactionsServiceImpl.addSecurityDetails(had, pooledFundValue.getPooledSecurityID(), holdingTaxLot.getRegistrationCode());

        boolean rulePassed = createGainLossDistributionTransactionsServiceImpl.addTransactionLine(false, had, holdingTaxLot, pooledFundValue);
        assertFalse("The add transaction line should have been skipped.", rulePassed);
        
        removeDocumentFromTables(had.getDocumentNumber());
        
        LOG.info("method testAddTransactionLine_LongTerm_Loss_ZeroAmount() exited.");        
    }
    
    /**
     * test method testAddTransactionLine_LongTerm_Loss_NonZeroAmount to create holdinghistoryvalueadjustment
     * document for short term loss.  The value from pooled fund value shortTermGainLossDistributionPerUnit
     * will be negated and added to the transaction(source) line and added to the document.
     * The document will also be run again the business validation rules for add event.
     * @see org.kuali.kfs.module.endow.batch.service.impl.CreateGainLossDistributionTransactionsServiceImpl#addTransactionLine(boolean, HoldingAdjustmentDocument, HoldingTaxLot, PooledFundValue)
     */
    public void testAddTransactionLine_LongTerm_Loss_NonZeroAmount() {
        LOG.info("method testAddTransactionLine_LongTerm_Loss_NonZeroAmount() entered.");
        
        createGainLossDistributionTransactionsServiceImpl.initializeReportLines("test", pooledFundValue.getPooledSecurityID());
        
        BigDecimal unitAmount = BigDecimal.valueOf(1000.89);
        
        //short term case 1 = isLoss = true
        pooledFundValue.setLongTermGainLossDistributionPerUnit(unitAmount.negate());
        
        HoldingAdjustmentDocument had = createGainLossDistributionTransactionsServiceImpl.generateHoldingAdjustmentDocument(false, pooledFundValue.getPooledSecurityID());
        createGainLossDistributionTransactionsServiceImpl.addSecurityDetails(had, pooledFundValue.getPooledSecurityID(), holdingTaxLot.getRegistrationCode());

        boolean rulePassed = createGainLossDistributionTransactionsServiceImpl.addTransactionLine(false, had, holdingTaxLot, pooledFundValue);
        assertTrue("Business Rules did not pass.", rulePassed);

        if (rulePassed) {
            assertTrue(had.getSourceTransactionLine(0).getUnitAdjustmentAmount().compareTo(unitAmount)== 0);
        }
        
        removeDocumentFromTables(had.getDocumentNumber());
        
        LOG.info("method testAddTransactionLine_LongTerm_Loss_NonZeroAmount() exited.");        
    }

    /**
     * test method testAddTransactionLine_LongTerm_Gain_ZeroAmount
     * The addTransactionLine method should skip the complete method and just return false.
     * @see org.kuali.kfs.module.endow.batch.service.impl.CreateGainLossDistributionTransactionsServiceImpl#addTransactionLine(boolean, HoldingAdjustmentDocument, HoldingTaxLot, PooledFundValue)
     */
    public void testAddTransactionLine_LongTerm_Gain_ZeroAmount() {
        LOG.info("method testAddTransactionLine_LongTerm_Gain_ZeroAmount() entered.");
        
        pooledFundValue.setLongTermGainLossDistributionPerUnit(BigDecimal.ZERO);
        
        //Long term case 2 and zero amount
        HoldingAdjustmentDocument had = createGainLossDistributionTransactionsServiceImpl.generateHoldingAdjustmentDocument(false, pooledFundValue.getPooledSecurityID());
        createGainLossDistributionTransactionsServiceImpl.addSecurityDetails(had, pooledFundValue.getPooledSecurityID(), holdingTaxLot.getRegistrationCode());

        boolean rulePassed = createGainLossDistributionTransactionsServiceImpl.addTransactionLine(false, had, holdingTaxLot, pooledFundValue);
        assertFalse("The add transaction line should have been skipped.", rulePassed);
        
        removeDocumentFromTables(had.getDocumentNumber());
        
        LOG.info("method testAddTransactionLine_LongTerm_Gain_ZeroAmount() exited.");        
    }
    
    /**
     * test method testAddTransactionLine_LongTerm_Gain_NonZeroAmount to create holdinghistoryvalueadjustment
     * document for short term loss.  The value from pooled fund value shortTermGainLossDistributionPerUnit
     * will be negated and added to the transaction(source) line and added to the document.
     * The document will also be run again the business validation rules for add event.
     * @see org.kuali.kfs.module.endow.batch.service.impl.CreateGainLossDistributionTransactionsServiceImpl#addTransactionLine(boolean, HoldingAdjustmentDocument, HoldingTaxLot, PooledFundValue)
     */
    public void testAddTransactionLine_LongTerm_Gain_NonZeroAmount() {
        LOG.info("method testAddTransactionLine_LongTerm_Gain_NonZeroAmount() entered.");
        
        createGainLossDistributionTransactionsServiceImpl.initializeReportLines("test", pooledFundValue.getPooledSecurityID());
        
        BigDecimal unitAmount = BigDecimal.valueOf(2000.12);
        
        //short term case 1 = isLoss = true
        pooledFundValue.setLongTermGainLossDistributionPerUnit(unitAmount);
        
        HoldingAdjustmentDocument had = createGainLossDistributionTransactionsServiceImpl.generateHoldingAdjustmentDocument(false, pooledFundValue.getPooledSecurityID());
        createGainLossDistributionTransactionsServiceImpl.addSecurityDetails(had, pooledFundValue.getPooledSecurityID(), holdingTaxLot.getRegistrationCode());

        boolean rulePassed = createGainLossDistributionTransactionsServiceImpl.addTransactionLine(false, had, holdingTaxLot, pooledFundValue);
        assertTrue("Business Rules did not pass.", rulePassed);

        if (rulePassed) {
            assertTrue(had.getTargetTransactionLine(0).getUnitAdjustmentAmount().compareTo(unitAmount)== 0);
        }
        
        removeDocumentFromTables(had.getDocumentNumber());
        
        LOG.info("method testAddTransactionLine_LongTerm_Gain_NonZeroAmount() exited.");        
    }
    
    /**
     * test method validateAndRouteHoldingAdjustmentDocument in the impl class
     * @see org.kuali.kfs.module.endow.batch.service.impl.CreateGainLossDistributionTransactionsServiceImpl#validateAndRouteHoldingAdjustmentDocument(HoldingAdjustmentDocument, PooledFundValue, boolean)
     */
    public void testValidateAndRouteHoldingAdjustmentDocument() throws Exception {
        LOG.info("method testValidateAndRouteHoldingAdjustmentDocument() entered.");     

        createGainLossDistributionTransactionsServiceImpl.initializeReportLines("test", pooledFundValue.getPooledSecurityID());

        HoldingAdjustmentDocument had4 = createGainLossDistributionTransactionsServiceImpl.generateHoldingAdjustmentDocument(true, pooledFundValue.getPooledSecurityID());
        createGainLossDistributionTransactionsServiceImpl.addSecurityDetails(had4, pooledFundValue.getPooledSecurityID(), holdingTaxLot.getRegistrationCode());
        boolean rulePassed = createGainLossDistributionTransactionsServiceImpl.addTransactionLine(true, had4, holdingTaxLot, pooledFundValue);
        createGainLossDistributionTransactionsServiceImpl.validateAndRouteHoldingAdjustmentDocument(had4, pooledFundValue, true);

        assertTrue("Routing did not take place.  Exception occured.", pooledFundValue.isShortTermGainLossDistributionComplete());
        
      //remove the added document, security info, and transaction and tax lot lines..
        removeDocumentFromTables(had4.getDocumentNumber());
        
        LOG.info("method testValidateAndRouteHoldingAdjustmentDocument() exited.");        
    }
    
    /**
     * private method to remove the added security, transaction and workflow docuemnts
     * 
     */
    private void removeDocumentFromTables(String documentNumber) {
        Map arg1 = new HashMap();
        arg1.put("documentNumber", documentNumber);
        
        //delete from EndowmentTransactionTaxLotLine
        businessObjectService.deleteMatching(EndowmentTransactionTaxLotLine.class, arg1);
        
        //delete from END_TRAN_SEC_T
        businessObjectService.deleteMatching(EndowmentTransactionSecurity.class, arg1);
        
        //delete from EndowmentTransactionLineBase
        businessObjectService.deleteMatching(EndowmentTransactionLineBase.class, arg1);
        
        //finally delete the document HoldingAdjustmentDocument
        businessObjectService.deleteMatching(HoldingAdjustmentDocument.class, arg1);
    }
    
}
