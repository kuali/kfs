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
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fo="http://www.w3.org/1999/XSL/Format"
  xmlns:xalan="http://xml.apache.org/xalan" 
  exclude-result-prefixes="xalan">

  <!-- ******************* **************************** ******************* -->
  <!-- *******************         Root Template        ******************* -->
  <!-- ******************* **************************** ******************* -->
  <!-- The root template sets the table layout (size, page numbers, etc.). -->
  <xsl:template match="/">
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

      <fo:layout-master-set>
        <fo:simple-page-master master-name="Legal"
           page-width="216mm" page-height="279mm"
           margin-top="6mm"  margin-bottom="6mm"
           margin-left="12mm" margin-right="6mm">
          <fo:region-body margin-top="2mm"
                          margin-bottom="15mm"/>
          <fo:region-after extent="6mm"/>
        </fo:simple-page-master>
      </fo:layout-master-set>

      <fo:page-sequence master-reference="Legal"
        initial-page-number="1" language="en" country="us">

        <fo:flow flow-name="xsl-region-body">
          <fo:block font-family="Times" font-size="6pt" text-align="left">
            <xsl:value-of select="/PROPOSAL/BUDGET/@BUDGET_NUMBER"/> yells: "HELLO WORLD!"
          </fo:block>
        </fo:flow>

      </fo:page-sequence>

    </fo:root>
  </xsl:template>
</xsl:stylesheet>
