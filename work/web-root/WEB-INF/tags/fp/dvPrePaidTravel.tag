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

<kul:tab tabTitle="Pre-Paid Travel Expenses" defaultOpen="false" tabErrorKey="${KFSConstants.DV_PREPAID_TAB_ERRORS}">
	<c:set var="prePaidConfAttributes" value="${DataDictionary.DisbursementVoucherPreConferenceDetail.attributes}" />
	<c:set var="prePaidRegistrantAttributes" value="${DataDictionary.DisbursementVoucherPreConferenceRegistrant.attributes}" />
  
    <div class="tab-container" align=center > 
<h3>Pre-Paid Travel Expenses</h3>
    	<table cellpadding=0 class="datatable" summary="Pre-Paid Travel Section">

            
            <tr>
              <td colspan="2" class="tab-subhead">Overview</td>
            </tr>
            
            <tr>
              <th width="35%" ><div align="right"><kul:htmlAttributeLabel attributeEntry="${prePaidConfAttributes.dvConferenceDestinationName}"/></div></th>
              <td width="65%" ><kul:htmlControlAttribute attributeEntry="${prePaidConfAttributes.dvConferenceDestinationName}" property="document.dvPreConferenceDetail.dvConferenceDestinationName" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
            </tr>
            
            <tr>
              <th><div align="right"><kul:htmlAttributeLabel attributeEntry="${prePaidConfAttributes.disbVchrExpenseCode}"/></div></th>
              <td><kul:htmlControlAttribute attributeEntry="${prePaidConfAttributes.disbVchrExpenseCode}" property="document.dvPreConferenceDetail.disbVchrExpenseCode" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
            </tr>
 
            <tr>
              <th><div align="right"><kul:htmlAttributeLabel attributeEntry="${prePaidConfAttributes.disbVchrConferenceStartDate}"/></div></th>
              <td><kul:htmlControlAttribute attributeEntry="${prePaidConfAttributes.disbVchrConferenceStartDate}" datePicker="true" property="document.dvPreConferenceDetail.disbVchrConferenceStartDate" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
            </tr>           

            <tr>
              <th><div align="right"><kul:htmlAttributeLabel attributeEntry="${prePaidConfAttributes.disbVchrConferenceEndDate}"/></div></th>
              <td><kul:htmlControlAttribute attributeEntry="${prePaidConfAttributes.disbVchrConferenceEndDate}" datePicker="true" property="document.dvPreConferenceDetail.disbVchrConferenceEndDate" readOnly="${!fullEntryMode&&!travelEntryMode}"/></td>
            </tr> 
          </table>
            
           <table cellpadding=0 class="datatable" summary="Expenses">
            <tr>
              <td colspan="6" class="tab-subhead">Expenses</td>
            </tr>
            
            <tr>
              <th>&nbsp;</th>
              <th> <div align="center"><kul:htmlAttributeLabel attributeEntry="${prePaidRegistrantAttributes.dvConferenceRegistrantName}"/></div></th>
              <th> <div align="center"><kul:htmlAttributeLabel attributeEntry="${prePaidRegistrantAttributes.disbVchrPreConfDepartmentCd}"/></div></th>
              <th> <div align="center"><kul:htmlAttributeLabel attributeEntry="${prePaidRegistrantAttributes.dvPreConferenceRequestNumber}"/></div></th>
              <th> <div align="center"><kul:htmlAttributeLabel attributeEntry="${prePaidRegistrantAttributes.disbVchrExpenseAmount}"/></div></th>
              <c:if test="${fullEntryMode||travelEntryMode}">
	              <th> <div align=center>Actions</div></th>
	          </c:if>
            </tr>
            
            <tr>
              <th scope="row"><div align="center">&nbsp;
	              <c:if test="${fullEntryMode||travelEntryMode}">
    		          add:
            	  </c:if>
              </div></th>
              <td valign=top nowrap class="infoline"><div align="center"><span>
                  <kul:htmlControlAttribute attributeEntry="${prePaidRegistrantAttributes.dvConferenceRegistrantName}" property="newPreConferenceRegistrantLine.dvConferenceRegistrantName" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
              </span></div></td>
              <td valign=top nowrap class="infoline"><div align="center"><span>
                  <kul:htmlControlAttribute attributeEntry="${prePaidRegistrantAttributes.disbVchrPreConfDepartmentCd}" property="newPreConferenceRegistrantLine.disbVchrPreConfDepartmentCd" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
              </span></div></td>
              <td valign=top nowrap class="infoline"><div align="center"><span>
                  <kul:htmlControlAttribute attributeEntry="${prePaidRegistrantAttributes.dvPreConferenceRequestNumber}" property="newPreConferenceRegistrantLine.dvPreConferenceRequestNumber" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
              </span></div></td>
              <td valign=top nowrap class="infoline"><div align="center"><span>
                  <kul:htmlControlAttribute attributeEntry="${prePaidRegistrantAttributes.disbVchrExpenseAmount}" property="newPreConferenceRegistrantLine.disbVchrExpenseAmount" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
              </span></div></td>
              <c:if test="${fullEntryMode||travelEntryMode}">
	              <td class="infoline"><div align=center>
	                   <html:image src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" styleClass="tinybutton" property="methodToCall.addPreConfRegistrantLine"  title="Add Pre-Conference Registrant Line" alt="Add Pre-Conference Registrant Line"/>
	              </div></td>
              </c:if>
            </tr>
  
            <logic:iterate indexId="ctr" name="KualiForm" property="document.dvPreConferenceDetail.dvPreConferenceRegistrants" id="currentLine">
            <tr>
              <th scope="row"><div align="center"><kul:htmlControlAttribute attributeEntry="${prePaidRegistrantAttributes.financialDocumentLineNumber}" property="document.dvPreConferenceDetail.dvPreConferenceRegistrants[${ctr}].financialDocumentLineNumber" readOnly="true"/></div></th>
              <td valign=top nowrap><div align="center"><span>
                  <kul:htmlControlAttribute attributeEntry="${prePaidRegistrantAttributes.dvConferenceRegistrantName}" property="document.dvPreConferenceDetail.dvPreConferenceRegistrants[${ctr}].dvConferenceRegistrantName" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
              </span></div></td>
              <td valign=top nowrap><div align="center"><span>
                  <kul:htmlControlAttribute attributeEntry="${prePaidRegistrantAttributes.disbVchrPreConfDepartmentCd}" property="document.dvPreConferenceDetail.dvPreConferenceRegistrants[${ctr}].disbVchrPreConfDepartmentCd" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
              </span></div></td>
              <td valign=top nowrap><div align="center"><span>
                  <kul:htmlControlAttribute attributeEntry="${prePaidRegistrantAttributes.dvPreConferenceRequestNumber}" property="document.dvPreConferenceDetail.dvPreConferenceRegistrants[${ctr}].dvPreConferenceRequestNumber" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
              </span></div></td>
              <td valign=top nowrap><div align="center"><span>
                  <kul:htmlControlAttribute attributeEntry="${prePaidRegistrantAttributes.disbVchrExpenseAmount}" property="document.dvPreConferenceDetail.dvPreConferenceRegistrants[${ctr}].disbVchrExpenseAmount" readOnly="${!fullEntryMode&&!travelEntryMode}"/>
              </span></div></td>
              <c:if test="${fullEntryMode||travelEntryMode}">
	              <td><div align=center>
	                   <html:image src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" styleClass="tinybutton" property="methodToCall.deletePreConfRegistrantLine.line${ctr}" title="Delete Pre-Conference Registrant Line" alt="Delete Pre-Conference Registrant Line"/>
	              </div></td>
              </c:if>
            </tr>
            </logic:iterate>
            
            <tr>
                <td colspan="4" class="infoline" scope="row"><div align="right"><strong><kul:htmlAttributeLabel attributeEntry="${prePaidConfAttributes.disbVchrConferenceTotalAmt}"/></strong></div></td>
                <td class="infoline" nowrap="nowrap" valign="top"><div align="center"><strong>$<kul:htmlControlAttribute attributeEntry="${prePaidConfAttributes.disbVchrConferenceTotalAmt}" property="document.dvPreConferenceDetail.disbVchrConferenceTotalAmt" readOnly="true"/></strong></div></td>
            	<c:if test="${fullEntryMode||travelEntryMode}">
               		<td class="infoline">&nbsp;</td>
              	</c:if>
            </tr>
          </table>
          
        
        </div>
</kul:tab>
