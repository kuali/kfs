<%--
 Copyright 2006-2007 The Kuali Foundation.
 
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
<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib prefix="fn" uri="/tlds/fn.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd"%>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map" 
              description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="purchaseOrder" required="false"
              description="A boolean as to whether the document is a Purchase Order."%>
<%@ attribute name="detailSectionLabel" required="true"
			  description="The label of the detail section."%>

<c:set var="readOnly" value="${empty KualiForm.editingMode['fullEntry']}" />

<div class="h2-container">
	<h2><c:out value="${detailSectionLabel}"/></h2>
</div>

<table cellpadding="0" cellspacing="0" class="datatable" summary="Detail Section">
    <tr>
        <th align=right valign=middle class="bord-l-b">
            <div align="right">Related Documents:</div>
        </th>
        <td align=left valign=middle class="datacell">
        <!-- "View" links only work if you comment out lines 130 and 131 of KualiDocumentActionBase (but don't commit the lines commented out!!) -->
            <!--  html:image property="methodToCall.viewRelatedDocuments" src="images/tinybutton-view.gif" title="View Related Documents" alt="View Related Documents" styleClass="tinybutton"/ -->
            <a href="purapRequisition.do?methodToCall=viewRelatedDocuments" tabindex="1000000" target="purapWindow"  title="View Related Documents">View</a>
        </td>
        <th align=right valign=middle class="bord-l-b">
            <div align="right">Payment History:</div>
        </th>
        <td align=left valign=middle class="datacell">
            <!-- html:image property="methodToCall.viewPaymentHistory" src="images/tinybutton-view.gif" title="View Related Documents" alt="View Related Documents" styleClass="tinybutton"/ -->
            <a href="purapRequisition.do?methodToCall=viewPaymentHistory&docTypeName=KualiRequisitionDocument" tabindex="1000000" target="purapWindow"  title="View Payment History">View R</a>
        </td>
    </tr>
    <tr>
        <th align=right valign=middle class="bord-l-b">
            <div align="right">
            	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.contractManagerName}" />
            </div>
        </th>
        <td align=left valign=middle class="datacell">
            <kul:htmlControlAttribute 
                property="document.contractManagerName" 
                attributeEntry="${documentAttributes.contractManagerName}" 
                readOnly="true" />
        </td>
        <th align=right valign=middle class="bord-l-b">
            <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.fundingSourceCode}" /></div>
        </th>
        <td align=left valign=middle class="datacell">
            <kul:htmlControlAttribute
                property="document.fundingSourceCode"
                attributeEntry="${documentAttributes.fundingSourceCode}"
                readOnly="true"/>
        </td>
    </tr>
    <c:if test="${purchaseOrder}">
    		<html:hidden property="document.purchaseOrderCreateDate" />
	    <tr>
	    	<th align=right valign=middle class="bord-l-b">
	            <div align="right">
	            	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderPreviousIdentifier}" />
	            </div>
	        </th>
	        <td align=left valign=middle class="datacell">
	        	<kul:htmlControlAttribute 
	                property="document.purchaseOrderPreviousIdentifier" 
	                attributeEntry="${documentAttributes.purchaseOrderPreviousIdentifier}" 
	                readOnly="true" />
	        </td>
	        <th align=right valign=middle class="bord-l-b">
	            <div align="right">
	            	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderConfirmedIndicator}" />
	            </div>
	        </th>
	        <td align=left valign=middle class="datacell">
	        	<kul:htmlControlAttribute 
	                property="document.purchaseOrderConfirmedIndicator"
	                attributeEntry="${documentAttributes.purchaseOrderConfirmedIndicator}" 
	                readOnly="${readOnly}" />
	        </td> 
	    </tr>
	 </c:if>
</table>

<c:if test="${purchaseOrder}">
	<div class="h2-container">
	    <h2>Status Changes</h2>
	</div>
	
	<table cellpadding="0" cellspacing="0" class="datatable" summary="Status Changes Section">
		<tr>
			<th align=right valign=middle class="bord-l-b">
	            <div align="right">
	            	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.statusChange}" />
	            </div>
	        </th>
	        <td align=left valign=middle class="datacell">
	        	<kul:htmlControlAttribute 
	                property="document.statusChange" 
	                attributeEntry="${documentAttributes.statusChange}" 
	                readOnly="${readOnly}" />
	        </td>
			<th align=right valign=middle class="bord-l-b" rowspan="2">
	            <div align="right">
	            	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.statusChangeNote}" />
	            </div>
	        </th>
	        <td align=left valign=middle class="datacell" rowspan="2">
	        	<kul:htmlControlAttribute 
	                property="document.statusChangeNote" 
	                attributeEntry="${documentAttributes.statusChangeNote}" 
	                readOnly="${readOnly}" />
	        </td>	         		
		</tr>
		<tr>
			<th align=right valign=middle class="bord-l-b">
	            <div align="right">
	            	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.requisitionSource}" />
	            </div>
	        </th>
	        <td align=left valign=middle class="datacell">
	        	<kul:htmlControlAttribute 
	                property="document.requisitionSource.requisitionSourceDescription" 
	                attributeEntry="${documentAttributes.requisitionSource}" 
	                readOnly="true" />
	        </td>
		</tr>
	</table>
</c:if>
