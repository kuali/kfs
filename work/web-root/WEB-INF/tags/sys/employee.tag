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

<%@ attribute name="userIdFieldName" required="true" %>
<%@ attribute name="userNameFieldName" required="true" %>

<%@ attribute name="label" required="false" %>
<%@ attribute name="fieldConversions" required="false" %>
<%@ attribute name="lookupParameters" required="false" %>
<%@ attribute name="referencesToRefresh" required="false" %>

<%@ attribute name="hasErrors" required="false" %>
<%@ attribute name="readOnly" required="false" %>
<%@ attribute name="onblur" required="false" %>

<%@ attribute name="highlight" required="false"
              description="boolean indicating if this field is rendered as highlighted (to indicate old/new value change)" %>
               <%@ attribute name="forceRequired" required="false" %>
               
<script language="JavaScript" type="text/javascript" src="dwr/interface/PersonService.js"></script>
<script language="JavaScript" type="text/javascript" src="scripts/sys/objectInfo.js"></script>

<kul:htmlControlAttribute property="${userIdFieldName}" 
                    attributeEntry="${DataDictionary['PersonImpl'].attributes.employeeId}" forceRequired="${forceRequired}"
                    onblur="loadEmplInfo( '${userIdFieldName}', '${userNameFieldName}' );${onblur}" readOnly="${readOnly}"/>
<c:if test="${!readOnly}">
	<kul:lookup boClassName="org.kuali.rice.kim.api.identity.Person" 
		        fieldConversions="${fieldConversions}" 
				lookupParameters="${lookupParameters}" 
				fieldLabel="${label}" 
				referencesToRefresh="${referencesToRefresh}"
				anchor="${currentTabIndex}"/>
</c:if>

<c:if test="${readOnly}">
  <div>${userName}</div>
</c:if>

<div id="${userNameFieldName}.div">
    <html:hidden write="true" property="${userNameFieldName}"/>       
</div>
	
<c:if test="${!empty universalIdFieldName}">
	<input type="hidden" name="${universalIdFieldName}" value="${universalId}" />
</c:if>
<c:if test="${!empty userNameFieldName}">
	<input type="hidden" name="${userNameFieldName}" value="${userName}" />
</c:if>

<c:if test="${highlight}">
<kul:fieldShowChangedIcon/>
</c:if>


