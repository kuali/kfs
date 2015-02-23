<%--
 Copyright 2009 The Kuali Foundation
 
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
<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="docCitizenshipAttributes" value="${DataDictionary.PersonDocumentCitizenship.attributes}" />

<kul:subtab lookedUpCollectionName="citizenship" width="${tableWidth}" subTabTitle="Citizenships">      
        <table cellpadding="0" cellspacing="0" summary="">
          	<tr>
          		<th><div align="left">&nbsp;</div></th> 
          		<kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docCitizenshipAttributes.countryCode}" noColon="true" />
          		<kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docCitizenshipAttributes.startDate}" noColon="true" />
          		<kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docCitizenshipAttributes.endDate}" noColon="true" />
              	<kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col"/>
          	
          	</tr>     
          	
             <tr>
				<th class="infoline">Add:</th>
				<kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="newCitizenship.countryCode" attributeEntry="${docCitizenshipAttributes.countryCode}" readOnly="${readOnly}" />
				<kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="newCitizenship.startDate" attributeEntry="${docCitizenshipAttributes.startDate}" datePicker="true" readOnly="${readOnly}" />
				<kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="newCitizenship.endDate" attributeEntry="${docCitizenshipAttributes.endDate}" datePicker="true" readOnly="${readOnly}" />
                <td class="infoline">
					<div align=center>
						<html:image property="methodToCall.addCitizenship.anchor${tabKey}"
						src='${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif' styleClass="tinybutton"/>
					</div>
                </td>
       </tr>         
            
        	<c:forEach var="citizenship" items="${KualiForm.document.citizenships}" varStatus="status">
	             <tr>
					<th class="infoline">
						<c:out value="${status.index+1}" />
					</th>
					<kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.citizenships[${status.index}].countryCode" attributeEntry="${docCitizenshipAttributes.countryCode}" readOnly="${readOnly}" />
					<kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.citizenships[${status.index}].startDate" attributeEntry="${docCitizenshipAttributes.startDate}" datePicker="true" readOnly="${readOnly}" />
					<kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.citizenships[${status.index}].endDate"  attributeEntry="${docCitizenshipAttributes.endDate}" datePicker="true" readOnly="${readOnly}" />

					<td>
					<div align=center>&nbsp;
						<html:image property="methodToCall.deleteCitizenship.line${status.index}.anchor${currentTabIndex}"
							src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif' styleClass="tinybutton"/>
					</div>
	                </td>
	            </tr>
        	</c:forEach>        

            
        </table>
</kul:subtab>
