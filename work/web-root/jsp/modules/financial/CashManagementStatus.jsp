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
