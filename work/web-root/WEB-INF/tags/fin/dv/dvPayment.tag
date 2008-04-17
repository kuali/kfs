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

<kul:tab tabTitle="Payment Information" defaultOpen="true" tabErrorKey="${KFSConstants.DV_PAYMENT_TAB_ERRORS},document.disbVchrPaymentMethodCode">
  	<c:set var="dvAttributes" value="${DataDictionary.DisbursementVoucherDocument.attributes}" />
  	<c:set var="payeeAttributes" value="${DataDictionary.DisbursementVoucherPayeeDetail.attributes}" />
    <div class="tab-container" align=center > 
    <div class="h2-container">
                <h2>Payment Information</h2>
              </div>
	   <table cellpadding=0 class="datatable" summary="PaymentSection">
            <tr>
              <th  class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrPaymentReasonCode}"/></div></th>
              <td colspan="3"  class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPaymentReasonCode}" property="document.dvPayeeDetail.disbVchrPaymentReasonCode" extraReadOnlyProperty="document.dvPayeeDetail.disbVchrPaymentReasonName" onchange="paymentReasonMessages(this.value);" readOnly="${!fullEntryMode}"/>
                <c:if test="${fullEntryMode}">
                  <kul:lookup boClassName="org.kuali.module.financial.bo.PaymentReasonCode" fieldConversions="code:document.dvPayeeDetail.disbVchrPaymentReasonCode"/>
                </c:if>
                <a href="${ConfigProperties.application.url}/kr/inquiry.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.PaymentReasonCode"
                   onclick="this.href='${ConfigProperties.application.url}/kr/inquiry.do?methodToCall=start&businessObjectClassName=org.kuali.module.financial.bo.PaymentReasonCode&code=' + document.forms[0].elements['document.dvPayeeDetail.disbVchrPaymentReasonCode'].value;" target="_blank">
                  <img src="${ConfigProperties.kr.externalizable.images.url}my_cp_inf.gif" styleClass="globalbuttons" alt="help"/>
                </a>
            </tr>
            
            <tr>
              <th width="20%"  class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbVchrCheckTotalAmount}"/></div></th>
              <td width="30%"  class="datacell">
                <kul:htmlControlAttribute attributeEntry="${dvAttributes.disbVchrCheckTotalAmount}" property="document.disbVchrCheckTotalAmount" readOnly="${!fullEntryMode&&!frnEntryMode&&!taxEntryMode&&!travelEntryMode&&!wireEntryMode}"/>
              </td>
              <th width="20%"  class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbursementVoucherDueDate}"/></div></th>
              <td width="30%"  class="datacell">
                 <kul:htmlControlAttribute attributeEntry="${dvAttributes.disbursementVoucherDueDate}" property="document.disbursementVoucherDueDate" datePicker="true" readOnly="${!fullEntryMode}"/>
              </td>
            </tr>
            
            <tr>
              <th  class="bord-l-b"><div align="right">Payment Type:</div></th>
              <td valign="top"  class="datacell">
                <c:if test="${taxEntryMode}">
                  <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrAlienPaymentCode}" property="document.dvPayeeDetail.disbVchrAlienPaymentCode"/>
                  <kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrAlienPaymentCode}"/><br><br>
                </c:if>
                <c:if test="${!taxEntryMode}">
                    <kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrAlienPaymentCode}"/>
                    <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrAlienPaymentCode}" property="document.dvPayeeDetail.disbVchrAlienPaymentCode" readOnly="true"/>
                  <br><br>
                </c:if>
                <kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrPayeeEmployeeCode}"/> &nbsp; <html:hidden write="true" property="document.dvPayeeDetail.disbVchrPayeeEmployeeCode" />  <br><br>
              </td>  
              <th width="20%"  class="bord-l-b"><div align="right">Other Considerations: </div></th>
              <td width="30%"  class="datacell">
                 <c:if test="${fullEntryMode}"> 
                   <kul:htmlControlAttribute attributeEntry="${dvAttributes.disbVchrAttachmentCode}" property="document.disbVchrAttachmentCode" readOnly="false"/>
                   <kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbVchrAttachmentCode}"/><br>
                 </c:if>
                 
                 <c:if test="${!fullEntryMode}"> 
                   <kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbVchrAttachmentCode}"/>
                   <kul:htmlControlAttribute attributeEntry="${dvAttributes.disbVchrAttachmentCode}" property="document.disbVchrAttachmentCode" readOnly="true"/><br>
                 </c:if>
         
                 <c:if test="${fullEntryMode}">        
                   <kul:htmlControlAttribute attributeEntry="${dvAttributes.disbVchrSpecialHandlingCode}" property="document.disbVchrSpecialHandlingCode" onclick="specialHandlingMessage(this);" readOnly="false"/>
                   <kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbVchrSpecialHandlingCode}"/><br>
                 </c:if>
                 
                 <c:if test="${!fullEntryMode}"> 
                   <kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbVchrSpecialHandlingCode}"/>
                   <kul:htmlControlAttribute attributeEntry="${dvAttributes.disbVchrSpecialHandlingCode}" property="document.disbVchrSpecialHandlingCode" readOnly="true"/><br>          
                 </c:if>
                 
                 <c:set var="w9IndReadOnly" value="${!fullEntryMode}"/>
                 <%-- cannot change w9 indicator if it has previousely been checked --%>
                 <c:if test="${KualiForm.document.disbVchrPayeeW9CompleteCode=='Yes'}">  
                     <c:set var="w9IndReadOnly" value="true"/>    
                 </c:if>
                 
                 <c:if test="${w9IndReadOnly}">    
                   <kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbVchrPayeeW9CompleteCode}"/>
                   <kul:htmlControlAttribute attributeEntry="${dvAttributes.disbVchrPayeeW9CompleteCode}" property="document.disbVchrPayeeW9CompleteCode" readOnly="true"/><br>                     
                 </c:if>
                 
                 <c:if test="${!w9IndReadOnly}">                
                   <kul:htmlControlAttribute attributeEntry="${dvAttributes.disbVchrPayeeW9CompleteCode}" property="document.disbVchrPayeeW9CompleteCode" readOnly="false"/>
                   <kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbVchrPayeeW9CompleteCode}"/><br>
                 </c:if>
                 
                 <c:if test="${fullEntryMode}">
                   <html:checkbox property="document.exceptionIndicator" onclick="exceptionMessage(this);"/>
                   Exception Attached:
                 </c:if>  
                 <c:if test="${!fullEntryMode}">
                   Exception Attached: <html:hidden write="true" property="document.exceptionIndicator"/>
                 </c:if>
                 </td>
            </tr>
            
            <tr>
              <th  class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbVchrPaymentMethodCode}"/></div></th>
              <td  class="datacell">
                <kul:htmlControlAttribute attributeEntry="${dvAttributes.disbVchrPaymentMethodCode}" property="document.disbVchrPaymentMethodCode" extraReadOnlyProperty="document.disbVchrPaymentMethodName" onchange="paymentMethodMessages(this.value);" readOnly="${!fullEntryMode&&!frnEntryMode}"/>
              </td>
              <th  class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbursementVoucherDocumentationLocationCode}"/></div></th>
              <td  class="datacell">
                <kul:htmlControlAttribute attributeEntry="${dvAttributes.disbursementVoucherDocumentationLocationCode}" property="document.disbursementVoucherDocumentationLocationCode" extraReadOnlyProperty="document.disbursementVoucherDocumentationLocationName" onchange="documentationMessage(this.value);" readOnly="${!fullEntryMode}"/>
                <c:if test="${fullEntryMode}">
                  <kul:lookup boClassName="org.kuali.module.financial.bo.DisbursementVoucherDocumentationLocation" fieldConversions="disbursementVoucherDocumentationLocationCode:document.disbursementVoucherDocumentationLocationCode"/>
                </c:if>
              </td>
            </tr>
            <tr>
              <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${dvAttributes.disbVchrCheckStubText}"/></div></th>
              <td colspan="3"><kul:htmlControlAttribute attributeEntry="${dvAttributes.disbVchrCheckStubText}" property="document.disbVchrCheckStubText" readOnly="${!fullEntryMode}"/></td>
            </tr>
          </table>
     </div>
</kul:tab>
