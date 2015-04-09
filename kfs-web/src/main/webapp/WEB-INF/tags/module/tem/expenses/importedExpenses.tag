<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>
<%@ taglib uri="/WEB-INF/tlds/temfunc.tld" prefix="temfunc"%>
<c:set var="importedExpenseAttributes" value="${DataDictionary.ImportedExpense.attributes}" />
<c:set var="temExtension" value="${DataDictionary.ExpenseTypeObjectCode.attributes}" />

<jsp:useBean id="paramMap" class="java.util.HashMap" />
  
    <div class="tab-container" align="center">
    <h3>Imported Expenses</h3>
    <table cellpadding="0" cellspacing="0" class="datatable" summary="Imported Expenses">
		<tr>
			<td colspan="13" class="tab-subhead">
				<span class="left">* All fields required if section is used</span>
				<c:if test="${fullEntryMode}">
					<span class="right">
						<kul:multipleValueLookup boClassName="org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense" lookedUpBODisplayName="Imported Expense"
	        	        	lookedUpCollectionName="historicalTravelExpenses" anchor="${tabKey}" />
					</span>
				</c:if>
			</td>
		</tr>
		
		<logic:iterate indexId="ctr" name="KualiForm"
			property="document.importedExpenses" id="currentLine">
			<c:set var="lineCounter" value="${lineCounter + 1}" />
			<tr>
				<td class="infoline" colspan="13"><div align="center">&nbsp;</div></td>
	        </tr>
	        <tem-exp:importedExpenseHeader isCTS="${currentLine.cardType == TemConstants.CARD_TYPE_CTS}" lineNumber="${lineCounter}" />	        
			<tem-exp:importedExpenseLine lineNumber="${lineCounter}" expense="document.importedExpenses[${ctr }]" detailObject="${KualiForm.document.importedExpenses[ctr]}" />
			
			<tr>
		  		<td colspan="13">
		  			<c:if test="${(currentLine.cardType != TemConstants.CARD_TYPE_CTS && KualiForm.canShowImportExpenseDetails)}">
		  				<kul:subtab lookedUpCollectionName="details${ctr}" 
					  		width="${tableWidth}" 
					  		subTabTitle="Imported Expense Details - ${currentLine.expenseTypeObjectCode.expenseType.name} - ${lineCounter}" 
					  		noShowHideButton="false"
					  		open="${'' }${(fn:length(KualiForm.document.importedExpenses[ctr].expenseDetails) > 0) }">
					  		<table cellpadding="0" cellspacing="0" class="datatable">
				  				<tem-exp:importedExpenseDetailHeader />						            
						        <c:if test="${fullEntryMode}">
									<tem-exp:importedExpenseDetailLine 
										lineNumber="${ctr}" 
										detail="newImportedExpenseLines[${ctr}]" 
										parentObject="${KualiForm.document.importedExpenses[ctr]}"
										detailObject="${KualiForm.document.importedExpenses[ctr]}" />
								</c:if>
								<logic:iterate indexId="ctrDetail" name="currentLine"
									property="expenseDetails" id="currentLineDetails">
									<c:set var="lineCounterDetails" value="${lineCounterDetails + 1}" />
									<tem-exp:importedExpenseDetailLine 
										detail="document.importedExpenses[${ctr}].expenseDetails[${ctrDetail}]" 
										lineNumber="${ctr}" 
										parentObject="${KualiForm.document.importedExpenses[ctr]}"
										detailObject="${KualiForm.document.importedExpenses[ctr].expenseDetails[ctrDetail]}"
										detailLineNumber="${ctrDetail}" />							           
								</logic:iterate>
							</table>
				  		</kul:subtab>
		  			</c:if>
			  	</td>
			</tr>
		</logic:iterate>
	</table>
 </div>

