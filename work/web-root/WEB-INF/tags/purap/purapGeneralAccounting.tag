<%--
 Copyright 2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<%@ attribute name="optionalFields" required="false"
    description="A comma separated list of names of accounting line fields
              to be appended to the required field columns, before the amount column.
              The optional columns appear in both source and target groups
              of accounting lines."%>
<%@ attribute name="isOptionalFieldsInNewRow" required="false" type="java.lang.Boolean" description="indicate if the oprtional fields are put in a new row under the default accouting line"%>

<%@ attribute name="sourceAccountingLinesOnly" required="false" description="A boolean controling whether the group of
              target accounting lines will be generated.
              Null or empty is the same as false."%>
<%@ attribute name="extraSourceRowFields" required="false" description="A comma seperated list of names of any non-standard fields
              required on the source accounting lines of this eDoc.
              See accountingLineRow.tag for details."%>
<%@ attribute name="extraTargetRowFields" required="false" description="A comma seperated list of names of any non-standard fields
              required on the target accounting lines of this eDoc.
              See accountingLineRow.tag for details."%>
<%@ attribute name="editingMode" required="false" type="java.util.Map"%>
<%@ attribute name="editableAccounts" required="true" type="java.util.Map" description="Map of Accounts which this user is allowed to edit"%>
<%@ attribute name="editableFields" required="false" type="java.util.Map" description="Map of accounting line fields which this user is allowed to edit"%>
<%@ attribute name="useCurrencyFormattedTotal" required="false"
    description="boolean indicating that the form's currency formatted total
              should be displayed instead of the document's source or target total.
              As with all boolean tag attributes, if it is not provided, it defaults to false."%>

<%@ attribute name="includeObjectTypeCode" required="false" description="boolean indicating that the object type code column should be displayed.
              As with all boolean tag attributes, if it is not provided, it defaults to false."%>

<%@ attribute name="debitCreditAmount" required="false"
    description="boolean whether the amount column is displayed as
              separate debit and credit columns, and the totals as the form's
              currency formatted debit and credit totals.
              As with all boolean tag attributes, if it is not provided, it defaults to false."%>

<%@ attribute name="currentBaseAmount" required="false"
    description="boolean whether the amount column is displayed as
              separate debit and credit columns, and the totals as the form's
              currency formatted debit and credit totals.
              As with all boolean tag attributes, if it is not provided, it defaults to false."%>

<%@ attribute name="extraHiddenFields" required="false"
    description="A comma seperated list of names of any accounting line fields
              that should be added to the list of normally hidden fields
              for the existing (but not the new) accounting lines."%>

<%@ attribute name="displayMonthlyAmounts" required="false"
    description="A boolean whether the monthy amounts table is displayed
              below each accounting line (needed for budget adjustment document).
              As with all boolean tag attributes, if it is not provided, it defaults to false."%>

<%@ attribute name="forcedReadOnlyFields" required="false" type="java.util.Map" description="map containing accounting line field names that should be marked as read only."%>
<%@ attribute name="accountingLineAttributes" required="false" type="java.util.Map" description="A parameter to specify an data dictionary entry for a sub-classed accounting line."%>
<%@ attribute name="itemsAttributes" required="false" type="java.util.Map" description="A parameter to specify an data dictionary entry for items to get cams fields."%>
<%@ attribute name="inherit" required="false" type="java.lang.Boolean" description="Should the default Financial Transactions Accounting Line tags be used?"%>
<%@ attribute name="groupsOverride" required="false" fragment="true" description="Fragment of code to override the default accountingline groups"%>
<%@ attribute name="accountPrefix" required="false" description="an optional prefix to specify a different location for acocunting lines rather than just on the document."%>
<%@ attribute name="hideTotalLine" required="false" type="java.lang.Boolean" description="an optional attribute to hide the total line."%>
<%@ attribute name="hideFields" required="false" description="comma delimited list of fields to hide for this type of accounting line"%>
<%@ attribute name="accountingAddLineIndex" required="false" description="index for multiple add new source lines"%>
<%@ attribute name="ctr" required="true" description="item count"%>

    <c:if test="${!accountingLineScriptsLoaded}">
        <script type='text/javascript' src="dwr/interface/ChartService.js"></script>
        <script type='text/javascript' src="dwr/interface/AccountService.js"></script>
        <script type='text/javascript' src="dwr/interface/SubAccountService.js"></script>
        <script type='text/javascript' src="dwr/interface/ObjectCodeService.js"></script>
        <script type='text/javascript' src="dwr/interface/ObjectTypeService.js"></script>
        <script type='text/javascript' src="dwr/interface/SubObjectCodeService.js"></script>
        <script type='text/javascript' src="dwr/interface/ProjectCodeService.js"></script>
        <script type='text/javascript' src="dwr/interface/OriginationCodeService.js"></script>
        <script type='text/javascript' src="dwr/interface/DocumentTypeService.js"></script>
        <script language="JavaScript" type="text/javascript" src="scripts/kfs/objectInfo.js"></script>
        <c:set var="accountingLineScriptsLoaded" value="true" scope="request" />
    </c:if>
    
    <c:forEach items="${editableAccounts}" var="account">
        <html:hidden property="editableAccounts(${account.key})" value="${account.key}" />
    </c:forEach>
    
    <c:forEach items="${editableFields}" var="field">
        <html:hidden property="accountingLineEditableFields(${field.key})" />
    </c:forEach>
    
    <c:set var="optionalFieldCount" value="${empty optionalFields ? 0 : fn:length(fn:split(optionalFields, ' ,'))}" />
    <c:set var="columnCountUntilAmount" value="${6 + (includeObjectTypeCode ? 1 : 0) + (isOptionalFieldsInNewRow ? 0 : optionalFieldCount)}" />
    <c:set var="arrHideFields" value="${fn:split(hideFields,',') }" />
    <c:set var="numHideFields" value="${fn:length(numHideFields) }" />
    <%-- add extra columns count for the "Action" button and/or dual amounts --%>
    <c:set var="columnCount" value="${columnCountUntilAmount + (debitCreditAmount || currentBaseAmount ? 2 : 1) - (not empty hideFields ? 0 : numHideFields) + (empty editingMode['viewOnly'] ? 0 : 1)}" />
    <%@ include file="/WEB-INF/tags/fin/accountingLinesVariablesOverride.tag"%>
    
    <c:set var="currentTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
    <c:set var="topLevelTabIndex" value="${KualiForm.currentTabIndex}" scope="request" />
    <c:set var="tabTitle" value="AccountingLines-${currentTabIndex}" />
    <c:set var="tabKey" value="${kfunc:generateTabKey(tabTitle)}"/>
    <!--  hit form method to increment tab index -->
    <c:set var="dummyIncrementer" value="${kfunc:incrementTabIndex(KualiForm, tabKey)}" />
    <c:set var="currentTab" value="${kfunc:getTabState(KualiForm, tabKey)}"/>

    <%-- default to closed --%>
    <c:choose>
        <c:when test="${empty currentTab}">
            <c:set var="isOpen" value="false" />
        </c:when>
        <c:when test="${!empty currentTab}">
            <c:set var="isOpen" value="${currentTab == 'OPEN'}" />
        </c:when>
    </c:choose>
	
    <html:hidden property="tabStates(${tabKey})" value="${(isOpen ? 'OPEN' : 'CLOSE')}" />

	<tr>
	<td colspan="${columnCount}">
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>

    <th colspan="${columnCount}" style="padding: 0px; border-right: none;">
        <div align=left>
      	    <c:if test="${isOpen == 'true' || isOpen == 'TRUE'}">
	            <html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-hide.gif" alt="hide" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle" onclick="javascript: return toggleTab(document, '${tabKey}'); " />
	        </c:if>
	        <c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
	            <html:image property="methodToCall.toggleTab.tab${tabKey}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-show.gif" alt="show" title="toggle" styleClass="tinybutton" styleId="tab-${tabKey}-imageToggle" onclick="javascript: return toggleTab(document, '${tabKey}'); " />
	        </c:if>
        	Accounting Lines
        </div>
    </th>
</tr>


<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
    <tr style="display: none;"  id="tab-${tabKey}-div">
</c:if>   
        <th colspan="${columnCount}" style="padding:0;">
            <purap:puraccountingLines editingMode="${editingMode}" editableAccounts="${KualiForm.editableAccounts}" sourceAccountingLinesOnly="${sourceAccountingLinesOnly}" optionalFields="${optionalFields}" extraHiddenFields="${extraHiddenFields}"
                accountingLineAttributes="${accountingLineAttributes}" accountPrefix="${accountPrefix}" hideTotalLine="${hideTotalLine}" hideFields="${hideFields}" accountingAddLineIndex="${accountingAddLineIndex}" />
        </th>
    
<c:if test="${isOpen != 'true' && isOpen != 'TRUE'}">
    </tr>
</c:if>

</table>
</td>
</tr>



