<%--
 Copyright 2006-2009 The Kuali Foundation
 
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

<kul:tab tabTitle="Labor Ledger Pending Entries" defaultOpen="false" tabErrorKey="${KFSConstants.LABOR_LEDGER_PENDING_ENTRIES_TAB_ERRORS}">
<div class="tab-container" align=center>
		<h3>Labor Ledger Pending Entries <kul:lookup boClassName="org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry" lookupParameters="document.documentNumber:documentNumber" hideReturnLink="true" suppressActions="true"/></h3>
	 <table cellpadding="0" cellspacing="0" class="datatable" summary="view/edit pending entries">

	<c:if test="${empty KualiForm.document.laborLedgerPendingEntries}">
		<tr>
			<td class="datacell" height="50"colspan="12"><div align="center">There are currently no Labor Ledger Pending Entries associated with this Transaction Processing document.</div></td>
		</tr>
	</c:if>
	<c:if test="${!empty KualiForm.document.laborLedgerPendingEntries}">
        <c:set var="entryAttributes" value="${DataDictionary.LaborLedgerPendingEntry.attributes}" />
		<tr>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.transactionLedgerEntrySequenceNumber}" hideRequiredAsterisk="true" scope="col"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.universityFiscalYear}" hideRequiredAsterisk="true" scope="col"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.chartOfAccountsCode}" hideRequiredAsterisk="true" scope="col"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.accountNumber}" hideRequiredAsterisk="true" scope="col"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.subAccountNumber}" hideRequiredAsterisk="true" scope="col"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.financialObjectCode}" hideRequiredAsterisk="true" scope="col"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.financialSubObjectCode}" hideRequiredAsterisk="true" scope="col"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.projectCode}" hideRequiredAsterisk="true" scope="col"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.universityFiscalPeriodCode}" hideRequiredAsterisk="true" scope="col"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.financialBalanceTypeCode}" hideRequiredAsterisk="true" scope="col"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.financialObjectTypeCode}" hideRequiredAsterisk="true" scope="col"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.transactionLedgerEntryAmount}" hideRequiredAsterisk="true" scope="col"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.transactionDebitCreditCode}" hideRequiredAsterisk="true" scope="col"/>
		</tr>
		<logic:iterate id="laborLedgerPendingEntry" name="KualiForm" property="document.laborLedgerPendingEntries" indexId="ctr">
			<tr>
				<th class="datacell center"><html:hidden property="document.laborLedgerPendingEntry[${ctr}].transactionLedgerEntrySequenceNumber" write="true"/></th>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.kfs.sys.businessobject.SystemOptions" keyValues="universityFiscalYear=${laborLedgerPendingEntry.universityFiscalYear}" render="true">
						<html:hidden property="document.laborLedgerPendingEntry[${ctr}].universityFiscalYear" write="true"/>
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Chart" keyValues="chartOfAccountsCode=${laborLedgerPendingEntry.chartOfAccountsCode}" render="true">
						<html:hidden property="document.laborLedgerPendingEntry[${ctr}].chartOfAccountsCode" write="true"/>
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Account" keyValues="chartOfAccountsCode=${laborLedgerPendingEntry.chartOfAccountsCode}&accountNumber=${laborLedgerPendingEntry.accountNumber}" render="true">
						<html:hidden property="document.laborLedgerPendingEntry[${ctr}].accountNumber" write="true"/>
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.SubAccount" keyValues="chartOfAccountsCode=${laborLedgerPendingEntry.chartOfAccountsCode}&accountNumber=${laborLedgerPendingEntry.accountNumber}&subAccountNumber=${laborLedgerPendingEntry.subAccountNumber}" render="${ ! laborLedgerPendingEntry.subAccountNumberBlank}">
						<html:hidden property="document.laborLedgerPendingEntry[${ctr}].subAccountNumber" write="true"/>
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.ObjectCode" keyValues="financialObjectCode=${laborLedgerPendingEntry.financialObjectCode}&chartOfAccountsCode=${laborLedgerPendingEntry.chartOfAccountsCode}&universityFiscalYear=${laborLedgerPendingEntry.universityFiscalYear}" render="${ ! laborLedgerPendingEntry.financialObjectCodeBlank}">
						<html:hidden property="document.laborLedgerPendingEntry[${ctr}].financialObjectCode" write="true"/>
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.SubObjCd" keyValues="financialSubObjectCode=${laborLedgerPendingEntry.financialSubObjectCode}&financialObjectCode=${laborLedgerPendingEntry.financialObjectCode}&chartOfAccountsCode=${laborLedgerPendingEntry.chartOfAccountsCode}&universityFiscalYear=${laborLedgerPendingEntry.universityFiscalYear}" render="${ ! laborLedgerPendingEntry.financialSubObjectCodeBlank}">
						<html:hidden property="document.laborLedgerPendingEntry[${ctr}].financialSubObjectCode" write="true"/>
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.ProjectCode" keyValues="code=${laborLedgerPendingEntry.projectCode}" render="${ ! laborLedgerPendingEntry.projectCodeBlank}">
						<html:hidden property="document.laborLedgerPendingEntry[${ctr}].projectCode" write="true"/>
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.AccountingPeriod" keyValues="universityFiscalYear=${laborLedgerPendingEntry.universityFiscalYear}&universityFiscalPeriodCode=${laborLedgerPendingEntry.universityFiscalPeriodCode}" render="true">
						<html:hidden property="document.laborLedgerPendingEntry[${ctr}].universityFiscalPeriodCode" write="true"/>
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.BalanceType" keyValues="code=${laborLedgerPendingEntry.financialBalanceTypeCode}" render="true">
						<html:hidden property="document.laborLedgerPendingEntry[${ctr}].financialBalanceTypeCode" write="true"/>
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.ObjectType" keyValues="code=${laborLedgerPendingEntry.financialObjectTypeCode}" render="${ ! laborLedgerPendingEntry.financialObjectTypeCodeBlank}">
						<html:hidden property="document.laborLedgerPendingEntry[${ctr}].financialObjectTypeCode" write="true"/>
					</kul:inquiry>
				</td>
				<td class="datacell center"><html:hidden property="document.laborLedgerPendingEntry[${ctr}].transactionLedgerEntryAmount" write="true"/></td>
				<td class="datacell center"><html:hidden property="document.laborLedgerPendingEntry[${ctr}].transactionDebitCreditCode" write="true"/>&nbsp;</td>
			</tr>
		</logic:iterate>
	</c:if>
	</table>
</div>
</kul:tab>
