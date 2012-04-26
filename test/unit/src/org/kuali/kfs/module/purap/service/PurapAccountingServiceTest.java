/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.appleton;
import static org.kuali.kfs.sys.fixture.UserNameFixture.kfs;
import static org.kuali.kfs.sys.fixture.UserNameFixture.parke;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.PurapDocTypeCodes;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurApSummaryItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.fixture.PurapAccountingServiceFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.module.purap.util.SummaryAccount;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;

@ConfigureContext(session = kfs)
public class PurapAccountingServiceTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapAccountingServiceTest.class);

    private PurapAccountingService purapAccountingService;
    private PurapService purapService;
    private ParameterService parameterService;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if(purapAccountingService == null) {
            purapAccountingService = SpringContext.getBean(PurapAccountingService.class);
        }
        if(purapService == null) {
            purapService = SpringContext.getBean(PurapService.class);
        }
        if(parameterService == null) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
        
    /*
     * Tests of generateAccountDistributionForProration(List<SourceAccountingLine> accounts, KualiDecimal totalAmount, Integer percentScale, Class clazz)
     */
    
    /**
     * Used by tests of generateAccountDistributionForProration and related methods to make comparisons between
     * the percentages given by the resulting distributed accounts and the percentages that we think should be
     * correct for those lines.
     * 
     * @param distributedAccounts       A List of the PurApAccountingLines that result from the distribution to be tested.
     * @param correctPercents           A List of percents we think should be correct, in BigDecimal format
     */
    private void comparePercentages(List<PurApAccountingLine> distributedAccounts, List<BigDecimal> correctPercents) {
        for(int i = 0; i < distributedAccounts.size(); i++) {
            PurApAccountingLine line = distributedAccounts.get(i);
            BigDecimal percent = line.getAccountLinePercent();
            assertTrue(percent.floatValue() == correctPercents.get(i).floatValue());
        }
    }
    
    public void testGenerateAccountDistributionForProration_OneAcctGood() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PREQ_PRORATION_ONE_ACCOUNT;
        List<PurApAccountingLine> distributedAccounts = purapAccountingService.generateAccountDistributionForProration(
                                                                                fixture.getSourceAccountingLineList(),
                                                                                fixture.getTotalAmount(),
                                                                                fixture.getPercentScale(),
                                                                                fixture.getAccountClass());
        List<BigDecimal> correctPercents = new ArrayList<BigDecimal>();
        correctPercents.add(0,new BigDecimal("100"));
        assertEquals(distributedAccounts.size(),correctPercents.size());
        comparePercentages(distributedAccounts, correctPercents);
    }
    
    public void testGenerateAccountDistributionForProration_TwoAcctGood() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PREQ_PRORATION_TWO_ACCOUNTS;
        List<PurApAccountingLine> distributedAccounts = purapAccountingService.generateAccountDistributionForProration(
                                                                                fixture.getSourceAccountingLineList(),
                                                                                fixture.getTotalAmount(),
                                                                                fixture.getPercentScale(),
                                                                                fixture.getAccountClass());
        List<BigDecimal> correctPercents = new ArrayList<BigDecimal>();
        correctPercents.add(0,new BigDecimal("50"));
        correctPercents.add(1,new BigDecimal("50"));
        assertEquals(distributedAccounts.size(),correctPercents.size());
        comparePercentages(distributedAccounts, correctPercents);
    }
    
    public void testGenerateAccountDistributionForProration_ThreeAccountGood() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PREQ_PRORATION_THIRDS;
        List<PurApAccountingLine> distributedAccounts = purapAccountingService.generateAccountDistributionForProration(
                                                                                fixture.getSourceAccountingLineList(),
                                                                                fixture.getTotalAmount(),
                                                                                fixture.getPercentScale(),
                                                                                fixture.getAccountClass());
        List<BigDecimal> correctPercents = new ArrayList<BigDecimal>();
        correctPercents.add(0,new BigDecimal("33"));
        correctPercents.add(1,new BigDecimal("33"));
        correctPercents.add(2,new BigDecimal("34"));
        assertEquals(distributedAccounts.size(),correctPercents.size());
        comparePercentages(distributedAccounts, correctPercents);
    }
    
    /*
     * Tests of generateAccountDistributionForProrationWithZeroTotal(PurchasingAccountsPayableDocument purapdoc)
     */
    @ConfigureContext(session = appleton, shouldCommitTransactions=true)
    public void testGenerateAccountDistributionForProrationWithZeroTotal_OneAcct() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PREQ_PRORATION_ONE_ACCOUNT_ZERO_TOTAL;
        PurchasingAccountsPayableDocument preq = fixture.generatePaymentRequestDocument_OneItem();
        List<PurApAccountingLine> distributedAccounts = null;
        try {
            distributedAccounts = purapAccountingService.generateAccountDistributionForProrationWithZeroTotal(preq);
        }
        catch (RuntimeException re) {
            fail(re.toString());
        }
        List<BigDecimal> correctPercents = new ArrayList<BigDecimal>();
        correctPercents.add(0,new BigDecimal("100"));
        assertEquals(distributedAccounts.size(),correctPercents.size());
        comparePercentages(distributedAccounts, correctPercents);
    }
    
    @ConfigureContext(session = appleton, shouldCommitTransactions=true)
    public void testGenerateAccountDistributionForProrationWithZeroTotal_TwoAcct() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PREQ_PRORATION_TWO_ACCOUNTS_ZERO_TOTAL;
        PurchasingAccountsPayableDocument preq = fixture.generatePaymentRequestDocument_OneItem();
        List<PurApAccountingLine> distributedAccounts = null;
        try {
            distributedAccounts = purapAccountingService.generateAccountDistributionForProrationWithZeroTotal(preq);
        }
        catch (RuntimeException re) {
            fail(re.toString());
        }        
        List<BigDecimal> correctPercents = new ArrayList<BigDecimal>();
        correctPercents.add(0,new BigDecimal("50"));
        correctPercents.add(1,new BigDecimal("50"));
        assertEquals(distributedAccounts.size(),correctPercents.size());
        comparePercentages(distributedAccounts, correctPercents);
    }
    
    @ConfigureContext(session = appleton, shouldCommitTransactions=true)
    public void testGenerateAccountDistributionForProrationWithZeroTotal_ThreeAccount() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PREQ_PRORATION_THIRDS_ZERO_TOTAL;
        PurchasingAccountsPayableDocument preq = fixture.generatePaymentRequestDocument_OneItem();
        List<PurApAccountingLine> distributedAccounts = null;
        try {
            distributedAccounts = purapAccountingService.generateAccountDistributionForProrationWithZeroTotal(preq);
        }
        catch (RuntimeException re) {
            fail(re.toString());
        }
        List<BigDecimal> correctPercents = new ArrayList<BigDecimal>();
        correctPercents.add(0,new BigDecimal("33.33"));
        correctPercents.add(1,new BigDecimal("33.33"));
        correctPercents.add(2,new BigDecimal("33.34"));
        assertEquals(distributedAccounts.size(),correctPercents.size());
        comparePercentages(distributedAccounts, correctPercents);
    }
    
    /*
     * Tests of generateSummaryAccounts(PurchasingAccountsPayableDocument document)
     */
    
    /**
     * Used by tests of generateSummaryAccounts and related methods to marshall the comparisons of the accounts 
     * and items of the generated summary accounts.
     * 
     * @param fixture       The PurapAccountingServiceFixture
     * @param document      A PurchasingAccountsPayableDocument generated from the fixture.
     */
    private void makePerAccountComparisons(PurapAccountingServiceFixture fixture, PurchasingAccountsPayableDocument doc) {
        int itemCount = doc.getItems().size();
        List<SummaryAccount> accounts = purapAccountingService.generateSummaryAccounts(doc);
        
        List<SourceAccountingLine> originalSourceAccounts = fixture.getSourceAccountingLineList();
        assertEquals(accounts.size(),originalSourceAccounts.size());
        for(int i = 0; i < originalSourceAccounts.size(); i++) {
            SummaryAccount account = accounts.get(i);
            SourceAccountingLine sourceAccount = account.getAccount();
            compareSourceAccounts(sourceAccount, originalSourceAccounts.get(i));
            List<PurApSummaryItem> summaryItems = account.getItems();
            assertTrue(summaryItems.size() <= itemCount);
        }
    }
    
    /**
     * Compares SourceAccounts to see whether those fields which are displayed in the SummaryAccount tab are
     * faithfully represented, with the exception of the Amount, which changes.
     * 
     * @param sourceAccount         The generated SourceAccountingLine
     * @param correctSourceAccount  The SourceAccountingLine which we think should contain correct values
     */
    private void compareSourceAccounts(SourceAccountingLine sourceAccount, SourceAccountingLine correctSourceAccount) {
        Map source = sourceAccount.getValuesMap();
        Map correct = correctSourceAccount.getValuesMap();
        assertEquals(source.get("chartOfAccountsCode"),correct.get("chartOfAccountsCode"));
        assertEquals(source.get("accountNumber"),correct.get("accountNumber"));
        assertEquals(source.get("subAccountNumber"),correct.get("subAccountNumber"));
        assertEquals(source.get("financialObjectCode"),correct.get("financialObjectCode"));
        assertEquals(source.get("financialSubObjectCode"),correct.get("financialSubObjectCode"));
        assertEquals(source.get("projectCode"),correct.get("projectCode"));
        assertEquals(source.get("organizationReferenceId"),correct.get("organizationReferenceId"));
        assertEquals(source.get("organizationDocumentNumber"),correct.get("organizationDocumentNumber"));
    }
    
    @ConfigureContext(session = parke, shouldCommitTransactions=true)
    public void testGenerateSummaryAccounts_OneRequisitionAccountOneItemWithPositiveTotal() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.REQ_PRORATION_ONE_ACCOUNT;
        PurchasingAccountsPayableDocument doc = fixture.generateRequisitionDocument_OneItem();
        makePerAccountComparisons(fixture, doc);
    }
    
    @ConfigureContext(session = parke, shouldCommitTransactions=true)
    public void testGenerateSummaryAccounts_OneRequisitionAccountTwoItemsWithPositiveTotal() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.REQ_PRORATION_ONE_ACCOUNT;
        PurchasingAccountsPayableDocument doc = fixture.generateRequisitionDocument_TwoItems();
        makePerAccountComparisons(fixture, doc);
    }
    
    /*public void testGenerateSummaryAccounts_TwoRequisitionAccountsTwoItems_OneEach_WithPositiveTotal() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.REQ_PRORATION_TWO_ACCOUNTS;
        PurchasingAccountsPayableDocument doc = fixture.generateRequisitionDocument_TwoItems();
        //TODO: Failing.  Needs work.
        makePerAccountComparisons(fixture, doc);
    }*/
    
    @ConfigureContext(session = appleton, shouldCommitTransactions=true)
    public void testGenerateSummaryAccounts_OnePREQAccountOneItemWithPositiveTotal() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PREQ_PRORATION_ONE_ACCOUNT;
        PurchasingAccountsPayableDocument doc = fixture.generatePaymentRequestDocument_OneItem();
        makePerAccountComparisons(fixture, doc);
    }
    
    @ConfigureContext(session = appleton, shouldCommitTransactions=true)
    public void testGenerateSummaryAccounts_OnePREQAccountTwoItemsWithPositiveTotal() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PREQ_PRORATION_ONE_ACCOUNT;
        PurchasingAccountsPayableDocument doc = fixture.generatePaymentRequestDocument_TwoItems();
        makePerAccountComparisons(fixture, doc);
    }
    
    /*
     * Tests of generateSummaryAccountsWithNoZeroTotals(PurchasingAccountsPayableDocument document)
     */
    
    /*
     * Tests of generateSummaryAccountsWithNoZeroTotalsNoUseTax(PurchasingAccountsPayableDocument document)
     */
    
    /*
     * Tests of generateSummary(List<PurApItem> items)
     */
    
    /**
     * Asserts that, for each original source account, there is exactly one source account in the summary, regardless
     * of whether there is more than one instance of this account in the original.
     * 
     * @param sourceLines               The List<SourceAccountingLine> from after the summary
     * @param originalSourceAccounts    The original List<SourceAccountingLine>
     */
    private void checkAccountConsolidation(List<SourceAccountingLine> sourceLines, List<SourceAccountingLine> originalSourceAccounts) {
        for(int i = 0; i < sourceLines.size(); i++) {
            SourceAccountingLine originalSourceAccount = originalSourceAccounts.get(i);
            boolean containsOneAccount = false;
            for( SourceAccountingLine sourceAccount : sourceLines ) {
                if ( StringUtils.equals(sourceAccount.getAccountNumber(), originalSourceAccount.getAccountNumber())) {
                    if (containsOneAccount == false) { 
                        containsOneAccount = true;
                    }
                    else {
                        fail();
                    }
                }
            }
            assertTrue(containsOneAccount);
        }
    }
    
    public void testGenerateSummary_OneItem_OneAccount() {
        //TestUtils.setSystemParameter(Document.class,PurapParameterConstants.ENABLE_SALES_TAX_IND,"Y");
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.REQ_SUMMARY_ONE_ITEM_ONE_ACCOUNT;
        List<SourceAccountingLine> originalSourceAccounts = fixture.getSourceAccountingLineList();
        List<PurApItem> items = fixture.getItems();
        List<SourceAccountingLine> sourceLines = purapAccountingService.generateSummary(items);
        assertEquals(sourceLines.size(),originalSourceAccounts.size());
        checkAccountConsolidation(sourceLines,originalSourceAccounts);
    }
    
    public void testGenerateSummary_OneItem_TwoAccounts() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.REQ_SUMMARY_ONE_ITEM_TWO_ACCOUNTS;
        List<SourceAccountingLine> originalSourceAccounts = fixture.getSourceAccountingLineList();
        List<PurApItem> items = fixture.getItems();
        List<SourceAccountingLine> sourceLines = purapAccountingService.generateSummary(items);
        assertEquals(sourceLines.size(),originalSourceAccounts.size());
        checkAccountConsolidation(sourceLines,originalSourceAccounts);
    }
    
    public void testGenerateSummary_TwoItems_OneAccount() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.REQ_SUMMARY_TWO_ITEMS_ONE_ACCOUNT;
        List<SourceAccountingLine> originalSourceAccounts = fixture.getSourceAccountingLineList();
        List<PurApItem> items = fixture.getItems();
        List<SourceAccountingLine> sourceLines = purapAccountingService.generateSummary(items);
        assertEquals(sourceLines.size(),originalSourceAccounts.size());
        checkAccountConsolidation(sourceLines,originalSourceAccounts);
    }
    
    /*
     * Tests of generateSummaryWithNoZeroTotals(List<PurApItem> items)
     */
    
    public void testGenerateSummaryWithNoZeroTotals_OneItem_OneAccount() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.REQ_SUMMARY_ONE_ITEM_ONE_ACCOUNT;
        List<SourceAccountingLine> originalSourceAccounts = fixture.getSourceAccountingLineList();
        List<PurApItem> items = fixture.getItems();
        List<SourceAccountingLine> sourceLines = purapAccountingService.generateSummaryWithNoZeroTotals(items);
        assertEquals(sourceLines.size(),originalSourceAccounts.size());
        checkAccountConsolidation(sourceLines,originalSourceAccounts);
    }
    
    public void testGenerateSummaryWithNoZeroTotals_OneItem_TwoAccounts() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.REQ_SUMMARY_ONE_ITEM_TWO_ACCOUNTS;
        List<SourceAccountingLine> originalSourceAccounts = fixture.getSourceAccountingLineList();
        List<PurApItem> items = fixture.getItems();
        List<SourceAccountingLine> sourceLines = purapAccountingService.generateSummaryWithNoZeroTotals(items);
        assertEquals(sourceLines.size(),originalSourceAccounts.size());
        checkAccountConsolidation(sourceLines,originalSourceAccounts);
    }
    
    public void testGenerateSummaryWithNoZeroTotals_TwoItems_OneAccount() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.REQ_SUMMARY_TWO_ITEMS_ONE_ACCOUNT;
        List<SourceAccountingLine> originalSourceAccounts = fixture.getSourceAccountingLineList();
        List<PurApItem> items = fixture.getItems();
        List<SourceAccountingLine> sourceLines = purapAccountingService.generateSummaryWithNoZeroTotals(items);
        assertEquals(sourceLines.size(),originalSourceAccounts.size());
        checkAccountConsolidation(sourceLines,originalSourceAccounts);
    }
    
    /*
     * Tests of generateSummaryWithNoZeroTotalsNoUseTax(List<PurApItem> items)
     */
    
    public void testGenerateSummaryWithNoZeroTotalsNoUseTax_OneItem_OneAccount() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.REQ_SUMMARY_ONE_ITEM_ONE_ACCOUNT;
        List<SourceAccountingLine> originalSourceAccounts = fixture.getSourceAccountingLineList();
        List<PurApItem> items = fixture.getItems();
        GlobalVariables.getUserSession().setBackdoorUser("parke");
        RequisitionDocumentFixture reqFixture = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS;
        RequisitionDocument req = reqFixture.createRequisitionDocument();
        GlobalVariables.getUserSession().setBackdoorUser("kfs");
        for(PurApItem item : items) {
            item.setPurapDocument(req);
        }      
        List<SourceAccountingLine> sourceLines = purapAccountingService.generateSummaryWithNoZeroTotalsNoUseTax(items);
        assertEquals(sourceLines.size(),originalSourceAccounts.size());
        checkAccountConsolidation(sourceLines,originalSourceAccounts);
    }
    
    public void testGenerateSummaryWithNoZeroTotalsNoUseTax_OneItem_TwoAccounts() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.REQ_SUMMARY_ONE_ITEM_TWO_ACCOUNTS;
        List<SourceAccountingLine> originalSourceAccounts = fixture.getSourceAccountingLineList();
        List<PurApItem> items = fixture.getItems();
        GlobalVariables.getUserSession().setBackdoorUser("parke");
        RequisitionDocumentFixture reqFixture = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS;
        RequisitionDocument req = reqFixture.createRequisitionDocument();
        GlobalVariables.getUserSession().setBackdoorUser("kfs");
        for(PurApItem item : items) {
            item.setPurapDocument(req);
        }
        List<SourceAccountingLine> sourceLines = purapAccountingService.generateSummaryWithNoZeroTotalsNoUseTax(items);
        assertEquals(sourceLines.size(),originalSourceAccounts.size());
        checkAccountConsolidation(sourceLines,originalSourceAccounts);
    }
    
    public void testGenerateSummaryWithNoZeroTotalsNoUseTax_TwoItems_OneAccount() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.REQ_SUMMARY_TWO_ITEMS_ONE_ACCOUNT;
        List<SourceAccountingLine> originalSourceAccounts = fixture.getSourceAccountingLineList();
        List<PurApItem> items = fixture.getItems();
        GlobalVariables.getUserSession().setBackdoorUser("parke");
        RequisitionDocumentFixture reqFixture = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS;
        RequisitionDocument req = reqFixture.createRequisitionDocument();
        GlobalVariables.getUserSession().setBackdoorUser("kfs");
        for(PurApItem item : items) {
            item.setPurapDocument(req);
        }
        List<SourceAccountingLine> sourceLines = purapAccountingService.generateSummaryWithNoZeroTotalsNoUseTax(items);
        assertEquals(sourceLines.size(),originalSourceAccounts.size());
        checkAccountConsolidation(sourceLines,originalSourceAccounts);
    }   
    
    /*
     * Tests of generateSummaryWithNoZeroTotalsUsingAlternateAmount(List<PurApItem> items)
     */    
    public void testGenerateSummaryWithNoZeroTotalsUsingAlternateAmount_OneItem_OneAccount() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.REQ_SUMMARY_ONE_ITEM_ONE_ACCOUNT;
        List<SourceAccountingLine> originalSourceAccounts = fixture.getSourceAccountingLineList();
        List<PurApItem> items = fixture.getItems();
        GlobalVariables.getUserSession().setBackdoorUser("parke");
        RequisitionDocumentFixture reqFixture = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS;
        RequisitionDocument req = reqFixture.createRequisitionDocument();
        GlobalVariables.getUserSession().setBackdoorUser("kfs");
        for(PurApItem item : items) {
            item.setPurapDocument(req);
        }      
        List<SourceAccountingLine> sourceLines = purapAccountingService.generateSummaryWithNoZeroTotalsUsingAlternateAmount(items);
        assertEquals(sourceLines.size(),originalSourceAccounts.size());
        checkAccountConsolidation(sourceLines,originalSourceAccounts);
    }
    
    public void testGenerateSummaryWithNoZeroTotalsUsingAlternateAmount_OneItem_TwoAccounts() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.REQ_SUMMARY_ONE_ITEM_TWO_ACCOUNTS;
        List<SourceAccountingLine> originalSourceAccounts = fixture.getSourceAccountingLineList();
        List<PurApItem> items = fixture.getItems();
        GlobalVariables.getUserSession().setBackdoorUser("parke");
        RequisitionDocumentFixture reqFixture = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS;
        RequisitionDocument req = reqFixture.createRequisitionDocument();
        GlobalVariables.getUserSession().setBackdoorUser("kfs");
        for(PurApItem item : items) {
            item.setPurapDocument(req);
        }
        List<SourceAccountingLine> sourceLines = purapAccountingService.generateSummaryWithNoZeroTotalsUsingAlternateAmount(items);
        assertEquals(sourceLines.size(),originalSourceAccounts.size());
        checkAccountConsolidation(sourceLines,originalSourceAccounts);
    }
    
    public void testGenerateSummaryWithNoZeroTotalsUsingAlternateAmount_TwoItems_OneAccount() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.REQ_SUMMARY_TWO_ITEMS_ONE_ACCOUNT;
        List<SourceAccountingLine> originalSourceAccounts = fixture.getSourceAccountingLineList();
        List<PurApItem> items = fixture.getItems();
        GlobalVariables.getUserSession().setBackdoorUser("parke");
        RequisitionDocumentFixture reqFixture = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS;
        RequisitionDocument req = reqFixture.createRequisitionDocument();
        GlobalVariables.getUserSession().setBackdoorUser("kfs");
        for(PurApItem item : items) {
            item.setPurapDocument(req);
        }
        List<SourceAccountingLine> sourceLines = purapAccountingService.generateSummaryWithNoZeroTotalsUsingAlternateAmount(items);
        assertEquals(sourceLines.size(),originalSourceAccounts.size());
        checkAccountConsolidation(sourceLines,originalSourceAccounts);
    }    
    
    /*
     * Tests of generateSummaryExcludeItemTypes(List<PurApItem> items, Set excludedItemTypeCodes)
     */
    
    /*
     * Tests of generateSummaryExcludeItemTypesAndNoZeroTotals(List<PurApItem> items, Set excludedItemTypeCodes)
     */
    
    /*
     * Tests of generateSummaryIncludeItemTypesAndNoZeroTotals(List<PurApItem> items, Set includedItemTypeCodes)
     */
    
    /*
     * Tests of updateAccountAmounts(PurchasingAccountsPayableDocument document)
     */ 
    @ConfigureContext(session = appleton)
    public void testUpdateAccountAmounts_BeforeFullEntry_PercentToAmount() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PREQ_PRORATION_THIRDS;
        PurchasingAccountsPayableDocument preq = fixture.generatePaymentRequestDocument_OneItem();
        purapAccountingService.updateAccountAmounts(preq);
        assertFalse(preq.getItems().get(0).getTotalAmount().isZero());
    }

    @ConfigureContext(session = appleton)    
    public void testUpdateAccountAmounts_BeforeFullEntry_AmountNotToPercent() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PREQ_PRORATION_THIRDS;
        PurchasingAccountsPayableDocument preq = fixture.generatePaymentRequestDocument_OneItem();
        purapAccountingService.updateAccountAmounts(preq);
        PurApItem item = preq.getItems().get(0);
        int i = 0;
        boolean orResult = false;
        for (PurApAccountingLine correctLine : fixture.getPurApAccountingLineList()) {
            PurApAccountingLine line = item.getSourceAccountingLines().get(i++);
            if(!line.getAccountLinePercent().equals(correctLine.getAccountLinePercent())) {
                orResult = true;
                break;
            }
        }
        assertFalse(orResult);
    }
    
    @ConfigureContext(session = appleton)
    public void testUpdateAccountAmounts_AfterFullEntry_AmountToPercent() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PREQ_PRORATION_THIRDS;
        PurchasingAccountsPayableDocument preq = fixture.generatePaymentRequestDocument_OneItem();
        preq.setApplicationDocumentStatus(PurapConstants.PaymentRequestStatuses.APPDOC_DEPARTMENT_APPROVED);
        purapAccountingService.updateAccountAmounts(preq);
        PurApItem item = preq.getItems().get(0);
        int i = 0;
        for (PurApAccountingLine correctLine : fixture.getPurApAccountingLineList()) {
            PurApAccountingLine line = item.getSourceAccountingLines().get(i++);
            assertTrue(line.getAccountLinePercent().equals(correctLine.getAccountLinePercent()));
        }
    }
    
    @ConfigureContext(session = appleton)
    public void testUpdateAccountAmounts_AfterFullEntry_PercentNotToAmount() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PREQ_PRORATION_THIRDS;
        PurchasingAccountsPayableDocument preq = fixture.generatePaymentRequestDocument_OneItem();
        preq.setApplicationDocumentStatus(PurapConstants.PaymentRequestStatuses.APPDOC_DEPARTMENT_APPROVED);
        preq.getItems().get(0).setExtendedPrice(KualiDecimal.ZERO);
        purapAccountingService.updateAccountAmounts(preq);
        assertTrue(preq.getItems().get(0).getTotalAmount().isZero());
    }
    
    /*
     * Tests of updateItemAccountAmounts(PurApItem item)
     */
    @ConfigureContext(session = parke)
    public void testUpdateItemAccountAmounts_OneAccount() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.REQ_PRORATION_ONE_ACCOUNT;
        RequisitionDocument req = fixture.generateRequisitionDocument_OneItem();
        RequisitionItem item = (RequisitionItem)req.getItems().get(0);
        KualiDecimal total = item.getTotalAmount();
        purapAccountingService.updateItemAccountAmounts(item);
        PurApAccountingLine line = item.getSourceAccountingLines().get(0);
        assertTrue(line.getAmount().compareTo(total) == 0);
    }
    
    @ConfigureContext(session = parke)
    public void testUpdateItemAccountAmounts_TwoAccounts() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.REQ_PRORATION_TWO_ACCOUNTS;
        RequisitionDocument req = fixture.generateRequisitionDocument_OneItem();
        RequisitionItem item = (RequisitionItem)req.getItems().get(0);
        KualiDecimal total = item.getTotalAmount();
        purapAccountingService.updateItemAccountAmounts(item);
        PurApAccountingLine line = item.getSourceAccountingLines().get(0);
        assertTrue(line.getAmount().compareTo(total.divide(new KualiDecimal(2))) == 0);
    }
    
    @ConfigureContext(session = parke)
    public void testUpdateItemAccountAmounts_ThreeAccounts() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.REQ_PRORATION_THIRDS;
        RequisitionDocument req = fixture.generateRequisitionDocument_OneItem();
        RequisitionItem item = (RequisitionItem)req.getItems().get(0);
        KualiDecimal total = item.getTotalAmount();
        purapAccountingService.updateItemAccountAmounts(item);
        PurApAccountingLine line1 = item.getSourceAccountingLines().get(0);
        assertTrue(line1.getAmount().compareTo(total.divide(new KualiDecimal(3))) == 0);
    }   
    
    /*
     * Tests of deleteSummaryAccounts(Integer purapDocumentIdentifier)
     */
    @ConfigureContext(session = appleton, shouldCommitTransactions=true)
    public void testDeleteSummaryAccounts_PaymentRequest() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PREQ_PRORATION_ONE_ACCOUNT;
        PaymentRequestDocument preq = fixture.generatePaymentRequestDocument_OneItem();
        List<SummaryAccount> summaryAccounts = purapAccountingService.generateSummaryAccounts(preq);
        assertNotNull(summaryAccounts);
        purapAccountingService.deleteSummaryAccounts(preq.getPurapDocumentIdentifier(), PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT);
        assertNull(purapAccountingService.getAccountsPayableSummaryAccounts(preq.getPurapDocumentIdentifier(), PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT));
    }
    
    @ConfigureContext(session = appleton, shouldCommitTransactions=true)
    public void testDeleteSummaryAccounts_CreditMemo() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.CREDIT_MEMO_ONE_ACCOUNT;
        VendorCreditMemoDocument vcm = fixture.generateVendorCreditMemoDocument_OneItem();
        List<SummaryAccount> summaryAccounts = purapAccountingService.generateSummaryAccounts(vcm);
        assertNotNull(summaryAccounts);
        purapAccountingService.deleteSummaryAccounts(vcm.getPurapDocumentIdentifier(), PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT);
        assertNull(purapAccountingService.getAccountsPayableSummaryAccounts(vcm.getPurapDocumentIdentifier(), PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT));
    }
    
    /*
     * Tests of generateSourceAccountsForVendorRemit(PurchasingAccountsPayableDocument document)
     */
    
    
    
    /*
     * Tests of convertMoneyToPercent(PaymentRequestDocument pr)
     */
    /*@ConfigureContext(session = appleton)
    public void testConvertMoneyToPercent_OneItem_OneAccount() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PREQ_PRORATION_ONE_ACCOUNT;
        PaymentRequestDocument preq = fixture.generatePaymentRequestDocument_OneItem();
        ((PurchasingAccountsPayableDocument)preq).getItems().get(0).setExtendedPrice(new KualiDecimal(32));
        purapAccountingService.convertMoneyToPercent(preq);
        PurchasingAccountsPayableDocument doc = (PurchasingAccountsPayableDocument)preq;
        BigDecimal percent = doc.getItems().get(0).getSourceAccountingLines().get(0).getAccountLinePercent();
        try {
            assertTrue(percent.compareTo(new BigDecimal(100)) == 0);
        }
        catch (ArithmeticException ae) {
            fail("The conversion is not exact." + ae.toString());
        }
    }*/
    
    /*@ConfigureContext(session = appleton)
    public void testConvertMoneyToPercent_OneItem_TwoAccounts() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PREQ_PRORATION_TWO_ACCOUNTS;
        PaymentRequestDocument preq = fixture.generatePaymentRequestDocument_OneItem();
        purapAccountingService.convertMoneyToPercent(preq);
        PurchasingAccountsPayableDocument doc = (PurchasingAccountsPayableDocument)preq;
        BigDecimal percent = doc.getItems().get(0).getSourceAccountingLines().get(0).getAccountLinePercent();
        try {
            assertTrue(percent.compareTo(new BigDecimal(50)) == 0);
        }
        catch (ArithmeticException ae) {
            fail("The conversion is not exact." + ae.toString());
        }
    }*/
    
    /*
     * Tests of generateUseTaxAccount(PurchasingAccountsPayableDocument document)
     */
}
