<%--
 Copyright 2006 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/core/tldHeader.jsp"%>

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
		src="images/buttonsmall_returnToIndex.gif" styleClass="globalbuttons"
		property="methodToCall.returnToIndex" alt="Return to Index" title="Return to Index" />
	&nbsp;&nbsp; <html:image src="images/buttonsmall_openExisting.gif"
		styleClass="globalbuttons" property="methodToCall.openExisting"
		alt="Open Existing Cash Management Document" title="Open Existing Cash Management Document" /></div>

</kul:page>
