<%--
 Copyright 2006-2008 The Kuali Foundation
 
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

<script type='text/javascript'>
	function toggle(id) {
		var v = document.getElementById(id);
		if ('none' != v.style.display) {
			v.style.display = 'none';
		} else {
			v.style.display = '';
		}
	}
</script>

<c:if test="${!accountingLineScriptsLoaded}">
	<script type='text/javascript' src="dwr/interface/ChartService.js"></script>
	<script type='text/javascript' src="dwr/interface/AccountService.js"></script>
	<script type='text/javascript' src="dwr/interface/SubAccountService.js"></script>
	<script type='text/javascript' src="dwr/interface/ObjectCodeService.js"></script>
	<script type='text/javascript' src="dwr/interface/ObjectTypeService.js"></script>
	<script type='text/javascript'
		src="dwr/interface/SubObjectCodeService.js"></script>
	<script type='text/javascript'
		src="dwr/interface/ProjectCodeService.js"></script>
	<script type='text/javascript'
		src="dwr/interface/OriginationCodeService.js"></script>
	<script language="JavaScript" type="text/javascript"
		src="scripts/sys/objectInfo.js"></script>
	<c:set var="accountingLineScriptsLoaded" value="true" scope="request" />
</c:if>

<c:set var="readOnly"
	value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:documentPage showDocumentInfo="true"
	documentTypeName="CollectionActivityDocument"
	htmlFormAction="arCollectionActivityDocument" renderMultipart="true"
	showTabButtons="true">
	
	<sys:hiddenDocumentFields isFinancialDocument="false" />

	<sys:documentOverview editingMode="${KualiForm.editingMode}" />
	
	<ar:collectionActivityAwardInformation readOnly="${readOnly}" />
		
	<kul:notes />

	<kul:adHocRecipients />

	<kul:routeLog />

	<kul:panelFooter />

	<sys:documentControls transactionalDocument="true"
		extraButtons="${KualiForm.extraButtons}" />
		
</kul:documentPage>