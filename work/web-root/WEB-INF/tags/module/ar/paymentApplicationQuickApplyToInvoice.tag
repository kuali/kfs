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
