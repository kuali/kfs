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

<kul:page docTitle="Card Application Question" showDocumentInfo="false"
	headerTitle="Card Application Question" transactionalDocument="false"
	htmlFormAction="tem*CardApplication">

	<html:hidden property="docTypeName" />
<div>

	<br>
	<table width="100%" border=0 cellspacing=0 cellpadding=0>
		<html:messages id="msg" message="true">
			<tr>
				<td><bean:write filter="false" name="msg" /></td>
			</tr>
		</html:messages>
	</table>
	<br>
</div>
	<div id="globalbuttons" class="globalbuttons"><html:image
		src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_Yes.gif" styleClass="globalbuttons"
		property="methodToCall.openNew" alt="Create new Card Application" title="Create new Card Application" />
	&nbsp;&nbsp; <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_No.gif"
		styleClass="globalbuttons" property="methodToCall.returnToIndex"
		alt="Return to Index" title="Return to Index" /></div>

</kul:page>
