<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="travelPaymentProperty" required="false" description="The property associated with the travel payment (travelPayment for TR, RELO, and ENT; advanceTravelPayment for TA)." %>
<%@ attribute name="pdpPaymentDocumentType" required="false" description="The document type which the document believes will be used as the PDP document type." %>
<%@ attribute name="displayCorporateCardExtraction" required="false" description="Whether this tag should display corporate card extraction status or not." %>

<c:set var="travelPaymentProp" value="${travelPaymentProperty}"/>
<c:if test="${empty travelPaymentProp}">
	<c:set var="travelPaymentProp" value="travelPayment"/>
</c:if>
<c:set var="displayCorpCard" value="${displayCorporateCardExtraction}"/>
<c:if test="${empty displayCorpCard}">
	<c:set var="displayCorpCard" value="false" />
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
	<c:if test="${displayCorpCard}">
		<c:set var="corporateCardAttributes" value="${DataDictionary[KualiForm.document.class.simpleName].attributes}"/>
		<tr>
		  <th align="right" valign="middle" class="bord-l-b" width="50%"><kul:htmlAttributeLabel attributeEntry="${corporateCardAttributes.corporateCardPaymentPdpStatus}"/></th>
		  <td align="left" valign="middle" class="datacell" width="50%"><kul:htmlControlAttribute attributeEntry="${corporateCardAttributes.corporateCardPaymentPdpStatus}" property="document.corporateCardPaymentPdpStatus" readOnly="true" /></td>
		</tr>
		<tr>
		  <th align="right" valign="middle" class="bord-l-b" width="50%"><kul:htmlAttributeLabel attributeEntry="${corporateCardAttributes.corporateCardPaymentExtractDate}"/></th>
		  <td align="left" valign="middle" class="datacell" width="50%"><kul:htmlControlAttribute attributeEntry="${corporateCardAttributes.corporateCardPaymentExtractDate}" property="document.corporateCardPaymentExtractDate" readOnly="true" /></td>
		</tr>
		<tr>
		  <th align="right" valign="middle" class="bord-l-b" width="50%"><kul:htmlAttributeLabel attributeEntry="${corporateCardAttributes.corporateCardPaymentPaidDate}"/></th>
		   <td align="left" valign="middle" class="datacell" width="50%"><kul:htmlControlAttribute attributeEntry="${corporateCardAttributes.corporateCardPaymentPaidDate}" property="document.corporateCardPaymentPaidDate" readOnly="true" />
			  
			  <c:if test="${not empty KualiForm.document.corporateCardPaymentExtractDate}">
				  <tem:travelPaymentDisbursementInfo sourceDocumentNumber="${KualiForm.document.documentNumber}" sourceDocumentType="${KualiForm.document.corporateCardPaymentAchCheckDocumentType}" /> 
			  </c:if>
		  </td>
		</tr>
		<tr>
		  <th align="right" valign="middle" class="bord-l-b" width="50%"><kul:htmlAttributeLabel attributeEntry="${corporateCardAttributes.corporateCardPaymentCancelDate}"/></th>
		  <td align="left" valign="middle" class="datacell" width="50%"><kul:htmlControlAttribute attributeEntry="${corporateCardAttributes.corporateCardPaymentCancelDate}" property="document.corporateCardPaymentCancelDate" readOnly="true" /></td>
		</tr>
	</c:if>
  </table>
  </div>
</kul:tab>
