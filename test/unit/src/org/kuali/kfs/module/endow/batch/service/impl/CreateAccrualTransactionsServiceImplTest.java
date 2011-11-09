/*
 * Copyright 2008-2009 The Kuali Foundation
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

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowTestConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.GLLink;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.KemidGeneralLedgerAccount;
import org.kuali.kfs.module.endow.businessobject.RegistrationCode;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;
import org.kuali.kfs.module.endow.document.CashIncreaseDocument;
import org.kuali.kfs.module.endow.document.service.HoldingTaxLotService;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.validation.impl.CashIncreaseDocumentRuleValidationsForBatchProcess;
import org.kuali.kfs.module.endow.fixture.ClassCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionLineFixture;
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
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;


@ConfigureContext(session = kfs)
public class CreateAccrualTransactionsServiceImplTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreateAccrualTransactionsServiceImplTest.class);

    private CreateAccrualTransactionsServiceImpl createAccrualTransactionsServiceImpl;    
    private BusinessObjectService businessObjectService;
    private KEMService kemService;
    
    private ClassCode classCode;
    private Security security;
    private SecurityReportingGroup securityReportingGroup;
    private EndowmentTransactionCode endowmentTransactionCode;
    private HoldingTaxLot holdingTaxLot;
    private KEMID kemid;
    private RegistrationCode registrationCode;
    private CashIncreaseDocument doc;
    private EndowmentTransactionCode endowmentTransactionCode1;
    private EndowmentTransactionCode endowmentTransactionCode2;
    private EndowmentTransactionCode endowmentTransactionCode3;
    private KemidGeneralLedgerAccount kemidGeneralLedgerAccount;
    private GLLink gLLink;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        createAccrualTransactionsServiceImpl = (CreateAccrualTransactionsServiceImpl) TestUtils.getUnproxiedService("mockCreateAccrualTransactionsService");
        kemService = SpringContext.getBean(KEMService.class);
        
        endowmentTransactionCode1 = EndowmentTransactionCodeFixture.EXPENSE_TRANSACTION_CODE.createEndowmentTransactionCode();
        endowmentTransactionCode2 = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        gLLink = GLLinkFixture.GL_LINK_BL_CHART.createGLLink();
        
        securityReportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        classCode = ClassCodeFixture.ACCRUAL_PROCESSING_CLASS_CODE.createClassCodeRecord();
        
        changeNextPayDateAllSecurityRecords(); //change the date
        //add a new one....so that GetAllSecuritiesWithNextPayDateEqualCurrentDate will find the record...
        security = SecurityFixture.ALTERNATIVE_INVEST_ACTIVE_SECURITY.createSecurityRecord();
        security.setIncomeNextPayDate(kemService.getCurrentDate());
        businessObjectService.save(security);
        
        kemid = KemIdFixture.CLOSED_KEMID_RECORD.createKemidRecord();
        kemidGeneralLedgerAccount = KemidGeneralLedgerAccountFixture.KEMID_GL_ACCOUNT.createKemidGeneralLedgerAccount();
        
        registrationCode = RegistrationCodeFixture.REGISTRATION_CODE_RECORD_FOR_LIABILITY.createRegistrationCode();
        HoldingTaxLotRebalanceFixture.HOLDING_TAX_LOT_REBALANCE_RECORD_FOR_CREATE_ACCRUAL_TRANSACTIONS.createHoldingTaxLotRebalanceRecord();
        holdingTaxLot = HoldingTaxLotFixture.HOLDING_TAX_LOT_RECORD_FOR_CREATE_ACCRUAL_TRANSACTIONS.createHoldingTaxLotRecord();

        //create the document with target transaction security added to it.
        doc = createCashIncreaseDocument();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    private CashIncreaseDocument createCashIncreaseDocument() {
        List<HoldingTaxLot> taxLots = SpringContext.getBean(HoldingTaxLotService.class).getAllTaxLotsWithAccruedIncomeGreaterThanZeroPerSecurity(security.getId());

        String registrationCode = "";
        for (HoldingTaxLot holdingTaxLot : taxLots) {
            registrationCode = holdingTaxLot.getRegistrationCode();
        }
        
        security.refreshNonUpdateableReferences();
        
        CashIncreaseDocument doc = createAccrualTransactionsServiceImpl.createNewCashIncreaseDocument(security.getId(), registrationCode);

        // Create a new transaction line
        EndowmentTargetTransactionLine line = (EndowmentTargetTransactionLine) EndowmentTransactionLineFixture.ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD.createEndowmentTransactionLine(false);
        line.setDocumentNumber(doc.getDocumentNumber());
        line.setKemid(kemid.getKemid());
        line.setEtranCode(security.getClassCode().getSecurityIncomeEndowmentTransactionPostCode());
        line.setTransactionIPIndicatorCode(EndowConstants.IncomePrincipalIndicator.INCOME);
        
        doc.getTargetTransactionLines().add(line);
        return doc;
    }
    
    private void changeNextPayDateAllSecurityRecords() {
        LOG.info("method changeNextPayDateAllSecurityRecords() entered.");
        
        Collection<Security> allSecurities = businessObjectService.findAll(Security.class);
        Date incomeNextPayDate = null;
        
        for (Security security : allSecurities) {
            security.setIncomeNextPayDate(incomeNextPayDate);
            businessObjectService.save(security);
        }

        LOG.info("method changeNextPayDateAllSecurityRecords() exited.");
    }
    
    /**
     * test to check the method getAllSecuritiesWithNextPayDateEqualCurrentDate() returning just
     * one record that we inserted in the setup method
     */
    public void testGetAllSecuritiesWithNextPayDateEqualCurrentDate() {
        LOG.info("method testGetAllSecuritiesWithNextPayDateEqualCurrentDate() entered.");
        
        List<Security> result = createAccrualTransactionsServiceImpl.getAllSecuritiesWithNextPayDateEqualCurrentDate();
        assertTrue("There should be just one record in the Security table.", result.size() == 1);

        LOG.info("method testGetAllSecuritiesWithNextPayDateEqualCurrentDate() exited.");        
    }
    
    /**
     * @see org.kuali.kfs.module.endow.batch.service.impl.CreateAccrualTransactionsServiceImpl#groupTaxLotsBasedOnRegistrationCode(List)
     */
    public void testGroupTaxLotsBasedOnRegistrationCode() {
        LOG.info("method testGroupTaxLotsBasedOnRegistrationCode() entered.");
        
        List<HoldingTaxLot> taxLots = SpringContext.getBean(HoldingTaxLotService.class).getAllTaxLotsWithAccruedIncomeGreaterThanZeroPerSecurity(security.getId());
        
        Map<String, List<HoldingTaxLot>> regCodeMap = createAccrualTransactionsServiceImpl.groupTaxLotsBasedOnRegistrationCode(taxLots);
        
        for (String registrationCode : regCodeMap.keySet()) {
            assertTrue("Unable to create group tax lots based on registration code.", EndowTestConstants.TEST_REGISTRATION_CD.equalsIgnoreCase(registrationCode));
        }

        LOG.info("method testGroupTaxLotsBasedOnRegistrationCode() exited.");        
    }

    /**
     * @see org.kuali.kfs.module.endow.batch.service.impl.CreateAccrualTransactionsServiceImpl#groupTaxLotsBasedOnKemidAndIPIndicator(List)
     */
    public void testGroupTaxLotsBasedOnKemidAndIPIndicator() {
        LOG.info("method testGroupTaxLotsBasedOnKemidAndIPIndicator() entered.");
        
        List<HoldingTaxLot> taxLots = SpringContext.getBean(HoldingTaxLotService.class).getAllTaxLotsWithAccruedIncomeGreaterThanZeroPerSecurity(security.getId());
        String kemidAndIp = "";
        for (HoldingTaxLot holdingTaxLot : taxLots) {
            kemidAndIp = holdingTaxLot.getKemid() + holdingTaxLot.getIncomePrincipalIndicator();
        }
        
        Map<String, List<HoldingTaxLot>> kemidIpMap = createAccrualTransactionsServiceImpl.groupTaxLotsBasedOnKemidAndIPIndicator(taxLots);
        for (String kemidIp : kemidIpMap.keySet()) {
            assertTrue("Kemid and IP in the key set do not match with holding tax record.", kemidAndIp.equalsIgnoreCase(kemidIp));
        }
            
        LOG.info("method testGroupTaxLotsBasedOnKemidAndIPIndicator() exited.");        
    }
    
    /**
     * test createNewCashIncreaseDocument() method
     */
    public void testCreateNewCashIncreaseDocument() {
        LOG.info("method testCreateNewCashIncreaseDocument() entered.");
        
        List<HoldingTaxLot> taxLots = SpringContext.getBean(HoldingTaxLotService.class).getAllTaxLotsWithAccruedIncomeGreaterThanZeroPerSecurity(security.getId());

        String registrationCode = "";
        for (HoldingTaxLot holdingTaxLot : taxLots) {
            registrationCode = holdingTaxLot.getRegistrationCode();
        }
        
        if (ObjectUtils.isNotNull(doc)) {
            assertTrue("Unable to create the cash increase docuemnt correctly.", doc.getTargetTransactionSecurity().getSecurityID().equalsIgnoreCase(security.getId()));
        }
        
        LOG.info("method testCreateNewCashIncreaseDocument() exited.");        
    }
    
    /**
     * test method addTransactionLine() in the impl class to make sure that
     * a transaction line can be added to the cash increase document....
     */
    public void testAddTransactionLine() throws Exception {
        LOG.info("method testAddTransactionLine() entered.");
        
        if (doc.getTargetTransactionLine(0) != null) {
            boolean lineValid = checkvalidateCashTransactionLine(doc, doc.getTargetTransactionLine(0));
            assertTrue("The business rules failed.", lineValid);
        }
        
        LOG.info("method testAddTransactionLine() exited.");        
    }
    
    /*
     * The static method uses the methods in CashIncreaseDocumentRuleValidatiionsForBatchProcess
     * which basically calls individual methods in cash document base rule class to
     * validate validateCashTransactionLine method.  The reason we are doing this way so that
     * the method validateChartMatch() can be ignored.  This method will always fail in unit tests
     * since END_KEMID_T and END_KEMID_GL_LNK_T tables have an inverse key KEMID and collection
     * records for a given kemid in END_KEMID_GL_LNK_T will not be visible during validateChartMatch
     * method so instead of calling validateCashTransactionLine, we validate the 
     * individual methods to make sure validateCashTransactionLine passes.
     */
    private static boolean checkvalidateCashTransactionLine(CashIncreaseDocument cashDecreaseDocument, EndowmentTargetTransactionLine line) throws Exception {
        boolean isValid = true;

        CashIncreaseDocumentRuleValidationsForBatchProcess ruleTest = new CashIncreaseDocumentRuleValidationsForBatchProcess();

        return ruleTest.checkValidateCashTransactionLine(cashDecreaseDocument, line, 0);
    }
    
    /**
     * Sets the kemService.
     * @param kemService
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }
}
