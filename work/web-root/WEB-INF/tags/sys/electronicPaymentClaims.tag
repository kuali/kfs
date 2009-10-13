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
