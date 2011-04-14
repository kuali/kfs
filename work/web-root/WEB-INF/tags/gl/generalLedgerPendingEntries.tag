<%--
 Copyright 2007 The Kuali Foundation
 
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

<!--  BEGIN RSMART CUSTOMIZATION -->
<%@ taglib uri="/WEB-INF/tlds/sec.tld" prefix="sec" %>
<!--  END RSMART CUSTOMIZATION -->
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
<c:set var="isMaintenance" value="${KualiForm.class.name eq 'org.kuali.rice.kns.web.struts.form.KualiMaintenanceForm' || maintenanceViewMode eq Constants.PARAM_MAINTENANCE_VIEW_MODE_MAINTENANCE}" />

<%-- if we are maintenance, then we need to rename the generalLedgerPendingEntryProperty, where document.newMaintainableObject actually gives us the business object --%>
<c:set var="realGeneralLedgerPendingEntryProperty" value="${generalLedgerPendingEntryProperty}" />
<c:if test="${isMaintenance}">
  <c:set var="generalLedgerPendingEntryProperty" value="${kfsfunc:renamePropertyForMaintenanceFramework(generalLedgerPendingEntryProperty)}" />
</c:if>

<kul:tab tabTitle="General Ledger Pending Entries" defaultOpen="false" tabErrorKey="${KFSConstants.GENERAL_LEDGER_PENDING_ENTRIES_TAB_ERRORS}">
<div class="tab-container" align=center>
		<h3>General Ledger Pending Entries <kul:lookup boClassName="org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry" lookupParameters="document.documentNumber:documentNumber" hideReturnLink="true" suppressActions="true"/></h3>
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
		  <!--  BEGIN RSMART CUSTOMIZATION -->
          <c:if test="${sec:canViewGLPE(KualiForm.document, generalLedgerPendingEntry)}">
          <!--  END RSMART CUSTOMIZATION -->
			<tr>
				<th class="datacell center"><html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].transactionLedgerEntrySequenceNumber" write="true" value="${generalLedgerPendingEntry.transactionLedgerEntrySequenceNumber}" /></th>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.kfs.sys.businessobject.SystemOptions" keyValues="universityFiscalYear=${generalLedgerPendingEntry.universityFiscalYear}" render="true">
            <html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].universityFiscalYear" value="${generalLedgerPendingEntry.universityFiscalYear}" write="true" />
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Chart" keyValues="chartOfAccountsCode=${generalLedgerPendingEntry.chartOfAccountsCode}" render="true">
						<html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].chartOfAccountsCode" write="true" value="${generalLedgerPendingEntry.chartOfAccountsCode}" />
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Account" keyValues="chartOfAccountsCode=${generalLedgerPendingEntry.chartOfAccountsCode}&accountNumber=${generalLedgerPendingEntry.accountNumber}" render="true">
						<html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].accountNumber" write="true" value="${generalLedgerPendingEntry.accountNumber}" />
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.SubAccount" keyValues="chartOfAccountsCode=${generalLedgerPendingEntry.chartOfAccountsCode}&accountNumber=${generalLedgerPendingEntry.accountNumber}&subAccountNumber=${generalLedgerPendingEntry.subAccountNumber}" render="${ ! generalLedgerPendingEntry.subAccountNumberBlank}">
						<html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].subAccountNumber" write="true" value="${generalLedgerPendingEntry.subAccountNumber}" />
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.ObjectCode" keyValues="financialObjectCode=${generalLedgerPendingEntry.financialObjectCode}&chartOfAccountsCode=${generalLedgerPendingEntry.chartOfAccountsCode}&universityFiscalYear=${generalLedgerPendingEntry.universityFiscalYear}" render="${ ! generalLedgerPendingEntry.financialObjectCodeBlank}">
						<html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].financialObjectCode" write="true" value="${generalLedgerPendingEntry.financialObjectCode}" />
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.SubObjCd" keyValues="financialSubObjectCode=${generalLedgerPendingEntry.financialSubObjectCode}&financialObjectCode=${generalLedgerPendingEntry.financialObjectCode}&chartOfAccountsCode=${generalLedgerPendingEntry.chartOfAccountsCode}&universityFiscalYear=${generalLedgerPendingEntry.universityFiscalYear}" render="${ ! generalLedgerPendingEntry.financialSubObjectCodeBlank}">
						<html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].financialSubObjectCode" write="true" value="${generalLedgerPendingEntry.financialSubObjectCode}" />
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.ProjectCode" keyValues="code=${generalLedgerPendingEntry.projectCode}" render="${ ! generalLedgerPendingEntry.projectCodeBlank}">
						<html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].projectCode" write="true" value="${generalLedgerPendingEntry.projectCode}" />
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.rice.kew.doctype.bo.DocumentTypeEBO" keyValues="documentTypeId=${generalLedgerPendingEntry.financialSystemDocumentTypeCode.documentTypeId}" render="true">
						<html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].financialDocumentTypeCode" write="true" value="${generalLedgerPendingEntry.financialDocumentTypeCode}" />
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.BalanceType" keyValues="code=${generalLedgerPendingEntry.financialBalanceTypeCode}" render="true">
						<html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].financialBalanceTypeCode" write="true" value="${generalLedgerPendingEntry.financialBalanceTypeCode}" />
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.ObjectType" keyValues="code=${generalLedgerPendingEntry.financialObjectTypeCode}" render="${ ! generalLedgerPendingEntry.financialObjectTypeCodeBlank}">
						<html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].financialObjectTypeCode" write="true" value="${generalLedgerPendingEntry.financialObjectTypeCode}" />
					</kul:inquiry>
				</td>
				<td class="datacell center">
					<html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].transactionLedgerEntryAmount" value="${generalLedgerPendingEntry.transactionLedgerEntryAmount}" />
					<bean:write name="KualiForm" property="${realGeneralLedgerPendingEntryProperty}[${ctr}].currencyFormattedTransactionLedgerEntryAmount" />
				</td>
				<td class="datacell center"><html:hidden property="${generalLedgerPendingEntryProperty}[${ctr}].transactionDebitCreditCode" write="true" value="${generalLedgerPendingEntry.transactionDebitCreditCode}" />&nbsp;</td>
			</tr>
		  <!--  BEGIN RSMART CUSTOMIZATION -->
          </c:if>
          <!--  END RSMART CUSTOMIZATION -->	
		</logic:iterate>
	</c:if>
	</table>
</div>
</kul:tab>
