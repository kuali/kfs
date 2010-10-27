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
<%@ attribute name="hasSource" required="true" %>
<%@ attribute name="hasTarget" required="true" %>
<%@ attribute name="hasUnits" required="true" %>
<%@ attribute name="isTransAmntReadOnly" required="true" %>

<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:tab tabTitle="Transaction Lines" defaultOpen="true" tabErrorKey="${EndowConstants.TRANSACTION_LINE_ERRORS}">

 <div class="tab-container" align=center>
	<h3>Transaction Lines <a href="${KualiForm.transactionLineImportInstructionsUrl}" target="helpWindow"><img src="${ConfigProperties.kr.externalizable.images.url}my_cp_inf.gif" title="Transaction Line Import Help" src="Transaction Line Import Help" hspace="5" border="0" align="middle" /></a></h3>
	<c:if test="${hasSource}" >
		<endow:endowmentTransactionalLines isSource="true" hasUnits="${hasUnits}" isTransAmntReadOnly="${isTransAmntReadOnly}"/>
	</c:if>
	<c:if test="${hasTarget}" >
		<endow:endowmentTransactionalLines isSource="false" hasUnits="${hasUnits}" isTransAmntReadOnly="${isTransAmntReadOnly}"/>
	</c:if>
        
</div>

</kul:tab>