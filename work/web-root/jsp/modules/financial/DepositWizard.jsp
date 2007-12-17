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

<c:set var="rawDepositTypeCode" value="${KualiForm.depositTypeCode}" />
<c:set var="docTitle"
	value="Create a New ${KualiForm.depositTypeString} Deposit" />

<c:set var="depositAttributes"
	value="${DataDictionary.Deposit.attributes}" />
<c:set var="cashReceiptAttributes"
	value="${DataDictionary.CashReceiptDocument.attributes}" />
<c:set var="checkAttributes"
	value="${DataDictionary.CheckBase.attributes}" />
<c:set var="dummyAttributes"
	value="${DataDictionary.AttributeReferenceDummy.attributes}" />


<kul:page showDocumentInfo="false" showTabButtons="false"
	headerTitle="${docTitle}" docTitle="${docTitle}"
	transactionalDocument="false" htmlFormAction="depositWizard">
	<script type="text/javascript">
function checkCRAllOrNone() {
  var masterCRCheckBox = document.getElementById('masterCRCheckBox');
  if (masterCRCheckBox) {
    for(var i = 0; i < document.KualiForm.elements.length; i++) {
      var e = document.KualiForm.elements[i];
      if((e.name.match(/^depositWizardHelper/)) && (e.type == 'checkbox') && (!e.disabled)) {
        e.checked = masterCRCheckBox.checked;
      }
    }
  }
}

function checkCheckAllOrNone() {
  var masterCheckCheckBox = document.getElementById('masterCheckCheckBox');
  if (masterCheckCheckBox) {
    for(var i = 0; i < document.KualiForm.elements.length; i++) {
      var e = document.KualiForm.elements[i];
      if((e.name.match(/^depositWizardCashieringCheckHelper/)) && (e.type == 'checkbox') && (!e.disabled)) {
        e.checked = masterCheckCheckBox.checked;
      }
    }
  }
}
</script>
	<script type='text/javascript' src="dwr/interface/BankService.js"></script>
	<script type='text/javascript' src="dwr/interface/BankAccountService.js"></script>
	<script type='text/javascript' src="scripts/financial/objectInfo.js"></script>

	<html:hidden property="cashDrawerVerificationUnit" />
	<html:hidden property="cashManagementDocId" />
	<html:hidden property="depositTypeCode" />

	<c:if test="${!empty KualiForm.depositableCashReceipts || !empty KualiForm.depositableCashieringChecks || !empty KualiForm.checkFreeCashReceipts}">
		<kul:tabTop tabTitle="Deposit Header" defaultOpen="true"
			tabErrorKey="depositHeaderErrors">
			<div class="tab-container" align=center>
			<div class="h2-container">
			<h2>Deposit Header</h2>
			</div>

			<!-- deposit header fields -->
			<div id="workarea">
			<table cellpadding="0" cellspacing="0" class="datatable"
				summary="deposit header info">
				<tr>
					<kul:htmlAttributeHeaderCell labelFor="bankCode"
						attributeEntry="${depositAttributes.depositBankCode}"
						horizontal="true" align="left" />
					<kul:htmlAttributeHeaderCell labelFor="bankAccountNumber"
						attributeEntry="${depositAttributes.depositBankAccountNumber}"
						align="left" />
					<kul:htmlAttributeHeaderCell labelFor="depositTypeCode"
						attributeEntry="${depositAttributes.depositTypeCode}"
						hideRequiredAsterisk="true" horizontal="true" align="left" />
					<kul:htmlAttributeHeaderCell labelFor="depositTicketNumber"
						attributeEntry="${depositAttributes.depositTicketNumber}"
						hideRequiredAsterisk="true" horizontal="true" align="left" />
				</tr>
				<tr>
					<td class="infoline"><kul:htmlControlAttribute property="bankCode"
						attributeEntry="${depositAttributes.depositBankCode}"
						onblur="loadBankInfo(document.forms['KualiForm'], 'bankCode', 'bank');" />
					<c:if test="${!readOnly}">
						<kul:lookup boClassName="org.kuali.module.financial.bo.Bank"
							fieldConversions="financialDocumentBankCode:bankCode" />
					</c:if> <br />
					<div id="bank.div" class="fineprint"><bean:write name="KualiForm"
						property="bankAccount.bank.financialDocumentBankShortNm" />&nbsp;
					</div>
					</td>
					<td class="infoline"><kul:htmlControlAttribute
						property="bankAccountNumber"
						attributeEntry="${depositAttributes.depositBankAccountNumber}"
						onblur="loadBankAccountInfo(document.forms['KualiForm'], 'bankCode', 'bankAccountNumber', 'bankAccount' );" />
					<c:if test="${!readOnly}">
						<kul:lookup
							boClassName="org.kuali.module.financial.bo.BankAccount"
							fieldConversions="financialDocumentBankCode:bankCode,finDocumentBankAccountNumber:bankAccountNumber"
							lookupParameters="bankCode:financialDocumentBankCode" />
					</c:if> <br />
					<div id="bankAccount.div" class="fineprint"><bean:write
						name="KualiForm" property="bankAccount.finDocumentBankAccountDesc" />&nbsp;
					</div>
					</td>
					<td class="infoline"><kul:htmlControlAttribute
						property="depositTypeCode"
						attributeEntry="${depositAttributes.depositTypeCode}"
						readOnly="true" /> <br />
					&nbsp;</td>
					<td class="infoline"><kul:htmlControlAttribute
						property="depositTicketNumber"
						attributeEntry="${depositAttributes.depositTicketNumber}" /> <br />
					&nbsp;</td>
				</tr>
			</table>
			</div>
			</div>
		</kul:tabTop>
    
    <c:if test="${KualiForm.depositFinal}">
      <kul:tab tabTitle="Currency and Coin Detail" defaultOpen="true">
        <div class="tab-container" align="center">
          <div class="h2-container">
            <h2>Currency and Coin Detail</h2>
          </div>
          <fin:currencyCoinLine currencyProperty="currencyDetail" coinProperty="coinDetail" readOnly="false" />
        </div>
      </kul:tab>
    </c:if>

    <c:set var="crCounter" value="0" />
    <c:if test="${!empty KualiForm.depositableCashReceipts || !empty KualiForm.checkFreeCashReceipts}">
		<kul:tab tabTitle="Cash Receipts" defaultOpen="true"
			tabErrorKey="cashReceiptErrors">
			<div class="tab-container" align="center">
			<div width="100%" align="left"
				style="padding-left: 10px; padding-bottom: 10px"><b>Please select
			the Cash Receipt documents that you would like to deposit.</b></div>
			<div class="h2-container">
			<h2>Cash Receipts Available for Deposit</h2>
			</div>
			<div id="workarea">
			<table cellpadding="0" cellspacing="0" class="datatable"
				summary="cash receipts available for deposit">
				<tr>
					<td>
					<div align="center"><input type="checkbox" name="masterCRCheckBox"
						onclick="checkCRAllOrNone();" id="masterCRCheckBox" /></div>
					</td>
					<kul:htmlAttributeHeaderCell literalLabel="#" scope="col" />
					<kul:htmlAttributeHeaderCell literalLabel="Document Number"
						scope="col" />
					<kul:htmlAttributeHeaderCell literalLabel="Description" scope="col" />
					<kul:htmlAttributeHeaderCell literalLabel="Create Date" scope="col" />
					<kul:htmlAttributeHeaderCell literalLabel="Check Total" scope="col" />
					
				</tr>

      <c:if test="${!empty KualiForm.depositableCashReceipts}">
				<logic:iterate name="KualiForm" id="cashReceipt"
					property="depositableCashReceipts" indexId="ctr">
          <c:set var="crCounter" value="${crCounter + 1}" />
					<tr>
						<td colspan="7"
							style="background-color: gray; border-bottom: 1px solid gray; padding: 0px"><img
							src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="1" height="1" /></td>
					</tr>

					<tr>
						<td>
						<div align="center"><html:checkbox
							property="depositWizardHelper[${ctr}].selectedValue"
							value="${cashReceipt.documentNumber}" /></div>
						</td>
						<td>
						<div align="center"><b>${(ctr + 1)}</b></div>
						</td>
						<td>
						<div align="center"><a
							href="financialCashReceipt.do?methodToCall=docHandler&docId=${cashReceipt.documentHeader.documentNumber}&command=displayDocSearchView"
							target="new"> <kul:htmlControlAttribute
							property="depositableCashReceipt[${ctr}].documentNumber"
							attributeEntry="${cashReceiptAttributes.documentNumber}"
							readOnly="true" /> </a> <html:hidden
							property="depositableCashReceipt[${ctr}].documentHeader.documentNumber" />
						</div>
						</td>
						<td>
						<div align="center"><kul:htmlControlAttribute
							property="depositableCashReceipt[${ctr}].documentHeader.financialDocumentDescription"
							attributeEntry="${cashReceiptAttributes.financialDocumentDescription}"
							readOnly="true" /></div>
						</td>
						<td>
						<div align="center"><kul:htmlControlAttribute
							property="depositWizardHelper[${ctr}].cashReceiptCreateDate"
							attributeEntry="${dummyAttributes.genericTimestamp}"
							readOnly="true" /></div>
						</td>
						<td>
						<div align="center">
						$&nbsp;${cashReceipt.currencyFormattedTotalCheckAmount} <html:hidden
							property="depositableCashReceipt[${ctr}].totalCheckAmount" /></div>
						</td>
						
					</tr>

					<c:if test="${cashReceipt.checkCount > 0}">
						<tr>
							<td class="infoline" rowspan="${cashReceipt.checkCount + 1}"
								colspan="2">&nbsp;</td>
							<td class="infoline"><kul:htmlAttributeLabel
								attributeEntry="${checkAttributes.checkNumber}" readOnly="true" />
							</td>
							<td class="infoline"><kul:htmlAttributeLabel
								attributeEntry="${checkAttributes.checkDate}" readOnly="true" />
							</td>
							<td class="infoline"><kul:htmlAttributeLabel
								attributeEntry="${checkAttributes.description}" readOnly="true" />
							</td>
							<td class="infoline"><kul:htmlAttributeLabel
								attributeEntry="${checkAttributes.amount}" readOnly="true" /></td>
							
						</tr>

						<logic:iterate name="cashReceipt" property="checks" id="check"
							indexId="checkIndex">
							<tr>
								<td><kul:htmlControlAttribute
									property="depositableCashReceipt[${ctr}].check[${checkIndex}].checkNumber"
									attributeEntry="${checkAttributes.checkNumber}" readOnly="true" />
								</td>
								<td><kul:htmlControlAttribute
									property="depositableCashReceipt[${ctr}].check[${checkIndex}].checkDate"
									attributeEntry="${checkAttributes.checkDate}" readOnly="true" />
								</td>
								<td><kul:htmlControlAttribute
									property="depositableCashReceipt[${ctr}].check[${checkIndex}].description"
									attributeEntry="${checkAttributes.description}" readOnly="true" />
								</td>
								<td>$<kul:htmlControlAttribute
									property="depositableCashReceipt[${ctr}].check[${checkIndex}].amount"
									attributeEntry="${checkAttributes.amount}" readOnly="true" />
								</td>
							</tr>
						</logic:iterate>
					</c:if>
				</logic:iterate>
      </c:if>
        
        <%-- check free cash receipts - only show on final deposit, when they are automatically deposited --%>
        <c:if test="${!empty KualiForm.checkFreeCashReceipts && KualiForm.depositFinal}">
        <tr>
          <td colspan="7"><strong>The following Cash Receipts had no checks associated with them; they are automatically deposited as part of the final deposit.</strong></td> 
        </tr>
        <logic:iterate name="KualiForm" id="cashReceipt"
					property="checkFreeCashReceipts" indexId="ctr">
          <c:set var="crCounter" value="${crCounter + 1}" />
					<tr>
						<td colspan="7"
							style="background-color: gray; border-bottom: 1px solid gray; padding: 0px"><img
							src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="1" height="1" /></td>
					</tr>
					<tr>
						<td>&nbsp;</td>
						<td>
						<div align="center"><b>${crCounter}</b></div>
						</td>
						<td>
						<div align="center"><a
							href="financialCashReceipt.do?methodToCall=docHandler&docId=${cashReceipt.documentHeader.documentNumber}&command=displayDocSearchView"
							target="new"> <kul:htmlControlAttribute
							property="checkFreeCashReceipt[${ctr}].documentNumber"
							attributeEntry="${cashReceiptAttributes.documentNumber}"
							readOnly="true" /> </a> <html:hidden
							property="checkFreeCashReceipt[${ctr}].documentHeader.documentNumber" />
						</div>
						</td>
						<td>
						<div align="center"><kul:htmlControlAttribute
							property="checkFreeCashReceipt[${ctr}].documentHeader.financialDocumentDescription"
							attributeEntry="${cashReceiptAttributes.financialDocumentDescription}"
							readOnly="true" /></div>
						</td>
						<td>
						<div align="center"><kul:htmlControlAttribute
							property="checkFreeCashReceipt[${ctr}].documentHeader.workflowDocument.createDate"
							attributeEntry="${dummyAttributes.genericTimestamp}"
							readOnly="true" /></div>
						</td>
						<td>&nbsp;</td>
						<td>
						<div align="center">
						$&nbsp;${cashReceipt.currencyFormattedSumTotalAmount}</div>
						</td>
					</tr>
				</logic:iterate>
        </c:if>

				<tr>
					<td colspan="7"
						style="background-color: gray; border-bottom: 1px solid gray; padding: 0px"><img
						src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="1" height="1" /></td>
				</tr>

			</table>
			</div>
      </kul:tab>
			
    </c:if>
    
    <c:if test="${!empty KualiForm.depositableCashieringChecks}">
    
      <kul:tab tabTitle="Cashiering Transaction Checks" defaultOpen="true" tabErrorKey="cashieringCheckErrors">
        <div class="tab-container" align="center">
        <div width="100%" align="left" style="padding-left: 10px; padding-bottom: 10px">
          <strong>Please select Cashiering Checks to deposit.</strong>
        </div>
        <div class="h2-container">
          <h2>Cashiering Checks Available for Deposit</h2>
        </div>
        <div id="workarea">
          <table cellpadding="0" cellspacing="0" class="datatable" summary="cashiering checks available for deposit">
            <tr>
              <td>
                <div align="center"><input type="checkbox" name="masterCheckCheckBox" onclick="checkCheckAllOrNone();" id="masterCheckCheckBox" /></div>
              </td>
              <kul:htmlAttributeHeaderCell literalLabel="#" scope="col" />
              <kul:htmlAttributeHeaderCell literalLabel="Check Number" scope="col" />
              <kul:htmlAttributeHeaderCell literalLabel="Description" scope="col" />
              <kul:htmlAttributeHeaderCell literalLabel="Check Date" scope="col" />
              <kul:htmlAttributeHeaderCell literalLabel="Check Amount" scope="col" />
            </tr>

            <logic:iterate name="KualiForm" id="cashieringCheck" property="depositableCashieringChecks" indexId="ctr">
              <tr>
                <td>
                  <div style="text-align: center">
                    <html:checkbox name="KualiForm" property="depositWizardCashieringCheckHelper[${ctr}].sequenceId" value="${KualiForm.depositableCashieringChecks[ctr].sequenceId}" />
                  </div>
                </td>
                <%-- if you change the spacing of the table cell below to put the different elements on different lines, giant monkeys will hurt you. Also, the DepositWizard form won't look quite as good --%>
                <td><strong>${ctr + 1}</strong><html:hidden name="KualiForm" property="depositableCashieringCheck[${ctr}].sequenceId" /></td>
                <td>
                  <kul:htmlControlAttribute property="depositableCashieringCheck[${ctr}].checkNumber" attributeEntry="${checkAttributes.checkNumber}" readOnly="true" />
                </td>
                <td>
                  <kul:htmlControlAttribute property="depositableCashieringCheck[${ctr}].description" attributeEntry="${checkAttributes.description}" readOnly="true" />
                </td>
                <td>
                  <kul:htmlControlAttribute property="depositableCashieringCheck[${ctr}].checkDate" attributeEntry="${checkAttributes.checkDate}" readOnly="true" />
                </td>
                <td>
                  <kul:htmlControlAttribute property="depositableCashieringCheck[${ctr}].amount" attributeEntry="${checkAttributes.amount}" readOnly="true" />
                </td>
              </tr>
            </logic:iterate>
          </table>
        </div>
      </kul:tab>
    </c:if>
    
    </div>
			<kul:panelFooter />

			<div id="globalbuttons" class="globalbuttons"><html:image
				property="methodToCall.createDeposit"
				src="${ConfigProperties.externalizable.images.url}buttonsmall_create.gif" alt="create" title="create"
				styleClass="tinybutton" /> <html:image
				property="methodToCall.refresh" src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_refresh.gif"
				alt="refresh" title="refresh" styleClass="tinybutton" /> <html:image
				property="methodToCall.cancel" src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif"
				alt="cancel" title="cancel" styleClass="tinybutton" /></div>
	</c:if>

	<c:if test="${empty KualiForm.depositableCashReceipts && empty KualiForm.depositableCashieringChecks && empty KualiForm.checkFreeCashReceipts}">
		<%-- manually handle parameter-substitution --%>
		<c:set var="msg0">
            ${fn:replace(ConfigProperties.depositWizard.status.noCashReceipts, "{0}", KualiForm.cashDrawerVerificationUnit )}
        </c:set>
		<c:set var="msg1">
            ${fn:replace(msg0, "{1}", KualiForm.cashManagementDocId)}
        </c:set>

		<table width="100%">
			<tr>
				<td align="center">${msg1}</td>
			</tr>
		</table>

		<div id="globalbuttons" class="globalbuttons"><html:image
			property="methodToCall.refresh" src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_refresh.gif"
			alt="refresh" title="refresh" styleClass="tinybutton" /> <html:image
			property="methodToCall.cancel" src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif"
			alt="cancel" title="cancel" styleClass="tinybutton" /></div>
	</c:if>
</kul:page>
