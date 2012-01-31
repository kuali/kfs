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

<c:set var="award" value="${DataDictionary.Award.attributes}" />
<c:set var="agency" value="${DataDictionary.Agency.attributes}" />
<c:set var="organization"
	value="${DataDictionary.OrganizationAccountingDefault.attributes}" />

<kul:page showDocumentInfo="false"
	headerTitle="Federal Financial Report Generation"
	docTitle="Federal Financial Report Generation" renderMultipart="true"
	transactionalDocument="false" htmlFormAction="arFederalFinancialReport"
	errorKey="foo">

	<table cellpadding="0" cellspacing="0" class="datatable-80"
		summary="Federal Financial Report Generation"">

		<tr>
			<th align=right valign=middle class="grid">
				<div align="right">Federal Form:</div>
			</th>
			<td align=left valign=middle class="grid"><html-el:select
					property="federalForm">
					<html-el:option value=""></html-el:option>
					<html-el:option value="425">SF425</html-el:option>
					<html-el:option value="425A">SF425A</html-el:option>
				</html-el:select>
			</td>
		</tr>

		<tr>
			<th align=right valign=middle class="grid" style="width: 25%;">
				<div align="right">
					<kul:htmlAttributeLabel attributeEntry="${award.proposalNumber}"
						readOnly="true" />
				</div></th>
			<td align=left valign=middle class="grid" style="width: 25%;"><kul:htmlControlAttribute
					attributeEntry="${award.proposalNumber}" property="proposalNumber" />
				<kul:lookup
					boClassName="org.kuali.kfs.module.cg.businessobject.Award" /></td>

		</tr>
		<tr>
			<th align=right valign=middle class="grid" style="width: 25%;">
				<div align="right">
					<kul:htmlAttributeLabel attributeEntry="${agency.agencyNumber}"
						readOnly="true" />
				</div></th>
			<td align=left valign=middle class="grid" style="width: 25%;"><kul:htmlControlAttribute
					attributeEntry="${agency.agencyNumber}" property="agencyNumber" />
				<kul:lookup
					boClassName="org.kuali.kfs.module.cg.businessobject.Agency" /></td>

		</tr>
		<tr>
			<th align=right valign=middle class="grid">
				<div align="right">Print invoices for Calendar Year:</div></th>
			<td align=left valign=middle class="grid"><kul:htmlControlAttribute
					attributeEntry="${organization.universityFiscalYear}"
					property="fiscalYear" /></td>

		</tr>
		<tr>
			<th align=right valign=middle class="grid">
				<div align="right">Reporting Period:</div>
			</th>
			<td align=left valign=middle class="grid"><html-el:select
					property="reportingPeriod">
					<html-el:option value=""></html-el:option>
					<html-el:option value="q1">Quarter 1</html-el:option>
					<html-el:option value="q2">Quarter 2</html-el:option>
					<html-el:option value="q3">Quarter 3</html-el:option>
					<html-el:option value="q4">Quarter 4</html-el:option>
					<html-el:option value="sa">Semi Annually</html-el:option>
					<html-el:option value="F">Final</html-el:option>
				</html-el:select>
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
		<c:if test="${!empty KualiForm.error }">
 			${KualiForm.error }	
      </c:if>
	</div>

</kul:page>
