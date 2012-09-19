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
<c:set var="requisitionAttributes" value="${DataDictionary.RequisitionDocument.attributes}" />
<c:set var="requisitionItemAttributes" value="${DataDictionary.RequisitionItem.attributes}" />
<c:set var="sourceAccountingLineAttributes" value="${DataDictionary.SourceAccountingLine.attributes}" />
<c:set var="purchaseOrderAttributes" value="${DataDictionary.PurchaseOrderDocument.attributes}" />
<c:set var="ContractManagerAssignmentAttributes" value="${DataDictionary.ContractManagerAssignmentDocument.attributes}" />
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:tab tabTitle="Assign A Contract Manager" defaultOpen="true" tabErrorKey="${PurapConstants.ASSIGN_CONTRACT_MANAGER_TAB_ERRORS}">

    <div class="tab-container" align=center>
            <h3>Assign A Contract Manager</h3>

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Assign A Contract Manager">
			<c:if test="${empty KualiForm.document.contractManagerAssignmentDetails}">
		        <th align=right valign=middle class="bord-l-b">
		            <div align="center"><br>There are no unassigned requisitions.<br></div>
		        </th>
			</c:if>
			<c:if test="${!empty KualiForm.document.contractManagerAssignmentDetails}">
	            <tr>
	                     <kul:htmlAttributeHeaderCell attributeEntry="${purchaseOrderAttributes.contractManagerCode}" forceRequired="true"/>
 
	                 	  <kul:htmlAttributeHeaderCell attributeEntry="${ContractManagerAssignmentAttributes.requisitionNumber}" />

	                    <kul:htmlAttributeHeaderCell attributeEntry="${ContractManagerAssignmentAttributes.deliveryCampusCode}" />
	
	                    <kul:htmlAttributeHeaderCell attributeEntry="${ContractManagerAssignmentAttributes.vendorName}" />
	                    
	                    <kul:htmlAttributeHeaderCell attributeEntry="${ContractManagerAssignmentAttributes.generalDescription}" />
	                    
	                    <kul:htmlAttributeHeaderCell attributeEntry="${ContractManagerAssignmentAttributes.requisitionTotalAmount}" />
	                    
	                    <kul:htmlAttributeHeaderCell attributeEntry="${ContractManagerAssignmentAttributes.requisitionCreateDate}" />
	                    
	                    <kul:htmlAttributeHeaderCell attributeEntry="${ContractManagerAssignmentAttributes.firstItemDescription}" />

                      <kul:htmlAttributeHeaderCell attributeEntry="${ContractManagerAssignmentAttributes.firstItemCommodityCode}" />
                        
	                    <kul:htmlAttributeHeaderCell attributeEntry="${ContractManagerAssignmentAttributes.firstObjectCode}" /> 
	                	
	                    <kul:htmlAttributeHeaderCell attributeEntry="${ContractManagerAssignmentAttributes.universityFiscalYear}" /> 
	            </tr>
	
		        <logic:iterate id="acmDetail" name="KualiForm" property="document.contractManagerAssignmentDetails" indexId="ctr">
		            <tr>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute property="document.contractManagerAssignmentDetail[${ctr}].contractManagerCode" attributeEntry="${purchaseOrderAttributes.contractManagerCode}" readOnly="${readOnly}" />
					        <c:if test="${!readOnly}" >
		                        <kul:lookup boClassName="org.kuali.kfs.vnd.businessobject.ContractManager" fieldConversions="contractManagerCode:document.contractManagerAssignmentDetail[${ctr}].contractManagerCode" /></div>
		                        <kul:checkErrors keyMatch="document.contractManagerAssignmentDetails[${ctr}].contractManagerCode" />
								<c:if test="${hasErrors}">
									 <kul:fieldShowErrorIcon />
								</c:if>	
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
		                    <kul:htmlControlAttribute property="document.contractManagerAssignmentDetail[${ctr}].requisition.deliveryCampusCode" attributeEntry="${requisitionAttributes.deliveryCampusCode}" readOnly="true" />
		                </td>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute property="document.contractManagerAssignmentDetail[${ctr}].requisition.vendorName" attributeEntry="${requisitionAttributes.vendorName}" readOnly="true" />
		                </td>
		                <td align=left valign=middle class="datacell">
		                    <kul:htmlControlAttribute property="document.contractManagerAssignmentDetail[${ctr}].requisition.documentHeader.documentDescription" attributeEntry="${requisitionAttributes['documentHeader.documentDescription']}" readOnly="true" />
		                </td>
		                <td align=right valign=middle class="datacell">
		                    <div align="right"><kul:htmlControlAttribute property="document.contractManagerAssignmentDetail[${ctr}].requisition.documentHeader.financialDocumentTotalAmount" attributeEntry="${requisitionAttributes['financialSystemDocumentHeader.financialDocumentTotalAmount']}" readOnly="true" /></div>
		                </td>		               
		                <td align=left valign=middle class="datacell">
		                    <%-- fmt:formatDate value="${acmDetail.requisition.documentHeader.workflowDocument.createDate}" pattern="hh:mm a MM/dd/yyyy" / --%>
						    <c:out value="${acmDetail.createDate}" />
						</td>						
		                <td align=left valign=middle class="datacell">		   
		                    <kul:htmlControlAttribute property="document.contractManagerAssignmentDetail[${ctr}].requisition.items[0].itemDescription" attributeEntry="${requisitionItemAttributes.itemDescription}" readOnly="true" />  
		                </td>
		                <td align=left valign=middle class="datacell">            
       
                        <kul:htmlControlAttribute property="document.contractManagerAssignmentDetail[${ctr}].requisition.items[0].purchasingCommodityCode" attributeEntry="${requisitionItemAttributes.purchasingCommodityCode}" readOnly="true" />
   
                        <c:if test="${! empty KualiForm.document.contractManagerAssignmentDetails[ctr].requisition.items[0].commodityCode.commodityDescription}">
                    	  	&nbsp;-&nbsp;
                    			<kul:htmlControlAttribute property="document.contractManagerAssignmentDetail[${ctr}].requisition.items[0].commodityCode.commodityDescription" attributeEntry="${requisitionItemAttributes.purchasingCommodityCode}" readOnly="true" />
                        </c:if>
                    </td>                       
		                <td align=left valign=middle class="datacell">		                    
		          				<c:choose>
												<c:when test="${!empty acmDetail.requisition.items[0].sourceAccountingLines}">

		                    		<kul:htmlControlAttribute property="document.contractManagerAssignmentDetail[${ctr}].requisition.items[0].sourceAccountingLines[0].financialObjectCode" attributeEntry="${sourceAccountingLineAttributes.financialObjectCode}" readOnly="true" />		      
           	
												</c:when>
												<c:when test="${empty acmDetail.requisition.items[0].sourceAccountingLines}">
		                    		Note: This is bad data! If you are seeing this, you may have a requisition with no account for one item.
												</c:when>
											</c:choose>		                    
		                </td>
		                <td align=left valign=middle class="datacell">		   
		                		 <kul:htmlControlAttribute property="document.contractManagerAssignmentDetail[${ctr}].requisition.postingYear" attributeEntry="${requisitionAttributes.postingYear}" readOnly="true" />            
										</td>
		                
		            </tr>
		        </logic:iterate>
			</c:if>
        </table>
    </div>
</kul:tab>
