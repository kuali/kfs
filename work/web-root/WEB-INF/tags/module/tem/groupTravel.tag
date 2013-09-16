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

<script>
	$(document).ready(function() {
		update_travelerTypeCode();
	});
</script>

<c:set var="groupTravelerAttributes" value="${DataDictionary.GroupTraveler.attributes}" />
<c:set var="customerAttributes" value="${DataDictionary.Customer.attributes}" />

<kul:tab tabTitle="Group Travel" defaultOpen="false" tabErrorKey="${TemKeyConstants.TRVL_GROUP_TRVL_ERRORS}">
	<div class="tab-container" align=center>
		<h3>Group Travel Section <a href="${KualiForm.uploadParserInstructionsUrl}" target="helpWindow"><img src="${ConfigProperties.kr.externalizable.images.url}my_cp_inf.gif" title="Group Traveler Import Help" src="Group Traveler Import Help" hspace="5" border="0" align="middle" /></a></h3>
		<table cellpadding="0" cellspacing="0" class="datatable" summary="Group Traveler Section">
			<c:if test="${fullEntryMode}">
				<tr>
					<td colspan="7" class="tab-subhead" align="right" nowrap="nowrap">
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
	                      		'<img src="${ConfigProperties.externalizable.images.url}tinybutton-importlines.gif" title="import items from file" alt="import items from file"' +
	                      		'     width=72 height=15 border=0 align="right" class="det-button">' +
	                    		'<\/a>' +
	                    		'<div id="uploadDiv" style="display:none; float:right;" >' +
	                      		'<html:file size="30" property="groupTravelerImportFile" />' +
	                      		'<html:image property="methodToCall.uploadGroupTravelerImportFile" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif"
	                                    styleClass="tinybutton" alt="add imported items" title="add imported items" />' +
	                      		'<html:image property="methodToCall.cancel" src="${ConfigProperties.externalizable.images.url}tinybutton-cancelimport.gif"
	                                    styleClass="tinybutton" alt="cancel import" title="cancel import" onclick="hideImport();return false;" />' +
	                    		'<\/div>');
	                		//-->
	            		</SCRIPT>
						<NOSCRIPT>
							Import lines
							<html:file size="30" property="groupTravelerImportFile" style="font:10px;height:16px;" />
							<html:image property="methodToCall.uploadGroupTravelerImportFile" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="add imported group traveler" title="add imported group traveler" />
						</NOSCRIPT>
					</td>
				</tr>			
				<tr>
					<th class="bord-l-b">
						<div align="right"><kul:htmlAttributeLabel attributeEntry="${groupTravelerAttributes.travelerTypeCode}" /></div>
					</th>
					<td class="datacell">
						<kul:htmlControlAttribute
						attributeEntry="${groupTravelerAttributes.travelerTypeCode}"
						property="newGroupTravelerLine.travelerTypeCode" 
						onchange="update_travelerTypeCode();" />
					</td>
					<th class="bord-l-b">
						<div align="right" style="display:block;" id="personLabel"><kul:htmlAttributeLabel attributeEntry="${groupTravelerAttributes.groupTravelerEmpId}" /></div>
						<div align="right" style="display:none;" id="customerLabel"><kul:htmlAttributeLabel attributeEntry="${customerAttributes.customerNumber}" /></div>
					</th>
					<td class="datacell">
						<kul:htmlControlAttribute attributeEntry="${groupTravelerAttributes.groupTravelerEmpId}" property="newGroupTravelerLine.groupTravelerEmpId" readOnly="true" />
						<div style="display:inline;" id="personLookupButton">
							<kul:lookup boClassName="org.kuali.kfs.module.tem.businessobject.GroupTravelerForLookup"
										fieldConversions="groupTravelerId:newGroupTravelerLine.groupTravelerEmpId,name:newGroupTravelerLine.name,travelerTypeCode:newGroupTravelerLine.travelerTypeCode"
										lookupParameters="newGroupTravelerLine.groupTravelerEmpId:principalId" />
						</div>
					</td>
					<th class="bord-l-b">
						<div align="right"><kul:htmlAttributeLabel attributeEntry="${groupTravelerAttributes.name}" /></div>
					</th>
					<td class="datacell">
						<kul:htmlControlAttribute attributeEntry="${groupTravelerAttributes.name}" property="newGroupTravelerLine.name" />
					</td>
					<td class="infoline">
						<div align=center>
							<html:image src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif"
							styleClass="tinybutton" property="methodToCall.addGroupTravelerLine"
							alt="Add Group Traveler Line" title="Add Group Traveler Line" />
						</div>
					</td>
				</tr>
			</c:if>
			<c:if test="${fn:length(KualiForm.document.groupTravelers) > 0}">
				<tr>
					<td colspan="7">
					<table cellpadding="0" cellspacing="0" class="datatable">
						<logic:iterate indexId="ctr" name="KualiForm" property="document.groupTravelers" id="currentLine">
							<tr>
								<kul:htmlAttributeHeaderCell literalLabel="${ctr+1}" scope="row" align="right"></kul:htmlAttributeHeaderCell>
								<th class="bord-l-b">
									<div align="right"><kul:htmlAttributeLabel attributeEntry="${groupTravelerAttributes.travelerTypeCode}" /></div>
								</th>
								<td valign=top>
									<kul:htmlControlAttribute
									attributeEntry="${groupTravelerAttributes.travelerTypeCode}"
									property="document.groupTravelers[${ctr}].travelerTypeCode"
									readOnly="true" />
								</td>
								<th class="bord-l-b">
									<div align="right"><bean:write name="KualiForm" property="document.groupTravelers[${ctr}].travelerLabel" />:</div>
								</th>
								<td valign=top>
									<kul:htmlControlAttribute
									attributeEntry="${groupTravelerAttributes.groupTravelerEmpId}"
									property="document.groupTravelers[${ctr}].groupTravelerEmpId"
									readOnly="true" />
								</td>
								<th class="bord-l-b">
									<div align="right"><kul:htmlAttributeLabel attributeEntry="${groupTravelerAttributes.name}" /></div>
								</th>
								<td valign=top nowrap>
									<div align="left">
										<kul:htmlControlAttribute
										attributeEntry="${groupTravelerAttributes.name}"
										property="document.groupTravelers[${ctr}].name"
										readOnly="true" />
									</div>
								</td>
								<c:if test="${fullEntryMode}">
									<td>
										<div align=center>
											<html:image src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif"
											styleClass="tinybutton"
											property="methodToCall.deleteGroupTravelerLine.line${ctr}"
											alt="Delete Group Traveler Line"
											title="Delete Group Traveler Line" />
										</div>
									</td>
								</c:if>
							</tr>
						</logic:iterate>
					</table>
					</td>
				</tr>
			</c:if>
		</table>
	</div>
</kul:tab>
