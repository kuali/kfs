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

<kul:tab tabTitle="${TemConstants.TabTitles.EMERGENCY_CONTACT_INFORMATION_TAB_TITLE}" defaultOpen="${KualiForm.document.emergencyContactDefaultOpen}" tabErrorKey="${TemKeyConstants.TRVL_AUTH_EMERGENCY_CONTACT_ERRORS}">
	<div class="tab-container" align="center">
		<c:set var="documentAttributes" value="${DataDictionary.TravelAuthorizationDocument.attributes}" />
		<c:set var="travelerAttributes" value="${DataDictionary.TravelerDetail.attributes}" /> 
		<c:set var="emergencyContactAttributes" value="${DataDictionary.TravelerDetailEmergencyContact.attributes}" /> 
		<c:set var="contactRelationTypeAttributes" value="${DataDictionary.ContactRelationType.attributes}" />

	<h3>Emergency Contact Information</h3>
	<table cellpadding="0" cellspacing="0" class="datatable" summary="Emergency Contact Section">
		<tr>
			<th class="bord-l-b">
				<div align="right">
					<kul:htmlAttributeLabel attributeEntry="${documentAttributes.cellPhoneNumber}" />
				</div>
			</th>
			<td class="datacell">
				<tem:htmlMaskControlAttribute attributeEntry="${documentAttributes.cellPhoneNumber}" property="document.cellPhoneNumber" readOnly="${!fullEntryMode}" />
 			</td>
		</tr>
		<tr>
			<th class="bord-l-b">
				<div align="right">
					<kul:htmlAttributeLabel attributeEntry="${travelerAttributes.citizenship}" useShortLabel="true" />
				</div>
			</th>
			<td class="datacell">
				<tem:htmlMaskControlAttribute attributeEntry="${travelerAttributes.citizenship}" property="document.traveler.citizenship" readOnly="true" />
			</td>
		</tr>
	</table>

	<table cellpadding="0" class="datatable" summary="Emergency Contacts">
		<tbody>
			<tr>
				<td colspan="7" class="tab-subhead">Emergency Contact(s) for
				Travelers:<br /></td>
			</tr>
			<tr>
				<th width="10">&nbsp;</th>
				<th>
				<div align=left><kul:htmlAttributeLabel
					attributeEntry="${emergencyContactAttributes.contactRelationTypeCode}" useShortLabel="true"/></div>
				</th>
				<th>
				<div align=left><kul:htmlAttributeLabel
					attributeEntry="${emergencyContactAttributes.contactName}" /></div>
				</th>
				<th>
				<div align=left><kul:htmlAttributeLabel
					attributeEntry="${emergencyContactAttributes.phoneNumber}" /></div>
				</th>
				<th>
				<div align=left><kul:htmlAttributeLabel
					attributeEntry="${emergencyContactAttributes.emailAddress}" /></div>
				</th>
				<th>
				<div align=left><kul:htmlAttributeLabel
					attributeEntry="${emergencyContactAttributes.primary}" /></div>
				</th>
				<c:if test="${fullEntryMode}">				
				<th>
				<div align=center>Actions</div>
				</th>
				</c:if>
			</tr>
			<c:if test="${fullEntryMode}">
				<tr>
					<th scope="row">
					<div align="right">add:</div>
					
					</th>
					<td>
					<div align="center"><tem:htmlMaskControlAttribute
	                    attributeEntry="${emergencyContactAttributes.contactRelationTypeCode}"
	                    property="newEmergencyContactLine.contactRelationTypeCode"
	                    readOnly="false" /></div>
	                </td>
					<td valign=top class="infoline"><tem:htmlMaskControlAttribute
						attributeEntry="${emergencyContactAttributes.contactName}"
						property="newEmergencyContactLine.contactName" readOnly="false" /></td>
					<td valign=top class="infoline"><tem:htmlMaskControlAttribute
						attributeEntry="${emergencyContactAttributes.phoneNumber}"
						property="newEmergencyContactLine.phoneNumber" readOnly="false" /></td>
					<td valign=top class="infoline"><tem:htmlMaskControlAttribute
						attributeEntry="${emergencyContactAttributes.emailAddress}"
						property="newEmergencyContactLine.emailAddress" readOnly="false" /></td>
					<td valign=top class="infoline"><tem:htmlMaskControlAttribute
						attributeEntry="${emergencyContactAttributes.primary}"
						property="newEmergencyContactLine.primary" readOnly="false" /></td>											
					<td class="infoline">
					<div align=center><html:image
						src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif"
						styleClass="tinybutton"
						property="methodToCall.addEmergencyContactLine"
						alt="Add Emergency Contact Line" title="Add Emergency Contact Line" />
					</div>
					</td>
				</tr>
			</c:if>
			
			<c:if test="${KualiForm.document.traveler.emergencyContacts != null && !empty KualiForm.document.traveler.emergencyContacts}">
			<logic:iterate indexId="ctr" name="KualiForm"
				property="document.traveler.emergencyContacts" id="currentLine">
				<tr>
					<th scope="row">
					<div align="right"><tem:htmlMaskControlAttribute
						attributeEntry="${emergencyContactAttributes.financialDocumentLineNumber}"
						property="document.traveler.emergencyContacts[${ctr}].financialDocumentLineNumber"
						readOnly="true" /></div>
					</th>
					<td valign=top nowrap>
					<div align="center"><tem:htmlMaskControlAttribute
						attributeEntry="${emergencyContactAttributes.contactRelationTypeCode}"
						property="document.traveler.emergencyContacts[${ctr}].contactRelationTypeCode"
						readOnly="${!fullEntryMode}" /></div>
					</td>
					<td valign=top><tem:htmlMaskControlAttribute
						attributeEntry="${emergencyContactAttributes.contactName}"
						property="document.traveler.emergencyContacts[${ctr}].contactName"
						readOnly="${!fullEntryMode}" /></td>
					<td valign=top><tem:htmlMaskControlAttribute
						attributeEntry="${emergencyContactAttributes.phoneNumber}"
						property="document.traveler.emergencyContacts[${ctr}].phoneNumber"
						readOnly="${!fullEntryMode}" /></td>
					<td valign=top><tem:htmlMaskControlAttribute
						attributeEntry="${emergencyContactAttributes.emailAddress}"
						property="document.traveler.emergencyContacts[${ctr}].emailAddress"
						readOnly="${!fullEntryMode}" /></td>
					<td valign=top><tem:htmlMaskControlAttribute
						attributeEntry="${emergencyContactAttributes.primary}"
						property="document.traveler.emergencyContacts[${ctr}].primary"
						readOnly="${!fullEntryMode}" /></td>						
					<c:if test="${fullEntryMode}">
						<td>
							<div align=center><html:image
								src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif"
								styleClass="tinybutton"
								property="methodToCall.deleteEmergencyContactLine.line${ctr}"
								alt="Delete Emergency Contact Line"
								title="Delete Emergency Contact Line" /></div>
						</td>
					</c:if>
				</tr>
			</logic:iterate>
			</c:if>
		</tbody>
	</table>
	<table cellpadding="0" class="datatable" summary="Modes of Transportation">
		<tbody>
			<tr>
				<td colspan="5" class="tab-subhead">Modes of Transportation
				while out-of-country:</td>
			</tr>
			<logic:iterate indexId="ctr2" name="KualiForm"
				property="modesOfTransportation" id="currentMode">
				<c:if test="${ctr2 == 0 || ctr2%5 == 0}">
					<tr>
				</c:if>
				<td><tem-html:multibox property="selectedTransportationModes" disabled="${!fullEntryMode}">
					<bean:write name="currentMode" property="key" />
				</tem-html:multibox> <bean:write name="currentMode" property="value" /></td>

				<c:if test="${ctr2 == 4 || ctr2 == 9}">
					</tr>
				</c:if>

			</logic:iterate>
		</tbody>
	</table>
	<table>
		<tr>
			<th class="bord-l-b">
			<div align="right"><kul:htmlAttributeLabel
				attributeEntry="${documentAttributes.regionFamiliarity}" /></div>
			</th>
			<td class="datacell"><kul:htmlControlAttribute
				attributeEntry="${documentAttributes.regionFamiliarity}"
				property="document.regionFamiliarity"
				readOnly="${!fullEntryMode}" /></td>
		</tr>
	</table>

	</div>
</kul:tab>
