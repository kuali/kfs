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
<%@ attribute name="customerAttributes" required="true"
    description="Attributes of Customer according to the data dictionary" %>
<%@ attribute name="customerInvoiceDetailAttributes" required="true"
    description="Attributes of CustomerInvoiceDetail according to the data dictionary" %>
<%@ attribute name="invoiceAttributes" required="true" 
    description="Attributes of Invoice according to the data dictionary" %>

<kul:tab tabTitle="Control Information"
    defaultOpen="true"
    tabErrorKey="${KFSConstants.CASH_CONTROL_DOCUMENT_ERRORS},document.hiddenFieldForErrors">

    <div class="tab-container" align="center">
		<html:hidden property="document.hiddenFieldForErrors"/>

        <c:choose>
            <c:when test="${!hasRelatedCashControlDocument}">
         	    <c:choose>
                  <c:when test="${!isCustomerSelected}">
				      No Customer Selected            
	              </c:when>
                  <c:otherwise>
    	              <div style='text-align: right; margin-top: 20px; padding: 2px 6px; width: 90%;'>
        	            <style type='text/css'>
            	            #ctrl-info th { text-align: right; }
                	        #ctrl-info th, #ctrl-info td { width: 50%; }
    	                </style>
    				    <table width="100%" cellpadding="0" cellspacing="0" class="datatable">
                    	    <tr>
                        	    <th>Application Document #</th>
                                <th>Unapplied Amount</th>
                                <th>Applied Amount</th>
                  		    </tr>
            	            <logic:iterate name="KualiForm" property="nonAppliedControlHoldings" id="nonApplied" indexId="idx">
        	               	    <tr>
    	                   		    <td>
                           			    <bean:write name="nonApplied" property="referenceFinancialDocumentNumber" />
                        		    </td>
                    	   		    <td style="text-align: right;">
                	           		    <bean:write name="nonApplied" property="availableUnappliedAmount" />
            	           		    </td>
                    	   		    <td style="text-align: right;">
                	           		    <bean:write name="nonApplied" property="appliedUnappliedAmount" />
            	           		    </td>
                        	    </tr>
                            </logic:iterate>
           	        	    <tr>
                        	    <kul:htmlAttributeHeaderCell align="right" literalLabel="Total:" />
                        	    <td style="text-align: right;">
                        		    <c:out value="${KualiForm.totalFromControl}" />
    						    </td>
                        	    <td style="text-align: right;">
                        		    <c:out value="${KualiForm.totalApplied}" />
                        	    </td>
                        	</tr>
                        </table>
                      </div>

        	      </c:otherwise>
                </c:choose>
	        </c:when>
        	<c:otherwise>
          	  <div style='text-align: right; margin-top: 20px; padding: 2px 6px; width: 98%;'>
                <style type='text/css'>
                	#ctrl-info th { text-align: right; }
                	#ctrl-info th, #ctrl-info td { width: 50%; }
            	</style>
            	<table id='ctrl-info' width="100%" cellpadding="0" cellspacing="0" class="datatable">
              		<tr>
                		<th>Org Doc #</th>
                		<td><c:out value="${KualiForm.cashControlDocument.documentNumber}" /></td>
              		</tr>
              		<tr>
	                	<th>Customer</th>
                		<td><c:out value="${KualiForm.document.cashControlDetail.customerNumber}" /></td>
              		</tr>
              		<tr>
                		<th>Control Total</th>
                		<td><c:out value="${KualiForm.document.cashControlDetail.financialDocumentLineAmount}" /></td>
              		</tr>
              		<tr>
                		<th>Open Amount</th>
                		<td><c:out value="${KualiForm.unallocatedBalance}" /></td>
              		</tr>
              		<tr>
                		<th>Payment #</th>
                		<td><c:out value="${KualiForm.document.paymentNumber}" />&nbsp;</td>
              		</tr>
            	</table>
              </div>
        	</c:otherwise>
        </c:choose>
    </div>
</kul:tab>
