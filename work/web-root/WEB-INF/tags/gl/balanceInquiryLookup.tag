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

<%@ attribute name="boClassName" required="true" %>
<%@ attribute name="actionPath" required="true" %>
<%@ attribute name="fieldConversions" required="false" %>
<%@ attribute name="lookupParameters" required="false" %>
<%@ attribute name="hideReturnLink" required="false" %>
<%@ attribute name="tabindexOverride" required="false" %>
<%@ attribute name="image" required="false"%>

<c:set var="imageName" value="${empty image ? 'searchicon.gif' : image}"/>

<c:choose>
  <c:when test="${!empty tabindexOverride}">
    <c:set var="tabindex" value="${tabindexOverride}"/>
  </c:when>
  <c:otherwise>
    <c:set var="tabindex" value="${KualiForm.nextArbitrarilyHighIndex}"/>
  </c:otherwise>
</c:choose>

<c:set var="balanceInquiryLookupButtonName" value="methodToCall.performBalanceInquiryLookup.(!!${boClassName}!!).(((${fieldConversions}))).((#${lookupParameters}#)).((<${hideReturnLink}>)).(([${actionPath}]))" />
${kfunc:registerEditableProperty(KualiForm, balanceInquiryLookupButtonName)}
<input type="image" tabindex="${tabindex}" name="${balanceInquiryLookupButtonName}"
   src="${ConfigProperties.kr.externalizable.images.url}${imageName}" alt="search" title="search" border="0" class="tinybutton" valign="middle"/>
