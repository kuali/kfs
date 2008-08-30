<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<%@ attribute name="propertyName" required="true"
	description="Property name of the CustomerInvoiceWriteLookupResult for this tab" %>
	
<kul:htmlControlAttribute attributeEntry="${DataDictionary.CustomerInvoiceLookupResults.attributes.customerNumber}" property="${propertyName}.customerNumber" readOnly="${true}" />