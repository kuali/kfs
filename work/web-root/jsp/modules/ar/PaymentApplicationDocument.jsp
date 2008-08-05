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

<c:set var="readOnly"
	value="${!empty KualiForm.editingMode['viewOnly']}" />
<c:set var="paymentApplicationDocumentAttributes"
	value="${DataDictionary['PaymentApplicationDocument'].attributes}" />
<c:set var="invoiceAttributes"
	value="${DataDictionary['CustomerInvoiceDocument'].attributes}" />
<c:set var="invoicePaidAppliedAttributes"
	value="${DataDictionary['InvoicePaidApplied'].attributes}" />
<c:set var="customerAttributes"
	value="${DataDictionary['Customer'].attributes}" />
<c:set var="customerInvoiceDetailAttributes"
	value="${DataDictionary['CustomerInvoiceDetail'].attributes}" />
<c:set var="unappliedAttributes"
	value="${DataDictionary['NonAppliedHolding'].attributes}" />
<c:set var="readOnly"
	value="${!empty KualiForm.editingMode['viewOnly']}" />
<c:set var="hasRelatedCashControlDocument" value="false" />
<c:set var="isCustomerSelected"
	value="${!empty KualiForm.document.accountsReceivableDocumentHeader.customerNumber}" />
<c:set var="invoices" value="${KualiForm.invoices}" />

<kul:documentPage showDocumentInfo="true"
	documentTypeName="PaymentApplicationDocument"
	htmlFormAction="arPaymentApplicationDocument" renderMultipart="true"
	showTabButtons="true">

	<kfs:hiddenDocumentFields isFinancialDocument="false" />

	<kfs:documentOverview editingMode="${KualiForm.editingMode}" />

	<kul:tab tabTitle="Control Information"
		defaultOpen="${hasRelatedCashControlDocument}"
		tabErrorKey="${KFSConstants.CASH_CONTROL_DOCUMENT_ERRORS}">

		<div class="tab-container" align="center">

			<c:choose>
				<c:when test="${!hasRelatedCashControlDocument}">
    			No related Cash Control Document.
    		</c:when>
				<c:otherwise>
					<div
						style='text-align: right; margin-top: 20px; padding: 2px 6px; width: 98%;'>
						<style type='text/css'>
		      		#ctrl-info th { text-align: right; }
		      		#ctrl-info th, #ctrl-info td { width: 50%; }
		      	</style>
						<table id='ctrl-info' width="100%" cellpadding="0" cellspacing="0"
							class="datatable">
							<tr>
								<th>
									Org Doc #
								</th>
								<td>
									TODO
								</td>
							</tr>
							<tr>
								<th>
									Customer
								</th>
								<td>
									TODO
								</td>
							</tr>
							<tr>
								<th>
									Control Total
								</th>
								<td>
									TODO
								</td>
							</tr>
							<tr>
								<th>
									Balance
								</th>
								<td>
									TODO
								</td>
							</tr>
							<tr>
								<th>
									Payment #
								</th>
								<td>
									TODO
								</td>
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

	<ar:paymentApplicationSummaryOfAppliedFunds readOnly="${readOnly}" />

	<kul:tab tabTitle="Quick Apply to Invoice"
		defaultOpen="${isCustomerSelected}"
		tabErrorKey="${KFSConstants.PAYMENT_APPLICATION_DOCUMENT_ERRORS}">
		<div class="tab-container" align="center">

			<c:choose>
				<c:when
					test="${null == KualiForm.document.accountsReceivableDocumentHeader.customerNumber}">
		      		No Customer Selected
		      	</c:when>
				<c:otherwise>
					<table width="100%" cellpadding="0" cellspacing="0"
						class="datatable">
						<tr>
							<th>
								Invoice Number
							</th>
							<th>
								Open Amount
							</th>
							<th>
								Quick Apply
							</th>
						</tr>
						<c:forEach items="${KualiForm.updatedBalanceInvoices}"
							var="updatedBalanceInvoice">
							<tr>
								<td>
									<c:out value="${updatedBalanceInvoice.invoice.documentNumber}" />
								</td>
								<td>
									$
									<c:out value="${updatedBalanceInvoice.openAmount}" />
								</td>
								<td>
									<input type="checkbox" name="quickApply"
										value="${updatedBalanceInvoice.invoice.documentNumber}" />
								</td>
							</tr>
						</c:forEach>
						<tr>
							<td colspan='3' style='text-align: right;'>
								<html:image property="methodToCall.quickApply"
									src="${ConfigProperties.externalizable.images.url}tinybutton-load.gif"
									alt="Quick Apply" title="Quick Apply" styleClass="tinybutton" />
							</td>
						</tr>
					</table>
				</c:otherwise>
			</c:choose>
		</div>
	</kul:tab>

	<ar:paymentApplicationApplyToInvoiceDetail
		customerAttributes="${customerAttributes}"
		customerInvoiceDetailAttributes="${customerInvoiceDetailAttributes}"
		invoiceAttributes="${invoiceAttributes}" readOnly="${readOnly}" />


	<kul:tab tabTitle="Non-AR" defaultOpen="true"
		tabErrorKey="${KFSConstants.PAYMENT_APPLICATION_DOCUMENT_ERRORS}">

		<div class="tab-container" align="center">
			<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
				<tr>
					<th>
						Chart
					</th>
					<th>
						Account
					</th>
					<th>
						Sacc
					</th>
					<th>
						Object
					</th>
					<th>
						Sobj
					</th>
					<th>
						Project
					</th>
					<th>
						Amount
					</th>
					<th>
						Action
					</th>
				</tr>
				<tr>
					<td>
						<input type='text' size=''>
					</td>
					<td>
						<input type='text' size=''>
					</td>
					<td>
						<input type='text' size=''>
					</td>
					<td>
						<input type='text' size=''>
					</td>
					<td>
						<input type='text' size=''>
					</td>
					<td>
						<input type='text' size=''>
					</td>
					<td>
						<input type='text' size=''>
					</td>
					<td>
						<html:image property="methodToCall.addNonAr"
							src="${ConfigProperties.externalizable.images.url}tinybutton-add1.gif"
							alt="Add" title="Add" styleClass="tinybutton" />
					</td>
				</tr>
				<tr>
					<td colspan='5'>
						&nbsp;
					</td>
					<th>
						Non-AR Total
					</th>
					<td>
						<input type='text' name='nonartotal'>
					</td>
					<td>
						&nbsp;
					</td>
				</tr>
			</table>
		</div>
	</kul:tab>

	<kul:tab tabTitle="Unapplied" defaultOpen="true"
		tabErrorKey="${KFSConstants.CASH_CONTROL_DOCUMENT_ERRORS}">
		<div class="tab-container" align="center">
			<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
				<tr>
					<th>
						<label for=''>
							Customer
						</label>
					</th>
					<td>
						<kul:htmlControlAttribute
							attributeEntry="${customerAttributes.customerNumber}"
							property="document.nonAppliedHolding.customerNumber" />
						<kul:lookup
							boClassName="org.kuali.kfs.module.ar.businessobject.Customer"
							fieldConversions="document.nonAppliedHolding.customerNumber:customer.customerNumber" />
					</td>
					<th>
						<label for=''>
							Amount
						</label>
					</th>
					<td>
						<kul:htmlControlAttribute
							attributeEntry="${unappliedAttributes.financialDocumentLineAmount}"
							property="document.nonAppliedHolding.financialDocumentLineAmount"
							readOnly="${readOnly}" />
					</td>
				</tr>
			</table>
		</div>
	</kul:tab>

	<kul:notes
		notesBo="${KualiForm.document.documentBusinessObject.boNotes}"
		noteType="${Constants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE}"
		allowsNoteFYI="true" />

	<kul:adHocRecipients />

	<kul:routeLog />

	<kul:panelFooter />

	<kfs:documentControls transactionalDocument="true" />

</kul:documentPage>
