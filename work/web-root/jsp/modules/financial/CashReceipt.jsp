<%@ include file="/jsp/core/tldHeader.jsp" %>

<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/cr" prefix="cr" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>

<c:set var="displayHidden" value="false" />

<kul:documentPage showDocumentInfo="true" htmlFormAction="financialCashReceipt" documentTypeName="KualiCashReceiptDocument"  renderMultipart="true" showTabButtons="true">

    <kul:hiddenDocumentFields />

    <html:hidden property="document.nextSourceLineNumber"/>

    <html:hidden property="document.nextCheckSequenceId"/>
    <html:hidden property="document.checkEntryMode" />

    <kul:documentOverview editingMode="${KualiForm.editingMode}"/>
        
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

                                <%-- change from doc.totalCheckAmount to form.totalCheckAmount, once you get that introduced --%>
                                <td align=left valign=middle class="right"><input type="text" size="10" class="right" name="document.totalCheckAmount" value="${KualiForm.cashReceiptDocument.totalCheckAmount}" /></td>
                                
                                <td>
                                    <html:select property="checkEntryMode" > <%-- onblur="${onblur}" onchange="${onchange}" --%>
                                      <html:optionsCollection property="checkEntryModes" label="label" value="value" />
                                    </html:select>

                                    <%-- need code here to only show this button if javascript is off --%>
                                    <html:image property="methodToCall.changeCheckEntryMode" src="images/tinybutton-verify1.gif" styleClass="tinybutton" alt="change check entry mode" />
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

    <%-- change from doc.totalCheckAmount to form.totalCheckAmount, once you get that introduced --%>
    <cr:checkLines editingMode="${editingMode}" totalAmount="${KualiForm.cashReceiptDocument.totalCheckAmount}" displayHidden="${displayHidden}" />
   		
    <fin:accountingLines editingMode="${KualiForm.editingMode}" editableAccounts="${KualiForm.editableAccounts}" sourceAccountingLinesOnly="true" />

    <kul:generalLedgerPendingEntries/>
		
    <kul:notes/>
		
    <kul:adHocRecipients/>
		
    <kul:routeLog/>

    <kul:panelFooter/>

    <kul:documentControls transactionalDocument="false" />
</kul:documentPage>
