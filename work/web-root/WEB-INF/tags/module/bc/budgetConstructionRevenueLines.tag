<%--
 Copyright 2007-2009 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<c:if test="${!accountingLineScriptsLoaded}">
    <script type='text/javascript' src="dwr/interface/ObjectCodeService.js"></script>
    <script type='text/javascript' src="dwr/interface/SubObjectCodeService.js"></script> 
	<script language="JavaScript" type="text/javascript" src="scripts/sys/objectInfo.js"></script>
	<c:set var="accountingLineScriptsLoaded" value="true" scope="request" />
</c:if>

<c:set var="pbglRevenueAttributes" value="${DataDictionary.PendingBudgetConstructionGeneralLedger.attributes}" />
<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT] || KualiForm.systemViewOnly}" />
<c:set var="pbglRevPropertyName" value="document.pendingBudgetConstructionGeneralLedgerRevenueLines"/>

<fmt:formatNumber value="${KualiForm.document.revenueAccountLineAnnualBalanceAmountTotal}" 
        	var="formattedRevReqTotal" type="number" groupingUsed="true" />
        		
<kul:tab tabTitle="Revenue" defaultOpen="false" tabErrorKey="${BCConstants.BUDGET_CONSTRUCTION_REVENUE_TAB_ERRORS}" tabItemCount="${formattedRevReqTotal}">
<div class="tab-container" align=center>

        <table width="100%" border="0" cellpadding="0" cellspacing="0" class="datatable">
            <bc:subheadingWithDetailToggleRow
              columnCount="7"
              subheading="Revenue"
              usePercentAdj="${KualiForm.budgetableDocument}"
              readOnly="${readOnly}"/>
			<tr>
				<th>
				    &nbsp;	
				</th>
				<kul:htmlAttributeHeaderCell attributeEntry="${pbglRevenueAttributes.financialObjectCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${pbglRevenueAttributes.financialSubObjectCode}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${pbglRevenueAttributes.financialBeginningBalanceLineAmount}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${pbglRevenueAttributes.accountLineAnnualBalanceAmount}" />
				<kul:htmlAttributeHeaderCell attributeEntry="${pbglRevenueAttributes.percentChange}" />
				<th>
					Action
				</th>
			</tr>

            <c:if test="${!readOnly && KualiForm.budgetableDocument}">
              <c:set var="valuesMap" value="${KualiForm.newRevenueLine.valuesMap}"/>
                
			<tr>
              <kul:htmlAttributeHeaderCell literalLabel="Add:" scope="row" rowspan="1">
<%-- FIXME: hidden for JS lookups to work and to have fully qualified newline remove when fix is in place --%>
                  <%-- these hidden fields are inside a table cell to keep the HTML valid --%>
                  <html:hidden property="newRevenueLine.documentNumber"/>
                  <html:hidden property="newRevenueLine.universityFiscalYear"/>
                  <html:hidden property="newRevenueLine.chartOfAccountsCode"/>
                  <html:hidden property="newRevenueLine.accountNumber"/>
                  <html:hidden property="newRevenueLine.subAccountNumber"/>
                  <html:hidden property="newRevenueLine.financialBalanceTypeCode"/>
                  <html:hidden property="newRevenueLine.financialObjectTypeCode"/>
                  <html:hidden property="newRevenueLine.versionNumber"/>
                  <html:hidden property="newRevenueLine.financialBeginningBalanceLineAmount"/>
              </kul:htmlAttributeHeaderCell>

              <bc:pbglLineDataCell dataCellCssClass="infoline"
                  accountingLine="newRevenueLine"
                  field="financialObjectCode" detailFunction="loadObjectCodeInfo"
                  detailField="financialObject.financialObjectCodeName"
                  attributes="${pbglRevenueAttributes}" lookup="true" inquiry="true"
                  boClassSimpleName="ObjectCode"
                  readOnly="false"
                  displayHidden="false"
                  lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode"
                  lookupParameters="revenueObjectTypeCodesLookup:financialObjectTypeCode"
                  accountingLineValuesMap="${newRevenueLine.valuesMap}"
                  inquiryExtraKeyValues="universityFiscalYear=${KualiForm.document.universityFiscalYear}"
                  anchor="revenuenewLineLineAnchor" />

              <bc:pbglLineDataCell dataCellCssClass="infoline"
                  accountingLine="newRevenueLine"
                  field="financialSubObjectCode" detailFunction="loadSubObjectInfo"
                  detailFunctionExtraParam="'${KualiForm.document.universityFiscalYear}', "
                  detailField="financialSubObject.financialSubObjectCodeName"
                  attributes="${pbglRevenueAttributes}" lookup="true" inquiry="true"
                  boClassSimpleName="SubObjectCode"
                  readOnly="false"
                  displayHidden="false"
                  lookupOrInquiryKeys="universityFiscalYear,chartOfAccountsCode,financialObjectCode,accountNumber"
                  accountingLineValuesMap="${newRevenueLine.valuesMap}"
                  inquiryExtraKeyValues="universityFiscalYear=${KualiForm.document.universityFiscalYear}"
                  lookupAnchor="revenuenewLineLineAnchor" />

              <td class="infoline" nowrap><div align="right"><span>
                  &nbsp;
              </span></div></td>

              <bc:pbglLineDataCell dataCellCssClass="datacell"
                  accountingLine="newRevenueLine"
                  cellProperty="newRevenueLine.accountLineAnnualBalanceAmount"
                  attributes="${pbglRevenueAttributes}"
                  field="accountLineAnnualBalanceAmount"
                  fieldAlign="right"
                  readOnly="false"
                  rowSpan="1" dataFieldCssClass="amount" />

              <td class="infoline" nowrap><div align="right"><span>
                  &nbsp;
              </span></div></td>
              <td class="infoline" nowrap><div align="center">
                  <html:image property="methodToCall.insertRevenueLine.anchorrevenuenewLineLineAnchor" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" title="Add an Revenue Line" alt="Add an Revenue Line" styleClass="tinybutton"/>
              </div></td>
			</tr>
            </c:if>
            
			<c:forEach items="${KualiForm.document.pendingBudgetConstructionGeneralLedgerRevenueLines}" var="item" varStatus="status" >
			<c:set var="itemLineName" value="${pbglRevPropertyName}[${status.index}]"/>	

             <c:choose>
                <c:when test="${readOnly}">
                    <c:set var="lineIsEditable" value="false" />
                </c:when>
                <c:otherwise>
                    <c:set var="lineIsEditable" value="true" />
                </c:otherwise>
            </c:choose>
            <c:set var="rowspan" value="${ (!KualiForm.hideAdjustmentMeasurement && (lineIsEditable && !(empty item.financialBeginningBalanceLineAmount || item.financialBeginningBalanceLineAmount == 0))) ? 2: 1}"/>
 
            <tr>
              <kul:htmlAttributeHeaderCell scope="row" rowspan="${rowspan}">
              </kul:htmlAttributeHeaderCell>

              <bc:pbglLineDataCell dataCellCssClass="datacell"
                  accountingLine="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}]"
                  field="financialObjectCode" detailFunction="loadObjectCodeInfo"
                  detailField="financialObject.financialObjectCodeShortName"
                  attributes="${pbglRevenueAttributes}" lookup="true" inquiry="true"
                  boClassSimpleName="ObjectCode"
                  readOnly="true"
                  displayHidden="false"
                  rowSpan="${rowspan}"
                  lookupOrInquiryKeys="chartOfAccountsCode"
                  lookupUnkeyedFieldConversions="financialObjectTypeCode:document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}].objectTypeCode,"
                  accountingLineValuesMap="${item.valuesMap}"
                  inquiryExtraKeyValues="universityFiscalYear=${KualiForm.document.universityFiscalYear}"
                  anchor="revenueexistingLineLineAnchor${status.index}" />

              <c:set var="doLookupOrInquiry" value="false"/>
              <c:if test="${item.financialSubObjectCode ne KualiForm.dashFinancialSubObjectCode}">
                  <c:set var="doLookupOrInquiry" value="true"/>
              </c:if>

              <bc:pbglLineDataCell dataCellCssClass="datacell"
                  accountingLine="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}]"
                  field="financialSubObjectCode" detailFunction="loadSubObjectInfo"
                  detailFunctionExtraParam="'${KualiForm.document.universityFiscalYear}', "
                  detailField="financialSubObject.financialSubObjectCdshortNm"
                  attributes="${pbglRevenueAttributes}" lookup="${doLookupOrInquiry}" inquiry="${doLookupOrInquiry}"
                  boClassSimpleName="SubObjectCode"
                  readOnly="true"
                  displayHidden="false"
                  rowSpan="${rowspan}"
                  lookupOrInquiryKeys="chartOfAccountsCode,financialObjectCode,accountNumber"
                  accountingLineValuesMap="${item.valuesMap}"
                  inquiryExtraKeyValues="universityFiscalYear=${KualiForm.document.universityFiscalYear}" />

              <c:set var="fieldTrailerValue" value="" />
              <c:if test="${empty item.financialBeginningBalanceLineAmount}">
                  <c:set var="fieldTrailerValue" value="&nbsp;" />
              </c:if>
              <bc:pbglLineDataCell dataCellCssClass="datacell"
                  accountingLine="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}]"
                  cellProperty="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}].financialBeginningBalanceLineAmount"
                  attributes="${pbglRevenueAttributes}"
                  field="financialBeginningBalanceLineAmount"
                  fieldAlign="right"
                  readOnly="true"
                  fieldTrailerValue="${fieldTrailerValue}"
                  rowSpan="1" dataFieldCssClass="amount" />

              <bc:pbglLineDataCell dataCellCssClass="datacell"
                  accountingLine="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}]"
                  cellProperty="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}].accountLineAnnualBalanceAmount"
                  attributes="${pbglRevenueAttributes}"
                  field="accountLineAnnualBalanceAmount"
                  fieldAlign="right"
                  readOnly="${!lineIsEditable}"
                  rowSpan="1" dataFieldCssClass="amount" />

              <c:set var="fieldTrailerValue" value="" />
              <c:if test="${empty item.percentChange}">
                  <c:set var="fieldTrailerValue" value="&nbsp;" />
              </c:if>
			  <fmt:formatNumber value="${item.percentChange}" var="formattedNumber" type="number" groupingUsed="true" minFractionDigits="2" />
              <bc:pbglLineDataCell dataCellCssClass="datacell"
                  accountingLine="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}].percentChange"
                  cellProperty="document.pendingBudgetConstructionGeneralLedgerRevenueLines[${status.index}].percentChange"
                  attributes="${pbglRevenueAttributes}"
                  field="percentChange"
                  formattedNumberValue="${formattedNumber}"
                  fieldTrailerValue="${fieldTrailerValue}"
                  fieldAlign="right"
                  readOnly="true"
                  rowSpan="1" dataFieldCssClass="amount" />

             <td class="datacell" rowspan="${rowspan}" nowrap>
                 <div align="center">
				   <c:choose>
					 <c:when test="${empty item.budgetConstructionMonthly[0]}" > 
                       <c:if test="${lineIsEditable && KualiForm.budgetableDocument}">
						 <html:image src="${ConfigProperties.externalizable.images.url}tinybutton-createmonth.gif" styleClass="tinybutton" property="methodToCall.performMonthlyRevenueBudget.line${status.index}.anchorrevenueexistingLineLineAnchor${status.index}" title="Create Month" alt="Create Month"/>
                       </c:if>
                       <c:if test="${!(lineIsEditable && KualiForm.budgetableDocument)}">
						 <html:img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" styleClass="tinybutton" alt="" width="80" height="15"/>
                       </c:if>
					 </c:when> 
					 <c:otherwise> 
                       <c:choose>
                         <c:when test="${lineIsEditable}">
                           <html:image src="${ConfigProperties.externalizable.images.url}tinybutton-editmonth.gif" styleClass="tinybutton" property="methodToCall.performMonthlyRevenueBudget.line${status.index}.anchorrevenueexistingLineLineAnchor${status.index}" title="Edit Month" alt="Edit Month"/>
                         </c:when> 
                         <c:otherwise> 
                           <html:image src="${ConfigProperties.externalizable.images.url}tinybutton-viewmonth.gif" styleClass="tinybutton" property="methodToCall.performMonthlyRevenueBudget.line${status.index}.anchorrevenueexistingLineLineAnchor${status.index}" title="View Month" alt="View Month"/>
                         </c:otherwise> 
                       </c:choose>
						
					 </c:otherwise> 
				   </c:choose>

                   <html:image property="methodToCall.performBalanceInquiryForRevenueLine.line${status.index}.anchorrevenueexistingLineLineAnchor${status.index}" src="${ConfigProperties.externalizable.images.url}tinybutton-balinquiry.gif" title="Balance Inquiry For Revenue Line ${status.index}" alt="Balance Inquiry For Revenue Line ${status.index}" styleClass="tinybutton" />

				   <c:choose>
                     <c:when test="${lineIsEditable && (empty item.financialBeginningBalanceLineAmount || item.financialBeginningBalanceLineAmount == 0)}">
                       <html:image property="methodToCall.deleteRevenueLine.line${status.index}.anchorrevenueexistingLineLineAnchor${status.index}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" title="Delete Revenue Line ${status.index}" alt="Delete Revenue Line ${status.index}" styleClass="tinybutton"/>
                     </c:when>
					 <c:otherwise> 
					   <html:img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" styleClass="tinybutton" alt="" width="40" height="15"/>
					 </c:otherwise> 
				   </c:choose>
                 </div>
             </td>
            </tr>

            <c:if test="${rowspan == 2}">
              <tr>
               <td class="datacell" colspan = "3" nowrap><center>
				 <bc:requestAdjustment attributes="${pbglRevenueAttributes}" 
					 adjustmentAmountFieldName="${itemLineName}.adjustmentAmount"
					 methodToCall="adjustRevenueLinePercent"
					 lineIndex = "${status.index}"
					 anchor="anchorrevenueeexistingLineLineAnchor${status.index}"/>
				 </center>
               </td>
              </tr>
            </c:if>

			</c:forEach>

			<tr>
				<kul:htmlAttributeHeaderCell literalLabel="Revenue Totals" colspan="3" horizontal="true" />
                <bc:columnTotalCell dataCellCssClass="infoline"
                    cellProperty="document.revenueFinancialBeginningBalanceLineAmountTotal"
                    textStyle="${textStyle}"
                    fieldAlign="right"
                    colSpan="1" disableHiddenField="true" />
                <bc:columnTotalCell dataCellCssClass="infoline"
                    cellProperty="document.revenueAccountLineAnnualBalanceAmountTotal"
                    textStyle="${textStyle}"
                    fieldAlign="right"
                    colSpan="1" disableHiddenField="true" />
                <bc:columnTotalCell dataCellCssClass="infoline"
                    cellProperty="document.revenuePercentChangeTotal"
                    textStyle="${textStyle}"
                    fieldAlign="right"
                    colSpan="1" disableHiddenField="true" />
				<td class="infoline">&nbsp;</td>
			</tr>

            <c:if test="${!readOnly}">
	    	<tr>
			    <td colspan="7" class="subhead">
					<span class="subhead-left">Global Revenue Actions</span>
			    </td>
			</tr>

            <tr>
            <c:if test="${KualiForm.budgetableDocument}">
              <th colspan="3" nowrap>&nbsp;
		    	<a name="anchorrevenueControlsAnchor"></a>
              </th>
              <td colspan="3" class="datacell" nowrap><center>
				<bc:requestAdjustment attributes="${pbglRevenueAttributes}" 
					adjustmentAmountFieldName="revenueAdjustmentAmount"
					methodToCall="adjustAllRevenueLinesPercent"
					anchor="anchorrevenueControlsAnchor"/>
				</center>
              </td>
              <td colspan="1" class="datacell" nowrap>
                <div align="center">
                  <html:image property="methodToCall.refresh.anchorrevenueControlsAnchor" src="${ConfigProperties.externalizable.images.url}tinybutton-refresh.gif" title="Refresh" alt="Refresh" styleClass="tinybutton" />
                  <html:image property="methodToCall.performRevMonthSpread.anchorrevenueControlsAnchor" src="${ConfigProperties.externalizable.images.url}tinybutton-monthspread.gif" title="Monthly Spread" alt="Monthly Spread" styleClass="tinybutton" />
                  <html:image property="methodToCall.performRevMonthDelete.anchorrevenueControlsAnchor" src="${ConfigProperties.externalizable.images.url}tinybutton-monthdelete.gif" title="Monthly Delete" alt="Monthly Delete" styleClass="tinybutton"/>
                </div>
              </td>
	        </c:if>
            <c:if test="${!KualiForm.budgetableDocument}">
              <td colspan="6" class="datacell" nowrap>&nbsp;
              </td>
              <td colspan="1" class="datacell" nowrap>
                <div align="center">
                  <html:image property="methodToCall.performRevMonthDelete.anchorrevenueControlsAnchor" src="${ConfigProperties.externalizable.images.url}tinybutton-monthdelete.gif" title="Monthly Delete" alt="Monthly Delete" styleClass="tinybutton"/>
                </div>
              </td>
	        </c:if>
	        </tr>
	        </c:if>

		</table>

</div>
</kul:tab>
