<%--
 Copyright 2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/core/tldHeader.jsp"%>

<c:set var="universalUser" value="${DataDictionary.UniversalUser.attributes}" />
<c:set var="routingFormPersonnel" value="${DataDictionary.RoutingFormPersonnel.attributes}" />
<c:set var="viewOnly" value="${KualiForm.editingMode['viewOnly']}"/>

<div id="workarea">
  <c:forEach items = "${KualiForm.document.routingFormPersonnel}" var="person" varStatus="status"  >
    <c:set var="personName">
      ${person.user.personName}
      <c:if test="${person.personToBeNamedIndicator}">TO BE NAMED</c:if>
    </c:set>
    
    <c:set var="personRoleDescription">
      <c:choose>
        <c:when test="${empty person.personRole.personRoleDescription}">
          &nbsp
        </c:when>
        <c:otherwise>
          ${fn:substring(person.personRole.personRoleDescription, 0, 16)}
        </c:otherwise>
      </c:choose>
    </c:set>
    
    <c:set var="defaultOpen">
      <c:choose>
        <c:when test="${empty KualiForm.anchor || KualiForm.anchor == Constants.ANCHOR_TOP_OF_FORM}">
          false
        </c:when>
        <c:otherwise>
          ${KualiForm.anchor eq status.index}
        </c:otherwise>
      </c:choose>
    </c:set>
    
    <kul:tab tabTitle="${personName}" tabDescription="${personRoleDescription}" defaultOpen="${defaultOpen}" transparentBackground="${status.index eq 0}" tabErrorKey="document.routingFormPersonnel[${status.index}]*">
      <html:hidden property="document.routingFormPersonnel[${status.index}].personUniversalIdentifier" />
      <html:hidden property="document.routingFormPersonnel[${status.index}].routingFormPersonSequenceNumber" />
      <html:hidden property="document.routingFormPersonnel[${status.index}].chartOfAccountsCode" />
      <html:hidden property="document.routingFormPersonnel[${status.index}].organizationCode" />
      <html:hidden property="document.routingFormPersonnel[${status.index}].personToBeNamedIndicator" />
      <html:hidden property="document.routingFormPersonnel[${status.index}].personRoleCode" />
      <html:hidden property="document.routingFormPersonnel[${status.index}].personRoleText" />
      <html:hidden property="document.routingFormPersonnel[${status.index}].personFinancialAidPercent" />
      <html:hidden property="document.routingFormPersonnel[${status.index}].personCreditPercent" />
      <html:hidden property="document.routingFormPersonnel[${status.index}].personToBeNamedIndicator" />
      <html:hidden property="document.routingFormPersonnel[${status.index}].versionNumber" />
      <html:hidden property="document.routingFormPersonnel[${status.index}].user.personName" />
      <html:hidden property="document.routingFormPersonnel[${status.index}].user.personFirstName" />
      <html:hidden property="document.routingFormPersonnel[${status.index}].user.personMiddleName" />
      <html:hidden property="document.routingFormPersonnel[${status.index}].user.personLastName" />
      <html:hidden property="document.routingFormPersonnel[${status.index}].personRole.personRoleDescription" />

      <div class="tab-container" align="center">
        <div class="h2-container">
          <h2><span class="subhead-left">${personName}</span></h2>
          <span class="subhead-right"> <span class="subhead"><a href="asdf.html"><img src="images/my_cp_inf.gif" alt="help" width="15" height="14" border="0" align="absmiddle"></a></span> </span>
        </div>
        <table cellpadding=0 cellspacing="0" class="datatable">
          <tr class="datatable">
            <th align="right"><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.personRoleCode}" skipHelpUrl="true" readOnly="true"/></th>
            <td>${personRoleDescription}</td>
            <th align="right"><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.personLine1Address}" skipHelpUrl="true"/></th>
            <td><kul:htmlControlAttribute property="document.routingFormPersonnel[${status.index}].personLine1Address" attributeEntry="${routingFormPersonnel.personLine1Address}" readOnly="${viewOnly}"/></td>
          </tr>
          <tr class="datatable">
            <th align="right"><kul:htmlAttributeLabel attributeEntry="${universalUser.personFirstName}" skipHelpUrl="true" readOnly="true"/></th>
            <td>
              <c:if test="${person.personToBeNamedIndicator}">TO BE NAMED</c:if>
              <c:if test="${empty person.user.personFirstName}">&nbsp</c:if>${person.user.personFirstName}
            </td>
            <th align="right"><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.personLine2Address}" skipHelpUrl="true"/></th>
            <td><kul:htmlControlAttribute property="document.routingFormPersonnel[${status.index}].personLine2Address" attributeEntry="${routingFormPersonnel.personLine2Address}" readOnly="${viewOnly}"/></td>
          </tr>
          <tr class="datatable">
            <th align="right"><kul:htmlAttributeLabel attributeEntry="${universalUser.personMiddleName}" skipHelpUrl="true" readOnly="true"/></th>
            <td><c:if test="${empty person.user.personMiddleName}">&nbsp</c:if>${person.user.personMiddleName}</td>
            <th align="right"><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.personCityName}" skipHelpUrl="true"/></th>
            <td><kul:htmlControlAttribute property="document.routingFormPersonnel[${status.index}].personCityName" attributeEntry="${routingFormPersonnel.personCityName}" readOnly="${viewOnly}"/></td>
          </tr>
          <tr class="datatable">
            <th align="right"><kul:htmlAttributeLabel attributeEntry="${universalUser.personLastName}" skipHelpUrl="true" readOnly="true"/></th>
            <td><c:if test="${empty person.user.personLastName}">&nbsp</c:if>${person.user.personLastName}</td>
            <th align="right"><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.personCountyName}" skipHelpUrl="true"/></th>
            <td><kul:htmlControlAttribute property="document.routingFormPersonnel[${status.index}].personCountyName" attributeEntry="${routingFormPersonnel.personCountyName}" readOnly="${viewOnly}"/></td>
          </tr>
          <tr class="datatable">
            <th align="right"><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.personPrefixText}" skipHelpUrl="true"/></th>
            <td><kul:htmlControlAttribute property="document.routingFormPersonnel[${status.index}].personPrefixText" attributeEntry="${routingFormPersonnel.personPrefixText}" readOnly="${viewOnly}"/></td>
            <th align="right"><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.personStateCode}" skipHelpUrl="true"/></th>
            <td><kul:htmlControlAttribute property="document.routingFormPersonnel[${status.index}].personStateCode" attributeEntry="${routingFormPersonnel.personStateCode}" readOnly="${viewOnly}"/></td>
          </tr>
          <tr class="datatable">
            <th align="right"><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.personSuffixText}" skipHelpUrl="true"/></th>
            <td><kul:htmlControlAttribute property="document.routingFormPersonnel[${status.index}].personSuffixText" attributeEntry="${routingFormPersonnel.personSuffixText}" readOnly="${viewOnly}"/></td>
            <th align="right"><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.personCountryCode}" skipHelpUrl="true"/></th>
            <td><kul:htmlControlAttribute property="document.routingFormPersonnel[${status.index}].personCountryCode" attributeEntry="${routingFormPersonnel.personCountryCode}" readOnly="${viewOnly}"/></td>
          </tr>
          <tr class="datatable">
            <th align="right"><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.personPositionTitle}" skipHelpUrl="true"/></th>
            <td><kul:htmlControlAttribute property="document.routingFormPersonnel[${status.index}].personPositionTitle" attributeEntry="${routingFormPersonnel.personPositionTitle}" readOnly="${viewOnly}"/></td>
            <th align="right"><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.personZipCode}" skipHelpUrl="true"/></th>
            <td><kul:htmlControlAttribute property="document.routingFormPersonnel[${status.index}].personZipCode" attributeEntry="${routingFormPersonnel.personZipCode}" readOnly="${viewOnly}"/></td>
          </tr>
          <tr class="datatable">
            <th align="right"><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.chartOfAccountsCode}" skipHelpUrl="true" readOnly="true"/></th>
            <td><c:if test="${empty person.chartOfAccountsCode}">&nbsp</c:if>${person.chartOfAccountsCode}</td>
            <th align="right"><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.personPhoneNumber}" skipHelpUrl="true"/></th>
            <td><kul:htmlControlAttribute property="document.routingFormPersonnel[${status.index}].personPhoneNumber" attributeEntry="${routingFormPersonnel.personPhoneNumber}" readOnly="${viewOnly}"/></td>
          </tr>
          <tr class="datatable">
            <th align="right"><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.organizationCode}" skipHelpUrl="true" readOnly="true"/></th>
            <td><c:if test="${empty person.organizationCode}">&nbsp</c:if>${person.organizationCode}</td>
            <th align="right"><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.personFaxNumber}" skipHelpUrl="true"/></th>
            <td><kul:htmlControlAttribute property="document.routingFormPersonnel[${status.index}].personFaxNumber" attributeEntry="${routingFormPersonnel.personFaxNumber}" readOnly="${viewOnly}"/></td>
          </tr>
          <tr class="datatable">
            <th align="right"><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.personDivisionText}" skipHelpUrl="true"/></th>
            <td><kul:htmlControlAttribute property="document.routingFormPersonnel[${status.index}].personDivisionText" attributeEntry="${routingFormPersonnel.personDivisionText}" readOnly="${viewOnly}"/></td>
            <th align="right"><kul:htmlAttributeLabel attributeEntry="${routingFormPersonnel.personEmailAddress}" skipHelpUrl="true"/></th>
            <td><kul:htmlControlAttribute property="document.routingFormPersonnel[${status.index}].personEmailAddress" attributeEntry="${routingFormPersonnel.personEmailAddress}" readOnly="${viewOnly}"/></td>
          </tr>
        </table>
      </div>
    </kul:tab>
  </c:forEach>
  <c:choose>
    <c:when test="${empty KualiForm.document.routingFormPersonnel}">
      <div align="center">
	    No Person selected yet.
	  </div>
    </c:when>
    <c:otherwise>
      <table width="100%" border="0" cellpadding="0" cellspacing="0" class="b3" summary="">
        <tr>
          <td align="left" class="footer"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="bl3"></td>
          <td align="right" class="footer-right"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="br3"></td>
        </tr>
      </table>
    </c:otherwise>
  </c:choose>
</div>
