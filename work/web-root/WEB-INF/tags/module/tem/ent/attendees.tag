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

<%@ attribute name="attendeeAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="entertainmentAttributes" required="true" type="java.util.Map" description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="extraHiddenAttendeeFields" required="false" description="A comma seperated list of names to be added to the list of normally hidden fields for the existing misc items."%>

<script language="JavaScript" type="text/javascript" src="dwr/interface/CommodityCodeService.js"></script>
<script language="JavaScript" type="text/javascript" src="scripts/vnd/objectInfo.js"></script>

<c:set var="hasAttendeeLines" value="${fn:length(KualiForm.document.attendee) > 0}" />
<c:set var="tabindexOverrideBase" value="50" />

<kul:tab tabTitle="Attendees" defaultOpen="true" tabErrorKey="${TemKeyConstants.TRVL_ENT_ATTENDEE_ERRORS}">
	<div class="tab-container" align=center>
		<table cellpadding="0" cellspacing="0" class="datatable" summary="Items Section">
			<tr>
				<td colspan="2" class="subhead">
					<span class="subhead-left">Attendee Information</span>
				</td>
			</tr>
			<tr>
				<th class="bord-l-b" style="width:325px;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${entertainmentAttributes.numberOfAttendees}" />
					</div>
				</th>
				<td class="datacell">
					<kul:htmlControlAttribute attributeEntry="${entertainmentAttributes.numberOfAttendees}" property="document.numberOfAttendees" readOnly="${!fullEntryMode}" />
				</td>			
			</tr>
			<tr>
				<th class="bord-l-b" style="width:325px;">
					<div align="right">
						<kul:htmlAttributeLabel attributeEntry="${entertainmentAttributes.attendeeListAttached}" />
					</div>
				</th>
				<td class="datacell">
					<kul:htmlControlAttribute attributeEntry="${entertainmentAttributes.attendeeListAttached}" property="document.attendeeListAttached" readOnly="${!fullEntryMode}" />
				</td>			
			</tr>
		</table>
		<br />			
		<c:if test="${fullEntryMode}">
			<table cellpadding="0" cellspacing="0" class="datatable" summary="Items Section">
				<tr>
					<td colspan="4" class="subhead">
						<span class="subhead-left">
							Add Attendee 
							<a href="${KualiForm.uploadParserInstructionsUrl}" target="helpWindow">
								<img src="${ConfigProperties.kr.externalizable.images.url}my_cp_inf.gif" title="Attendee Import Help" src="Attendee Import Help" hspace="5" border="0" align="middle" />
							</a>
						</span>
					</td>
					<td class="subhead" align="right" nowrap="nowrap" style="border-left:none;">
						<SCRIPT type="text/javascript">
	               		<!--
	                 		function hideImport() {
	                     		document.getElementById("showLink").style.display="inline";
	                     		document.getElementById("uploadDiv").style.display="none";
	                 		}
	                 		function showImport() {
	                     		document.getElementById("showLink").style.display="none";
	                     		document.getElementById("uploadDiv").style.display="inline";
	                 		}
	                 		document.write(
	                   		'<a id="showLink" href="#" onclick="showImport();return false;">' +
	                     		'<img src="${ConfigProperties.externalizable.images.url}tinybutton-importlines.gif" title="import Attendee from file" alt="import Attendee from file"' +
	                     		'     width=72 height=15 border=0 align="right" class="det-button">' +
	                   		'<\/a>' +
	                   		'<div id="uploadDiv" style="display:none;" >' +
	                     		'<html:file size="30" property="attendeesImportFile" />' +
	                     		'<html:image property="methodToCall.importAttendees" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif"
	                                   styleClass="tinybutton" alt="add imported Attendees" title="add imported Attendees" />' +
	                     		'<html:image property="methodToCall.cancel" src="${ConfigProperties.externalizable.images.url}tinybutton-cancelimport.gif"
	                                   styleClass="tinybutton" alt="cancel import" title="cancel import" onclick="hideImport();return false;" />' +
	                   		'<\/div>');
	               		//-->
	           			</SCRIPT>
						<NOSCRIPT>
							Import Attendees
							<html:file size="30" property="attendeesImportFile" style="font:10px;height:16px;" />
							<html:image property="methodToCall.importAttendees" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="add imported items" title="add imported items" />
						</NOSCRIPT>					
					</td>
				</tr>
				<tr>
					<kul:htmlAttributeHeaderCell attributeEntry="${attendeeAttributes.attendeeType}" forceRequired="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${attendeeAttributes.company}" forceRequired="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${attendeeAttributes.title}" forceRequired="false" />
					<kul:htmlAttributeHeaderCell attributeEntry="${attendeeAttributes.name}" forceRequired="false" />
					<th>&nbsp;</th>
				</tr>
				<tr>
					<td class="infoline">
						<kul:htmlControlAttribute attributeEntry="${attendeeAttributes.attendeeType}" property="newAttendeeLines[0].attendeeType" readOnly="${!fullEntryMode}" />
					</td>
					<td class="infoline">
						<kul:htmlControlAttribute attributeEntry="${attendeeAttributes.company}" property="newAttendeeLines[0].company" readOnly="${!fullEntryMode}" />
					</td>
					<td class="infoline">
						<kul:htmlControlAttribute attributeEntry="${attendeeAttributes.title}" property="newAttendeeLines[0].title" readOnly="${!fullEntryMode}" />
					</td>
					<td class="infoline">
						<kul:htmlControlAttribute attributeEntry="${attendeeAttributes.name}" property="newAttendeeLines[0].name" readOnly="${!fullEntryMode}" />
						<c:if test="${fullEntryMode}">
							<kul:lookup
								boClassName="org.kuali.kfs.module.tem.businessobject.TemProfile"
								fieldConversions="firstName:document.attendeeDetail.firstName,middleName:document.attendeeDetail.middleName,lastName:document.attendeeDetail.lastName" />
						</c:if>
					</td>
					<td class="infoline">
						<div align="center">
							<html:image property="methodToCall.addAttendeeLine.line0"
								src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif"
								alt="Insert an Attendee" title="Add an Item"
								styleClass="tinybutton" tabindex="${tabindexOverrideBase + 0}" />
						</div>
					</td>
				</tr>
			</table>
			<br />
		</c:if>
		<c:if test="${hasAttendeeLines}">
			<table cellpadding="0" cellspacing="0" class="datatable" summary="Items Added Section">			
				<tr>
					<td colspan="5" class="subhead"><span class="subhead-left">Attendees Added </td>
				</tr>
				<tr>
					<kul:htmlAttributeHeaderCell attributeEntry="${attendeeAttributes.attendeeType}" />
					<kul:htmlAttributeHeaderCell attributeEntry="${attendeeAttributes.company}" />
					<kul:htmlAttributeHeaderCell attributeEntry="${attendeeAttributes.title}" />
					<kul:htmlAttributeHeaderCell attributeEntry="${attendeeAttributes.name}" />
					<th>&nbsp;</th>
				</tr>
				<logic:iterate indexId="ctr" name="KualiForm" property="document.attendee" id="attendeeLine">
					<tr>
						<td class="infoline">
							<kul:htmlControlAttribute attributeEntry="${attendeeAttributes.attendeeType}" property="document.attendee[${ctr}].attendeeType" readOnly="true" />
						</td>
						<td class="infoline">
							<kul:htmlControlAttribute attributeEntry="${attendeeAttributes.company}" property="document.attendee[${ctr}].company" readOnly="true" />
						</td>
						<td class="infoline">
							<kul:htmlControlAttribute attributeEntry="${attendeeAttributes.title}" property="document.attendee[${ctr}].title" readOnly="true" />
						</td>
						<td class="infoline">
							<kul:htmlControlAttribute attributeEntry="${attendeeAttributes.name}" property="document.attendee[${ctr}].name" readOnly="true" />
						</td>
						<td class="infoline">
							<c:choose>
								<c:when test="${fullEntryMode}">							
									<div align="center">
										<html:image
											property="methodToCall.deleteAttendeeLine.line${ctr}"
											src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif"
											alt="Delete Attendee ${ctr+1}" title="Delete Attendee ${ctr+1}"
											styleClass="tinybutton" />
									</div>
								</c:when>
								<c:otherwise>&nbsp;</c:otherwise>
							</c:choose>
						</td>						
					</tr>
				</logic:iterate>
			</table>
		</c:if>
	</div>
</kul:tab>
