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
	documentTypeName="ContractsGrantsCollectionActivityDocument"
	htmlFormAction="arContractsGrantsCollectionActivityDocument" renderMultipart="true"
	showTabButtons="true">
	
	<sys:hiddenDocumentFields isFinancialDocument="false" />

	<sys:documentOverview editingMode="${KualiForm.editingMode}" />
	
	<ar:contractsGrantsCollectionActivityAwardInformation readOnly="${readOnly}" />
		
	<kul:notes />

	<kul:adHocRecipients />

	<kul:routeLog />
	
	<kul:superUserActions />

	<kul:panelFooter />

	<sys:documentControls transactionalDocument="true"
		extraButtons="${KualiForm.extraButtons}" />
		
</kul:documentPage>
