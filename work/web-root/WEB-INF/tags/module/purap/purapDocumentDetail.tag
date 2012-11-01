<%--
 Copyright 2006-2009 The Kuali Foundation
 
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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map" 
              description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="purchaseOrder" required="false"
              description="A boolean as to whether the document is a Purchase Order."%>
<%@ attribute name="paymentRequest" required="false"
              description="A boolean as to whether the document is a PREQ."%>              
<%@ attribute name="detailSectionLabel" required="true"
			  description="The label of the detail section."%>
<%@ attribute name="editableFundingSource" required="false"
			  description="Is fundingsourcecode editable?."%>
<%@ attribute name="tabErrorKey" required="false"
			  description="error map to display"%>
<%@ attribute name="editableAccountDistributionMethod" required="false"
			  description="Is editableAccountDistributionMethod editable?"%>
			
<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] && (empty KualiForm.editingMode['restrictFiscalEntry'])}" />
<c:if test="${empty editableFundingSource}">
	<c:set var="editableFundingSource" value="false" />
</c:if>

<c:if test="${amendmentEntry}">
	<c:if test="${KualiForm.readOnlyReceivingRequired eq 'true'}">	
		<c:set var="readOnlyReceivingRequired" value="true" />
	</c:if>
</c:if>

<c:set var="useTaxIndicatorButton" value="changeusetax" scope="request" />
<c:if test="${KualiForm.document.useTaxIndicator}">
	<c:set var="useTaxIndicatorButton" value="changesalestax" scope="request" />
</c:if>

<c:set var="purapTaxEnabled" value="${(not empty KualiForm.editingMode['purapTaxEnabled'])}" />
<c:set var="contentReadOnly" value="${(not empty KualiForm.editingMode['lockContentEntry'])}" />
<c:set var="internalPurchasingReadOnly" value="${(not empty KualiForm.editingMode['lockInternalPurchasingEntry'])}" />
<c:set var="tabindexOverrideBase" value="10" />
<c:set var="poOutForQuote" value="${KualiForm.document.applicationDocumentStatus eq 'Out for Quote'}" />

<h3><c:out value="${detailSectionLabel}"/> </h3>
<div class="tab-container-error"><div class="left-errmsg-tab"><kul:errors keyMatch="document.assignedUserPrincipalName,document.purchaseOrderPreviousIdentifier"/></div></div>		        



<table cellpadding="0" cellspacing="0" class="datatable" summary="Detail Section">
    <%-- only display on REQ and PO --%>
    <c:if test="${not paymentRequest}">
	    <tr>
	        <th align=right valign=middle class="bord-l-b">
	            <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.organizationCode}" /></div>
	        </th>
	        <td align=left valign=middle class="datacell">
	            <kul:htmlControlAttribute attributeEntry="${documentAttributes.chartOfAccountsCode}" property="document.chartOfAccountsCode" readOnly="true" />
	            &nbsp;/&nbsp;<kul:htmlControlAttribute attributeEntry="${documentAttributes.organizationCode}" property="document.organizationCode"  readOnly="true"/>
	            <c:if test="${(fullEntryMode or amendmentEntry) and not (contentReadOnly or internalPurchasingReadOnly)}" >
	                <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Organization" fieldConversions="organizationCode:document.organizationCode,chartOfAccountsCode:document.chartOfAccountsCode" />
	            </c:if>
	        </td>
	        <th align=right valign=middle class="bord-l-b">
	            <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.documentFundingSourceCode}" /></div>
	        </th>
	        <td align=left valign=middle class="datacell">
	            <kul:htmlControlAttribute
	                property="document.documentFundingSourceCode"
	                attributeEntry="${documentAttributes.documentFundingSourceCode}"
	                extraReadOnlyProperty="document.fundingSource.fundingSourceDescription"
	                readOnly="${not (fullEntryMode and editableFundingSource)}"
	                tabindexOverride="${tabindexOverrideBase + 5}"/>
	        </td>
	    </tr>
    </c:if>
	
	<%-- no need to display this row if both fields are hidden; when available, fields display on all doc types (REQ, PO, PREQ) --%>
	<c:if test="${KualiForm.document.enableReceivingDocumentRequiredIndicator or KualiForm.document.enablePaymentRequestPositiveApprovalIndicator}">
		<tr>
		  <c:if test="${KualiForm.document.enableReceivingDocumentRequiredIndicator}">			
	        <th align=right valign=middle class="bord-l-b">         
	            <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.receivingDocumentRequiredIndicator}" /></div>
	        </th>
	        <td align=left valign=middle class="datacell">
	            <kul:htmlControlAttribute
	                property="document.receivingDocumentRequiredIndicator"
	                attributeEntry="${documentAttributes.receivingDocumentRequiredIndicator}"
	                readOnly="${paymentRequest or 
	                readOnlyReceivingRequired or 
	                not(fullEntryMode or amendmentEntry) and 
	                not (contentReadOnly or internalPurchasingReadOnly)}"
	                tabindexOverride="${tabindexOverrideBase + 0}"/>
	        </td>
	      </c:if>
		  <c:if test="${not KualiForm.document.enableReceivingDocumentRequiredIndicator}">	
		    <th align=right valign=middle class="bord-l-b">&nbsp;</th>
		    <td align=left valign=middle class="datacell">&nbsp;</td>
		  </c:if>
		  <c:if test="${KualiForm.document.enablePaymentRequestPositiveApprovalIndicator}">				        
			<th align=right valign=middle class="bord-l-b">
			  <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.paymentRequestPositiveApprovalIndicator}" /></div>
			</th>
			<td align=left valign=middle class="datacell">
			  <kul:htmlControlAttribute
			      property="document.paymentRequestPositiveApprovalIndicator"
				  attributeEntry="${documentAttributes.paymentRequestPositiveApprovalIndicator}"
				  readOnly="${paymentRequest or not(fullEntryMode or amendmentEntry) and not (contentReadOnly or internalPurchasingReadOnly)}"
			  	  tabindexOverride="${tabindexOverrideBase + 5}"/>
			</td>
		  </c:if>
		  <c:if test="${not KualiForm.document.enablePaymentRequestPositiveApprovalIndicator}">
		    <th align=right valign=middle class="bord-l-b">&nbsp;</th>
		    <td align=left valign=middle class="datacell">&nbsp;</td>	          				        
	      </c:if>			  
		</tr>  
	</c:if>

	<%-- the following rows only need to be displayed for the PO --%>
	<c:if test="${purchaseOrder}">
		<tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.contractManager}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute 
                    property="document.contractManager.contractManagerName" 
                    attributeEntry="${documentAttributes.contractManagerName}" 
                    readOnly="true" tabindexOverride="${tabindexOverrideBase + 0}" />
                <c:if test="${preRouteChangeMode}" >
                    <kul:lookup
                        boClassName="org.kuali.kfs.vnd.businessobject.ContractManager"
                        fieldConversions="contractManagerName:document.contractManager.contractManagerName,contractManagerCode:document.contractManagerCode" />
                </c:if>                     
            </td>
		   	<th align=right valign=middle class="bord-l-b">
		        <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderPreviousIdentifier}" /></div>
		    </th>
		    <td align=left valign=middle class="datacell">
		       	<kul:htmlControlAttribute 
		            property="document.purchaseOrderPreviousIdentifier" 
		            attributeEntry="${documentAttributes.purchaseOrderPreviousIdentifier}" 
		            readOnly="${not (fullEntryMode or amendmentEntry)}" 
		            tabindexOverride="${tabindexOverrideBase + 5}" />
		    </td>
		</tr>
	    <tr>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.assignedUserPrincipalName}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
             	<kul:htmlControlAttribute 
                    property="document.assignedUserPrincipalName" 
                    attributeEntry="${documentAttributes.assignedUserPrincipalName}" 
                    readOnly="${!fullEntryMode and !amendmentEntry}" tabindexOverride="${tabindexOverrideBase + 0}" />
                <c:if test="${fullEntryMode or amendmentEntry}"  >
                    <kul:lookup boClassName="org.kuali.rice.kim.api.identity.Person" 
                    	fieldConversions="principalId:document.assignedUserPrincipalId,principalName:document.assignedUserPrincipalName" /></div>
                </c:if>                     
            </td>
            <th align=right valign=middle class="bord-l-b">
                <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderConfirmedIndicator}" /></div>
            </th>
            <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute 
                    property="document.purchaseOrderConfirmedIndicator"
                    attributeEntry="${documentAttributes.purchaseOrderConfirmedIndicator}" 
                    readOnly="${not (fullEntryMode or amendmentEntry)}" 
                    tabindexOverride="${tabindexOverrideBase + 5}" />
            </td> 
		</tr>		
	</c:if>

    <%-- row only needs to be displayed if tax is enabled or if the doc is a PO --%>
    <c:if test="${purapTaxEnabled or purchaseOrder}">
	    <tr>
	        <c:if test="${purapTaxEnabled}">
		        <th align=right valign=middle class="bord-l-b">
		            <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.useTaxIndicator}" /></div>
		        </th>
		        <td align=left valign=middle class="datacell">
		            <kul:htmlControlAttribute
		                property="document.useTaxIndicator"
		                attributeEntry="${documentAttributes.useTaxIndicator}"
		                readOnly="true"/>&nbsp;
		            <c:if test="${fullEntryMode and paymentRequest}">          
		                <html:image property="methodToCall.changeUseTaxIndicator" src="${ConfigProperties.externalizable.images.url}tinybutton-${useTaxIndicatorButton}.gif" alt="Change Use Tax Indicator" title="Change Use Tax Indicator" styleClass="tinybutton"  tabindex="${tabindexOverrideBase + 0}" />
		            </c:if>
		        </td>
	        </c:if>
	        <c:if test="${not purapTaxEnabled}">
	            <th align=right valign=middle class="bord-l-b">&nbsp;</th>
	            <td align=left valign=middle class="datacell">&nbsp;</td>
	        </c:if>
			<c:if test="${purchaseOrder}">
	            <th align=right valign=middle class="bord-l-b">
	                <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.requisitionSource}" /></div>
	            </th>
	            <td align=left valign=middle class="datacell">
	                <kul:htmlControlAttribute 
	                    property="document.requisitionSource.requisitionSourceDescription" 
	                    attributeEntry="${documentAttributes.requisitionSource}" 
	                    readOnly="true" />
	            </td>                   
			</c:if>
	        <c:if test="${not purchaseOrder}">
	            <th align=right valign=middle class="bord-l-b">&nbsp;</th>
	            <td align=left valign=middle class="datacell">&nbsp;</td>
	        </c:if>
	    </tr>
	  </c:if>

      <%-- always display this row --%>
	  <tr>
	      <th align=right valign=middle class="bord-l-b">
	          <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.accountDistributionMethod}" /></div>
	      </th>
	      <td align=left valign=middle class="datacell">
	          <kul:htmlControlAttribute
	              property="document.accountDistributionMethod"
	              attributeEntry="${documentAttributes.accountDistributionMethod}"
	              readOnly="${editableAccountDistributionMethod or not(fullEntryMode or amendmentEntry)}"
	              tabindexOverride="${tabindexOverrideBase + 0}"/>
	      </td>
          <th align=right valign=middle class="bord-l-b">&nbsp;</th>
          <td align=left valign=middle class="datacell">&nbsp;</td>
	  </tr>  


</table>
	
<c:if test="${purchaseOrder and preRouteChangeMode and !poOutForQuote and !amendmentEntry}">
	<h3>Status Changes</h3>
		
	<table cellpadding="0" cellspacing="0" class="datatable" summary="Status Changes Section">
		<tr>
			<th align=right valign=middle class="bord-l-b">
	            <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.statusChange}" /></div>
	        </th>
	        <td align=left valign=middle class="datacell">&nbsp;
		        <html:radio title="${documentAttributes.statusChange.label} - None" property="statusChange" value="${PurapConstants.PurchaseOrderStatuses.APPDOC_IN_PROCESS}" tabindex="${tabindexOverrideBase + 9}" />&nbsp;None&nbsp;
				<html:radio title="${documentAttributes.statusChange.label} - Department" property="statusChange" value="${PurapConstants.PurchaseOrderStatuses.APPDOC_WAITING_FOR_DEPARTMENT}" tabindex="${tabindexOverrideBase + 9}" />&nbsp;Department&nbsp;
				<html:radio title="${documentAttributes.statusChange.label} - Vendor" property="statusChange" value="${PurapConstants.PurchaseOrderStatuses.APPDOC_WAITING_FOR_VENDOR}" tabindex="${tabindexOverrideBase + 9}" />&nbsp;Vendor&nbsp;
			</td>
		</tr>
	</table>
</c:if>

