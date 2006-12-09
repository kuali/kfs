<%--
 Copyright 2005-2006 The Kuali Foundation.
 
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
<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/cr" prefix="cr"%>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd"%>
<c:set var="displayHidden" value="false" />
<c:set var="checkDetailMode" value="${KualiForm.checkEntryDetailMode}" />
<c:set var="cashReceiptAttributes"
	value="${DataDictionary['KualiCashReceiptDocument'].attributes}" />
<c:set var="readOnly"
	value="${!empty KualiForm.editingMode['viewOnly']}" />
<kul:documentPage showDocumentInfo="true"
	htmlFormAction="financialCashReceipt"
	documentTypeName="KualiCashReceiptDocument" renderMultipart="true"
	showTabButtons="true">
	<cr:printCoverSheet />
	<kul:hiddenDocumentFields />
	<html:hidden property="document.nextCheckSequenceId" />
	<html:hidden property="document.checkEntryMode" />
	<html:hidden property="checkTotal" />
	<c:set var="docStatusMessage"
		value="${KualiForm.financialDocumentStatusMessage}" />
	<c:if test="${!empty docStatusMessage}">
		<div align="left"><b>${KualiForm.financialDocumentStatusMessage}</b></div>
		<br>
	</c:if>
	<c:set var="cashDrawerStatusMessage"
		value="${KualiForm.cashDrawerStatusMessage}" />
	<c:if test="${!empty cashDrawerStatusMessage}">
		<div align="left"><font color="red"><b>${KualiForm.cashDrawerStatusMessage}</b></font>
		</div>
		<br>
	</c:if>
	<kul:documentOverview editingMode="${KualiForm.editingMode}" />
	<SCRIPT type="text/javascript">
    <!--
        function submitForm() {
            document.forms[0].submit();
        }
    //-->
    </SCRIPT>
	<html:hidden write="false" property="document.campusLocationCode" />
	<kul:tab tabTitle="Cash Reconciliation" defaultOpen="true"
		tabErrorKey="${Constants.EDIT_CASH_RECEIPT_CASH_RECONCILIATION_ERRORS}">
		<div class="tab-container" align=center>
		<div class="h2-container">
		<h2>Cash Reconciliation</h2>
		</div>
		<table>
			<tbody>
				<tr>
					<th width="35%">
					<div align="right"><kul:htmlAttributeLabel
						attributeEntry="${cashReceiptAttributes.totalCheckAmount}"
						useShortLabel="false" /></div>
					</th>
					<c:if test="${readOnly}">
						<td>$${KualiForm.document.currencyFormattedTotalCheckAmount} <html:hidden
							write="false" property="document.totalCheckAmount" /> <html:hidden
							write="false" property="checkEntryMode" /></td>
					</c:if>
					<c:if test="${!readOnly}">
						<td><c:if test="${!checkDetailMode}">
							<kul:htmlControlAttribute property="document.totalCheckAmount"
								attributeEntry="${cashReceiptAttributes.totalCheckAmount}" />
						</c:if> <c:if test="${checkDetailMode}"> $${KualiForm.document.currencyFormattedTotalCheckAmount} 
	        		<html:hidden write="false"
								property="document.totalCheckAmount" />
						</c:if>
					</c:if>
					<c:if test="${!readOnly}">

						<html:select property="checkEntryMode" onchange="submitForm()">
							<html:optionsCollection property="checkEntryModes" label="label"
								value="value" />
						</html:select>
						<noscript><html:image src="images/tinybutton-select.gif"
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
					<td width="35%" align="left" valign="middle"><c:if
						test="${readOnly}"> $${KualiForm.document.currencyFormattedTotalCashAmount} <html:hidden
							write="false" property="document.totalCashAmount" />
					</c:if> <c:if test="${!readOnly}">
						<kul:htmlControlAttribute property="document.totalCashAmount"
							attributeEntry="${cashReceiptAttributes.totalCashAmount}" styleClass="amount" />
					</c:if></td>
				</tr>
				<tr>
					<th>
					<div align="right"><strong><kul:htmlAttributeLabel
						attributeEntry="${cashReceiptAttributes.totalCoinAmount}"
						useShortLabel="false" /></strong></div>
					</th>
					<td width="35%" align="left" valign="middle"><c:if
						test="${readOnly}"> $${KualiForm.document.currencyFormattedTotalCoinAmount} <html:hidden
							write="false" property="document.totalCoinAmount" />
					</c:if> <c:if test="${!readOnly}">
						<kul:htmlControlAttribute property="document.totalCoinAmount"
							attributeEntry="${cashReceiptAttributes.totalCoinAmount}" styleClass="amount" />
					</c:if></td>
				</tr>
				<tr>
					<th>
					<div align="right"><strong><kul:htmlAttributeLabel
						attributeEntry="${cashReceiptAttributes.sumTotalAmount}"
						useShortLabel="false" skipHelpUrl="true" /></strong></div>
					</th>
					<td width="35%" align="left" valign="middle">$${KualiForm.document.currencyFormattedSumTotalAmount}&nbsp;&nbsp;&nbsp;
					<c:if test="${!readOnly}">
						<html:image src="images/tinybutton-recalculate.gif"
							styleClass="tinybutton" alt="recalculate total" title="recalculate total" />
					</c:if> <c:if test="${readOnly}"> &nbsp; </c:if></td>
				</tr>
			</tbody>
		</table>
		</div>
	</kul:tab>
	<cr:checkLines checkDetailMode="${checkDetailMode}"
		editingMode="${KualiForm.editingMode}"
		totalAmount="${KualiForm.cashReceiptDocument.currencyFormattedTotalCheckAmount}"
		displayHidden="${displayHidden}" />
	<fin:accountingLines editingMode="${KualiForm.editingMode}"
		editableAccounts="${KualiForm.editableAccounts}"
		sourceAccountingLinesOnly="true"
		extraSourceRowFields="financialDocumentLineDescription" />
	<kul:generalLedgerPendingEntries />
	<kul:notes />
	<kul:adHocRecipients />
	<kul:routeLog />
	<kul:panelFooter />
	<kul:documentControls
		transactionalDocument="${documentEntry.transactionalDocument}" />
</kul:documentPage>
