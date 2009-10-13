
<%--
 Copyright 2005-2009 The Kuali Foundation
 
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

<%@ attribute name="attributes" required="true" type="java.util.Map"
			  description="The DataDictionary entry containing attributes for the line fields."%>
<%@ attribute name="readOnly" required="false"
			  description="determine whether the widgets in the tag are read-only or not"%> 			  	
<%@ attribute name="image" required ="false" 
			  description="name of the image file" %>

<c:set var="imageName" value="${empty image ? 'tinybutton-load.gif' : image}"/>

<table cellpadding="0" cellspacing="0" class="datatable" summary="Effort Detail Importing">
	
	<tr>
		<td colspan="2"><kul:errors keyMatch="${EffortConstants.DOCUMENT_PREFIX}*" errorTitle="Errors found in Importing Criteria:" /></td>
	</tr>

	<tr>
		<th width="35%" class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel 
					attributeEntry="${DataDictionary.PersonImpl.attributes.employeeId}"
					labelFor="emplid" forceRequired="true" useShortLabel="false" />
			</div>
		</th>
			
		<td class="datacell-nowrap">
			<sys:employee userIdFieldName="emplid" 
                forceRequired="true" userNameFieldName="name" 
                fieldConversions="employeeId:emplid"
                lookupParameters="emplid:employeeId"
                readOnly="${readOnly}" />
		</td>
	</tr>
	
	<tr>
		<th width="35%" class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${attributes.universityFiscalYear}"
				labelFor="universityFiscalYear" forceRequired="true" useShortLabel="false" />
			</div>
		</th>
		
		<td class="datacell-nowrap">	
			<kul:htmlControlAttribute
				attributeEntry="${attributes.universityFiscalYear}"
				property="universityFiscalYear" readOnly="${readOnly}" forceRequired="true" />
			
			<c:if test="${!readOnly}" >	 
			<kul:lookup boClassName="org.kuali.kfs.sys.businessobject.SystemOptions"
				fieldConversions="universityFiscalYear:universityFiscalYear"
				lookupParameters="universityFiscalYear:universityFiscalYear"
				fieldLabel="${attributes.universityFiscalYear.label}" />
			</c:if>
		</td>
	</tr>
	
	<tr>
		<th width="35%" class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${attributes.effortCertificationReportNumber}"
				forceRequired="true" useShortLabel="false" />
			</div>
		</th>
				
		<td class="datacell-nowrap">
			<kul:htmlControlAttribute
				attributeEntry="${attributes.effortCertificationReportNumber}"
				property="effortCertificationReportNumber" readOnly="${readOnly}" forceRequired="true" />
			
			<c:if test="${!readOnly}" > 
			<kul:lookup
				boClassName="org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition"
				fieldConversions="universityFiscalYear:universityFiscalYear,effortCertificationReportNumber:effortCertificationReportNumber"
				lookupParameters="universityFiscalYear:universityFiscalYear,effortCertificationReportNumber:effortCertificationReportNumber"
				fieldLabel="${attributes.effortCertificationReportNumber.label}" />
			</c:if>
		</td>
	</tr>
	
	<c:if test="${!readOnly}" >
	<tr>
		<td height="30" class="infoline">&nbsp;</td>
        <td height="30" class="infoline">
		<c:set var="loadDetailLineButtonName" value="methodToCall.loadDetailLine" />
		  	   ${kfunc:registerEditableProperty(KualiForm, loadDetailLineButtonName)}
			<input type="image" tabindex="${tabindex}" name="${loadDetailLineButtonName}"
   				   src="${ConfigProperties.externalizable.images.url}${imageName}" 
			   	   alt="Import Detail Lines" title="Import Detail Lines" border="0" class="tinybutton" valign="middle"/>
        </td>
	</tr>
	</c:if>	
</table>

