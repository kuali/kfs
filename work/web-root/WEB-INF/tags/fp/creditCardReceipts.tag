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
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />

<kul:tab tabTitle="Credit Card Receipts" defaultOpen="true" tabErrorKey="${KFSConstants.CREDIT_CARD_RECEIPTS_LINE_ERRORS}">
<c:set var="ccrAttributes" value="${DataDictionary.CreditCardDetail.attributes}" />
<c:set var="tabindexOverrideBase" value="20" />

 <div class="tab-container" align=center>
	<h3>Credit Card Receipts</h3>
	<table cellpadding=0 class="datatable" summary="Credit Card Receipts section">
		<tr>
            <kul:htmlAttributeHeaderCell literalLabel="&nbsp;"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${ccrAttributes.financialDocumentCreditCardTypeCode}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${ccrAttributes.financialDocumentCreditCardVendorNumber}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${ccrAttributes.creditCardDepositDate}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${ccrAttributes.creditCardDepositReferenceNumber}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${ccrAttributes.creditCardAdvanceDepositAmount}"/>
            <c:if test="${not readOnly}">
                <kul:htmlAttributeHeaderCell literalLabel="Actions"/>
            </c:if>
		</tr>
        <c:if test="${not readOnly}">
            <tr>
                <kul:htmlAttributeHeaderCell literalLabel="add:" scope="row"/>
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${ccrAttributes.financialDocumentCreditCardTypeCode}" 
                	tabindexOverride="${tabindexOverrideBase}"
                	property="newCreditCardReceipt.financialDocumentCreditCardTypeCode" />
                	&nbsp;
                	<kul:lookup boClassName="org.kuali.kfs.fp.businessobject.CreditCardType" fieldConversions="financialDocumentCreditCardTypeCode:newCreditCardReceipt.financialDocumentCreditCardTypeCode" />
                </td>
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${ccrAttributes.financialDocumentCreditCardVendorNumber}" 
                	tabindexOverride="${tabindexOverrideBase} + 5"
                	property="newCreditCardReceipt.financialDocumentCreditCardVendorNumber" />
                	&nbsp;
                	<kul:lookup boClassName="org.kuali.kfs.fp.businessobject.CreditCardVendor" fieldConversions="financialDocumentCreditCardTypeCode:newCreditCardReceipt.financialDocumentCreditCardTypeCode,financialDocumentCreditCardVendorNumber:newCreditCardReceipt.financialDocumentCreditCardVendorNumber" lookupParameters="newCreditCardReceipt.financialDocumentCreditCardTypeCode:financialDocumentCreditCardTypeCode" />
                </td>
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${ccrAttributes.creditCardDepositDate}" datePicker="true"
                	tabindexOverride="${tabindexOverrideBase} + 10"
                	property="newCreditCardReceipt.creditCardDepositDate" />
                </td>
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${ccrAttributes.creditCardDepositReferenceNumber}"
                	tabindexOverride="${tabindexOverrideBase} + 15"
                	property="newCreditCardReceipt.creditCardDepositReferenceNumber" />
                </td>
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${ccrAttributes.creditCardAdvanceDepositAmount}"
                	tabindexOverride="${tabindexOverrideBase} + 20"
                	property="newCreditCardReceipt.creditCardAdvanceDepositAmount" styleClass="amount" />
                </td>
                <td class="infoline">
                	<div align="center">
                		<html:image property="methodToCall.addCreditCardReceipt" tabindex="${tabindexOverrideBase} + 25"
                		src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" title="Add a Credit Card Receipt" alt="Add a Credit Card Receipt" styleClass="tinybutton"/>
                	</div>
                </td>
            </tr>
        </c:if>
        <logic:iterate id="creditCardReceipt" name="KualiForm" property="document.creditCardReceipts" indexId="ctr">
            <tr>
                <th>
					<c:out value="${ctr + 1}" />:
				</th>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${ccrAttributes.financialDocumentCreditCardTypeCode}" property="document.creditCardReceipt[${ctr}].financialDocumentCreditCardTypeCode" readOnly="${readOnly}" />
                	<c:if test="${not readOnly}">
	                	&nbsp;
    	            	<kul:lookup boClassName="org.kuali.kfs.fp.businessobject.CreditCardType" fieldConversions="financialDocumentCreditCardTypeCode:document.creditCardReceipt[${ctr}].financialDocumentCreditCardTypeCode" />
                	</c:if>
                </td>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${ccrAttributes.financialDocumentCreditCardVendorNumber}" property="document.creditCardReceipt[${ctr}].financialDocumentCreditCardVendorNumber" readOnly="${readOnly}" />
                	<c:if test="${not readOnly}">
	                	&nbsp;
    	            	<kul:lookup boClassName="org.kuali.kfs.fp.businessobject.CreditCardVendor" fieldConversions="financialDocumentCreditCardTypeCode:document.creditCardReceipt[${ctr}].financialDocumentCreditCardTypeCode,financialDocumentCreditCardVendorNumber:document.creditCardReceipt[${ctr}].financialDocumentCreditCardVendorNumber" lookupParameters="document.creditCardReceipt[${ctr}].financialDocumentCreditCardTypeCode:financialDocumentCreditCardTypeCode" />
    	            </c:if>
                </td>
                <td class="datacell">
                	<c:choose>
                        <c:when test="${readOnly}">
                            <kul:htmlControlAttribute attributeEntry="${ccrAttributes.creditCardDepositDate}" property="document.creditCardReceipt[${ctr}].creditCardDepositDate" readOnly="true" />
                        </c:when>
                        <c:otherwise>
                            <kul:dateInput attributeEntry="${ccrAttributes.creditCardDepositDate}" property="document.creditCardReceipt[${ctr}].creditCardDepositDate" />
                        </c:otherwise>
                    </c:choose>
                </td>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${ccrAttributes.creditCardDepositReferenceNumber}" property="document.creditCardReceipt[${ctr}].creditCardDepositReferenceNumber" readOnly="${readOnly}"/>
                </td>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${ccrAttributes.creditCardAdvanceDepositAmount}" property="document.creditCardReceipt[${ctr}].creditCardAdvanceDepositAmount" readOnly="${readOnly}" styleClass="amount"/>
                </td>
                <c:if test="${not readOnly}">
                    <td class="datacell">
                    	<div align="center">
                    		<html:image property="methodToCall.deleteCreditCardReceipt.line${ctr}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" title="Delete a Credit Card Receipt" alt="Delete a Credit Card Receipt" styleClass="tinybutton"/>
                    	</div>
                    </td>
                </c:if>
            </tr>
        </logic:iterate>
		<tr>
	 		<td class="total-line" colspan="5">&nbsp;</td>
	  		<td class="total-line" ><strong>Total: ${KualiForm.document.currencyFormattedTotalCreditCardAmount}</strong></td>
            <c:if test="${not readOnly}">
                <td class="total-line">&nbsp;</td>
            </c:if>
		</tr>
	</table>
  </div>
</kul:tab>
