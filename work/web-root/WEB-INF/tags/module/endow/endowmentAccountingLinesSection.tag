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

<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:tab tabTitle="Accounting Lines" defaultOpen="true" tabErrorKey="${EndowConstants.ACCOUNTING_LINE_ERRORS}">

 <div class="tab-container" align=center>
	<h3>Accounting Lines <a href="${KualiForm.accountingLineImportInstructionsUrl}" target="helpWindow"><img src="${ConfigProperties.kr.externalizable.images.url}my_cp_inf.gif" title="Accounting Line Import Help" src="Accounting Line Import Help" hspace="5" border="0" align="middle" /></a></h3>
	<c:if test="${hasSource}" >
		<endow:endowmentAccountingLines isSource="true" />
	</c:if>
	<c:if test="${hasTarget}" >
		<endow:endowmentAccountingLines isSource="false" />
	</c:if>
        
</div>

</kul:tab>
