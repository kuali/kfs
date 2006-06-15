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

import java.sql.Date;
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
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.financial.bo.OffsetAccount;
import org.kuali.module.financial.service.FlexibleOffsetAccountService;
import org.kuali.module.gl.GLSpringBeansRegistry;
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

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        scrubberService = (ScrubberService) beanFactory.getBean(GLSpringBeansRegistry.glScrubberService);

        businessObjectService = (BusinessObjectService) beanFactory.getBean("businessObjectService");
        documentTypeService = (DocumentTypeService) beanFactory.getBean("documentTypeService");

        flexibleOffsetAccountService = (FlexibleOffsetAccountService) beanFactory.getBean("flexibleOffsetAccountService");

        // Get the test date time service so we can specify the date/time of the run
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.YEAR, 2006);
        date = c.getTime();
        dateTimeService.currentDate = date;
    }

    /**
     * test the primary scenario of flexible offset generation, that is, the given origin entry must have a flexible offset entry
     * generated.
     * 
     * @throws Exception
     */
    public void testFlexibleOffsetGeneration() throws Exception {

        // reset the preconditions of flexible offset generation so that they have the vaild values
        resetFlexibleOffsetEnableFlag(true);
        resetScrubberGenerationIndicator(true, DOCUMENT_TYPE_CODE);
        resetAnOffsetAccount(COA_CODE, ACCOUNT_NUMBER, OFFSET_OBJECT_CODE);
        resetAnOffsetDefinition(FISCAL_YEAR, COA_CODE, DOCUMENT_TYPE_CODE, BALANCE_TYPE_CODE);

        String[] input = new String[] {
                "2004BL1031400-----4190---ACEX07PCDO01OFFSETDTP00000TEST FLEXIBLE OFFSET                              2000.00D2006-01-01          ----------                                  ",
                "2004BL1031400-----4021---ACEX07GEC 01OFFSETDTP00000TEST FLEXIBLE OFFSET                              2000.00D2006-01-01          ----------                                  ",
        };

        EntryHolder[] output = new EntryHolder[] {
                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BL1031400-----4190---ACEX07PCDO01OFFSETDTP00000TEST FLEXIBLE OFFSET                              2000.00D2006-01-01          ----------                                  "),
                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BL1031400-----4021---ACEX07GEC 01OFFSETDTP00000TEST FLEXIBLE OFFSET                              2000.00D2006-01-01          ----------                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL1031400-----4021---ACEX07GEC 01OFFSETDTP00000TEST FLEXIBLE OFFSET                              2000.00D2006-01-01          ----------                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL1031400-----4190---ACEX07PCDO01OFFSETDTP00000TEST FLEXIBLE OFFSET                              2000.00D2006-01-01          ----------                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004UA1912201-----8100---ACAS07PCDO01OFFSETDTP00000GENERATED OFFSET                                  2000.00C2006-01-01          ----------                                  "),
        };

        fail("I think this is wrong...there should be 2 offsets?");
        scrub(input);
        assertOriginEntries(4,output);
    }

    /**
     * test the case when the global flexible offset enable flag is set to false. In this case, the offset generation must not be
     * executed, that is, there is no flexible offset entry generated.
     * 
     * @throws Exception
     */
    public void testFlexibleOffsetEnableFlag() throws Exception {
        this.clearOriginEntryTables();
        OriginEntryGroup group = originEntryGroupService.createGroup(new Date(new java.util.Date().getTime()), OriginEntrySource.EXTERNAL, true, true, true);

        // disable the global flexible offset enable flag
        // flexibleOffsetAccountService.setKualiConfigurationService(createMockConfigurationService(false));
        resetFlexibleOffsetEnableFlag(false);

        // reset the preconditions of flexible offset generation so that they have the vaild values
        resetScrubberGenerationIndicator(true, DOCUMENT_TYPE_CODE);
        resetAnOffsetAccount(COA_CODE, ACCOUNT_NUMBER, OFFSET_OBJECT_CODE);
        resetAnOffsetDefinition(FISCAL_YEAR, COA_CODE, DOCUMENT_TYPE_CODE, BALANCE_TYPE_CODE);

        String[] input = new String[] {
                "2004BL1031400-----4190---ACEX07PCDO01OFFSETDTP00000TEST FLEXIBLE OFFSET                              2000.00D2006-01-01          ----------                                  "
        };

        EntryHolder[] output = new EntryHolder[] {
                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BL1031400-----4190---ACEX07PCDO01OFFSETDTP00000TEST FLEXIBLE OFFSET                              2000.00D2006-01-01          ----------                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL1031400-----4190---ACEX07PCDO01OFFSETDTP00000TEST FLEXIBLE OFFSET                              2000.00D2006-01-01          ----------                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL1031400-----8100---ACAS07PCDO01OFFSETDTP00000GENERATED OFFSET                                  2000.00C2006-01-01          ----------                                  "),
        };

        scrub(input);
        assertOriginEntries(4,output);
    }

    /**
     * test the case when the scrubber generation indicator is set to false. In this case, the offset generation must not be
     * executed, that is, there is no flexible offset entry generated.
     * 
     * @throws Exception
     */
    public void testScrubberGenerationIndicator() throws Exception {
        this.clearOriginEntryTables();
        OriginEntryGroup group = originEntryGroupService.createGroup(new Date(new java.util.Date().getTime()), OriginEntrySource.EXTERNAL, true, true, true);

        // disable the scrubber generation indicator
        resetScrubberGenerationIndicator(false, DOCUMENT_TYPE_CODE);

        // reset the preconditions of flexible offset generation so that they have the vaild values
        // flexibleOffsetAccountService.setKualiConfigurationService(createMockConfigurationService(true));
        resetFlexibleOffsetEnableFlag(true);

        resetAnOffsetAccount(COA_CODE, ACCOUNT_NUMBER, OFFSET_OBJECT_CODE);
        resetAnOffsetDefinition(FISCAL_YEAR, COA_CODE, DOCUMENT_TYPE_CODE, BALANCE_TYPE_CODE);

        String[] input = new String[] {
                "2004BL1031400-----4190---ACEX07PCDO01OFFSETDTP00000TEST FLEXIBLE OFFSET                              2000.00D2006-01-01          ----------                                  "
        };

        EntryHolder[] output = new EntryHolder[] {
                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BL1031400-----4190---ACEX07PCDO01OFFSETDTP00000TEST FLEXIBLE OFFSET                              2000.00D2006-01-01          ----------                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL1031400-----4190---ACEX07PCDO01OFFSETDTP00000TEST FLEXIBLE OFFSET                              2000.00D2006-01-01          ----------                                  "),
        };

        scrub(input);
        assertOriginEntries(4,output);
    }

    /**
     * test the case when there is no corresponding offset account for the given origin entry. In this case, the flexible offset generation
     * must not be executed, that is, there is no flexible offset entry generated.  But there should be a non-flexible offset generated
     * 
     * @throws Exception
     */
    public void testOffsetAccount() throws Exception {
        this.clearOriginEntryTables();
        OriginEntryGroup group = originEntryGroupService.createGroup(new Date(new java.util.Date().getTime()), OriginEntrySource.EXTERNAL, true, true, true);

        // reset the preconditions of flexible offset generation so that they have the vaild values
        // flexibleOffsetAccountService.setKualiConfigurationService(createMockConfigurationService(true));
        resetFlexibleOffsetEnableFlag(true);
        resetScrubberGenerationIndicator(true, DOCUMENT_TYPE_CODE);
        resetAnOffsetAccount(COA_CODE, ACCOUNT_NUMBER, OFFSET_OBJECT_CODE);
        resetAnOffsetDefinition(FISCAL_YEAR, COA_CODE, DOCUMENT_TYPE_CODE, BALANCE_TYPE_CODE);

        String[] input = new String[] {
                "2004BA6044913-----4190---ACEX07PCDO01OFFSETDTP00000TEST FLEXIBLE OFFSET                              2000.00D2006-01-01          ----------                                  "
        };

        EntryHolder[] output = new EntryHolder[] {
                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BA6044913-----4190---ACEX07PCDO01OFFSETDTP00000TEST FLEXIBLE OFFSET                              2000.00D2006-01-01          ----------                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044913-----4190---ACEX07PCDO01OFFSETDTP00000TEST FLEXIBLE OFFSET                              2000.00D2006-01-01          ----------                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044913-----8000---ACAS07PCDO01OFFSETDTP00000GENERATED OFFSET                                  2000.00C2006-01-01          ----------                                  "),
        };

        scrub(input);
        assertOriginEntries(4,output);
    }

    /**
     * test the case when there is no offset object available. In this case, the offset generation must not be executed, that is,
     * there is no flexible offset entry generated.
     * 
     * @throws Exception
     */
    public void testOffsetDefinition() throws Exception {
        this.clearOriginEntryTables();
        OriginEntryGroup group = originEntryGroupService.createGroup(new Date(new java.util.Date().getTime()), OriginEntrySource.EXTERNAL, true, true, true);

        // reset the preconditions of flexible offset generation so that they have the vaild values
        // flexibleOffsetAccountService.setKualiConfigurationService(createMockConfigurationService(true));
        resetFlexibleOffsetEnableFlag(true);
        resetScrubberGenerationIndicator(true, DOCUMENT_TYPE_CODE);
        resetAnOffsetAccount(COA_CODE, ACCOUNT_NUMBER, OFFSET_OBJECT_CODE);
        resetAnOffsetDefinition(FISCAL_YEAR, COA_CODE, DOCUMENT_TYPE_CODE, BALANCE_TYPE_CODE);

        String[] input = new String[] {
                "2004BL1031400-----4190---ACEX07GEC 01OFFSETDTP00000TEST FLEXIBLE OFFSET                              2000.00D2006-01-01          ----------                                  "
        };

        EntryHolder[] output = new EntryHolder[] {
                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BL1031400-----4190---ACEX07GEC 01OFFSETDTP00000TEST FLEXIBLE OFFSET                              2000.00D2006-01-01          ----------                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL1031400-----4190---ACEX07GEC 01OFFSETDTP00000TEST FLEXIBLE OFFSET                              2000.00D2006-01-01          ----------                                  "),
        };
        fail("I think this is wrong");

        scrub(input);
        assertOriginEntries(4,output);
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
        // System.out.println("Enabled: " + flexibleOffsetAccountService.getEnabled());
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
    private void resetAnOffsetDefinition(Integer fiscalYear, String chartOfAccountsCode, String documentTypeCode, String balanceTypeCode) {
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

    private void scrub(String[] inputTransactions) {
        clearOriginEntryTables();
        loadInputTransactions(OriginEntrySource.EXTERNAL,inputTransactions,date);
        persistenceService.getPersistenceBroker().clearCache();
        scrubberService.scrubEntries();
    }
}