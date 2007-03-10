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
<%@ include file="/jsp/core/tldHeader.jsp"%>

<c:set var="rawDepositTypeCode" value="${KualiForm.depositTypeCode}" />
<c:set var="docTitle"
	value="Create a New ${KualiForm.depositTypeString} Deposit" />

<c:set var="depositAttributes"
	value="${DataDictionary.Deposit.attributes}" />
<c:set var="cashReceiptAttributes"
	value="${DataDictionary.KualiCashReceiptDocument.attributes}" />
<c:set var="checkAttributes"
	value="${DataDictionary.CheckBase.attributes}" />
<c:set var="dummyAttributes"
	value="${DataDictionary.AttributeReferenceDummy.attributes}" />


<kul:page showDocumentInfo="false" showTabButtons="false"
	headerTitle="${docTitle}" docTitle="${docTitle}"
	transactionalDocument="false" htmlFormAction="depositWizard">
	<script type="text/javascript">
function checkAllOrNone() {
  for(var i = 0; i < document.KualiForm.elements.length; i++) {
    var e = document.KualiForm.elements[i];
    if((e.name != 'masterCheckBox') && (e.type == 'checkbox') && (!e.disabled)) {
      e.checked = document.KualiForm.elements['masterCheckBox'].checked;
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

	<c:if test="${!empty KualiForm.depositableCashReceipts}">
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
					<div align="center"><input type="checkbox" name="masterCheckBox"
						onclick="checkAllOrNone();" /></div>
					</td>
					<kul:htmlAttributeHeaderCell literalLabel="#" scope="col" />
					<kul:htmlAttributeHeaderCell literalLabel="Document Number"
						scope="col" />
					<kul:htmlAttributeHeaderCell literalLabel="Description" scope="col" />
					<kul:htmlAttributeHeaderCell literalLabel="Create Date" scope="col" />
					<kul:htmlAttributeHeaderCell literalLabel="Check Total" scope="col" />
					<kul:htmlAttributeHeaderCell literalLabel="Currency Total"
						scope="col" />
					<kul:htmlAttributeHeaderCell literalLabel="Coin Total" scope="col" />
					<kul:htmlAttributeHeaderCell literalLabel="Total" scope="col" />
				</tr>

				<logic:iterate name="KualiForm" id="cashReceipt"
					property="depositableCashReceipts" indexId="ctr">

					<tr>
						<td colspan="9"
							style="background-color: gray; border-bottom: 1px solid gray; padding: 0px"><img
							src="images/pixel_clear.gif" alt="" width="1" height="1" /></td>
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
						<td>
						<div align="center">
						$&nbsp;${cashReceipt.currencyFormattedTotalCashAmount} <html:hidden
							property="depositableCashReceipt[${ctr}].totalCashAmount" /></div>
						</td>
						<td>
						<div align="center">
						$&nbsp;${cashReceipt.currencyFormattedTotalCoinAmount} <html:hidden
							property="depositableCashReceipt[${ctr}].totalCoinAmount" /></div>
						</td>
						<td>
						<div align="center">
						$&nbsp;${cashReceipt.currencyFormattedSumTotalAmount}</div>
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
							<td class="infoline" rowspan="${receipt.checkCount + 1}"
								colspan="3">&nbsp;</td>
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

				<tr>
					<td colspan="9"
						style="background-color: gray; border-bottom: 1px solid gray; padding: 0px"><img
						src="images/pixel_clear.gif" alt="" width="1" height="1" /></td>
				</tr>

			</table>
			</div>
			</div>
			<kul:panelFooter />

			<div id="globalbuttons" class="globalbuttons"><html:image
				property="methodToCall.createDeposit"
				src="images/buttonsmall_create.gif" alt="create" title="create"
				styleClass="tinybutton" /> <html:image
				property="methodToCall.refresh" src="images/buttonsmall_refresh.gif"
				alt="refresh" title="refresh" styleClass="tinybutton" /> <html:image
				property="methodToCall.cancel" src="images/buttonsmall_cancel.gif"
				alt="cancel" title="cancel" styleClass="tinybutton" /></div>
		</kul:tab>
	</c:if>

	<c:if test="${empty KualiForm.depositableCashReceipts}">
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
			property="methodToCall.refresh" src="images/buttonsmall_refresh.gif"
			alt="refresh" title="refresh" styleClass="tinybutton" /> <html:image
			property="methodToCall.cancel" src="images/buttonsmall_cancel.gif"
			alt="cancel" title="cancel" styleClass="tinybutton" /></div>
	</c:if>
</kul:page>
