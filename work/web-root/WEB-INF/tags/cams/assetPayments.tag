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


<%@ attribute name="editingMode" required="true" description="used to decide if items may be edited" type="java.util.Map"%>
<c:set var="readOnly" value="${not empty editingMode['viewOnly']}" />

<kul:tab tabTitle="Asset Payments" defaultOpen="true" tabErrorKey="${KFSConstants.ADVANCE_DEPOSITS_LINE_ERRORS}">
<c:set var="paymentAttributes" value="${DataDictionary.AssetPaymentDetail.attributes}" />
 <div class="tab-container" align=center>
	<div class="h2-container">
	<h2>Advance Deposits</h2>
	</div>
	<table cellpadding=0 class="datatable" summary="Asset Payments">
		<tr>
            <kul:htmlAttributeHeaderCell literalLabel="&nbsp;"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${paymentAttributes.financialDocumentBankCode}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${paymentAttributes.financialDocumentBankAccountNumber}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${paymentAttributes.financialDocumentAdvanceDepositDate}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${paymentAttributes.financialDocumentAdvanceDepositReferenceNumber}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${paymentAttributes.financialDocumentAdvanceDepositDescription}"/>
            <kul:htmlAttributeHeaderCell attributeEntry="${paymentAttributes.financialDocumentAdvanceDepositAmount}"/>
            <c:if test="${not readOnly}">
                <kul:htmlAttributeHeaderCell literalLabel="Actions"/>
            </c:if>
		</tr>
        <c:if test="${not readOnly}">
            <tr>
                <kul:htmlAttributeHeaderCell literalLabel="add:" scope="row"/>
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${paymentAttributes.financialDocumentBankCode}" property="newAdvanceDeposit.financialDocumentBankCode" />
                	&nbsp;
                	<kul:lookup boClassName="org.kuali.module.financial.bo.Bank" fieldConversions="financialDocumentBankCode:newAdvanceDeposit.financialDocumentBankCode" />
                </td>
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${paymentAttributes.financialDocumentBankAccountNumber}" property="newAdvanceDeposit.financialDocumentBankAccountNumber" />
                	&nbsp;
                	<kul:lookup boClassName="org.kuali.module.financial.bo.BankAccount" fieldConversions="financialDocumentBankCode:newAdvanceDeposit.financialDocumentBankCode,finDocumentBankAccountNumber:newAdvanceDeposit.financialDocumentBankAccountNumber" lookupParameters="newAdvanceDeposit.financialDocumentBankCode:financialDocumentBankCode" />
                </td>
                <td class="infoline">
                	<kul:dateInput attributeEntry="${paymentAttributes.financialDocumentAdvanceDepositDate}" property="newAdvanceDeposit.financialDocumentAdvanceDepositDate" />
                </td>
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${paymentAttributes.financialDocumentAdvanceDepositReferenceNumber}" property="newAdvanceDeposit.financialDocumentAdvanceDepositReferenceNumber" />
                </td>
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${paymentAttributes.financialDocumentAdvanceDepositDescription}" property="newAdvanceDeposit.financialDocumentAdvanceDepositDescription" />
                </td>
                <td class="infoline">
                	<kul:htmlControlAttribute attributeEntry="${paymentAttributes.financialDocumentAdvanceDepositAmount}" property="newAdvanceDeposit.financialDocumentAdvanceDepositAmount" styleClass="amount"/>
                </td>
                <td class="infoline">
                	<div align="center">
                		<html:image property="methodToCall.addAdvanceDeposit" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Add an Advance Deposit" title="Add an Advance Deposit" styleClass="tinybutton"/>
                	</div>
                </td>
            </tr>
        </c:if>
        <logic:iterate id="advanceDepositDetail" name="KualiForm" property="document.advanceDeposits" indexId="ctr">
            <tr>
                <kul:htmlAttributeHeaderCell literalLabel="${ctr+1}:" scope="row">
                    <%-- Outside this th, these hidden fields would be invalid HTML. --%>
                    <html:hidden property="document.advanceDepositDetail[${ctr}].documentNumber" />
                    <html:hidden property="document.advanceDepositDetail[${ctr}].financialDocumentTypeCode" />
                    <html:hidden property="document.advanceDepositDetail[${ctr}].financialDocumentColumnTypeCode" />
                    <html:hidden property="document.advanceDepositDetail[${ctr}].financialDocumentLineNumber" />
                    <html:hidden property="document.advanceDepositDetail[${ctr}].versionNumber" />
                    <html:hidden property="document.advanceDepositDetail[${ctr}].objectId" />
                </kul:htmlAttributeHeaderCell>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${paymentAttributes.financialDocumentBankCode}" property="document.advanceDepositDetail[${ctr}].financialDocumentBankCode" readOnly="${readOnly}" />
                	<c:if test="${not readOnly}">
	                	&nbsp;
    	            	<kul:lookup boClassName="org.kuali.module.financial.bo.Bank" fieldConversions="financialDocumentBankCode:document.advanceDepositDetail[${ctr}].financialDocumentBankCode" />
                	</c:if>
                </td>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${paymentAttributes.financialDocumentBankAccountNumber}" property="document.advanceDepositDetail[${ctr}].financialDocumentBankAccountNumber" readOnly="${readOnly}" />
                	<c:if test="${not readOnly}">
	                	&nbsp;
    	            	<kul:lookup boClassName="org.kuali.module.financial.bo.BankAccount" fieldConversions="financialDocumentBankCode:document.advanceDepositDetail[${ctr}].financialDocumentBankCode,finDocumentBankAccountNumber:document.advanceDepositDetail[${ctr}].financialDocumentBankAccountNumber" lookupParameters="document.advanceDepositDetail[${ctr}].financialDocumentBankCode:financialDocumentBankCode" />
    	            </c:if>
                </td>
                <td class="datacell">
                	<c:choose>
                        <c:when test="${readOnly}">
                            <kul:htmlControlAttribute attributeEntry="${paymentAttributes.financialDocumentAdvanceDepositDate}" property="document.advanceDepositDetail[${ctr}].financialDocumentAdvanceDepositDate" readOnly="true" />
                        </c:when>
                        <c:otherwise>
                            <kul:dateInput attributeEntry="${paymentAttributes.financialDocumentAdvanceDepositDate}" property="document.advanceDepositDetail[${ctr}].financialDocumentAdvanceDepositDate" />
                        </c:otherwise>
                    </c:choose>
                </td>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${paymentAttributes.financialDocumentAdvanceDepositReferenceNumber}" property="document.advanceDepositDetail[${ctr}].financialDocumentAdvanceDepositReferenceNumber" readOnly="${readOnly}"/>
                </td>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${paymentAttributes.financialDocumentAdvanceDepositDescription}" property="document.advanceDepositDetail[${ctr}].financialDocumentAdvanceDepositDescription" readOnly="${readOnly}"/>
                </td>
                <td class="datacell">
                	<kul:htmlControlAttribute attributeEntry="${paymentAttributes.financialDocumentAdvanceDepositAmount}" property="document.advanceDepositDetail[${ctr}].financialDocumentAdvanceDepositAmount" readOnly="${readOnly}"/>
                </td>
                <c:if test="${not readOnly}">
                    <td class="datacell">
                    	<div align="center">
                    		<html:image property="methodToCall.deleteAdvanceDeposit.line${ctr}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" alt="Delete an Advance Deposit" title="Delete an Advance Deposit" styleClass="tinybutton"/>
                    	</div>
                    </td>
                </c:if>
            </tr>
        </logic:iterate>
		<tr>
	 		<td class="total-line" colspan="6">&nbsp;</td>
	  		<td class="total-line" ><strong>Total: ${KualiForm.document.currencyFormattedTotalAdvanceDepositAmount}</strong><html:hidden write="false" property="document.totalAdvanceDepositAmount" /></td>
            <c:if test="${not readOnly}">
                <td class="total-line">&nbsp;</td>
            </c:if>
		</tr>
	</table>
  </div>
</kul:tab>