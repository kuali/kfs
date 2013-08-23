<%--
 Copyright 2007-2009 The Kuali Foundation
 
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
