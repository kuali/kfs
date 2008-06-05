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

<%@ attribute name="generalLedgerPendingEntries" required="false" type="java.util.List" %>
<%@ attribute name="generalLedgerPendingEntriesProperty" required="false" %>
<%@ attribute name="generalLedgerPendingEntryProperty" required="false" %>

<c:set var="generalLedgerPendingEntriesList" value="${generalLedgerPendingEntries}" />
<c:if test="${empty generalLedgerPendingEntries}">
	<c:set var="generalLedgerPendingEntriesList" value="${KualiForm.document.generalLedgerPendingEntries}" />
</c:if>
<c:if test="${empty generalLedgerPendingEntriesProperty}">
	<c:set var="generalLedgerPendingEntriesProperty" value="document.generalLedgerPendingEntries" />
</c:if>
<c:if test="${empty generalLedgerPendingEntryProperty}">
	<c:set var="generalLedgerPendingEntryProperty" value="document.generalLedgerPendingEntry" />
</c:if>

<%-- are we in a maint doc? --%>
<c:set var="maintenanceViewMode" value="${requestScope[Constants.PARAM_MAINTENANCE_VIEW_MODE]}" />
<c:set var="isMaintenance" value="${KualiForm.class.name eq 'org.kuali.core.web.struts.form.KualiMaintenanceForm' || maintenanceViewMode eq Constants.PARAM_MAINTENANCE_VIEW_MODE_MAINTENANCE}" />

<%-- if we are maintenance, then we need to rename the generalLedgerPendingEntryProperty, where document.newMaintainableObject actually gives us the business object --%>
<c:if test="${isMaintenance}">
  <c:set var="generalLedgerPendingEntryProperty" value="${kfsfunc:renamePropertyForMaintenanceFramework(generalLedgerPendingEntryProperty)}" />
</c:if>

<kul:tab tabTitle="General Ledger Pending Entries" defaultOpen="false" tabErrorKey="${KFSConstants.GENERAL_LEDGER_PENDING_ENTRIES_TAB_ERRORS}">
<div class="tab-container" align=center>
		<div class="h2-container">
		<h2>General Ledger Pending Entries <kul:lookup boClassName="org.kuali.kfs.bo.GeneralLedgerPendingEntry" lookupParameters="document.documentNumber:documentNumber" hideReturnLink="true" suppressActions="true"/></h2>
		</div>
	 <table cellpadding="0" cellspacing="0" class="datatable" summary="view/edit pending entries">

	<c:if test="${empty generalLedgerPendingEntriesList}">
		<tr>
			<td class="datacell" height="50"colspan="12"><div align="center">There are currently no General Ledger Pending Entries associated with this Transaction Processing document.</div></td>
		</tr>
	</c:if>
	<c:if test="${!empty generalLedgerPendingEntriesList}">
        <c:set var="entryAttributes" value="${DataDictionary.GeneralLedgerPendingEntry.attributes}" />
		<tr>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.transactionLedgerEntrySequenceNumber}" hideRequiredAsterisk="true" scope="col"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.universityFiscalYear}" hideRequiredAsterisk="true" scope="col"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.chartOfAccountsCode}" hideRequiredAsterisk="true" scope="col"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.accountNumber}" hideRequiredAsterisk="true" scope="col"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.subAccountNumber}" hideRequiredAsterisk="true" scope="col"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.financialObjectCode}" hideRequiredAsterisk="true" scope="col"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.financialSubObjectCode}" hideRequiredAsterisk="true" scope="col"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.projectCode}" hideRequiredAsterisk="true" scope="col"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.financialDocumentTypeCode}" hideRequiredAsterisk="true" scope="col"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.financialBalanceTypeCode}" hideRequiredAsterisk="true" scope="col"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.financialObjectTypeCode}" hideRequiredAsterisk="true" scope="col"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.transactionLedgerEntryAmount}" hideRequiredAsterisk="true" scope="col"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.transactionDebitCreditCode}" hideRequiredAsterisk="true" scope="col"/>
		</tr>
		<logic:iterate id="generalLedgerPendingEntry" name="KualiForm" property="${generalLedgerPendingEntriesProperty}" indexId="ctr">
			<tr>
				<th class="datacell center"><html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].transactionLedgerEntrySequenceNumber" write="true" value="${generalLedgerPendingEntry.transactionLedgerEntrySequenceNumber}" /></th>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.kfs.bo.Options" keyValues="universityFiscalYear=${generalLedgerPendingEntry.universityFiscalYear}" render="true">
            <html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].universityFiscalYear" value="${generalLedgerPendingEntry.universityFiscalYear}" write="true" />
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.module.chart.bo.Chart" keyValues="chartOfAccountsCode=${generalLedgerPendingEntry.chartOfAccountsCode}" render="true">
						<html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].chartOfAccountsCode" write="true" value="${generalLedgerPendingEntry.chartOfAccountsCode}" />
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.module.chart.bo.Account" keyValues="chartOfAccountsCode=${generalLedgerPendingEntry.chartOfAccountsCode}&accountNumber=${generalLedgerPendingEntry.accountNumber}" render="true">
						<html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].accountNumber" write="true" value="${generalLedgerPendingEntry.accountNumber}" />
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.module.chart.bo.SubAccount" keyValues="chartOfAccountsCode=${generalLedgerPendingEntry.chartOfAccountsCode}&accountNumber=${generalLedgerPendingEntry.accountNumber}&subAccountNumber=${generalLedgerPendingEntry.subAccountNumber}" render="${ ! generalLedgerPendingEntry.subAccountNumberBlank}">
						<html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].subAccountNumber" write="true" value="${generalLedgerPendingEntry.subAccountNumber}" />
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.module.chart.bo.ObjectCode" keyValues="financialObjectCode=${generalLedgerPendingEntry.financialObjectCode}&chartOfAccountsCode=${generalLedgerPendingEntry.chartOfAccountsCode}&universityFiscalYear=${generalLedgerPendingEntry.universityFiscalYear}" render="${ ! generalLedgerPendingEntry.financialObjectCodeBlank}">
						<html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].financialObjectCode" write="true" value="${generalLedgerPendingEntry.financialObjectCode}" />
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.module.chart.bo.SubObjCd" keyValues="financialSubObjectCode=${generalLedgerPendingEntry.financialSubObjectCode}&financialObjectCode=${generalLedgerPendingEntry.financialObjectCode}&chartOfAccountsCode=${generalLedgerPendingEntry.chartOfAccountsCode}&universityFiscalYear=${generalLedgerPendingEntry.universityFiscalYear}" render="${ ! generalLedgerPendingEntry.financialSubObjectCodeBlank}">
						<html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].financialSubObjectCode" write="true" value="${generalLedgerPendingEntry.financialSubObjectCode}" />
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.module.chart.bo.ProjectCode" keyValues="code=${generalLedgerPendingEntry.projectCode}" render="${ ! generalLedgerPendingEntry.projectCodeBlank}">
						<html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].projectCode" write="true" value="${generalLedgerPendingEntry.projectCode}" />
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.core.bo.DocumentType" keyValues="financialDocumentTypeCode=${generalLedgerPendingEntry.financialDocumentTypeCode}" render="true">
						<html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].financialDocumentTypeCode" write="true" value="${generalLedgerPendingEntry.financialDocumentTypeCode}" />
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.module.chart.bo.codes.BalanceTyp" keyValues="code=${generalLedgerPendingEntry.financialBalanceTypeCode}" render="true">
						<html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].financialBalanceTypeCode" write="true" value="${generalLedgerPendingEntry.financialBalanceTypeCode}" />
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.module.chart.bo.ObjectType" keyValues="code=${generalLedgerPendingEntry.financialObjectTypeCode}" render="${ ! generalLedgerPendingEntry.financialObjectTypeCodeBlank}">
						<html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].financialObjectTypeCode" write="true" value="${generalLedgerPendingEntry.financialObjectTypeCode}" />
					</kul:inquiry>
				</td>
				<td class="datacell center"><html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].transactionLedgerEntryAmount" write="true" value="${generalLedgerPendingEntry.transactionLedgerEntryAmount}" /></td>
				<td class="datacell center"><html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].transactionDebitCreditCode" write="true" value="${generalLedgerPendingEntry.transactionDebitCreditCode}" />&nbsp;</td>
			</tr>
		</logic:iterate>
	</c:if>
	</table>
</div>
</kul:tab>