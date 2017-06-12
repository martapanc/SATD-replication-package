File path: src/main/org/apache/tools/ant/taskdefs/optional/net/FTP.java
Comment: XXX - why not simply new File(dir
Initial commit id: 152be14c
Final commit id: 13000c1a
   Bugs between [      25]:
5f20b9914 microoptimizations.  PR 50716
92663dfd6 ftp download fails for certain setups.  PR 49296.  Based on patch by Kristof Neirynck
d72413184 refactored <ftp> so commons-net may be loaded via a separate classloader.  Based on patch by Greg Roodt in PR 45860.  Not really tested, yet, therefore I haven't changed defaults.properties so far
b38b45047 make ftp task compile against commons-net 2.0 and 1.4.1.  PR 47669.  Submitted by Alexander Kurtakov
a4e5c13b8 Allow selectors for remote filesets in <ftp>.  Submitted by Mario Frasca.  PR 44726.  (unfortunately some whitespace changes slipped in as well, sorry for that).
a0e5b158c allow ftp remote verification to be disabled.  PR 35471.  Suggested by Viacheslav Garmash.
7e05b3d98 pr33969: tempfile task / FileUtils.createTempFile now actually creates the file.
02fce032e pr 41724: FTP task fail, FTPClient may return null in its arrays.
11f0a8954 Add logging of local vs remote timestamps in <ftp> task. PR:31812
f2d32ac66 Add initialSiteCommand and siteCommand attributes. PR:34257, 34853
22bdc578f Change so that systemTypeKey and all the new xxxConfig attributes work by the convention that setting the attribute to "" leaves the attribute unset, at whatever value it had before, that is, for all these, the default value of null.  This would enable property-file based development to proceed more easily. Update documentantation to reflect this. PR: 34978
ce53f734e Adapt Ant to use the new functionalities of commons-net 1.4.0 to enable greater configurability of the server: Month names other than English, date formats other than the standard ones (such as all-numeric timestamps on unix), and different server time zones can now be supported in Ant. PR:30706, 33443 Obtained from: Submitted by: Neeme Praks Reviewed by: Steve Cohen CVS: ---------------------------------------------------------------------- CVS: PR: CVS:   If this change addresses a PR in the problem report tracking CVS:   database, then enter the PR number(s) here. CVS: Obtained from: CVS:   If this change has been taken from another system, such as NCSA, CVS:   then name the system in this line, otherwise delete it. CVS: Submitted by: CVS:   If this code has been contributed to Apache by someone else; i.e., CVS:   they sent us a patch or a new module, then include their name/email CVS:   address here. If this is your work then delete this line. CVS: Reviewed by: CVS:   If we are doing pre-commit code reviews and someone else has CVS:   reviewed your changes, include their name(s) here. CVS:   If you have not had it reviewed then delete this line.
e5dd10d19 PR: 28797
2d1c5924e PR: 33770
17841320a FTP getTimeDiff was returning wrong value PR: 30595
c885f5683 remove authors from files PR: 27177 Obtained from: J.M. (Martijn) Kruithof
a679a5bbc Prevent NPE in method CheckRemoteSensitivity PR: 24440
d3f03ad75 Merge from ANT_16_BRANCH Fix problem with non absolute remote dirs PR: 23833
6b62a56cc More robust cleanup of temporary files, PR 17512
d9068e8df send the filename in the proper remote format in the site command done to chmod a file after a put PR: 23143
9a7f8d24f Optimize scanning in FTP.FTPDirectoryScanner, using similar algorithms to the ones introduced in DirectoryScanner. There is a gain when - the include patterns look like some/very/long/path - the remote file system is case sensitive - the casesensitive and followsymlinks options of the fileset are set to true (the default) PR: 20103
ad1b099b2 Add two new attributes timediffmillis and timediffauto for the ftp task so that the newer attribute can take into account time differences between local machine and remote server. PR: 19358
24f9da26b Issue a warning message indicating that selectors within filesets are not supported in the ftp task when the fileset is remote (all actions except put) PR: 18280
0bd649e56 resolve files sent to chmod PR: 21865
4a288273a When changing dirs resolve to the coorect separator conventions PR:	10755
   Bugs after [       1]:
43844a7e6 PR 56748 Spelling fixes, submitted by Ville Skyttä

Start block index: 710
End block index: 769
    protected void sendFile(FTPClient ftp, String dir, String filename)
        throws IOException, BuildException
    {
        InputStream instream = null;
        try
        {
            // XXX - why not simply new File(dir, filename)?
            File file = project.resolveFile(new File(dir, filename).getPath());

            if (newerOnly && isUpToDate(ftp, file, resolveFile(filename))) {
                return;
            }

            if (verbose)
            {
                log("transferring " + file.getAbsolutePath());
            }

            instream = new BufferedInputStream(new FileInputStream(file));

            createParents(ftp, filename);

            ftp.storeFile(resolveFile(filename), instream);
            boolean success=FTPReply.isPositiveCompletion(ftp.getReplyCode());
            if (!success)
            {
                String s="could not put file: " + ftp.getReplyString();
                if(skipFailedTransfers==true) {
                    log(s,Project.MSG_WARN);
                    skipped++;
                }
                else {
                    throw new BuildException(s);
                }

            }
            else {
                if (chmod != null) { // see if we should issue a chmod command
                    doSiteCommand(ftp,"chmod " + chmod + " " + filename);
                }
                log("File " + file.getAbsolutePath() + " copied to " + server,
                    Project.MSG_VERBOSE);
                transferred++;
            }
        }
        finally
        {
            if (instream != null)
            {
                try
                {
                    instream.close();
                }
                catch(IOException ex)
                {
                    // ignore it
                }
            }
        }
    }
