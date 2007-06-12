<%--
 Copyright 2007 The Kuali Foundation.
 
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

<c:set var="balanceInquiryAttributes"
	value="${DataDictionary.LedgerBalanceForBenefitExpenseTransfer.attributes}" />

<c:set var="readOnly"
	value="${!empty KualiForm.editingMode['viewOnly']}" />

<kul:documentPage showDocumentInfo="true"
	documentTypeName="KualiBenefitExpenseTransferDocument"
	htmlFormAction="laborBenefitExpenseTransfer" renderMultipart="true"
	showTabButtons="true">

	<kul:hiddenDocumentFields />
	<kul:documentOverview editingMode="${KualiForm.editingMode}" />
	
	<c:if test="${!readOnly}">
		<kul:tab tabTitle="Ledger Balance Importing" defaultOpen="true"
			tabErrorKey="${Constants.EMPLOYEE_LOOKUP_ERRORS}">
			<div class="tab-container" align=center>
			<div class="h2-container">
			<h2>Ledger Balance Importing</h2>
			</div>
			<table cellpadding="0" cellspacing="0" class="datatable"
				summary="Ledger Balance Importing">
	
				<tr>
					<kul:htmlAttributeHeaderCell
						attributeEntry="${balanceInquiryAttributes.universityFiscalYear}"
						horizontal="true" width="35%"  forceRequired="true"/>
	
					<td class="datacell-nowrap"><kul:htmlControlAttribute
						attributeEntry="${balanceInquiryAttributes.universityFiscalYear}"
						property="universityFiscalYear" readOnly="${readOnly}" /> <kul:lookup
						boClassName="org.kuali.kfs.bo.Options"
						lookupParameters="universityFiscalYear:universityFiscalYear"
						fieldLabel="${balanceInquiryAttributes.universityFiscalYear.label}" /></td>
				</tr>			
	
				<tr>
					<kul:htmlAttributeHeaderCell
						attributeEntry="${balanceInquiryAttributes.accountNumber}"
						horizontal="true" forceRequired="true"/>
						
					<td class="datacell-nowrap"><kul:htmlControlAttribute
						attributeEntry="${balanceInquiryAttributes.accountNumber}"
						property="accountNumber" readOnly="${readOnly}" /> <kul:lookup
						boClassName="org.kuali.module.chart.bo.Account"
						lookupParameters="accountNumber:accountNumber,chartOfAccountsCode:chartOfAccountsCode"
						fieldLabel="${balanceInquiryAttributes.accountNumber.label}" /></td>
				</tr>
							
				<tr>
					<kul:htmlAttributeHeaderCell
						attributeEntry="${balanceInquiryAttributes.chartOfAccountsCode}"
						horizontal="true" forceRequired="false" hideRequiredAsterisk="true"/>
	
					<td class="datacell-nowrap"><kul:htmlControlAttribute
						attributeEntry="${balanceInquiryAttributes.chartOfAccountsCode}"
						property="chartOfAccountsCode" readOnly="${readOnly}" /> <kul:lookup
						boClassName="org.kuali.module.chart.bo.Chart"
						lookupParameters="chartOfAccountsCode:chartOfAccountsCode"
						fieldLabel="${balanceInquiryAttributes.chartOfAccountsCode.label}" /></td>
				</tr>
	
				<tr>
					<kul:htmlAttributeHeaderCell
						attributeEntry="${balanceInquiryAttributes.subAccountNumber}"
						horizontal="true" forceRequired="false"  hideRequiredAsterisk="true"/>
						
					<td class="datacell-nowrap"><kul:htmlControlAttribute
						attributeEntry="${balanceInquiryAttributes.subAccountNumber}"
						property="subAccountNumber" readOnly="${readOnly}" /> <kul:lookup
						boClassName="org.kuali.module.chart.bo.SubAccount"
						lookupParameters="accountNumber:accountNumber,subAccountNumber:subAccountNumber,chartOfAccountsCode:chartOfAccountsCode"
						fieldLabel="${balanceInquiryAttributes.subAccountNumber.label}" /></td>
				</tr>
				
				<tr>
					<kul:htmlAttributeHeaderCell
	                    attributeEntry="${balanceInquiryAttributes.emplid}" horizontal="true" forceRequired="false"/>
	                    
					<td class="datacell-nowrap"><kul:htmlControlAttribute
						attributeEntry="${balanceInquiryAttributes.emplid}"
						property="emplid" readOnly="${readOnly}" /> <kul:lookup
						boClassName="org.kuali.core.bo.user.UniversalUser"
						fieldConversions="personPayrollIdentifier:emplid"
						lookupParameters="emplid:personPayrollIdentifier"
						fieldLabel="${balanceInquiryAttributes.personPayrollIdentifier.label}" /></td>
	            </tr>
	            
	            <tr>
	            	<td height="30" class="infoline">&nbsp;</td>
	            	<td height="30" class="infoline">
	                <gl:balanceInquiryLookup
							boClassName="org.kuali.module.labor.bo.LedgerBalanceForBenefitExpenseTransfer"
							actionPath="glBalanceInquiryLookup.do"
							lookupParameters="universityFiscalYear:universityFiscalYear,accountNumber:accountNumber,subAccountNumber:subAccountNumber,chartOfAccountsCode:chartOfAccountsCode,emplid:emplid"
							hideReturnLink="false" image="buttonsmall_search.gif"/>
					</td>
				</tr>
	
			</table>
			</div>
		</kul:tab>
	</c:if>

	<c:set var="copyMethod" value="" scope="request" />
	<c:set var="actionInfixVar" value="" scope="request" />
	<c:set var="accountingLineIndexVar" value="" scope="request" />
	<fin:accountingLines editingMode="${KualiForm.editingMode}"
		editableAccounts="${KualiForm.editableAccounts}" inherit="false" extraHiddenFields=",emplid,positionNumber,balanceTypeCode,payrollTotalHours"
		optionalFields="payrollEndDateFiscalYear,payrollEndDateFiscalPeriodCode">

		<jsp:attribute name="groupsOverride">
			<table width="100%" border="0" cellpadding="0" cellspacing="0"
				class="datatable">
				<fin:subheadingWithDetailToggleRow columnCount="${columnCount}"
					subheading="Accounting Lines" />
				<ld:importedAccountingLineGroup isSource="true"
					columnCountUntilAmount="${columnCountUntilAmount}"
					columnCount="${columnCount}" optionalFields="${optionalFieldsMap}"
					extraRowFields="${extraSourceRowFieldsMap}"
					editingMode="${KualiForm.editingMode}"
					editableAccounts="${editableAccountsMap}"
					editableFields="${KualiForm.accountingLineEditableFields}"
					debitCreditAmount="${debitCreditAmountString}"
					currentBaseAmount="${currentBaseAmountString}"
					extraHiddenFields="${extraHiddenFieldsMap}"
					useCurrencyFormattedTotal="${useCurrencyFormattedTotalBoolean}"
					includeObjectTypeCode="${includeObjectTypeCodeBoolean}"
					displayMonthlyAmounts="${displayMonthlyAmountsBoolean}"
					forcedReadOnlyFields="${KualiForm.forcedReadOnlySourceFields}"
					accountingLineAttributes="${accountingLineAttributesMap}">
					<jsp:attribute name="importRowOverride">
						<html:image property="methodToCall.copyAllAccountingLines"
							src="${ConfigProperties.externalizable.images.url}tinybutton-copyall.gif"
							title="Copy all Source Accounting Lines"
							alt="Copy all Source Lines" styleClass="tinybutton" />
						<html:image property="methodToCall.deleteAllAccountingLines"
							src="${ConfigProperties.externalizable.images.url}tinybutton-deleteall.gif"
							title="Delete all Source Accounting Lines"
							alt="Delete all Source Lines" styleClass="tinybutton" />
                    </jsp:attribute>
					<jsp:attribute name="customActions">
						<c:set var="copyMethod"
							value="copyAccountingLine.line${accountingLineIndexVar}"
							scope="request" />
						<html:image
							property="methodToCall.${copyMethod}.anchoraccounting${actionInfixVar}Anchor"
							src="${ConfigProperties.externalizable.images.url}tinybutton-copy2.gif" title="Copy an Accounting Line"
							alt="Copy an Accounting Line" styleClass="tinybutton" />
					</jsp:attribute>
				</ld:importedAccountingLineGroup>

				<ld:importedAccountingLineGroup isSource="false"
					columnCountUntilAmount="${columnCountUntilAmount}"
					columnCount="${columnCount}" optionalFields="${optionalFieldsMap}"
					extraRowFields="${extraSourceRowFieldsMap}"
					editingMode="${KualiForm.editingMode}"
					editableAccounts="${editableAccountsMap}"
					editableFields="${KualiForm.accountingLineEditableFields}"
					debitCreditAmount="${debitCreditAmountString}"
					currentBaseAmount="${currentBaseAmountString}"
					extraHiddenFields="${extraHiddenFieldsMap}"
					useCurrencyFormattedTotal="${useCurrencyFormattedTotalBoolean}"
					includeObjectTypeCode="${includeObjectTypeCodeBoolean}"
					displayMonthlyAmounts="${displayMonthlyAmountsBoolean}"
					forcedReadOnlyFields="${KualiForm.forcedReadOnlyTargetFields}"
					accountingLineAttributes="${accountingLineAttributesMap}">
					<jsp:attribute name="importRowOverride">
					</jsp:attribute>
				</ld:importedAccountingLineGroup>
			</table>
		</jsp:attribute>
	</fin:accountingLines>
	<ld:laborLedgerPendingEntries />
	<kul:notes />
	<kul:adHocRecipients />
	<kul:routeLog />
	<kul:panelFooter />
	<kul:documentControls transactionalDocument="true" />
</kul:documentPage>
