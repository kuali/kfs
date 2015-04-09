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

<%@ tag description="render the error certification tab"%>

<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
    	
<kul:tab tabTitle="Error Certification" defaultOpen="false" tabErrorKey="document.errorCertification">
     <div class="tab-container" align="center">
        <c:set var="attributes" value="${DataDictionary.ErrorCertification.attributes}" />	
     		<h3>Error Certification</h3>
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Error Certification Section">
            <tr>
                <th align="right" valign="middle" class="bord-l-b" width="50%">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${attributes.expenditureDescription}" /></div>
                </th>
                <td align="left" valign="middle" class="datacell" width="50%">
                     <kul:htmlControlAttribute 
                    	attributeEntry="${attributes.expenditureDescription}" property="document.errorCertification.expenditureDescription" 
                    	readOnly="${not fullEntryMode}" />
                </td>
            </tr>
            <tr>            
                <th align="right" valign="middle" class="bord-l-b" width="50%">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${attributes.expenditureProjectBenefit}" /></div>
                </th>
                <td align="left" valign="middle" class="datacell" width="50%">
                     <kul:htmlControlAttribute 
                    	attributeEntry="${attributes.expenditureProjectBenefit}" property="document.errorCertification.expenditureProjectBenefit" 
                    	readOnly="${not fullEntryMode}" />
                </td>
            </tr>
                <th align="right" valign="middle" class="bord-l-b" width="50%">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${attributes.errorDescription}" /></div>
                </th>
                <td align="left" valign="middle" class="datacell" width="50%">
                     <kul:htmlControlAttribute 
                    	attributeEntry="${attributes.errorDescription}" property="document.errorCertification.errorDescription" 
                    	readOnly="${not fullEntryMode}" />
                </td>
            </tr>
            <tr>
                <th align="right" valign="middle" class="bord-l-b" width="50%">
                   <div align="right"><kul:htmlAttributeLabel attributeEntry="${attributes.errorCorrectionReason}" /></div>
                </th>
                <td align="left" valign="middle" class="datacell" width="50%">
                     <kul:htmlControlAttribute 
                    	attributeEntry="${attributes.errorCorrectionReason}" property="document.errorCertification.errorCorrectionReason" 
                    	readOnly="${not fullEntryMode}" />
                </td>
            </tr>     	
		</table>    	
	 </div>	
</kul:tab>
