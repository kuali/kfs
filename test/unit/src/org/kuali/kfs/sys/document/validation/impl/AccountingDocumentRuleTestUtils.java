/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.validation.impl;

import static org.kuali.kfs.sys.KualiTestAssertionUtils.assertGlobalErrorMapEmpty;
import static org.kuali.kfs.sys.KualiTestAssertionUtils.assertSparselyEqualBean;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.AddAccountingLineRule;
import org.kuali.kfs.sys.fixture.GeneralLedgerPendingEntryFixture;
import org.kuali.rice.kns.rule.BusinessRule;
import org.kuali.rice.kns.rule.RouteDocumentRule;
import org.kuali.rice.kns.rule.SaveDocumentRule;
import org.kuali.rice.kns.service.DataDictionaryService;

public abstract class AccountingDocumentRuleTestUtils extends KualiTestBase {

    // test methods
    public static void testAddAccountingLineRule_IsObjectTypeAllowed(Class<? extends AccountingDocument> documentClass, AccountingLine line, boolean expected) throws Exception {
        AddAccountingLineRule rule = getBusinessRule(documentClass, AddAccountingLineRule.class);
        assertEquals(expected, rule.isObjectTypeAllowed(documentClass, line));
    }

    public static void testAddAccountingLineRule_IsObjectCodeAllowed(Class<? extends AccountingDocument> documentClass, AccountingLine line, boolean expected) throws Exception {
        AddAccountingLineRule rule = getBusinessRule(documentClass, AddAccountingLineRule.class);
        assertEquals(expected, rule.isObjectCodeAllowed(documentClass, line));
    }

    public static void testAddAccountingLine_IsObjectSubTypeAllowed(Class<? extends AccountingDocument> documentClass, AccountingLine line, boolean expected) throws Exception {
        AddAccountingLineRule rule = getBusinessRule(documentClass, AddAccountingLineRule.class);
        assertEquals(expected, rule.isObjectSubTypeAllowed(documentClass, line));
    }

    public static <T extends AccountingDocument> void testAddAccountingLineRule_ProcessAddAccountingLineBusinessRules(T document, boolean expected) throws Exception {
        // Check business rules
        List<? extends AccountingLine> allLines = new ArrayList<AccountingLine>();
        allLines.addAll(document.getSourceAccountingLines());
        allLines.addAll(document.getTargetAccountingLines());

        assertGlobalErrorMapEmpty();
        AddAccountingLineRule rule = getBusinessRule(document.getClass(), AddAccountingLineRule.class);
        for (AccountingLine accountingLine : allLines) {
            String collectionName = null;
            if(accountingLine instanceof SourceAccountingLine){
                collectionName = KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME;
            }else if(accountingLine instanceof TargetAccountingLine){
                collectionName = KFSConstants.NEW_TARGET_ACCT_LINE_PROPERTY_NAME;
            }
            boolean ruleResult = rule.processAddAccountingLineBusinessRules(document, accountingLine, collectionName);
            if (expected) {
                assertGlobalErrorMapEmpty(accountingLine.toString());
            }
            assertEquals(expected, ruleResult);
        }
    }

    public static <T extends AccountingDocument> void testSaveDocumentRule_ProcessSaveDocument(T document, boolean expected) throws Exception {
        SaveDocumentRule rule = getBusinessRule(document.getClass(), SaveDocumentRule.class);
        boolean rulePassed = rule.processSaveDocument(document);
        if (expected) {
            assertGlobalErrorMapEmpty();
        }
        assertEquals(expected, rulePassed);
    }

    public static <T extends AccountingDocument> void testRouteDocumentRule_processRouteDocument(T document, boolean expected) throws Exception {
        RouteDocumentRule rule = getBusinessRule(document.getClass(), RouteDocumentRule.class);
        boolean rulePassed = rule.processRouteDocument(document);
        if (expected) {
            assertGlobalErrorMapEmpty();
        }
        assertEquals(expected, rulePassed);
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
    public static <T extends BusinessRule> T getBusinessRule(Class<? extends AccountingDocument> documentClass, Class<T> businessRuleClass) throws Exception {
        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        final String documentTypeName = dataDictionaryService.getDocumentTypeNameByClass(documentClass);
        T businessRule = (T) dataDictionaryService.getDataDictionary().getDocumentEntry(documentTypeName).getBusinessRulesClass().newInstance();
        return businessRule;
    }
}
