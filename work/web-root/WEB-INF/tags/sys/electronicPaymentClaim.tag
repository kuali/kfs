<%--
 Copyright 2007-2009 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="claimIndex" required="true" description="The index of the org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim in the form to display." %>
<%@ attribute name="electronicPaymentClaim" required="true" description="The ElectronicPaymentClaim being displayed" type="org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim" %>
<%@ attribute name="allowAdministration" required="false" description="Whether the current user is an electronic funds administrator." %>
<c:set var="accessibleTitle"><bean:message key="${KualiForm.previouslyClaimedHeaderKey}" /></c:set>

<c:set var="claimProperty" value="claim[${claimIndex}]" />

<tr>
  <td>
    <html:hidden property="${claimProperty}.documentNumber" write="true" />
    <html:hidden property="${claimProperty}.versionNumber" write="false" />
    <html:hidden property="${claimProperty}.objectId" write="false" />
    <html:hidden property="${claimProperty}.financialDocumentPostingYear" write="false" />
    <html:hidden property="${claimProperty}.financialDocumentPostingPeriodCode" write="false" />
  </td>
  <td>
    <html:hidden property="${claimProperty}.financialDocumentLineNumber" write="true" />
  </td>
  <td>
    <html:hidden property="${claimProperty}.generatingDocument.documentHeader.documentFinalDate" write="true" />
  </td>
  <td>
    <html:hidden property="${claimProperty}.generatingAccountingLine.financialDocumentLineDescription" write="true" />
  </td>
  <td>
    <html:hidden property="${claimProperty}.generatingAccountingLine.amount" write="true" />
  </td>
  <c:if test="${allowAdministration}">
    <td>
      <kul:htmlControlAttribute property="${claimProperty}.referenceFinancialDocumentNumber" attributeEntry="${DataDictionary.ElectronicPaymentClaim.attributes.referenceFinancialDocumentNumber}" onblur="javascript:toggleclaimedcheckbox(${claimIndex})" />
    </td>
    <td>
      <html:checkbox title="${accessibleTitle}" property="claimedByCheckboxHelper[${claimIndex}].electronicPaymentClaimRepresentation" value="${electronicPaymentClaim.electronicPaymentClaimRepresentation}" />
    </td>
  </c:if>
</tr>
