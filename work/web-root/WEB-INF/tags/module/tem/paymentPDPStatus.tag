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

<kul:tab tabTitle="Pre-Disbursement Processor Status" defaultOpen="false">
  <div class="tab-container" align=center > 
  <h3>Disbursement Voucher Pre-Disbursement Processor Status</h3>
  <c:set var="travelPaymentAttributes" value="${DataDictionary.TravelPayment.attributes}" />
  <table style="datatable" cellpadding="0" border="0" summary="DV PDP Status">
    <tr>
      <th align="right" valign="middle" class="bord-l-b" width="50%"><kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.pdpStatus}"/></th>
      <td align="left" valign="middle" class="datacell" width="50%"><kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.pdpStatus}" property="document.travelPayment.pdpStatus" readOnly="true" /></td>
    </tr>
    <tr>
      <th align="right" valign="middle" class="bord-l-b" width="50%"><kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.extractDate}"/></th>
      <td align="left" valign="middle" class="datacell" width="50%"><kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.extractDate}" property="document.travelPayment.extractDate" readOnly="true" /></td>
    </tr>
    <tr>
      <th align="right" valign="middle" class="bord-l-b" width="50%"><kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.paidDate}"/></th>
       <td align="left" valign="middle" class="datacell" width="50%"><kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.paidDate}" property="document.travelPayment.paidDate" readOnly="true" />
          
          <c:if test="${not empty KualiForm.document.extractDate}">
              <fp:dvDisbursementInfo sourceDocumentNumber="${KualiForm.document.documentNumber}" sourceDocumentType="${KualiForm.document.paymentDetailDocumentType}" /> 
          </c:if>
      </td>
    </tr>
    <tr>
      <th align="right" valign="middle" class="bord-l-b" width="50%"><kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.cancelDate}"/></th>
      <td align="left" valign="middle" class="datacell" width="50%"><kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.cancelDate}" property="document.travelPayment.cancelDate" readOnly="true" /></td>
    </tr>
  </table>
  </div>
</kul:tab>
