<%--
 Copyright 2005-2007 The Kuali Foundation.
 
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

<%--
  This page exists as an antipattern because it was created while Kuali is still using
  Apache Tomcat 5.5 which complies with JSP Specification 2.0. Deferred Values offered
  in JSP Specification 2.1 makes this page obsolete.
  
  See http://java.sun.com/javaee/5/docs/tutorial/doc/JSPIntro7.html#wp101899 and
  http://java.sun.com/javaee/5/docs/tutorial/doc/JSPTags5.html#wp89718  
--%>

<%@ variable name-given="columnCount" %>
<%@ variable name-given="optionalFieldCount" %>
<%@ variable name-given="columnCountUntilAmount" %>
<%@ variable name-given="editableAccountsMap" %>
<c:set var="editableAccountsMap" value="${editableAccounts}"/>
<%@ variable name-given="editableFieldsMap" %>
<c:set var="editableFieldsMap" value="${editableFields}"/>
<%@ variable name-given="optionalFieldsMap" %>
<c:set var="optionalFieldsMap" value="${optionalFields}"/>
<%@ variable name-given="editingModeString" %>
<c:set var="editingModeString" value="${editingMode}"/>
<%@ variable name-given="debitCreditAmountString" %>
<c:set var="debitCreditAmountString" value="${debitCreditAmount}"/>
<%@ variable name-given="currentBaseAmountString" %>
<c:set var="currentBaseAmountString" value="${currentBaseAmount}"/>
<%@ variable name-given="extraSourceRowFieldsMap" %>
<c:set var="extraSourceRowFieldsMap" value="${extraSourceRowFields}"/>
<%@ variable name-given="extraTargetRowFieldsMap" %>
<c:set var="extraTargetRowFieldsMap" value="${extraTargetRowFields}"/>
<%@ variable name-given="extraHiddenFieldsMap" %>
<c:set var="extraHiddenFieldsMap" value="${extraHiddenFields}"/>
<%@ variable name-given="useCurrencyFormattedTotalBoolean" %>
<c:set var="useCurrencyFormattedTotalBoolean" value="${useCurrencyFormattedTotal}"/>
<%@ variable name-given="includeObjectTypeCodeBoolean" %>
<c:set var="includeObjectTypeCodeBoolean" value="${includeObjectTypeCode}"/>
<%@ variable name-given="displayMonthlyAmountsBoolean" %>
<c:set var="displayMonthlyAmountsBoolean" value="${displayMontlyAmounts}"/>
<%@ variable name-given="forcedReadOnlyFieldsMap" %>
<c:set var="forcedReadOnlyFieldsMap" value="${forcedReadOnlyFields}"/>
<%@ variable name-given="accountingLineAttributesMap" %>
<c:set var="accountingLineAttributesMap" value="${accountingLineAttributes}"/>
<%@ variable name-given="accountingLineScriptsLoaded" %>
