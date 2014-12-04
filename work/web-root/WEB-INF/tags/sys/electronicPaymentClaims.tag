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

<%@ attribute name="allowAdministration" required="false" description="Whether the current user is an electronic funds administrator." %>

<c:set var="electronicPaymentClaimAttributes" value="${DataDictionary.ElectronicPaymentClaim.attributes}" />
<c:set var="advanceDepositDocumentAttributes" value="${DataDictionary.AdvanceDepositDetail.attributes}" />
<c:set var="accountingLineAttributes" value="${DataDictionary.SourceAccountingLine.attributes}" />

<kul:tabTop tabTitle="Electronic Payments" defaultOpen="true" tabErrorKey="claims*">
  <div class="tab-container" align="center">
    <h3>Electronic Payments</h3>

    <table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
      <tr>
        <kul:htmlAttributeHeaderCell attributeEntry="${electronicPaymentClaimAttributes.documentNumber}" />
        <kul:htmlAttributeHeaderCell attributeEntry="${electronicPaymentClaimAttributes.fiscalDocumentLineNumber}" />
        <kul:htmlAttributeHeaderCell attributeEntry="${advanceDepositDetailAttributes.financialDocumentAdvanceDepositDate}" />
        <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.financialDocumentLineDescription}" />
        <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes.amount}" />
        <c:if test="${allowAdministration}">
          <th><bean:message key="${KualiForm.claimingDocumentNumberHeaderKey}" /></th>
          <th><bean:message key="${KualiForm.previouslyClaimedHeaderKey}" /></th>
        </c:if>
      </tr>
      <logic:iterate indexId="ctr" name="KualiForm" property="claims" id="currentLine">
        <sys:electronicPaymentClaim claimIndex="${ctr}" allowAdministration="${allowAdministration}" electronicPaymentClaim="${currentLine}" />
      </logic:iterate>
    </table>
  </div>
</kul:tabTop>
