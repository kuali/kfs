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
<%@ attribute name="hasRelatedCashControlDocument" required="true"
    description="If has related cash control document"%>
<%@ attribute name="readOnly" required="true"
    description="If document is in read only mode"%>
<%@ attribute name="isCustomerSelected" required="true"
    description="Whether or not the customer is set" %>
<c:set var="unappliedAttributes" value="${DataDictionary['NonAppliedHolding'].attributes}" />
<c:set var="customerAttributes" value="${DataDictionary['Customer'].attributes}" />

    <kul:tab tabTitle="Unapplied" defaultOpen="true"
        tabErrorKey="${KFSConstants.PaymentApplicationTabErrorCodes.UNAPPLIED_TAB}">
        <div class="tab-container" align="center">
            <table width="100%" cellpadding="0" cellspacing="0" class="datatable">
                <tr>
                    <kul:htmlAttributeHeaderCell literalLabel="Customer"/>  
                    <td>
                        <kul:htmlControlAttribute
                        	readOnly="${readOnly}"
                            attributeEntry="${customerAttributes.customerNumber}"
                            property="document.nonAppliedHolding.customerNumber"/>
						<c:if test="${readOnly ne true}">
	                        <kul:lookup boClassName="org.kuali.kfs.module.ar.businessobject.Customer" autoSearch="true"
	                            fieldConversions="customerNumber:document.nonAppliedHolding.customerNumber"
	                            lookupParameters="document.nonAppliedHolding.customerNumber:customerNumber" />
                        </c:if>
                    </td>
                    <kul:htmlAttributeHeaderCell literalLabel="Amount"/>  
                    <td>
                    	<html:hidden property="oldNonAppliedHoldingAmount" value="${oldNonAppliedHoldingAmount}" />
                        <kul:htmlControlAttribute
	                        styleClass="amount"
                            attributeEntry="${unappliedAttributes.financialDocumentLineAmount}"
                            property="document.nonAppliedHolding.financialDocumentLineAmount"
                            readOnly="${readOnly}" />
						<c:if test="${readOnly ne true}">
	                        <html:image property="methodToCall.applyAllAmounts"
	                            src="${ConfigProperties.externalizable.images.url}tinybutton-apply.gif"
	                            alt="Commit Unapplied" title="Commit Unapplied" styleClass="tinybutton" />
	                    </c:if>
                    </td>
                </tr>
            </table>
        </div>
    </kul:tab>