<%--
 Copyright 2007 The Kuali Foundation
 
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
