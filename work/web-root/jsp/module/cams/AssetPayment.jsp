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
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:documentPage showDocumentInfo="true"
	htmlFormAction="camsAssetPayment"
	documentTypeName="AssetPaymentDocument" renderMultipart="true"
	showTabButtons="true">
	<sys:documentOverview editingMode="${KualiForm.editingMode}" />
	<html:hidden property="document.capitalAssetNumber" />
	
	<cams:assetPaymentAllocation />
	<cams:assetPayments />

	<kul:tab tabTitle="Accounting Lines" defaultOpen="true"
		tabErrorKey="${KFSConstants.ACCOUNTING_LINE_ERRORS}">
		<c:choose>
			<c:when
				test="${KualiForm.document.capitalAssetBuilderOriginIndicator }">
				<sys-java:accountingLines>
					<sys-java:accountingLineGroup
						collectionPropertyName="document.sourceAccountingLines"
						collectionItemPropertyName="document.sourceAccountingLines"
						attributeGroupName="source" />
				</sys-java:accountingLines>
			</c:when>
			<c:otherwise>
				<sys-java:accountingLines>
					<sys-java:accountingLineGroup newLinePropertyName="newSourceLine"
						collectionPropertyName="document.sourceAccountingLines"
						collectionItemPropertyName="document.sourceAccountingLines"
						attributeGroupName="source" />
				</sys-java:accountingLines>
			</c:otherwise>
		</c:choose>
	</kul:tab>
	<cams:viewPaymentInProcessByAsset
		assetPaymentAssetDetail="${KualiForm.document.assetPaymentAssetDetail}"
		assetPaymentDetail="${KualiForm.document.sourceAccountingLines}" 
		assetPaymentDistribution="${KualiForm.document.assetPaymentDistributor.assetPaymentDistributions}" />

	<kul:notes />
	<kul:adHocRecipients />
	<kul:routeLog />
	<kul:superUserActions />
	<kul:panelFooter />
	<sys:documentControls
		transactionalDocument="${documentEntry.transactionalDocument}" />
</kul:documentPage>
