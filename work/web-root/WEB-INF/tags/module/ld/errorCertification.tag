<%--
 Copyright 2005-2014 The Kuali Foundation
 
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