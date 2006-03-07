<%@ include file="/jsp/core/tldHeader.jsp" %>

<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/cr" prefix="cr" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>

<c:set var="displayHidden" value="false" />
<c:set var="checkDetailMode" value="${KualiForm.checkEntryDetailMode}" />

<c:set var="cashReceiptAttributes" value="${DataDictionary['KualiCashReceiptDocument'].attributes}" />
<c:set var="readOnly" value="${!empty KualiForm.editingMode['viewOnly']}" />

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
                                <td align=left valign=middle><div align="right"><strong><kul:htmlAttributeLabel attributeEntry="${cashReceiptAttributes.totalCheckAmount}" useShortLabel="false" /></strong></div></td>
								<c:if test="${readOnly}">
									<td align=left valign=middle class="right" colspan=2>
										$${KualiForm.document.currencyFormattedTotalCheckAmount}
										<html:hidden write="false" property="document.totalCheckAmount" />
	                                </td>
								</c:if>
								<c:if test="${!readOnly}">
	                                <td align=left valign=middle class="right">
	                                    <c:if test="${!checkDetailMode}">
	                                        <kul:htmlControlAttribute property="document.totalCheckAmount" attributeEntry="${cashReceiptAttributes.totalCheckAmount}" />
	                                    </c:if>
	                                    <c:if test="${checkDetailMode}">
	                                    	$${KualiForm.document.currencyFormattedTotalCheckAmount}
	                                        <html:hidden write="false" property="document.totalCheckAmount" />
	                                    </c:if>
	                                </td>
                                </c:if>
								<c:if test="${!readOnly}">
	                                <td>
	                                    <html:select property="checkEntryMode" onchange="submitForm()" >
	                                      <html:optionsCollection property="checkEntryModes" label="label" value="value" />
	                                    </html:select>
	
	                                    <noscript>
	                                        <html:image src="images/tinybutton-select.gif" styleClass="tinybutton" alt="change check entry mode" />
	                                    </noscript>
	                                </td>
	                            </c:if>
                            </tr>
                            <tr>
                                <td align=left valign=middle><div align="right"><strong><kul:htmlAttributeLabel attributeEntry="${cashReceiptAttributes.totalCashAmount}" useShortLabel="false" /></strong></div></td>
                                <td align=left valign=middle class="right">
                                    <c:if test="${readOnly}">
                                    	$${KualiForm.document.currencyFormattedTotalCashAmount}
                                    	<html:hidden write="false" property="document.totalCashAmount" />
                                    </c:if>
                                    <c:if test="${!readOnly}">
                                    	<kul:htmlControlAttribute property="document.totalCashAmount" attributeEntry="${cashReceiptAttributes.totalCashAmount}" />
                                	</c:if>
                                </td>
                                <td>&nbsp;</td>
                            </tr>
                            <tr>
                                <td align=left valign=middle><div align="right"><strong><kul:htmlAttributeLabel attributeEntry="${cashReceiptAttributes.totalCoinAmount}" useShortLabel="false" /></strong></div></td>
                                <td align=left valign=middle class="right">
									<c:if test="${readOnly}">
                                    	$${KualiForm.document.currencyFormattedTotalCoinAmount}
                                    	<html:hidden write="false" property="document.totalCoinAmount" />
                                    </c:if>
                                    <c:if test="${!readOnly}">
                                    	<kul:htmlControlAttribute property="document.totalCoinAmount" attributeEntry="${cashReceiptAttributes.totalCoinAmount}" />
                                	</c:if>
                                </td>
                                <td>&nbsp;</td>
                            </tr>
                            <tr>
                                <td align=left valign=middle><div align="right"><strong>Cash Reconciliation Total:</strong></div></td>
                                <td align=left valign=middle class="right">$${KualiForm.document.currencyFormattedSumTotalAmount}</td>
                                <td>
                                	<c:if test="${!readOnly}">
                                    	<html:image src="images/tinybutton-recalculate.gif" styleClass="tinybutton" alt="recalculate total" />
                                	</c:if>
                                	<c:if test="${readOnly}">
                                	&nbsp;
                                	</c:if>
                                </td>
                            </tr>
                        </table><br>    
                    </div></td>
                </tr>
            </table>
        </div>
    </kul:tab>

    <cr:checkLines checkDetailMode="${checkDetailMode}" editingMode="${KualiForm.editingMode}" totalAmount="${KualiForm.cashReceiptDocument.currencyFormattedTotalCheckAmount}" displayHidden="${displayHidden}" />
   		
    <fin:accountingLines editingMode="${KualiForm.editingMode}" editableAccounts="${KualiForm.editableAccounts}" sourceAccountingLinesOnly="true" />

    <kul:generalLedgerPendingEntries/>
		
    <kul:notes/>
		
    <kul:adHocRecipients/>
		
    <kul:routeLog/>

    <kul:panelFooter/>

    <kul:documentControls transactionalDocument="${documentEntry.transactionalDocument}"/>
</kul:documentPage>
