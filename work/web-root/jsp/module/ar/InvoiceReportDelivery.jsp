<%--
 Copyright 2006-2008 The Kuali Foundation
 
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

<c:set var="orgAttributes"
	value="${DataDictionary.Organization.attributes}" />
<c:set var="award" value="${DataDictionary.Award.attributes}" />
<c:set var="invoice"
	value="${DataDictionary.ContractsGrantsInvoiceReport.attributes}" />
<c:set var="organization"
	value="${DataDictionary.OrganizationAccountingDefault.attributes}" />

<kul:page showDocumentInfo="false" headerTitle="Invoice Report Delivery"
	docTitle="Invoice Report Delivery" renderMultipart="true"
	transactionalDocument="false" htmlFormAction="arInvoiceReportDelivery"
	errorKey="foo">
	<table cellpadding="0" cellspacing="0" class="datatable-80"
		summary="Invoice Report Delivery">

		<tr>
			<th align=right valign=middle class="grid" style="width: 25%;">
				<div align="right">
					<kul:htmlAttributeLabel
						attributeEntry="${orgAttributes.chartOfAccountsCode}"
						readOnly="true" />
				</div>
			</th>
			<td align=left valign=middle class="grid" style="width: 25%;"><kul:htmlControlAttribute
					attributeEntry="${orgAttributes.chartOfAccountsCode}"
					property="chartCode" /> <kul:lookup
					boClassName="org.kuali.kfs.coa.businessobject.Chart"
					fieldConversions="chartOfAccountsCode:chartCode" />
			</td>
		</tr>

		<tr>
			<th align=right valign=middle class="grid">
				<div align="right">
					<kul:htmlAttributeLabel
						attributeEntry="${orgAttributes.organizationCode}" readOnly="true" />
				</div>
			</th>
			<td align=left valign=middle class="grid"><kul:htmlControlAttribute
					attributeEntry="${orgAttributes.organizationCode}"
					property="orgCode" /> <kul:lookup
					boClassName="org.kuali.kfs.coa.businessobject.Organization"
					fieldConversions="organizationCode:orgCode"
					lookupParameters="orgCode:organizationCode,chartCode:chartOfAccountsCode" />
			</td>
		</tr>

		<tr>
			<th align=right valign=middle class="grid">
				<div align="right">Invoice Initiator Principal Name:</div>
			</th>
			<td align=left valign=middle class="grid">
			<kul:htmlControlAttribute attributeEntry="${DataDictionary.PersonImpl.attributes.principalName}" property="userId" readOnly="false" />
			<kul:lookup boClassName="org.kuali.rice.kim.api.identity.Person" fieldConversions="principalName:userId" />
			</td>
		</tr>

		<tr>
			<th align=right valign=middle class="grid">
				<div align="right">Print invoices from date:</div>
			</th>
			<td align=left valign=middle class="grid"><kul:dateInput
					attributeEntry="${invoice.invoiceDate}" property="fromDate" />
			</td>
		</tr>

		<tr>
			<th align=right valign=middle class="grid">
				<div align="right">Print invoices to date:</div>
			</th>
			<td align=left valign=middle class="grid"><kul:dateInput
					attributeEntry="${invoice.invoiceDate}" property="toDate" />
			</td>
		</tr>

		<tr>
			<th align=right valign=middle class="grid">
				<div align="right">Method of Invoice Transmission *:</div>
			</th>
			<td align=left valign=middle class="grid"><html-el:radio
					property="deliveryType" value="MAIL" />Mail <html-el:radio
					property="deliveryType" value="EMAIL" />Email<html-el:radio
					property="deliveryType" value="BOTH" />Both</td>
		</tr>

		<tr>
			<th align=right valign=middle class="grid" style="width: 25%;">
				<div align="right">
					<kul:htmlAttributeLabel attributeEntry="${award.proposalNumber}"
						readOnly="true" />
				</div>
			</th>
			<td align=left valign=middle class="grid" style="width: 25%;"><kul:htmlControlAttribute
					attributeEntry="${award.proposalNumber}" property="proposalNumber" />
				<kul:lookup
					boClassName="org.kuali.kfs.module.cg.businessobject.Award" />
			</td>
		</tr>

		<tr>
			<th align=right valign=middle class="grid">
				<div align="right">
					<kul:htmlAttributeLabel attributeEntry="${invoice.invoiceAmount}"
						readOnly="true" />
				</div>
			</th>
			<td align=left valign=middle class="grid"><kul:htmlControlAttribute
					attributeEntry="${invoice.invoiceAmount}" property="invoiceAmount" />
			</td>
		</tr>

		<tr>
			<th align=right valign=middle class="grid">
				<div align="right">
					<kul:htmlAttributeLabel attributeEntry="${invoice.documentNumber}"
						readOnly="true" />
				</div>
			</th>
			<td align=left valign=middle class="grid"><kul:htmlControlAttribute
					attributeEntry="${invoice.documentNumber}"
					property="documentNumber" />
			</td>
		</tr>
	</table>


	<c:set var="extraButtons" value="${KualiForm.extraButtons}" />


	<div id="globalbuttons" class="globalbuttons">

		<c:if test="${!empty extraButtons}">
			<c:forEach items="${extraButtons}" var="extraButton">
				<html:image src="${extraButton.extraButtonSource}"
					styleClass="globalbuttons"
					property="${extraButton.extraButtonProperty}"
					title="${extraButton.extraButtonAltText}"
					alt="${extraButton.extraButtonAltText}" />
			</c:forEach>
		</c:if>
	</div>

	<div>
		<c:if test="${!empty KualiForm.message }">
 			${KualiForm.message }	
      </c:if>
	</div>

</kul:page>

