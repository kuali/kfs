To load these file sets, create an empty file with the name of a data or recon file, but replace the extension from .data or .recon to .done.

Description of file sets (ID numbers are padded with 0's on the left so the ID in the file name is 3 digits long):

ID 1 (i.e. files entp_test_file_001.data and entp_test_file_001.recon): NOT LOADABLE: a one line origin entry file that correctly parses, but does not reconcile correctly

ID 2: LOADABLE: 736 rows, reconciles correctly

ID 3: NOT LOADABLE: 736 rows, reconciliation file unparsable

ID 4: NOT LOADABLE: 1589 rows, does not reconcile because expects 736 rows

ID 5: LOADABLE: 10307 rows, reconciles correctly

ID 6: NOT LOADABLE: 10307 rows, recon fails

ID 7: NOT LOADABLE: data file missing

ID 8: NOT LOADABLE: recon file missing

ID 9: NOT LOADABLE: data and recon file missing
