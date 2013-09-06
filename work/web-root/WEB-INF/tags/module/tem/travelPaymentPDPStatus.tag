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

<%@ attribute name="travelPaymentProperty" required="false" description="The property associated with the travel payment (travelPayment for TR, RELO, and ENT; advanceTravelPayment for TA)." %>
<%@ attribute name="pdpPaymentDocumentType" required="false" description="The document type which the document believes will be used as the PDP document type." %>

<c:set var="travelPaymentProp" value="${travelPaymentProperty}"/>
<c:if test="${empty travelPaymentProp}">
	<c:set var="travelPaymentProp" value="travelPayment"/>
</c:if>

<kul:tab tabTitle="Pre-Disbursement Processor Status" defaultOpen="false">
  <div class="tab-container" align=center > 
  <h3>Pre-Disbursement Processor Status</h3>
  <c:set var="travelPaymentAttributes" value="${DataDictionary.TravelPayment.attributes}" />
  <table style="datatable" cellpadding="0" border="0" summary="PDP Status">
    <tr>
      <th align="right" valign="middle" class="bord-l-b" width="50%"><kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.paymentPdpStatus}"/></th>
      <td align="left" valign="middle" class="datacell" width="50%"><kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.paymentPdpStatus}" property="document.${travelPaymentProp}.paymentPdpStatus" readOnly="true" /></td>
    </tr>
    <tr>
      <th align="right" valign="middle" class="bord-l-b" width="50%"><kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.extractDate}"/></th>
      <td align="left" valign="middle" class="datacell" width="50%"><kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.extractDate}" property="document.${travelPaymentProp}.extractDate" readOnly="true" /></td>
    </tr>
    <tr>
      <th align="right" valign="middle" class="bord-l-b" width="50%"><kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.paidDate}"/></th>
       <td align="left" valign="middle" class="datacell" width="50%"><kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.paidDate}" property="document.${travelPaymentProp}.paidDate" readOnly="true" />
          <c:if test="${not empty KualiForm.document[travelPaymentProp].extractDate}">
              <tem:travelPaymentDisbursementInfo sourceDocumentNumber="${KualiForm.document.documentNumber}" sourceDocumentType="${pdpPaymentDocumentType}" /> 
          </c:if>
      </td>
    </tr>
    <tr>
      <th align="right" valign="middle" class="bord-l-b" width="50%"><kul:htmlAttributeLabel attributeEntry="${travelPaymentAttributes.cancelDate}"/></th>
      <td align="left" valign="middle" class="datacell" width="50%"><kul:htmlControlAttribute attributeEntry="${travelPaymentAttributes.cancelDate}" property="document.${travelPaymentProp}.cancelDate" readOnly="true" /></td>
    </tr>
  </table>
  </div>
</kul:tab>
