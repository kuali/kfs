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
<%@ attribute name="tabTitle" required="false" type="java.lang.String" description="The title of this tab; defaults to: Travel Advances."%>
<c:set var="tabTitleVar" value="${tabTitle}"/>
<c:if test="${empty tabTitleVar}"><c:set var="tabTitleVar" value="Travel Advances"/></c:if>

<c:set var="travelAdvanceAttributes" value="${DataDictionary.TravelAdvance.attributes}" />
<kul:tab tabTitle="${tabTitleVar}" defaultOpen="true">
	<div id="TravelAdvances" class="tab-container">
		<div align="right">
			<a target="_blank" class="portal_link"
				href="portal.do?channelTitle=Customer Report&channelUrl=kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.Customer&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true&customerNumber=${KualiForm.document.traveler.customer.customerNumber}&customerTypeCode=${KualiForm.document.traveler.customer.customerTypeCode}"
				title="Customer Report">Customer Report</a>
		</div>
		<h3><c:out value="${tabTitleVar}"/></h3>
		<table style="border: none;">
			<c:forEach items="${KualiForm.document.travelAdvances}" var="travelAdvance">
				<tr>
					<th><kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.travelAdvanceRequested}" /> </th>
					<td class="datacell">${travelAdvance.travelAdvanceRequested}</td>
					<th><kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.dueDate}" /> </th>
					<td class="datacell">${travelAdvance.dueDate==null ? '&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp&nbsp' : travelAdvance.dueDate}</td>
					<th><kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.amountDue}" /> </th>
					<td class="datacell">${travelAdvance.amountDue}</td>
					<th><kul:htmlAttributeLabel attributeEntry="${travelAdvanceAttributes.arInvoiceDocNumber}" /> </th>
					<td class="datacell">${travelAdvance.arInvoiceDocNumber}</td>
				</tr>
			</c:forEach>
		</table>
	</div>
</kul:tab>
