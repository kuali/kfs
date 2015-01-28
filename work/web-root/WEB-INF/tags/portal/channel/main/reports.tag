<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<channel:portalChannelTop channelTitle="Reports" />
<div class="body">
	<c:if test="${ConfigProperties.module.accounts.receivable.enabled == 'true'}">
		<strong>Accounts Receivable</strong></br>
	    <ul class="chan">	
			<li><portal:portalLink displayTitle="true" title="Billing Statement" url="${ConfigProperties.application.url}/arCustomerStatement.do?methodToCall=start" /></li>
			<c:if test="${ConfigProperties.contracts.grants.billing.enabled == 'true'}">
				<li><portal:portalLink displayTitle="true"
						title="Collection Activity Report"
						url="${ConfigProperties.application.url}/collectionActivityReportLookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CollectionActivityReport&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
				<li><portal:portalLink displayTitle="true"
						title="Contracts & Grants Aging Report"
						url="${ConfigProperties.application.url}/contractsGrantsAgingReportLookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.ContractsAndGrantsAgingReport&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
				<li><portal:portalLink displayTitle="true"
						title="Contracts & Grants Invoice Document Error Log Report"
						url="${ConfigProperties.application.url}/contractsGrantsInvoiceDocumentErrorLogReport.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDocumentErrorLog&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
			</c:if>
	   		<li><portal:portalLink displayTitle="true" title="Customer Aging Report" url="${ConfigProperties.application.url}/arCustomerAgingReportLookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.CustomerAgingReportDetail" /></li> 		
	   		<li><portal:portalLink displayTitle="true" title="Customer Invoice" url="${ConfigProperties.application.url}/arCustomerInvoice.do?methodToCall=start" /></li>
			<c:if test="${ConfigProperties.contracts.grants.billing.enabled == 'true'}">
				<li><portal:portalLink displayTitle="true"
						title="Federal Financial Report"
						url="${ConfigProperties.application.url}/arFederalFinancialReport.do?methodToCall=start" /></li>
				<li><portal:portalLink displayTitle="true"
						title="Outstanding Invoice Report"
						url="${ConfigProperties.application.url}/contractsGrantsInvoiceReport.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceReport&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
				<li><portal:portalLink displayTitle="true"
						title="Letter of Credit Draw Report"
						url="${ConfigProperties.application.url}/contractsGrantsLOCReport.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.ContractsGrantsLOCReport&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
				</li>
				<li><portal:portalLink displayTitle="true"
						title="Milestone Report"
						url="${ConfigProperties.application.url}/contractsGrantsMilestoneReport.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.ContractsGrantsMilestoneReport&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
				</li>
				<li><portal:portalLink displayTitle="true"
						title="Payment History Report"
						url="${ConfigProperties.application.url}/contractsGrantsPaymentHistoryReport.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.ContractsGrantsPaymentHistoryReport&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
				</li>
				<li><portal:portalLink displayTitle="true"
						title="Suspended Invoice Detail Report"
						url="${ConfigProperties.application.url}/contractsGrantsSuspendedInvoiceDetailReport.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.ContractsGrantsSuspendedInvoiceDetailReport&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
				</li>
				<li><portal:portalLink displayTitle="true"
						title="Suspended Invoice Summary Report"
						url="${ConfigProperties.application.url}/contractsGrantsSuspendedInvoiceSummaryReport.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.ContractsGrantsSuspendedInvoiceSummaryReport&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
				</li>
				<li><portal:portalLink displayTitle="true"
						title="Tickler"
						url="${ConfigProperties.application.url}/arTicklersReport.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ar.businessobject.TicklersReport&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" /></li>
			</c:if>
		</ul>
	</c:if>
	<c:if
		test="${ConfigProperties['module.contracts.and.grants.enabled'] == 'true' && ConfigProperties.contracts.grants.billing.enabled == 'true'}">
		<strong>Contracts & Grants</strong>
		</br>
		<ul class="chan">
			<li><portal:portalLink displayTitle="true"
					title="Award Balances"
					url="${ConfigProperties.application.url}/contractsGrantsAwardBalancesReport.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.cg.businessobject.ContractsGrantsAwardBalancesReport&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>
		</ul>
	</c:if>
	<c:if test="${ConfigProperties.module.labor.distribution.enabled == 'true'}">
		<strong>Effort Certification</strong></br>
	    <ul class="chan">	
			<li>
				<portal:portalLink displayTitle="true" title="Duplicate Certifications Report"
					url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ec.businessobject.DuplicateCertificationsReport&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>
			
			<li>
				<portal:portalLink displayTitle="true" title="Effort Certification Extract Build"
					url="${ConfigProperties.application.url}/${KFSConstants.GL_MODIFIED_INQUIRY_ACTION}?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ec.businessobject.EffortCertificationDetailBuild&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>
			
			<li>
				<portal:portalLink displayTitle="true" title="Outstanding Certifications by Chart/Org/Report"
					url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ec.businessobject.OutstandingCertificationsByOrganization&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>
			
			<li>
				<portal:portalLink displayTitle="true" title="Outstanding Certifications By Report"
					url="${ConfigProperties.application.url}/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.module.ec.businessobject.OutstandingCertificationsByReport&docFormKey=88888888&returnLocation=${ConfigProperties.application.url}/portal.do&hideReturnLink=true" />
			</li>
		</ul>
	</c:if>
	<c:if test="${ConfigProperties.module.endowment.enabled == 'true'}">
		<strong>Endowments</strong></br>
	    <ul class="chan">	
			<li><portal:portalLink displayTitle="true" title="Asset Statement" url="${ConfigProperties.application.url}/reportEndowAssetStatement.do?methodToCall=start"/></li>
			<li><portal:portalLink displayTitle="true" title="Transaction Statement" url="${ConfigProperties.application.url}/reportEndowTransactionStatement.do?methodToCall=start"/></li>
			<li><portal:portalLink displayTitle="true" title="Transaction Summary" url="${ConfigProperties.application.url}/reportEndowTransactionSummary.do?methodToCall=start"/></li>		
			<li><portal:portalLink displayTitle="true" title="Trial Balance" url="${ConfigProperties.application.url}/reportEndowTrialBalance.do?methodToCall=start"/></li>
		</ul>
	</c:if>
	<strong>General Ledger</strong></br>
	<ul class="chan">
		<li><portal:portalLink displayTitle="true" title="Trial Balance" url="${ConfigProperties.application.url}/glTrialBalance.do?methodToCall=start&businessObjectClassName=org.kuali.kfs.gl.businessobject.TrialBalanceReport&docFormKey=88888888&returnLocation=portal.do&hideReturnLink=true" /></li>
	</ul>
</div>
<channel:portalChannelBottom />
						
						
						
						
						
						
