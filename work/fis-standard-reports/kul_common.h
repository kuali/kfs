!
! Copyright 2005-2006 The Kuali Foundation.
! 
! $Source: /opt/cvs/kfs/work/fis-standard-reports/kul_common.h,v $
! 
! Licensed under the Educational Community License, Version 1.0 (the "License");
! you may not use this file except in compliance with the License.
! You may obtain a copy of the License at
! 
! http://www.opensource.org/licenses/ecl1.php
! 
! Unless required by applicable law or agreed to in writing, software
! distributed under the License is distributed on an "AS IS" BASIS,
! WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
! See the License for the specific language governing permissions and
! limitations under the License.
! DO NOT add comments before the blank line below, or they will disappear.

!
! Financial Management Support
! Indiana University
! Bloomington, IN 47405
!
! Copyright    (C)    1999, Board of Trustees of Indiana University
!
!        common.h
!
!            Author:    Bill Hamlin
!                Date:    January 10, 1995
!            Purpose:    file of common procedures for reports
!
! Modified by:    Chris Shelton
!                Date:    August 14, 1996
!            Purpose:    add Print_Orgs
!
! Modified by:    Harry Walden Banek
!                Date:    February 28, 1998
!            Purpose:    add delimited report for spreadsheet applications
!                                add check for valid date
!                                add check for valid hyphenated entry
!                                add clean for single character answer
!                                add clean for multi-character response
!                                moved into check.h
!
! Modified by:    Harry Walden Banek
!                Date:    April 1998
!            Purpose:    add error messages for invalid data entry
!                                moved to error.h
!
! Modified by:    Harry Walden Banek
!                Date:    October 1998
!            Purpose:    consolidation of all common.h files
!                                check for usage by all SQR programs
!
! Modified by:    Harry Walden Banek
!                Date:    March 1999
!            Purpose:    add constant variables for all SQR programs
!
! Modified by:  Chris Shelton
! Date: November 2002
! Purpose: For use by monthly standard labor reports.
!
! *******************************************************************************************************
!    quick reference for procedures
!
!    Procedure name                Accepts            Passes
!    --------------                -------            ------
!    CleanAnswer                   $string            $string
!    CleanResponse                 $string            $string
!    CleanSpace                    $string            $string
!    CleanTab                      $string            $string
!    ConvertDateCompare            $date              $date_compare
!    ConvertInteger                $integer           $string
!    DelimitedBlank                #output_number
!    DelimitedClose                #output_number
!    DelimitedHierarchy
!    DelimitedHierarchyHeader
!    DelimitedOpen                 $output_name
!                                  #output_number
!    DelimitedWrite                #file_number
!                                  $string
!    EnterMimeType
!    PresentHierarchy
!    PrettyDate                                       $date
!    SetConstantGlobal
!    SetDateReport                 #long_date
!    SetEndFebruary
!    SetFebruaryEnd                #year             $day_last
!    SetMonth
!    SetPeriod                                       $fdoc_post_prd_cd
!    SetPeriodDate                 #fiscal_period    
!                                  #fiscal_year
!                                                    $period_day_first
!                                                    $period_day_last
!    SetReportDate
!    SetYear
!    StandardHierarchy
!    StandardHierarchyHeader
! **********************************************************************************************************
 
begin-procedure CleanAnswer( :$string )
! remove leading and following spaces
! reduce string to single character
! convert string to uppercase 
! use only for single entry, single character input
! error occurs with multiple entry when 'EOF' is truncated
     let $string    = ltrim( $string, $SPACE )
     let $string    = rtrim( $string, $SPACE )
    let $string    = substr( $string, 1, 1 )
    let $string    = upper( $string )
end-procedure

begin-procedure CleanResponse( :$string )
! remove leading and following spaces
! convert string to uppercase 
! use for single or multiple entry, multiple character input
    do CleanSpace( $string )
    let $string    = upper( $string )
end-procedure

begin-procedure CleanSpace( :$string )
! remove leading and following spaces
! use for single or multiple entry, multiple character input
    let $string    = ltrim( $string, $SPACE )
    let $string    = rtrim( $string, $SPACE )
end-procedure

begin-procedure CleanTab( :$string )
! remove tab from field data
! convert string to uppercase
    let $string    = translate( $string, $TAB, $SPACE )
    let $string    = ltrim( $string, $SPACE )
    let $string    = rtrim( $string, $SPACE )
    let $string    = upper( $string )
end-procedure

begin-procedure ConvertInteger( $integer, :$string )
! convert an integer to a string
    let $string    = edit( $integer, '999' )
end-procedure

begin-procedure DelimitedBlank( #output_number )
! write blank line to delimited file
    do DelimitedWrite( #output_number, $_blank )
end-procedure

begin-procedure DelimitedClose( #output_number )
! close file used in writing delimited report
    close #output_number
end-procedure

begin-procedure DelimitedOpen( $output_name, #output_number )
    new-report '/dev/null'
    open $output_name AS #output_number FOR-WRITING RECORD=8192
end-procedure


begin-procedure DelimitedWrite( #file_number, $string )
! write line to delimited file
    write #file_number FROM $string
end-procedure

begin-procedure EnterMimeType
! enter mime type for report
! determines output type
    input $mime_type 'Mime Type'
    do CleanResponse( $mime_type )
    if ( $mime_type = $text_delimited )
        let $output_type    = $DELIMITED
    else
        let $output_type    = $STANDARD
    end-if
end-procedure


begin-procedure SetEndFebruary
    let #divisible_by_4        = cond( mod( #year_calendar_cur, 4 ), 0, 1 )
    let #divisible_by_100    = cond( mod( #year_calendar_cur, 100 ), 0, 1 )
    let #divisible_by_400    = cond( mod( #year_calendar_cur, 400 ), 0, 1 )
    if ( #divisible_by_400 )
        let $day_last_cur    = '29'
    else
        if ( #divisible_by_100 )
            let $day_last_cur    = '28'
        else
            if ( #divisible_by_4 )
                let $day_last_cur    = '29'
            else
                let $day_last_cur    = '28'
            end-if
        end-if
    end-if
    let #divisible_by_4        = cond( mod( #year_calendar_cmp, 4 ), 0, 1 )
    let #divisible_by_100    = cond( mod( #year_calendar_cmp, 100 ), 0, 1 )
    let #divisible_by_400    = cond( mod( #year_calendar_cmp, 400 ), 0, 1 )
    if ( #divisible_by_400 )
        let $day_last_cmp    = '29'
    else
        if ( #divisible_by_100 )
            let $day_last_cmp    = '28'
        else
            if ( #divisible_by_4 )
                let $day_last_cmp    = '29'
            else
                let $day_last_cmp    = '28'
            end-if
        end-if
    end-if
end-procedure

begin-procedure SetFebruaryEnd( #year, :$day_last)
    let #divisible_by_4        = cond( mod( #year, 4 ), 0, 1 )
    let #divisible_by_100    = cond( mod( #year, 100 ), 0, 1 )
    let #divisible_by_400    = cond( mod( #year, 400 ), 0, 1 )
    if ( #divisible_by_400 )
        let $day_last    = '29'
    else
        if ( #divisible_by_100 )
            let $day_last    = '28'
        else
            if ( #divisible_by_4 )
                let $day_last    = '29'
            else
                let $day_last    = '28'
            end-if
        end-if
    end-if
end-procedure

begin-procedure SetMonth
    let $date    = datenow( )
    let $month    = edit( $date, 'MM' )
    let #month_current        = to_number( $month )
    let #month_default        = #month_current - 1
    if ( #month_default > 6 )
        let #period_default = #month_default - 6
    else
        let #period_default = #month_default + 6
    end-if
    let $period_default        = edit( #period_default, '88' )
end-procedure

begin-procedure SetQuarter
!Set default fiscal quarter for current quarter
        let $date        = datenow()
        let $month       = edit( $date, 'MM')
        let #month_current         =  to_number($month)
        if (#month_current > 6)
            let #fiscal_month = #month_current - 6
        else
            let #fiscal_month = #month_current + 6
        end-if
        evaluate #fiscal_month
          when > 9
             let #quarter_default = 4
          when > 6
             let #quarter_default = 3
          when > 3
             let #quarter_default = 2
          when-other
             let #quarter_default = 1 
        end-evaluate
        let $quarter_default = to_char(#quarter_default)
end-procedure

begin-procedure SetYear
    let $date    = datenow( )
    let $year    = edit( $date, 'YYYY' )
    let #year_current    = to_number( $year )
    if ( #month_default > 6 )
        let #year_default = #year_current + 1
    else
        let #year_default = #year_current
    end-if
    let $year_default    = edit( #year_default, '8888' )
end-procedure

begin-procedure SetPeriod( :$fdoc_post_prd_cd )
    evaluate $fdoc_post_prd_cd
        when    = '01'
            let $fdoc_post_prd_cd    = 'July'
        when    = '02'
            let $fdoc_post_prd_cd    = 'August'
        when    = '03'
            let $fdoc_post_prd_cd    = 'September'
        when    = '04'
            let $fdoc_post_prd_cd    = 'October'
        when    = '05'
            let $fdoc_post_prd_cd    = 'November'
        when    = '06'
            let $fdoc_post_prd_cd    = 'December'
        when    = '07'
            let $fdoc_post_prd_cd    = 'January'
        when    = '08'
            let $fdoc_post_prd_cd    = 'February'
        when    = '09'
            let $fdoc_post_prd_cd    = 'March'
        when    = '10'
            let $fdoc_post_prd_cd    = 'April'
        when    = '11'
            let $fdoc_post_prd_cd    = 'May'
        when    = '12'
            let $fdoc_post_prd_cd    = 'June'
        when    = '13'
            let $fdoc_post_prd_cd    = 'Closing'
        when    = 'ALL'
            let $fdoc_post_prd_cd    = 'All Months'
    end-evaluate
end-procedure

begin-procedure SetPeriodDate( #fiscal_period, #fiscal_year, :$period_day_first, :$period_day_last )
! set the last day of specified fiscal period
! return date string in format MM/DD/YYYY
    evaluate #fiscal_period
        when > 12
            let #month_calendar    = 6
            let #year_calendar    = #fiscal_year
            break
        when > 6
            let #month_calendar    = #fiscal_period - 6
            let #year_calendar    = #fiscal_year
            break
        when-other
            let #month_calendar    = #fiscal_period + 6
            let #year_calendar    = #fiscal_year - 1
    end-evaluate
    evaluate #month_calendar
        when = 1
            let $month_digit    = '01'
            let $day_last    = '31'
            break
        when = 2
            let $month_digit    = '02'
            do SetFebruaryEnd( #year_calendar, $day_last )
            break
        when = 3
            let $month_digit    = '03'
            let $day_last    = '31'
            break
        when = 4
            let $month_digit    = '04'
            let $day_last    = '30'
            break
        when = 5
            let $month_digit    = '05'
            let $day_last    = '31'
            break
        when = 6
            let $month_digit    = '06'
            let $day_last    = '30'
            break
        when = 7
            let $month_digit    = '07'
            let $day_last    = '31'
            break
        when = 8
            let $month_digit    = '08'
            let $day_last    = '31'
            break
        when = 9
            let $month_digit    = '09'
            let $day_last    = '30'
            break
        when = 10
            let $month_digit    = '10'
            let $day_last    = '31'
            break
        when = 11
            let $month_digit    = '11'
            let $day_last    = '30'
            break
        when = 12
            let $month_digit    = '12'
            let $day_last    = '31'
            break
    end-evaluate
    let $year_calendar    = edit( #year_calendar, '8888' )
    let $period_day_first    = $month_digit || '/' || '01' || '/' || $year_calendar
    let $period_day_last    = $month_digit || '/' || $day_last || '/' || $year_calendar
end-procedure

begin-procedure ConvertDateCompare( $date, :$date_compare )
! set today's date for report
    unstring $date by '/' into $month $day $year
    let $date_compare    = $year || $month || $day
end-procedure

begin-procedure SetReportDate
! set today's date for report
    let $today    = datenow()
    let $today    = edit( $today, 'MM/DD/YYYY' )
    let $date_report_line    = 'Report Date: ' || $today 
end-procedure

begin-procedure SetDateReport( #long_date )
    let $_today    = datenow( )
    if ( #long_date )
        let $_today    = edit( $_today, 'MM/DD/YYYY' )
        let $_date_report = $_today
    else
        let $_date_report = edit( $_today, 'MM/DD/YY' )
        let $_date_report = $_today
    end-if
    let $_date_report_line    = 'Report Date: ' || $_today 
end-procedure

begin-procedure SetFiscalDate($calendar_date, :#fiscal_year, :#fiscal_period)
! takes a calendar date in format 'MM/DD/YYYY' or 'MM/DD/YY' and returns fiscal year and fiscal period  
        let $calendar_month = substr($calendar_date,1,2)
        if (length($calendar_date) = 10)
          let $calendar_year  = substr($calendar_date,7,4)
        else
          let $calendar_year  = substr($calendar_date,7,2)
        end-if
        let #calendar_month = to_number($calendar_month)
        let #calendar_year  = to_number($calendar_year) 
        if (length($calendar_date) = 8)
          let #calendar_year  = #calendar_year + 1900 
        end-if
        if (#calendar_month > 6 )
           let #fiscal_period = #calendar_month - 6
           let #fiscal_year   = #calendar_year + 1
        else
           let #fiscal_period = #calendar_month + 6 
           let #fiscal_year   = #calendar_year
        end-if
end-procedure


begin-procedure PrettyDate ( :$date )
    if ( length( $date ) != 8 )
        show BEEP BEEP BEEP 
        display 'Date Format must be YYYYMMDD'
        stop
    end-if
    let $Year    = substr( $date, 1, 4 )
    let $Month    = substr( $date, 5, 2 )
    let $Day    = substr( $date, 7, 2 )
    evaluate $Month
        when    = '01'
            let $Month    = 'January'
        when    = '02'
            let $Month    = 'February'
        when    = '03'
            let $Month    = 'March'
        when    = '04'
            let $Month    = 'April'
        when    = '05'
            let $Month    = 'May'
        when    = '06'
            let $Month    = 'June'
        when    = '07'
            let $Month    = 'July'
        when    = '08'
            let $Month    = 'August'
        when    = '09'
            let $Month    = 'September'
        when    = '10'
            let $Month    = 'October'
        when    = '11'
            let $Month    = 'November'
        when    = '12'
            let $Month    = 'December'
    end-evaluate
    if substr( $Day, 1, 1 )    = '0'
        let $Day    = substr( $Day, 2, 1 )
    end-if
    let $date    = $Month || $SPACE || $Day || ', ' || $Year
end-procedure

begin-procedure SetConstantGlobal
! set constants for all reports
    let $DELIMITED            = 'DELIMITED'
    let $STANDARD            = 'STANDARD'
    let $TEXT_DELIMITED        = 'TEXT/DELIMITED'
    let #NO_INPUT_ERROR        = 1
    let #HAS_PRINTED_ERROR        = 0
    let $BLANK            = '' 
    let $COLON_SPACE        = ': ' 
    let $DASH            = '-'
    let $EOL            = 'EOL'
    let $NONE            = 'None entered'
    let $PERIOD            = '.'
    let $SPACE            = ' '
    let $CR                = chr( 13 )
    let $STAR            = chr( 42 )
    let $TAB            = chr( 9 )
    let $TAB2            = $TAB || $TAB
    let $TABS            = $TAB2 || $TAB2 || $TAB2
    let $EXPRESSION_FALSE        = '0=1 '
    let $EXPRESSION_TRUE        = '1=1 '
    let $CLAUSE_FALSE        = '( ' || $EXPRESSION_FALSE || ') '
    let $CLAUSE_TRUE        = '( ' || $EXPRESSION_TRUE || ') '
    let #TRUE            = 1
    let #FALSE            = 0
    let #WILDCARD            = 1
    let #NO_WILDCARD        = 0
    let #ZERO            = 1
    let #NO_ZERO            = 0
    let #LONG_YEAR            = 1
    let #NO_LONG_YEAR        = 0
    let #SHORT_YEAR            = 1
    let #NO_SHORT_YEAR        = 0
    let #END_REPORT            = 0
    let #MID_REPORT            = 1
end-procedure


