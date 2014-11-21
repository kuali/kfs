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

<c:set var="cashDrawerAttributes" value="${DataDictionary.CashDrawer.attributes}" />

<c:set var="drawer" value="${KualiForm.document.cashDrawer}" />

            			<h3>Cash Drawer Contents</h3>
        <html:hidden name="KualiForm" property="document.cashDrawer.statusCode" />
				<table border="0" cellspacing="0" cellpadding="0" class="datatable">
					<tr>
						<td><kul:htmlAttributeLabel attributeEntry="${cashDrawerAttributes.financialDocumentHundredDollarAmount}" /><c:if test="${!empty KualiForm.document.cashDrawer.hundredDollarCount}"> (Count: ${KualiForm.document.cashDrawer.hundredDollarCount})</c:if></td>
						<td>$<html:hidden property="cashManagementDocument.cashDrawer.financialDocumentHundredDollarAmount" write="true" /></td>
						<td><kul:htmlAttributeLabel attributeEntry="${cashDrawerAttributes.financialDocumentHundredCentAmount}" /><c:if test="${!empty KualiForm.document.cashDrawer.hundredCentCount}"> (Count: ${KualiForm.document.cashDrawer.hundredCentCount})</c:if></td>
						<td>$<html:hidden property="cashManagementDocument.cashDrawer.financialDocumentHundredCentAmount" write="true" /></td>
					</tr>
					<tr>
						<td><kul:htmlAttributeLabel attributeEntry="${cashDrawerAttributes.financialDocumentFiftyDollarAmount}" /><c:if test="${!empty KualiForm.document.cashDrawer.fiftyDollarCount}"> (Count: ${KualiForm.document.cashDrawer.fiftyDollarCount})</c:if></td>
						<td>$<html:hidden property="cashManagementDocument.cashDrawer.financialDocumentFiftyDollarAmount" write="true" /></td>
						<td><kul:htmlAttributeLabel attributeEntry="${cashDrawerAttributes.financialDocumentFiftyCentAmount}" /><c:if test="${!empty KualiForm.document.cashDrawer.fiftyCentCount}"> (Count: ${KualiForm.document.cashDrawer.fiftyCentCount})</c:if></td>
						<td>$<html:hidden property="cashManagementDocument.cashDrawer.financialDocumentFiftyCentAmount" write="true" /></td>
					</tr>
					<tr>
						<td><kul:htmlAttributeLabel attributeEntry="${cashDrawerAttributes.financialDocumentTwentyDollarAmount}" /><c:if test="${!empty KualiForm.document.cashDrawer.twentyDollarCount}"> (Count: ${KualiForm.document.cashDrawer.twentyDollarCount})</c:if></td>
						<td>$<html:hidden property="cashManagementDocument.cashDrawer.financialDocumentTwentyDollarAmount" write="true" /></td>
						<td><kul:htmlAttributeLabel attributeEntry="${cashDrawerAttributes.financialDocumentTwentyFiveCentAmount}" /><c:if test="${!empty KualiForm.document.cashDrawer.twentyFiveCentCount}"> (Count: ${KualiForm.document.cashDrawer.twentyFiveCentCount})</c:if></td>
						<td>$<html:hidden property="cashManagementDocument.cashDrawer.financialDocumentTwentyFiveCentAmount" write="true" /></td>
					</tr>
					<tr>
						<td><kul:htmlAttributeLabel attributeEntry="${cashDrawerAttributes.financialDocumentTenDollarAmount}" /><c:if test="${!empty KualiForm.document.cashDrawer.tenDollarCount}"> (Count: ${KualiForm.document.cashDrawer.tenDollarCount})</c:if></td>
						<td>$<html:hidden property="cashManagementDocument.cashDrawer.financialDocumentTenDollarAmount" write="true" /></td>
						<td><kul:htmlAttributeLabel attributeEntry="${cashDrawerAttributes.financialDocumentTenCentAmount}" /><c:if test="${!empty KualiForm.document.cashDrawer.tenCentCount}"> (Count: ${KualiForm.document.cashDrawer.tenCentCount})</c:if></td>
						<td>$<html:hidden property="cashManagementDocument.cashDrawer.financialDocumentTenCentAmount" write="true" /></td>
					</tr>
					<tr>
						<td><kul:htmlAttributeLabel attributeEntry="${cashDrawerAttributes.financialDocumentFiveDollarAmount}" /><c:if test="${!empty KualiForm.document.cashDrawer.fiveDollarCount}"> (Count: ${KualiForm.document.cashDrawer.fiveDollarCount})</c:if></td>
						<td>$<html:hidden property="cashManagementDocument.cashDrawer.financialDocumentFiveDollarAmount" write="true" /></td>
						<td><kul:htmlAttributeLabel attributeEntry="${cashDrawerAttributes.financialDocumentFiveCentAmount}" /><c:if test="${!empty KualiForm.document.cashDrawer.fiveCentCount}"> (Count: ${KualiForm.document.cashDrawer.fiveCentCount})</c:if></td>
						<td>$<html:hidden property="cashManagementDocument.cashDrawer.financialDocumentFiveCentAmount" write="true" /></td>
					</tr>
					<tr>
						<td><kul:htmlAttributeLabel attributeEntry="${cashDrawerAttributes.financialDocumentTwoDollarAmount}" /><c:if test="${!empty KualiForm.document.cashDrawer.twoDollarCount}"> (Count: ${KualiForm.document.cashDrawer.twoDollarCount})</c:if></td>
						<td>$<html:hidden property="cashManagementDocument.cashDrawer.financialDocumentTwoDollarAmount" write="true" /></td>
						<td><kul:htmlAttributeLabel attributeEntry="${cashDrawerAttributes.financialDocumentOneCentAmount}" /><c:if test="${!empty KualiForm.document.cashDrawer.oneCentCount}"> (Count: ${KualiForm.document.cashDrawer.oneCentCount})</c:if></td>
						<td>$<html:hidden property="cashManagementDocument.cashDrawer.financialDocumentOneCentAmount" write="true" /></td>
					</tr>
					<tr>
						<td><kul:htmlAttributeLabel attributeEntry="${cashDrawerAttributes.financialDocumentOneDollarAmount}" /><c:if test="${!empty KualiForm.document.cashDrawer.oneDollarCount}"> (Count: ${KualiForm.document.cashDrawer.oneDollarCount})</c:if></td>
						<td>$<html:hidden property="cashManagementDocument.cashDrawer.financialDocumentOneDollarAmount" write="true" /></td>
						<td><kul:htmlAttributeLabel attributeEntry="${cashDrawerAttributes.financialDocumentOtherCentAmount}" /> </td>
						<td>$<html:hidden property="cashManagementDocument.cashDrawer.financialDocumentOtherCentAmount" write="true" /></td>
					</tr>
					<tr>
						<td><kul:htmlAttributeLabel attributeEntry="${cashDrawerAttributes.financialDocumentOtherDollarAmount}" /></td>
						<td>$<html:hidden property="cashManagementDocument.cashDrawer.financialDocumentOtherDollarAmount" write="true" /></td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
				</table>
