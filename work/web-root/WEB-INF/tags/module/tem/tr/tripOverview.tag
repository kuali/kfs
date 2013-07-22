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
