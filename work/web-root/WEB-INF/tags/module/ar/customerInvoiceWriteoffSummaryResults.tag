<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="customerInvoiceDocumentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>

<logic:iterate id="customerInvoiceWriteoffLookupResult" name="KualiForm"
	property="customerInvoiceWriteoffLookupResults" indexId="ctr">
	
	<c:set var="useTabTop" value="${ctr == 0}" />
	<c:set var="tabTitle" value="${KualiForm.customerInvoiceWriteoffLookupResults[ctr].customerNumber}, ${KualiForm.customerInvoiceWriteoffLookupResults[ctr].customerName}" />
	<ar:customerInvoiceWriteoffSummaryResult
		propertyName="customerInvoiceWriteoffLookupResults[${ctr}]"
		customerInvoiceDocumentAttributes="${customerInvoiceDocumentAttributes}"
		tabTitle="${tabTitle}"
		useTabTop="${useTabTop}"
		/>
</logic:iterate>

