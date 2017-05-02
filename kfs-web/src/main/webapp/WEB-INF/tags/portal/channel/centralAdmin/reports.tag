<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<channel:portalChannelTop channelTitle="Reports" />
<div class="body">
	<c:if test="${ConfigProperties.module.accounts.receivable.enabled == 'true'}">
		<strong>Accounts Receivable</strong></br>
	    <ul class="chan">	
			<li><portal:portalLink displayTitle="true" title="Billing Statement" url="${ConfigProperties.application.url}/arCustomerStatement.do?methodToCall=start" /></li>
	   		<li><portal:portalLink displayTitle="true" title="Customer Aging Report" url="${ConfigProperties.application.url}/arCustomerAgingReportLookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerAgingReportDetail" /></li> 		
	   		<li><portal:portalLink displayTitle="true" title="Customer Invoice" url="${ConfigProperties.application.url}/arCustomerInvoice.do?methodToCall=start" /></li>
		</ul>
	</c:if>
	<c:if test="${ConfigProperties.module.labor.distribution.enabled == 'true'}">
		<strong>Effort Certification</strong></br>
	    <ul class="chan">	
			<li><portal:portalLink displayTitle="true" title="Duplicate Certifications Report" url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ec.businessobject.DuplicateCertificationsReport&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
			<li><portal:portalLink displayTitle="true" title="Effort Certification Extract Build" url="${ConfigProperties.application.url}/${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ec.businessobject.EffortCertificationDetailBuild&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
		</ul>
	</c:if>
	<strong>General Ledger</strong></br>
	<ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Trial Balance" url="${ConfigProperties.application.url}/glTrialBalance.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.gl.businessobject.TrialBalanceReport&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	</ul>

    <strong>Check Reconciliation</strong>
    <ul class="chan">
        <li><portal:portalLink displayTitle="true" title="Check Reconciliation Report" url="${ConfigProperties.application.url}/crCheckReconciliationReport.do" /></li>
    </ul>

</div>
<channel:portalChannelBottom />
