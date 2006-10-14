!
! Copyright 2005-2006 The Kuali Foundation.
! 
! $Source: /opt/cvs/kfs/work/fis-standard-reports/kul_error.h,v $
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
! Copyright  (C)  1999, Board of Trustees of Indiana University
!
!    error.h
!
!    Author:    Harry Walden Banek
!    Date:    April 1999
!    Purpose:    common procedures for handling errors
!            procedures to write error messages to monitor or browser
!
!************************************************************************************************* 
!        quick reference for procedures
!
!    Procedure name                Accepts            Passes
!    --------------                -------            ------
!    ErrorAccount                $string
!    ErrorAgingPeriod            $string
!    ErrorAgingPeriodEntry            $string
!    ErrorAlpha-Numeric             $string
!                        #alpha_length
!                        #numeric_length
!    ErrorCampus                $string
!    ErrorCenter                $string
!    ErrorChart                $string
!    ErrorChartAccount            $string
!    ErrorChartMissing            $string
!    ErrorChartOrganization            $string
!    ErrorCity                $string
!    ErrorClass                $string
!    ErrorComment                $string
!                        #maximum
!    ErrorCreditCard                $string
!    ErrorDate                $string
!                        #short_date
!                        #long_date
!    ErrorDateBeginMissing            #short_date
!                        #long_date
!    ErrorDateType                $string
!    ErrorDayBegin                #value
!    ErrorDayEnd                #value
!    ErrorDetailLevel            $string
!    ErrorEventCode                $string
!    ErrorEventDate                $string
!    ErrorEventLocation            $string
!    ErrorEventName                $string
!    ErrorFirstMiddle            $string
!    ErrorID                    $string
!    ErrorInterrupted
!    ErrorLast                $string
!    ErrorMajor                $string
!    ErrorMonthBegin                #value
!    ErrorMonthEnd                #value
!    ErrorOffice                $string
!                        #maximum
!    ErrorOrganization            $string
!    ErrorOrganizationMissing        $string
!    ErrorPeriod                $string
!    ErrorPreparer                $string
!                        #maximum
!    ErrorProject                $string
!    ErrorSIDN                $string
!    ErrorSSN                $string
!    ErrorState                $string
!    ErrorStatus                $string
!    ErrorStreet                $string
!    ErrorStyle                $string
!    ErrorSubaccount                $string
!    ErrorTable                $string
!    ErrorTelephone                $string
!    ErrorTypeChartOrg            $string
!    ErrorWeekBegin                #value
!    ErrorWeekEnd                #value
!    ErrorYear                $string
!                        $length
!    ErrorZipCode                $string
!    PresentError                $string
!                        $message1
!                        $message2
!    ShowError                $string
!                        $message1
!                        $message2
!    StandardError                $string
!                        $message1
! *************************************************************************************************

begin-procedure ErrorAccount( $string )
! error message for invalid account
    let $message1    = 'An invalid account number was entered.'
    let $message2    = 'Please enter the account number as seven numeric characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure

begin-procedure ErrorAgingPeriod( $string )
! error message for invalid aging period
    let $message1    = 'An invalid aging period was entered.'
    let $message2    = 'Please enter the aging period as one to three numeric characters which represent a number greater than the previous aging period.'
    do PresentError( $string, $message1, $message2 )
end-procedure

begin-procedure ErrorAgingPeriodEntry( $string )
! error message for invalid aging period
    let $message1    = 'A previous aging period was blank.'
    let $message2    = 'Please enter the aging periods in consecutive order and leave blank entries at the end of the set.'
    do PresentError( $string, $message1, $message2 )
end-procedure

begin-procedure ErrorAlpha-Numeric ( $string, #alpha_length, #numeric_length )
! error message for invalid alpha-numeric code
        let $alpha_length = to_char (#alpha_length)
        let $numeric_length = to_char (#numeric_length)
    let $message1    = 'An invalid alpha-numeric code was entered.'
    let $message2    = 'Please enter the code as ' || $alpha_length || ' alpha characters and 1 - ' 
              || $numeric_length || ' numeric characters separated by a dash.'
    do PresentError( $string, $message1, $message2 )
end-procedure

begin-procedure ErrorCampus( $string )
! error message for invalid campus
    let $message1    = 'An invalid campus code was entered.'
    let $message2    = 'Please enter the campus code as two alpha characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure

begin-procedure ErrorCenter( $string )
! error message for invalid responsibility center
    let $message1    = 'An invalid responsibility center code was entered.'
    let $message2    =    'Please enter the responsibility center code as two alphanumeric characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure

begin-procedure ErrorChart( $string )
! error message for invalid chart code
    let $message1    = 'An invalid chart of accounts code was entered.'
    let $message2    =    'Please enter the chart of accounts code as two alpha characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure

begin-procedure ErrorChartAccount( $string )
! error message for invalid chart-account code
    let $message1    = 'An invalid chart-account code was entered.'
    let $message2    = 'Please enter the chart code (two alpha characters) and account number (seven numeric characters) separated by a dash.'
    do PresentError( $string, $message1, $message2 )
end-procedure

begin-procedure ErrorChartMissing( $string )
! error message for missing chart of accounts code
    let $message1    = 'At least one chart of accounts code is required.'
    let $message2    = 'Please enter a chart of accounts code as two alpha characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure

begin-procedure ErrorChartOrganization( $string )
! error message for invalid chart-organization code
    let $message1    = 'An invalid chart-organization code was entered.'
    let $message2    = 'Please enter the chart code (two alpha characters) and organization code (two to four alpha characters) separated by a dash.'
    do PresentError( $string, $message1, $message2 )
end-procedure

begin-procedure ErrorCity( $string )
! error message for invalid city
    let $message1    = 'An invalid city was entered.'
    let $message2    = 'Please enter the city as twenty-five characters including spaces.'
    do PresentError( $string, $message1, $message2 )
end-procedure

begin-procedure ErrorClass( $string )
! error message for invalid class code
    let $message1    = 'An invalid class code was entered.'
    let $message2    =    'Please enter the class code as two alpha characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure

begin-procedure ErrorComment( $string, #maximum )
! error message for invalid comment
    let $maximum    = to_char( #maximum )
    let $maximum    = $maximum || $_SPACE 
    let $message1    = 'An invalid comment was entered.'
    let $message2    = 'Please limit the length of a comment to ' || $maximum || 'characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure
    
begin-procedure ErrorCreditCard( $string )
! error message for invalid credit card number code
    let $message1    = 'An invalid credit card number was entered.'
    let $message2    =    'Please enter the credit card number as sixteen numeric characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure

begin-procedure ErrorDate( $string, #short_date, #long_date )
! error message for invalid date
    if ( #short_date )
        if ( #long_date )
            let $message2    = 'eight or ten characters in the format MM/DD/YY or MM/DD/CCYY respectively. '
        else
            let $message2    = 'eight characters in the format MM/DD/YY. '
        end-if
    else
        if ( #long_date )
            let $message2    = 'ten characters in the format MM/DD/CCYY. '
        else
            let $message2    = 'eight or ten characters in the format MM/DD/YY or MM/DD/CCYY respectively. '
        end-if
    end-if
    let $message1    = 'An invalid date was entered.'
    let $message2    = 'Please enter the date as ' || $message2
    do PresentError( $string, $message1, $message2 )
end-procedure

begin-procedure ErrorDateBeginMissing( #short_date, #long_date )
! error message for missing beginning date
    if ( #short_date )
        if ( #long_date )
            let $message2    = 'eight or ten characters in the format MM/DD/YY or MM/DD/CCYY respectively. '
        else
            let $message2    = 'eight characters in the format MM/DD/YY. '
        end-if
    else
        if ( #long_date )
            let $message2    = 'ten characters in the format MM/DD/CCYY. '
        else
            let $message2    = 'eight or ten characters in the format MM/DD/YY or MM/DD/CCYY respectively. '
        end-if
    end-if
    let $message1    = 'If an ending date is entered, a beginning date must also be entered.'
    let $message2    = 'Please enter the beginning date as ' || $message2
    do PresentError( $string, $message1, $message2 )
end-procedure

begin-procedure ErrorDayEnd( #value )
! error message for invalid ending day
    let $message1    = 'An invalid ending day was entered.'
    let $message2    = 'Please enter the ending day as one or two numeric characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure
    
begin-procedure ErrorDayBegin( #value )
! error message for invalid beginning day
    let $message1    = 'An invalid beginning day was entered.'
    let $message2    = 'Please enter the beginning day as one or two numeric characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure
    
begin-procedure ErrorEventCode( $string )
! error message for invalid event code
    let $message1    = 'An invalid event code was entered.'
    let $message2    = 'Please enter the event code as 8 numeric characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure
    
begin-procedure ErrorEventName( $string )
! error message for invalid event name
    let $message1    = 'An invalid event name was entered.'
    let $message2    = 'Please enter the event name as 40 characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure
    
begin-procedure ErrorEventLocation( $string )
! error message for invalid event location
    let $message1    = 'An invalid event location was entered.'
    let $message2    = 'Please enter the event location as 40 characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure
    
begin-procedure ErrorEventDate( $string )
! error message for invalid event date
    let $message1    = 'An invalid event date was entered.'
    let $message2    = 'Please enter the event date as 40 characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure
    
begin-procedure ErrorFirstMiddle( $string )
! error message for invalid first and middle names
    let $message1    = 'An invalid first and middle names were entered.'
    let $message2    = 'Please enter the first and middle names as twenty alpha charcters including spaces.'
    do PresentError( $string, $message1, $message2 )
end-procedure

begin-procedure ErrorID( $string )
end-procedure

begin-procedure ErrorTable( $string )
end-procedure

begin-procedure ErrorInterrupted
! error message for interrupted program
    let $message1    = 'This program has been interrupted.'
    let $message2    =    'Please correct the errors in input and try again.'
    do PresentError( $string, $message1, $message2 )
end-procedure

begin-procedure ErrorLast( $string )
! error message for invalid last name
    let $message1    = 'An invalid last name was entered.'
    let $message2    = 'Please enter the last name as twenty alpha characters including spaces.'
    do PresentError( $string, $message1, $message2 )
end-procedure

begin-procedure ErrorMajor( $string )
! error message for invalid major code
    let $message1    = 'An invalid major code was entered.'
    let $message2    =    'Please enter the major code as three alphanumeric characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure

begin-procedure ErrorMonthEnd( #value )
! error message for invalid ending month
    let $message1    = 'An invalid ending month was entered.'
    let $message2    = 'Please enter the ending month as one or two numeric characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure
    
begin-procedure ErrorMonthBegin( #value )
! error message for invalid beginning month
    let $message1    = 'An invalid beginning month was entered.'
    let $message2    = 'Please enter the beginning month as one or two numeric characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure
    
begin-procedure ErrorOrganization( $string )
! error message for invalid organization
    let $message1    = 'An invalid organization code was entered.'
    let $message2    =    'Please enter the organization code as two to four alpha characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure
    
begin-procedure ErrorOffice( $string, #maximum )
! error message for invalid preparer's office location
    let $maximum    = to_char( #maximum )
    let $maximum    = $maximum || $_SPACE 
    let $message1    = 'An invalid office location was entered.'
    let $message2    = 'Please limit the length of the office location to ' || $maximum || 'characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure

begin-procedure ErrorOrganizationMissing( $string )
! error message for missing organization code
    let $message1    = 'An organization code is required to generate a hierarchy.'
    let $message2    = 'Please enter an organization code as two to four alpha characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure

begin-procedure ErrorPeriod( $string )
! error message for invalid fiscal period
    let $message1    = 'An invalid fiscal period was entered.'
    let $message2    =    'Please enter a number between 1 and 13.'
    do PresentError( $string, $message1, $message2 )
end-procedure

begin-procedure ErrorPreparer( $string, #maximum )
! error message for invalid preparer's name
    let $maximum    = to_char( #maximum )
    let $maximum    = $maximum || $_SPACE 
    let $message1    = 'An invalid name was entered.'
    let $message2    = 'Please limit the length of the name to ' || $maximum || 'characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure

begin-procedure ErrorProject( $string )
! error message for invalid project code
    let $message1    = 'An invalid fiscal period was entered.'
    let $message2    = 'Please enter the project code as one to ten alphanumeric characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure
    
begin-procedure ErrorSIDN( $string )
! error message for invalid project code
    let $message1    = 'An invalid student identification number was entered.'
    let $message2    = 'Please enter the SIDN as nine numeric characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure
    
begin-procedure ErrorSSN( $string )
! error message for invalid project code
    let $message1    = 'An invalid social security number was entered.'
    let $message2    = 'Please enter the SSN as nine numeric characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure
    
begin-procedure ErrorState( $string )
! error message for invalid project code
    let $message1    = 'An invalid state abbreviation was entered.'
    let $message2    = 'Please enter the state abbreviation as two alpha characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure
    
begin-procedure ErrorStatus( $string )
! error message for invalid waiver status
    let $message1    = 'An invalid waiver status was entered.'
    let $message2    = 'Please enter the waiver status as N, X or Y.'
    do PresentError( $string, $message1, $message2 )
end-procedure
    
begin-procedure ErrorStreet( $string )
! error message for invalid street
    let $message1    = 'An invalid street was entered.'
    let $message2    = 'Please enter the street as thirty characters including spaces.'
    do PresentError( $string, $message1, $message2 )
end-procedure

begin-procedure ErrorStyle( $string )
! error message for invalid report style
    let $message1    = 'An invalid report style was entered.'
    let $message2    =    'Please enter one of the following report styles: DETAIL, CONSOLIDATED, or SUMMARY.'
    do PresentError( $string, $message1, $message2 )
end-procedure
    
begin-procedure ErrorDateType( $string )
! error message for invalid date type
    let $message1    = 'An invalid date type was entered.'
    let $message2    =    'Please enter one of the following date types: APPROVED or DUE.'
    do PresentError( $string, $message1, $message2 )
end-procedure
    
begin-procedure ErrorDetailLevel( $string )
! error message for invalid detail level
    let $message1    = 'An invalid detail level was entered.'
    let $message2    =    'Please enter following detail level as a single numeric character in the range 0-3.'
    do PresentError( $string, $message1, $message2 )
end-procedure
    
begin-procedure ErrorTypeChartOrg( $string )
! error message for invalid chart-organization type
    let $message1    = 'An invalid chart-organization type was entered.'
    let $message2    =    'Please enter one of the following chart-organization types: ACCOUNTING, BILL BY, or PROCESSING.'
    do PresentError( $string, $message1, $message2 )
end-procedure
    
begin-procedure ErrorSubaccount( $string )
! error message for invalid subaccount number
    let $message1    = 'An invalid subaccount number was entered.'
    let $message2    =    'Please enter the subaccount number as five alphanumeric characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure
    
begin-procedure ErrorTelephone( $string )
! error message for invalid preparer's telephone number
    let $maximum    = to_char( #maximum )
    let $message1    = 'An invalid telephone number was entered.'
    let $message2    = 'Please enter the telephone number as 7 numeric characters in the format ###-####.'
    do PresentError( $string, $message1, $message2 )
end-procedure

begin-procedure ErrorWeekEnd( #value )
! error message for invalid ending week
    let $message1    = 'An invalid ending week was entered.'
    let $message2    = 'Please enter the ending week as one or two numeric characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure
    
begin-procedure ErrorWeekBegin( #value )
! error message for invalid beginning week
    let $message1    = 'An invalid beginning week was entered.'
    let $message2    = 'Please enter the beginning week as one or two numeric characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure
    
begin-procedure ErrorYear( $string, $length )
! error message for invalid year
    let $length = $length || $_SPACE
    let $message1    = 'An invalid year was entered.'
    let $message2    = 'Please enter the year as ' || $length || 'numeric characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure
    
begin-procedure ErrorZipCode( $string )
! error message for invalid zip code
    let $message1    = 'An invalid zip code was entered.'
    let $message2    = 'Please enter the zip code as 5 or 9 numeric characters.'
    do PresentError( $string, $message1, $message2 )
end-procedure
    
begin-procedure PresentError( $string, $message1, $message2 )
! display error messages to delimited text file, standard text file, and monitor
    do StandardError( $string, $message1, $message2 )
    do ShowError( $string, $message1, $message2 )
    let #_NO_INPUT_ERROR    = 0
end-procedure

begin-procedure ShowError( $string, $message1, $message2 )
! display error messages to monitor
    display $string
    display $message1
    display $message2
    display $_BLANK
end-procedure

begin-procedure StandardError( $string, $message1, $message2 )
! display error messages to standard text file
    if ( #_HAS_PRINTED_ERROR )
        print $string        ( +1,1 )
    else
        print $string        ( 1,1 )
    end-if
    print $message1    ( +1,1 )
    print $message2    ( +1,1 )
    print $BLANK        ( +1,1 )
    let #_HAS_PRINTED_ERROR = 1
end-procedure


