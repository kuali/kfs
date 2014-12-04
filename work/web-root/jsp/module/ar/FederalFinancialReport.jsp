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
