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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map" 
              description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="purchaseOrder" required="false"
              description="A boolean as to whether the document is a Purchase Order."%>
<%@ attribute name="detailSectionLabel" required="true"
			  description="The label of the detail section."%>
<%@ attribute name="editableFundingSource" required="false"
			  description="Is fundingsourcecode editable?."%>

<c:if test="${empty editableFundingSource}">
	<c:set var="editableFundingSource" value="false" />
</c:if>

<div class="h2-container">
	<h2><c:out value="${detailSectionLabel}"/></h2>
</div>

<table cellpadding="0" cellspacing="0" class="datatable" summary="Detail Section">
    <tr>
        <th align=right valign=middle class="bord-l-b">
            <div align="right">
            	<kul:htmlAttributeLabel attributeEntry="${documentAttributes.contractManager}" />
            </div>
        </th>
        <td align=left valign=middle class="datacell">
            <kul:htmlControlAttribute 
                property="document.contractManager.contractManagerName" 
                attributeEntry="${documentAttributes.contractManagerName}" 
                readOnly="true" />
            <c:if test="${contractManagerChangeMode}" >
	            <kul:lookup
	            	boClassName="org.kuali.module.vendor.bo.ContractManager"
	            	fieldConversions="contractManagerName:document.contractManager.contractManagerName" />
	        </c:if>
        </td>
        <th align=right valign=middle class="bord-l-b">
            <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.fundingSourceCode}" /></div>
        </th>
        <td align=left valign=middle class="datacell">
		    <c:if test="${editableFundingSource}">
            <kul:htmlControlAttribute
                property="document.fundingSourceCode"
                attributeEntry="${documentAttributes.fundingSourceCode}"
                readOnly="${not fullEntryMode}"/>
			</c:if>
		    <c:if test="${!editableFundingSource}">
            <kul:htmlControlAttribute
                property="document.fundingSource.fundingSourceDescription"
                attributeEntry="${documentAttributes.fundingSourceCode}"
                readOnly="true"/>
			</c:if>
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
	                readOnly="${not fullEntryMode}" />
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
	        	<c:if test="${fullEntryMode}">
	        		<c:choose>
	        			<c:when test="${KualiForm.document.statusCode == 'INPR'}">
			        		&nbsp;<input type=radio name="document.statusChange" value="INPR" checked/>&nbsp;NONE&nbsp;
			        	</c:when>
			        	<c:otherwise>
			        		&nbsp;<input type=radio name="document.statusChange" value="INPR"/>&nbsp;NONE&nbsp;
			        	</c:otherwise>
			        </c:choose>
			        <c:choose>
			        	<c:when test="${KualiForm.document.statusCode == 'WDPT'}">
			        		<input type=radio name="document.statusChange" value="WDPT" checked/>&nbsp;Department&nbsp;
			        	</c:when>
			        	<c:otherwise>
			        		<input type=radio name="document.statusChange" value="WDPT" />&nbsp;Department&nbsp;
			        	</c:otherwise>
			        </c:choose>
			        <c:choose>
			        	<c:when test="${KualiForm.document.statusCode == 'WVEN'}">
			        		<input type=radio name="document.statusChange" value="WVEN" checked/>&nbsp;Vendor&nbsp;
			        	</c:when>
			        	<c:otherwise>
			        		<input type=radio name="document.statusChange" value="WVEN" />&nbsp;Vendor&nbsp;
			        	</c:otherwise>
			        </c:choose>
		        </c:if>	        	
	        </td>
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
