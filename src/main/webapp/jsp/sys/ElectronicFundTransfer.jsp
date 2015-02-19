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
