<?xml version="1.0"?>
<!--
 Copyright 2006 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->
<xsl:stylesheet exclude-result-prefixes="xalan" version="1.0"
    xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xalan="http://xml.apache.org/xalan" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <!--                           VERSION HISTORY
      - 12/2004: jmin@indiana.edu, Initial Release
                 comment: if the value needs to be shown as 0, just
                          delete the if test statement where the value
                          needs to be shown.
      - 01/2005: pcberg@indiana.edu, revisions per functional requests
                 prior to initial release:
                 - Amounts without values should display as 0.  This
                   goes for all three amount types (and
                   totals). Technically this was accomplished with the
                   comment above by Julia.
                 - There is an empty line between Variable Adjustment
                   and the first line of the printed text.  That
                   should be removed.
                 - There should be a space between the words "Variable
                   Adjustment" and the italicized text in parens next
                   to it.
                 - The user entered text should be a smaller font -
                   should be smaller than the labels
                 - The Variable Adjustment label should not display at
                   all if there is no variable adjust ment data in the
                   XML
                 - The overflow pages should indicate that they are
                   continuations in the footer. Technically this was
                   accomplished with the help of those links:
                   http://www.xmlpdf.com/ibex-examples.html
                   http://www.dpawson.co.uk/xsl/sect3/headers.html#d11137e73
                   http://marc.theaimsgroup.com/?l=fop-user
                 - Also: Eliminated a few warnings, minor style
                   cleanup, ensured metric system is used consistently
                   (it was a mix of metric / industrial), and gave
                   Consortium F&A / Total Direct Costs a tiny
                   indentation to match DC less ... .
      -05/2005: dterret@indiana.edu, revision per functional request
                                     prior to a release:
                 - The order of printing of the Variable Adjustment section
                 and the Consortium section was switched.
      - 05/2006: pcberg@indiana.edu, refactoring for KRA.
               - @PERIOD = @PERIOD_NUMBER
    -->

    <!-- ******************* **************************** ******************* -->
    <!-- *******************         Variables            ******************* -->
    <!-- ******************* **************************** ******************* -->
    <xsl:variable name="DCless1">
        <xsl:choose>
            <xsl:when test="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER='1']/ADJUSTED_MODULAR_DIRECT_COST &gt; 0">
                <xsl:value-of select="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER='1']/ADJUSTED_MODULAR_DIRECT_COST"/>
            </xsl:when>
            <xsl:otherwise>0</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="DCless2">
        <xsl:choose>
            <xsl:when test="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER='2']/ADJUSTED_MODULAR_DIRECT_COST &gt; 0">
                <xsl:value-of select="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER='2']/ADJUSTED_MODULAR_DIRECT_COST"/>
            </xsl:when>
            <xsl:otherwise>0</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="DCless3">
        <xsl:choose>
            <xsl:when test="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER='3']/ADJUSTED_MODULAR_DIRECT_COST &gt; 0">
                <xsl:value-of select="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER='3']/ADJUSTED_MODULAR_DIRECT_COST"/>
            </xsl:when>
            <xsl:otherwise>0</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="DCless4">
        <xsl:choose>
            <xsl:when test="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER='4']/ADJUSTED_MODULAR_DIRECT_COST &gt; 0">
                <xsl:value-of select="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER='4']/ADJUSTED_MODULAR_DIRECT_COST"/>
            </xsl:when>
            <xsl:otherwise>0</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="DCless5">
        <xsl:choose>
            <xsl:when test="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER='5']/ADJUSTED_MODULAR_DIRECT_COST &gt; 0">
                <xsl:value-of select="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_PERIODS/MODULAR_BUDGET_PERIOD[@PERIOD_NUMBER='5']/ADJUSTED_MODULAR_DIRECT_COST"/>
            </xsl:when>
            <xsl:otherwise>0</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="DClessSum">
        <xsl:choose>
            <xsl:when test="($DCless1 + $DCless2 + $DCless3 + $DCless4 + $DCless5) &gt; 0">
                <xsl:value-of select="$DCless1 + $DCless2 + $DCless3 + $DCless4 + $DCless5"/>
            </xsl:when>
            <xsl:otherwise>0</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="Con1">
        <xsl:value-of select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD [@PERIOD_NUMBER='1']/NON_PERSONNEL /NON_PERSONNEL_ITEM [CATEGORY='Subcontractors' and (SUB_CATEGORY = 'Subcontractor IDC - first $25K'  or SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT)"/>
    </xsl:variable>
    <xsl:variable name="Con2">
        <xsl:value-of select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD [@PERIOD_NUMBER='2']/NON_PERSONNEL /NON_PERSONNEL_ITEM [CATEGORY='Subcontractors' and (SUB_CATEGORY = 'Subcontractor IDC - first $25K'  or SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT)"/>
    </xsl:variable>
    <xsl:variable name="Con3">
        <xsl:value-of select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD [@PERIOD_NUMBER='3']/NON_PERSONNEL /NON_PERSONNEL_ITEM [CATEGORY='Subcontractors' and (SUB_CATEGORY = 'Subcontractor IDC - first $25K'  or SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT)"/>
    </xsl:variable>
    <xsl:variable name="Con4">
        <xsl:value-of select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD [@PERIOD_NUMBER='4']/NON_PERSONNEL /NON_PERSONNEL_ITEM [CATEGORY='Subcontractors' and (SUB_CATEGORY = 'Subcontractor IDC - first $25K'  or SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT)"/>
    </xsl:variable>
    <xsl:variable name="Con5">
        <xsl:value-of select="sum(/PROPOSAL/BUDGET/TASK_PERIODS/TASK_PERIOD [@PERIOD_NUMBER='5']/NON_PERSONNEL /NON_PERSONNEL_ITEM [CATEGORY='Subcontractors' and (SUB_CATEGORY = 'Subcontractor IDC - first $25K'  or SUB_CATEGORY = 'Subcontractor IDC - over $25K')]/AGENCY_REQUEST_AMOUNT)"/>
    </xsl:variable>
    <xsl:variable name="ConSum">
        <xsl:choose>
            <xsl:when test="($Con1+ $Con2 + $Con3 + $Con4 + $Con5) &gt; 0">
                <xsl:value-of select="$Con1 + $Con2 + $Con3 + $Con4 + $Con5"/>
            </xsl:when>
            <xsl:otherwise>0</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <!-- Following are the attribute sets. -->
    <xsl:attribute-set name="tableCell">
        <xsl:attribute name="border-style">solid</xsl:attribute>
        <xsl:attribute name="margin-right">1mm</xsl:attribute>
        <xsl:attribute name="font-size">12pt</xsl:attribute>
        <xsl:attribute name="text-align">right</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="tableHeadColumn">
        <xsl:attribute name="border-style">solid</xsl:attribute>
        <xsl:attribute name="font-size">10pt</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="text-align">left</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="header">
        <xsl:attribute name="font-size">11pt</xsl:attribute>
        <xsl:attribute name="font-weight">bold</xsl:attribute>
        <xsl:attribute name="space-before">12pt</xsl:attribute>
    </xsl:attribute-set>
    <xsl:attribute-set name="text">
        <xsl:attribute name="font-size">11pt</xsl:attribute>
        <xsl:attribute name="font-family">Arial, sans-serif</xsl:attribute>
        <xsl:attribute name="white-space-collapse">false</xsl:attribute>
    </xsl:attribute-set>

    <!-- ******************* **************************** ******************* -->
    <!-- *******************         Root Template        ******************* -->
    <!-- ******************* **************************** ******************* -->
    <!-- The root template sets the table layout (size, page numbers, etc.)
         and then calls the main template. Notice that this
         layout-master-set is responsible for ensuring the footer
         is different on the first page. -->
    <xsl:template match="/">
        <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="firstpage"
                      page-width="216mm" page-height="279mm"
                      margin-top="12mm"  margin-bottom="12mm"
                      margin-left="12mm" margin-right="12mm">
                    <fo:region-body margin-top="8mm" margin-bottom="8mm"/>
                    <fo:region-before extent="12mm"/>
                    <fo:region-after region-name="footer-first" extent="8mm"/>
                </fo:simple-page-master>
                <fo:simple-page-master master-name="otherpages"
                      page-width="216mm" page-height="279mm"
                      margin-top="12mm"  margin-bottom="12mm"
                      margin-left="12mm" margin-right="12mm">
                    <fo:region-body margin-top="8mm" margin-bottom="8mm"/>
                    <fo:region-before extent="12mm"/>
                    <fo:region-after region-name="footer-rest" extent="8mm"/>
                </fo:simple-page-master>
                <fo:page-sequence-master master-name="all">
                    <fo:repeatable-page-master-alternatives>
                        <fo:conditional-page-master-reference master-reference="firstpage" page-position="first"/>
                        <fo:conditional-page-master-reference master-reference="otherpages" page-position="any"/>
                    </fo:repeatable-page-master-alternatives>
                </fo:page-sequence-master>
            </fo:layout-master-set>
            <fo:page-sequence initial-page-number="1" master-reference="all">
                <fo:static-content flow-name="footer-first">
                    <xsl:call-template name="footer-first"/>
                </fo:static-content>

                <fo:static-content flow-name="footer-rest">
                    <xsl:call-template name="footer-rest"/>
                </fo:static-content>

                <fo:static-content flow-name="xsl-region-before">
                    <xsl:call-template name="header"/>
                </fo:static-content>

                <fo:flow flow-name="xsl-region-body">
                    <xsl:call-template name="main"/>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>

    <!-- ******************* **************************** ******************* -->
    <!-- *******************      Header Template         ******************* -->
    <!-- ******************* **************************** ******************* -->
    <xsl:template name="header">
        <fo:table table-layout="fixed">
            <fo:table-column column-width="95mm"/>
            <fo:table-column column-width="95mm"/>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell>
                        <fo:block font-size="8pt" margin-left="2cm" space-after="3.2mm"
                            space-before="2mm" start-indent="4mm" text-align="left"> Principal
                            Investigator/Program Director (Last, First, Middle): </fo:block>
                    </fo:table-cell>
                    <fo:table-cell display-align="center" padding-right="2mm">
                        <fo:block>
                            <xsl:if test="not(//PROJECT_DIRECTOR/@FIRST_NAME = 'To Be Named') ">
                                <xsl:value-of select="substring(concat(//PROJECT_DIRECTOR/@LAST_NAME, ', ', //PROJECT_DIRECTOR/@FIRST_NAME ), 1, 28)"/>
                            </xsl:if>
                            <xsl:if test="//PROJECT_DIRECTOR/@FIRST_NAME = 'To Be Named' "> To Be
                                Named </xsl:if>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
    </xsl:template>

    <!-- ******************* **************************** ******************* -->
    <!-- ******************* Footer First Page Template   ******************* -->
    <!-- ******************* **************************** ******************* -->
    <xsl:template name="footer-first">
        <fo:table space-before="1.5mm" table-layout="fixed">
            <fo:table-column column-width="2in"/>
            <fo:table-column column-width="3.5in"/>
            <fo:table-column column-width="2in"/>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell border-after-width="0mm" border-left-width="0mm"
                        border-right-width="0mm" border-style="solid"
                        font-family="Arial, sans-serif" font-size="8pt">
                        <fo:block space-before="2mm">PHS 398 (Rev. 04/06)</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-after-width="0mm" border-left-width="0mm"
                        border-right-width="0mm" border-style="solid"
                        font-family="Arial, sans-serif" font-size="8pt">
                        <fo:block space-before="2mm" text-align="center" text-indent="2mm">Page __</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-after-width="0mm" border-left-width="0mm"
                        border-right-width="0mm" border-style="solid"
                        font-family="Arial, sans-serif" font-size="8pt" text-align="right">
                        <fo:block space-before="2mm">
                            <fo:inline font-weight="bold">Modular Budget Format Page</fo:inline>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
    </xsl:template>

    <!-- ******************* **************************** ******************* -->
    <!-- *******************    Footer "Rest" Template    ******************* -->
    <!-- ******************* **************************** ******************* -->
    <xsl:template name="footer-rest">
        <fo:table space-before="1.5mm" table-layout="fixed">
            <fo:table-column column-width="2in"/>
            <fo:table-column column-width="3.5in"/>
            <fo:table-column column-width="2in"/>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell border-after-width="0mm" border-left-width="0mm"
                        border-right-width="0mm" border-style="solid"
                        font-family="Arial, sans-serif" font-size="8pt">
                        <fo:block space-before="2mm">PHS 398 (Rev. 04/06)</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-after-width="0mm" border-left-width="0mm"
                        border-right-width="0mm" border-style="solid"
                        font-family="Arial, sans-serif" font-size="8pt">
                        <fo:block space-before="2mm" text-align="center" text-indent="2mm">Page __</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-after-width="0mm" border-left-width="0mm"
                        border-right-width="0mm" border-style="solid"
                        font-family="Arial, sans-serif" font-size="8pt" text-align="right">
                        <fo:block space-before="2mm">
                            <fo:inline font-weight="bold">Continuation Format Page</fo:inline>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
    </xsl:template>

    <!-- ******************* **************************** ******************* -->
    <!-- *******************       Main Template          ******************* -->
    <!-- ******************* **************************** ******************* -->
	
		<!-- 5/17/2005 dterret: The order of execution of the Variable Adjustment section 
				and the Consortium section were switched in this template. -->
    <xsl:template name="main">
        <xsl:apply-templates select="/PROPOSAL/BUDGET"/>
        <xsl:apply-templates select="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_DESCRIPTIONS/MODULAR_BUDGET_PERSONNEL_DESCRIPTION"/>
        <xsl:apply-templates select="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_DESCRIPTIONS/MODULAR_BUDGET_CONSORTIUM_DESCRIPTION"/>
        <xsl:apply-templates select="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_DESCRIPTIONS/MODULAR_BUDGET_VARIABLE_ADJUSTMENT_DESCRIPTION"/>
    </xsl:template>

    <!-- ******************* **************************** ******************* -->
    <!-- ******************* Modular Budget Table Template******************* -->
    <!-- ******************* **************************** ******************* -->
    <xsl:template match="/PROPOSAL/BUDGET">
        <fo:table table-layout="fixed">
            <fo:table-column column-width="45mm"/>
            <fo:table-column column-width="30mm"/>
            <fo:table-column column-width="22mm"/>
            <fo:table-column column-width="21mm"/>
            <fo:table-column column-width="21mm"/>
            <fo:table-column column-width="21mm"/>
            <fo:table-column column-width="32mm"/>
            <fo:table-body>
                <fo:table-row height="14mm">
                    <fo:table-cell border-style="solid" display-align="center"
                        font-family="Arial, sans-serif" font-size="11pt" number-columns-spanned="7" text-align="center">
                        <fo:block font-weight="bold">BUDGET JUSTIFICATION PAGE <fo:block/> MODULAR
                            RESEARCH GRANT APPLICATION </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row display-align="center" font-size="9pt" font-weight="bold"
                    height="12mm" text-align="center">
                    <fo:table-cell border-style="solid">
                        <fo:block/>
                    </fo:table-cell>
                    <fo:table-cell border-style="solid">
                        <fo:block>Initial Period</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-style="solid">
                        <fo:block>
                            <fo:inline>2<fo:inline font-size="5pt" vertical-align="super">nd</fo:inline>
                            </fo:inline>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-style="solid">
                        <fo:block>
                            <fo:inline>3<fo:inline font-size="5pt" vertical-align="super">rd</fo:inline>
                            </fo:inline>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-style="solid">
                        <fo:block>
                            <fo:inline>4<fo:inline font-size="5pt" vertical-align="super">th</fo:inline>
                            </fo:inline>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-style="solid">
                        <fo:block>
                            <fo:inline>5<fo:inline font-size="5pt" vertical-align="super">th</fo:inline>
                            </fo:inline>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-style="solid">
                        <fo:block>Sum Total<fo:block/>(For Entire Project<fo:block/>Period)</fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <!-- 3rd row first half -->
                <fo:table-row display-align="center" height="10mm">
                    <fo:table-cell border-bottom="none" margin-left="0.5mm" xsl:use-attribute-sets="tableHeadColumn">
                        <fo:block>DC less Consortium F&amp;A</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-bottom="none" xsl:use-attribute-sets="tableCell">
                        <fo:block>
                            <xsl:value-of select="format-number($DCless1,'###,###')"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-bottom="none" xsl:use-attribute-sets="tableCell">
                        <fo:block>
                            <xsl:value-of select="format-number($DCless2,'###,###')"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-bottom="none" xsl:use-attribute-sets="tableCell">
                        <fo:block>
                            <xsl:value-of select="format-number($DCless3,'###,###')"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-bottom="none" xsl:use-attribute-sets="tableCell">
                        <fo:block>
                            <xsl:value-of select="format-number($DCless4,'###,###')"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-bottom="none" xsl:use-attribute-sets="tableCell">
                        <fo:block>
                            <xsl:value-of select="format-number($DCless5,'###,###')"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-bottom="none" xsl:use-attribute-sets="tableCell">
                        <fo:block>
                            <xsl:value-of select="format-number($DClessSum,'###,###')"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <!--3rd row second half -->
                <fo:table-row height="4mm">
                    <fo:table-cell border-style="solid" border-top="none">
                        <fo:block/>
                    </fo:table-cell>
                    <fo:table-cell border-style="solid" border-top="none">
                        <fo:block font-size="8pt" font-style="italic" text-align="center">(Item 7a,
                            Face Page)</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-style="solid" border-top="none">
                        <fo:block/>
                    </fo:table-cell>
                    <fo:table-cell border-style="solid" border-top="none">
                        <fo:block/>
                    </fo:table-cell>
                    <fo:table-cell border-style="solid" border-top="none">
                        <fo:block/>
                    </fo:table-cell>
                    <fo:table-cell border-style="solid" border-top="none">
                        <fo:block/>
                    </fo:table-cell>
                    <fo:table-cell border-style="solid" border-top="none">
                        <fo:block font-size="8pt" font-style="italic" text-align="center">(Item 8a,
                            Face Page)</fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row display-align="after" height="8mm">
                    <fo:table-cell margin-left="0.5mm" xsl:use-attribute-sets="tableHeadColumn">
                        <fo:block>Consortium F&amp;A</fo:block>
                    </fo:table-cell>
                    <fo:table-cell xsl:use-attribute-sets="tableCell">
                        <fo:block>
                            <xsl:value-of select="format-number($Con1,'###,###')"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell xsl:use-attribute-sets="tableCell">
                        <fo:block>
                            <xsl:value-of select="format-number($Con2,'###,###')"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell xsl:use-attribute-sets="tableCell">
                        <fo:block>
                            <xsl:value-of select="format-number($Con3,'###,###')"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell xsl:use-attribute-sets="tableCell">
                        <fo:block>
                            <xsl:value-of select="format-number($Con4,'###,###')"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell xsl:use-attribute-sets="tableCell">
                        <fo:block>
                            <xsl:value-of select="format-number($Con5,'###,###')"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell xsl:use-attribute-sets="tableCell">
                        <fo:block>
                            <xsl:value-of select="format-number($ConSum,'###,###')"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
        <fo:table table-layout="fixed">
            <fo:table-column column-width="45mm"/>
            <fo:table-column column-width="30mm"/>
            <fo:table-column column-width="22mm"/>
            <fo:table-column column-width="21mm"/>
            <fo:table-column column-width="21mm"/>
            <fo:table-column column-width="21mm"/>
            <fo:table-column column-width="4mm"/>
            <fo:table-column column-width="28mm"/>
            <fo:table-body>
                <fo:table-row display-align="after" height="8mm">
                    <fo:table-cell margin-left="0.5mm" xsl:use-attribute-sets="tableHeadColumn">
                        <fo:block>Total Direct Costs</fo:block>
                    </fo:table-cell>
                    <fo:table-cell xsl:use-attribute-sets="tableCell">
                        <fo:block>
                            <xsl:value-of select="format-number(($DCless1+$Con1),'###,###')"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell xsl:use-attribute-sets="tableCell">
                        <fo:block>
                            <xsl:value-of select="format-number(($DCless2+$Con2),'###,###')"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell xsl:use-attribute-sets="tableCell">
                        <fo:block>
                            <xsl:value-of select="format-number(($DCless3+$Con3),'###,###')"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell xsl:use-attribute-sets="tableCell">
                        <fo:block>
                            <xsl:value-of select="format-number(($DCless4+$Con4),'###,###')"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell xsl:use-attribute-sets="tableCell">
                        <fo:block>
                            <xsl:value-of select="format-number(($DCless5+$Con5),'###,###')"/>
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-bottom-width="1mm" border-left-width="1mm"
                        border-right="none" border-style="solid" border-top-width="1mm" margin-left="0.1mm">
                        <fo:block text-align="left">$</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-bottom-width="1mm" border-left="none"
                        border-right-width="1mm" border-style="solid" border-top-width="1mm" margin-right="1mm">
                        <fo:block text-align="right">
                            <xsl:value-of select="format-number(($DClessSum+$ConSum),'###,###')"/>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
    </xsl:template>

    <!-- ******************* **************************** ******************* -->
    <!-- *******************       Personnel Template    ******************* -->
    <!-- ******************* **************************** ******************* -->
    <xsl:template match="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_DESCRIPTIONS/MODULAR_BUDGET_PERSONNEL_DESCRIPTION">
        <fo:block xsl:use-attribute-sets="header">Personnel</fo:block>
        <fo:block xsl:use-attribute-sets="text">
            <xsl:value-of select="."/>
        </fo:block>
    </xsl:template>

    <!-- ******************* **************************** ******************* -->
    <!-- *******************      Equipment Template     ******************* -->
    <!-- ******************* **************************** ******************* -->
    <xsl:template
    match="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_DESCRIPTIONS/MODULAR_BUDGET_VARIABLE_ADJUSTMENT_DESCRIPTION">
        <xsl:if test=". != ''">
            <fo:block xsl:use-attribute-sets="header">Variable Adjustment<fo:inline font-style="italic"
                    font-weight="100"> (This additional narrative budget justification is provided
                    because there is a variation in the number of modules requested.)</fo:inline></fo:block>
            <fo:block xsl:use-attribute-sets="text">
                <xsl:value-of select="."/>
            </fo:block>
        </xsl:if>
    </xsl:template>

    <!-- ******************* **************************** ******************* -->
    <!-- *******************  Consortium Template       ******************* -->
    <!-- ******************* **************************** ******************* -->
    <xsl:template match="/PROPOSAL/BUDGET/MODULAR_BUDGET/MODULAR_BUDGET_DESCRIPTIONS/MODULAR_BUDGET_CONSORTIUM_DESCRIPTION">
        <fo:block xsl:use-attribute-sets="header">Consortium</fo:block>
        <fo:block xsl:use-attribute-sets="text">
            <xsl:value-of select="."/>
        </fo:block>
    </xsl:template>

</xsl:stylesheet>
