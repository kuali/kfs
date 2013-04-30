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

<c:set var="payeeAttributes" value="${DataDictionary.DisbursementVoucherPayeeDetail.attributes}" />
<c:set var="dvAttributes" value="${DataDictionary.DisbursementVoucherDocument.attributes}" />
<c:set var="payeeAttributes" value="${DataDictionary.DisbursementVoucherPayeeDetail.attributes}" />
<c:set var="paymentReasonEditMode" value="${(not empty KualiForm.editingMode['paymentReasonEditMode'])}" />

<kul:tab tabTitle="Payment Information" defaultOpen="true" tabErrorKey="${KFSConstants.DV_PAYMENT_TAB_ERRORS},document.disbVchrPaymentMethodCode,${KFSConstants.DV_PAYEE_TAB_ERRORS},document.dvPayeeDetail.disbursementVoucherPayeeTypeCode">
    <div class="tab-container" align=center > 
        <h3>Payment Information</h3>
		<table cellpadding=0 class="datatable" summary="Payment Section">			            
            <tr>
              <th class="bord-l-b"><div align="right">
              	<kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrPaymentReasonCode}"/>
              </div></th>
              <td colspan="3" class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPaymentReasonCode}" property="document.dvPayeeDetail.disbVchrPaymentReasonCode" extraReadOnlyProperty="document.dvPayeeDetail.disbVchrPaymentReasonName" readOnly="${!paymentReasonEditMode}"/>
              </td> 
            </tr>
            
            <tr>
              <th class="bord-l-b"><div align="right">
              	<kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrPayeeIdNumber}"/>           	
              </div></th>
              <td colspan="3" class="datacell">              	
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeeIdNumber}" property="document.dvPayeeDetail.disbVchrPayeeIdNumber" readOnly="true" />
                <c:if test="${fullEntryMode}">
	                <kul:lookup boClassName="org.kuali.kfs.fp.businessobject.DisbursementPayee"
	                	fieldConversions="payeeIdNumber:document.dvPayeeDetail.disbVchrPayeeIdNumber,payeeTypeCode:document.dvPayeeDetail.disbursementVoucherPayeeTypeCode,paymentReasonCode:document.dvPayeeDetail.disbVchrPaymentReasonCode"
	                	/>
                </c:if>
              </td>
            </tr>
		
            <tr>
              <th class="bord-l-b">
              	<div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbursementVoucherPayeeTypeName}"/></div>
              </th>
              <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbursementVoucherPayeeTypeName}" property="document.dvPayeeDetail.disbursementVoucherPayeeTypeName" readOnly="true"/>   
              </td>
              
              <th class="bord-l-b">
              	<div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrPayeePersonName}"/></div>
              </th>
              <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeePersonName}" property="document.dvPayeeDetail.disbVchrPayeePersonName" readOnly="true"/>  
              </td>              
            </tr>
            
            <tr>
              <th class="bord-l-b">
              	<div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrPayeeLine1Addr}"/></div>
              </th>
              <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeeLine1Addr}" property="document.dvPayeeDetail.disbVchrPayeeLine1Addr" readOnly="${!fullEntryMode && !payeeEntryMode}"/>  
              </td>
              
              <th class="bord-l-b">
              	<div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrPayeeLine2Addr}"/></div>
              </th>
              <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeeLine2Addr}" property="document.dvPayeeDetail.disbVchrPayeeLine2Addr" readOnly="${!fullEntryMode && !payeeEntryMode}"/>  
              </td>
            </tr>
            
            <tr>
			  <th class="bord-l-b">
			  	<div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrPayeeCityName}"/></div>
              </th>
              <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeeCityName}" property="document.dvPayeeDetail.disbVchrPayeeCityName" readOnly="${!fullEntryMode && !payeeEntryMode}"/>
              </td> 
                           
			  <th class="bord-l-b">
			  	<div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrPayeeStateCode}"/></div>
              </th>
              <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeeStateCode}" property="document.dvPayeeDetail.disbVchrPayeeStateCode" readOnly="${!fullEntryMode && !payeeEntryMode}"/>
                <c:if test="${fullEntryMode || payeeEntryMode}">
              		<kul:lookup boClassName="org.kuali.rice.location.framework.state.StateEbo" fieldConversions="countryCode:document.dvPayeeDetail.disbVchrPayeeCountryCode,code:document.dvPayeeDetail.disbVchrPayeeStateCode" 
              		lookupParameters="document.dvPayeeDetail.disbVchrPayeeCountryCode:countryCode" />
              	</c:if>
              </td>
            </tr>            
            
            <tr>
              <th class="bord-l-b">
              	<div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrPayeeCountryCode}"/></div>
              </th>
              <td class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeeCountryCode}" property="document.dvPayeeDetail.disbVchrPayeeCountryCode" readOnly="${!fullEntryMode && !payeeEntryMode}"/>  
              </td>
                          
              <th class="bord-l-b">
              	<div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrPayeeZipCode}"/></div>            	
              </th>
              <td class="datacell">	 
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeeZipCode}" property="document.dvPayeeDetail.disbVchrPayeeZipCode" readOnly="${!fullEntryMode && !payeeEntryMode}"/>
                <c:if test="${fullEntryMode || payeeEntryMode}">
              		<kul:lookup boClassName="org.kuali.rice.location.framework.postalcode.PostalCodeEbo" fieldConversions="code:document.dvPayeeDetail.disbVchrPayeeZipCode,countryCode:document.dvPayeeDetail.disbVchrPayeeCountryCode,stateCode:document.dvPayeeDetail.disbVchrPayeeStateCode,cityName:document.dvPayeeDetail.disbVchrPayeeCityName" 
              		lookupParameters="document.dvPayeeDetail.disbVchrPayeeCountryCode:countryCode,document.dvPayeeDetail.disbVchrPayeeZipCode:code,document.dvPayeeDetail.disbVchrPayeeStateCode:stateCode,document.dvPayeeDetail.disbVchrPayeeCityName:cityName" />
              	</c:if>
              </td>              
            </tr>
            
            <tr>
              <th width="20%"  class="bord-l-b">
              	<div align="right"><kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbVchrCheckTotalAmount}"/></div>
              </th>
              <td width="30%"  class="datacell">
                <kul:htmlControlAttribute attributeEntry="${dvAttributes.disbVchrCheckTotalAmount}" property="document.disbVchrCheckTotalAmount" readOnly="${!fullEntryMode&&!frnEntryMode&&!taxEntryMode&&!travelEntryMode&&!wireEntryMode}"/>
              </td>
              <th width="20%"  class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbursementVoucherDueDate}"/></div></th>
              <td width="30%"  class="datacell">
                 <kul:htmlControlAttribute attributeEntry="${dvAttributes.disbursementVoucherDueDate}" property="document.disbursementVoucherDueDate" datePicker="true" readOnly="${!fullEntryMode && !voucherDeadlineEntryMode}"/>
              </td>
            </tr>
            
            <tr>
              <th  class="bord-l-b"><div align="right">Payment Type:</div></th>
              <td valign="top"  class="datacell">
                <c:if test="${taxEntryMode}">
                  <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrAlienPaymentCode}" property="document.dvPayeeDetail.disbVchrAlienPaymentCode"/>
                  <kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrAlienPaymentCode}" noColon="true" />
                  <br><br>
                </c:if>
                <c:if test="${!taxEntryMode}">
                    <kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrAlienPaymentCode}"/>
                    <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrAlienPaymentCode}" property="document.dvPayeeDetail.disbVchrAlienPaymentCode" readOnly="true"/>
                    <br><br>
                </c:if>
                <kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrPayeeEmployeeCode}"/> <bean:write  name="KualiForm" property="document.dvPayeeDetail.disbVchrPayeeEmployeeCode" /><br><br>
				<c:if test="${KualiForm.document.dvPayeeDetail.disbursementVoucherPayeeTypeCode=='V'}">
                	<kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrEmployeePaidOutsidePayrollCode}"/><bean:write  name="KualiForm" property="document.dvPayeeDetail.disbVchrEmployeePaidOutsidePayrollCode" /><br><br>
                </c:if>
              </td>  
              <th width="20%"  class="bord-l-b"><div align="right">Other Considerations: </div></th>
              <td width="30%"  class="datacell">
				<c:choose>
					<c:when test="${fullEntryMode || paymentHandlingEntryMode}"> 
						<kul:htmlControlAttribute attributeEntry="${dvAttributes.disbVchrAttachmentCode}" property="document.disbVchrAttachmentCode" readOnly="false"/>
						<kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbVchrAttachmentCode}" noColon="true" /><br>
					</c:when>
					<c:otherwise> 
						<kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbVchrAttachmentCode}"/>
						<kul:htmlControlAttribute attributeEntry="${dvAttributes.disbVchrAttachmentCode}" property="document.disbVchrAttachmentCode" readOnly="true"/><br>
					</c:otherwise>
				</c:choose>
         
				<c:choose>
                 <c:when test="${fullEntryMode || specialHandlingChangingEntryMode}">        
                   <kul:htmlControlAttribute attributeEntry="${dvAttributes.disbVchrSpecialHandlingCode}" property="document.disbVchrSpecialHandlingCode" onclick="specialHandlingMessage(this);" readOnly="false"/>
                   <kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbVchrSpecialHandlingCode}" noColon="true" /><br>
                 </c:when>
                 <c:otherwise>
                   <kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbVchrSpecialHandlingCode}"/>
                   <kul:htmlControlAttribute attributeEntry="${dvAttributes.disbVchrSpecialHandlingCode}" property="document.disbVchrSpecialHandlingCode" readOnly="true"/><br>          
                 </c:otherwise>
				</c:choose>
                 
                 <c:set var="w9IndReadOnly" value="${!fullEntryMode}"/>
                 <%-- cannot change w9 indicator if it has previousely been checked --%>
                 <c:if test="${KualiForm.document.editW9W8BENbox==true}">  
                     <c:set var="w9IndReadOnly" value="true"/>    
                 </c:if>
                 <c:if test="${w9IndReadOnly}">    
                   <kul:htmlControlAttribute attributeEntry="${dvAttributes.disbVchrPayeeW9CompleteCode}" property="document.disbVchrPayeeW9CompleteCode" disabled="true"/>
                   <kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbVchrPayeeW9CompleteCode}" noColon="true" /><br>                     
                 </c:if>
                 
                 <c:if test="${!w9IndReadOnly}">                
                   <kul:htmlControlAttribute attributeEntry="${dvAttributes.disbVchrPayeeW9CompleteCode}" property="document.disbVchrPayeeW9CompleteCode"/>
                   <kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbVchrPayeeW9CompleteCode}" noColon="true"/><br>
                 </c:if>
                 
                 
				<c:choose>
                 <c:when test="${fullEntryMode}">        
                   <kul:htmlControlAttribute attributeEntry="${dvAttributes.disbExcptAttachedIndicator}" property="document.disbExcptAttachedIndicator" onclick="exceptionMessage(this);" readOnly="false"/>
                   <kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbExcptAttachedIndicator}" noColon="true" /><br>
                 </c:when>
                 <c:otherwise>
                   <kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbExcptAttachedIndicator}"/>
                   <kul:htmlControlAttribute attributeEntry="${dvAttributes.disbExcptAttachedIndicator}" property="document.disbExcptAttachedIndicator" readOnly="true"/><br>          
                 </c:otherwise>
				</c:choose>     

				<c:choose>
                 <c:when test="${immediateDisbursementEntryMode}">        
                   <kul:htmlControlAttribute attributeEntry="${dvAttributes.immediatePaymentIndicator}" property="document.immediatePaymentIndicator" readOnly="false"/>
                   <kul:htmlAttributeLabel attributeEntry="${dvAttributes.immediatePaymentIndicator}" noColon="true" /><br>
                 </c:when>
                 <c:otherwise>
                   <kul:htmlAttributeLabel attributeEntry="${dvAttributes.immediatePaymentIndicator}"/>
                   <kul:htmlControlAttribute attributeEntry="${dvAttributes.immediatePaymentIndicator}" property="document.immediatePaymentIndicator" readOnly="true"/><br>          
                 </c:otherwise>
				</c:choose>
                 </td>
            </tr>
            
            <tr>
              <th  class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbVchrPaymentMethodCode}"/></div></th>
              <td  class="datacell">
                <kul:htmlControlAttribute attributeEntry="${dvAttributes.disbVchrPaymentMethodCode}" property="document.disbVchrPaymentMethodCode" extraReadOnlyProperty="document.disbVchrPaymentMethodName" onchange="paymentMethodMessages(this.value);" readOnly="${!fullEntryMode && !frnEntryMode}"/>
              </td>
              <th  class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbursementVoucherDocumentationLocationCode}"/></div></th>
              <td  class="datacell">
                <kul:htmlControlAttribute attributeEntry="${dvAttributes.disbursementVoucherDocumentationLocationCode}" property="document.disbursementVoucherDocumentationLocationCode" extraReadOnlyProperty="document.disbursementVoucherDocumentationLocationName" onchange="documentationMessage(this.value);" readOnly="${!fullEntryMode}"/>
                <c:if test="${fullEntryMode}">
              		<kul:lookup boClassName="org.kuali.kfs.sys.businessobject.PaymentDocumentationLocation" fieldConversions="paymentDocumentationLocationCode:document.disbursementVoucherDocumentationLocationCode" 
              		lookupParameters="document.disbursementVoucherDocumentationLocationCode:paymentDocumentationLocationCode" />
              	</c:if>
              </td>
            </tr>
            <tr>
              <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbVchrCheckStubText}"/></div></th>
              <td colspan="3"><kul:htmlControlAttribute attributeEntry="${dvAttributes.disbVchrCheckStubText}" property="document.disbVchrCheckStubText" readOnly="${!fullEntryMode && !paymentHandlingEntryMode}"/></td>
            </tr>
        </table>
     </div>
</kul:tab>
