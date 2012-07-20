<%--
 Copyright 2005 The Kuali Foundation
 
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

<c:set var="displayHidden" value="false" />
<c:set var="checkDetailMode" value="${KualiForm.checkEntryDetailMode}" />
<c:set var="confirmMode" value="${KualiForm.editingMode['cmConfirm']}" />
<c:set var="changeRequestMode" value="${KualiForm.editingMode['changeRequestOn']}" />
<c:set var="cashReceiptAttributes"
	value="${DataDictionary['CashReceiptDocument'].attributes}" />
<c:set var="readOnly"
	value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:documentPage showDocumentInfo="true"
	htmlFormAction="financialCashReceipt"
	documentTypeName="CashReceiptDocument" renderMultipart="true"
	showTabButtons="true">
	<fp:crPrintCoverSheet />
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
	<sys:documentOverview editingMode="${KualiForm.editingMode}" />
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
				<c:if test="${confirmMode}">
					<tr>
						<th>&nbsp;</th>
						<th>Original</th>
						<th>Cash Manager</th>
					</tr>
				</c:if>
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
						<kul:htmlControlAttribute property="document.checkEntryMode"
								attributeEntry="${cashReceiptAttributes.checkEntryMode}" onchange="submitForm()" />
						
						<noscript><html:image src="${ConfigProperties.externalizable.images.url}tinybutton-select.gif"
							styleClass="tinybutton" alt="change check entry mode" title="change check entry mode" /></noscript>
						</td>
					</c:if>
					<c:if test="${confirmMode}">
						<c:if test="${!checkDetailMode}">
						<td>
							<kul:htmlControlAttribute property="document.totalConfirmedCheckAmount"
								attributeEntry="${cashReceiptAttributes.totalCheckAmount}" /></td>
						</c:if>
						<c:if test="${checkDetailMode}">
							<td>${KualiForm.document.currencyFormattedTotalConfirmedCheckAmount}</td>
						</c:if>
					</c:if>
					<c:if test="${confirmMode}">
						<c:if test="${!checkDetailMode}">
						<td>
							<kul:htmlControlAttribute property="document.totalConfirmedCheckAmount"
								attributeEntry="${cashReceiptAttributes.totalCheckAmount}" /></td>
						</c:if>
						<c:if test="${checkDetailMode}">
							<td>${KualiForm.document.currencyFormattedTotalConfirmedCheckAmount}</td>
						</c:if>
					</c:if>
				</tr>
				<tr>
					<th>
					<div align="right"><strong><kul:htmlAttributeLabel
						attributeEntry="${cashReceiptAttributes.totalCashAmount}"
						useShortLabel="false" /></strong></div>
					</th>
					<td width="35%" align="left" valign="middle"><c:out value="${KualiForm.document.currencyFormattedTotalCashAmount}" /></td>
					<c:if test="${confirmMode}">
						<td width="35%" align="left" valign="middle"><c:out value="${KualiForm.document.currencyFormattedTotalConfirmedCashAmount}" /></td>
					</c:if>
				</tr>
				<tr>
					<th>
					<div align="right"><strong><kul:htmlAttributeLabel
						attributeEntry="${cashReceiptAttributes.totalCoinAmount}"
						useShortLabel="false" /></strong></div>
					</th>
					<td width="35%" align="left" valign="middle"><c:out value="${KualiForm.document.currencyFormattedTotalCoinAmount}" /></td>
					<c:if test="${confirmMode}">
						<td width="35%" align="left" valign="middle"><c:out value="${KualiForm.document.currencyFormattedTotalConfirmedCoinAmount}" /></td>
					</c:if>
				</tr>
				<tr>
					<th>
					<div align="right"><strong><kul:htmlAttributeLabel
						attributeEntry="${cashReceiptAttributes.totalDollarAmount}"
						useShortLabel="false" /></strong></div>
					</th>
					<td width="35%" align="left" valign="middle"><c:out value="${KualiForm.document.currencyFormattedSumTotalAmount}" />&nbsp;&nbsp;&nbsp;
					<c:if test="${!readOnly}">
						<html:image src="${ConfigProperties.externalizable.images.url}tinybutton-recalculate.gif"
							styleClass="tinybutton" alt="recalculate total" title="recalculate total" />
					</c:if> <c:if test="${confirmMode}"> &nbsp; 
						<td width="35%" align="left" valign="middle"><c:out value="${KualiForm.document.currencyFormattedConfirmedSumTotalAmount}" />
						&nbsp;&nbsp;&nbsp;
						<html:image src="${ConfigProperties.externalizable.images.url}tinybutton-recalculate.gif"
							styleClass="tinybutton" alt="recalculate total" title="recalculate total" />
					</c:if></td>
				</tr>
			</tbody>
		</table>
		</div>
	</kul:tab>
  <kul:tab tabTitle="Currency and Coin Detail" defaultOpen="true" tabErrorKey="${KFSConstants.EDIT_CASH_RECEIPT_CURRENCY_COIN_ERRORS}">
    <div class="tab-container" align="center">
  	  <c:if test="${confirmMode}">
  	    <div>
	  	  <html:image align="left" property="methodToCall.copyAllCurrencyAndCoin" src="${ConfigProperties.externalizable.images.url}tinybutton-copyall.gif" title="Copy all original currency and coin" alt="Copy all currency and coin" styleClass="tinybutton"/>
	    </div>
	  </c:if>
    </div>
    <div class="tab-container" align="center">
        <h3>Currency and Coin Detail</h3>
      <fp:currencyCoinLine currencyProperty="document.currencyDetail" coinProperty="document.coinDetail" confirmedCurrencyProperty="document.confirmedCurrencyDetail" confirmedCoinProperty="document.confirmedCoinDetail" readOnly="${readOnly}" editingMode="${KualiForm.editingMode}" confirmMode="${confirmMode}"/>
    </div>
  </kul:tab>
	
	<fp:crCheckLines checkDetailMode="${checkDetailMode}"
		editingMode="${KualiForm.editingMode}"
		totalAmount="${KualiForm.cashReceiptDocument.currencyFormattedTotalCheckAmount}"
		totalConfirmedAmount="${KualiForm.cashReceiptDocument.currencyFormattedTotalConfirmedCheckAmount}"
		displayHidden="${displayHidden}" 
		confirmMode="${confirmMode}"/>
		
	<c:if test="${changeRequestMode}">	
	<kul:tab tabTitle="Change Request" defaultOpen="false">
		<div class="tab-container" align="center">
			<h3>Requesting</h3>
      		<fp:currencyCoinLine currencyProperty="document.changeCurrencyDetail" coinProperty="document.changeCoinDetail" readOnly="${readOnly}" editingMode="${KualiForm.editingMode}" confirmMode="${confirmMode}" totalChangeAmount="${KualiForm.document.currencyFormattedChangeTotalAmount}"/>
      	</div>
	</kul:tab>	
	</c:if>
		
	<kul:tab tabTitle="Accounting Lines" defaultOpen="true" tabErrorKey="${KFSConstants.ACCOUNTING_LINE_ERRORS}">
		<sys-java:accountingLines>
			<sys-java:accountingLineGroup newLinePropertyName="newSourceLine" collectionPropertyName="document.sourceAccountingLines" collectionItemPropertyName="document.sourceAccountingLine" attributeGroupName="source" />
		</sys-java:accountingLines>
	</kul:tab>			
		 
  	<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
  	<fp:capitalAccountingLines readOnly="${readOnly}"/>
  	
	<c:if test="${KualiForm.capitalAccountingLine.canCreateAsset}">
		<fp:capitalAssetCreateTab readOnly="${readOnly}"/>
	</c:if>
  	
	<fp:capitalAssetModifyTab readOnly="${readOnly}"/>  
			
	<gl:generalLedgerPendingEntries />
	<kul:notes />
	<kul:adHocRecipients />
	<kul:routeLog />
	<kul:superUserActions />
	<kul:panelFooter />
	<sys:documentControls
		transactionalDocument="${documentEntry.transactionalDocument}" />
</kul:documentPage>
