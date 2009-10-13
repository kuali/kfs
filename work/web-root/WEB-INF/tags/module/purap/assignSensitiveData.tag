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
              description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="itemAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for item's fields." %>
<%@ attribute name="poSensitiveDataAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for purchase order sensitive data's fields." %>
<%@ attribute name="sensitiveDataAssignAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for sensitive data assignment's fields." %>
              
<c:set var="lastSensitiveDataAssignment" value="${KualiForm.lastSensitiveDataAssignment}" />
              
<kul:tabTop tabTitle="Assign Sensitive Data to Purchase Order" defaultOpen="true" tabErrorKey="${PurapConstants.ASSIGN_SENSITIVE_DATA_TAB_ERRORS}">
	
    <div class="tab-container" align=center>
    	<h3>General Information</h3>

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Sensitive Data Assignment General Information">
        	<tr>
        		<th align=right valign=middle class="bord-l-b" width="25%">
            		<div align="right">Reason for Assignment:</div>
            	</th>
            	<td align=left valign=middle class="datacell" width="25%">
                	<kul:htmlControlAttribute attributeEntry="${sensitiveDataAssignAttributes.sensitiveDataAssignmentReasonText}" property="sensitiveDataAssignmentReason" />
            	</td>
        		<th align=right valign=middle class="bord-l-b" width="25%">
            		<div align="right">Last Updated by Person:</div>
            	</th>
            	<td align=left valign=middle class="datacell" width="25%">&nbsp;
	            	<c:if test="${lastSensitiveDataAssignment != null}">
                	<kul:htmlControlAttribute attributeEntry="${sensitiveDataAssignAttributes.sensitiveDataAssignmentPersonIdentifier}" property="lastSensitiveDataAssignment.sensitiveDataAssignmentPersonIdentifier" readOnly="true" />
            		</c:if>
            	</td>
            </tr>
            <tr>
        		<th align=right valign=middle class="bord-l-b" width="25%">
            		<div align="right">Reason for Last Update:</div>
            	</th>
            	<td align=left valign=middle class="datacell" width="25%">&nbsp;
	            	<c:if test="${lastSensitiveDataAssignment != null}">
                	<kul:htmlControlAttribute attributeEntry="${sensitiveDataAssignAttributes.sensitiveDataAssignmentReasonText}" property="lastSensitiveDataAssignment.sensitiveDataAssignmentReasonText" readOnly="true" />
            		</c:if>
            	</td>
        		<th align=right valign=middle class="bord-l-b" width="25%">
            		<div align="right">Last Updated on Date:</div>
            	</th>
            	<td align=left valign=middle class="datacell" width="25%">&nbsp;
	            	<c:if test="${lastSensitiveDataAssignment != null}">
                	<kul:htmlControlAttribute attributeEntry="${sensitiveDataAssignAttributes.sensitiveDataAssignmentChangeDate}" property="lastSensitiveDataAssignment.sensitiveDataAssignmentChangeDate" readOnly="true" />
            		</c:if>
            	</td>
            <tr>
            
        </table>
    
    	<h3>Vendor Information</h3>

        <table cellpadding="0" cellspacing="0" class="datatable" summary="Purchase Order Vendor Information">
        	<tr>
        		<th align=right valign=middle class="bord-l-b" width="50%">
            		<div align="right"><kul:htmlAttributeLabel attributeEntry="${documentAttributes.vendorName}" /></div>
            	</th>
            	<td align=left valign=middle class="datacell" width="50%">
                	<kul:htmlControlAttribute attributeEntry="${documentAttributes.vendorName}" property="document.vendorName" readOnly="true" />
            	</td>
            </tr>
        </table>

        <h3>Items Information</h3>
        
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Purchase Order Items Information">             
            <tr>
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemLineNumber}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemTypeCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemQuantity}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemCatalogNumber}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.purchasingCommodityCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemDescription}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemUnitPrice}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.extendedPrice}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${itemAttributes.itemAssignedToTradeInIndicator}" />
            </tr>
            <logic:iterate indexId="ctr" name="KualiForm" property="document.items" id="itemLine">
	        	<c:if test="${itemLine.itemType.lineItemIndicator == true}">
                	<tr>		
                    	<td class="datacell"><div align="center"><b>&nbsp;<bean:write name="KualiForm" property="document.item[${ctr}].itemLineNumber"/>&nbsp;</b></div></td>
                        <td class="datacell"><div align="center"><kul:htmlControlAttribute readOnly="true" attributeEntry="${itemAttributes.itemTypeCode}" property="document.item[${ctr}].itemTypeCode"/></div></td>
                        <td class="datacell"><div align="center"><kul:htmlControlAttribute readOnly="true" attributeEntry="${itemAttributes.itemQuantity}" property="document.item[${ctr}].itemQuantity" /></div></td>			
 				        <td class="datacell"><div align="center"><kul:htmlControlAttribute readOnly="true" attributeEntry="${itemAttributes.itemUnitOfMeasureCode}" property="document.item[${ctr}].itemUnitOfMeasureCode" /></div></td>
 				        <td class="datacell"><div align="center"><kul:htmlControlAttribute readOnly="true" attributeEntry="${itemAttributes.itemCatalogNumber}" property="document.item[${ctr}].itemCatalogNumber" /></div></td>
 				        <td class="datacell"><div align="center"><kul:htmlControlAttribute readOnly="true" attributeEntry="${itemAttributes.purchasingCommodityCode}" property="document.item[${ctr}].purchasingCommodityCode" /></div></td>
 				        <td class="datacell"><div align="left"><kul:htmlControlAttribute readOnly="true" attributeEntry="${itemAttributes.itemDescription}" property="document.item[${ctr}].itemDescription" /></div></td>
 				        <td class="datacell"><div align="right"><kul:htmlControlAttribute readOnly="true" attributeEntry="${itemAttributes.itemUnitPrice}" property="document.item[${ctr}].itemUnitPrice" /></div></td>
 				        <td class="datacell"><div align="right"><kul:htmlControlAttribute readOnly="true" attributeEntry="${itemAttributes.extendedPrice}" property="document.item[${ctr}].extendedPrice" /></div></td>
  				        <td class="datacell"><div align="center"><kul:htmlControlAttribute readOnly="true" attributeEntry="${itemAttributes.itemAssignedToTradeInIndicator}" property="document.item[${ctr}].itemAssignedToTradeInIndicator" /></div></td>
                    </tr>
                </c:if>
            </logic:iterate>
        </table>
        
        <h3>New Sensitive Data Entry To Be Assigned</h3>
        
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Assign New Sensitive Data Entry">             
            <tr>
				<kul:htmlAttributeHeaderCell attributeEntry="${poSensitiveDataAttributes.sensitiveDataCode}" />
				<kul:htmlAttributeHeaderCell literalLabel="Actions"/>
        	</tr>
        	<tr>
        		<td class="datacell"><div align="center">
        			<kul:htmlControlAttribute attributeEntry="${poSensitiveDataAttributes.sensitiveDataCode}" 
        				property="newSensitiveDataLine.sensitiveDataCode" />
        		</div></td>
        		<td class="datacell"><div align="center">
        			<html:image property="methodToCall.addSensitiveData" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" 
        				alt="Add New Sensitive Data Entry" title="Add New Sensitive Data Entry" styleClass="tinybutton" />
        		</div></td>
        	</tr>
        </table>
        
        <h3>Current Sensitive Data Entries Assigned</h3>
        
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Delete/Update Current Sensitive Data Entries">             
            <tr>
				<kul:htmlAttributeHeaderCell attributeEntry="${poSensitiveDataAttributes.sensitiveDataCode}" />
				<kul:htmlAttributeHeaderCell literalLabel="Actions"/>
        	</tr>
            <logic:iterate indexId="ctr" name="KualiForm" property="sensitiveDatasAssigned" id="sd">
        	<tr>        	
        		<td class="datacell"><div align="center">
        			<kul:htmlControlAttribute attributeEntry="${poSensitiveDataAttributes.sensitiveDataCode}" 
        				property="sensitiveDatasAssigned[${ctr}].sensitiveDataCode" />
        		</div></td>
        		<td class="datacell"><div align="center">
        			<html:image property="methodToCall.deleteSensitiveData.line${ctr}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif"
						alt="Delete Sensitive Data Entry ${ctr+1}" title="Delete Sensitive Data Entry ${ctr+1}" styleClass="tinybutton" />
        		</div></td>
        	</tr>
        	</logic:iterate>
        </table>

    </div>    
</kul:tabTop>
        
                
             
