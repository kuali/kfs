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

<c:set var="camsFullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] && (empty KualiForm.editingMode['restrictFiscalEntry'])}" />

<kul:documentPage showDocumentInfo="true"
    documentTypeName="PurchaseOrderDocument"
    htmlFormAction="purapPurchaseOrder" renderMultipart="true"
    showTabButtons="true">

    <c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

	<c:if test="${!empty KualiForm.editingMode['amendmentEntry']}">
		<c:set var="amendmentEntry" value="true" scope="request" />
	</c:if>

    <c:if test="${!empty KualiForm.editingMode['lockB2BEntry']}">
        <c:set var="lockB2BEntry" value="true" scope="request" />
    </c:if>

	<c:if test="${!empty KualiForm.editingMode['preRoute']}">
		<c:set var="preRouteChangeMode" value="true" scope="request" />
	</c:if>

	<c:if test="${((KualiForm.editingMode['displayRetransmitTab']))}">
        <c:set var="retransmitMode" value="true" scope="request" />
    </c:if>

    <c:if test="${!empty KualiForm.editingMode['splittingItemSelection']}">
    	<c:set var="splittingItemSelectionMode" value="true" scope="request"/>
    </c:if>

    <c:if test="${KualiForm.document.needWarning}">
    	<font color="black"><bean:message key="${PurapConstants.WARNING_PURCHASEORDER_NUMBER_DONT_DISCLOSE}" /></font>
    	<br><br>
    </c:if>

    <c:choose> 
	<c:when test="${KualiForm.document.assigningSensitiveData}">
		<purap:assignSensitiveData
			documentAttributes="${DataDictionary.PurchaseOrderDocument.attributes}"
	        itemAttributes="${DataDictionary.PurchaseOrderItem.attributes}"
	        poSensitiveDataAttributes="${DataDictionary.PurchaseOrderSensitiveData.attributes}"
	        sensitiveDataAssignAttributes="${DataDictionary.SensitiveDataAssignment.attributes}" />
    </c:when>

	<c:when test="${splittingItemSelectionMode}">
		<purap:splitPurchaseOrder
			documentAttributes="${DataDictionary.PurchaseOrderDocument.attributes}"
	        itemAttributes="${DataDictionary.PurchaseOrderItem.attributes}" />
    </c:when>

    <c:otherwise>
		<c:if test="${empty KualiForm.editingMode['amendmentEntry']}">
			<sys:documentOverview editingMode="${KualiForm.editingMode}"
		    	includePostingYear="true"
                fiscalYearReadOnly="${not KualiForm.editingMode['allowPostingYearEntry']}"
		        postingYearAttributes="${DataDictionary.PurchaseOrderDocument.attributes}" >
		        <purap:purapDocumentDetail
		        	documentAttributes="${DataDictionary.PurchaseOrderDocument.attributes}"
		            purchaseOrder="true"
		            detailSectionLabel="Purchase Order Detail"
	    			editableAccountDistributionMethod="${KualiForm.readOnlyAccountDistributionMethod}"
		            tabErrorKey="${PurapConstants.DETAIL_TAB_ERRORS}"/>
		    </sys:documentOverview>
		</c:if>

		<!--  TODO maybe we ought to rename the accountingLineEditingMode to something more generic -->
		<c:if test="${! empty KualiForm.editingMode['amendmentEntry']}">
		 	<c:set target="${KualiForm.accountingLineEditingMode}" property="fullEntry" value="true" />
		    <sys:documentOverview editingMode="${KualiForm.accountingLineEditingMode}"
		    	includePostingYear="true"
		        fiscalYearReadOnly="true"
		        postingYearAttributes="${DataDictionary.PurchaseOrderDocument.attributes}" >

		        <purap:purapDocumentDetail
		        	documentAttributes="${DataDictionary.PurchaseOrderDocument.attributes}"
		            purchaseOrder="true"
	    			editableAccountDistributionMethod="${KualiForm.readOnlyAccountDistributionMethod}"
		            detailSectionLabel="Purchase Order Detail" />
		    </sys:documentOverview>
		</c:if>

		<c:if test="${retransmitMode}" >
			<purap:purchaseOrderRetransmit 
		    	documentAttributes="${DataDictionary.PurchaseOrderDocument.attributes}"
		        itemAttributes="${DataDictionary.PurchaseOrderItem.attributes}"
		        displayPurchaseOrderFields="true" />
		</c:if>

		<c:if test="${not retransmitMode}" >
            <purap:delivery
                documentAttributes="${DataDictionary.PurchaseOrderDocument.attributes}" 
                showDefaultBuildingOption="false" />

		    <purap:vendor
		        documentAttributes="${DataDictionary.PurchaseOrderDocument.attributes}" 
		        displayPurchaseOrderFields="true"
		        purchaseOrderAwarded="${KualiForm.document.purchaseOrderAwarded}" />

		    <c:if test="${!lockB2BEntry}">
		        <purap:stipulationsAndInfo
		            documentAttributes="${DataDictionary.PurchaseOrderDocument.attributes}" />
		    </c:if>

		    <purap:puritems itemAttributes="${DataDictionary.PurchaseOrderItem.attributes}"
		        accountingLineAttributes="${DataDictionary.PurchaseOrderAccount.attributes}"
		        extraHiddenItemFields="documentNumber"/> 

			<purap:purCams documentAttributes="${DataDictionary.PurchaseOrderDocument.attributes}"
				itemAttributes="${DataDictionary.PurchaseOrderItem.attributes}" 
				camsItemAttributes="${DataDictionary.PurchaseOrderCapitalAssetItem.attributes}" 
				camsSystemAttributes="${DataDictionary.PurchaseOrderCapitalAssetSystem.attributes}"
				camsAssetAttributes="${DataDictionary.PurchaseOrderItemCapitalAsset.attributes}"
				camsLocationAttributes="${DataDictionary.PurchaseOrderCapitalAssetLocation.attributes}" 
				isPurchaseOrder="true"
				fullEntryMode="${camsFullEntryMode}" />

		    <purap:paymentinfo
		        documentAttributes="${DataDictionary.PurchaseOrderDocument.attributes}" 
		        displayPurchaseOrderFields="true"/>

		    <purap:additional
		        documentAttributes="${DataDictionary.PurchaseOrderDocument.attributes}" />

            <c:if test="${!lockB2BEntry}">
                <purap:quotes
                    documentAttributes="${DataDictionary.PurchaseOrderDocument.attributes}"
                    vendorQuoteAttributes="${DataDictionary.PurchaseOrderVendorQuote.attributes}"
                    isPurchaseOrderAwarded="${KualiForm.document.purchaseOrderAwarded}" />
            </c:if>

            <purap:summaryaccounts
                itemAttributes="${DataDictionary.PurchaseOrderItem.attributes}"
                documentAttributes="${DataDictionary.SourceAccountingLine.attributes}" />  

		    <purap:customRelatedDocuments
		            documentAttributes="${DataDictionary.RelatedDocuments.attributes}" />

		    <purap:customPaymentHistory
		            documentAttributes="${DataDictionary.RelatedDocuments.attributes}" />

		    <gl:generalLedgerPendingEntries />

			<kul:notes attachmentTypesValuesFinderClass="${documentEntry.attachmentTypesValuesFinderClass}" >
		        <html:messages id="warnings" property="noteWarning" message="true">
		            &nbsp;&nbsp;&nbsp;<bean:write name="warnings"/><br><br>
		        </html:messages>
		    </kul:notes>

		    <kul:adHocRecipients />

		    <kul:routeLog />

		    <kul:superUserActions />

		</c:if>
	</c:otherwise>
	</c:choose>

    <kul:panelFooter />

	<c:choose>
		<c:when test="${KualiForm.document.assigningSensitiveData}">
    		<sys:documentControls 
        		transactionalDocument="true" 
        		extraButtons="${KualiForm.extraButtons}"
        		suppressRoutingControls="true" />
		</c:when>
		<c:otherwise>
    		<sys:documentControls 
        		transactionalDocument="true" 
        		extraButtons="${KualiForm.extraButtons}" />
		</c:otherwise>
	</c:choose>

</kul:documentPage>

