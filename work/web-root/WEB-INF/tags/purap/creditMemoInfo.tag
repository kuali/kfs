<%--
 Copyright 2007 The Kuali Foundation.
 
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
              description="The DataDictionary entry containing attributes for this row's fields." %>
              
<c:set var="purchaseOrderAttributes" value="${DataDictionary.PurchaseOrderDocument.attributes}" />

<kul:tab tabTitle="Credit Memo Info" defaultOpen="true">
   
    <div class="tab-container" align=center>
        <div class="h2-container">
            <h2>Credit Memo Info</h2>
        </div>

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Credit Memo Info Section">

            <tr>
                <th align=right valign=middle class="bord-l-b" width="25%">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.creditMemoNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell" width="25%">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.creditMemoNumber}" property="document.creditMemoNumber" readOnly="true" /> 
                </td>
                <th align=right valign=middle class="bord-l-b" width="25%">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.creditMemoType}" /></div>
                </th>
                <td align=left valign=middle class="datacell" width="25%">
                   <bean:write name="KualiForm" property="document.creditMemoType" />
                </td>
            </tr>
            
            <tr>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.creditMemoDate}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.creditMemoDate}" property="document.creditMemoDate" readOnly="true" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorNumber}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorNumber}" property="document.vendorNumber" readOnly="true" />
                </td>
             </tr>
             
             <tr>
                <th align=right valign=middle class="bord-l-b">                   
                	<c:choose>
                	<c:when test="${KualiForm.fullDocumentEntryCompleted eq false}">
					<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.creditMemoAmount}" useShortLabel="true" /></div>
					</c:when>
					<c:otherwise>
					&nbsp;
					</c:otherwise>
					</c:choose>                   
                </th>
                <td align=left valign=middle class="datacell">                   
                	<c:choose>
                	<c:when test="${KualiForm.fullDocumentEntryCompleted eq false}">                
                	<kul:htmlControlAttribute attributeEntry="${documentAttributes.creditMemoAmount}" property="document.creditMemoAmount" readOnly="true" />
					</c:when>
					<c:otherwise>
					&nbsp;
					</c:otherwise>
					</c:choose>                	                   
                </td>
             <tr>   
             
             <tr>   
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderEndDate}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute  attributeEntry="${documentAttributes.purchaseOrderEndDate}" property="document.purchaseOrder.purchaseOrderEndDate" readOnly="true" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.purchaseOrderIdentifier}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.purchaseOrderIdentifier}" property="document.purchaseOrderIdentifier" readOnly="true" />
                </td>
             </tr>
             
             <tr>   
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel  attributeEntry="${documentAttributes.purchaseOrderNotes}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <bean:write name="KualiForm" property="document.purchaseOrderNotes" />
                </td>
                <th align=right valign=middle class="bord-l-b">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.paymentRequestIdentifier}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                   <kul:htmlControlAttribute attributeEntry="${documentAttributes.paymentRequestIdentifier}" property="document.paymentRequestIdentifier" readOnly="true" />
                </td>
            </tr>
			<tr>
                <th align=right valign=middle class="bord-l-b">
                    <div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.extractedDate}" /></div>
                </th>
                <td align=left valign=middle class="datacell">
                    <kul:htmlControlAttribute attributeEntry="${documentAttributes.extractedDate}" property="document.extractedDate" readOnly="${true}" />
                    <c:if test="${not empty KualiForm.document.extractedDate}">
	        			   <c:url var="page" value="/pdp/epicpaymentdetail.do">
        			     <c:param name="sourceDocNbr" value="${KualiForm.document.documentNumber}"/>
        			     <c:param name="docTypeCode" value="PREQ"/>
        			   </c:url>
        			   <c:url var="image" value="/pdp/images/tinybutton-disbursinfo.gif"/>
					   &nbsp;<a href="${page}" target="_pdp"><img src="${image}" border="0"/></a>
					</c:if>
                </td>
                <th align=right valign=middle class="bord-l-b">&nbsp;</th>
                <td align=left valign=middle class="datacell">&nbsp;</td>                
            </tr>

		</table> 
    </div>
    
</kul:tab>
