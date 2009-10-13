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

<kul:tab tabTitle="Non-Employee Travel Expense" defaultOpen="false" tabErrorKey="${KFSConstants.DV_NON_EMPL_TRAVEL_TAB_ERRORS}">
	<c:set var="nonEmplTravelAttributes" value="${DataDictionary.DisbursementVoucherNonEmployeeTravel.attributes}" />
    <c:set var="travelExpenseAttributes" value="${DataDictionary.DisbursementVoucherNonEmployeeExpense.attributes}" />
   
    <div class="tab-container" align=center > 
<h3>Non-Employee Travel Expense</h3>
	  <table cellpadding=0 class="datatable" summary="Non-Employee Travel Section">
           
            <tr>
              <td colspan="2" class="tab-subhead">Traveler Information </td>
            </tr>
            <tr>
              <th width="35%" ><div align="right"><kul:htmlAttributeLabel attributeEntry="${nonEmplTravelAttributes.disbVchrNonEmpTravelerName}"/></div></th>
              <td width="65%" ><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrNonEmpTravelerName}" property="document.dvNonEmployeeTravel.disbVchrNonEmpTravelerName" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
            </tr>
            <tr>
              <th ><div align="right"><kul:htmlAttributeLabel attributeEntry="${nonEmplTravelAttributes.disbVchrServicePerformedDesc}"/></div></th>
              <td valign="top" ><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrServicePerformedDesc}" property="document.dvNonEmployeeTravel.disbVchrServicePerformedDesc" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
            </tr>
            <tr>
              <th ><div align="right"><kul:htmlAttributeLabel attributeEntry="${nonEmplTravelAttributes.dvServicePerformedLocName}"/></div></th>
              <td ><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.dvServicePerformedLocName}" property="document.dvNonEmployeeTravel.dvServicePerformedLocName" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
            </tr>
            <tr>
              <th ><div align="right"><kul:htmlAttributeLabel attributeEntry="${nonEmplTravelAttributes.dvServiceRegularEmprName}"/></div></th>
              <td ><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.dvServiceRegularEmprName}" property="document.dvNonEmployeeTravel.dvServiceRegularEmprName" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
            </tr>
      </table>
         
        <table cellpadding=0 class="datatable" summary="Destination">
            <tr>
              <td colspan="6" class="tab-subhead">Destination</td>
            </tr>
            <tr>
              <th>&nbsp;</th>
              <th> <div align=left><kul:htmlAttributeLabel attributeEntry="${nonEmplTravelAttributes.disbVchrTravelFromCityName}"/></div></th>
              <th> <div align=left><kul:htmlAttributeLabel attributeEntry="${nonEmplTravelAttributes.disbVchrTravelFromStateCode}"/>*US only</div></th>
              <th> <div align=left><kul:htmlAttributeLabel attributeEntry="${nonEmplTravelAttributes.dvTravelFromCountryCode}"/></div></th>
              <th> <div align=left><kul:htmlAttributeLabel attributeEntry="${nonEmplTravelAttributes.perDiemEndDateTime}"/></div></th>
            </tr>
            
            <tr>
              <th scope="row"><div align="right">From:</div></th>
              <td valign=top><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrTravelFromCityName}" property="document.dvNonEmployeeTravel.disbVchrTravelFromCityName" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
              <td valign=top><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrTravelFromStateCode}" property="document.dvNonEmployeeTravel.disbVchrTravelFromStateCode" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
              <td valign=top><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.dvTravelFromCountryCode}" property="document.dvNonEmployeeTravel.dvTravelFromCountryCode" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
              <td valign=top><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.perDiemStartDateTime}" property="document.dvNonEmployeeTravel.perDiemStartDateTime" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
              <c:if test="${fullEntryMode||travelEntryMode}">
              <img src="${ConfigProperties.kr.externalizable.images.url}cal.gif" id="document.dvNonEmployeeTravel.perDiemStartDateTime_datepicker" style="cursor: pointer;" title="Date selector" alt="Date selector" onmouseover="this.style.backgroundColor='red';" onmouseout="this.style.backgroundColor='transparent';"	/>
                <script type="text/javascript">
                  Calendar.setup(
                          {
                            inputField : "document.dvNonEmployeeTravel.perDiemStartDateTime", // ID of the input field
                            ifFormat : "%m/%d/%Y %I:%M %p", // the date format
                            button : "document.dvNonEmployeeTravel.perDiemStartDateTime_datepicker", // ID of the button
                            showsTime: true,
                            timeFormat: "12"
                          }
                  );
               </script>
              </c:if> 
              </td>
              </td>
            </tr>
            
            <tr>
              <th scope="row"><div align="right">To:</div></th>
              <td valign=top><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrTravelToCityName}" property="document.dvNonEmployeeTravel.disbVchrTravelToCityName" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
              <td valign=top><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrTravelToStateCode}" property="document.dvNonEmployeeTravel.disbVchrTravelToStateCode" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
              <td valign=top><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrTravelToCountryCode}" property="document.dvNonEmployeeTravel.disbVchrTravelToCountryCode" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
              <td valign=top><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.perDiemStartDateTime}" property="document.dvNonEmployeeTravel.perDiemEndDateTime" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
              <c:if test="${fullEntryMode||travelEntryMode}">
              <img src="${ConfigProperties.kr.externalizable.images.url}cal.gif" id="document.dvNonEmployeeTravel.perDiemEndDateTime_datepicker" style="cursor: pointer;" title="Date selector" alt="Date selector" onmouseover="this.style.backgroundColor='red';" onmouseout="this.style.backgroundColor='transparent';"	/>
                <script type="text/javascript">
                  Calendar.setup(
                          {
                            inputField : "document.dvNonEmployeeTravel.perDiemEndDateTime", // ID of the input field
                            ifFormat : "%m/%d/%Y %I:%M %p", // the date format
                            button : "document.dvNonEmployeeTravel.perDiemEndDateTime_datepicker", // ID of the button
                            showsTime: true,
                            timeFormat: "12"
                          }
                  );
               </script>
               </c:if>
              </td>
            </tr>
      </table>
          
          <table cellpadding=0 class="datatable" summary="Per Diem, Personal Vehicle ">
            <tr>
              <td colspan="2" class="tab-subhead">Per Diem<br/>* All fields required if section is used.</td>
              <td colspan="2" class="tab-subhead">Personal Vehicle<br/>* All fields required if section is used.</td>
            </tr>
            
            <tr>
              <th><div align="right"><kul:htmlAttributeLabel attributeEntry="${nonEmplTravelAttributes.disbVchrPerdiemCategoryName}"/></div></th>
              <td><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrPerdiemCategoryName}" property="document.dvNonEmployeeTravel.disbVchrPerdiemCategoryName" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
              <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${nonEmplTravelAttributes.disbVchrAutoFromCityName}"/></div></th>
              <td><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrAutoFromCityName}" property="document.dvNonEmployeeTravel.disbVchrAutoFromCityName" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                 <kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrAutoFromStateCode}" property="document.dvNonEmployeeTravel.disbVchrAutoFromStateCode" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
              </td>
            </tr>

            <tr>
              <th ><div align="right"><kul:htmlAttributeLabel attributeEntry="${nonEmplTravelAttributes.disbVchrPerdiemRate}"/></div></th>
              <td><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrPerdiemRate}" property="document.dvNonEmployeeTravel.disbVchrPerdiemRate" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                &nbsp;&nbsp;<a href="dvPerDiem.do?methodToCall=showTravelPerDiemLinks" tabindex="${KualiForm.nextArbitrarilyHighIndex}" target="_blank">Per Diem Links</a>
              </td>
              <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${nonEmplTravelAttributes.disbVchrAutoToCityName}"/></div></th>
              <td><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrAutoToCityName}" property="document.dvNonEmployeeTravel.disbVchrAutoToCityName" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                 <kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrAutoToStateCode}" property="document.dvNonEmployeeTravel.disbVchrAutoToStateCode" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
              </td>
            </tr>

            <tr>
              <th><div align="right"><kul:htmlAttributeLabel attributeEntry="${nonEmplTravelAttributes.disbVchrPerdiemCalculatedAmt}"/></div></th>
              <td><div class="left"><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrPerdiemCalculatedAmt}" property="document.dvNonEmployeeTravel.disbVchrPerdiemCalculatedAmt" readOnly="true"/></div>
              <c:if test="${fullEntryMode||travelEntryMode}">
                   <div class="right"><html:image src="${ConfigProperties.kr.externalizable.images.url}tinybutton-clear1.gif" styleClass="tinybutton" property="methodToCall.clearTravelPerDiem" alt="Clear Per Diem" title="Clear Per Diem"/></div>
                   <div class="right"><html:image src="${ConfigProperties.externalizable.images.url}tinybutton-calculate.gif" styleClass="tinybutton" property="methodToCall.calculateTravelPerDiem" alt="Calculate Per Diem" title="Calculate Per Diem"/></div>
              </c:if>
              </td>
              <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${nonEmplTravelAttributes.disbVchrAutoRoundTripCode}"/></div></th>
              <td><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrAutoRoundTripCode}" property="document.dvNonEmployeeTravel.disbVchrAutoRoundTripCode" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
            </tr>
            
            <tr>
              <th><div align="right"><kul:htmlAttributeLabel attributeEntry="${nonEmplTravelAttributes.disbVchrPerdiemActualAmount}"/></div></th>
              <td><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrPerdiemActualAmount}" property="document.dvNonEmployeeTravel.disbVchrPerdiemActualAmount" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
              <th scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${nonEmplTravelAttributes.dvPersonalCarMileageAmount}"/></div></th>
              <td><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.dvPersonalCarMileageAmount}" property="document.dvNonEmployeeTravel.dvPersonalCarMileageAmount" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
            </tr>
            
            <tr>
              <th rowspan="2"><div align="right"><kul:htmlAttributeLabel attributeEntry="${nonEmplTravelAttributes.dvPerdiemChangeReasonText}"/></div></th>
              <td rowspan="2"><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.dvPerdiemChangeReasonText}" property="document.dvNonEmployeeTravel.dvPerdiemChangeReasonText" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
              <th><div align="right"><kul:htmlAttributeLabel attributeEntry="${nonEmplTravelAttributes.disbVchrMileageCalculatedAmt}"/></div></th>
              <td><div class="left"><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrMileageCalculatedAmt}" property="document.dvNonEmployeeTravel.disbVchrMileageCalculatedAmt" readOnly="true"/></div>
              <c:if test="${fullEntryMode||travelEntryMode}">
                   <div class="right"><html:image src="${ConfigProperties.kr.externalizable.images.url}tinybutton-clear1.gif" styleClass="tinybutton" property="methodToCall.clearTravelMileageAmount" alt="Clear Total Mileage" title="Clear Total Mileage"/></div>
                   <div class="right"><html:image src="${ConfigProperties.externalizable.images.url}tinybutton-calculate.gif" styleClass="tinybutton" property="methodToCall.calculateTravelMileageAmount" alt="Calculate Total Mileage" title="Calculate Total Mileage"/></div>
              </c:if>
              </td>
            </tr>

            <tr>
              <th ><div align="right"><kul:htmlAttributeLabel attributeEntry="${nonEmplTravelAttributes.disbVchrPersonalCarAmount}"/></div></th>
              <td><kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.disbVchrPersonalCarAmount}" property="document.dvNonEmployeeTravel.disbVchrPersonalCarAmount" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
            </tr>
          </table>

          <table cellpadding="0" class="datatable" summary="Travel Reimbursements">
                <tbody>
                  <tr>
                    <td colspan="5" class="tab-subhead">Traveler Expenses<br/>* All fields required if section is used</td>
                  </tr>
           <tr>
              <th width="10">&nbsp;</th>
              <th> <div align=left><kul:htmlAttributeLabel attributeEntry="${travelExpenseAttributes.disbVchrExpenseCode}"/></div></th>
              <th> <div align=left><kul:htmlAttributeLabel attributeEntry="${travelExpenseAttributes.disbVchrExpenseCompanyName}"/></div></th>
              <th> <div align=left><kul:htmlAttributeLabel attributeEntry="${travelExpenseAttributes.disbVchrExpenseAmount}"/></div></th>
			  <c:if test="${fullEntryMode||travelEntryMode}">
	              <th> <div align=center>Actions</div></th>
			  </c:if>
            </tr>
            
            <c:if test="${fullEntryMode||travelEntryMode}">
            <tr>
              <th  scope="row"><div align="right">add:</div></th>
              <td valign=top class="infoline"><kul:htmlControlAttribute attributeEntry="${travelExpenseAttributes.disbVchrExpenseCode}" property="newNonEmployeeExpenseLine.disbVchrExpenseCode" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
              <td valign=top class="infoline"><kul:htmlControlAttribute attributeEntry="${travelExpenseAttributes.disbVchrExpenseCompanyName}" property="newNonEmployeeExpenseLine.disbVchrExpenseCompanyName" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                  <kul:lookup boClassName="org.kuali.kfs.fp.businessobject.TravelCompanyCode" fieldConversions="name:newNonEmployeeExpenseLine.disbVchrExpenseCompanyName,code:newNonEmployeeExpenseLine.disbVchrExpenseCode" fieldLabel="${travelExpenseAttributes.disbVchrExpenseCompanyName.label}" lookupParameters="'N':travelExpenseTypeCode.prepaidExpense" readOnlyFields="travelExpenseTypeCode.prepaidExpense"/>
              </td>
              <td valign=top nowrap class="infoline"><div align="center">
                  <kul:htmlControlAttribute attributeEntry="${travelExpenseAttributes.disbVchrExpenseAmount}" property="newNonEmployeeExpenseLine.disbVchrExpenseAmount" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
              </div></td>
	              <td class="infoline"><div align=center>
		               <html:image src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="tinybutton" property="methodToCall.addNonEmployeeExpenseLine" alt="Add Expense Line" title="Add Expense Line"/>
	              </div></td>
            </tr>
            </c:if>
            
            <logic:iterate indexId="ctr" name="KualiForm" property="document.dvNonEmployeeTravel.dvNonEmployeeExpenses" id="currentLine">
                <tr>
                  <th scope="row"><div align="right"><kul:htmlControlAttribute attributeEntry="${travelExpenseAttributes.financialDocumentLineNumber}" property="document.dvNonEmployeeTravel.dvNonEmployeeExpenses[${ctr}].financialDocumentLineNumber" readOnly="true"/></div></th>
                  <td valign=top><kul:htmlControlAttribute attributeEntry="${travelExpenseAttributes.disbVchrExpenseCode}" property="document.dvNonEmployeeTravel.dvNonEmployeeExpenses[${ctr}].disbVchrExpenseCode" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
                  <td valign=top><kul:htmlControlAttribute attributeEntry="${travelExpenseAttributes.disbVchrExpenseCompanyName}" property="document.dvNonEmployeeTravel.dvNonEmployeeExpenses[${ctr}].disbVchrExpenseCompanyName" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                     <c:if test="${fullEntryMode||travelEntryMode}">
                      <kul:lookup boClassName="org.kuali.kfs.fp.businessobject.TravelCompanyCode" fieldConversions="name:document.dvNonEmployeeTravel.dvNonEmployeeExpenses[${ctr}].disbVchrExpenseCompanyName,code:document.dvNonEmployeeTravel.dvNonEmployeeExpenses[${ctr}].disbVchrExpenseCode" fieldLabel="${travelExpenseAttributes.disbVchrExpenseCompanyName.label}" lookupParameters="'N':travelExpenseTypeCode.prepaidExpense" readOnlyFields="travelExpenseTypeCode.prepaidExpense"/>
                    </c:if>
                  </td>   
                  <td valign=top nowrap ><div align="center">
                     <kul:htmlControlAttribute attributeEntry="${travelExpenseAttributes.disbVchrExpenseAmount}" property="document.dvNonEmployeeTravel.dvNonEmployeeExpenses[${ctr}].disbVchrExpenseAmount" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                  </div></td>
				  <c:if test="${fullEntryMode||travelEntryMode}">
	                  <td><div align=center>
	                     <html:image src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" styleClass="tinybutton" property="methodToCall.deleteNonEmployeeExpenseLine.line${ctr}" alt="Delete Expense Line" title="Delete Expense Line"/>
	                  </div></td>
				  </c:if>
                </tr>
           </logic:iterate>


                  <tr>
                    <th colspan="3" class="infoline" scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${nonEmplTravelAttributes.totalExpenseAmount}"/></div></th>
                    <td valign="top" nowrap="nowrap" class="infoline"><div align="center"><strong>$ ${KualiForm.document.dvNonEmployeeTravel.totalExpenseAmount}</strong></div></td>
					<c:if test="${fullEntryMode||travelEntryMode}">
	                    <td class="infoline">&nbsp;</td>
					</c:if>
                  </tr>
				  <tr>
                    <td colspan="5" class="tab-subhead">Travel Expenses Total</td>
                  </tr>
                  <tr>
                    <th colspan="3" class="infoline" scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${nonEmplTravelAttributes.totalTravelAmount}"/></div></th>
                    <td valign="top" nowrap="nowrap" class="infoline">
                    	<div align="center">
                    		<kul:checkErrors keyMatch="${KFSConstants.DV_CHECK_TRAVEL_TOTAL_ERROR}"/>
                    		$<kul:htmlControlAttribute attributeEntry="${nonEmplTravelAttributes.totalTravelAmount}" property="document.dvNonEmployeeTravel.totalTravelAmount" readOnly="true"/>
                    	</div>
                    </td>
					<c:if test="${fullEntryMode||travelEntryMode}">
                    	<td class="infoline">&nbsp;</td>
					</c:if>
                  </tr>
                  <tr>
                    <td colspan="5" class="tab-subhead">Pre Paid Expenses<br/>* All fields required if section is used</td>
                  </tr>
             <tr>
              <th>&nbsp;</th>
              <th> <div align=left><kul:htmlAttributeLabel attributeEntry="${travelExpenseAttributes.disbVchrPrePaidExpenseCode}"/></div></th>
              <th> <div align=left><kul:htmlAttributeLabel attributeEntry="${travelExpenseAttributes.disbVchrPrePaidExpenseCompanyName}"/></div></th>
              <th> <div align=left><kul:htmlAttributeLabel attributeEntry="${travelExpenseAttributes.disbVchrExpenseAmount}"/></div></th>
			  <c:if test="${fullEntryMode||travelEntryMode}">
              	  <th> <div align=center>Actions</div></th>
			  </c:if>
            </tr>
            
            <c:if test="${fullEntryMode||travelEntryMode}">
            <tr>
              <th scope="row"><div align="right">add:</div></th>
              <td valign=top class="infoline"><kul:htmlControlAttribute attributeEntry="${travelExpenseAttributes.disbVchrPrePaidExpenseCode}" property="newPrePaidNonEmployeeExpenseLine.disbVchrPrePaidExpenseCode" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
              <td valign=top class="infoline"><kul:htmlControlAttribute attributeEntry="${travelExpenseAttributes.disbVchrPrePaidExpenseCompanyName}" property="newPrePaidNonEmployeeExpenseLine.disbVchrPrePaidExpenseCompanyName" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                <kul:lookup boClassName="org.kuali.kfs.fp.businessobject.TravelCompanyCode" fieldConversions="name:newPrePaidNonEmployeeExpenseLine.disbVchrPrePaidExpenseCompanyName,code:newPrePaidNonEmployeeExpenseLine.disbVchrPrePaidExpenseCode" fieldLabel="${travelExpenseAttributes.disbVchrPrePaidExpenseCompanyName.label}" lookupParameters="'Y':travelExpenseTypeCode.prepaidExpense" readOnlyFields="travelExpenseTypeCode.prepaidExpense"/>
              </td> 
              <td valign=top nowrap class="infoline"><div align="center">
                <kul:htmlControlAttribute attributeEntry="${travelExpenseAttributes.disbVchrExpenseAmount}" property="newPrePaidNonEmployeeExpenseLine.disbVchrExpenseAmount" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
              </div></td>
	            <td class="infoline"><div align=center>
	              <html:image src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="tinybutton" property="methodToCall.addPrePaidNonEmployeeExpenseLine" alt="Add Expense Line" title="Add Expense Line"/>
	            </div></td>
            </tr>
            </c:if>
            
            <logic:iterate indexId="ctr2" name="KualiForm" property="document.dvNonEmployeeTravel.dvPrePaidEmployeeExpenses" id="currentLine">
                <tr>
                  <th scope="row"><div align="right"><kul:htmlControlAttribute attributeEntry="${travelExpenseAttributes.financialDocumentLineNumber}" property="document.dvNonEmployeeTravel.dvPrePaidEmployeeExpenses[${ctr2}].financialDocumentLineNumber" readOnly="true"/></div></th>
                  <td valign=top ><kul:htmlControlAttribute attributeEntry="${travelExpenseAttributes.disbVchrPrePaidExpenseCode}" property="document.dvNonEmployeeTravel.dvPrePaidEmployeeExpenses[${ctr2}].disbVchrExpenseCode" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
                  <td valign=top ><kul:htmlControlAttribute attributeEntry="${travelExpenseAttributes.disbVchrPrePaidExpenseCompanyName}" property="document.dvNonEmployeeTravel.dvPrePaidEmployeeExpenses[${ctr2}].disbVchrExpenseCompanyName" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                    <c:if test="${fullEntryMode||travelEntryMode}">
                      <kul:lookup boClassName="org.kuali.kfs.fp.businessobject.TravelCompanyCode" fieldConversions="name:document.dvNonEmployeeTravel.dvPrePaidEmployeeExpenses[${ctr2}].disbVchrExpenseCompanyName,code:document.dvNonEmployeeTravel.dvPrePaidEmployeeExpenses[${ctr2}].disbVchrExpenseCode" lookupParameters="'Y':travelExpenseTypeCode.prepaidExpense" readOnlyFields="travelExpenseTypeCode.prepaidExpense"/>
                    </c:if>
                  </td> 
                  <td valign=top nowrap ><div align="center">
                     <kul:htmlControlAttribute attributeEntry="${travelExpenseAttributes.disbVchrExpenseAmount}" property="document.dvNonEmployeeTravel.dvPrePaidEmployeeExpenses[${ctr2}].disbVchrExpenseAmount" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
                  </div></td>
				  <c:if test="${fullEntryMode||travelEntryMode}">
	                  <td><div align=center>
	                      <html:image src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" styleClass="tinybutton" property="methodToCall.deletePrePaidEmployeeExpenseLine.line${ctr2}" alt="Delete Expense Line" title="Delete Expense Line"/>
	                  </div></td>
                  </c:if>
                </tr>
           </logic:iterate>
               <tr>
                    <th colspan="3" class="infoline" scope="row"><div align="right"><kul:htmlAttributeLabel attributeEntry="${nonEmplTravelAttributes.totalPrePaidAmount}"/></div></th>
                    <td valign="top" nowrap="nowrap" class="infoline"><div align="center"><strong>$ ${KualiForm.document.dvNonEmployeeTravel.totalPrePaidAmount}</strong></div></td>
					<c:if test="${fullEntryMode||travelEntryMode}">
                    	<td class="infoline">&nbsp;</td>
                    </c:if>
                  </tr>
                  
                </tbody>
      </table>
       
    </div>
</kul:tab>
