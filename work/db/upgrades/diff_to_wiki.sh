
svn diff https://test.kuali.org/svn/kfs/tags/releases/release-3-0-1 https://test.kuali.org/svn/kfs/trunk --summarize > difference_summary.txt
# strip the url prefix
perl -pi -e 's@https://test.kuali.org/svn/kfs/tags/releases/release-3-0-1/@kfs/@g' difference_summary.txt
perl -pi -e 's/^(...... )(.*)$/| $1 | $2 |/' difference_summary.txt
perl -pi -e 's/^\| M /| Modified           /' difference_summary.txt
perl -pi -e 's/^\| A /| Added              /' difference_summary.txt
perl -pi -e 's/^\| D /| Deleted            /' difference_summary.txt
perl -pi -e 's/^\|  M/| Properties Modified/' difference_summary.txt
