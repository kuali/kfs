<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<%@ attribute name="customerInvoiceDocumentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="propertyName" required="true"
              description="The DataDictionary entry containing attributes for this row's fields." %>

<tr>
	<td><kul:htmlControlAttribute attributeEntry="${customerInvoiceDocumentAttributes.documentNumber}" property="${propertyName}.documentNumber" readOnly="true" /></td>
	<td><kul:htmlControlAttribute attributeEntry="${customerInvoiceDocumentAttributes.age}" property="${propertyName}.age" readOnly="true" /></td>
	<td><kul:htmlControlAttribute attributeEntry="${customerInvoiceDocumentAttributes.billingDate}" property="${propertyName}.billingDate" readOnly="true" /></td>
	<td><kul:htmlControlAttribute attributeEntry="${customerInvoiceDocumentAttributes.sourceTotal}" property="${propertyName}.sourceTotal" readOnly="true" /></td>
	<td><kul:htmlControlAttribute attributeEntry="${customerInvoiceDocumentAttributes.openAmount}" property="${propertyName}.openAmount" readOnly="true" /></td>
</tr>