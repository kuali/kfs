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

<%@ attribute name="accountingLine" required="false"
              description="The name in the form of the accounting line
              being edited or displayed by the row containing the cell containing this detail." %>
<%@ attribute name="detailField" required="false"
              description="The name of the accounting line field containing the detailed description
              of the value of this cell.  This attribute requires the accountingLine attribute.
              If this attribute is empty, this tag generates a blank line in the same CSS class,
              for consistent spacing." %>
<%@ attribute name="detailFields" required="false"
              description="The name of the accounting line fields containing detailed descriptions
              of the value of each cell. This attribute requires the accountingLine attribute.
              If this attribute is empty, this tag generates a blank line in the same CSS class,
              for consistent spacing. Any supplied field that starts with a semicolon will be treated as a text
              field, rather than a database field. The semicolon will be ignored in the output." %>
<%@ attribute name="dataFieldCssClass" required="false"
              description="The name of the CSS class for this data field." %>
<%@ attribute name="formattedNumberValue" required="false"
              description="number to format instead of property" %>              
              
<c:set var="class" value="${empty dataFieldCssClass ? 'fineprint' : dataFieldCssClass}"/> 
             
<c:if test="${!KualiForm.hideDetails}">
<%--    <br/> --%>
    <div id="${accountingLine}.${detailField}.div" class="${class}">
    <c:if test="${!empty detailFields}">
	    <c:forTokens var="key" items="${detailFields}" delims=",">
	        <c:set var="field" value="${key}"/>
			<c:choose>
			    <c:when test="${fn:startsWith(field,';')}">
	    			<c:out value="${fn:substringAfter(field,';')}" />
			    </c:when>
				<c:otherwise>		
					<bean:write name="KualiForm" property="${accountingLine}.${field}"/>
			    </c:otherwise>
    		</c:choose>    
	    </c:forTokens>    
    </c:if>
    
    <c:if test="${!empty detailField && empty detailFields && empty formattedNumberValue}">
        <bean:write name="KualiForm" property="${accountingLine}.${detailField}"/>
    </c:if>
    
    <c:if test="${!empty detailField && empty detailFields && not empty formattedNumberValue}">
        ${formattedNumberValue}
    </c:if>
  </div>
</c:if>
<c:if test="${!empty detailField}">
  <c:catch var="e">
 	<html:hidden property="${accountingLine}.${detailField}"/>
  </c:catch>
  <c:if test="${e!=null}">
    <input type="hidden" name="${accountingLine}.${detailField}" value="test"/>
  </c:if>
</c:if>

