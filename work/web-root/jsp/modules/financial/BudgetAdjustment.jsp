<%@ include file="/jsp/core/tldHeader.jsp" %>
<kul:documentPage showDocumentInfo="true" documentTypeName="KualiBudgetAdjustmentDocument" htmlFormAction="financialBudgetAdjustment"
                  renderMultipart="true" showTabButtons="true">

<SCRIPT type="text/javascript">
<!--
    function submitForm() {
        document.forms[0].submit();
    }
//-->
</SCRIPT>

		<html:hidden property="document.nextSourceLineNumber"/>
		<html:hidden property="document.nextTargetLineNumber"/>

		<kul:hiddenDocumentFields excludePostingYear="true"/>

        <kul:documentOverview editingMode="${KualiForm.editingMode}">
            <c:set var="baAttributes" value="${DataDictionary.KualiBudgetAdjustmentDocument.attributes}" />
            <tr>
                <kul:htmlAttributeHeaderCell
                    labelFor="document.postingYear"
                    attributeEntry="${baAttributes.postingYear}"
                    horizontal="true"
                    />

                <td class="datacell-nowrap">
                    <kul:htmlControlAttribute 
                        attributeEntry="${baAttributes.postingYear}" 
                        property="document.postingYear" 
                        onchange="submitForm()"
                        readOnly="${!KualiForm.editingMode['fullEntry']}"/>
                </td>

              <th colspan="2">&nbsp;</th>
            </tr>
        </kul:documentOverview>

        <fin:accountingLines 
            editingMode="${KualiForm.editingMode}" 
            editableAccounts="${KualiForm.editableAccounts}" 
            currentBaseAmount="true"
            displayMonthlyAmounts="true"
            />

		<kul:generalLedgerPendingEntries/>

		<kul:notes/>

		<kul:adHocRecipients/>

		<kul:routeLog/>

		<kul:panelFooter/>

		<kul:documentControls transactionalDocument="true" />

</kul:documentPage>