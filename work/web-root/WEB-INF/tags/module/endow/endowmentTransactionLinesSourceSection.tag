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
</div>
</kul:tab>
