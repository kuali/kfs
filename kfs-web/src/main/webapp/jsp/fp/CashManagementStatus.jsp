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

<kul:page docTitle="Cash Management status" showDocumentInfo="false"
	headerTitle="Cash Management status" transactionalDocument="false"
	htmlFormAction="cashManagementStatus">

	<html:hidden property="verificationUnit" />
	<html:hidden property="controllingDocumentId" />
	<html:hidden property="currentDrawerStatus" />
	<html:hidden property="desiredDrawerStatus" />

	<br>
	<table width="100%" border=0 cellspacing=0 cellpadding=0>
		<html:messages id="msg" message="true">
			<tr>
				<td><bean:write filter="false" name="msg" /></td>
			</tr>
		</html:messages>
	</table>
	<br>

	<div id="globalbuttons" class="globalbuttons"><html:image
		src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_returnToIndex.gif" styleClass="globalbuttons"
		property="methodToCall.returnToIndex" alt="Return to Index" title="Return to Index" />
	&nbsp;&nbsp; <html:image src="${ConfigProperties.externalizable.images.url}buttonsmall_openExisting.gif"
		styleClass="globalbuttons" property="methodToCall.openExisting"
		alt="Open Existing Cash Management Document" title="Open Existing Cash Management Document" /></div>

</kul:page>
