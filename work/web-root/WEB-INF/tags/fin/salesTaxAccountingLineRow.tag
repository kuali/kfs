<%--
 Copyright 2007 The Kuali Foundation.
 
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
<%@ include file="/jsp/modules/financial/customActionsInterface.jsp"%>
<%@ attribute name="accountingLine" required="true"
	description="The name in the form of the accounting line
              being edited or displayed by this row."%>
<%@ attribute name="baselineAccountingLine" required="false"
	description="The name in the form of the baseline accounting line
              from before the most recent edit of this row,
              to put in hidden fields for comparison or reversion."%>
<%@ attribute name="accountingLineAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="accountingLineIndex" required="false"
	description="index of this accountingLine in the corresponding form list"%>

<%@ attribute name="dataCellCssClass" required="true"
	description="The name of the CSS class for this data cell.
              This is used to distinguish the look of the add row from the already-added rows."%>

<%@ attribute name="accountingAddLineIndex" required="false"
	description="index for multiple add new source lines"%>

<%@ attribute name="readOnly" required="true"%>
<%@ attribute name="editableFields" required="false"
	type="java.util.Map"
	description="Map of accounting line fields which this user is allowed to edit"%>
<%@ attribute name="displayHidden" required="false"
	description="display values of hidden fields"%>
<%@ attribute name="accountingLineValuesMap" required="true"
	type="java.util.Map"
	description="map of the accounting line primitive fields and values"%>
<%@ attribute name="forcedReadOnlyFields" required="false"
	type="java.util.Map"
	description="foces the object code to become a read only field regardless of document state"%>
<%@ attribute name="hideFields" required="false"
    description="Comma delimited list of fields to hide for this type of accounting line" %>
<%@ attribute name="nestedIndex" required="false"
    description="A boolean whether we'll need a nested index that includes item index and account index or if we just need one index for the accountingLineIndex"%>
<%@ attribute name="salesTaxRowlabelFontWeight" required="false"
    description="Font weight for the sales tax labels, bold on new lines, not on existing lines"%>
<%@ attribute name="hasMultipleActionButtons" required="true"
    description="Whether or not this row will contain multiple action buttons"%>
	
<%-- Multiple buttons increase this row's height, but the table can't automatically cover that extra height, so kludge it. --%>
<table cellpadding="0" cellspacing="0"
	style="width: 100%;border: 0px;${hasMultipleActionButtons ? 'height: 60px;' : ''}">
	<tr>
		<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
		accountingLine="${accountingLine}"
		baselineAccountingLine="${baselineAccountingLine}"
		field="salesTax.chartOfAccountsCode" 
		attributes="${accountingLineAttributes}" lookup="false" inquiry="true"
		labelFontWeight="${salesTaxRowlabelFontWeight}"
		boClassSimpleName="Account"
		readOnly="${(readOnly&&(empty editableFields['salesTax.accountNumber']))||!(empty forcedReadOnlyFields['salesTax.accountNumber'])}"
		displayHidden="${displayHidden}"
		accountingLineValuesMap="${accountingLineValuesMap}" />
	<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
		accountingLine="${accountingLine}"
		baselineAccountingLine="${baselineAccountingLine}"
		field="salesTax.accountNumber" 
		conversionField="accountNumber"
		attributes="${accountingLineAttributes}" lookup="true" inquiry="true"
		labelFontWeight="${salesTaxRowlabelFontWeight}"
		boClassSimpleName="Account"
		readOnly="${(readOnly&&(empty editableFields['salesTax.accountNumber']))||!(empty forcedReadOnlyFields['salesTax.accountNumber'])}"
		displayHidden="${displayHidden}"
		lookupUnkeyedFieldConversions="chartOfAccountsCode:${accountingLine}.salesTax.chartOfAccountsCode,"
		lookupParameters="${accountingLine}.salesTax.chartOfAccountsCode:chartOfAccountsCode,${accountingLine}.salesTax.accountNumber:accountNumber,"
		accountingLineValuesMap="${accountingLineValuesMap}" />
	<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
		accountingLine="${accountingLine}"
		baselineAccountingLine="${baselineAccountingLine}"
		field="salesTax.financialDocumentGrossSalesAmount" 
		attributes="${accountingLineAttributes}"
		labelFontWeight="${salesTaxRowlabelFontWeight}"
		readOnly="${(readOnly&&(empty editableFields['salesTax.financialDocumentGrossSalesAmount']))||!(empty forcedReadOnlyFields['salesTax.accountNumber'])}"
		displayHidden="${displayHidden}"
		accountingLineValuesMap="${accountingLineValuesMap}" />
	<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
		accountingLine="${accountingLine}"
		baselineAccountingLine="${baselineAccountingLine}"
		field="salesTax.financialDocumentTaxableSalesAmount" 
		labelFontWeight="${salesTaxRowlabelFontWeight}"
		attributes="${accountingLineAttributes}"
		readOnly="${(readOnly&&(empty editableFields['salesTax.financialDocumentTaxableSalesAmount']))||!(empty forcedReadOnlyFields['salesTax.accountNumber'])}"
		displayHidden="${displayHidden}"
		accountingLineValuesMap="${accountingLineValuesMap}" />
	<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
		accountingLine="${accountingLine}"
		baselineAccountingLine="${baselineAccountingLine}"
		field="salesTax.financialDocumentSaleDate" 
		labelFontWeight="${salesTaxRowlabelFontWeight}"
		attributes="${accountingLineAttributes}"
		readOnly="${(readOnly&&(empty editableFields['salesTax.financialDocumentSaleDate']))||!(empty forcedReadOnlyFields['salesTax.accountNumber'])}"
		displayHidden="${displayHidden}"
		accountingLineValuesMap="${accountingLineValuesMap}" />
	
	</tr>
</table>