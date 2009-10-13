<%--
 Copyright 2006-2008 The Kuali Foundation
 
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

<script language="javascript">
  function toggleclaimedcheckbox(lineNbr) {
    if (document.getElementById) {
      var claimRefNbr = document.getElementById("claim["+lineNbr+"].referenceFinancialDocumentNumber");
      if (claimRefNbr && claimRefNbr.value) {
        // check the related claimed checkbox
        var claimCheckboxName = "claimedByCheckboxHelper["+lineNbr+"].electronicPaymentClaimRepresentation";
        var claimCheckbox = document.forms[0][claimCheckboxName];
        claimCheckbox.checked = true;
      }
    }
  }
</script>

<kul:page headerTitle="Electronic Payment Claiming" transactionalDocument="false" showDocumentInfo="false" htmlFormAction="electronicFundTransfer" docTitle="Electronic Payments to Claim">
  <sys:electronicPaymentClaims allowAdministration="${KualiForm.allowElectronicFundsTransferAdministration}" />
  <kul:tab tabTitle="Claiming Document" defaultOpen="true" tabErrorKey="chosenElectronicPaymentClaimingDocumentCode">
    <div class="tab-container" align=center>
      <h3>Claiming Document Type</h3>
      <table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
        <tr>
          <td>
            <p>
              <bean:message key="${KualiForm.documentChoiceMessageKey}" />
            </p>
            <p align="center">
              <c:forEach var="docType" items="${KualiForm.availableClaimingDocumentStrategies}">
                <c:set var="docTypeFieldName" value="chosenElectronicPaymentClaimingDocumentCode" />
                <c:set var="docTypeFieldId" value="${docTypeFieldName}${docType.claimingDocumentWorkflowDocumentType}"/>
              	${kfunc:registerEditableProperty(KualiForm, docTypeFieldName)}
              	
              	<c:set var="checked" value="${KualiForm.chosenElectronicPaymentClaimingDocumentCode == docType.claimingDocumentWorkflowDocumentType? 'checked' : ''}" />
                <input type="radio" id="${docTypeFieldId}" name="${docTypeFieldName}" value="${docType.claimingDocumentWorkflowDocumentType}" ${checked} />
                <label for="${docTypeFieldId}">${docType.documentLabel}</label>&nbsp;
              </c:forEach>
            </p>
          </td>
        </tr>
      </table>
    </div>
  </kul:tab>
  <kul:tab tabTitle="Documentation" defaultOpen="true" tabErrorKey="hasDocumentation">
    <div class="tab-container" align=center>
      <table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
        <tr>
          <td>
            <p>
              <bean:message key="${KualiForm.documentationMessageKey}" />
            </p>
            <p align="center">
              <c:set var="questionFieldName" value="hasDocumentation" />
              ${kfunc:registerEditableProperty(KualiForm, questionFieldName)}
              <input type="radio" id="hasDocumentationYes" name="${questionFieldName}" value="Yep"<c:if test="${KualiForm.properlyDocumented}"> checked="checked"</c:if> /><label for="hasDocumentationYes">Yes</label>&nbsp;
              <input type="radio" id="hasDocumentationNo" name="${questionFieldName}" value="Nope"<c:if test="${not KualiForm.properlyDocumented}"> checked="checked"</c:if> /><label for="hasDocumentationNo">No</label>
            </p>
          </td>
        </tr>
      </table>
    </div>
  </kul:tab>
  <kul:panelFooter />
  <div id="globalbuttons" class="globalbuttons">
    <html:image src="${ConfigProperties.externalizable.images.url}buttonsmall_claim.gif" styleClass="globalbuttons" property="methodToCall.claim" title="claim" alt="claim"/>
    <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif" styleClass="globalbuttons" property="methodToCall.cancel" title="cancel" alt="cancel"/>
  </div>
</kul:page>
