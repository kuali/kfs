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

<c:set var="documentAttributes"
	value="${DataDictionary.TravelReimbursementDocument.attributes}" />

<html:hidden property="startDate" />
<html:hidden property="endDate" />
<kul:tab tabTitle="Trip Overview" defaultOpen="true"
	tabErrorKey="${TemKeyConstants.TRVL_AUTH_TRIP_OVERVIEW_ERRORS}">
	<div class="tab-container" align="center">

		<tem:traveler />
		<tem:tripInformation>
			<%-- if TAC doc exists, don't display checkbox--%>
			<c:if test="${!isClose}">
				<tr>
					<th class="bord-l-b">
						<div align="right">
							<kul:htmlAttributeLabel
								attributeEntry="${documentAttributes.finalReimbursement}" />
						</div></th>
					<td class="datacell" colspan="3"><kul:htmlControlAttribute
							attributeEntry="${documentAttributes.finalReimbursement}"
							property="document.finalReimbursement"
							readOnly="${!fullEntryMode}" />
					</td>
				</tr>
			</c:if>
			
			<th class="bord-l-b" />
			<td class="datacell" />
		</tem:tripInformation>
		<jsp:doBody />
	</div>
</kul:tab>
