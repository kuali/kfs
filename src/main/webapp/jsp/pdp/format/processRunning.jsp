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

<kul:page headerTitle="Format Process is Running"
	transactionalDocument="false" showDocumentInfo="false" errorKey="foo"
	htmlFormAction="pdp/format" docTitle="Format Process is Running">
	
	The PDP format for Process ID "${KualiForm.formatProcessSummary.processId}" has started. An email will be sent to "${KualiForm.initiatorEmail}" when the process is complete. 
	<div id="globalbuttons" class="globalbuttons">
		<html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif" 
        	styleClass="globalbuttons" property="methodToCall.returnToPortal" title="Close Window" alt="Close"/>
	</div>

</kul:page>
