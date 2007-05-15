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

<%@ include file="/jsp/core/tldHeader.jsp"%>
<kul:documentPage showDocumentInfo="true"
    documentTypeName="KualiSalaryExpenseTransferDocument"
    htmlFormAction="laborSalaryExpenseTransfer" renderMultipart="true"
    showTabButtons="true">

    <html:hidden property="financialBalanceTypeCode" />
    <kul:hiddenDocumentFields />
    <kul:documentOverview editingMode="${KualiForm.editingMode}" />
    <kul:tab tabTitle="Employee Lookup" defaultOpen="true"
        tabErrorKey="${Constants.EMPLOYEE_LOOKUP_ERRORS}">
        <div class="tab-container" align=center>
            <div class="h2-container"><b>Employee Lookup</b></div>
            <table cellpadding="0" cellspacing="0" class="datatable"
                summary="employee lookup">
    
              <tr>
                <kul:htmlAttributeHeaderCell
                    attributeEntry="${DataDictionary.UniversalUser.attributes.personPayrollIdentifier}"
                    horizontal="true"
                    forceRequired="true"
                    />
                <td>
                        <kul:employee userIdFieldName="personPayrollIdentifier" 
                                  userNameFieldName="user.personName" 
                                  fieldConversions="personPayrollIdentifier:personPayrollIdentifier"
                                  lookupParameters="personPayrollIdentifier:personPayrollIdentifier"
                                  hasErrors="${hasErrors}"
                                  onblur="${onblur}"
                                  highlight="${addHighlighting}">
                            <jsp:attribute name="helpLink" trim="true">
                                <kul:help
                                    businessObjectClassName="${field.businessObjectClassName}"
                                    attributeName="${field.fieldHelpName}"
                                    altText="${field.fieldHelpSummary}" />      
                            </jsp:attribute>
                        </kul:employee>
 
                </td>
              </tr>
              <tr>
                <kul:htmlAttributeHeaderCell
                    attributeEntry="${DataDictionary.AccountingPeriod.attributes.universityFiscalYear}"
                    horizontal="true"
                    forceRequired="true"
                    />
                <td>${KualiForm.universityFiscalYear}</td>
              </tr>
            </table>
                <p>
        </div>
    </kul:tab>
	<fin:accountingLines editingMode="${KualiForm.editingMode}"
		editableAccounts="${KualiForm.editableAccounts}" inherit="false"
		optionalFields="positionNumber,payrollEndDateFiscalYear,payrollEndDateFiscalPeriodCode,payrollTotalHours">
      <jsp:attribute name="groupsOverride">
      <table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
        <fin:subheadingWithDetailToggleRow
            columnCount="${columnCount}"
             subheading="Accounting Lines"/>
        <ld:importedAccountingLineGroup
            isSource="true"
            columnCountUntilAmount="${columnCountUntilAmount}"
            columnCount="${columnCount}"
            optionalFields="${optionalFieldsMap}"
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
            forcedReadOnlyFields="${KualiForm.forcedReadOnlyFields}"
            accountingLineAttributes="${accountingLineAttributesMap}">
            <jsp:attribute name="importRowOverride">
                Import from Labor Ledger
                <kul:balanceInquiryLookup
                    boClassName="org.kuali.module.labor.bo.LedgerBalance"
                    actionPath="glBalanceInquiryLookup.do"
                    fieldConversions="universityFiscalYear:universityFiscalYear"
                    lookupParameters="personPayrollIdentifier:personPayrollIdentifier,financialBalanceTypeCode:financialBalanceTypeCode"
                    hideReturnLink="false" />
            </jsp:attribute>
        </ld:importedAccountingLineGroup>

        <ld:importedAccountingLineGroup
            isSource="false"
            columnCountUntilAmount="${columnCountUntilAmount}"
            columnCount="${columnCount}"
            optionalFields="${optionalFieldsMap}"
            extraRowFields="${extraTargetRowFieldsMap}"
            editingMode="${KualiForm.editingMode}"
            editableAccounts="${editableAccountsMap}"
            editableFields="${editableFieldsMap}"
            debitCreditAmount="${debitCreditAmountString}"
            currentBaseAmount="${currentBaseAmountString}"
            extraHiddenFields="${extraHiddenFieldsMap}"
            useCurrencyFormattedTotal="${useCurrencyFormattedTotalBoolean}"
            includeObjectTypeCode="${includeObjectTypeCodeBoolean}"
            displayMonthlyAmounts="${displayMonthlyAmountsBoolean}"
            accountingLineAttributes="${accountingLineAttributesMap}">
            <jsp:attribute name="importRowOverride">
            </jsp:attribute>
         </ld:importedAccountingLineGroup>
      </table>
      </jsp:attribute>
    </fin:accountingLines>
    <ld:laborLedgerPendingEntries />
    <kul:generalLedgerPendingEntries />
    <kul:notes />
    <kul:adHocRecipients />
    <kul:routeLog />
    <kul:panelFooter />
    <kul:documentControls transactionalDocument="true" />
</kul:documentPage>
