<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
