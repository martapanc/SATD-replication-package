File path: src/core/org/apache/jmeter/reporters/ResultSaver.java
Comment: TODO is this the right thing to do?
Initial commit id: 5a0a9ac5e
Final commit id: da1c07e79
   Bugs between [       6]:
3e16150b7 Bug 52214 - Save Responses to a file - improve naming algorithm - add fixed width numbers - add optional timestamp - fix synchronisation
9cca78bc0 Bug 49365 - Allow result set to be written to file in a path relative to the loaded script
d81ad7e22 Bug 43119 - Save Responses to file: optionally omit the file number
59671c56f Bug 44575 - Result Saver can now save only successful results [Oops - should have been included in r639127]
e861ae37d Bug 36755 (patch 20073) - consistent closing of file streams
289264650 Bug 41944 - Subresults not handled recursively by ResultSaver
   Bugs after [       2]:
285abc026 Bug 60859 - Save Responses to a file : 2 elements with different configuration will overlap Add JUnits Bugzilla Id: 60859
11d942f4a Bug 60564 - Migrating LogKit to SLF4J - core/engine,plugin,report,reporters packages (2/2) Contributed by Woonsan Ko This closes #270 Bugzilla Id: 60564

Start block index: 69
End block index: 74
	public void clear()
	{
		//System.out.println("-- "+me+this.getName()+" "+Thread.currentThread().getName());
		super.clear();
		sequenceNumber=0; //TODO is this the right thing to do?
	}

*********************** Method when SATD was removed **************************

@Override
public void clear() {
    synchronized(LOCK){
        sequenceNumber = 0;
        if (getAddTimeStamp()) {
            DateFormat format = new SimpleDateFormat(TIMESTAMP_FORMAT);
            timeStamp = format.format(new Date());
        } else {
            timeStamp = "";
        }
        numberPadLength=getNumberPadLen();
    }
    super.clear();
}
