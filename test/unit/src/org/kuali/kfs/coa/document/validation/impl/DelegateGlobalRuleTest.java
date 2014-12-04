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
package org.kuali.kfs.coa.document.validation.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.coa.businessobject.AccountDelegateGlobalDetail;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;

@ConfigureContext
public class DelegateGlobalRuleTest extends ChartRuleTestBase {

    private AccountDelegateGlobalDetail delegateGlobal;
    private List<AccountDelegateGlobalDetail> delegateGlobals;

    public void testCheckPrimaryRoutePerDocType_InputValidations() {
        DelegateGlobalRule rule = new DelegateGlobalRule();
        delegateGlobal = null;
        delegateGlobals = null;
        Integer offendingLine = null;
        Integer result = rule.checkPrimaryRoutePerDocType(delegateGlobal, delegateGlobals, null);
        assertNull(result);

        delegateGlobal = new AccountDelegateGlobalDetail();
        delegateGlobals = null;
        offendingLine = null;
        result = rule.checkPrimaryRoutePerDocType(delegateGlobal, delegateGlobals, null);
        assertNull(result);

        delegateGlobal = new AccountDelegateGlobalDetail();
        delegateGlobals = new ArrayList<AccountDelegateGlobalDetail>();
        offendingLine = null;
        result = rule.checkPrimaryRoutePerDocType(delegateGlobal, delegateGlobals, null);
        assertNull(result);

        delegateGlobal = new AccountDelegateGlobalDetail();
        delegateGlobal.setAccountDelegatePrimaryRoutingIndicator(false);
        delegateGlobal.setFinancialDocumentTypeCode(KFSConstants.ROOT_DOCUMENT_TYPE);
        delegateGlobals = new ArrayList<AccountDelegateGlobalDetail>();
        offendingLine = null;
        result = rule.checkPrimaryRoutePerDocType(delegateGlobal, delegateGlobals, null);
        assertNull(result);

        delegateGlobal = new AccountDelegateGlobalDetail();
        delegateGlobal.setAccountDelegatePrimaryRoutingIndicator(true);
        delegateGlobal.setFinancialDocumentTypeCode("A21");
        delegateGlobals = new ArrayList<AccountDelegateGlobalDetail>();
        offendingLine = null;
        result = rule.checkPrimaryRoutePerDocType(delegateGlobal, delegateGlobals, null);
        assertNull(result);

    }

    public void testCheckPrimaryRoutePerDocType_NewLine_NoOtherPrimariesWithSameDocType() {
        DelegateGlobalRule rule = new DelegateGlobalRule();
        delegateGlobals = new ArrayList<AccountDelegateGlobalDetail>();
        AccountDelegateGlobalDetail listItem;

        delegateGlobal = new AccountDelegateGlobalDetail();
        delegateGlobal.setAccountDelegatePrimaryRoutingIndicator(true);
        delegateGlobal.setFinancialDocumentTypeCode("ABC");

        listItem = new AccountDelegateGlobalDetail();
        listItem.setAccountDelegatePrimaryRoutingIndicator(true);
        listItem.setFinancialDocumentTypeCode("A21");
        delegateGlobals.add(listItem);

        Integer result = rule.checkPrimaryRoutePerDocType(delegateGlobal, delegateGlobals, null);
        assertNull(result);
    }

    public void testCheckPrimaryRoutePerDocType_NewLine_AddingIdenticalLine() {
        DelegateGlobalRule rule = new DelegateGlobalRule();
        delegateGlobals = new ArrayList<AccountDelegateGlobalDetail>();
        AccountDelegateGlobalDetail listItem;

        delegateGlobal = new AccountDelegateGlobalDetail();
        delegateGlobal.setAccountDelegatePrimaryRoutingIndicator(true);
        delegateGlobal.setFinancialDocumentTypeCode("ABC");

        listItem = new AccountDelegateGlobalDetail();
        listItem.setAccountDelegatePrimaryRoutingIndicator(true);
        listItem.setFinancialDocumentTypeCode("ABC");
        delegateGlobals.add(listItem);

        Integer result = rule.checkPrimaryRoutePerDocType(delegateGlobal, delegateGlobals, null);
        assertEquals(new Integer(0), result);
    }

    public void testCheckOnlyOnePrimaryRoute_NewLine_OnePrimaryWithSameDocType() {
        DelegateGlobalRule rule = new DelegateGlobalRule();
        delegateGlobals = new ArrayList<AccountDelegateGlobalDetail>();
        AccountDelegateGlobalDetail listItem;

        delegateGlobal = new AccountDelegateGlobalDetail();
        delegateGlobal.setAccountDelegatePrimaryRoutingIndicator(true);
        delegateGlobal.setFinancialDocumentTypeCode("A21");

        listItem = new AccountDelegateGlobalDetail();
        listItem.setAccountDelegatePrimaryRoutingIndicator(true);
        listItem.setFinancialDocumentTypeCode("ABC");
        delegateGlobals.add(listItem);

        listItem = new AccountDelegateGlobalDetail();
        listItem.setAccountDelegatePrimaryRoutingIndicator(true);
        listItem.setFinancialDocumentTypeCode("A21");
        delegateGlobals.add(listItem);

        Integer result = rule.checkPrimaryRoutePerDocType(delegateGlobal, delegateGlobals, null);
        assertEquals(new Integer(1), result);
    }

}
