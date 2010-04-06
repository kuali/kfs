<%--
 Copyright 2007-2009 The Kuali Foundation
 
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

<%@ attribute name="editingMode" required="true" description="used to decide if items may be edited" type="java.util.Map"%>
<%@ attribute name="isSource" required="false" %>
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:tab tabTitle="Transaction Lines" defaultOpen="false" tabErrorKey="${KFSConstants.ITEM_LINE_ERRORS}">

<c:when>
<c:choose test="${isSource}" >
  <c:set var="lineAttributes" value="${DataDictionary.EndowmentSourceTransactionLine.attributes}" />
</c:choose>
<c:otherwise>
  <c:set var="lineAttributes" value="${DataDictionary.EndowmentTargetTransactionLine.attributes}" />
</c:otherwise>


 <div class="tab-container" align=center>
	<h3>Items</h3>
	<table cellpadding="0" cellspacing="0" class="datatable" summary="Items section">
	    
		<tr>
            <kul:htmlAttributeHeaderCell literalLabel="&nbsp;"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.kemid}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.itemStockNumber}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.itemStockDescription}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.itemQuantity}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.unitOfMeasureCode}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.itemUnitAmount}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${lineAttributes.total}"/>
            <c:if test="${not readOnly}">
                <kul:htmlAttributeHeaderCell literalLabel="Actions"/>
            </c:if>
		</tr>
      
        <c:if test="${not readOnly}">
            <tr>
                <kul:htmlAttributeHeaderCell literalLabel="add:" scope="row"/>
                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${lineAttributes.kemid}" property="newSourceTransactionLine.kemid" /></td>
                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${lineAttributes.itemStockDescription}" property="newItem.itemStockDescription" /></td>
                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${lineAttributes.itemQuantity}" property="newItem.itemQuantity" /></td>
                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${lineAttributes.unitOfMeasureCode}" property="newItem.unitOfMeasureCode" /></td>
                <td class="infoline"><kul:htmlControlAttribute attributeEntry="${lineAttributes.itemUnitAmount}" property="newItem.itemUnitAmount" styleClass="amount" /></td>
                <td class="infoline"><!-- no total until it's added --></td>
                <td class="infoline"><div align="center"><html:image property="methodToCall.insertItem" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Insert an Item" title="Insert an Item" styleClass="tinybutton"/></div></td>
            </tr>
        </c:if>
    
    <c:set var="transLines" value="$document.sourceTransactionLines"/>  
    <c:if test="${!isSource}">
       <c:set var="transLines" value="$document.targetTransactionLines"/>
    </c:if>
      
        <logic:iterate id="item" name="KualiForm" property="transLines" indexId="ctr">
            <tr>
                <kul:htmlAttributeHeaderCell literalLabel="${ctr+1}:" scope="row">
                    <%-- Outside this th, these hidden fields would be invalid HTML. --%>
                    <html:hidden property="document.item[${ctr}].itemSequenceId" />
                    <html:hidden property="document.item[${ctr}].versionNumber" />
                </kul:htmlAttributeHeaderCell>
  
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.itemStockNumber}" property="${transLines[ctr]]}.kemid" readOnly="${readOnly}"/></td>
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.itemStockDescription}" property="document.item[${ctr}].itemStockDescription" readOnly="${readOnly}"/></td>
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.itemQuantity}" property="document.item[${ctr}].itemQuantity" readOnly="${readOnly}"/></td>
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.unitOfMeasureCode}" property="document.item[${ctr}].unitOfMeasureCode" readOnly="${readOnly}"/></td>
                <td class="datacell"><kul:htmlControlAttribute attributeEntry="${lineAttributes.itemUnitAmount}" property="document.item[${ctr}].itemUnitAmount" readOnly="${readOnly}" styleClass="amount"/></td>
                <td class="datacell">$${KualiForm.document.items[ctr].total}</td> <%-- EL doesn't quash items' plural like Struts does. --%>
                <c:if test="${not readOnly}">
                    <td class="datacell"><div align="center"><html:image property="methodToCall.deleteItem.line${ctr}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" title="Delete Item ${ctr+1}" alt="Delete Item ${ctr+1}" styleClass="tinybutton"/></div></td>
                </c:if>
            </tr>
        </logic:iterate>
        
		<tr>
	 		<td class="total-line" colspan="6">&nbsp;</td>
	 	    <td class="total-line" ><strong>Total: $${KualiForm.document.totalIncomeAmount}</strong></td>
	  		<td class="total-line" ><strong>Total: $${KualiForm.document.totalPrincipalAmount}</strong></td>
            <c:if test="${not readOnly}">
                <td class="total-line">&nbsp;</td>
            </c:if>
		</tr>
		
	    <tr>
	 		<td class="total-line" colspan="6">&nbsp;</td>
	 	    <td class="total-line" ><strong>Total: $${KualiForm.document.itemTotal}</strong></td>
	  		<td class="total-line" ><strong>Total: $${KualiForm.document.itemTotal}</strong></td>
            <c:if test="${not readOnly}">
                <td class="total-line">&nbsp;</td>
            </c:if>
		</tr>
	</table>
</div>