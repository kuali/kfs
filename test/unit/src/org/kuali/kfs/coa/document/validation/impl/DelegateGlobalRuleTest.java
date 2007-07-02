/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.chart.rules;

import java.util.ArrayList;
import java.util.List;

import org.kuali.module.chart.bo.DelegateGlobalDetail;
import org.kuali.test.WithTestSpringContext;

@WithTestSpringContext
public class DelegateGlobalRuleTest extends ChartRuleTestBase {

    private DelegateGlobalDetail delegateGlobal;
    private List<DelegateGlobalDetail> delegateGlobals;

    public void testCheckPrimaryRouteOnlyAllowOneAllDocType_InputValidations() {
        DelegateGlobalRule rule = new DelegateGlobalRule();
        delegateGlobal = null;
        delegateGlobals = null;
        Integer offendingLine = null;
        Integer result = rule.checkPrimaryRouteOnlyAllowOneAllDocType(delegateGlobal, delegateGlobals, null);
        assertNull(result);

        delegateGlobal = new DelegateGlobalDetail();
        delegateGlobals = null;
        offendingLine = null;
        result = rule.checkPrimaryRouteOnlyAllowOneAllDocType(delegateGlobal, delegateGlobals, null);
        assertNull(result);

        delegateGlobal = new DelegateGlobalDetail();
        delegateGlobals = new ArrayList<DelegateGlobalDetail>();
        offendingLine = null;
        result = rule.checkPrimaryRouteOnlyAllowOneAllDocType(delegateGlobal, delegateGlobals, null);
        assertNull(result);

        delegateGlobal = new DelegateGlobalDetail();
        delegateGlobal.setAccountDelegatePrimaryRoutingIndicator(false);
        delegateGlobal.setFinancialDocumentTypeCode("ALL");
        delegateGlobals = new ArrayList<DelegateGlobalDetail>();
        offendingLine = null;
        result = rule.checkPrimaryRouteOnlyAllowOneAllDocType(delegateGlobal, delegateGlobals, null);
        assertNull(result);

        delegateGlobal = new DelegateGlobalDetail();
        delegateGlobal.setAccountDelegatePrimaryRoutingIndicator(true);
        delegateGlobal.setFinancialDocumentTypeCode("A21");
        delegateGlobals = new ArrayList<DelegateGlobalDetail>();
        offendingLine = null;
        result = rule.checkPrimaryRouteOnlyAllowOneAllDocType(delegateGlobal, delegateGlobals, null);
        assertNull(result);

    }

    public void testCheckPrimaryRouteOnlyAllowOneAllDocType_NewLine_NoOtherPrimaryAll() {
        DelegateGlobalRule rule = new DelegateGlobalRule();
        delegateGlobals = new ArrayList<DelegateGlobalDetail>();
        DelegateGlobalDetail listItem;

        delegateGlobal = new DelegateGlobalDetail();
        delegateGlobal.setAccountDelegatePrimaryRoutingIndicator(true);
        delegateGlobal.setFinancialDocumentTypeCode("ALL");

        listItem = new DelegateGlobalDetail();
        listItem.setAccountDelegatePrimaryRoutingIndicator(false);
        listItem.setFinancialDocumentTypeCode("A21");
        delegateGlobals.add(listItem);

        Integer result = rule.checkPrimaryRouteOnlyAllowOneAllDocType(delegateGlobal, delegateGlobals, null);
        assertNull(result);
    }

    public void testCheckPrimaryRouteOnlyAllowOneAllDocType_NewLine_OtherPrimaryNotAll() {
        DelegateGlobalRule rule = new DelegateGlobalRule();
        delegateGlobals = new ArrayList<DelegateGlobalDetail>();
        DelegateGlobalDetail listItem;

        delegateGlobal = new DelegateGlobalDetail();
        delegateGlobal.setAccountDelegatePrimaryRoutingIndicator(true);
        delegateGlobal.setFinancialDocumentTypeCode("ALL");

        listItem = new DelegateGlobalDetail();
        listItem.setAccountDelegatePrimaryRoutingIndicator(true);
        listItem.setFinancialDocumentTypeCode("A21");
        delegateGlobals.add(listItem);

        Integer result = rule.checkPrimaryRouteOnlyAllowOneAllDocType(delegateGlobal, delegateGlobals, null);
        assertEquals(new Integer(0), result);
    }

    public void testCheckPrimaryRouteOnlyAllowOneAllDocType_ExistingLine_OtherPrimaryNotAll() {
        DelegateGlobalRule rule = new DelegateGlobalRule();
        delegateGlobals = new ArrayList<DelegateGlobalDetail>();
        DelegateGlobalDetail listItem;

        listItem = new DelegateGlobalDetail();
        listItem.setAccountDelegatePrimaryRoutingIndicator(true);
        listItem.setFinancialDocumentTypeCode("A21");
        delegateGlobals.add(listItem);

        listItem = new DelegateGlobalDetail();
        listItem.setAccountDelegatePrimaryRoutingIndicator(true);
        listItem.setFinancialDocumentTypeCode("ALL");
        delegateGlobals.add(listItem);

        Integer result = null;
        DelegateGlobalDetail delegateGlobal = null;

        delegateGlobal = delegateGlobals.get(0);
        result = rule.checkPrimaryRouteOnlyAllowOneAllDocType(delegateGlobal, delegateGlobals, 0);
        assertNull(result);

        delegateGlobal = delegateGlobals.get(1);
        result = rule.checkPrimaryRouteOnlyAllowOneAllDocType(delegateGlobal, delegateGlobals, 1);
        assertEquals(new Integer(0), result);

    }

    public void testCheckPrimaryRoutePerDocType_InputValidations() {
        DelegateGlobalRule rule = new DelegateGlobalRule();
        delegateGlobal = null;
        delegateGlobals = null;
        Integer offendingLine = null;
        Integer result = rule.checkPrimaryRoutePerDocType(delegateGlobal, delegateGlobals, null);
        assertNull(result);

        delegateGlobal = new DelegateGlobalDetail();
        delegateGlobals = null;
        offendingLine = null;
        result = rule.checkPrimaryRoutePerDocType(delegateGlobal, delegateGlobals, null);
        assertNull(result);

        delegateGlobal = new DelegateGlobalDetail();
        delegateGlobals = new ArrayList<DelegateGlobalDetail>();
        offendingLine = null;
        result = rule.checkPrimaryRoutePerDocType(delegateGlobal, delegateGlobals, null);
        assertNull(result);

        delegateGlobal = new DelegateGlobalDetail();
        delegateGlobal.setAccountDelegatePrimaryRoutingIndicator(false);
        delegateGlobal.setFinancialDocumentTypeCode("ALL");
        delegateGlobals = new ArrayList<DelegateGlobalDetail>();
        offendingLine = null;
        result = rule.checkPrimaryRoutePerDocType(delegateGlobal, delegateGlobals, null);
        assertNull(result);

        delegateGlobal = new DelegateGlobalDetail();
        delegateGlobal.setAccountDelegatePrimaryRoutingIndicator(true);
        delegateGlobal.setFinancialDocumentTypeCode("A21");
        delegateGlobals = new ArrayList<DelegateGlobalDetail>();
        offendingLine = null;
        result = rule.checkPrimaryRoutePerDocType(delegateGlobal, delegateGlobals, null);
        assertNull(result);

    }

    public void testCheckPrimaryRoutePerDocType_NewLine_NoOtherPrimariesWithSameDocType() {
        DelegateGlobalRule rule = new DelegateGlobalRule();
        delegateGlobals = new ArrayList<DelegateGlobalDetail>();
        DelegateGlobalDetail listItem;

        delegateGlobal = new DelegateGlobalDetail();
        delegateGlobal.setAccountDelegatePrimaryRoutingIndicator(true);
        delegateGlobal.setFinancialDocumentTypeCode("ABC");

        listItem = new DelegateGlobalDetail();
        listItem.setAccountDelegatePrimaryRoutingIndicator(true);
        listItem.setFinancialDocumentTypeCode("A21");
        delegateGlobals.add(listItem);

        Integer result = rule.checkPrimaryRoutePerDocType(delegateGlobal, delegateGlobals, null);
        assertNull(result);
    }

    public void testCheckPrimaryRoutePerDocType_NewLine_AddingIdenticalLine() {
        DelegateGlobalRule rule = new DelegateGlobalRule();
        delegateGlobals = new ArrayList<DelegateGlobalDetail>();
        DelegateGlobalDetail listItem;

        delegateGlobal = new DelegateGlobalDetail();
        delegateGlobal.setAccountDelegatePrimaryRoutingIndicator(true);
        delegateGlobal.setFinancialDocumentTypeCode("ABC");

        listItem = new DelegateGlobalDetail();
        listItem.setAccountDelegatePrimaryRoutingIndicator(true);
        listItem.setFinancialDocumentTypeCode("ABC");
        delegateGlobals.add(listItem);

        Integer result = rule.checkPrimaryRoutePerDocType(delegateGlobal, delegateGlobals, null);
        assertEquals(new Integer(0), result);
    }

    public void testCheckOnlyOnePrimaryRoute_NewLine_OnePrimaryWithSameDocType() {
        DelegateGlobalRule rule = new DelegateGlobalRule();
        delegateGlobals = new ArrayList<DelegateGlobalDetail>();
        DelegateGlobalDetail listItem;

        delegateGlobal = new DelegateGlobalDetail();
        delegateGlobal.setAccountDelegatePrimaryRoutingIndicator(true);
        delegateGlobal.setFinancialDocumentTypeCode("A21");

        listItem = new DelegateGlobalDetail();
        listItem.setAccountDelegatePrimaryRoutingIndicator(true);
        listItem.setFinancialDocumentTypeCode("ABC");
        delegateGlobals.add(listItem);

        listItem = new DelegateGlobalDetail();
        listItem.setAccountDelegatePrimaryRoutingIndicator(true);
        listItem.setFinancialDocumentTypeCode("A21");
        delegateGlobals.add(listItem);

        Integer result = rule.checkPrimaryRoutePerDocType(delegateGlobal, delegateGlobals, null);
        assertEquals(new Integer(1), result);
    }

}
