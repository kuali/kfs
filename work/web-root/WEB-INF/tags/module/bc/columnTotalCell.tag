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

<%@ attribute name="cellProperty" required="true"
              description="the fully qualified name of the property being displayed by this cell.
              This could be in the document instead of the accounting line." %>
<%@ attribute name="textStyle" required="true" %>
<%@ attribute name="dataCellCssClass" required="true"
              description="The name of the CSS class for this data cell." %>

<%@ attribute name="rowSpan" required="false"
              description="row span for the data cell" %>
<%@ attribute name="colSpan" required="false"
              description="col span for the data cell" %>
<%@ attribute name="fieldAlign" required="false"
              description="div alignment. default is align=left" %>
<%@ attribute name="formattedNumberValue" required="false"
              description="number to format instead of property" %>
<%@ attribute name="disableHiddenField" required="false"
              description="determine whether the hidden field is needed for the given cell property" %>              


<c:if test="${empty fieldAlign}">
    <c:set var="fieldAlign" value="left"/>
</c:if>
<c:set var="rowSpan" value="${empty rowSpan ? 1 : rowSpan}"/>
<c:set var="colSpan" value="${empty colSpan ? 1 : colSpan}"/>

<td class="${dataCellCssClass}" valign="top" rowspan="${rowSpan}" colspan="${colSpan}">
<div style="text-align: ${fieldAlign};">
<strong>
<span class="nowrap">
    <c:choose>
        <c:when test="${empty formattedNumberValue}">
            <bean:write name="KualiForm" property="${cellProperty}"/>&nbsp;
        </c:when>
        <c:otherwise>
            ${formattedNumberValue}
        </c:otherwise>
    </c:choose>
</span>
</strong>
</div>
</td>
