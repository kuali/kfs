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

<c:set var="requisitionAttributes" value="${DataDictionary.RequisitionDocument.attributes}" />
<c:set var="purchaseOrderAttributes" value="${DataDictionary.PurchaseOrderDocument.attributes}" />
<c:set var="assignContractManagerAttributes" value="${DataDictionary.AssignContractManagerDocument.attributes}" />
<c:set var="readOnly" value="${empty KualiForm.editingMode['fullEntry']}" />

<kul:tab tabTitle="Assign A Contract Manager" defaultOpen="true" tabErrorKey="${PurapConstants.ASSIGN_CONTRACT_MANAGER_TAB_ERRORS}">

    <div class="tab-container" align=center>
        <div class="h2-container">
            <h2>Assign A Contract Manager</h2>
        </div>

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Assign A Contract Manager">
			<c:if test="${empty KualiForm.document.assignContractManagerDetails}">
		        <th align=right valign=middle class="bord-l-b">
		            <div align="center"><br>There are no unassigned requisitions.<br></div>
		        </th>
			</c:if>
			<c:if test="${!empty KualiForm.document.assignContractManagerDetails}">
	            <tr>
	                
	                    <kul:htmlAttributeHeaderCell attributeEntry="${purchaseOrderAttributes.contractManagerCode}" />
 
	                 	<kul:htmlAttributeHeaderCell attributeEntry="${assignContractManagerAttributes.requisitionNumber}" />

	                    <kul:htmlAttributeHeaderCell attributeEntry="${assignContractManagerAttributes.deliveryCampusCode}" />
	
	                    <kul:htmlAttributeHeaderCell attributeEntry="${assignContractManagerAttributes.vendorName}" />
	                    
	                    <kul:htmlAttributeHeaderCell attributeEntry="${assignContractManagerAttributes.generalDescription}" />
	                    
	                    <kul:htmlAttributeHeaderCell attributeEntry="${assignContractManagerAttributes.requisitionTotalAmount}" />
	                    
	                    <kul:htmlAttributeHeaderCell attributeEntry="${assignContractManagerAttributes.requisitionCreateDate}" />
	                    
	                    <kul:htmlAttributeHeaderCell attributeEntry="${assignContractManagerAttributes.firstItemDescription}" />

                        <kul:htmlAttributeHeaderCell attributeEntry="${assignContractManagerAttributes.firstItemCommodityCode}" />
                        
	                    <kul:htmlAttributeHeaderCell attributeEntry="${assignContractManagerAttributes.firstObjectCode}" /> 
	                	
	            </tr>
	
		        <logic:iterate id="acmDetail" name="KualiForm" property="document.assignContractManagerDetails" indexId="ctr">
		            <tr>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute property="document.assignContractManagerDetail[${ctr}].contractManagerCode" attributeEntry="${purchaseOrderAttributes.contractManagerCode}" readOnly="${readOnly}" />
					        <c:if test="${!readOnly}" >
		                        <kul:lookup boClassName="org.kuali.module.vendor.bo.ContractManager" fieldConversions="contractManagerCode:document.assignContractManagerDetail[${ctr}].contractManagerCode" /></div>
					        </c:if>
		                </td>
		                <td align=left valign=middle class="datacell">
		                <c:if test="${!readOnly}" >
		                    <a href="<c:out value="${acmDetail.requisition.url}" />"  target="_BLANK"><c:out value="${acmDetail.requisitionIdentifier}" /></a>
		                </c:if>
		                <c:if test="${readOnly}" >
		                    <c:out value="${acmDetail.requisitionIdentifier}" />
		                </c:if>
		                </td>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute property="document.assignContractManagerDetail[${ctr}].requisition.deliveryCampusCode" attributeEntry="${requisitionAttributes.deliveryCampusCode}" readOnly="true" />
		                </td>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute property="document.assignContractManagerDetail[${ctr}].requisition.vendorName" attributeEntry="${requisitionAttributes.vendorName}" readOnly="true" />
		                </td>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute property="document.assignContractManagerDetail[${ctr}].requisition.documentHeader.financialDocumentDescription" attributeEntry="${requisitionAttributes.documentHeader.financialDocumentDescription}" readOnly="true" />
		                </td>
		                <td align=right valign=middle class="datacell">
		                    <div align="right"><kul:htmlControlAttribute property="document.assignContractManagerDetail[${ctr}].requisition.documentHeader.financialDocumentTotalAmount" attributeEntry="${requisitionAttributes.documentHeader.financialDocumentTotalAmount}" readOnly="true" /></div>
		                </td>		               
		                <td align=left valign=middle class="datacell">
		                    <%-- fmt:formatDate value="${acmDetail.requisition.documentHeader.workflowDocument.createDate}" pattern="hh:mm a MM/dd/yyyy" / --%>
						    <c:out value="${acmDetail.createDate}" />
						</td>						
		                <td align=left valign=middle class="datacell">		                   
		                    <kul:htmlControlAttribute property="document.assignContractManagerDetail[${ctr}].requisition.items[0].itemDescription" attributeEntry="${requisitionAttributes.items[0].itemDescription}" readOnly="true" />
		                </td>
		                <td align=left valign=middle class="datacell">                         
                        <kul:htmlControlAttribute property="document.assignContractManagerDetail[${ctr}].requisition.items[0].purchasingCommodityCode" attributeEntry="${requisitionAttributes.items[0].purchasingCommodityCode}" readOnly="true" />
                        <c:if test="${! empty KualiForm.document.assignContractManagerDetails[ctr].requisition.items[0].commodityCode.commodityDescription}">
                    	  	&nbsp;-&nbsp;
                    			<kul:htmlControlAttribute property="document.assignContractManagerDetail[${ctr}].requisition.items[0].commodityCode.commodityDescription" attributeEntry="${requisitionAttributes.items[0].commodityCode.commodityDescription}" readOnly="true" />
                        </c:if>
                    </td>                       
		                <td align=left valign=middle class="datacell">		                    
		                    <c:choose>
								<c:when test="${!empty acmDetail.requisition.items[0].sourceAccountingLines}">
		                    		<kul:htmlControlAttribute property="document.assignContractManagerDetail[${ctr}].requisition.items[0].sourceAccountingLines[0].financialObjectCode" attributeEntry="${requisitionAttributes.items[0].sourceAccountingLines[0].financialObjectCode}" readOnly="true" />		                    	
								</c:when>
								<c:when test="${empty acmDetail.requisition.items[0].sourceAccountingLines}">
		                    		Note: This is bad data! If you are seeing this, you may have a requisition with no account for one item.
								</c:when>
							</c:choose>		                    
		                </td>
		                <html:hidden property="document.assignContractManagerDetail[${ctr}].requisitionIdentifier" />
		                <html:hidden property="document.assignContractManagerDetail[${ctr}].createDate" />
		            </tr>
		        </logic:iterate>
			</c:if>
        </table>
    </div>
</kul:tab>
