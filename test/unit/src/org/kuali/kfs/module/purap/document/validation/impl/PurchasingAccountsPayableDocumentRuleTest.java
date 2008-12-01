/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.kuluser;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestAccount;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.validation.PurapRuleTestBase;
import org.kuali.kfs.module.purap.fixture.PurApAccountingLineFixture;
import org.kuali.kfs.module.purap.fixture.PurapAccountingServiceFixture;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderAccountingLineFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.fixture.AccountingLineFixture;

@ConfigureContext(session = kuluser, shouldCommitTransactions=true)
public class PurchasingAccountsPayableDocumentRuleTest extends
		PurapRuleTestBase {
	
    PurchasingAccountsPayableDocumentRuleBase rules;

    protected void setUp() throws Exception {
        super.setUp();
        rules = new PurchasingAccountsPayableDocumentRuleBase();
    }

    protected void tearDown() throws Exception {
        rules = null;
        super.tearDown();
    }

    // Tests of processAccountSummaryValidation
    
    // Tests of validateBelowTheLineValues
    
    // Tests of verifyHasAccounts
    
    // Tests of verifyAccountPercent
    
    public void testVerifyAccountPercent_FiftyFifty() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PRORATION_TWO_ACCOUNTS;
        PurchasingAccountsPayableDocument doc =  fixture.generateRequisitionDocument_OneItem();
        List<PurApAccountingLine> purAccounts = doc.getItems().get(0).getSourceAccountingLines();
        assertTrue(rules.verifyAccountPercent(doc, purAccounts, "1"));
    }
    
    /*public void testVerifyAccountPercent_ThreeWay() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.PRORATION_THIRDS;
        PurchasingAccountsPayableDocument doc =  fixture.generateRequisitionDocument_OneItem();
        List<PurApAccountingLine> purAccounts = doc.getItems().get(0).getSourceAccountingLines();
        assertTrue(rules.verifyAccountPercent(doc, purAccounts, "1"));
    }*/
    
    // Tests of verifyUniqueAccountingStrings
    
    public void testVerifyUniqueAccountingStrings_DifferentStrings() {
        List<PurApAccountingLine> purAccounts = new ArrayList<PurApAccountingLine>();
        PurApAccountingLineFixture purApAcctLineFixture = PurApAccountingLineFixture.ACCOUNT_50_PERCENT;
        PurApAccountingLine purApAcctLine1 = purApAcctLineFixture.createPurApAccountingLine(PaymentRequestAccount.class, 
                AccountingLineFixture.PURAP_LINE1);      
        purAccounts.add(purApAcctLine1);
        PurApAccountingLine purApAcctLine2 = purApAcctLineFixture.createPurApAccountingLine(PaymentRequestAccount.class, 
                AccountingLineFixture.PURAP_LINE2);      
        purAccounts.add(purApAcctLine2);       
        assertTrue(rules.verifyUniqueAccountingStrings(purAccounts, PurapConstants.ITEM_TAB_ERROR_PROPERTY, "1"));
    }
    
    public void testVerifyUniqueAccountingStrings_SameStrings() {
        List<PurApAccountingLine> purAccounts = new ArrayList<PurApAccountingLine>();
        PurApAccountingLineFixture purApAcctLineFixture = PurApAccountingLineFixture.ACCOUNT_50_PERCENT;
        PurApAccountingLine purApAcctLine1 = purApAcctLineFixture.createPurApAccountingLine(PaymentRequestAccount.class, 
                AccountingLineFixture.PURAP_LINE1);      
        purAccounts.add(purApAcctLine1);
        PurApAccountingLine purApAcctLine2 = purApAcctLineFixture.createPurApAccountingLine(PaymentRequestAccount.class, 
                AccountingLineFixture.PURAP_LINE1);      
        purAccounts.add(purApAcctLine2);       
        assertFalse(rules.verifyUniqueAccountingStrings(purAccounts, PurapConstants.ITEM_TAB_ERROR_PROPERTY, "1"));
    }
    
    // Tests of verifyAccountingStringsBetween0And100Percent
    
    public void testVerifyAccountingStringsBetween0And100Percent_Happy() {
        PurApAccountingLine accountingLine = PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1.createPurApAccountingLine(
                PurchaseOrderDocument.class,
                PurApAccountingLineFixture.BASIC_ACCOUNT_1,
                AccountingLineFixture.PURAP_LINE1);
        assertTrue(rules.verifyAccountingStringsBetween0And100Percent(accountingLine,PurapPropertyConstants.ACCOUNT_LINE_PERCENT,"1"));
    }
    
    public void testVerifyAccountingStringsBetween0And100Percent_PercentTooHigh() {
        PurApAccountingLine accountingLine = PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1.createPurApAccountingLine(
                PurchaseOrderDocument.class,
                PurApAccountingLineFixture.BAD_ACCOUNT_PERCENT_TOO_HIGH,
                AccountingLineFixture.PURAP_LINE1);
        assertFalse(rules.verifyAccountingStringsBetween0And100Percent(accountingLine,PurapPropertyConstants.ACCOUNT_LINE_PERCENT,"1"));
    }
    
    public void testVerifyAccountingStringsBetween0And100Percent_PercentZero() {
        PurApAccountingLine accountingLine = PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1.createPurApAccountingLine(
                PurchaseOrderDocument.class,
                PurApAccountingLineFixture.BAD_ACCOUNT_PERCENT_ZERO,
                AccountingLineFixture.PURAP_LINE1);
        assertFalse(rules.verifyAccountingStringsBetween0And100Percent(accountingLine,PurapPropertyConstants.ACCOUNT_LINE_PERCENT,"1"));
    }
    
    public void testVerifyAccountingStringsBetween0And100Percent_PercentNegative() {
        PurApAccountingLine accountingLine = PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1.createPurApAccountingLine(
                PurchaseOrderDocument.class,
                PurApAccountingLineFixture.BAD_ACCOUNT_PERCENT_NEGATIVE,
                AccountingLineFixture.PURAP_LINE1);
        assertFalse(rules.verifyAccountingStringsBetween0And100Percent(accountingLine,PurapPropertyConstants.ACCOUNT_LINE_PERCENT,"1"));
    }
}
