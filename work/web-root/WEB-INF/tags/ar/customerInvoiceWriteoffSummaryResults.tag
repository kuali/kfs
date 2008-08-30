<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<logic:iterate id="customerInvoiceWriteoffLookupResult" name="KualiForm"
	property="customerInvoiceWriteoffLookupResults" indexId="ctr">
	<ar:customerInvoiceWriteoffSummaryResult
		propertyName="customerInvoiceWriteoffLookupResults[${ctr}]"
		/>
	</br>
</logic:iterate>