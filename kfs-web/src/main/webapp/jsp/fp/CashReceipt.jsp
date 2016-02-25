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

<c:set var="displayHidden" value="false" />
<c:set var="checkDetailMode" value="${KualiForm.checkEntryDetailMode}" />
<c:set var="confirmMode" value="${KualiForm.editingMode['cmConfirm']}" />
<c:set var="changeRequestMode" value="${KualiForm.editingMode['changeRequestOn']}" />
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<c:set var="confirmed" value="${KualiForm.document.confirmed}" />
<c:set var="cashReceiptAttributes" value="${DataDictionary['CashReceiptDocument'].attributes}" />
<c:set var="displayCashReceiptDenominationDetail" value="${KualiForm.displayCashReceiptDenominationDetail}" />

<%-- 
	We should show both the original and confirmed details after CashManagerment confirmation; only that neither column would be editable.
--%>	
<c:set var="showConfirm" value="${confirmMode || confirmed}" />

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
				<c:if test="${showConfirm}">
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
					<c:if test="${showConfirm}">
						<c:if test="${!checkDetailMode}">
						<td>
							<kul:htmlControlAttribute property="document.totalConfirmedCheckAmount"
								attributeEntry="${cashReceiptAttributes.totalCheckAmount}" readOnly="${!confirmMode}"/></td>
						</c:if>
						<c:if test="${checkDetailMode}">
							<td>${KualiForm.document.currencyFormattedTotalConfirmedCheckAmount}</td>
						</c:if>
					</c:if>			
				</tr>
				<tr>
					<th>
					<div align="right"><strong><kul:htmlAttributeLabel
						attributeEntry="${cashReceiptAttributes.totalCurrencyAmount}"
						useShortLabel="false" /></strong></div>
					</th>
					<td width="35%" align="left" valign="middle"><bean:write name="KualiForm" property="document.totalCurrencyAmount" /></td>
					<c:if test="${showConfirm}">
						<td width="35%" align="left" valign="middle"><bean:write name="KualiForm" property="document.totalConfirmedCurrencyAmount" /></td>
					</c:if>
				</tr>
				<tr>
					<th>
					<div align="right"><strong><kul:htmlAttributeLabel
						attributeEntry="${cashReceiptAttributes.totalCoinAmount}"
						useShortLabel="false" /></strong></div>
					</th>
					<td width="35%" align="left" valign="middle"><bean:write name="KualiForm" property="document.totalCoinAmount" /></td>
					<c:if test="${showConfirm}">
						<td width="35%" align="left" valign="middle"><bean:write name="KualiForm" property="document.totalConfirmedCoinAmount" /></td>
					</c:if>
				</tr>
				<c:if test="${displayCashReceiptDenominationDetail}">												
				<tr>
					<th>
					<div align="right"><strong><kul:htmlAttributeLabel
						attributeEntry="${cashReceiptAttributes.totalCashInAmount}"
						useShortLabel="false" /></strong></div>
					</th>
					<td width="35%" align="left" valign="middle"><bean:write name="KualiForm" property="document.totalCashInAmount" /></td>
					<c:if test="${showConfirm}">
						<td width="35%" align="left" valign="middle"><bean:write name="KualiForm" property="document.totalConfirmedCashInAmount" /></td>
					</c:if>
				</tr>												
				<tr>
					<th>
					<div align="right"><strong><kul:htmlAttributeLabel
						attributeEntry="${cashReceiptAttributes.totalMoneyInAmount}"
						useShortLabel="false" /></strong></div>
					</th>
					<td width="35%" align="left" valign="middle"><bean:write name="KualiForm" property="document.totalMoneyInAmount" /></td>
					<c:if test="${showConfirm}">
						<td width="35%" align="left" valign="middle"><bean:write name="KualiForm" property="document.totalConfirmedMoneyInAmount" /></td>
					</c:if>
				</tr>												
				<tr>
					<th>
					<div align="right"><strong><kul:htmlAttributeLabel
						attributeEntry="${cashReceiptAttributes.totalChangeCurrencyAmount}"
						useShortLabel="false" /></strong></div>
					</th>
					<td width="35%" align="left" valign="middle"><bean:write name="KualiForm" property="document.totalChangeCurrencyAmount" /></td>
					<c:if test="${showConfirm}">
						<td width="35%" align="left" valign="middle"><bean:write name="KualiForm" property="document.totalConfirmedChangeCurrencyAmount" /></td>
					</c:if>
				</tr>												
				<tr>
					<th>
					<div align="right"><strong><kul:htmlAttributeLabel
						attributeEntry="${cashReceiptAttributes.totalChangeCoinAmount}"
						useShortLabel="false" /></strong></div>
					</th>
					<td width="35%" align="left" valign="middle"><bean:write name="KualiForm" property="document.totalChangeCoinAmount" /></td>
					<c:if test="${showConfirm}">
						<td width="35%" align="left" valign="middle"><bean:write name="KualiForm" property="document.totalConfirmedChangeCoinAmount" /></td>
					</c:if>
				</tr>												
				<tr>
					<th>
					<div align="right"><strong><kul:htmlAttributeLabel
						attributeEntry="${cashReceiptAttributes.totalChangeAmount}"
						useShortLabel="false" /></strong></div>
					</th>
					<td width="35%" align="left" valign="middle"><bean:write name="KualiForm" property="document.totalChangeAmount" /></td>
					<c:if test="${showConfirm}">
						<td width="35%" align="left" valign="middle"><bean:write name="KualiForm" property="document.totalConfirmedChangeAmount" /></td>
					</c:if>
				</tr>
				</c:if>												
				<tr>
					<th>
					<div align="right"><strong><kul:htmlAttributeLabel
						attributeEntry="${cashReceiptAttributes.totalNetAmount}"
						useShortLabel="false" /></strong></div>
					</th>
					<td width="35%" align="left" valign="middle"><bean:write name="KualiForm" property="document.totalNetAmount" /></td>
					<c:if test="${showConfirm}">
						<td width="35%" align="left" valign="middle"><bean:write name="KualiForm" property="document.totalConfirmedNetAmount" /></td>
					</c:if>
				</tr>												
			</tbody>
		</table>

		<c:if test="${!readOnly || confirmMode}">
			<div>
				<html:image
					src="${ConfigProperties.externalizable.images.url}tinybutton-recalculate.gif" styleClass="tinybutton" 
					title="Recalculate all subtotals and totals" alt="Recalculate Totals" />
				</div>
			</c:if>

		</div>
	</kul:tab>
	
  <kul:tab tabTitle="Currency and Coin Detail" defaultOpen="true" tabErrorKey="${KFSConstants.EDIT_CASH_RECEIPT_CURRENCY_COIN_ERRORS}">
  	<c:if test="${confirmMode}"> <%-- we only show copy buttons in CashManager Confirm Mode --%>
  	    <div class="tab-container">
			<html:image align="center" property="methodToCall.copyAllCurrencyAndCoin" 
			 	src="${ConfigProperties.externalizable.images.url}tinybutton-copyall.gif" styleClass="tinybutton"
			 	title="Copy all currency and coin from Original to CashManager section" alt="Copy all currency and coin" />
	    </div>
	</c:if>
    <div class="tab-container" align="center">
        <h3>Currency and Coin Detail</h3>
      <fp:currencyCoinLine currencyProperty="document.currencyDetail" coinProperty="document.coinDetail" 
		confirmedCurrencyProperty="document.confirmedCurrencyDetail" confirmedCoinProperty="document.confirmedCoinDetail" 
      	readOnly="${readOnly}" editingMode="${KualiForm.editingMode}" confirmMode="${confirmMode}" confirmed="${confirmed}" />
    </div>
  </kul:tab>
	
	<fp:crCheckLines checkDetailMode="${checkDetailMode}"
		editingMode="${KualiForm.editingMode}"
		totalAmount="${KualiForm.cashReceiptDocument.currencyFormattedTotalCheckAmount}"
		totalConfirmedAmount="${KualiForm.cashReceiptDocument.currencyFormattedTotalConfirmedCheckAmount}"
		displayHidden="${displayHidden}" 
		confirmMode="${confirmMode}"/>
		
	<c:if test="${changeRequestMode and displayCashReceiptDenominationDetail}">	
		<kul:tab tabTitle="Change Request" defaultOpen="${KualiForm.document.changeRequested}" tabErrorKey="${KFSConstants.EDIT_CASH_RECEIPT_CHANGE_REQUEST_ERRORS}">
			<c:if test="${confirmMode}"> <%-- we only show copy buttons in CashManager Confirm Mode --%>
				<div class="tab-container">
					<html:image align="center" property="methodToCall.copyAllChangeCurrencyAndCoin"
						src="${ConfigProperties.externalizable.images.url}tinybutton-copyall.gif" styleClass="tinybutton" 
						title="Copy all change currency and coin from Original to CashManager section" alt="Copy all change currency and coin" />
				</div>
			</c:if>
			<div class="tab-container" align="center">
				<h3>Requesting</h3>
	      		<fp:currencyCoinLine currencyProperty="document.changeCurrencyDetail" coinProperty="document.changeCoinDetail" 
					confirmedCurrencyProperty="document.confirmedChangeCurrencyDetail" confirmedCoinProperty="document.confirmedChangeCoinDetail" 
	      			readOnly="${readOnly}" editingMode="${KualiForm.editingMode}" confirmMode="${confirmMode}" confirmed="${confirmed}" />
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
