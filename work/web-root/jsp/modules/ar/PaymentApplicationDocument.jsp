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

<c:set var="readOnly" value="${!empty KualiForm.editingMode['viewOnly']}" />
<c:set var="paymentApplicationDocumentAttributes" value="${DataDictionary['PaymentApplicationDocument'].attributes}" />
<c:set var="invoiceAttributes" value="${DataDictionary['CustomerInvoiceDocument'].attributes}" />
<c:set var="customerAttributes" value="${DataDictionary['Customer'].attributes}" />
<c:set var="readOnly" value="${!empty KualiForm.editingMode['viewOnly']}" />

<kul:documentPage showDocumentInfo="true"
  documentTypeName="PaymentApplicationDocument"
  htmlFormAction="arPaymentApplicationDocument" renderMultipart="true"
  showTabButtons="true">

  <kul:hiddenDocumentFields />

  <kul:documentOverview editingMode="${KualiForm.editingMode}" />

  <kul:tab tabTitle="Control Information" defaultOpen="true"
    tabErrorKey="${KFSConstants.CASH_CONTROL_DOCUMENT_ERRORS}">
    
    <div class="tab-container" align="center">
    	
    	<c:choose>
    		<c:when test="${null == document.accountsReceivableDocumentHeader}">
    			No related Cash Control Document.
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
		          	<td><input type='text' name=''></td>
		          </tr>
		          <tr>
		          	<th>Customer</th>
		          	<td>&nbsp;</td>
		          </tr>
		          <tr>
		          	<th>Control Total</th>
		          	<td>&nbsp;</td>
		          </tr>
		          <tr>
		          	<th>Balance</th>
		          	<td>&nbsp;</td>
		          </tr>
		          <tr>
		          	<th>Payment #</th>
		          	<td>&nbsp;</td>
		          </tr>
		        </table>
		      </div>
      		</c:otherwise>
    	</c:choose>
   </div>
  </kul:tab>
  
  <script type='text/javascript'>
    function toggle(id) {
      var v=document.getElementById(id); 
      if('none' != v.style.display) {
        v.style.display='none';
      } else {
        v.style.display='';
      }
    }
  </script>

  <kul:tab tabTitle="Summary of Applied Funds" defaultOpen="true"
    tabErrorKey="${KFSConstants.PAYMENT_APPLICATION_DOCUMENT_ERRORS}">
    
    <div class="tab-container" align="center">
    
    	<c:choose>
	    	<c:when test="${empty KualiForm.document.appliedPayments}">
	    		No applied payments.
	    	</c:when>
	    	<c:otherwise>
				<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
					<tr>
						<th>Invoice #</th>
						<td>A12345678</td>
						<th>Item #</th>
						<td>1</td>
						<th>Description</th>
						<td>Foo</td>
						<th>Applied Amount</th>
						<td>$1234.00</td>
					</tr>
				</table>
	      	</c:otherwise>
	    </c:choose>
    </div>
  </kul:tab>
  
  <kul:tab tabTitle="Quick Apply" defaultOpen="true"
    tabErrorKey="${KFSConstants.PAYMENT_APPLICATION_DOCUMENT_ERRORS}">
    
    <div class="tab-container" align="center">
    
    <table width="100%" cellpadding="0" celspacing="0" class="datatable">
    	
    	<c:forEach items="${KualiForm.invoices}" var="invoice">
    	
    		<tr>
    			<td><c:out value="${invoice.documentNumber}"/></td>
    			<td><input type="checkbox" name="quickApply" value="${invoice.documentNumber}" /></td>
    		</tr>
    	
    	</c:forEach>
    	
    	<tr>
    		<td colspan='2' style='text-align: right;'>
    			<input type='submit' value='Quick Apply' />
    		</td>
    	</tr>
    	
    </table>
    
	</div>
	
  </kul:tab>
  
  <kul:tab tabTitle="Application Details" defaultOpen="true"
    tabErrorKey="${KFSConstants.PAYMENT_APPLICATION_DOCUMENT_ERRORS}">

    <div class="tab-container" align="center">
    
	<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
        <tr>
        	<th>Customer</th>
			<td>
				<kul:htmlControlAttribute
					attributeEntry="${customerAttributes.customerNumber}"
					property="customerNumber"
					readOnly="${readOnly}" />
				<kul:lookup boClassName="org.kuali.module.ar.bo.Customer" 
					fieldConversions="customerNumber:customer.customerNumber" />
			</td>
		</tr>
        <tr>
        	<th width='50%'>Invoice</th>
			<td>
				<kul:htmlControlAttribute
					attributeEntry="${invoiceAttributes.organizationInvoiceNumber}"
					property="selectedInvoiceDocumentNumber"
					readOnly="${readOnly}" />
			</td>
		</tr>
		<tr>
			<td colspan='2'>
				<center>
					<html:image property="methodToCall.setCustomer"
						src="${ConfigProperties.externalizable.images.url}tinybutton-load.gif"
						alt="Load Invoices"
						title="Load Invoices"
						styleClass="tinybutton" />
				</center>
			</td>
		</tr>
	</table>
	
	<c:choose>
      	<c:when test="${null == KualiForm.customer}"></c:when>
      	<c:otherwise>
			<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
			  <tr id='beta_zeta'>
			    <td>
			      <table width="100%" cellpadding="0" cellspacing="0" class="datatable">
			        <tr>
			          <td style='vertical-align: top;'>
			            <table width="100%" cellpadding="0" cellspacing="0" class="datatable">
			              <tr>
			                <td colspan='4' class='tab-subhead'>Unapplied Funds</td>
			              </tr>
			              <tr>
			                <th>Sequence</th>
			                <th>Doc Nbr</th>
			                <th>Type</th>
			                <th>Amount Available</th>
			              </tr>
			              <c:forEach items="${KualiForm.nonAppliedHoldingsForCustomer}" var="holding">
				              <tr>
				              	<td>&nbsp;</td>
				              	<td><c:out value="${holding.referenceFinancialDocumentNumber}"/></td>
				              	<td>&nbsp;</td>
				              	<td><c:out value="${holding.financialDocumentLineAmount}"/></td>
				              </tr>
			              </c:forEach>
			            </table>
			          </td>
			          <td style="vertical-align: top;">
			            <table class='datatable'>
			              <tr>
			                <th class='tab-subhead'>Cash Control</th>
			              </tr>
			              <tr>
			                <td><input type='text'/></td>
			              </tr>
			              <tr>
			                <th class='tab-subhead'>Non-AR</th>
			              </tr>
			              <tr>
			                <td><input type='text'/></td>
			              </tr>
			            </table>
			          </td>
			        </tr>
			        <tr>
			          <td colspan='2' class='tab-subhead'>
			            Invoices
			            <select>
			            	<c:forEach items="${KualiForm.invoices}" var="invoice">
			            		<option><c:out value="${invoice.documentNumber}" /></option>
			            	</c:forEach>
			            </select>
			            <input type='submit' value='Go To Invoice'>
			          </td>
			        </tr>
			        <tr>
			        	<th colspan='2' class='tab-subhead'>
			        		<c:out value="Invoice ${KualiForm.selectedInvoiceDocumentNumber}" />
			        		<c:if test="${!empty KualiForm.previousInvoiceDocumentNumber}">
								<c:out escapeXml="false" value="<a href='arPaymentApplicationDocument.do?methodToCall=setCustomer&selectedInvoiceDocumentNumber=${KualiForm.previousInvoiceDocumentNumber}&document.documentNumber=${KualiForm.document.documentNumber}'><- Previous Invoice</a>" />
				        	</c:if>
				        	<c:if test="${!empty KualiForm.previousInvoiceDocumentNumber && !empty KualiForm.nextInvoiceDocumentNumber}">|</c:if>
			        		<c:if test="${!empty KualiForm.nextInvoiceDocumentNumber}">
								<c:out escapeXml="false" value="<a href='arPaymentApplicationDocument.do?methodToCall=setCustomer&selectedInvoiceDocumentNumber=${KualiForm.nextInvoiceDocumentNumber}&document.documentNumber=${KualiForm.document.documentNumber}'>Next Invoice -></a>" />
				        	</c:if>
			        	</th>
			        </tr>
			        <tr>
			          <td colspan='2'>
			            <table width='100%' cellpadding='0' cellspacing='0' class='datatable'>
			              <tr>
			                <th>Sequence</th>
			                <th>Invoice Number</th>
			                <th>Invoice Header/Custom Name</th>
			                <th>Balance/Total</th>
			                <th>Apply Amount</th>
			              </tr>
			              <tr>
			                <td>01</td>
			                <td>AB1234567</td>
			                <td><input type='text' value='${KualiForm.selectedInvoiceDocument.accountsReceivableDocumentHeader.financialDocumentExplanationText}'></td>
			                <td><input type='text' value=''></td>
			                <td rowspan='2'><input type='text' value=''/></td>
			              </tr>
			              <tr>
			                <td>&nbsp;</td>
			                <td>&nbsp;</td>
			                <td><input type='text' value='ABBOTT NORTHWESTERN H'></td>
			                <td><input type='text' value='115.00'></td>
			              </tr>
			              <tr>
			                <td colspan='5' class='tab-subhead'>Invoice Detail</td>
			              </tr>
			              <tr>
			                <td colspan='5'>
			                  <table width='100%' cellpadding='0' cellspacing='0' class='datatable'>
			                    <tr>
			                      <th>&nbsp;</th>
			                      <th>Chart</th>
			                      <th>Account</th>
			                      <th>Item Desc</th>
			                      <th>Item Tot Amt</th>
			                      <th>Dtl Balance</th>
			                      <th>Apply Amount</th>
			                    </tr>
			                    <tr>
			                      <td><input type='text' size='2' value='1'></td>
			                      <td>BL</td>
			                      <td>1043200</td>
			                      <td><input type='text' value='Seminar for Nurse'></td>
			                      <td><input type='text' value='100.00'></td>
			                      <td><input type='text' value='100.00'></td>
			                      <td><input type='text' value=''></td>
			                    </tr>
			                    <tr>
			                      <td><input type='text' size='2' value='2'></td>
			                      <td>BL</td>
			                      <td>1043200</td>
			                      <td><input type='text' value='Books for Seminar'></td>
			                      <td><input type='text' value='15.00'></td>
			                      <td><input type='text' value='15.00'></td>
			                      <td><input type='text' value=''></td>
			                    </tr>
			                  </table>
			                </td>
			              </tr>
			              <tr>
			              	<td style='text-align: right;' colspan='5'>
				              	<input type='text' value='Apply Amounts to Invoice' />
			              	</td>
			              </tr>
			            </table>
			          </td>
			        </tr>
			      </table>
			    </td>
			  </tr>
			  <tr style='display: none;'>
			    <td class="subhead">
			      <span class="subhead-left">VELIZ COMPANY (ALA17234)</span>
			      <span class="subhead-right">
			        <c:set var="toggle" value="hide"/>
			        <html:image property="methodToCall.${toggle}Details" 
			          src="${ConfigProperties.kr.externalizable.images.url}det-${toggle}.gif"
			          onclick="toggle('veliz'); return false;" alt="${toggle} transaction details" 
			          title="${toggle} transaction details" styleClass="tinybutton"/>
			      </span>
			    </td>
			  </tr>
			  <tr id='veliz' style='display: none;'>
			    <td>
			      <table width="100%" cellpadding="0" cellspacing="0" class="datatable">
			        <tr>
			          <td style='vertical-align: top;'>
			            <table width="100%" cellpadding="0" cellspacing="0" class="datatable">
			              <tr>
			                <td colspan='4' class='tab-subhead'>Unapplied</td>
			              </tr>
			              <tr>
			                <th>Sequence</th>
			                <th>Doc Nbr</th>
			                <th>Type</th>
			                <th>Amount Available</th>
			              </tr>
			            </table>
			          </td>
			          <td>
			            <table class='datatable'>
			              <tr>
			                <th class='tab-subhead'>Cash Control</th>
			              </tr>
			              <tr>
			                <td><input type='text'/></td>
			              </tr>
			              <tr>
			                <th class='tab-subhead'>Non-AR</th>
			              </tr>
			              <tr>
			                <td><input type='text'/></td>
			              </tr>
			            </table>
			          </td>
			        </tr>
			        <tr>
			          <td colspan='2' class='tab-subhead'>
			            Invoice
			          </td>
			        </tr>
			        <tr>
			          <td colspan='2'>
			            <table width='100%' cellpadding='0' cellspacing='0' class='datatable'>
			              <tr>
			                <th>Sequence</th>
			                <th>Invoice Number</th>
			                <th>Invoice Header/Custom Name</th>
			                <th>Balance/Total</th>
			              </tr>
			              <tr>
			                <td>01</td>
			                <td>AB1234567</td>
			                <td><input type='text' value='INVOICE HEADER TEXT'></td>
			                <td><input type='text' value='115.00'></td>
			              </tr>
			              <tr>
			                <td>&nbsp;</td>
			                <td>&nbsp;</td>
			                <td><input type='text' value='ABBOTT NORTHWESTERN H'></td>
			                <td><input type='text' value='115.00'></td>
			              </tr>
			              <tr>
			                <td colspan='4' class='tab-subhead'>Invoice Detail</td>
			              </tr>
			              <tr>
			                <td colspan='4'>
			                  <table width='100%' cellpadding='0' cellspacing='0' class='datatable'>
			                    <tr>
			                      <th>&nbsp;</th>
			                      <th>Chart</th>
			                      <th>Account</th>
			                      <th>Item Desc</th>
			                      <th>Item Tot Amt</th>
			                      <th>Dtl Balance</th>
			                    </tr>
			                    <tr>
			                      <td><input type='text' size='2' value='1'></td>
			                      <td>BL</td>
			                      <td>1043200</td>
			                      <td><input type='text' value='Seminar for Nurse'></td>
			                      <td><input type='text' value='100.00'></td>
			                      <td><input type='text' value='100.00'></td>
			                    </tr>
			                    <tr>
			                      <td><input type='text' size='2' value='2'></td>
			                      <td>BL</td>
			                      <td>1043200</td>
			                      <td><input type='text' value='Books for Seminar'></td>
			                      <td><input type='text' value='15.00'></td>
			                      <td><input type='text' value='15.00'></td>
			                    </tr>
			                  </table>
			                </td>
			              </tr>
			            </table>
			          </td>
			        </tr>
			      </table>
			    </td>
			  </tr>
			</table>
	      </c:otherwise>
      </c:choose>
	</div>
  </kul:tab>

  <kul:tab tabTitle="Non-AR" defaultOpen="true"
    tabErrorKey="${KFSConstants.PAYMENT_APPLICATION_DOCUMENT_ERRORS}">
    
    <div class="tab-container" align="center">
      <table width="100%" cellpadding="0" cellspacing="0" class="datatable">
        <tr>
        	<th>Chart</th>
        	<th>Account</th>
        	<th>Sacc</th>
        	<th>Object</th>
        	<th>Sobj</th>
        	<th>Project</th>
        	<th>Amount</th>
        </tr>
        <tr>
        	<td><input type='text' size=''></td>
        	<td><input type='text' size=''></td>
        	<td><input type='text' size=''></td>
        	<td><input type='text' size=''></td>
        	<td><input type='text' size=''></td>
        	<td><input type='text' size=''></td>
        	<td><input type='text' size=''></td>
        </tr>
        <tr>
        	<td colspan='5'>&nbsp;</td>
        	<th>Non-AR Total</th>
        	<td> <input type='text' name='nonartotal'></td>
        </tr>
      </table>
    </div>
    
  </kul:tab>

  <kul:tab tabTitle="Unapplied" defaultOpen="true"
    tabErrorKey="${KFSConstants.CASH_CONTROL_DOCUMENT_ERRORS}">
    <div class="tab-container" align="center">
      <table width="100%" cellpadding="0" cellspacing="0" class="datatable">
        <tr>
        	<th><label for=''>Customer</label></th>
        	<td><select>
        		<option value='0'>Some Really Long Customer Name 0</option>
        		<option value='1'>Some Really Long Customer Name 1</option>
        		<option value='2'>Some Really Long Customer Name 2</option>
        		<option value='3'>Some Really Long Customer Name 3</option>
        		<option value='4'>Some Really Long Customer Name 4</option>
        	</select></td>
        	<th><label for=''>Amount</label></th>
        	<td><input type='text' name='' value=''></td>
    	</tr>
	  </table>
    </div>
  </kul:tab>

  <kul:notes notesBo="${KualiForm.document.documentBusinessObject.boNotes}" 
    noteType="${Constants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE}"  allowsNoteFYI="true"/> 

  <kul:adHocRecipients />

  <kul:routeLog />

  <kul:panelFooter />

  <kul:documentControls transactionalDocument="true" />

</kul:documentPage>
