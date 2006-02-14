<%@ include file="/jsp/core/tldHeader.jsp" %>

<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>

<kul:documentPage showDocumentInfo="true" htmlFormAction="financialCashReceipt" documentTypeName="KualiCashReceiptDocument"  renderMultipart="true" showTabButtons="true">

    <html:hidden property="document.nextSourceLineNumber"/>
    <kul:hiddenDocumentFields />
    <kul:documentOverview editingMode="${KualiForm.editingMode}"/>
        
    <kul:tab tabTitle="Cash Reconciliation" defaultOpen="true" tabErrorKey="${Constants.EDIT_CASH_RECEIPT_CASH_RECONCILIATION_ERRORS}" >	    
        <div class="tab-container" align=center>
            <table cellpadding=0 class="datatable" summary="view/edit document overview information">
                <tr>
                    <td align=right valign=middle class="subhead"><span class="subhead-left">Cash Reconciliation</span> <span class="subhead-right"> </span></td>
                </tr>
                <tr>
                    <td height="70" align=left valign=middle class="datacell"><div align="center"><br>
                        <table align="center" cellpadding="4">
                            <tr>
                                <td align=left valign=middle><div align="right"><strong>Checks:</strong></div></td>
                                <td align=left valign=middle class="right">${KualiForm.cashReceiptDocument.totalCheckAmount}</td>
                                <td rowspan="4" valign=bottom class="right"><a href="#"><img src="images/tinybutton-verify1.gif" width="42" height="15" hspace="12"></a></td>
                            </tr>
                            <tr>
                                <td align=left valign=middle><div align="right"><strong>Currency:</strong></div></td>
                                <td align=left valign=middle class="right"><input type="text" size="10" class="right" name="document.totalCashAmount" value="${KualiForm.cashReceiptDocument.totalCashAmount}" /></td>
                            </tr>
                            <tr>
                                <td align=left valign=middle><div align="right"><strong>Coins:</strong></div></td>
                                <td align=left valign=middle class="right"><input type="text" class="right" size="10" name="document.totalCoinAmount" value="${KualiForm.cashReceiptDocument.totalCoinAmount}" /></td>
                            </tr>
                            <tr>
                                <td align=left valign=middle><div align="right"><strong>Total:</strong></div></td>
                                <td align=left valign=middle class="right">${KualiForm.cashReceiptDocument.sumTotalAmount}</td>
                            </tr>
                        </table><br>    
                    </div></td>
                </tr>
            </table>
        </div>
    </kul:tab>
		
    <script language="JavaScript" type="text/javascript">
        /**
         * Script to prompt user to confirm their choice to switch from using individual checks
         * to using total only. This change will delete all individual checks if accepted. If
         * denied, nothing will change.
         */
        var choiceForCheckTotal;
        function switchCheckTotalMethod() {
            if(choiceForCheckTotal == null) {
                if(document.forms[0].checkTotalOnly[0].checked) {
                    choiceForCheckTotal = 'total';
                } else if(document.forms[0].checkTotalOnly[1].checked) {
                    choiceForCheckTotal = 'checks';
                }
                return;
            }
            
            if(choiceForCheckTotal == 'total') {
                return; // nothing to change
            }
            
            if(choiceForCheckTotal == 'checks') { 
                if(confirm("Individual check data will be lost. Is this OK?")) {
                    alert("OK. Using check totals (not implemented yet, this alert will be removed later).");
                    choiceForCheckTotal = 'checks';
                    // Set the flag to use the total only, submit the form and return.
                    // set the action to methodToCall.switchToCheckTotalOnly
                    // document.forms[0].submit();
                } else {
                    alert("OK. NOT using check totals (not implemented yet, this alert will be removed later).")
                    document.forms[0].checkTotalOnly[1].checked = true;
                }
            }
        }
    </script>
		
    <c:set var="checkBaseAttributes" value="${DataDictionary.CheckBase.attributes}" />
    <kul:tab tabTitle="Check Detail" defaultOpen="true" tabErrorKey="${Constants.EDIT_CASH_RECEIPT_CHECK_DETAIL_ERRORS}" >
	   
        <div class="tab-container" align=center>
            <table cellpadding=0 class="datatable" summary="view/edit document overview information">
                <tr>
                    <td colspan="4" align=right valign=middle class="subhead"><span class="subhead-left">Check Detail</span> <span class="subhead-right"> </span></td>
                </tr>
                <tr>
                    <td rowspan="2" align=right valign=top class="tab-subhead"><div align="right">
                        <input type="radio" name="checkTotalOnly" value="1" onClick="switchCheckTotalMethod()" />
                    </div></td>
                    <td align=left valign=middle class="tab-subhead">Total Only</td>
                    <td rowspan="2" align=right valign=top class="tab-subhead"><div align="right">
                        <input type="radio" name="checkTotalOnly" value="1" onClick="switchCheckTotalMethod()" />
                    </div></td>
                    <td align=left valign=middle class="tab-subhead">List Individual Checks or Batches</td>
                </tr>
                <tr>
                    <td align=center valign=middle class="datacell"><div align="center">
                        <input type="text" name="document.totalCheckAmount" value="${KualiForm.cashReceiptDocument.totalCheckAmount}" />
                    </div></td>
                    <td align=left valign=middle class="datacell"><div align="center"><br>
                        <table cellspacing="0" class="datatable">
                            <tr>
                                <th class="bord-l-b">&nbsp;</th>
                                <th class="bord-l-b"><div align="center">Check/Batch #</div></th>
                                <th class="bord-l-b"><div align="center">Date</div></th>
                                <th class="bord-l-b"><div align="center">Description</div></th>
                                <th class="bord-l-b"><div align="center">Amount</div></th>
                                <th class="bord-l-b">Action</th>
                           </tr>
                           <tr>
                                <th class="bord-l-b">add:</th>
                                <td class="infoline center" nowrap valign="top">
                                    <kul:htmlControlAttribute property="newCheck.checkNumber" attributeEntry="${checkBaseAttributes.checkNumber}" />
                                </td>
                                <td class="infoline center" nowrap valign="top">
                                    <kul:dateInputNoAttributeEntry property="newCheck.checkDate" maxLength="10" size="10" />
                                </td>
                                <td class="infoline center" nowrap valign="top">
                                    <kul:htmlControlAttribute property="newCheck.description" attributeEntry="${checkBaseAttributes.description}" />
                                </td>
                                <td class="infoline center" nowrap valign="top">
                                    <kul:htmlControlAttribute property="newCheck.amount" attributeEntry="${checkBaseAttributes.amount}" />
                                </td>
                                <td class="infoline center" nowrap>
                                    <html:image property="methodToCall.addCheck" styleClass="tinybutton" src="images/tinybutton-add1.gif" alt="add"/>
                                    <c:if test="${!KualiForm.hideDetails}"><span class="fineprint"><br /></span></c:if>
                                </td>
                            </tr>
                            <logic:iterate id="check" name="KualiForm" property="cashReceiptDocument.checks" indexId="ctr">
                                <tr>
                                    <th class="bord-l-b">${ctr + 1}</th>
                                    <td class="datacell center" nowrap><div align="center">
                                        <kul:htmlControlAttribute property="cashReceiptDocument.check[${ctr}].checkNumber" attributeEntry="${checkBaseAttributes.checkNumber}" /></div>
                                    </td>
                                    <td class="datacell center" nowrap><div align="center">
                                        <kul:dateInputNoAttributeEntry property="cashReceiptDocument.check[${ctr}].checkDate" maxLength="10" size="10" /></div>
                                    </td>
                                    <td class="datacell center" nowrap><div align="center">
                                        <kul:htmlControlAttribute property="cashReceiptDocument.check[${ctr}].description" attributeEntry="${checkBaseAttributes.description}" /></div>
                                    </td>
                                    <td class="datacell center" nowrap><div align="center">
                                        <kul:htmlControlAttribute property="cashReceiptDocument.check[${ctr}].amount" attributeEntry="${checkBaseAttributes.amount}" /></div>
                                    </td>
                                    <td class="datacell center" nowrap><div align="center">
                                        <html:image property="methodToCall.deleteCheck.line${ctr}" src="images/tinybutton-delete1.gif" styleClass="tinybutton" alt="delete"/></div>
                                        <c:if test="${!KualiForm.hideDetails}"></c:if>
                                    </td>
                                </tr>
                            </logic:iterate>
                        </table><br>
                    </div></td>
                </tr>
            </table>
        </div>	
    </kul:tab>
   		
    <script type="text/javascript" language="JavaScript">
        /**
         * Select the appropriate radio button for whether or not to use check totals.
         */
        <c:choose>
            <c:when test="${empty KualiForm.cashReceiptDocument.checks}">
                choiceForCheckTotal = 'total';
                document.forms[0].checkTotalOnly[0].checked = true;
            </c:when>
            <c:otherwise>
                choiceForCheckTotal = 'checks';
                document.forms[0].checkTotalOnly[1].checked = true;
            </c:otherwise>
        </c:choose>
    </script>
		
    <fin:accountingLines editingMode="${KualiForm.editingMode}" editableAccounts="${KualiForm.editableAccounts}" />

    <kul:generalLedgerPendingEntries/>
		
    <kul:notes/>
		
    <kul:adHocRecipients/>
		
    <kul:routeLog/>

    <kul:panelFooter/>

    <kul:documentControls transactionalDocument="false" />
</kul:documentPage>
