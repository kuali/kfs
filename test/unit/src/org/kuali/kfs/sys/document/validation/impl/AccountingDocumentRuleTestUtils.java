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
package org.kuali.kfs.sys.document.validation.impl;

import static org.kuali.kfs.sys.KualiTestAssertionUtils.assertGlobalMessageMapEmpty;
import static org.kuali.kfs.sys.KualiTestAssertionUtils.assertSparselyEqualBean;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.event.AddAccountingLineEvent;
import org.kuali.kfs.sys.fixture.GeneralLedgerPendingEntryFixture;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.rules.rule.RouteDocumentRule;
import org.kuali.rice.krad.rules.rule.SaveDocumentRule;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;

public abstract class AccountingDocumentRuleTestUtils extends KualiTestBase {

    // test methods

    public static <T extends AccountingDocument> void testAddAccountingLineRule_ProcessAddAccountingLineBusinessRules(T document, boolean expected) throws Exception {
        // Check business rules
        List<? extends AccountingLine> allLines = new ArrayList<AccountingLine>();
        allLines.addAll(document.getSourceAccountingLines());
        allLines.addAll(document.getTargetAccountingLines());

        assertGlobalMessageMapEmpty();
        for (AccountingLine accountingLine : allLines) {
            String collectionName = null;
            if(accountingLine instanceof SourceAccountingLine){
                collectionName = KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME;
            }else if(accountingLine instanceof TargetAccountingLine){
                collectionName = KFSConstants.NEW_TARGET_ACCT_LINE_PROPERTY_NAME;
            }
            boolean ruleResult = SpringContext.getBean(KualiRuleService.class).applyRules(new AddAccountingLineEvent(KFSPropertyConstants.SOURCE_ACCOUNTING_LINE, document, accountingLine));
            if (expected) {
                assertGlobalMessageMapEmpty(accountingLine.toString());
            }
            assertEquals("GlobalVariables.getMessageMap() : " + GlobalVariables.getMessageMap(), expected, ruleResult);
        }
    }

    public static <T extends AccountingDocument> void testSaveDocumentRule_ProcessSaveDocument(T document, boolean expected) throws Exception {
        SaveDocumentRule rule = getBusinessRule(document.getClass(), SaveDocumentRule.class);
        boolean rulePassed = rule.processSaveDocument(document);
        if (expected) {
            assertGlobalMessageMapEmpty();
        }
        assertEquals("GlobalVariables.getMessageMap() : " + GlobalVariables.getMessageMap(), expected, rulePassed);
    }

    public static <T extends AccountingDocument> void testRouteDocumentRule_processRouteDocument(T document, boolean expected) throws Exception {
        RouteDocumentRule rule = getBusinessRule(document.getClass(), RouteDocumentRule.class);
        boolean rulePassed = rule.processRouteDocument(document);
        if (expected) {
            assertGlobalMessageMapEmpty();
        }
        assertEquals("GlobalVariables.getMessageMap() : " + GlobalVariables.getMessageMap(), expected, rulePassed);
    }

    public static boolean testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries(AccountingDocument document, AccountingLine line, GeneralLedgerPendingEntryFixture expectedExplicitFixture, GeneralLedgerPendingEntryFixture expectedOffsetFixture) throws Exception {
        boolean success = true;
        assertEquals(0, document.getGeneralLedgerPendingEntries().size());
        success &= document.generateGeneralLedgerPendingEntries(line, new GeneralLedgerPendingEntrySequenceHelper());
        assertEquals(expectedOffsetFixture == null ? 1 : 2, document.getGeneralLedgerPendingEntries().size());
        assertSparselyEqualBean(expectedExplicitFixture.createGeneralLedgerPendingEntry(), document.getGeneralLedgerPendingEntry(0));
        if (expectedOffsetFixture != null) {
            assertSparselyEqualBean(expectedOffsetFixture.createGeneralLedgerPendingEntry(), document.getGeneralLedgerPendingEntry(1));
        }
        return success;
    }


    // helper methods
    /**
     * retrieves a rule instance for a given document
     *
     * @param <T>
     * @param documentClass the type of document to retrieve the rule for
     * @param businessRuleClass the type of rule to create
     * @return an instance of a BusinessRule of the same type as the businessRuleClass parameter
     * @throws Exception
     */
    public static <T extends org.kuali.rice.krad.rules.rule.BusinessRule> T getBusinessRule(Class<? extends AccountingDocument> documentClass, Class<T> businessRuleClass) throws Exception {
        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        final String documentTypeName = dataDictionaryService.getDocumentTypeNameByClass(documentClass);
        T businessRule = (T) dataDictionaryService.getDataDictionary().getDocumentEntry(documentTypeName).getBusinessRulesClass().newInstance();
        return businessRule;
    }
}
