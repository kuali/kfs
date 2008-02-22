
<%--
 Copyright 2005-2007 The Kuali Foundation.
 
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

<%@ include file="/kr/WEB-INF/jsp/tldHeader.jsp"%>

<c:set var="documentAttributes"	value="${DataDictionary.EffortCertificationDocument.attributes}" />

<table cellpadding="0" cellspacing="0" class="datatable" summary="Effort Detail Importing">

	<tr>
		<th width="35%" class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel 
					attributeEntry="${DataDictionary.UniversalUser.attributes.personPayrollIdentifier}"
					forceRequired="true" useShortLabel="false" />
			</div>
		</th>
			
		<td>                  
			<kul:htmlControlAttribute 
				 attributeEntry="${DataDictionary['UniversalUser'].attributes.personPayrollIdentifier}"
				 property="document.emplid"/>
				
			<kul:lookup boClassName="org.kuali.core.bo.user.UniversalUser" 
                 fieldConversions="personPayrollIdentifier:document.emplid"
                 lookupParameters="document.emplid:personPayrollIdentifier"/>
		</td>
	</tr>
	
	<tr>
		<th width="35%" class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${documentAttributes.universityFiscalYear}"
				forceRequired="true" useShortLabel="false" />
			</div>
		</th>
		
		<td class="datacell-nowrap">	
			<kul:htmlControlAttribute
				attributeEntry="${documentAttributes.universityFiscalYear}"
				property="document.universityFiscalYear" />
				 
			<kul:lookup boClassName="org.kuali.kfs.bo.Options"
				fieldConversions="universityFiscalYear:document.universityFiscalYear"
				lookupParameters="document.universityFiscalYear:universityFiscalYear"
				fieldLabel="${documentAttributes.universityFiscalYear.label}" />
		</td>
	</tr>
	
	<tr>
		<th width="35%" class="bord-l-b">
			<div align="right">
				<kul:htmlAttributeLabel attributeEntry="${documentAttributes.effortCertificationReportNumber}"
				forceRequired="true" useShortLabel="false" />
			</div>
		</th>
				
		<td class="datacell-nowrap">
			<kul:htmlControlAttribute
				attributeEntry="${documentAttributes.effortCertificationReportNumber}"
				property="document.effortCertificationReportNumber" />
			 
			<kul:lookup
				boClassName="org.kuali.module.effort.bo.EffortCertificationReportDefinition"
				fieldConversions="universityFiscalYear:document.universityFiscalYear,effortCertificationReportNumber:document.effortCertificationReportNumber"
				lookupParameters="document.universityFiscalYear:universityFiscalYear,document.effortCertificationReportNumber:effortCertificationReportNumber"
				fieldLabel="${documentAttributes.effortCertificationReportNumber.label}" />
		</td>
	</tr>
	
	<tr>
		<td height="30" class="infoline">&nbsp;</td>
        <td height="30" class="infoline">
			<input type="image" tabindex="${tabindex}" name="methodToCall.loadDetailLine"
			   src="${ConfigProperties.externalizable.images.url}tinybutton-load.gif" 
			   alt="Import Detail Lines" title="Import Detail Lines" border="0" class="tinybutton" valign="middle"/>
        </td>
	</tr>	
</table>