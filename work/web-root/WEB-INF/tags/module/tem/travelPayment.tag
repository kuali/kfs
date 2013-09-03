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
<%@ attribute name="isForAdvance" required="false" type="java.lang.Boolean" description="If true, this payment information is to pay a travel advance; as such, it will change attributes and the property of the travel payment."%>

<c:set var="paymentPropertyName" value="travelPayment"/>
<c:set var="tabErrorKey" value="${TemConstants.TRAVEL_PAYMENT_TAB_ERRORS},${TemConstants.TRVL_SPECHAND_TAB_ERRORS},${KFSConstants.WIRETRANSFER_TAB_ERRORS},${KFSConstants.FOREIGNDRAFTS_TAB_ERRORS}"/>
<c:set var="travelPaymentAttributes" value="${DataDictionary.TravelPayment.attributes}" />
<c:set var="travelPaymentLabel" value="${DataDictionary.TravelPayment.objectLabel}"/>
<c:if test="${!empty isForAdvance and isForAdvance}">
	<c:set var="paymentPropertyName" value="advanceTravelPayment"/>
	<c:set var="tabErrorKey" value="${TemConstants.ADVANCE_TRAVEL_PAYMENT_TAB_ERRORS},${TemConstants.ADVANCE_TRVL_SPECHAND_TAB_ERRORS},${KFSConstants.WIRETRANSFER_TAB_ERRORS},${KFSConstants.FOREIGNDRAFTS_TAB_ERRORS}"/>
	<c:set var="travelPaymentAttributes" value="${DataDictionary.AdvanceTravelPayment.attributes}"/>
	<c:set var="travelPaymentLabel" value="${DataDictionary.AdvanceTravelPayment.objectLabel}"/>
</c:if>

<c:set var="checkAmountEntry" value="${KualiForm.editingMode['checkAmountEntry']}" />

<kul:tab tabTitle="${travelPaymentLabel}" defaultOpen="${KualiForm.defaultOpenPaymentInfoTab}" tabErrorKey="${tabErrorKey}">
    <div class="tab-container" align=center > 
        <h3>Payment Information</h3>
		<table cellpadding=0 class="datatable" summary="Payment Section">
            <tr>
              <th width="20%"  class="bord-l-b">
              	<div align="right"><kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.checkTotalAmount}"/></div>
              </th>
              <td width="30%"  class="datacell">
                <kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.checkTotalAmount}" property="document.${paymentPropertyName}.checkTotalAmount" readOnly="${(!fullEntry&&!checkAmountEntry) || advancePaymentMode}"/>
              </td>
              <th width="20%"  class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.dueDate}"/></div></th>
              <td width="30%"  class="datacell">
                 <kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.dueDate}" property="document.${paymentPropertyName}.dueDate" datePicker="true" readOnly="${!fullEntryMode || advancePaymentMode}"/>
              </td>
            </tr>
            
            <tr>
              <th  class="bord-l-b"><div align="right">Payment Type:</div></th>
              <td valign="top"  class="datacell">
                <c:if test="${taxEntryMode}">
                  <kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.alienPaymentCode}" property="document.${paymentPropertyName}.alienPaymentCode"/>
                  <kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.alienPaymentCode}" noColon="true" />
                  <br><br>
                </c:if>
                <c:if test="${!taxEntryMode}">
                    <kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.alienPaymentCode}"/>
                    <kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.alienPaymentCode}" property="document.${paymentPropertyName}.alienPaymentCode" readOnly="true"/>
                    <br><br>
                </c:if>
				<%--
                <kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.payeeEmployeeCode}"/> <bean:write  name="KualiForm" property="document.${paymentPropertyName}.payeeEmployeeCode" /><br><br>
				<c:if test="${KualiForm.document[paymentPropertyName].payeeTypeCode=='V'}">
                	<kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.employeePaidOutsidePayrollCode}"/><bean:write  name="KualiForm" property="document.${paymentPropertyName}.employeePaidOutsidePayrollCode" /><br><br>
                </c:if>
				--%>
              </td>  
              <th width="20%"  class="bord-l-b"><div align="right">Other Considerations: </div></th>
              <td width="30%"  class="datacell">
				<c:choose>
					<c:when test="${fullEntryMode}"> 
						<kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.attachmentCode}" property="document.${paymentPropertyName}.attachmentCode" readOnly="false"/>
						<kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.attachmentCode}" noColon="true" /><br>
					</c:when>
					<c:otherwise> 
						<kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.attachmentCode}"/>
						<kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.attachmentCode}" property="document.${paymentPropertyName}.attachmentCode" readOnly="true"/><br>
					</c:otherwise>
				</c:choose>
         
				<c:choose>
                 <c:when test="${fullEntryMode || specialHandlingChangingEntryMode}">        
                   <kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.specialHandlingCode}" property="document.${paymentPropertyName}.specialHandlingCode" onclick="specialHandlingMessage(this);" readOnly="false"/>
                   <kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.specialHandlingCode}" noColon="true" /><br>
                 </c:when>
                 <c:otherwise>
                   <kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.specialHandlingCode}"/>
                   <kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.specialHandlingCode}" property="document.${paymentPropertyName}.specialHandlingCode" readOnly="true"/><br>          
                 </c:otherwise>
				</c:choose>
                 
                 <c:set var="w9IndReadOnly" value="${!fullEntryMode}"/>
                 <%-- cannot change w9 indicator if it has previousely been checked --%>
                 <c:if test="${KualiForm.document[paymentPropertyName].editW9W8BENbox==true}">  
                     <c:set var="w9IndReadOnly" value="true"/>    
                 </c:if>
                 <c:choose>
					<c:when test="${w9IndReadOnly}">    
						<kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.payeeW9CompleteCode}" property="document.${paymentPropertyName}.payeeW9CompleteCode" disabled="true"/>
						<kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.payeeW9CompleteCode}" noColon="true" /><br>                     
					</c:when>
					<c:otherwise>                
						<kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.payeeW9CompleteCode}" property="document.${paymentPropertyName}.payeeW9CompleteCode"/>
						<kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.payeeW9CompleteCode}" noColon="true"/><br>
					</c:otherwise>
				</c:choose>
                 
                 
				<c:choose>
                 <c:when test="${fullEntryMode}">        
                   <kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.exceptionAttachedIndicator}" property="document.${paymentPropertyName}.exceptionAttachedIndicator" onclick="exceptionMessage(this);" readOnly="false"/>
                   <kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.exceptionAttachedIndicator}" noColon="true" /><br>
                 </c:when>
                 <c:otherwise>
                   <kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.exceptionAttachedIndicator}"/>
                   <kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.exceptionAttachedIndicator}" property="document.${paymentPropertyName}.exceptionAttachedIndicator" readOnly="true"/><br>          
                 </c:otherwise>
				</c:choose>     

				<c:choose>
                 <c:when test="${immediateDisbursementEntryMode}">        
                   <kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.immediatePaymentIndicator}" property="document.${paymentPropertyName}.immediatePaymentIndicator" readOnly="false"/>
                   <kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.immediatePaymentIndicator}" noColon="true" /><br>
                 </c:when>
                 <c:otherwise>
                   <kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.immediatePaymentIndicator}"/>
                   <kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.immediatePaymentIndicator}" property="document.${paymentPropertyName}.immediatePaymentIndicator" readOnly="true"/><br>          
                 </c:otherwise>
				</c:choose>
                 </td>
            </tr>
            
            <tr>
              <th  class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.paymentMethodCode}"/></div></th>
              <td  class="datacell">
                <kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.paymentMethodCode}" property="document.${paymentPropertyName}.paymentMethodCode" extraReadOnlyProperty="document.${paymentPropertyName}.paymentMethodName" onchange="paymentMethodMessages(this.value);" readOnly="${!fullEntryMode && !frnEntryMode}"/>
              </td>
              <th  class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.documentationLocationCode}"/></div></th>
              <td  class="datacell">
                <kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.documentationLocationCode}" property="document.${paymentPropertyName}.documentationLocationCode" extraReadOnlyProperty="document.${paymentPropertyName}.paymentDocumentationLocationName" onchange="documentationMessage(this.value);" readOnly="true"/>
              </td>
            </tr>
            <tr>
              <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.checkStubText}"/></div></th>
              <td colspan="3"><kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.checkStubText}" property="document.${paymentPropertyName}.checkStubText" readOnly="true"/></td>
            </tr>
        </table>
		<%-- SPECIAL HANDLING --%>
		<h3>Send Check To</h3>
		<table cellpadding=0 class="datatable" summary="Special Handling Section">          
            <tr>
              <th align="right" valign="middle" class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.specialHandlingCityName}"/></div></th>
              <td align="left" valign="middle" class="datacell" colspan="3">
                <kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.specialHandlingCityName}" property="document.${paymentPropertyName}.specialHandlingCityName" readOnly="${!(fullEntryMode || specialHandlingChangingEntryMode) }"/>  
              </td>
            </tr>
            
            <tr>
              <th align="right" valign="middle" class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.specialHandlingLine1Addr}"/></div></th>
              <td align="left" valign="middle" class="datacell">
                <kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.specialHandlingLine1Addr}" property="document.${paymentPropertyName}.specialHandlingLine1Addr" readOnly="${!(fullEntryMode || specialHandlingChangingEntryMode)}"/>  
              </td>
              <th align="right" valign="middle" class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.specialHandlingStateCode}"/></div></th>
              <td align="left" valign="middle" class="datacell">
                <kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.specialHandlingStateCode}" property="document.${paymentPropertyName}.specialHandlingStateCode" readOnly="${!(fullEntryMode || specialHandlingChangingEntryMode) }"/>  
              </td>
            </tr>
            
            <tr>
              <th align="right" valign="middle" class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.specialHandlingLine2Addr}"/></div></th>
              <td align="left" valign="middle" class="datacell">
                <kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.specialHandlingLine2Addr}" property="document.${paymentPropertyName}.specialHandlingLine2Addr" readOnly="${!(fullEntryMode || specialHandlingChangingEntryMode) }"/>  
              </td>
              <th align="right" valign="middle" class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.specialHandlingZipCode}"/></div></th>
              <td align="left" valign="middle" class="datacell">
                <kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.specialHandlingZipCode}" property="document.${paymentPropertyName}.specialHandlingZipCode" readOnly="${!(fullEntryMode || specialHandlingChangingEntryMode) }"/>  
              </td>
            </tr>
            
            <tr>
              <th align="right" valign="middle" class="bord-l-b"></th>
              <td align="left" valign="middle" class="datacell">
              </td>
              <th align="right" valign="middle" class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.specialHandlingCountryCode}"/></div></th>
              <td align="left" valign="middle" class="datacell">
                <kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.specialHandlingCountryCode}" property="document.${paymentPropertyName}.specialHandlingCountryCode" readOnly="${!(fullEntryMode || specialHandlingChangingEntryMode) }"/>  
              </td>
            </tr>
     </table>
	 <%-- WIRE TRANSFER --%>
	 <sys:innerWireTransfer/>
	 <%-- FOREIGN DRAFT --%>
	 <sys:innerForeignDraft/>
     </div>
</kul:tab>
