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

<%@ attribute name="customerInvoiceDocumentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>

<logic:iterate id="customerInvoiceWriteoffLookupResult" name="KualiForm"
	property="customerInvoiceWriteoffLookupResults" indexId="ctr">
	
	<c:set var="useTabTop" value="${ctr == 0}" />
	<c:set var="tabTitle" value="${KualiForm.customerInvoiceWriteoffLookupResults[ctr].customerNumber}, ${KualiForm.customerInvoiceWriteoffLookupResults[ctr].customerName}" />
	<ar:customerInvoiceWriteoffSummaryResult
		propertyName="customerInvoiceWriteoffLookupResults[${ctr}]"
		customerInvoiceDocumentAttributes="${customerInvoiceDocumentAttributes}"
		tabTitle="${tabTitle}"
		useTabTop="${useTabTop}"
		/>
</logic:iterate>

