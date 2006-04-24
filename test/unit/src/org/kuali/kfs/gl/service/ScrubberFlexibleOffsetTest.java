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
package org.kuali.module.gl.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.FinancialSystemParameter;
import org.kuali.core.document.DocumentType;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.financial.bo.OffsetAccount;
import org.kuali.module.financial.service.FlexibleOffsetAccountService;
import org.kuali.module.gl.OriginEntryTestBase;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.test.MockService;

/**
 * This class...
 * 
 * @author Bin Gao from Michigan State University
 */
public class ScrubberFlexibleOffsetTest extends OriginEntryTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberFlexibleOffsetTest.class);

    private BusinessObjectService businessObjectService;
    private FlexibleOffsetAccountService flexibleOffsetAccountService;
    private DocumentTypeService documentTypeService;
    private ScrubberService scrubberService;
    private KualiConfigurationService originalConfigService;

    public final Integer FISCAL_YEAR = new Integer(2004);
    public final String COA_CODE = "BL";
    public final String ACCOUNT_NUMBER = "1031400";

    public final String OFFSET_COA_CODE = "UA";
    public final String OFFSET_ACCOUNT_NUMBER = "1912201";
    public final String OFFSET_OBJECT_CODE = "8100";

    public final String OBJECT_CODE = "4190";
    public final String BALANCE_TYPE_CODE = "AC";
    public final String DOCUMENT_TYPE_CODE = "PCDO";
    public final KualiDecimal ENTRY_AMOUNT = new KualiDecimal(2000);

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        scrubberService = (ScrubberService) beanFactory.getBean("glScrubberService");
        persistenceService = (PersistenceService) beanFactory.getBean("persistenceService");

        businessObjectService = (BusinessObjectService) beanFactory.getBean("businessObjectService");
        documentTypeService = (DocumentTypeService) beanFactory.getBean("documentTypeService");

        flexibleOffsetAccountService = (FlexibleOffsetAccountService) beanFactory.getBean("flexibleOffsetAccountService");
        //originalConfigService = flexibleOffsetAccountService.getKualiConfigurationService();

        // Get the test date time service so we can specify the date/time of the run
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.YEAR, 2006);
        date = c.getTime();
        dateTimeService.currentDate = date;
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        //flexibleOffsetAccountService.setKualiConfigurationService(originalConfigService);
        super.tearDown();
    }
    
    /**
     * test the primary scenario of flexible offset generation, that is, the given origin entry must have a flexible 
     * offset entry generated.
     * 
     * @throws Exception
     */
    public void testFlexibleOffsetGeneration() throws Exception {
        this.clearOriginEntryTables();
        OriginEntryGroup group = this.createNewGroup(OriginEntrySource.EXTERNAL);
        
        // reset the preconditions of flexible offset generation so that they have the vaild values 
        // flexibleOffsetAccountService.setKualiConfigurationService(createMockConfigurationService(true));
        resetFlexibleOffsetEnableFlag(true);
        resetScrubberGenerationIndicator(true, DOCUMENT_TYPE_CODE);
        resetAnOffsetAccount(COA_CODE, ACCOUNT_NUMBER, OFFSET_OBJECT_CODE);
        resetAnOffsetDefinition(FISCAL_YEAR, COA_CODE, DOCUMENT_TYPE_CODE, BALANCE_TYPE_CODE);

        // mock origin entry that is eligible for flexible offset generation
        OriginEntry entryEligibleForFlexibleOffset = this.getOriginEntryFromTemplate(group, OBJECT_CODE, BALANCE_TYPE_CODE,
                ENTRY_AMOUNT, DOCUMENT_TYPE_CODE, Constants.GL_DEBIT_CODE);
        originEntryDao.saveOriginEntry(entryEligibleForFlexibleOffset);

        // mock origin entry that is NOT eligible for flexible offset generation
        OriginEntry entryIneligibleForFlexibleOffset = this.getOriginEntryFromTemplate(group, "4021", BALANCE_TYPE_CODE,
                ENTRY_AMOUNT, "GEC", Constants.GL_DEBIT_CODE);
        originEntryDao.saveOriginEntry(entryIneligibleForFlexibleOffset);

        scrubberService.scrubEntries();

        // fetch the origin entry that is generated through flexible offset logic 
        OriginEntry expectedOriginEntry = this.getExpectedOriginEntryFromTemplate(group, OFFSET_COA_CODE, OFFSET_ACCOUNT_NUMBER,
                OFFSET_OBJECT_CODE, BALANCE_TYPE_CODE, ENTRY_AMOUNT, DOCUMENT_TYPE_CODE, Constants.GL_CREDIT_CODE);
        Map primaryKeyMap = this.populateOriginEntryPrimaryKey(expectedOriginEntry, false);
        
        // One and only one flexible offset record should be generated
        int numOfMatching = businessObjectService.countMatching(OriginEntry.class, primaryKeyMap);
        assertTrue("One and only one flexible offset record should be generated.", numOfMatching == 1);
    }
    
    /**
     * test the case when the global felxible offset enable flag is set to false. In this case, the offset generation
     * must not be executed, that is, there is no flexible offset entry generated. 
     * 
     * @throws Exception
     */
    public void testFlexibleOffsetEnableFlag() throws Exception {
        this.clearOriginEntryTables();
        OriginEntryGroup group = this.createNewGroup(OriginEntrySource.EXTERNAL);
        
        // disable the global flexible offset enable flag
        // flexibleOffsetAccountService.setKualiConfigurationService(createMockConfigurationService(false));
        resetFlexibleOffsetEnableFlag(false);
        
        // reset the preconditions of flexible offset generation so that they have the vaild values 
        resetScrubberGenerationIndicator(true, DOCUMENT_TYPE_CODE);
        resetAnOffsetAccount(COA_CODE, ACCOUNT_NUMBER, OFFSET_OBJECT_CODE);
        resetAnOffsetDefinition(FISCAL_YEAR, COA_CODE, DOCUMENT_TYPE_CODE, BALANCE_TYPE_CODE);

        // mock origin entry that is eligible for flexible offset generation
        OriginEntry entryEligibleForFlexibleOffset = this.getOriginEntryFromTemplate(group, OBJECT_CODE, BALANCE_TYPE_CODE,
                ENTRY_AMOUNT, DOCUMENT_TYPE_CODE, Constants.GL_DEBIT_CODE);
        originEntryDao.saveOriginEntry(entryEligibleForFlexibleOffset);

        scrubberService.scrubEntries();

        // fetch the origin entry that is generated through flexible offset logic 
        OriginEntry expectedOriginEntry = this.getExpectedOriginEntryFromTemplate(group, OFFSET_COA_CODE, OFFSET_ACCOUNT_NUMBER,
                OFFSET_OBJECT_CODE, BALANCE_TYPE_CODE, ENTRY_AMOUNT, DOCUMENT_TYPE_CODE, Constants.GL_CREDIT_CODE);
        Map primaryKeyMap = this.populateOriginEntryPrimaryKey(expectedOriginEntry, false);
               
        // No flexible offset record can be generated        
        int numOfMatching = businessObjectService.countMatching(OriginEntry.class, primaryKeyMap);
        assertTrue("No flexible offset record can be generated.", numOfMatching == 0);
    } 

    /**
     * test the case when the scrubber generation indicator is set to false. In this case, the offset generation
     * must not be executed, that is, there is no flexible offset entry generated. 
     * 
     * @throws Exception
     */
    public void testScrubberGenerationIndicator() throws Exception {
        this.clearOriginEntryTables();
        OriginEntryGroup group = this.createNewGroup(OriginEntrySource.EXTERNAL);
        
        // disable the scrubber generation indicator
        resetScrubberGenerationIndicator(false, DOCUMENT_TYPE_CODE);
        
        // reset the preconditions of flexible offset generation so that they have the vaild values 
        // flexibleOffsetAccountService.setKualiConfigurationService(createMockConfigurationService(true));
        resetFlexibleOffsetEnableFlag(true);
        
        resetAnOffsetAccount(COA_CODE, ACCOUNT_NUMBER, OFFSET_OBJECT_CODE);
        resetAnOffsetDefinition(FISCAL_YEAR, COA_CODE, DOCUMENT_TYPE_CODE, BALANCE_TYPE_CODE);

        // mock origin entry that is eligible for flexible offset generation
        OriginEntry entryEligibleForFlexibleOffset = this.getOriginEntryFromTemplate(group, OBJECT_CODE, BALANCE_TYPE_CODE,
                ENTRY_AMOUNT, DOCUMENT_TYPE_CODE, Constants.GL_DEBIT_CODE);
        originEntryDao.saveOriginEntry(entryEligibleForFlexibleOffset);

        scrubberService.scrubEntries();

        // fetch the origin entry that is generated through flexible offset logic 
        OriginEntry expectedOriginEntry = this.getExpectedOriginEntryFromTemplate(group, OFFSET_COA_CODE, OFFSET_ACCOUNT_NUMBER,
                OFFSET_OBJECT_CODE, BALANCE_TYPE_CODE, ENTRY_AMOUNT, DOCUMENT_TYPE_CODE, Constants.GL_CREDIT_CODE);
        Map primaryKeyMap = this.populateOriginEntryPrimaryKey(expectedOriginEntry, false);
               
        // No flexible offset record can be generated        
        int numOfMatching = businessObjectService.countMatching(OriginEntry.class, primaryKeyMap);
        assertTrue("No flexible offset record can be generated.", numOfMatching == 0);
    }
    
    /**
     * test the case when there is no corresponding offset account for the given origin entry. In this case, the 
     * offset generation must not be executed, that is, there is no flexible offset entry generated. 
     * 
     * @throws Exception
     */
    public void testOffsetAccount() throws Exception {
        this.clearOriginEntryTables();
        OriginEntryGroup group = this.createNewGroup(OriginEntrySource.EXTERNAL);
        
        // reset the preconditions of flexible offset generation so that they have the vaild values 
        // flexibleOffsetAccountService.setKualiConfigurationService(createMockConfigurationService(true));
        resetFlexibleOffsetEnableFlag(true);
        resetScrubberGenerationIndicator(true, DOCUMENT_TYPE_CODE);
        resetAnOffsetAccount(COA_CODE, ACCOUNT_NUMBER, OFFSET_OBJECT_CODE);
        resetAnOffsetDefinition(FISCAL_YEAR, COA_CODE, DOCUMENT_TYPE_CODE, BALANCE_TYPE_CODE);

        // mock origin entry that is eligible for flexible offset generation
        OriginEntry entryEligibleForFlexibleOffset = this.getOriginEntryFromTemplate(group, OBJECT_CODE, BALANCE_TYPE_CODE,
                ENTRY_AMOUNT, DOCUMENT_TYPE_CODE, Constants.GL_DEBIT_CODE);
        entryEligibleForFlexibleOffset.setAccountNumber("6044913");
        entryEligibleForFlexibleOffset.setChartOfAccountsCode("BA");
        originEntryDao.saveOriginEntry(entryEligibleForFlexibleOffset);

        scrubberService.scrubEntries();

        // fetch the origin entry that is generated through flexible offset logic 
        OriginEntry expectedOriginEntry = this.getExpectedOriginEntryFromTemplate(group, OFFSET_COA_CODE, OFFSET_ACCOUNT_NUMBER,
                OFFSET_OBJECT_CODE, BALANCE_TYPE_CODE, ENTRY_AMOUNT, DOCUMENT_TYPE_CODE, Constants.GL_CREDIT_CODE);
        Map primaryKeyMap = this.populateOriginEntryPrimaryKey(expectedOriginEntry, false);

        // No flexible offset record can be generated        
        int numOfMatching = businessObjectService.countMatching(OriginEntry.class, primaryKeyMap);
        assertTrue("No flexible offset record can be generated.", numOfMatching == 0);
    }        

    /**
     * test the case when there is no offset object available. In this case, the offset generation
     * must not be executed, that is, there is no flexible offset entry generated. 
     * 
     * @throws Exception
     */
    public void testOffsetDefinition() throws Exception {
        this.clearOriginEntryTables();
        OriginEntryGroup group = this.createNewGroup(OriginEntrySource.EXTERNAL);
        
        // reset the preconditions of flexible offset generation so that they have the vaild values 
        // flexibleOffsetAccountService.setKualiConfigurationService(createMockConfigurationService(true));
        resetFlexibleOffsetEnableFlag(true);
        resetScrubberGenerationIndicator(true, DOCUMENT_TYPE_CODE);
        resetAnOffsetAccount(COA_CODE, ACCOUNT_NUMBER, OFFSET_OBJECT_CODE);
        resetAnOffsetDefinition(FISCAL_YEAR, COA_CODE, DOCUMENT_TYPE_CODE, BALANCE_TYPE_CODE);

        // mock origin entry that is eligible for flexible offset generation
        OriginEntry entryEligibleForFlexibleOffset = this.getOriginEntryFromTemplate(group, OBJECT_CODE, BALANCE_TYPE_CODE,
                ENTRY_AMOUNT, "GEC", Constants.GL_DEBIT_CODE);
        originEntryDao.saveOriginEntry(entryEligibleForFlexibleOffset);

        scrubberService.scrubEntries();

        // fetch the origin entry that is generated through flexible offset logic 
        OriginEntry expectedOriginEntry = this.getExpectedOriginEntryFromTemplate(group, OFFSET_COA_CODE, OFFSET_ACCOUNT_NUMBER,
                OFFSET_OBJECT_CODE, BALANCE_TYPE_CODE, ENTRY_AMOUNT, DOCUMENT_TYPE_CODE, Constants.GL_CREDIT_CODE);
        Map primaryKeyMap = this.populateOriginEntryPrimaryKey(expectedOriginEntry, false);
               
        // No flexible offset record can be generated        
        int numOfMatching = businessObjectService.countMatching(OriginEntry.class, primaryKeyMap);
        assertTrue("No flexible offset record can be generated.", numOfMatching == 0);
    }        
    
    /**
     * reset the flexible offset enable flag to the given value of the flag
     * 
     * @param flag the given value of the flag.
     */
    private void resetFlexibleOffsetEnableFlag(boolean flag) {
        HashMap keys = new HashMap();
        keys.put(Constants.PARM_SECTION_NAME_FIELD, Constants.ParameterGroups.SYSTEM);
        keys.put(Constants.PARM_PARM_NAME_FIELD, Constants.SystemGroupParameterNames.FLEXIBLE_OFFSET_ENABLED_FLAG);

        // create a new flexible offset enable flag
        FinancialSystemParameter financialSystemParameter = new FinancialSystemParameter();
        financialSystemParameter.setFinancialSystemScriptName(Constants.ParameterGroups.SYSTEM);
        financialSystemParameter.setFinancialSystemParameterName(Constants.SystemGroupParameterNames.FLEXIBLE_OFFSET_ENABLED_FLAG);

        String textFlag = flag ? Constants.ParameterValues.YES : Constants.ParameterValues.NO;
        financialSystemParameter.setFinancialSystemParameterText(textFlag);

        financialSystemParameter.setFinancialSystemParameterActiveIndicator(true);
        financialSystemParameter.setFinancialSystemParameterOperator("A");
        financialSystemParameter.setFinancialSystemMultipleValueIndicator(false);

        this.resetBusinessObject(financialSystemParameter, keys);
        //System.out.println("Enabled: " + flexibleOffsetAccountService.getEnabled());
    }

    /**
     * reset the scubber generation indicator to the given value of the indicator for the specified document type
     * 
     * @param indicator the given value of the indicator.
     * @param documentTypeCode the code of the given document type
     */
    private void resetScrubberGenerationIndicator(boolean indicator, String documentTypeCode) {
        HashMap keys = new HashMap();
        keys.put(PropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, documentTypeCode);

        // create a new flexible offset enable flag
        DocumentType documentType = new DocumentType();
        documentType.setFinancialDocumentTypeCode(documentTypeCode);
        documentType.setTransactionScrubberOffsetGenerationIndicator(indicator);

        this.resetBusinessObject(documentType, keys);
    }

    /**
     * reset the offset account whose primary key is the combination of the given information so that the offset account can be
     * available during proccessing.
     * 
     * @param chartOfAccountsCode the given chart of account code
     * @param accountNumber the given account number
     * @param financialOffsetObjectCode the given offset object code
     */
    private void resetAnOffsetAccount(String chartOfAccountsCode, String accountNumber, String financialOffsetObjectCode) {
        HashMap keys = new HashMap();
        keys.put(PropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        keys.put(PropertyConstants.ACCOUNT_NUMBER, accountNumber);
        keys.put(PropertyConstants.FINANCIAL_OFFSET_OBJECT_CODE, financialOffsetObjectCode);

        OffsetAccount offsetAccount = new OffsetAccount();
        offsetAccount.setChartOfAccountsCode(chartOfAccountsCode);
        offsetAccount.setAccountNumber(accountNumber);
        offsetAccount.setFinancialOffsetObjectCode(financialOffsetObjectCode);

        offsetAccount.setFinancialOffsetChartOfAccountCode(OFFSET_COA_CODE);
        offsetAccount.setFinancialOffsetAccountNumber(OFFSET_ACCOUNT_NUMBER);

        this.resetBusinessObject(offsetAccount, keys);
    }

    /**
     * reset the offset definition whose primary key is the combination of the given information so that the offset definition can
     * be available during proccessing. Essentially, it ensures the value of object code is OFFSET_OBJECT_CODE.
     * 
     * @param fiscalYear the given fiscal year
     * @param chartOfAccountsCode the given chart of accounts code
     * @param documentTypeCode the given document type code
     * @param balanceTypeCode the given balance type code
     */
    private void resetAnOffsetDefinition(Integer fiscalYear, String chartOfAccountsCode, String documentTypeCode,
            String balanceTypeCode) {
        HashMap keys = new HashMap();
        keys.put(PropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        keys.put(PropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        keys.put(PropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, documentTypeCode);
        keys.put(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, balanceTypeCode);

        OffsetDefinition offsetDefinition = new OffsetDefinition();
        offsetDefinition.setUniversityFiscalYear(fiscalYear);
        offsetDefinition.setChartOfAccountsCode(chartOfAccountsCode);
        offsetDefinition.setFinancialDocumentTypeCode(documentTypeCode);
        offsetDefinition.setFinancialBalanceTypeCode(balanceTypeCode);
        offsetDefinition.setFinancialObjectCode(OFFSET_OBJECT_CODE);

        this.resetBusinessObject(offsetDefinition, keys);
    }

    /**
     * reset the business object with underlying database. There are two steps: delete the business object from database, and create
     * a new record with the given business object.
     * 
     * @param businessObject the given business object
     * @param keys the primary key of the record of the given business object in the database
     */
    private void resetBusinessObject(BusinessObject businessObject, Map keys) {
        businessObjectService.deleteMatching(businessObject.getClass(), keys);
        businessObjectService.save(businessObject);
    }

    // put the given entry information into the origin entry template and generate the origin entry
    private OriginEntry getOriginEntryFromTemplate(OriginEntryGroup group, String objectCode, String balanceTypeCode,
            KualiDecimal entryAmount, String documentTypeCode, String debitCreditCode) {

        OriginEntry entry = this.buildOriginEntryTemplate(group);

        entry.setFinancialObjectCode(objectCode);
        entry.setFinancialBalanceTypeCode(balanceTypeCode);
        entry.setTransactionLedgerEntryAmount(entryAmount);
        entry.setFinancialDocumentTypeCode(documentTypeCode);
        entry.setTransactionDebitCreditCode(debitCreditCode);

        return entry;
    }

    // put the given entry information into the origin entry template and generate the origin entry
    private OriginEntry getExpectedOriginEntryFromTemplate(OriginEntryGroup group, String chartOfAccountsCode,
            String accountNumber, String objectCode, String balanceTypeCode, KualiDecimal entryAmount, String documentTypeCode,
            String debitCreditCode) {

        OriginEntry entry = this.getOriginEntryFromTemplate(group, objectCode, balanceTypeCode, entryAmount, documentTypeCode,
                debitCreditCode);

        entry.setChartOfAccountsCode(chartOfAccountsCode);
        entry.setAccountNumber(accountNumber);

        return entry;
    }

    /**
     * build and populate a primary key map with the given origin entry
     * 
     * @param entry the given origin entry
     * @param completeKey the flag that indicates if the complete primary key will be used
     * 
     * @return a primary key map built from the given origin entry
     */
    private Map populateOriginEntryPrimaryKey(OriginEntry entry, boolean completeKey) {
        Map primaryKeyMap = new HashMap();

        primaryKeyMap.put(PropertyConstants.UNIVERSITY_FISCAL_YEAR, entry.getUniversityFiscalYear());
        primaryKeyMap.put(PropertyConstants.CHART_OF_ACCOUNTS_CODE, entry.getChartOfAccountsCode());
        primaryKeyMap.put(PropertyConstants.ACCOUNT_NUMBER, entry.getAccountNumber());
        primaryKeyMap.put(PropertyConstants.SUB_ACCOUNT_NUMBER, entry.getSubAccountNumber());
        primaryKeyMap.put(PropertyConstants.FINANCIAL_OBJECT_CODE, entry.getFinancialObjectCode());

        if (completeKey) {
            primaryKeyMap.put(PropertyConstants.FINANCIAL_SUB_OBJECT_CODE, entry.getFinancialSubObjectCode());
            primaryKeyMap.put(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, entry.getFinancialBalanceTypeCode());
            primaryKeyMap.put(PropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, entry.getFinancialObjectTypeCode());
            primaryKeyMap.put(PropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, entry.getUniversityFiscalPeriodCode());
            primaryKeyMap.put(PropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, entry.getFinancialDocumentTypeCode());
            primaryKeyMap.put(PropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, entry.getFinancialSystemOriginationCode());
            primaryKeyMap.put(PropertyConstants.FINANCIAL_DOCUMENT_NUMBER, entry.getFinancialDocumentNumber());
            primaryKeyMap.put(PropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER, entry.getTransactionLedgerEntrySequenceNumber());
        }

        return primaryKeyMap;
    }


    /**
     * This method offer a template of origin entry that is an instance of transaction. The template can be used to construct other
     * origin entries with an approperiate modification.
     * 
     * @return an origin entry with prepopulated data
     */
    private OriginEntry buildOriginEntryTemplate(OriginEntryGroup group) {
        OriginEntry entry = new OriginEntry();
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());

        entry.setFinancialObjectCode("");
        entry.setFinancialBalanceTypeCode("");
        entry.setFinancialDocumentTypeCode("");
        entry.setTransactionLedgerEntryAmount(new KualiDecimal(0));

        // don't need to chang the values of the following properties
        entry.setUniversityFiscalYear(FISCAL_YEAR);
        entry.setChartOfAccountsCode(COA_CODE);
        entry.setAccountNumber(ACCOUNT_NUMBER);
        entry.setSubAccountNumber("-----");

        entry.setFinancialSubObjectCode("---");
        entry.setFinancialObjectTypeCode("EX");
        entry.setUniversityFiscalPeriodCode("07");
        entry.setFinancialSystemOriginationCode("01");
        entry.setFinancialDocumentNumber("OFFSETDTP");

        entry.setTransactionLedgerEntrySequenceNumber(null);
        entry.setTransactionLedgerEntryDescription("TEST FLEXIBLE OFFSET");
        entry.setTransactionDate(date);
        entry.setTransactionDebitCreditCode("");
        entry.setOrganizationDocumentNumber("");
        entry.setProjectCode("----------");
        entry.setOrganizationReferenceId("");

        entry.setReferenceFinancialDocumentTypeCode("");
        entry.setReferenceFinancialSystemOriginationCode("");
        entry.setReferenceFinancialDocumentNumber("");
        entry.setFinancialDocumentReversalDate(null);
        entry.setTransactionEncumbranceUpdateCode("");

        entry.setGroup(group);
        entry.setEntryGroupId(group.getId());

        return entry;
    }
    
    // create a mock ConfigurationService
    private KualiConfigurationService createMockConfigurationService(boolean flexibleOffsetEnabled) {
        return (KualiConfigurationService) MockService.createProxy(KualiConfigurationService.class,
                "getRequiredApplicationParameterValue", new Object[] { Constants.ParameterGroups.SYSTEM,
                        Constants.SystemGroupParameterNames.FLEXIBLE_OFFSET_ENABLED_FLAG },
                flexibleOffsetEnabled ? Constants.ParameterValues.YES : Constants.ParameterValues.NO);
    }
}