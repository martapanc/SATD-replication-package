File path: src/components/org/apache/jmeter/reporters/MailerModel.java
Comment: TODO: should this be clearData()?
Initial commit id: 17eb25fca
Final commit id: fdfff0eab
   Bugs between [       8]:
c092a32f6 Bug 57193: Add param and return tags to javadoc Bugzilla Id: 57193
472da1514 Bug 53765 - Switch to commons-lang3-3.1 Bugzilla Id: 53765
cc414d419 Bug 52614 - MailerModel.sendMail has strange way to calculate debug setting
1a5cce61c Bug 52603 - MailerVisualizer : Enable SSL , TLS and Authentication Fixed issues discussed on mailing list
27a435f1e Bug 52603 - MailerVisualizer : Enable SSL , TLS and Authentication Fixed @throws Javadocs
5ab942607 Bug 52603 - MailerVisualizer : Enable SSL , TLS and Authentication Javadocs+fix to missing case
09e09b7ba Bug 52603 - MailerVisualizer : Enable SSL , TLS and Authentication
876a4a028 Bug 48603 - Mailer Visualiser sends two emails for a single failed response
   Bugs after [       1]:
3f4bc4990 Bug 60564 - Migrating LogKit to SLF4J - Replace logkit loggers with slf4j ones with keeping the current logkit binding solution Contributed by Woonsan Ko This closes #265 Bugzilla Id: 60564

Start block index: 216
End block index: 223
	public synchronized void clear() {// TODO: should this be clearData()?
		failureCount = 0;
		successCount = 0;
		siteDown = false;
		successMsgSent = false;
		failureMsgSent = false;
		notifyChangeListeners();
	}

*********************** Method when SATD was removed **************************

@Override
public synchronized void clear() {
    failureCount = 0;
    successCount = 0;
    siteDown = false;
    successMsgSent = false;
    failureMsgSent = false;
    notifyChangeListeners();
}
