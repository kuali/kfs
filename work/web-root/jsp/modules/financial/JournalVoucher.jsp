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

<c:set var="journalVoucherAttributes"
	value="${DataDictionary['JournalVoucherDocument'].attributes}" />
<c:set var="readOnly"
	value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:documentPage showDocumentInfo="true"
	documentTypeName="JournalVoucherDocument"
	htmlFormAction="financialJournalVoucher" renderMultipart="true"
	showTabButtons="true">
	<kfs:hiddenDocumentFields />
	<kfs:documentOverview editingMode="${KualiForm.editingMode}" />
	<!-- JOURNAL VOUCHER SPECIFIC FIELDS -->
	<kul:tab tabTitle="Journal Voucher Details" defaultOpen="true"
		tabErrorKey="${KFSConstants.EDIT_JOURNAL_VOUCHER_ERRORS}">
		<div class="tab-container" align=center>
		<h3>Journal Voucher Details</h3>
		<table cellpadding=0 class="datatable"
			summary="view/edit ad hoc recipients">
			<tbody>

				<tr>
					<th width="35%" class="bord-l-b">
					<div align="right"><kul:htmlAttributeLabel
						labelFor="selectedAccountingPeriod" attributeEntry="${journalVoucherAttributes.accountingPeriod}"
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
						<html:select property="selectedAccountingPeriod" onchange="submitForChangedAccountingPeriod()">
							<c:forEach items="${KualiForm.accountingPeriods}"
								var="accountingPeriod">
								<c:set var="accountingPeriodCompositeValue"
									value="${accountingPeriod.universityFiscalPeriodCode}${accountingPeriod.universityFiscalYear}" />
								<c:choose>
									<c:when
										test="${KualiForm.selectedAccountingPeriod==accountingPeriodCompositeValue}">
										<html:option
											value="${accountingPeriodCompositeValue}"><c:out
											value="${accountingPeriod.universityFiscalPeriodName}" /></html:option>
									</c:when>
									<c:otherwise>
										<html:option
											value="${accountingPeriodCompositeValue}" ><c:out
											value="${accountingPeriod.universityFiscalPeriodName}" /></html:option>
									</c:otherwise>
								</c:choose>
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
					</c:if> <c:if test="${!readOnly}">
						<SCRIPT type="text/javascript">
						<!--
						    function submitForChangedBalanceType() {
					    		document.forms[0].submit();
						    }
						//-->
						</SCRIPT>
						<html:select property="selectedBalanceType.code" onchange="submitForChangedBalanceType()">
						<c:forEach items="${KualiForm.balanceTypes}" var="balanceType">
								<c:choose>
									<c:when
										test="${KualiForm.selectedBalanceType.code==balanceType.code}">
										<html:option value="${balanceType.code}"><c:out value="${balanceType.code}" /> -
										<c:out value="${balanceType.name}" /></html:option>
									</c:when>
									<c:otherwise>
										<html:option value="${balanceType.code}" ><c:out
											value="${balanceType.code}" /> - <c:out
											value="${balanceType.name}" /></html:option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</html:select>
						<NOSCRIPT><html:submit value="refresh"
							title="press this button to refresh the page after changing the balance type" alt="press this button to refresh the page after changing the balance type" />
						</NOSCRIPT>
						<kul:lookup
							boClassName="org.kuali.kfs.coa.businessobject.BalanceTyp"
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

	<c:set var="isExtEncumbrance" value="${KualiForm.selectedBalanceType.code==KFSConstants.BALANCE_TYPE_EXTERNAL_ENCUMBRANCE}" />
	<c:set var="isDebitCreditAmount" value="${KualiForm.selectedBalanceType.financialOffsetGenerationIndicator}" />

	<c:choose>
		<c:when test="${isExtEncumbrance && isDebitCreditAmount}">
			<c:set var="attributeGroupName" value="source-withDebitCreditExtEncumbrance"/>
		</c:when>
		<c:when test="${!isExtEncumbrance && isDebitCreditAmount}">
			<c:set var="attributeGroupName" value="source-withDebitCredit"/>
		</c:when>		
		<c:when test="${isExtEncumbrance && !isDebitCreditAmount}">
			<c:set var="attributeGroupName" value="source-withExtEncumbrance"/>
		</c:when>
		<c:otherwise>
			<c:set var="attributeGroupName" value="source"/>
		</c:otherwise>
	</c:choose>

	<kul:tab tabTitle="Accounting Lines" defaultOpen="true" tabErrorKey="${KFSConstants.ACCOUNTING_LINE_ERRORS}">
		<sys:accountingLines>
			<sys:accountingLineGroup newLinePropertyName="newSourceLine" collectionPropertyName="document.sourceAccountingLines" collectionItemPropertyName="document.sourceAccountingLine" attributeGroupName="${attributeGroupName}" />
		</sys:accountingLines>
	</kul:tab>
		
	<gl:generalLedgerPendingEntries />
	<kul:notes />
	<kul:adHocRecipients />
	<kul:routeLog />
	<kul:panelFooter />
	<kfs:documentControls transactionalDocument="true" extraButtons="${KualiForm.extraButtons}"/>
</kul:documentPage>
