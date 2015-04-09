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

<c:set var="balanceInquiryAttributes"
	value="${DataDictionary.LedgerBalanceForBenefitExpenseTransfer.attributes}" />

<c:set var="readOnly"
	value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] || !KualiForm.editingMode['ledgerBalanceImporting']}"/>	

<!-- accountsChartReadOnly is set to false even when account can cross indicator = Y because if it 
read only then the javascript can not write value to the field. -->
<c:set var="accountsChartsReadOnly" value="${readOnly}"/>
<c:if test="${not readOnly}">
	<c:set var="accountsChartsReadOnly" value="false"/>
</c:if>	
							    
<c:set var="documentTypeName" value="BenefitExpenseTransferDocument"/>
<c:set var="htmlFormAction" value="laborBenefitExpenseTransfer"/>

<c:if test="${isYearEnd}">
  <c:set var="documentTypeName" value="YearEndBenefitExpenseTransferDocument"/>
  <c:set var="htmlFormAction" value="laborYearEndBenefitExpenseTransfer"/>
</c:if>


<kul:documentPage showDocumentInfo="true"
    documentTypeName="${documentTypeName}"
    htmlFormAction="${htmlFormAction}" renderMultipart="true"
    showTabButtons="true">

	<sys:documentOverview editingMode="${KualiForm.editingMode}" />
	
	<kul:tab tabTitle="Ledger Balance Importing" defaultOpen="true"
		tabErrorKey="${KFSConstants.EMPLOYEE_LOOKUP_ERRORS}">
		<div class="tab-container" align=center>
		<h3>Ledger Balance Importing</h3>
	
		<table cellpadding="0" cellspacing="0" class="datatable"
			summary="Ledger Balance Importing">

			<tr>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${balanceInquiryAttributes.universityFiscalYear}"
					horizontal="true" width="35%"  labelFor="universityFiscalYear" forceRequired="true"/>

				<td class="datacell-nowrap"><kul:htmlControlAttribute
					attributeEntry="${balanceInquiryAttributes.universityFiscalYear}"
					property="universityFiscalYear" forceRequired="true" readOnly="${readOnly}" /> 
					<c:if test="${!readOnly}">
						<!-- KULLAB-704 Force the field conversions. -->	
						<kul:lookup	boClassName="org.kuali.kfs.sys.businessobject.SystemOptions"						
						lookupParameters="universityFiscalYear:universityFiscalYear" 
						fieldConversions="universityFiscalYear:universityFiscalYear"
						fieldLabel="${balanceInquiryAttributes.universityFiscalYear.label}" />
					</c:if>
				</td>
			</tr>	
													
			<tr>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${balanceInquiryAttributes.chartOfAccountsCode}"
					horizontal="true" labelFor="chartOfAccountsCode" forceRequired="true" />

				<td class="datacell-nowrap">
				<kul:htmlControlAttribute
					attributeEntry="${balanceInquiryAttributes.chartOfAccountsCode}"
						property="chartOfAccountsCode" forceRequired="true" readOnly="${accountsChartsReadOnly}" />
					<c:if test="${KualiForm.editingMode['AccountsCanCrossChart']}">
					<!-- KULLAB-704 Force the field conversions. -->
					<kul:lookup	boClassName="org.kuali.kfs.coa.businessobject.Chart"
						lookupParameters="chartOfAccountsCode:chartOfAccountsCode"							
						fieldConversions="chartOfAccountsCode:chartOfAccountsCode"
						fieldLabel="${balanceInquiryAttributes.chartOfAccountsCode.label}" />
					</c:if>
				</td>
					
			</tr>		

			<tr>			 
				<kul:htmlAttributeHeaderCell
					attributeEntry="${balanceInquiryAttributes.accountNumber}"
					horizontal="true" labelFor="accountNumber" forceRequired="true"/>
                        <c:set var="accountNumberField"  value="accountNumber" />
                        <c:set var="coaCodePropertyName"  value="chartOfAccountsCode" />
	<script language="JavaScript" type="text/javascript" src="dwr/interface/AccountService.js"></script>
	<script language="JavaScript" type="text/javascript" src="scripts/coa/accountDocument.js"></script>
                        
				<td class="datacell-nowrap"><kul:htmlControlAttribute
					attributeEntry="${balanceInquiryAttributes.accountNumber}"
					property="accountNumber" forceRequired="true" readOnly="${readOnly}" 
					onblur="loadChartCodeUsingAccountNumber('${accountNumberField}', '${coaCodePropertyName}');${onblur}" />
					<c:if test="${!readOnly}">
						 <kul:lookup boClassName="org.kuali.kfs.coa.businessobject.Account"
						lookupParameters="accountNumber:accountNumber,chartOfAccountsCode:chartOfAccountsCode"
						fieldConversions="accountNumber:accountNumber,chartOfAccountsCode:chartOfAccountsCode"
						fieldLabel="${balanceInquiryAttributes.accountNumber.label}" />
					</c:if>
				</td>
			</tr>

			<tr>
				<kul:htmlAttributeHeaderCell
					attributeEntry="${balanceInquiryAttributes.subAccountNumber}"
					horizontal="true" labelFor="subAccountNumber" forceRequired="false"  hideRequiredAsterisk="true"/>
					
				<td class="datacell-nowrap"><kul:htmlControlAttribute
					attributeEntry="${balanceInquiryAttributes.subAccountNumber}"
					property="subAccountNumber" forceRequired="true" readOnly="${readOnly}" /> 
					<c:if test="${!readOnly}">
						<kul:lookup	boClassName="org.kuali.kfs.coa.businessobject.SubAccount"
						lookupParameters="accountNumber:accountNumber,subAccountNumber:subAccountNumber,chartOfAccountsCode:chartOfAccountsCode"
						fieldConversions="accountNumber:accountNumber,subAccountNumber:subAccountNumber,chartOfAccountsCode:chartOfAccountsCode"
						fieldLabel="${balanceInquiryAttributes.subAccountNumber.label}" />
					</c:if>
				</td>
			</tr>
            
            <tr>
            	<td height="30" class="infoline">&nbsp;</td>
            	<td height="30" class="infoline">
	            	<c:if test="${!readOnly}">
		                <gl:balanceInquiryLookup
								boClassName="org.kuali.kfs.module.ld.businessobject.LedgerBalanceForBenefitExpenseTransfer"
								actionPath="glBalanceInquiryLookup.do"
								lookupParameters="universityFiscalYear:universityFiscalYear,accountNumber:accountNumber,subAccountNumber:subAccountNumber,chartOfAccountsCode:chartOfAccountsCode,emplid:emplid"
								tabindexOverride="KualiForm.currentTabIndex"
								hideReturnLink="false" image="buttonsmall_search.gif"/>
					</c:if>
				</td>				
			</tr>

		</table>
		</div>
	</kul:tab>
		
	
	<kul:tab tabTitle="Accounting Lines" defaultOpen="true">
		<sys-java:accountingLines>
			<sys-java:accountingLineGroup collectionPropertyName="document.sourceAccountingLines" collectionItemPropertyName="document.sourceAccountingLine" attributeGroupName="source" />
			<sys-java:accountingLineGroup collectionPropertyName="document.targetAccountingLines" collectionItemPropertyName="document.targetAccountingLine" attributeGroupName="target"/> 
		</sys-java:accountingLines>
	</kul:tab>
	
	<ld:laborLedgerPendingEntries />
	<kul:notes />
	<kul:adHocRecipients />
	<kul:routeLog />
	<kul:superUserActions />
	<kul:panelFooter />
	<sys:documentControls transactionalDocument="true" extraButtons="${KualiForm.extraButtons}" />
</kul:documentPage>
