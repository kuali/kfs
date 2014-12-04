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
