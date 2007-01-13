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
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd"%>
<c:set var="journalVoucherAttributes"
	value="${DataDictionary['KualiJournalVoucherDocument'].attributes}" />
<c:set var="readOnly"
	value="${!empty KualiForm.editingMode['viewOnly']}" />

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiJournalVoucherDocument"
	htmlFormAction="financialJournalVoucher" renderMultipart="true"
	showTabButtons="true">
	<kul:hiddenDocumentFields />
	<kul:documentOverview editingMode="${KualiForm.editingMode}" />
	<!-- JOURNAL VOUCHER SPECIFIC FIELDS -->
	<kul:tab tabTitle="Journal Voucher Details" defaultOpen="true"
		tabErrorKey="${Constants.EDIT_JOURNAL_VOUCHER_ERRORS}">
		<div class="tab-container" align=center>
		<div class="h2-container">
		<h2>Journal Voucher Details</h2>
		</div>
		<table cellpadding=0 class="datatable"
			summary="view/edit ad hoc recipients">
			<tbody>

				<tr>
					<th width="35%" class="bord-l-b">
					<div align="right"><kul:htmlAttributeLabel
						attributeEntry="${journalVoucherAttributes.accountingPeriod}"
						useShortLabel="false" /></div>
					</th>
					<td class="datacell-nowrap"><c:if test="${readOnly}">
                        ${KualiForm.accountingPeriod.universityFiscalPeriodName}
                        <html:hidden property="selectedAccountingPeriod" />
					</c:if> <c:if test="${!readOnly}">
						<SCRIPT type="text/javascript">
						<!--
						    function submitForChangedAccountingPeriod() {
					    		document.forms[0].submit();
						    }
						//-->
						</SCRIPT>
						<select name="selectedAccountingPeriod"
							onchange="submitForChangedAccountingPeriod()">
							<c:forEach items="${KualiForm.accountingPeriods}"
								var="accountingPeriod">
								<c:set var="accountingPeriodCompositeValue"
									value="${accountingPeriod.universityFiscalPeriodCode}${accountingPeriod.universityFiscalYear}" />
								<c:choose>
									<c:when
										test="${KualiForm.selectedAccountingPeriod==accountingPeriodCompositeValue}">
										<option
											value='<c:out value="${accountingPeriodCompositeValue}"/>'
											selected="selected"><c:out
											value="${accountingPeriod.universityFiscalPeriodName}" /></option>
									</c:when>
									<c:otherwise>
										<option
											value='<c:out value="${accountingPeriodCompositeValue}" />'><c:out
											value="${accountingPeriod.universityFiscalPeriodName}" /></option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>
						<NOSCRIPT><html:submit value="refresh"
							alt="press this button to refresh the page after changing the accounting period" />
						</NOSCRIPT>
						<kul:lookup
							boClassName="org.kuali.module.chart.bo.AccountingPeriod" 
							fieldLabel="${journalVoucherAttributes.accountingPeriod.label}"/>
					</c:if></td>
				</tr>
				<tr>
					<th width="35%" class="bord-l-b">
					<div align="right"><kul:htmlAttributeLabel
						attributeEntry="${journalVoucherAttributes.balanceTypeCode}"
						useShortLabel="false" /></div>
					</th>
					<td class="datacell-nowrap"><html:hidden
						property="originalBalanceType"
						value="${KualiForm.selectedBalanceType.code}" /> <html:hidden
						property="selectedBalanceType.financialOffsetGenerationIndicator" />
					<c:if test="${readOnly}">
                        ${KualiForm.selectedBalanceType.financialBalanceTypeName}
						<html:hidden property="selectedBalanceType.code" />
						<html:hidden property="selectedBalanceType.name" />
					</c:if> <c:if test="${!readOnly}">
						<SCRIPT type="text/javascript">
						<!--
						    function submitForChangedBalanceType() {
					    		document.forms[0].submit();
						    }
						//-->
						</SCRIPT>
						<select name="selectedBalanceType.code"
							onchange="submitForChangedBalanceType()">
							<c:forEach items="${KualiForm.balanceTypes}" var="balanceType">
								<c:choose>
									<c:when
										test="${KualiForm.selectedBalanceType.code==balanceType.code}">
										<option value='<c:out value="${balanceType.code}"/>'
											selected="selected"><c:out value="${balanceType.code}" /> -
										<c:out value="${balanceType.name}" /></option>
									</c:when>
									<c:otherwise>
										<option value='<c:out value="${balanceType.code}" />'><c:out
											value="${balanceType.code}" /> - <c:out
											value="${balanceType.name}" /></option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>
						<NOSCRIPT><html:submit value="refresh"
							alt="press this button to refresh the page after changing the balance type" />
						</NOSCRIPT>
						<kul:lookup
							boClassName="org.kuali.module.chart.bo.codes.BalanceTyp"
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
                        readOnlyAlternateDisplay="${KualiForm.formattedReversalDate}"
                        /></td>
				</tr>
			</tbody>
		</table>
		</div>
	</kul:tab>
	<fin:voucherAccountingLines
		isDebitCreditAmount="${KualiForm.selectedBalanceType.financialOffsetGenerationIndicator}"
		displayExternalEncumbranceFields="${KualiForm.selectedBalanceType.code==Constants.BALANCE_TYPE_EXTERNAL_ENCUMBRANCE}"
		editingMode="${KualiForm.editingMode}"
		editableAccounts="${KualiForm.editableAccounts}"
		includeObjectTypeCode="true" />
	<kul:generalLedgerPendingEntries />
	<kul:notes />
	<kul:adHocRecipients />
	<kul:routeLog />
	<kul:panelFooter />
	<kul:documentControls transactionalDocument="true" />
</kul:documentPage>
