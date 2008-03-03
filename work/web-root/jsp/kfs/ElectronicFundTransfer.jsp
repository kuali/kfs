<%--
 Copyright 2006-2007 The Kuali Foundation.
 
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

<kul:page headerTitle="Electronic Payment Claiming" transactionalDocument="false" showDocumentInfo="false" htmlFormAction="electronicFundTransfer" docTitle="Electronic Payments to Claim">
  <fin:electronicPaymentClaims allowAdministration="${KualiForm.allowElectronicFundsTransferAdministration}" />
  <kul:tab tabTitle="Claiming Document" defaultOpen="true" tabErrorKey="chosenElectronicPaymentClaimingDocumentCode">
    <div class="tab-container" align=center>
      <div class="h2-container"><h2>Claiming Document Type</h2></div>
      <p>
        Please enter the type of document you would like to use to claim the Electronic Payment records you have chosen.
      </p>
      <p align="center">
        <c:forEach var="docType" items="${KualiForm.availableClaimingDocuments}">
          <input type="radio" id="chosenElectronicPaymentClaimingDocumentCode${docType.documentCode}" name="chosenElectronicPaymentClaimingDocumentCode" value="${docType.documentCode}"<c:if test="${KualiForm.chosenElectronicPaymentClaimingDocumentCode == docType.documentCode}"> checked="checked"</c:if> /><label for="chosenElectronicPaymentClaimingDocumentCode${docType.documentCode}">${docType.documentLabel}</label>&nbsp;
        </c:forEach>
      </p>
    </div>
  </kul:tab>
  <kul:tab tabTitle="Documentation" defaultOpen="true" tabErrorKey="hasDocumentation">
    <div class="tab-container" align=center>
      <p>
        <bean:message key="${KualiForm.documentationMessageKey}" />
      </p>
      <p align="center">
        <input type="radio" id="hasDocumentationYes" name="hasDocumentation" value="Yep"<c:if test="${KualiForm.properlyDocumented}"> checked="checked"</c:if> /><label for="hasDocumentationYes">Yes</label>&nbsp;<input type="radio" id="hasDocumentationNo" name="hasDocumentation" value="Nope"<c:if test="${not KualiForm.properlyDocumented}"> checked="checked"</c:if> /><label for="hasDocumentationNo">No</label>
      </p>
    </div>
  </kul:tab>
  <kul:panelFooter />
  <div id="globalbuttons" class="globalbuttons">
    <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_claim.gif" styleClass="globalbuttons" property="methodToCall.claim" title="claim" alt="claim"/>
    <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif" styleClass="globalbuttons" property="methodToCall.cancel" title="cancel" alt="cancel"/>
  </div>
</kul:page>