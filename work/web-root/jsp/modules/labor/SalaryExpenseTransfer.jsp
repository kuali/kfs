<%--
 Copyright 2006-2007 The Kuali Foundation.
 
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
	value="${empty KualiForm.editingMode['fullEntry']}" />
	
<c:if test="${fn:length(KualiForm.document.sourceAccountingLines)>0 || readOnly}">
	<c:set var="disabled" value="true"/>
</c:if>

<c:if test="${fn:length(KualiForm.document.targetAccountingLines)>0 || readOnly}">
	<c:set var="targetDisabled" value="true"/>
</c:if>

<c:set var="documentTypeName" value="SalaryExpenseTransferDocument"/>
<c:set var="htmlFormAction" value="laborSalaryExpenseTransfer"/>

<c:if test="${isYearEnd}">
  <c:set var="documentTypeName" value="YearEndSalaryExpenseTransferDocument"/>
  <c:set var="htmlFormAction" value="laborYearEndSalaryExpenseTransfer"/>
</c:if>

<kul:documentPage showDocumentInfo="true"
    documentTypeName="${documentTypeName}"
    htmlFormAction="${htmlFormAction}" renderMultipart="true"
    showTabButtons="true">

    <html:hidden property="financialBalanceTypeCode" />
    
    <c:forEach items="${KualiForm.document.approvalObjectCodeBalances}" var="objCodeBal">
      <html:hidden property="document.approvalObjectCodeBalances(${objCodeBal.key})"/>
    </c:forEach>
    
    <kfs:hiddenDocumentFields />
    <kfs:documentOverview editingMode="${KualiForm.editingMode}" />
 
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
						<kul:lookup	boClassName="org.kuali.kfs.sys.businessobject.Options"
						lookupParameters="universityFiscalYear:universityFiscalYear"
						fieldLabel="${balanceInquiryAttributes.universityFiscalYear.label}" />
					</c:if>
				</td>
			</tr>			

             <tr>
               <kul:htmlAttributeHeaderCell
                   attributeEntry="${DataDictionary.PersonImpl.attributes.principalId}"
                   horizontal="true"
                   forceRequired="true" labelFor="emplid"
                   />
               <td>
                     <kfs:employee userIdFieldName="emplid"
                                 userNameFieldName="user.name" 
                                 fieldConversions="principalId:emplid"
                                 lookupParameters="emplid:principalId,universityFiscalYear:universityFiscalYear"
                                 hasErrors="${hasErrors}"
                                 onblur="${onblur}"
                                 highlight="${addHighlighting}" readOnly="${disabled}" >
                     </kfs:employee>
               </td>
             </tr>
            
            <tr>
            	<td height="30" class="infoline">&nbsp;</td>
            	<td height="30" class="infoline">
            		<c:if test="${!readOnly}">
	                   <gl:balanceInquiryLookup
	                       boClassName="org.kuali.kfs.module.ld.businessobject.LedgerBalanceForSalaryExpenseTransfer"
	                       actionPath="glBalanceInquiryLookup.do"
	                       lookupParameters="universityFiscalYear:universityFiscalYear,emplid:emplid,financialBalanceTypeCode:financialBalanceTypeCode"
	                       tabindexOverride="KualiForm.currentTabIndex"
	                       hideReturnLink="false" image="buttonsmall_search.gif"/>
	                </c:if>
				</td>
			</tr>
		</table>
		</div>
	</kul:tab>
	
	<kul:tab tabTitle="Accounting Lines" defaultOpen="true"
		tabErrorKey="${KFSConstants.ACCOUNTING_LINE_ERRORS}">
			<c:if test="${!disabled}">
				<div class="tab-container" align=center>
					<table cellpadding="0" cellspacing="0" class="datatable"			
						<ld:importedAccountingLineGroup
							isSource="true"
				            columnCountUntilAmount="12"
				            columnCount="14"
				            optionalFields="positionNumber,payrollEndDateFiscalYear,payrollEndDateFiscalPeriodCode,payrollTotalHours"
				            extraRowFields="${extraSourceRowFieldsMap}"
				            editingMode="${KualiForm.editingMode}"
				            editableAccounts="${editableAccountsMap}"
				            debitCreditAmount="${debitCreditAmountString}"
				            currentBaseAmount="${currentBaseAmountString}"
				            extraHiddenFields="${extraHiddenFieldsMap}"
				            useCurrencyFormattedTotal="${useCurrencyFormattedTotalBoolean}"
				            includeObjectTypeCode="false"
				            displayMonthlyAmounts="${displayMonthlyAmountsBoolean}"
				            forcedReadOnlyFields="${KualiForm.forcedReadOnlySourceFields}"
				            accountingLineAttributes="${accountingLineAttributesMap}" />
				    </table>
			    </div>
			</c:if>

			<c:if test="${disabled}">
				<sys:accountingLines>
				       	<sys:accountingLineGroup 
				        		collectionPropertyName="document.sourceAccountingLines" 
				        		collectionItemPropertyName="document.sourceAccountingLine" 
				        		attributeGroupName="source" />
				</sys:accountingLines>
			</c:if>			
			
			<c:if test="${!targetDisabled}">
				<div class="tab-container" align=center>
					<table cellpadding="0" cellspacing="0" class="datatable"			
						<ld:importedAccountingLineGroup
							isSource="false"
				            columnCountUntilAmount="12"
				            columnCount="14"
				            optionalFields="positionNumber,payrollEndDateFiscalYear,payrollEndDateFiscalPeriodCode,payrollTotalHours"
				            extraRowFields="${extraSourceRowFieldsMap}"
				            editingMode="${KualiForm.editingMode}"
				            editableAccounts="${editableAccountsMap}"
				            debitCreditAmount="${debitCreditAmountString}"
				            currentBaseAmount="${currentBaseAmountString}"
				            extraHiddenFields="${extraHiddenFieldsMap}"
				            useCurrencyFormattedTotal="${useCurrencyFormattedTotalBoolean}"
				            includeObjectTypeCode="false"
				            displayMonthlyAmounts="${displayMonthlyAmountsBoolean}"
				            forcedReadOnlyFields="${KualiForm.forcedReadOnlySourceFields}"
				            accountingLineAttributes="${accountingLineAttributesMap}" />
				    </table>
			    </div>
			</c:if>
			
			<c:if test="${targetDisabled}">
				<sys:accountingLines>
				       	<sys:accountingLineGroup 
				        		collectionPropertyName="document.targetAccountingLines" 
				        		collectionItemPropertyName="document.targetAccountingLine" 
				        		attributeGroupName="target" />
				</sys:accountingLines>
			</c:if>
	</kul:tab>      
	<ld:laborLedgerPendingEntries />
    <kul:notes />
    <kul:adHocRecipients />
    <kul:routeLog />
    <kul:panelFooter />
    <kfs:documentControls transactionalDocument="true" />
</kul:documentPage>
