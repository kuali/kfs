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

<%@ attribute name="editingMode" required="false" description="used to decide if items may be edited" type="java.util.Map"%>
<%@ attribute name="showSource" required="true" %>
<%@ attribute name="showTarget" required="true" %>
<%@ attribute name="showRegistrationCode" required="true" %>
<%@ attribute name="openTabByDefault" required="true" %>
<%@ attribute name="showLabels" required="true" %>
<%@ attribute name="securityRequired" required="false" %>

<!-- set default to true if its not provided from the tag attribute -->
<c:choose>
<c:when test="${not empty securityRequired}" >
  <c:set var="showRequired" value="${securityRequired}" />
</c:when>
<c:otherwise>
  <c:set var="showRequired" value="true" />
</c:otherwise>
</c:choose>

<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:tab tabTitle="Security Details" defaultOpen="${openTabByDefault}" tabErrorKey="${EndowConstants.TRANSACTION_SECURITY_TAB_ERRORS}">

 <div class="tab-container" align=center>
	<h3>Security Details</h3>
	<c:if test="${showSource}" >
		<endow:endowmentSecurityTransactionDetails showTarget="false" showSource="true" showRegistrationCode="true" showLabels="${showLabels}" showRequired="${showRequired}"/>
	</c:if>
	<c:if test="${showTarget}" >
		<endow:endowmentSecurityTransactionDetails showTarget="true" showSource="false" showRegistrationCode="true" showLabels="${showLabels}" showRequired="${showRequired}"/>
	</c:if>
       
</div>

</kul:tab>
