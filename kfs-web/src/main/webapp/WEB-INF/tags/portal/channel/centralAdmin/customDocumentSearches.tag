<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<channel:portalChannelTop channelTitle="Custom Document Searches" />
<div class="body">
	<c:if test="${ConfigProperties.module.accounts.receivable.enabled == 'true'}">
	 	<strong>Accounts Receivable</strong><br/>
	    <ul class="chan">
	        <li><portal:portalLink displayTitle="true" title='Cash Controls' url='${ConfigProperties.workflow.documentsearch.base.url}&documentTypeName=CTRL'/></li>
			<c:if test="${ConfigProperties.contracts.grants.billing.enabled == 'true'}">
		        <li><portal:portalLink displayTitle="true" title='Contracts & Grants Invoices' url='${ConfigProperties.workflow.documentsearch.base.url}&documentTypeName=CINV'/></li>
			</c:if>
	        <li><portal:portalLink displayTitle="true" title='Customer Credit Memos' url='${ConfigProperties.workflow.documentsearch.base.url}&documentTypeName=CRM'/></li>
	        <li><portal:portalLink displayTitle="true" title='Customer Invoices' url='${ConfigProperties.workflow.documentsearch.base.url}&documentTypeName=INV'/></li>
	        <li><portal:portalLink displayTitle="true" title='Customer Invoice Writeoffs' url='${ConfigProperties.workflow.documentsearch.base.url}&documentTypeName=INVW'/></li>
	        <li><portal:portalLink displayTitle="true" title='Payment Applications' url='${ConfigProperties.workflow.documentsearch.base.url}&documentTypeName=APP'/></li>
	    </ul>
	</c:if>
	<c:if test="${ConfigProperties.module.purchasing.enabled == 'true'}">
		<strong>Purchasing/Accounts Payable</strong><br/>
	    <ul class="chan">
	        <li><portal:portalLink displayTitle="true" title='Electronic Invoice Rejects' url='${ConfigProperties.workflow.documentsearch.base.url}&documentTypeName=EIRT'/></li>
	     </ul>
	</c:if>
</div>
<channel:portalChannelBottom />
