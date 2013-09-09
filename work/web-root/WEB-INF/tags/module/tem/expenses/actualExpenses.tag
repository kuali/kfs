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
<%@ taglib uri="/WEB-INF/tlds/temfunc.tld" prefix="temfunc"%>

<c:set var="otherExpenseAttributes" value="${DataDictionary.ActualExpense.attributes}" />
<c:set var="temExtension" value="${DataDictionary.ExpenseTypeObjectCode.attributes}" />
<c:set var="isTA" value="${KualiForm.isTravelAuthorizationDoc}" />

<jsp:useBean id="paramMap" class="java.util.HashMap" />
<c:set target="${paramMap}" property="tripType" value="${KualiForm.document.tripTypeCode}" />
<c:set target="${paramMap}" property="travelerType" value="${KualiForm.document.traveler.travelerTypeCode}" />
<c:set target="${paramMap}" property="documentType" value="${KualiForm.docTypeName}" />
  	
	<div class="tab-container" align="center">
	    <h3>${KualiForm.expenseLabel}</h3>
	    <table cellpadding="0" cellspacing="0" class="datatable" summary="Actual Expenses">
			<tr>
				<td colspan="12" class="tab-subhead">* All fields required if section is used</td>
			</tr>
					
	        <c:if test="${fullEntryMode}">
	        	<tem-exp:actualExpenseHeader detailObject="${KualiForm.newActualExpenseLine }" />
				<tem-exp:actualExpenseLine expense="newActualExpenseLine" detailObject="${KualiForm.newActualExpenseLine}" />
	        </c:if>
	        
			<logic:iterate indexId="ctr" name="KualiForm" property="document.actualExpenses" id="currentLine">
				<c:set var="lineCounter" value="${lineCounter + 1}" />
				<tr>
	       			<td class="infoline" colspan="12"><div align="center">&nbsp;</div></td>
	       		</tr>
	        	<tem-exp:actualExpenseHeader lineNumber="${lineCounter}" detailObject="${KualiForm.document.actualExpenses[ctr]}" />
	        	<tem-exp:actualExpenseLine lineNumber="${lineCounter}" expense="document.actualExpenses[${ctr}]" detailObject="${KualiForm.document.actualExpenses[ctr]}" />

			  	<%-- Expense Details SubTab --%>
				<tr>
				  	<td colspan="12">
			  			<kul:subtab lookedUpCollectionName="expenseDetails${ctr}" width="${tableWidth}" 
			  				subTabTitle="${KualiForm.expenseLabel} Details - ${KualiForm.document.actualExpenses[ctr].expenseTypeObjectCode.expenseType.name} - ${lineCounter}" 
			  				noShowHideButton="false" 
			  				open="${KualiForm.document.actualExpenses[ctr].defaultTabOpen}">
			  				<table cellpadding="0" cellspacing="0" class="datatable">
								<c:set var="detailLineCounter" value="0" />
							    <tr>
							    	<th colspan="1">&nbsp;</th>
							        <td colspan="11">
	          							<table cellpadding="0" cellspacing="0" class="datatable">
	          								<tem-exp:actualExpenseDetailHeader detailObject="${KualiForm.document.actualExpenses[ctr]}" />
	          								<c:if test="${fullEntryMode}">
												<tem-exp:actualExpenseDetailLine 
													lineNumber="${ctr}" 
													detail="newActualExpenseLines[${ctr}]" 
													detailObject="${KualiForm.document.actualExpenses[ctr]}"
													parentObject="${KualiForm.document.actualExpenses[ctr]}"  />
											</c:if>
											<logic:iterate indexId="ctrDetail" name="currentLine" property="expenseDetails" id="currentLineDetails">
												<c:set var="lineCounterDetails" value="${lineCounterDetails + 1}" />
												<tem-exp:actualExpenseDetailLine
													detail="document.actualExpenses[${ctr}].expenseDetails[${ctrDetail}]" 
													lineNumber="${ctr}" 
													detailLineNumber="${ctrDetail}"
													detailObject="${KualiForm.document.actualExpenses[ctr].expenseDetails[ctrDetail]}"
													parentObject="${KualiForm.document.actualExpenses[ctr]}" />							           
											</logic:iterate>
	          							</table>
	          						</td>
	          					</tr>
	          				</table>
	          			</kul:subtab>
	          		</td>
	          	</tr>
			</logic:iterate>
		</table>
	</div>	
		          									
		          									
				
