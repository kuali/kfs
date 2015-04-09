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

