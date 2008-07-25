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

<kul:tab tabTitle="Assets" defaultOpen="true">

<div class="tab-container" id="G3" style="" align="center">

  <table class="datatable" width="100%" border="0" cellpadding="0" cellspacing="0">
      <tbody><tr>
  <td colspan="12" class="subhead">
    <span class="subhead-left">Assets</span>

  </td>
</tr>

<tr>
    <th colspan="1">&nbsp;
    <a name="accountingSourceAnchor"></a>
    &nbsp;</th>
<th width="90%" colspan="10">&nbsp;

    <font color="">*&nbsp;</font>Asset Number</th>
<th colspan="1">&nbsp;
    Actions</th>

</tr>

    <tr>
	<th colspan="1" scope="row">&nbsp;
    add:<input name="newSourceLine.postingYear" value="2009" type="hidden"><input name="newSourceLine.overrideCode" value="N" type="hidden"></th>

<td class="infoline" colspan="10" valign="top">
    <span class="nowrap">
    <input name="newSourceLine.accountNumber" maxlength="7" size="10" tabindex="0" value="" onchange="" onblur="loadAccountInfo( this.name, 'newSourceLine.account.accountName');" id="newSourceLine.accountNumber" style="" class="" title="* Account Number" type="text"><input tabindex="32767" name="methodToCall.performLookup.(!!org.kuali.kfs.coa.businessobject.Account!!).(((chartOfAccountsCode:newSourceLine.chartOfAccountsCode,accountNumber:newSourceLine.accountNumber))).((#newSourceLine.chartOfAccountsCode:chartOfAccountsCode#)).((&lt;&gt;)).(([])).((**)).((^^)).((&amp;&amp;)).((//)).((~~)).anchor" src="https://test.kuali.org/kuali-cnv/kr/static/images/searchicon.gif" class="tinybutton" valign="middle" alt="Search Account Number" title="Search Account Number" type="image" border="0"></span>

<input name="newSourceLine.accountExpiredOverride" value="No" type="hidden"><input name="newSourceLine.accountExpiredOverrideNeeded" value="No" type="hidden"><input name="newSourceLine.nonFringeAccountOverride" value="No" type="hidden"><input name="newSourceLine.nonFringeAccountOverrideNeeded" value="No" type="hidden"><br>
    <div id="newSourceLine.account.accountName.div" class="fineprint">
    </div>

<input name="newSourceLine.account.accountName" value="" type="hidden"></td>

<td class="infoline" rowspan="" nowrap="nowrap"><div align="center">
            <input name="methodToCall.insertSourceLine.anchoraccountingSourceAnchor" src="https://test.kuali.org/kuali-cnv/kr/static/images/tinybutton-add1.gif" class="tinybutton" title="Add an Accounting Line" alt="Add an Accounting Line" type="image"><br>

    <div id="..div" class="fineprint">
    </div>
</div>
        </td>
    </tr>
<!--  baselineLine = baselineSourceAccountingLine[0] -->
    <!-- Editable = true ; -->
    <tr>

	<th rowspan="1" colspan="1" scope="row">&nbsp;
    1:<input name="document.sourceAccountingLine[0].postingYear" value="2009" type="hidden"><input name="baselineSourceAccountingLine[0].postingYear" value="2009" type="hidden"><input name="document.sourceAccountingLine[0].overrideCode" value="N" type="hidden"><input name="baselineSourceAccountingLine[0].overrideCode" value="N" type="hidden"><input name="document.sourceAccountingLine[0].sequenceNumber" value="1" type="hidden"><input name="baselineSourceAccountingLine[0].sequenceNumber" value="1" type="hidden"><input name="document.sourceAccountingLine[0].versionNumber" value="" type="hidden"><input name="baselineSourceAccountingLine[0].versionNumber" value="" type="hidden"><input name="document.sourceAccountingLine[0].documentNumber" value="319112" type="hidden"><input name="baselineSourceAccountingLine[0].documentNumber" value="319112" type="hidden"><input name="document.sourceAccountingLine[0].budgetAdjustmentPeriodCode" value="" type="hidden"><input name="baselineSourceAccountingLine[0].budgetAdjustmentPeriodCode" value="" type="hidden"><input name="document.sourceAccountingLine[0].fringeBenefitIndicator" value="No" type="hidden"><input name="baselineSourceAccountingLine[0].fringeBenefitIndicator" value="No" type="hidden"></th>
<td class="datacell" colspan="10" rowspan="1" valign="top">
123456
</td>
<td class="datacell" rowspan="" nowrap="nowrap">
            <div align="center">
                <input name="sourceLineDecorator[0].revertible" value="false" type="hidden"><input name="methodToCall.deleteSourceLine.line0.anchoraccountingSourceAnchor" src="https://test.kuali.org/kuali-cnv/kr/static/images/tinybutton-delete1.gif" class="tinybutton" title="Delete Accounting Line 1" alt="Delete Accounting Line 1" type="image">
        </td>
    </tr>

<!--  hit form method to increment tab index -->
<input name="tabStates(InformationLines-document-informationLine0)" value="CLOSE" type="hidden"><tr>
    <th>&nbsp;</th>
    <td class="total-line" colspan="12" style="padding: 0px;">
        <table class="datatable" style="width: 100%;">
            <tbody><tr>
                <td colspan="4" class="tab-subhead" style="border-right: medium none;">Information
                  <input name="methodToCall.toggleTab.tabInformationLines-document-InformationLine0" src="https://test.kuali.org/kuali-cnv/kr/static/images/tinybutton-show.gif" onclick="javascript: return toggleTab(document, 'InformationLines-document-informationLine0'); " id="tab-InformationLines-document-informationLine0-imageToggle" class="tinybutton" title="toggle" alt="show" type="image"></td>
            </tr>
        </tbody></table>    
            
        <div style="display: none;" id="tab-InformationLines-document-informationLine0-div">

        <table class="datatable" style="width: 100%;">
            <tbody><tr>
                <th scope="row"><div align="right">Asset Description:</div></th>
                <td valign="middle" align="left"><input name="document.sourceAccountingLine[0].financialDocumentMonth1LineAmount" maxlength="19" size="21" tabindex="0" value="0.00" onchange="" onblur="" id="document.sourceAccountingLine[0].financialDocumentMonth1LineAmount" style="" class="amount" title="Month 01 Line Amount" type="text"></td>
                <th scope="row"><div align="right">Organization Owner Chart of Accounts Code:</div></th>
                <td valign="middle" align="left"><input name="document.sourceAccountingLine[0].financialDocumentMonth7LineAmount" maxlength="19" size="21" tabindex="0" value="0.00" onchange="" onblur="" id="document.sourceAccountingLine[0].financialDocumentMonth7LineAmount" style="" class="amount" title="Month 07 Line Amount" type="text"></td>
            </tr>
            <tr>

                <th scope="row"><div align="right">Organization Owner Account Number:</div></th>
                <td valign="middle" align="left"><input name="document.sourceAccountingLine[0].financialDocumentMonth2LineAmount" maxlength="19" size="21" tabindex="0" value="0.00" onchange="" onblur="" id="document.sourceAccountingLine[0].financialDocumentMonth2LineAmount" style="" class="amount" title="Month 02 Line Amount" type="text"></td>
                <th scope="row"><div align="right">Organization Code :</div></th>
                <td valign="middle" align="left"><input name="document.sourceAccountingLine[0].financialDocumentMonth8LineAmount" maxlength="19" size="21" tabindex="0" value="0.00" onchange="" onblur="" id="document.sourceAccountingLine[0].financialDocumentMonth8LineAmount" style="" class="amount" title="Month 08 Line Amount" type="text"></td>
            </tr>
            <tr>
                <th scope="row"><div align="right">Asset Type Code:</div></th>
                <td valign="middle" align="left"><input name="document.sourceAccountingLine[0].financialDocumentMonth3LineAmount" maxlength="19" size="21" tabindex="0" value="0.00" onchange="" onblur="" id="document.sourceAccountingLine[0].financialDocumentMonth3LineAmount" style="" class="amount" title="Month 03 Line Amount" type="text"></td>
                <th scope="row"><div align="right">Vendor Name:</div></th>
                <td valign="middle" align="left"><input name="document.sourceAccountingLine[0].financialDocumentMonth9LineAmount" maxlength="19" size="21" tabindex="0" value="0.00" onchange="" onblur="" id="document.sourceAccountingLine[0].financialDocumentMonth9LineAmount" style="" class="amount" title="Month 09 Line Amount" type="text"></td>
            </tr>
            <tr>
                <th scope="row"><div align="right">In-Service Date:</div></th>
                <td valign="middle" align="left"><input name="document.sourceAccountingLine[0].financialDocumentMonth4LineAmount" maxlength="19" size="21" tabindex="0" value="0.00" onchange="" onblur="" id="document.sourceAccountingLine[0].financialDocumentMonth4LineAmount" style="" class="amount" title="Month 04 Line Amount" type="text"></td>
                <th scope="row"><div align="right"></div></th>
                <td valign="middle" align="left"></td>
            </tr>
            <tr>
                <th scope="row"><div align="right">Previous Cost:</div></th>
                <td valign="middle" align="left"></td>
                <th scope="row"><div align="right">New Total:</div></th>
                <td valign="middle" align="left"></td>
            </tr>
            
          </tbody></table>
        </div>

    </td>

<!--  hit form method to increment tab index -->
<input name="tabStates(PaymentLines-document-paymentLine0)" value="CLOSE" type="hidden"><tr>
    <th>&nbsp;</th>
    <td class="total-line" colspan="12" style="padding: 0px;">
        <table class="datatable" style="width: 100%;">
            <tbody><tr>
                <td colspan="4" class="tab-subhead" style="border-right: medium none;">Payments
                  <input name="methodToCall.toggleTab.tabPaymentLines-document-paymentLine0" src="https://test.kuali.org/kuali-cnv/kr/static/images/tinybutton-show.gif" onclick="javascript: return toggleTab(document, 'PaymentLines-document-paymentLine0'); " id="tab-PaymentLines-document-paymentLine0-imageToggle" class="tinybutton" title="toggle" alt="show" type="image"></td>
            </tr>
        </tbody></table>    
            
        <div style="display: none;" id="tab-PaymentLines-document-paymentLine0-div">

        <table class="datatable" style="width: 100%;">
          <tbody id="G516" style="">
            <tr>
              <th>&nbsp;</th>
              <th>Chart Of Accounts Code</th>
              <th>Account Number</th>
              <th>Sub Account Number</th>
              <th>Object Code</th>
              <th>Sub Object Code</th>
              <th>Project Code</th>
              <th>Purchase Order Number</th>
              <th>Requisition Number </th>
              <th>Doc #</th>
              <th>Type</th>
              <th>Posting Date</th>
              <th>Posting Year</th>
              <th>Posting Period Code</th>
              <th>Amount</th>
            </tr>
            <tr>
              <th>&nbsp;</th>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>

              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>

              <td>&nbsp;</td>
            </tr>
            <tr>
              <th>&nbsp;</th>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>

              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>

            </tr>
            <tr>
              <th>&nbsp;</th>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>

              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
            </tr>

            <tr>
              <td colspan="14" class="infoline"><div align="right"><strong>Total:</strong></div></td>
              <td class="infoline">&nbsp;</td>
            </tr>
          </tbody>
        </table>
        </div>

    </td>

</tr>
</table>

        </div>
</kul:tab>
