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
<style type="text/css">
  select.fixed-size-200-select {
    width:200px;
   }
</style>

<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>
<c:set var="docAddressAttributes" value="${DataDictionary.PersonDocumentAddress.attributes}" />

<c:set var="canModify" scope="request" value="${!KualiForm.document.privacy.suppressAddress || KualiForm.canOverrideEntityPrivacyPreferences}" />
<c:set var="maskData" value="${KualiForm.document.privacy.suppressAddress && !KualiForm.canOverrideEntityPrivacyPreferences}" />

<kul:subtab lookedUpCollectionName="address" width="${tableWidth}" subTabTitle="Addresses" noShowHideButton="true">      
    <table cellpadding="0" cellspacing="0" summary="">
        <tr>
            <th><div align="left">&nbsp;</div></th>
            <kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docAddressAttributes.addressTypeCode}" noColon="true" /> 
            <kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docAddressAttributes.line1}" noColon="true" /> 
            <kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docAddressAttributes.line2}" noColon="true" /> 
            <kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docAddressAttributes.line3}" noColon="true" /> 
            <kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docAddressAttributes.city}" noColon="true" />
            <kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docAddressAttributes.stateProvinceCode}" noColon="true" />
            <kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docAddressAttributes.postalCode}" noColon="true" /> 
            <kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docAddressAttributes.countryCode}" noColon="true" /> 
            <kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docAddressAttributes.dflt}" noColon="true" /> 
            <kim:cell inquiry="${inquiry}" isLabel="true" textAlign="center" attributeEntry="${docAddressAttributes.active}" noColon="true" /> 
            <c:if test="${not inquiry and canModify}">  
                <kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col"/>
            </c:if>
        </tr>
        <c:if test="${not inquiry and not readOnlyEntity and canModify}">
             <tr>
                <th class="infoline">
                    <c:out value="Add:" />
                </th>
                <td>
                    <div align="center">
                        <kul:htmlControlAttribute property="newAddress.addressTypeCode" attributeEntry="${docAddressAttributes.addressTypeCode}" readOnly="${readOnlyEntity}" />
                    </div>
                </td>
                <td>
                    <div align="center">
                        <kul:htmlControlAttribute property="newAddress.line1" attributeEntry="${docAddressAttributes.line1}" readOnly="${readOnlyEntity}" />
                    </div>
                </td>
                <td>
                    <div align="center">
                      <kul:htmlControlAttribute property="newAddress.line2" attributeEntry="${docAddressAttributes.line2}" readOnly="${readOnlyEntity}" />
                    </div>
                </td>
                <td>
                    <div align="center"><kul:htmlControlAttribute property="newAddress.line3" attributeEntry="${docAddressAttributes.line3}" readOnly="${readOnlyEntity}" />
                </div>
                </td>
                <td>
                    <div align="center"><kul:htmlControlAttribute property="newAddress.city" attributeEntry="${docAddressAttributes.city}" readOnly="${readOnlyEntity}" />
                    </div>
                </td>
                <td>
                    <div align="center"><kul:htmlControlAttribute property="newAddress.stateProvinceCode" attributeEntry="${docAddressAttributes.stateProvinceCode}" styleClass="fixed-size-200-select" readOnly="${readOnlyEntity}" />
                    </div>
                </td>
                <td>
                    <div align="center"><kul:htmlControlAttribute property="newAddress.postalCode" attributeEntry="${docAddressAttributes.postalCode}" readOnly="${readOnlyEntity}" />
                    </div>
                </td>
                <td>
                    <div align="center"><kul:htmlControlAttribute property="newAddress.countryCode" attributeEntry="${docAddressAttributes.countryCode}" styleClass="fixed-size-200-select" readOnly="${readOnlyEntity}" />
                    </div>
                </td>
                <td>
                    <div align="center"><kul:htmlControlAttribute property="newAddress.dflt" attributeEntry="${docAddressAttributes.dflt}" readOnly="${readOnlyEntity}" />
                    </div>
                </td>
                <td>
                    <div align="center"><kul:htmlControlAttribute property="newAddress.active" attributeEntry="${docAddressAttributes.active}" readOnly="${readOnlyEntity}" />
                    </div>
                </td>
                <td>
                    <div align="center">
                        <html:image property="methodToCall.addAddress.anchor${tabKey}"
                        src='${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif' styleClass="tinybutton"/>
                    </div>
                </td>
            </tr>
        </c:if>
        <c:forEach var="address" items="${KualiForm.document.addrs}" varStatus="status">
            <c:if test="${!(inquiry and readOnlyEntity and kfunc:isHiddenKimObjectType(address.addressType.code, 'kim.hide.PersonDocumentAddress.type'))}">
              <tr>
                <th class="infoline">
                    <c:out value="${status.index+1}" />
                </th>
                <kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.addrs[${status.index}].addressTypeCode" attributeEntry="${docAddressAttributes.addressTypeCode}" readOnlyAlternateDisplay="${fn:escapeXml(address.addressType.name)}" readOnly="${readOnlyEntity or !canModify}" />
                <kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.addrs[${status.index}].line1" attributeEntry="${docAddressAttributes.line1}" readOnly="${readOnlyEntity or !canModify}" displayMask="${maskData}" displayMaskValue="Xxxxxxx" />
                <kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.addrs[${status.index}].line2" attributeEntry="${docAddressAttributes.line2}" readOnly="${readOnlyEntity or !canModify}" displayMask="${maskData}" displayMaskValue="Xxxxxxx" />
                <kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.addrs[${status.index}].line3" attributeEntry="${docAddressAttributes.line3}" readOnly="${readOnlyEntity or !canModify}" displayMask="${maskData}" displayMaskValue="Xxxxxxx" />
                <kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.addrs[${status.index}].city" attributeEntry="${docAddressAttributes.city}" readOnly="${readOnlyEntity or !canModify}" displayMask="${maskData}" displayMaskValue="Xxxxxxx" />
                <kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.addrs[${status.index}].stateProvinceCode" attributeEntry="${docAddressAttributes.stateProvinceCode}" styleClass="fixed-size-200-select" readOnly="${readOnlyEntity or !canModify}" displayMask="${maskData}" displayMaskValue="XX" />
                <kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.addrs[${status.index}].postalCode" attributeEntry="${docAddressAttributes.postalCode}" readOnly="${readOnlyEntity or !canModify}" displayMask="${maskData}" displayMaskValue="XXXXX" />
                <kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.addrs[${status.index}].countryCode" attributeEntry="${docAddressAttributes.countryCode}" styleClass="fixed-size-200-select" readOnly="${readOnlyEntity or !canModify}" displayMask="${maskData}" displayMaskValue="XX" />
                <kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.addrs[${status.index}].dflt" attributeEntry="${docAddressAttributes.dflt}" readOnly="${readOnlyEntity or !canModify}" />
                <kim:cell inquiry="${inquiry}" valign="middle" cellClass="infoline" textAlign="center" property="document.addrs[${status.index}].active" attributeEntry="${docAddressAttributes.active}" readOnly="${readOnlyEntity or !canModify}" />

                <c:if test="${not inquiry and canModify}">
                    <td>
                        <div align="center">&nbsp;
                             <c:choose>
                               <c:when test="${address.edit or readOnlyEntity}">
                                  <img class='nobord' src='${ConfigProperties.kr.externalizable.images.url}tinybutton-delete2.gif' styleClass='tinybutton'/>
                               </c:when>
                               <c:otherwise>
                                  <html:image property='methodToCall.deleteAddress.line${status.index}.anchor${currentTabIndex}'
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
