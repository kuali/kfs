<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<%@ attribute name="customerInvoiceDocumentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>

<%@ attribute name="propertyName" required="true"
              description="The DataDictionary entry containing attributes for this row's fields." %>
<%@ attribute name="tabTitle" required="true"%>
<%@ attribute name="useTabTop" required="true"%>
<c:set var="tabErrorKey" value="${propertyName}.*" />

<div id="workarea">
<c:choose>
	<c:when test="${useTabTop}">
		<kul:tabTop tabTitle="${tabTitle}" defaultOpen="true" tabErrorKey="${tabErrorKey}">
			<ar:customerInvoiceWriteoffSummaryResultContent customerInvoiceDocumentAttributes="${customerInvoiceDocumentAttributes}" propertyName="${propertyName}"/>
		</kul:tabTop>
	</c:when>
	<c:otherwise>
		<kul:tab tabTitle="${tabTitle}" defaultOpen="true" tabErrorKey="${tabErrorKey}" >
			<ar:customerInvoiceWriteoffSummaryResultContent customerInvoiceDocumentAttributes="${customerInvoiceDocumentAttributes}" propertyName="${propertyName}"/>
		</kul:tab>
	</c:otherwise>
</c:choose>
</div>