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

<%@ attribute name="property" required="true"
              description="the fully qualified name of the property being displayed by this cell.
              This could be in the document instead of the accounting line." %>
<%@ attribute name="textStyle" required="true" %>
<%@ attribute name="inquiry" required="false"
              description="Boolean indicating whether this cell should have an inquiry link if it's writable.
              If true, the boClassName, field, and conversionField attributes at least are required." %>
<%@ attribute name="boClassName" required="false" %>
<%@ attribute name="field" required="false"
              description="if the inquiry attribute is true, this must be the name of a field in the accounting line
              that is being displayed by this cell." %>
<%@ attribute name="conversionField" required="false"
              description="The name of the field in the business object corresponding to
              this cell's field  in the accounting line. This may be used to generate an inquiry." %>
<%@ attribute name="inquiryKeys" required="false"
              description="comma separated list of inquiry key names in the accountingLineValuesMap,
              in addition to the field attribute" %>
<%@ attribute name="accountingLineValuesMap" required="false" type="java.util.Map"
              description="map of the accounting line primitive fields and values, for inquiry keys" %>
<%@ attribute name="inquiryExtraKeyValues" required="false"
              description="ampersand separated list of inquiry key=value pairs not in accountingLineValuesMap" %>
<%@ attribute name="formattedNumberValue" required="false"
              description="number to format instead of property" %>

<c:set var="aKeyIsMissing" value="${empty accountingLineValuesMap[field]}"/>
<c:set var="keyValues" value="${conversionField}=${accountingLineValuesMap[field]}"/>
<c:forTokens var="key" items="${inquiryKeys}" delims=",">
    <c:set var="aKeyIsMissing" value="${missingKey || empty accountingLineValuesMap[key]}"/>
    <c:set var="keyValues" value="${keyValues}&${key}=${accountingLineValuesMap[key]}"/>
</c:forTokens>
<c:set var="keyValues" value="${keyValues}${empty inquiryExtraKeyValues ? '' : '&'}${inquiryExtraKeyValues}"/>
<c:set var="canRenderInquiry" value="${not empty keyValues && not aKeyIsMissing}"/>

<kul:inquiry
    boClassName="${boClassName}"
    keyValues="${keyValues}"
    render="${inquiry && canRenderInquiry}"
    >
    <c:choose>
        <c:when test="${empty formattedNumberValue}">
            <bean:write name="KualiForm" property="${property}"/>&nbsp;
        </c:when>
        <c:otherwise>
            ${formattedNumberValue}
        </c:otherwise>
    </c:choose>
    
</kul:inquiry>
