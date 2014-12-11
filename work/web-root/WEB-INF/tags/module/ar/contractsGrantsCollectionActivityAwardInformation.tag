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

<%@ attribute name="readOnly" required="true"
	description="If document is in read only mode"%>

<c:set var="ContractsGrantsCollectionActivityAttributes"
	value="${DataDictionary.ContractsGrantsCollectionActivityDocument.attributes}"/>

<kul:tab tabTitle="Award Information" defaultOpen="true" tabErrorKey="document.proposalNumber">
	<div class="tab-container" align="center">
		<h3>Award Information</h3>
		<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
			<tr>
				<kul:htmlAttributeHeaderCell width="50%"
					attributeEntry="${ContractsGrantsCollectionActivityAttributes.proposalNumber}" useShortLabel="false" horizontal="true" />
				<td>
					<kul:htmlControlAttribute readOnly="true"
						attributeEntry="${ContractsGrantsCollectionActivityAttributes.proposalNumber}"
						property="document.proposalNumber" forceRequired="true" /> 
					<c:if test="${not readOnly}">
						<kul:lookup
							boClassName="org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward"
							fieldConversions="proposalNumber:document.proposalNumber" />
					</c:if>
				</td>
			</tr>

			<tr>
				<kul:htmlAttributeHeaderCell literalLabel="Agency Number"
					horizontal="true" />
				<td>
					<div id="document.agencyNumber.div">
						<kul:htmlControlAttribute readOnly="true"
							attributeEntry="${ContractsGrantsCollectionActivityAttributes.agencyNumber}"
							property="document.agencyNumber" />
					</div>
				</td>
			</tr>
			<tr>
				<kul:htmlAttributeHeaderCell literalLabel="Agency Name"
					horizontal="true" />
				<td>
					<div id="document.agencyName.div">
						<kul:htmlControlAttribute readOnly="true"
							attributeEntry="${ContractsGrantsCollectionActivityAttributes.agencyName}"
							property="document.agencyName" />
					</div>
				</td>
			</tr>

			<tr>
				<kul:htmlAttributeHeaderCell literalLabel="Customer Number"
					horizontal="true" />
				<td>
					<div id="document.customerNumber.div">
						<kul:htmlControlAttribute readOnly="true"
							attributeEntry="${ContractsGrantsCollectionActivityAttributes.customerNumber}"
							property="document.customerNumber" />
					</div>
				</td>
			</tr>

			<tr>
				<kul:htmlAttributeHeaderCell literalLabel="Customer Name"
					horizontal="true" />
				<td>
					<div id="document.customerName.div">
						<kul:htmlControlAttribute readOnly="true"
							attributeEntry="${ContractsGrantsCollectionActivityAttributes.customerName}"
							property="document.customerName" />
					</div>
				</td>
			</tr>
		</table>
	</div>
</kul:tab>