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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for cash control document fields." %>
              
<%@ attribute name="cashControlDetailAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for cash control detail fields." %>
              
<%@ attribute name="readOnly" required="true" description="If document is in read only mode" %>              
              
<kul:tab tabTitle="Cash Control Details" defaultOpen="true" tabErrorKey="${KFSConstants.CASH_CONTROL_DETAILS_ERRORS}">
    <div class="tab-container" align=center>		
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Cash control Details">
            <tr>
                <td colspan="8" class="subhead">Cash Control Details</td>
            </tr>
			<tr>
			    <kul:htmlAttributeHeaderCell literalLabel="&nbsp;"/>
			    <kul:htmlAttributeHeaderCell attributeEntry="${cashControlDetailAttributes.documentNumber}" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${cashControlDetailAttributes.status}" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${cashControlDetailAttributes.customerNumber}" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${cashControlDetailAttributes.customerPaymentMediumIdentifier}" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${cashControlDetailAttributes.customerPaymentDate}" />
			    <kul:htmlAttributeHeaderCell attributeEntry="${cashControlDetailAttributes.financialDocumentLineAmount}" />
			
			    <kul:htmlAttributeHeaderCell literalLabel="Actions" />
			</tr>     
			<c:if test="${not readOnly}">
				<ar:cashControlDetail propertyName="newCashControlDetail" cashControlDetailAttributes="${cashControlDetailAttributes}" readOnly="${readOnly}" rowHeading="add" cssClass="infoline" actionMethod="addCashControlDetail"  actionAlt="Add Cash Control Detail" actionImage="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" />				
			</c:if>     
			<logic:iterate id="cashControlDetail" name="KualiForm" property="document.cashControlDetails" indexId="ctr">
				<ar:cashControlDetail propertyName="document.cashControlDetail[${ctr}]" cashControlDetailAttributes="${cashControlDetailAttributes}" readOnly="${readOnly}" rowHeading="${ctr+1}" cssClass="datacell" actionMethod="deleteCashControlDetail.line${ctr}" actionAlt="Delete Cash Control Detail" actionImage="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" />
	        </logic:iterate> 
	        <tr>
	 		   <td class="total-line" colspan="6">&nbsp;</td>
	  		   <td class="total-line" ><strong>Total: ${KualiForm.document.currencyFormattedTotalCashControlAmount}</strong><html:hidden write="false" property="document.cashControlTotalAmount" /></td>
               <c:if test="${!readOnly}">
                   <td class="total-line">&nbsp;</td>
               </c:if>
		    </tr> 
		   
    	 </table>
    </div>
</kul:tab>