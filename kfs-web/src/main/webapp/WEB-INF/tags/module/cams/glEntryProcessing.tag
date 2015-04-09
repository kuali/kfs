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
<%@ attribute name="defaultTabHide" type="java.lang.Boolean" required="false" description="Show tab contents indicator" %>
<%@ attribute name="generalLedgerEntry" required="true" type="java.lang.Object"
	description="General Ledger Entry info object containing the data being displayed"%>
<c:set var="entryAttributes" value="${DataDictionary.GeneralLedgerEntry.attributes}" />

<div class="tab-container" align=center>
	<div align="center" vAlign="middle">
	<h3>GL Entry Processing Line</h3>
	<table width="95%" border="0" cellpadding="0" cellspacing="0" class="datatable">
		<tr>
			<kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.universityFiscalYear}" hideRequiredAsterisk="true" scope="col"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.universityFiscalPeriodCode}" hideRequiredAsterisk="true" scope="col"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.chartOfAccountsCode}" hideRequiredAsterisk="true" scope="col"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.accountNumber}" hideRequiredAsterisk="true" scope="col"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.subAccountNumber}" hideRequiredAsterisk="true" scope="col"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.financialObjectCode}" hideRequiredAsterisk="true" scope="col"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.financialSubObjectCode}" hideRequiredAsterisk="true" scope="col"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.financialDocumentTypeCode}" hideRequiredAsterisk="true" scope="col"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.financialSystemOriginationCode}" hideRequiredAsterisk="true" scope="col"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.documentNumber}" hideRequiredAsterisk="true" scope="col"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.transactionLedgerEntryDescription}" hideRequiredAsterisk="true" scope="col"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.organizationDocumentNumber}" hideRequiredAsterisk="true" scope="col"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.organizationReferenceId}" hideRequiredAsterisk="true" scope="col"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.referenceFinancialSystemOriginationCode}" hideRequiredAsterisk="true" scope="col"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.referenceFinancialDocumentNumber}" hideRequiredAsterisk="true" scope="col"/>
		    <kul:htmlAttributeHeaderCell attributeEntry="${entryAttributes.amount}" hideRequiredAsterisk="true" scope="col"/>
		</tr>
		<tr>
			<td class="grid"><kul:htmlControlAttribute property="generalLedgerEntry.universityFiscalYear" 
				attributeEntry="${entryAttributes.universityFiscalYear}" readOnly="true"/></td>
			<td class="grid"><kul:htmlControlAttribute property="generalLedgerEntry.universityFiscalPeriodCode" 
				attributeEntry="${entryAttributes.universityFiscalPeriodCode}" readOnly="true"/></td>
			<td class="grid">
				<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Chart" keyValues="chartOfAccountsCode=${generalLedgerEntry.chartOfAccountsCode}" render="true">
					<kul:htmlControlAttribute property="generalLedgerEntry.chartOfAccountsCode" 
						attributeEntry="${entryAttributes.chartOfAccountsCode}" readOnly="true"/>
				</kul:inquiry>
			</td>
			<td class="grid">
				<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.Account" keyValues="chartOfAccountsCode=${generalLedgerEntry.chartOfAccountsCode}&accountNumber=${generalLedgerEntry.accountNumber}" render="true">
					<kul:htmlControlAttribute property="generalLedgerEntry.accountNumber" 
						attributeEntry="${entryAttributes.accountNumber}" readOnly="true"/>
				</kul:inquiry>
			</td>
			<td class="grid"><kul:htmlControlAttribute property="generalLedgerEntry.subAccountNumber" 
				attributeEntry="${entryAttributes.subAccountNumber}" readOnly="true"/></td>
			<td class="grid">
				<kul:inquiry boClassName="org.kuali.kfs.coa.businessobject.ObjectCode" keyValues="universityFiscalYear=${generalLedgerEntry.universityFiscalYear}&chartOfAccountsCode=${generalLedgerEntry.chartOfAccountsCode}&financialObjectCode=${generalLedgerEntry.financialObjectCode}" render="true">
					<kul:htmlControlAttribute property="generalLedgerEntry.financialObjectCode" 
					attributeEntry="${entryAttributes.financialObjectCode}" readOnly="true"/>
				</kul:inquiry>
			</td>
			<td class="grid"><kul:htmlControlAttribute property="generalLedgerEntry.financialSubObjectCode" 
				attributeEntry="${entryAttributes.financialSubObjectCode}" readOnly="true"/></td>
			<td class="grid">
				<kul:inquiry boClassName="org.kuali.rice.kew.doctype.bo.DocumentTypeEBO" keyValues="documentTypeId=${generalLedgerEntry.financialSystemDocumentTypeCode.documentTypeId}" render="true">
					<kul:htmlControlAttribute property="generalLedgerEntry.financialDocumentTypeCode" 
					attributeEntry="${entryAttributes.financialDocumentTypeCode}" readOnly="true"/>
				</kul:inquiry>
			</td>
			<td class="grid"><kul:htmlControlAttribute property="generalLedgerEntry.financialSystemOriginationCode" 
				attributeEntry="${entryAttributes.financialSystemOriginationCode}" readOnly="true"/></td>
			<td class="grid">
				<html:link target="_blank" href="cabGlLine.do?methodToCall=viewDoc&documentNumber=${generalLedgerEntry.documentNumber}">
					<kul:htmlControlAttribute property="generalLedgerEntry.documentNumber" attributeEntry="${entryAttributes.documentNumber}" readOnly="true"/>
				</html:link>
			</td>
			<td class="grid"><kul:htmlControlAttribute property="generalLedgerEntry.transactionLedgerEntryDescription" 
				attributeEntry="${entryAttributes.transactionLedgerEntryDescription}" readOnly="true"/></td>
			<td class="grid"><kul:htmlControlAttribute property="generalLedgerEntry.organizationDocumentNumber" 
				attributeEntry="${entryAttributes.organizationDocumentNumber}" readOnly="true"/></td>
			<td class="grid"><kul:htmlControlAttribute property="generalLedgerEntry.organizationReferenceId" 
				attributeEntry="${entryAttributes.organizationReferenceId}" readOnly="true"/>&nbsp;</td>
			<td class="grid"><kul:htmlControlAttribute property="generalLedgerEntry.referenceFinancialSystemOriginationCode" 
				attributeEntry="${entryAttributes.referenceFinancialSystemOriginationCode}" readOnly="true"/>&nbsp;</td>
			<td class="grid"><kul:htmlControlAttribute property="generalLedgerEntry.referenceFinancialDocumentNumber" 
				attributeEntry="${entryAttributes.referenceFinancialDocumentNumber}" readOnly="true"/>&nbsp;</td>
			<td class="grid"><kul:htmlControlAttribute property="generalLedgerEntry.amount" 
				attributeEntry="${entryAttributes.amount}" readOnly="true"/></td>
		</tr>
    </table>
	</div>
</div>
