<%--
 Copyright 2006-2009 The Kuali Foundation
 
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
<%@ attribute name="hasRelatedCashControlDocument" required="true" description="If has related cash control document"%>
<%@ attribute name="readOnly" required="true" description="If document is in read only mode"%>
<%@ attribute name="isCustomerSelected" required="true" description="Whether or not the customer is set" %>
<%@ attribute name="customerInvoiceDetailAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>
<%@ attribute name="invoiceAttributes" required="true"
	type="java.util.Map"
	description="The DataDictionary entry containing attributes for this row's fields."%>

<c:set var="invoiceApplicationsByDocumentNumber" value="${KualiForm.invoiceApplicationsByDocumentNumber}" scope="request" />

    <kul:tab tabTitle="Quick Apply to Invoice"
        defaultOpen="${isCustomerSelected}"
        tabErrorKey="${KFSConstants.PAYMENT_APPLICATION_DOCUMENT_ERRORS}">
        <div class="tab-container" align="center">
            <c:choose>
                <c:when test="${!isCustomerSelected}">
                    No Customer Selected
                </c:when>
                <c:otherwise>
                    <h3>Quick Apply to Invoice</h3>
                    <table width="100%" cellpadding="0" cellspacing="0"
                        class="datatable">
                        <tr>
                            <th>Invoice Number</th>
                            <th>Open Amount</th>
							<c:if test="${readOnly ne true}">
	                            <th>Quick Apply</th>
	                        </c:if>
                        </tr>
                     <c:choose>
                     <c:when test="${empty KualiForm.invoiceApplications}">
                     	<tr><td colspan="3">No Invoices Are Available for QuickApply</td></tr>
                     </c:when>
                     <c:otherwise>
                        <logic:iterate name="KualiForm" property="invoiceApplications" id="invoiceApplication" indexId="idx">
                        
                            <tr>
                                <td>
									<kul:htmlControlAttribute
										attributeEntry="${invoiceAttributes.documentNumber}"
										property="invoiceApplications[${idx}].documentNumber" readOnly="true" />
                                    <!--<bean:write name="invoiceApplication" property="documentNumber" />-->
                                </td>
                                <td style="text-align: right;">
									<kul:htmlControlAttribute
										attributeEntry="${invoiceAttributes.openAmount}"
										property="invoiceApplications[${idx}].openAmount" readOnly="true" />
                                    <!-- $<bean:write name="invoiceApplication" property="openAmount" />-->
                                </td>
								<c:if test="${readOnly ne true}">
	                                <td>
	                                	<center>
											<kul:htmlControlAttribute
												readOnly="${readOnly}"
												disabled="${!invoiceApplication.quickApply && invoiceApplication.anyAppliedAmounts}" 
												attributeEntry="${customerInvoiceDetailAttributes.taxableIndicator}" 
												property="invoiceApplications[${idx}].quickApply" />
		                                </center>
	                                </td>
	                            </c:if>
                            </tr>
                        </logic:iterate>
                      </c:otherwise>
                      </c:choose>
						<c:if test="${readOnly ne true}">
	                        <tr>
	                            <td colspan='3' style='text-align: right;'>
	                                <html:image property="methodToCall.applyAllAmounts"
	                                    src="${ConfigProperties.externalizable.images.url}tinybutton-apply.gif"
	                                    alt="Quick Apply" title="Quick Apply" styleClass="tinybutton" />
	                            </td>
	                        </tr>
	                    </c:if>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </kul:tab>
