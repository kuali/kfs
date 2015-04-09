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

<c:set var="camsFullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] && not empty KualiForm.editingMode['allowCapitalAssetEdit']}" />

<kul:documentPage showDocumentInfo="true"
	documentTypeName="RequisitionDocument"
	htmlFormAction="purapRequisition" renderMultipart="true"
	showTabButtons="true">

    <c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] && (empty KualiForm.editingMode['restrictFiscalEntry'])}" />
 
	<sys:documentOverview editingMode="${KualiForm.editingMode}"
		includePostingYear="true"
        fiscalYearReadOnly="${not KualiForm.editingMode['allowPostingYearEntry']}"
        postingYearAttributes="${DataDictionary.RequisitionDocument.attributes}" >

    	<purap:purapDocumentDetail
	    	documentAttributes="${DataDictionary.RequisitionDocument.attributes}"
	    	detailSectionLabel="Requisition Detail"
	    	editableFundingSource="true" 
	    	editableAccountDistributionMethod="${KualiForm.readOnlyAccountDistributionMethod}" />
    </sys:documentOverview>

    <purap:delivery
        documentAttributes="${DataDictionary.RequisitionDocument.attributes}" 
        showDefaultBuildingOption="true" />

    <purap:vendor
        documentAttributes="${DataDictionary.RequisitionDocument.attributes}"
        displayRequisitionFields="true" />
 
    <purap:puritems itemAttributes="${DataDictionary.RequisitionItem.attributes}"
    	accountingLineAttributes="${DataDictionary.RequisitionAccount.attributes}" 
    	displayRequisitionFields="true"/>
    	
 	<purap:purCams documentAttributes="${DataDictionary.RequisitionDocument.attributes}"
		itemAttributes="${DataDictionary.RequisitionItem.attributes}" 
		camsItemAttributes="${DataDictionary.RequisitionCapitalAssetItem.attributes}" 
		camsSystemAttributes="${DataDictionary.RequisitionCapitalAssetSystem.attributes}"
		camsAssetAttributes="${DataDictionary.RequisitionItemCapitalAsset.attributes}"
		camsLocationAttributes="${DataDictionary.RequisitionCapitalAssetLocation.attributes}" 
		isRequisition="true"
		fullEntryMode="${camsFullEntryMode}" />

    <purap:paymentinfo
        documentAttributes="${DataDictionary.RequisitionDocument.attributes}" />

    <purap:additional
        documentAttributes="${DataDictionary.RequisitionDocument.attributes}"
        displayRequisitionFields="true" />
         
    <purap:summaryaccounts
        itemAttributes="${DataDictionary.RequisitionItem.attributes}"
    	documentAttributes="${DataDictionary.SourceAccountingLine.attributes}" />

    <purap:customRelatedDocuments
            documentAttributes="${DataDictionary.RelatedDocuments.attributes}" />
    
    <purap:customPaymentHistory
            documentAttributes="${DataDictionary.RelatedDocuments.attributes}" />
	     
	<kul:notes />         

	<kul:adHocRecipients />

	<kul:routeLog />

	<kul:superUserActions />

	<kul:panelFooter />
	
	<c:set var="extraButtons" value="${KualiForm.extraButtons}"/>  	

	<sys:documentControls transactionalDocument="true" extraButtons="${extraButtons}" />

</kul:documentPage>
