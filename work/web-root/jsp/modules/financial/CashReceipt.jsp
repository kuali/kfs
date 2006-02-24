<%@ include file="/jsp/core/tldHeader.jsp" %>

<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/cr" prefix="cr" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>

<c:set var="displayHidden" value="false" />
<c:set var="checkDetailMode" value="${KualiForm.checkEntryDetailMode}" />

<kul:documentPage showDocumentInfo="true" htmlFormAction="financialCashReceipt" documentTypeName="KualiCashReceiptDocument"  renderMultipart="true" showTabButtons="true">

    <kul:hiddenDocumentFields />
    
    <html:hidden property="document.nextSourceLineNumber"/>
    <html:hidden property="document.nextCheckSequenceId"/>
    <html:hidden property="document.checkEntryMode" />

    <html:hidden property="checkTotal" />

    <kul:documentOverview editingMode="${KualiForm.editingMode}"/>

    <SCRIPT type="text/javascript">
    <!--
        function submitForm() {
            document.forms[0].submit();
        }
    //-->
    </SCRIPT>
    <kul:tab tabTitle="Cash Reconciliation" defaultOpen="true" tabErrorKey="${Constants.EDIT_CASH_RECEIPT_CASH_RECONCILIATION_ERRORS}" >	    
        <div class="tab-container" align=center>
            <table cellpadding=0 class="datatable" summary="view/edit document overview information">
                <tr>
                    <td align=right valign=middle class="subhead"><span class="subhead-left">Cash Reconciliation</span> <span class="subhead-right"> </span></td>
                </tr>
                <tr>
                    <td height="70" align=left valign=middle class="datacell"><div align="center"><br>
                        <table cellpadding="4">
                            <tr>
                                <td align=left valign=middle><div align="right"><strong>Checks:</strong></div></td>

                                <td align=left valign=middle class="right">
                                    <c:if test="${!checkDetailMode}">
                                        <input type="text" size="10" class="right" name="document.totalCheckAmount" value="${KualiForm.cashReceiptDocument.totalCheckAmount}" />
                                    </c:if>
                                    <c:if test="${checkDetailMode}">
                                        <html:hidden write="true" property="document.totalCheckAmount" />
                                    </c:if>
                                </td>

                                <td>
                                    <html:select property="checkEntryMode" onchange="submitForm()" >
                                      <html:optionsCollection property="checkEntryModes" label="label" value="value" />
                                    </html:select>

                                    <noscript>
                                        <html:image src="images/tinybutton-select.gif" styleClass="tinybutton" alt="change check entry mode" />
                                    </noscript>
                                </td>
                            </tr>
                            <tr>
                                <td align=left valign=middle><div align="right"><strong>Currency:</strong></div></td>
                                <td align=left valign=middle class="right"><input type="text" size="10" class="right" name="document.totalCashAmount" value="${KualiForm.cashReceiptDocument.totalCashAmount}" /></td>
                                <td>&nbsp;</td>
                            </tr>
                            <tr>
                                <td align=left valign=middle><div align="right"><strong>Coins:</strong></div></td>
                                <td align=left valign=middle class="right"><input type="text" class="right" size="10" name="document.totalCoinAmount" value="${KualiForm.cashReceiptDocument.totalCoinAmount}" /></td>
                                <td>&nbsp;</td>
                            </tr>
                            <tr>
                                <td align=left valign=middle><div align="right"><strong>Total:</strong></div></td>
                                <td align=left valign=middle class="right">${KualiForm.cashReceiptDocument.sumTotalAmount}</td>
                                <td>&nbsp;</td>
                            </tr>
                        </table><br>    
                    </div></td>
                </tr>
            </table>
        </div>
    </kul:tab>

    <cr:checkLines checkDetailMode="${checkDetailMode}" editingMode="${editingMode}" totalAmount="${KualiForm.cashReceiptDocument.totalCheckAmount}" displayHidden="${displayHidden}" />
   		
    <fin:accountingLines editingMode="${KualiForm.editingMode}" editableAccounts="${KualiForm.editableAccounts}" sourceAccountingLinesOnly="true" />

    <kul:generalLedgerPendingEntries/>
		
    <kul:notes/>
		
    <kul:adHocRecipients/>
		
    <kul:routeLog/>

    <kul:panelFooter/>

    <kul:documentControls transactionalDocument="${documentEntry.transactionalDocument}"/>
</kul:documentPage>
