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

<c:set var="advanceDepositAttributes"
	value="${DataDictionary['AdvanceDepositDocument'].attributes}" />
<c:set var="readOnly"
	value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:documentPage showDocumentInfo="true"
	htmlFormAction="financialAdvanceDeposit"
	documentTypeName="AdvanceDepositDocument" renderMultipart="true"
	showTabButtons="true">
	<sys:hiddenDocumentFields />
	<sys:documentOverview editingMode="${KualiForm.editingMode}" />
	<SCRIPT type="text/javascript">
	    <!--
	        function submitForm() {
	            document.forms[0].submit();
	        }
	    //-->
	</SCRIPT>
	
	<fp:advanceDeposits editingMode="${KualiForm.editingMode}" />
	<kul:tab tabTitle="Accounting Lines" defaultOpen="true" tabErrorKey="${KFSConstants.ACCOUNTING_LINE_ERRORS},newSourceLine*">
		<sys-java:accountingLines>
			<sys-java:accountingLineGroup newLinePropertyName="newSourceLine" collectionPropertyName="document.sourceAccountingLines" collectionItemPropertyName="document.sourceAccountingLine" attributeGroupName="source" />
		</sys-java:accountingLines>
	</kul:tab>
	
	<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
  	<fp:capitalAccountingLines readOnly="${readOnly}"/>
  	
	<c:if test="${KualiForm.capitalAccountingLine.canCreateAsset}">
		<fp:capitalAssetCreateTab readOnly="${readOnly}"/>
	</c:if>
			
	<fp:capitalAssetModifyTab readOnly="${readOnly}"/>  
			
	<gl:generalLedgerPendingEntries />
	<kul:notes />
	<kul:adHocRecipients />
	<kul:routeLog />
	<kul:superUserActions />
	<kul:panelFooter />
	<sys:documentControls
		transactionalDocument="${documentEntry.transactionalDocument}"  extraButtons="${KualiForm.extraButtons}" />
</kul:documentPage>
