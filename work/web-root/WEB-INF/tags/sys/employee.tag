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


