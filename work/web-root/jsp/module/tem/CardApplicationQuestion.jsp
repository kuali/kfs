<%--
 Copyright 2013 The Kuali Foundation
 
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
