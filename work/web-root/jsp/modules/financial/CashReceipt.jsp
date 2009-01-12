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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<c:set var="displayHidden" value="false" />
<c:set var="checkDetailMode" value="${KualiForm.checkEntryDetailMode}" />
<c:set var="cashReceiptAttributes"
	value="${DataDictionary['CashReceiptDocument'].attributes}" />
<c:set var="readOnly"
	value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:documentPage showDocumentInfo="true"
	htmlFormAction="financialCashReceipt"
	documentTypeName="CashReceiptDocument" renderMultipart="true"
	showTabButtons="true">
	<cr:printCoverSheet />
	<c:set var="docStatusMessage"
		value="${KualiForm.financialDocumentStatusMessage}" />
	<c:if test="${!empty docStatusMessage}">
		<div align="left"><b>${KualiForm.financialDocumentStatusMessage}</b></div>
		<br>
	</c:if>
	<c:set var="cashDrawerStatusMessage"
		value="${KualiForm.cashDrawerStatusMessage}" />
	<c:if test="${!empty cashDrawerStatusMessage}">
		<div align="left"><span style="color: #ff0000;"><b>${KualiForm.cashDrawerStatusMessage}</b></span>
		</div>
		<br>
	</c:if>
	<kfs:documentOverview editingMode="${KualiForm.editingMode}" />
	<SCRIPT type="text/javascript">
    <!--
        function submitForm() {
            document.forms[0].submit();
        }
    //-->
    </SCRIPT>
	<kul:tab tabTitle="Cash Reconciliation" defaultOpen="true"
		tabErrorKey="${KFSConstants.EDIT_CASH_RECEIPT_CASH_RECONCILIATION_ERRORS}">
		<div class="tab-container" align=center>
		<h3>Cash Reconciliation</h3>
		<table>
			<tbody>
				<tr>
					<th width="35%">
					<div align="right"><kul:htmlAttributeLabel
						attributeEntry="${cashReceiptAttributes.totalCheckAmount}"
						useShortLabel="false" /></div>
					</th>
					<c:if test="${readOnly}">
						<td>${KualiForm.document.currencyFormattedTotalCheckAmount}</td>
					</c:if>
					<c:if test="${!readOnly}">
						<td><c:if test="${!checkDetailMode}">
							<kul:htmlControlAttribute property="document.totalCheckAmount"
								attributeEntry="${cashReceiptAttributes.totalCheckAmount}" />
						</c:if> 
					</c:if>
					<c:if test="${!readOnly}">
						<kul:htmlControlAttribute property="checkEntryMode"
								attributeEntry="${cashReceiptAttributes.checkEntryMode}" />
						
						<noscript><html:image src="${ConfigProperties.externalizable.images.url}tinybutton-select.gif"
							styleClass="tinybutton" alt="change check entry mode" title="change check entry mode" /></noscript>
						</td>
					</c:if>
				</tr>
				<tr>
					<th>
					<div align="right"><strong><kul:htmlAttributeLabel
						attributeEntry="${cashReceiptAttributes.totalCashAmount}"
						useShortLabel="false" /></strong></div>
					</th>
					<td width="35%" align="left" valign="middle">${KualiForm.document.currencyFormattedTotalCashAmount}</td>
				</tr>
				<tr>
					<th>
					<div align="right"><strong><kul:htmlAttributeLabel
						attributeEntry="${cashReceiptAttributes.totalCoinAmount}"
						useShortLabel="false" /></strong></div>
					</th>
					<td width="35%" align="left" valign="middle">${KualiForm.document.currencyFormattedTotalCoinAmount}</td>
				</tr>
				<tr>
					<th>
					<div align="right"><strong><kul:htmlAttributeLabel
						attributeEntry="${cashReceiptAttributes.totalDollarAmount}"
						useShortLabel="false" /></strong></div>
					</th>
					<td width="35%" align="left" valign="middle">${KualiForm.document.currencyFormattedSumTotalAmount}&nbsp;&nbsp;&nbsp;
					<c:if test="${!readOnly}">
						<html:image src="${ConfigProperties.externalizable.images.url}tinybutton-recalculate.gif"
							styleClass="tinybutton" alt="recalculate total" title="recalculate total" />
					</c:if> <c:if test="${readOnly}"> &nbsp; </c:if></td>
				</tr>
			</tbody>
		</table>
		</div>
	</kul:tab>
  <kul:tab tabTitle="Currency and Coin Detail" defaultOpen="true" tabErrorKey="${KFSConstants.EDIT_CASH_RECEIPT_CURRENCY_COIN_ERRORS}">
    <div class="tab-container" align="center">
        <h3>Currency and Coin Detail</h3>
      <fin:currencyCoinLine currencyProperty="document.currencyDetail" coinProperty="document.coinDetail" readOnly="false" editingMode="${KualiForm.editingMode}" />
    </div>
  </kul:tab>
	
	<cr:checkLines checkDetailMode="${checkDetailMode}"
		editingMode="${KualiForm.editingMode}"
		totalAmount="${KualiForm.cashReceiptDocument.currencyFormattedTotalCheckAmount}"
		displayHidden="${displayHidden}" />
		
	<kul:tab tabTitle="Accounting Lines" defaultOpen="true" tabErrorKey="${KFSConstants.ACCOUNTING_LINE_ERRORS}">
		<sys:accountingLines>
			<sys:accountingLineGroup newLinePropertyName="newSourceLine" collectionPropertyName="document.sourceAccountingLines" collectionItemPropertyName="document.sourceAccountingLine" attributeGroupName="source" />
		</sys:accountingLines>
	</kul:tab>			
		 
  	<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
	<fin:capitalAssetEditTab readOnly="${readOnly}"/>
			
	<gl:generalLedgerPendingEntries />
	<kul:notes />
	<kul:adHocRecipients />
	<kul:routeLog />
	<kul:panelFooter />
	<kfs:documentControls
		transactionalDocument="${documentEntry.transactionalDocument}" />
</kul:documentPage>
