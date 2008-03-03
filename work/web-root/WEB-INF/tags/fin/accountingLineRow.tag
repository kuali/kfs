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
<%@ attribute name="rowHeader" required="true"
	description="The value of the header cell of this row.
              It would be 'add:' or the number of this row's accounting line within its group."%>

<%@ attribute name="actionGroup" required="true"
	description="The name of the group of action buttons to be displayed; valid values are newLine and existingLine."%>
<%@ attribute name="actionInfix" required="true"
	description="Infix used to build method names which will be invoked by the buttons in this actionGroup"%>

<%@ attribute name="accountingAddLineIndex" required="false"
	description="index for multiple add new source lines"%>

<%@ attribute name="readOnly" required="true"%>
<%@ attribute name="editableFields" required="false"
	type="java.util.Map"
	description="Map of accounting line fields which this user is allowed to edit"%>
<%@ attribute name="hiddenFields" required="true"
	description="A comma separated list of names of accounting line fields
              to be put in hidden fields on this form."%>
<%@ attribute name="salesTaxHiddenFields" required="false"
	description="A comma separated list of names of sales tax line fields
              to be put in hidden fields on this form."%>
<%@ attribute name="columnCountUntilAmount" required="true"
	description="the number of columns to the left of the amount column(s) in the
              accounting lines table.  This depends on the number
              of optional fields and whether there is an object type column."%>

<%@ attribute name="optionalFields" required="false"
	description="A comma separated list of names of accounting line fields
              to be appended to the required field columns, before the amount column."%>
              
<%@ attribute name="isOptionalFieldsInNewRow" required="false" type="java.lang.Boolean"
	description="indicate if the oprtional fields are put in a new row under the default accouting line"%>              

<%@ attribute name="extraRowFields" required="false"
	description="A comma seperated list of names of any non-standard fields
              required by this accounting line.  They are placed on a second row, sharing the
              same row header and action (if any). Each field includes its own label and takes up two columns.
              Note that since there are only 8 standard fields, no more than 4+(optionalFieldsCount/2)
              extra fields can be properly formatted in the table.
              This attribute requires the extraRowLabelFontWeight attribute."%>
<%@ attribute name="extraRowLabelFontWeight" required="false"
	description="The font weight for the in-cell labels on the extra row, e.g., bold or normal.
              Providing this attribute without the extraRowFields attribute has no effect.  Changing the weight
              allows the labels in the 'add' row to correspond to the weight of the column headers."%>

<%@ attribute name="debitCreditAmount" required="false"
	description="boolean whether the amount column is displayed as
              separate debit and credit columns.
              If true, debitCellProperty and creditCellProperty are required.
              As with all boolean tag attributes, if it is not provided, it defaults to false."%>

<%@ attribute name="currentBaseAmount" required="false"
	description="boolean whether the amount column is displayed as
              separate current and base columns.
              If true, currentCellProperty and baseCellProperty are required.
              As with all boolean tag attributes, if it is not provided, it defaults to false."%>

<%@ attribute name="debitCellProperty" required="false"
	description="the name of the form property to display or edit in the debit amount column"%>
<%@ attribute name="creditCellProperty" required="false"
	description="the name of the form property to display or edit in the credit amount column"%>

<%@ attribute name="currentCellProperty" required="false"
	description="the name of the form property to display or edit in the current amount column"%>
<%@ attribute name="baseCellProperty" required="false"
	description="the name of the form property to display or edit in the base amount column"%>

<%@ attribute name="includeObjectTypeCode" required="false"
	description="boolean indicating that the object type code column should be displayed.
              As with all boolean tag attributes, if it is not provided, it defaults to false."%>

<%@ attribute name="displayHidden" required="false"
	description="display values of hidden fields"%>
<%@ attribute name="decorator" required="false"
	description="propertyName of the AccountingLineDecorator associated with this accountingLine"%>

<%@ attribute name="accountingLineValuesMap" required="true"
	type="java.util.Map"
	description="map of the accounting line primitive fields and values"%>
<%@ attribute name="LookupOptionalFields" required="false"
              description="A comma separated list of names of optional accounting line fields
              that require a quickfinder." %>
<%@ attribute name="forcedReadOnlyFields" required="false"
	type="java.util.Map"
	description="foces the object code to become a read only field regardless of document state"%>
<%@ attribute name="hideFields" required="false"
    description="Comma delimited list of fields to hide for this type of accounting line" %>
<%@ attribute name="nestedIndex" required="false"
    description="A boolean whether we'll need a nested index that includes item index and account index or if we just need one index for the accountingLineIndex"%>
    
<%@ attribute name="customActions" required="false" fragment="true"
              description="For defines an attribute for invoking JSP/JSTL code to display custom actions on existing accounting lines" %>
<%@ attribute name="newLineCustomActions" required="false" fragment="true"
              description="For defines an attribute for invoking JSP/JSTL code to display custom actions on the new line" %>
	
<c:set var="salesTaxNeeded" value="false"/>
<logic:notEmpty name="KualiForm" property="${accountingLine}.salesTaxRequired">
	<c:set var="salesTaxRequired">
		<bean:write name="KualiForm" property="${accountingLine}.salesTaxRequired"/>
	</c:set>
	<c:if test="${salesTaxRequired == 'Yes' }">
		<c:set var="salesTaxNeeded" value="true"/>
	</c:if>
</logic:notEmpty>



<c:set var="rowCount" value="${empty extraRowFields ? 1 : 2}" />
<c:set var="rowCount" value="${salesTaxNeeded ? rowCount + 1 : rowCount}"/>
<c:set var="dataColumnCount" value="${columnCountUntilAmount -1}"/>

<%-- compute the count of the new rows --%>
<c:set var="numOfOptionalFields" value="${empty optionalFields ? 0 : fn:length(fn:split(optionalFields, ','))}" />	
<c:if test="${numOfOptionalFields > 0}">
	<c:set var="tempNumOfNewRows" value="${fn:substringBefore(numOfOptionalFields/dataColumnCount, '.')}"/>
	<c:set var="remainingCells" value="${numOfOptionalFields % dataColumnCount}"/>
	<c:set var="numOfNewRows" value="${remainingCells == 0 ? tempNumOfNewRows : tempNumOfNewRows + 1}"/>
</c:if>

<c:set var="rowspan" value="${rowCount + (isOptionalFieldsInNewRow ? 2*numOfNewRows: 0)}" />



<tr>
	<kul:htmlAttributeHeaderCell literalLabel="${rowHeader}:" scope="row" rowspan="${rowspan}">
		<%-- these hidden fields are inside a table cell to keep the HTML valid --%>
		<c:if test="${salesTaxNeeded}">
			<c:set var="hiddenFields" value="${hiddenFields},salesTax.versionNumber"/>
		</c:if>
		<c:forTokens var="hiddenField" items="${hiddenFields}" delims=",">
			<fin:hiddenAccountingLineField accountingLine="${accountingLine}"
				hiddenField="${hiddenField}" displayHidden="${displayHidden}" />
			<c:if test="${!empty baselineAccountingLine}">
				<fin:hiddenAccountingLineField
					accountingLine="${baselineAccountingLine}" isBaseline="true"
					hiddenField="${hiddenField}" displayHidden="${displayHidden}" />
			</c:if>
		</c:forTokens>
	</kul:htmlAttributeHeaderCell>

	<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
		accountingLine="${accountingLine}"
		baselineAccountingLine="${baselineAccountingLine}"
		field="chartOfAccountsCode" detailFunction="loadChartInfo"
		detailField="chart.finChartOfAccountDescription"
		attributes="${accountingLineAttributes}" lookup="false" inquiry="true"
		boClassSimpleName="Chart"
		readOnly="${(readOnly&&(empty editableFields['chartOfAccountsCode']))||!(empty forcedReadOnlyFields['chartOfAccountsCode'])}"
		displayHidden="${displayHidden}"
		accountingLineValuesMap="${accountingLineValuesMap}"
		anchor="accounting${actionInfix}${actionGroup}LineAnchor${0 + accountingLineIndex}" />
	<c:set var="details" value="" />
	
	<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
		accountingLine="${accountingLine}"
		baselineAccountingLine="${baselineAccountingLine}"
		field="accountNumber" detailFunction="loadAccountInfo"
		detailField="account.accountName"
		detailFields="${details}"
		attributes="${accountingLineAttributes}" lookup="true" inquiry="true"
		boClassSimpleName="Account"
		readOnly="${(readOnly&&(empty editableFields['accountNumber']))||!(empty forcedReadOnlyFields['accountNumber'])}"
		displayHidden="${displayHidden}"
		overrideField="accountExpiredOverride,nonFringeAccountOverride"
		lookupOrInquiryKeys="chartOfAccountsCode"
		accountingLineValuesMap="${accountingLineValuesMap}" />

	<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
		accountingLine="${accountingLine}"
		baselineAccountingLine="${baselineAccountingLine}"
		field="subAccountNumber" detailFunction="loadSubAccountInfo"
		detailField="subAccount.subAccountName"
		attributes="${accountingLineAttributes}" lookup="true" inquiry="true"
		boClassSimpleName="SubAccount"
		readOnly="${(readOnly&&(empty editableFields['subAccountNumber']))||!(empty forcedReadOnlyFields['subAccountNumber'])}"
		displayHidden="${displayHidden}"
		lookupOrInquiryKeys="chartOfAccountsCode,accountNumber"
		accountingLineValuesMap="${accountingLineValuesMap}" />
	<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
		accountingLine="${accountingLine}"
		baselineAccountingLine="${baselineAccountingLine}"
		field="financialObjectCode" detailFunction="loadObjectInfo"
		detailFunctionExtraParam="'${KualiForm.document.postingYear}', '${accountingLine}.objectType.name', '${accountingLine}.objectTypeCode', "
		detailField="objectCode.financialObjectCodeName"
		attributes="${accountingLineAttributes}" lookup="true" inquiry="true"
		boClassSimpleName="ObjectCode"
		readOnly="${(readOnly&&(empty editableFields['financialObjectCode']))|| !(empty forcedReadOnlyFields[accountingLineAttributes.financialObjectCode.name])}"
		displayHidden="${displayHidden}" overrideField="objectBudgetOverride"
		lookupOrInquiryKeys="chartOfAccountsCode"
		lookupUnkeyedFieldConversions="financialObjectTypeCode:${accountingLine}.objectTypeCode,"
		accountingLineValuesMap="${accountingLineValuesMap}"
		inquiryExtraKeyValues="universityFiscalYear=${KualiForm.document.postingYear}" />

	<c:if test="${includeObjectTypeCode}">
		<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
			accountingLine="${accountingLine}"
			baselineAccountingLine="${baselineAccountingLine}"
			field="objectTypeCode" detailFunction="loadObjectTypeInfo"
			detailField="objectType.name"
			attributes="${accountingLineAttributes}" lookup="true" inquiry="true"
			boClassSimpleName="ObjectType" conversionField="code"
			readOnly="${readOnly}" displayHidden="${displayHidden}"
			accountingLineValuesMap="${accountingLineValuesMap}" />
	</c:if>

	<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
		accountingLine="${accountingLine}"
		baselineAccountingLine="${baselineAccountingLine}"
		field="financialSubObjectCode" detailFunction="loadSubObjectInfo"
		detailFunctionExtraParam="'${KualiForm.document.postingYear}', "
		detailField="subObjectCode.financialSubObjectCodeName"
		attributes="${accountingLineAttributes}" lookup="true" inquiry="true"
		boClassSimpleName="SubObjCd"
		readOnly="${(readOnly&&(empty editableFields['financialSubObjectCode']))||!(empty forcedReadOnlyFields['financialSubObjectCode'])}"
		displayHidden="${displayHidden}"
		lookupOrInquiryKeys="chartOfAccountsCode,financialObjectCode,accountNumber"
		accountingLineValuesMap="${accountingLineValuesMap}"
		inquiryExtraKeyValues="universityFiscalYear=${KualiForm.document.postingYear}" />

	<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
		accountingLine="${accountingLine}"
		baselineAccountingLine="${baselineAccountingLine}" field="projectCode"
		detailFunction="loadProjectInfo" detailField="project.name"
		attributes="${accountingLineAttributes}" lookup="true" inquiry="true"
		boClassSimpleName="ProjectCode" conversionField="code"
		readOnly="${(readOnly&&(empty editableFields['projectCode']))||!(empty forcedReadOnlyFields['projectCode'])}"
		displayHidden="${displayHidden}"
		accountingLineValuesMap="${accountingLineValuesMap}" />

	<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
		accountingLine="${accountingLine}"
		baselineAccountingLine="${baselineAccountingLine}"
		field="organizationReferenceId"
		attributes="${accountingLineAttributes}" readOnly="${readOnly||!(empty forcedReadOnlyFields['organizationReferenceId'])}"
		displayHidden="${displayHidden}" />
	
	<c:if test="${not isOptionalFieldsInNewRow}">
		<c:forTokens items="${optionalFields}" delims=" ," var="currentField">
            <c:choose>
                <c:when test="${not empty KualiForm.forcedLookupOptionalFields[currentField]}">
                    <c:set var="key" value="${fn:split(KualiForm.forcedLookupOptionalFields[currentField], &quot;,&quot;)[0]}"/>
                    <c:set var="boClassFullName" value="${fn:split(KualiForm.forcedLookupOptionalFields[currentField], &quot;,&quot;)[1]}"/>
                    
 			        <fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
				        accountingLine="${accountingLine}"
				        baselineAccountingLine="${baselineAccountingLine}" lookup="true"
                        boClassFullName="${boClassFullName}" 
				        field="${currentField}" attributes="${accountingLineAttributes}"
                        lookupUnkeyedFieldConversions="${currentField}:${accountingLine}.${key}"
				        readOnly="${readOnly||!(empty forcedReadOnlyFields[currentField])}" displayHidden="${displayHidden}" />
                </c:when>
                <c:otherwise>
 			        <fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
				        accountingLine="${accountingLine}"
				        baselineAccountingLine="${baselineAccountingLine}"
				        field="${currentField}" attributes="${accountingLineAttributes}"
				        readOnly="${readOnly||!(empty forcedReadOnlyFields[currentField])}" displayHidden="${displayHidden}" />
                </c:otherwise>
            </c:choose>
		</c:forTokens>
	</c:if>

	<c:set var="delimitedhideFields" value=",${hideFields}," />
	<c:set var="delimitedField" value=",amount," />
	<c:if test="${not fn:contains(delimitedhideFields, delimitedField)}">
	  <c:choose>
		<c:when test="${debitCreditAmount}">
			<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
				cellProperty="${debitCellProperty}"
				attributes="${accountingLineAttributes}" field="amount"
				readOnly="${readOnly&&(empty editableFields['amount'])}"
				rowSpan="${rowspan}" dataFieldCssClass="amount" />
			<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
				cellProperty="${creditCellProperty}"
				attributes="${accountingLineAttributes}" field="amount"
				readOnly="${readOnly&&(empty editableFields['amount'])}"
				rowSpan="${rowspan}" dataFieldCssClass="amount" />
		</c:when>
		<c:when test="${currentBaseAmount}">
			<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
				accountingLine="${accountingLine}"
				baselineAccountingLine="${baselineAccountingLine}"
				cellProperty="${currentCellProperty}"
				attributes="${accountingLineAttributes}"
				field="currentBudgetAdjustmentAmount"
				readOnly="${readOnly&&(empty editableFields['currentBudgetAdjustmentAmount'])}"
				rowSpan="${rowspan}" dataFieldCssClass="amount" />
			<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
				accountingLine="${accountingLine}"
				baselineAccountingLine="${baselineAccountingLine}"
				cellProperty="${baseCellProperty}"
				attributes="${accountingLineAttributes}"
				field="baseBudgetAdjustmentAmount"
				readOnly="${(readOnly&&(empty editableFields['baseBudgetAdjustmentAmount']))||!KualiForm.editingMode['baseAmtEntry']}"
				rowSpan="${rowspan}" dataFieldCssClass="amount" />
		</c:when>
		<c:otherwise>
			<fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
				accountingLine="${accountingLine}"
				baselineAccountingLine="${baselineAccountingLine}" field="amount"
				attributes="${accountingLineAttributes}"
				readOnly="${(readOnly&&(empty editableFields['amount']))||!(empty forcedReadOnlyFields['amount'])}"
				displayHidden="${displayHidden}" rowSpan="${rowspan}"
				dataFieldCssClass="amount" />
		</c:otherwise>
	  </c:choose>
	</c:if>

	<c:if test="${!readOnly}">
		<c:choose>
			<c:when test="${empty extraRowFields && (numOfOptionalFields == 0 || !isOptionalFieldsInNewRow) && !salesTaxNeeded}">
				<fin:accountingLineActionDataCell
					dataCellCssClass="${dataCellCssClass}" actionGroup="${actionGroup}"
					actionInfix="${actionInfix}"
					accountingAddLineIndex="${accountingAddLineIndex}"
					accountingLineIndex="${accountingLineIndex}"
					decorator="${decorator}" 
          customActions="${customActions}"
          newLineCustomActions="${newLineCustomActions}"
					nestedIndex="${nestedIndex}"/>
			</c:when>
			<c:otherwise>
				<td style="border-bottom-style: none" rowspan="${rowspan - 1}">
					<!-- No CSS class or bottom border so this cell looks like the start of one that spans two rows. -->
					&nbsp; <!-- This nbsp makes Firefox draw the left border of this cell. -->
				</td>
			</c:otherwise>
		</c:choose>
	</c:if>
</tr>

<%-- put the optional fields into the separate rows in accounting line row --%>
<c:if test="${isOptionalFieldsInNewRow && numOfOptionalFields > 0}">
	<c:forEach begin="0" end="${numOfNewRows-1}" var="currentRowCount">
		<tr>		
		    <c:forTokens items="${optionalFields}" delims=" ," var="currentField" begin="${currentRowCount*dataColumnCount}" end="${(currentRowCount+1)*dataColumnCount -1}">
			    <kul:htmlAttributeHeaderCell attributeEntry="${accountingLineAttributes[currentField]}"/>
		    </c:forTokens>
		    
		    <c:if test="${currentRowCount == (numOfNewRows-1) && remainingCells != 0}">
		    	<c:forEach begin="1" end="${dataColumnCount - remainingCells}">
		        	<td class="${dataCellCssClass}" rowspan="2">&nbsp;</td>
		        </c:forEach>	 
	        </c:if>
		</tr>
		
		<tr>
		    <c:forTokens items="${optionalFields}" delims=" ," var="currentField" begin="${currentRowCount*dataColumnCount}" end="${(currentRowCount+1)*dataColumnCount -1}">
              <c:choose>
                 <c:when test="${not empty KualiForm.forcedLookupOptionalFields[currentField]}">
                    <c:set var="lookupField" value="${fn:split(KualiForm.forcedLookupOptionalFields[currentField], &quot;;&quot;)[0]}"/>
                    <c:set var="boClassFullName" value="${fn:split(KualiForm.forcedLookupOptionalFields[currentField], &quot;;&quot;)[1]}"/>
                    <c:set var="mappedFields" value="${fn:split(KualiForm.forcedLookupOptionalFields[currentField], &quot;;&quot;)[2]}"/>
					
                    <c:forTokens var="mappedField" items="${mappedFields}" delims=",">
					   <c:set var="conversionField" value="${fn:split(mappedField, &quot;:&quot;)[0]}"/>
                       <c:set var="lineField" value="${fn:split(mappedField, &quot;:&quot;)[1]}"/>
                       <c:set var="lookupParameters" value="${lookupParameters}${accountingLine}.${lineField}:${conversionField},"/>
                       <c:set var="fieldConversions" value="${fieldConversions}${conversionField}:${accountingLine}.${lineField},"/>
                    </c:forTokens>
                    
 			        <fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
				        accountingLine="${accountingLine}"
				        baselineAccountingLine="${baselineAccountingLine}" lookup="true"
                        boClassFullName="${boClassFullName}" 
				        field="${currentField}" conversionField="${lookupField}" attributes="${accountingLineAttributes}"
                        lookupUnkeyedFieldConversions="${fieldConversions}" lookupParameters="${lookupParameters}"
				        readOnly="${readOnly||!(empty forcedReadOnlyFields[currentField])}" displayHidden="${displayHidden}" />
				  </c:when>
				  <c:otherwise>
					 <fin:accountingLineDataCell dataCellCssClass="${dataCellCssClass}"
						 accountingLine="${accountingLine}"
					     baselineAccountingLine="${baselineAccountingLine}"
						 field="${currentField}" attributes="${accountingLineAttributes}"
						 readOnly="${readOnly}" displayHidden="${displayHidden}" />
				  </c:otherwise>
			</c:choose>
			</c:forTokens>
			
			<c:if test="${(currentRowCount == (numOfNewRows - 1)) && empty extraRowFields && !readOnly}">
				<fin:accountingLineActionDataCell
					dataCellCssClass="${dataCellCssClass}" actionGroup="${actionGroup}"
					actionInfix="${actionInfix}"
					accountingAddLineIndex="${accountingAddLineIndex}"
					accountingLineIndex="${accountingLineIndex}"
					decorator="${decorator}" 
          customActions="${customActions}" 
          newLineCustomActions="${newLineCustomActions}"
          nestedIndex="${nestedIndex}" />
			</c:if>
		</tr>
	</c:forEach>
</c:if>

<%-- optional second row of accounting fields, between index and amount columns --%>

<c:if test="${!empty extraRowFields}">
	<tr>
		<td colspan="${dataColumnCount}"
			style="padding: 0px;border: 0px none ;">
			<c:set var="hasMultipleActionButtons"
				value="${!readOnly && actionGroup == 'existingLine' && !salesTaxNeeded}" />
			<%-- Multiple buttons increase this row's height, but the table can't automatically cover that extra height, so kludge it. --%>
			<table cellpadding="0" cellspacing="0"
				style="width: 100%;border: 0px;${hasMultipleActionButtons ? 'height: 60px;' : ''}">
				<tr>
					<c:set var="delimitedExtraRowFields" value=",${extraRowFields}," />
					<c:if
						test="${fn:contains(delimitedExtraRowFields, ',referenceOriginCode,')}">
						<fin:accountingLineDataCell field="referenceOriginCode"
							lookup="true" inquiry="true" boClassSimpleName="OriginationCode"
							boPackageName="org.kuali.kfs.bo"
							conversionField="financialSystemOriginationCode"
							detailFunction="loadOriginationInfo"
							detailField="referenceOrigin.financialSystemDatabaseName"
							accountingLine="${accountingLine}"
							baselineAccountingLine="${baselineAccountingLine}"
							attributes="${accountingLineAttributes}"
							accountingLineValuesMap="${accountingLineValuesMap}"
							dataCellCssClass="${dataCellCssClass}"
							labelFontWeight="${extraRowLabelFontWeight}"
							readOnly="${readOnly}" displayHidden="${displayHidden}" />
					</c:if>					
					<c:if
						test="${fn:contains(delimitedExtraRowFields, ',referenceTypeCode,')}">
						<fin:accountingLineDataCell field="referenceTypeCode"
							lookup="true" inquiry="true" boClassSimpleName="DocumentType"
							boPackageName="org.kuali.core.bo"
							conversionField="financialDocumentTypeCode"
							detailFunction="loadDocumentTypeInfo"
							detailField="referenceType.financialDocumentName"
							accountingLine="${accountingLine}"
							baselineAccountingLine="${baselineAccountingLine}"
							attributes="${accountingLineAttributes}"
							accountingLineValuesMap="${accountingLineValuesMap}"
							dataCellCssClass="${dataCellCssClass}"
							labelFontWeight="${extraRowLabelFontWeight}"
							readOnly="${readOnly}" displayHidden="${displayHidden}" />
					</c:if>
					<%-- referenceNumber displays like an unknown field, but explicitly here to follow referenceOriginCode. --%>
					<c:if
						test="${fn:contains(delimitedExtraRowFields, ',referenceNumber,')}">
						<fin:accountingLineDataCell field="referenceNumber"						
							accountingLine="${accountingLine}"
							baselineAccountingLine="${baselineAccountingLine}"
							attributes="${accountingLineAttributes}"
							dataCellCssClass="${dataCellCssClass}"
							labelFontWeight="${extraRowLabelFontWeight}"
							readOnly="${readOnly}" displayHidden="${displayHidden}" />
					</c:if>
					<c:set var="knownExtraFields"
						value=",referenceOriginCode,referenceTypeCode,referenceNumber," />
					<c:forTokens items="${extraRowFields}" delims="," var="extraField">
						<c:if test="${not fn:contains(knownExtraFields, extraField)}">
							<fin:accountingLineDataCell field="${extraField}"
								accountingLine="${accountingLine}"
								baselineAccountingLine="${baselineAccountingLine}"
								attributes="${accountingLineAttributes}"
								dataCellCssClass="${dataCellCssClass}"
								labelFontWeight="${extraRowLabelFontWeight}"
								readOnly="${readOnly}" displayHidden="${displayHidden}" />
						</c:if>
					</c:forTokens>
					<%-- use up the remaining space, to push the extra fields together --%>
					<td class="${dataCellCssClass}" width="100%">
						&nbsp;
						<%-- This nbsp causes Firefox to draw the left and bottom borders of this empty cell. --%>
					</td>
				</tr>
			</table>
		</td>
		<%-- Assert rowCount == 2.  Browser skips 2-row amount columns here. --%>
		<!-- Action buttons go on the second row here, if any, to get the right default tab navigation. -->

		<c:if test="${!readOnly && !salesTaxNeeded}">
			<fin:accountingLineActionDataCell
				dataCellCssClass="${dataCellCssClass}" actionGroup="${actionGroup}"
				actionInfix="${actionInfix}"
				accountingAddLineIndex="${accountingAddLineIndex}"
				accountingLineIndex="${accountingLineIndex}"
				decorator="${decorator}"
        customActions="${customActions}" 
        newLineCustomActions="${newLineCustomActions}"
				nestedIndex="${nestedIndex}" />
		</c:if>
	</tr>
</c:if>
<c:if test="${salesTaxNeeded}">
	<c:set var="hasMultipleActionButtons"
				value="${!readOnly && actionGroup == 'existingLine'}" />
	<tr>
	<td colspan="${dataColumnCount}"
		style="padding: 0px;border: 0px none ;">
		<fin:salesTaxAccountingLineRow 
			dataCellCssClass="${dataCellCssClass}"
			accountingLine="${accountingLine}"
			baselineAccountingLine="${baselineAccountingLine}"
			accountingLineAttributes="${accountingLineAttributes}"
			displayHidden="${displayHidden}"
			accountingLineValuesMap="${accountingLineValuesMap}"
			salesTaxRowlabelFontWeight="bold"
			readOnly="${readOnly}"
			hasMultipleActionButtons="${hasMultipleActionButtons }"/>
	</td>

<!-- Action buttons go on the second row here, if any, to get the right default tab navigation. -->	
	<c:if test="${!readOnly}">
		<fin:accountingLineActionDataCell
			dataCellCssClass="${dataCellCssClass}" actionGroup="${actionGroup}"
			actionInfix="${actionInfix}"
			accountingAddLineIndex="${accountingAddLineIndex}"
			accountingLineIndex="${accountingLineIndex}"
      customActions="${customActions}" 
      newLineCustomActions="${newLineCustomActions}"
			decorator="${decorator}"
			nestedIndex="${nestedIndex}" />
	</c:if>
	</tr>
</c:if>