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