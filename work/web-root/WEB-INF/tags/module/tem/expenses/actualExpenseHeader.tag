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
<%@ taglib uri="/WEB-INF/tlds/temfunc.tld" prefix="temfunc"%>
<%@ attribute name="lineNumber" required="false"
	description="Line number for the record."%>
<%@ attribute name="detailObject" required="true"
	description="The actual object"
	type="org.kuali.kfs.module.tem.businessobject.ActualExpense"%>

<c:set var="otherExpenseAttributes"	value="${DataDictionary.ActualExpense.attributes}" />
<c:set var="temExtension" value="${DataDictionary.ExpenseTypeObjectCode.attributes}" />


<tr>
	<c:choose>
		<c:when test="${lineNumber == null }">
			<th>&nbsp;</th>
		</c:when>
		<c:otherwise>
			<th scope="row" class="infoline" rowspan="4">
				<div align="center">${lineNumber}</div></th>
		</c:otherwise>
	</c:choose>
	<th>
		<div align="left">
			<kul:htmlAttributeLabel
				attributeEntry="${otherExpenseAttributes.expenseDate}"
				noColon="true" />
		</div>
	</th>
	<th>
		<div align="left">
			<kul:htmlAttributeLabel
				attributeEntry="${otherExpenseAttributes.expenseTypeCode}"
				noColon="true" />
		</div>
	</th>
	<th>
		<div align="left">
			<kul:htmlAttributeLabel
				attributeEntry="${otherExpenseAttributes.travelCompanyCodeName}"
				noColon="true" />
		</div>
	</th>
	<th>
		<div align="left">
			<kul:htmlAttributeLabel
				attributeEntry="${otherExpenseAttributes.expenseAmount}"
				noColon="true" />
		</div>
	</th>
	<th>
		<div align="left">
			<kul:htmlAttributeLabel
				attributeEntry="${otherExpenseAttributes.currencyRate}"
				noColon="true" />
		</div>
	</th>
	<th>
		<div align="left">
			<kul:htmlAttributeLabel
				attributeEntry="${otherExpenseAttributes.nonReimbursable}"
				noColon="true" />
		</div>
	</th>
	<th>
		<div align="left">
			<kul:htmlAttributeLabel
				attributeEntry="${otherExpenseAttributes.taxable}" noColon="true" />
		</div>
	</th>
	<th>
		<div align="left">
			<kul:htmlAttributeLabel
				attributeEntry="${temExtension.receiptRequired}" noColon="true" />
		</div>
	</th>
	<th>
		<div align="left">
			<kul:htmlAttributeLabel
					attributeEntry="${otherExpenseAttributes.missingReceipt}"
				noColon="true" />
		</div>
	</th>
	<th>
		<div align="left">$US</div>
	</th>
	<th>
		<div align="center">Actions</div>
	</th>
</tr>
