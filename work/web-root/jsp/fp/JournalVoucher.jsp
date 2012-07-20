<%--
 Copyright 2005-2006 The Kuali Foundation
 
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

<c:set var="journalVoucherAttributes"
	value="${DataDictionary['JournalVoucherDocument'].attributes}" />
<c:set var="readOnly"
	value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:documentPage showDocumentInfo="true"
	documentTypeName="JournalVoucherDocument"
	htmlFormAction="financialJournalVoucher" renderMultipart="true"
	showTabButtons="true">
	<sys:hiddenDocumentFields />
	<sys:documentOverview editingMode="${KualiForm.editingMode}" />
	<!-- JOURNAL VOUCHER SPECIFIC FIELDS -->
	<kul:tab tabTitle="Journal Voucher Details" defaultOpen="true"
		tabErrorKey="${KFSConstants.EDIT_JOURNAL_VOUCHER_ERRORS}">
		<div class="tab-container" align=center>
		<h3>Journal Voucher Details</h3>
		<table cellpadding=0 class="datatable"
			summary="Journal Voucher Details">
			<tbody>

				<tr>
					<th width="35%" class="bord-l-b">
					<div align="right"><kul:htmlAttributeLabel
						labelFor="selectedAccountingPeriod" attributeEntry="${journalVoucherAttributes.accountingPeriod}"
						useShortLabel="false" /></div>
					</th>
					<td class="datacell-nowrap">
					<c:if test="${readOnly}">
                        ${KualiForm.accountingPeriod.universityFiscalPeriodName}
                        <html:hidden property="selectedAccountingPeriod" />
					</c:if> 
					<c:if test="${!readOnly}">
						<SCRIPT type="text/javascript">
						<!--
						    function submitForChangedAccountingPeriod() {
					    		document.forms[0].submit();
						    }
						//-->
						</SCRIPT>
						<html:select property="selectedAccountingPeriod" onchange="submitForChangedAccountingPeriod()">
							<c:forEach items="${KualiForm.accountingPeriods}" var="accountingPeriod">
								<c:set var="accountingPeriodCompositeValue" value="${accountingPeriod.universityFiscalPeriodCode}${accountingPeriod.universityFiscalYear}" />
								<html:option value="${accountingPeriodCompositeValue}"><c:out value="${accountingPeriod.universityFiscalPeriodName}" /></html:option>
							</c:forEach>
						</html:select>
						
						<NOSCRIPT><html:submit value="refresh"
							title="press this button to refresh the page after changing the accounting period" alt="press this button to refresh the page after changing the accounting period" />
						</NOSCRIPT>
					</c:if></td>
				</tr>
				<tr>
					<th width="35%" class="bord-l-b">
					<div align="right"><kul:htmlAttributeLabel
						labelFor="" attributeEntry="${journalVoucherAttributes.balanceTypeCode}"
						useShortLabel="false" /></div>
					</th>
					<td class="datacell-nowrap">
					<c:if test="${readOnly}">
                        ${KualiForm.selectedBalanceType.financialBalanceTypeName}
					</c:if> 
					
					<c:if test="${!readOnly}">
						<SCRIPT type="text/javascript">
						<!--
						    function submitForChangedBalanceType() {
					    		document.forms[0].submit();
						    }
						//-->
						</SCRIPT>
						<html:select property="selectedBalanceType.code" onchange="submitForChangedBalanceType()">
							<c:forEach items="${KualiForm.balanceTypes}" var="balanceType">
								<html:option value="${balanceType.code}" >
									<c:out value="${balanceType.codeAndDescription}" /> 
								</html:option>
							</c:forEach>
						</html:select>
						<NOSCRIPT><html:submit value="refresh"
							title="press this button to refresh the page after changing the balance type" alt="press this button to refresh the page after changing the balance type" />
						</NOSCRIPT>
						<kul:lookup
							boClassName="org.kuali.kfs.coa.businessobject.BalanceType"
							fieldConversions="code:selectedBalanceType.code"
							lookupParameters="selectedBalanceType.code:code" 
							 fieldLabel="${journalVoucherAttributes.balanceTypeCode.label}" />
					</c:if></td>
				</tr>
				<tr>
                    <kul:htmlAttributeHeaderCell
                        attributeEntry="${journalVoucherAttributes.reversalDate}"
                        horizontal="true"
                        width="35%"
                        />
                    <td class="datacell-nowrap"><kul:htmlControlAttribute
                        attributeEntry="${journalVoucherAttributes.reversalDate}"
                        datePicker="true"
                        property="document.reversalDate"
                        readOnly="${readOnly}"
                        readOnlyAlternateDisplay="${fn:escapeXml(KualiForm.formattedReversalDate)}"
                        /></td>
				</tr>
			</tbody>
		</table>
		</div>
	</kul:tab>

	<c:set var="isEncumbrance" value="${KualiForm.isEncumbranceBalanceType}" />
	<c:set var="isDebitCreditAmount" value="${KualiForm.selectedBalanceType.financialOffsetGenerationIndicator}" />

	<c:choose>
		<c:when test="${isEncumbrance && isDebitCreditAmount}">
			<c:set var="attributeGroupName" value="source-withDebitCreditEncumbrance"/>
		</c:when>
		<c:when test="${!isEncumbrance && isDebitCreditAmount}">
			<c:set var="attributeGroupName" value="source-withDebitCredit"/>
		</c:when>		
		<c:when test="${isEncumbrance && !isDebitCreditAmount}">
			<c:set var="attributeGroupName" value="source-withEncumbrance"/>
		</c:when>
		<c:otherwise>
			<c:set var="attributeGroupName" value="source"/>
		</c:otherwise>
	</c:choose>

	<kul:tab tabTitle="Accounting Lines" defaultOpen="true" tabErrorKey="${KFSConstants.NEW_SOURCE_LINE_ERRORS}">
		<sys-java:accountingLines>
			<sys-java:accountingLineGroup newLinePropertyName="newSourceLine" collectionPropertyName="document.sourceAccountingLines" collectionItemPropertyName="document.sourceAccountingLine" attributeGroupName="${attributeGroupName}" />
		</sys-java:accountingLines>
	</kul:tab>
		
	<gl:generalLedgerPendingEntries />
	<kul:notes />
	<kul:adHocRecipients />
	<kul:routeLog />
	<kul:superUserActions />
	<kul:panelFooter />
	<sys:documentControls transactionalDocument="true" extraButtons="${KualiForm.extraButtons}"/>
</kul:documentPage>
