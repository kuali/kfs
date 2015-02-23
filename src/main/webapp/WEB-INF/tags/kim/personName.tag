<%--
 Copyright 2008-2009 The Kuali Foundation
 
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

<c:set var="docNameAttributes" value="${DataDictionary.PersonDocumentName.attributes}" />

<c:set var="canModify" scope="request" value="${!KualiForm.document.privacy.suppressName || KualiForm.canOverrideEntityPrivacyPreferences}" />
<c:set var="maskData" value="${KualiForm.document.privacy.suppressName && !KualiForm.canOverrideEntityPrivacyPreferences}" />

<kul:subtab lookedUpCollectionName="name" width="${tableWidth}" subTabTitle="Names" noShowHideButton="true">      
    <table cellpadding="0" cellspacing="0" summary="">
        <tr>
            <th>&nbsp;</th> 
            <kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docNameAttributes.nameCode}" noColon="true" />
            <kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docNameAttributes.namePrefix}" noColon="true" />
            <kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docNameAttributes.firstName}" noColon="true" />
            <kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docNameAttributes.middleName}" noColon="true" />
            <kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docNameAttributes.lastName}" noColon="true" />
            <kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docNameAttributes.nameSuffix}" noColon="true" />
            <kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docNameAttributes.dflt}" noColon="true" /> 
            <kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docNameAttributes.active}" noColon="true" /> 
          
            <c:if test="${not inquiry and canModify}">    
                <kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col"/>
            </c:if>
        </tr>     
        <c:if test="${not inquiry and not readOnlyEntity and canModify}">              
            <tr>
                <th class="infoline">
                    <c:out value="Add:" />
                </th>
                <td align="left" valign="middle" class="infoline">
                    <div align="center">
                        <kul:htmlControlAttribute property="newName.nameCode" attributeEntry="${docNameAttributes.nameCode}" readOnly="${readOnlyEntity}" />
                    </div>
                </td>
                <td class="infoline">
                    <div align="center">
                        <kul:htmlControlAttribute property="newName.namePrefix" attributeEntry="${docNameAttributes.namePrefix}" readOnly="${readOnlyEntity}" />
                    </div>
                </td>
                <td class="infoline">   
                    <div align="center">                
                      <kul:htmlControlAttribute property="newName.firstName" attributeEntry="${docNameAttributes.firstName}" readOnly="${readOnlyEntity}" />
                    </div>
                </td>
              <td class="infoline">
                <div align="center">
                  <kul:htmlControlAttribute property="newName.middleName" attributeEntry="${docNameAttributes.middleName}" readOnly="${readOnlyEntity}" />
                </div>
              </td>
              <td align="left" valign="middle" class="infoline">
                    <div align="center"><kul:htmlControlAttribute property="newName.lastName" attributeEntry="${docNameAttributes.lastName}" readOnly="${readOnlyEntity}" />
                    </div>
                </td>
                <td align="left" valign="middle" class="infoline">
                    <div align="center"><kul:htmlControlAttribute property="newName.nameSuffix" attributeEntry="${docNameAttributes.nameSuffix}" readOnly="${readOnlyEntity}" />
                    </div>
                </td>
                <td align="left" valign="middle" class="infoline">
                    <div align="center"><kul:htmlControlAttribute property="newName.dflt" attributeEntry="${docNameAttributes.dflt}" readOnly="${readOnlyEntity}" />
                    </div>
                </td>
                <td align="left" valign="middle" class="infoline">
                    <div align="center"><kul:htmlControlAttribute property="newName.active" attributeEntry="${docNameAttributes.active}" readOnly="${readOnlyEntity}" />
                    </div>
                </td>                               
                <td class="infoline">
                    <div align=center>
                        <html:image property="methodToCall.addName.anchor${tabKey}"
                        src='${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif' styleClass="tinybutton"/>
                    </div>
                </td>
            </tr>         
        </c:if>      
        <c:forEach var="name" items="${KualiForm.document.names}" varStatus="status"> 
            <c:if test="${!(inquiry and readOnlyEntity and kfunc:isHiddenKimObjectType(name.entityNameType.code, 'kim.hide.PersonDocumentName.type'))}">
              <tr>
                <th class="infoline">
                    <c:out value="${status.index+1}" />
                </th>
                <kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.names[${status.index}].nameCode"  attributeEntry="${docNameAttributes.nameCode}"  readOnlyAlternateDisplay="${fn:escapeXml(name.entityNameType.name)}" readOnly="${readOnlyEntity or !canModify}" />
				<kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.names[${status.index}].namePrefix" attributeEntry="${docNameAttributes.namePrefix}" readOnly="${readOnlyEntity or !canModify}"  displayMask="${maskData}" displayMaskValue="Xxxxxxx" />
				<kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.names[${status.index}].firstName" attributeEntry="${docNameAttributes.firstName}" readOnly="${readOnlyEntity or !canModify}"  displayMask="${maskData}" displayMaskValue="Xxxxxxx" />
                <kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.names[${status.index}].middleName" attributeEntry="${docNameAttributes.middleName}" readOnly="${readOnlyEntity or !canModify}"  displayMask="${maskData}" displayMaskValue="Xxxxxxx" />
                <kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.names[${status.index}].lastName" attributeEntry="${docNameAttributes.lastName}" readOnly="${readOnlyEntity or !canModify}" displayMask="${maskData}" displayMaskValue="Xxxxxxx" />
				<kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.names[${status.index}].nameSuffix" attributeEntry="${docNameAttributes.nameSuffix}" readOnly="${readOnlyEntity or !canModify}"  displayMask="${maskData}" displayMaskValue="Xxxxxxx" />
				<kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.names[${status.index}].dflt" attributeEntry="${docNameAttributes.dflt}" readOnly="${readOnlyEntity or !canModify}" />
				<kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.names[${status.index}].active" attributeEntry="${docNameAttributes.active}" readOnly="${readOnlyEntity or !canModify}" />
                <c:if test="${not inquiry and canModify}">                        
                    <td>
                        <div align="center">&nbsp;
                         <c:choose>
                           <c:when test="${name.edit or readOnlyEntity}">
                              <img class='nobord' src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete2.gif' styleClass='tinybutton'/>
                           </c:when>
                           <c:otherwise>
                              <html:image property='methodToCall.deleteName.line${status.index}.anchor${currentTabIndex}'
                                    src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif' styleClass='tinybutton'/>
                           </c:otherwise>
                         </c:choose>  
                        </div>
                    </td>
                </c:if>    
              </tr>
            </c:if>  
        </c:forEach> 
    </table>
</kul:subtab>
