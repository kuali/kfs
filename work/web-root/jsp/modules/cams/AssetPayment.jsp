<%--
 Copyright 2005-2007 The Kuali Foundation.
 
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

<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<c:set var="assetAttributes" value="${DataDictionary.Asset.attributes}" />
<c:set var="paymentAttributes" 	value="${DataDictionary.AssetPaymentDocument.attributes}" />
<c:set var="readOnly" value="${empty KualiForm.editingMode['fullEntry']}" />

<kul:documentPage showDocumentInfo="true"  htmlFormAction="camsAssetPayment"  documentTypeName="AssetPaymentDocument" renderMultipart="true"  showTabButtons="true">

    <kul:hiddenDocumentFields />
  	<html:hidden property="document.capitalAssetNumber"/>
    <html:hidden property="document.representativeUniversalIdentifier" />
	<html:hidden property="document.organizationOwnerChartOfAccountsCode" />    
    <html:hidden property="document.organizationOwnerAccountNumber" />
	<html:hidden property="document.agencyNumber" />    
    <html:hidden property="document.campusCode" />
	<html:hidden property="document.buildingCode" />
	<html:hidden property="document.nextCapitalAssetPaymentLineNumber"/>

    <kul:documentOverview editingMode="${KualiForm.editingMode}" />

	<kul:tab tabTitle="Asset" defaultOpen="true"> 
	    <div class="tab-container" align="center">
	      <table width="100%" cellpadding="0" cellspacing="0" class="datatable">
	      	<tr>
				<td class="tab-subhead"  width="100%" colspan="4">Asset Information</td>
			</tr>	
		     <tr>
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.capitalAssetNumber}" /></th>
		      	<td class="grid" width="75%" colspan="3">
		      		<kul:htmlControlAttribute property="document.asset.capitalAssetNumber" attributeEntry="${assetAttributes.capitalAssetNumber}" readOnly="true" />
				</td>		      	
		     </tr>
		      <tr>
		      	<th class="grid" width="25%" align="right"><kul:htmlAttributeLabel attributeEntry="${assetAttributes.capitalAssetDescription}" /></th>
		      	<td class="grid" width="75%" colspan="3"><kul:htmlControlAttribute property="document.asset.capitalAssetDescription" attributeEntry="${assetAttributes.capitalAssetDescription}" readOnly="true"/></td>		      	
		     </tr>		    
		  </table>   
        </div>
	  </kul:tab>
	     
	<fin:accountingLines editingMode="${KualiForm.editingMode}"
		editableAccounts="${KualiForm.editableAccounts}"
		sourceAccountingLinesOnly="true"
		isOptionalFieldsInNewRow="true"		
		optionalFields="purchaseOrderNumber,requisitionNumber,expenditureFinancialDocumentNumber,expenditureFinancialDocumentTypeCode,expenditureFinancialSystemOriginationCode,expenditureFinancialDocumentPostedDate,financialDocumentPostingYear,financialDocumentPostingPeriodCode"		
		extraHiddenFields=",paymentApplicationDate,transferPaymentIndicator,financialDocumentLineNumber"
		sourceTotalsOverride="${KualiForm.assetPaymentTotals}">
	</fin:accountingLines>

	<cams:assetPaymentHeader defaultTabHide="true" />
	<cams:viewPayments 		defaultTabHide="true" assetPayments="${KualiForm.document.asset.assetPayments}" />	
    <kul:notes />
    <kul:adHocRecipients />
    <kul:routeLog />
    <kul:panelFooter />
    <kul:documentControls transactionalDocument="${documentEntry.transactionalDocument}" />
</kul:documentPage>