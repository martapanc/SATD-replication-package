File path: src/core/org/apache/jmeter/reporters/ResultSaver.java
Comment: TODO - perhaps save other items such as headers?
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

Start block index: 0
End block index: 157
package org.apache.jmeter.reporters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;

import org.apache.jmeter.samplers.Clearable;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * Save Result responseData to a set of files
 * TODO - perhaps save other items such as headers?
 *
 * This is mainly intended for validation tests
 *
 * @author sebb AT apache DOT org
 * @version $Revision$ Last updated: $Date$
 */
public class ResultSaver
    extends AbstractTestElement
    implements Serializable,
    SampleListener,
    Clearable
{
	private static final Logger log = LoggingManager.getLoggerForClass();

    // File name sequence number
    private static long sequenceNumber = 0;

	public static final String FILENAME = "FileSaver.filename";

    private static synchronized long nextNumber(){
    	return ++sequenceNumber;
    }
    /*
     * Constructor is initially called once for each occurrence in the test plan
     * For GUI, several more instances are created
     * Then clear is called at start of test
     * Called several times during test startup
     * The name will not necessarily have been set at this point.
     */
	public ResultSaver(){
		super();
		//log.debug(Thread.currentThread().getName());
		//System.out.println(">> "+me+"        "+this.getName()+" "+Thread.currentThread().getName());
	}

    /*
     * Constructor for use during startup
     * (intended for non-GUI use)
     * @param name of summariser
     */
    public ResultSaver(String name){
    	this();
    	setName(name);
    }

    /*
     * This is called once for each occurrence in the test plan, before the start of the test.
     * The super.clear() method clears the name (and all other properties),
     * so it is called last.
     */
	public void clear()
	{
		//System.out.println("-- "+me+this.getName()+" "+Thread.currentThread().getName());
		super.clear();
		sequenceNumber=0; //TODO is this the right thing to do?
	}

	/**
	 * Contains the items needed to collect stats for a summariser
	 *
	 * @author sebb AT apache DOT org
	 * @version $revision$ Last updated: $date$
	 */
	private static class Totals{

	}

	/**
	 * Accumulates the sample in two SampleResult objects
	 * - one for running totals, and the other for deltas
	 *
	 * @see org.apache.jmeter.samplers.SampleListener#sampleOccurred(org.apache.jmeter.samplers.SampleEvent)
	 */
	public void sampleOccurred(SampleEvent e) {
		SampleResult s = e.getResult();
		saveSample(s);
		SampleResult []sr = s.getSubResults();
		if (sr != null){
			for (int i = 0; i < sr.length; i++){
				saveSample(sr[i]);
			}

		}
    }

	/**
	 * @param s SampleResult to save
	 */
	private void saveSample(SampleResult s) {
		nextNumber();
		String fileName=makeFileName(s.getContentType());
		log.debug("Saving "+s.getSampleLabel()+" in "+fileName);
		//System.out.println(fileName);
		File out = new File(fileName);
		FileOutputStream pw=null;
		try {
			pw = new FileOutputStream(out);
			pw.write(s.getResponseData());
			pw.close();
		} catch (FileNotFoundException e1) {
			log.error("Error creating sample file for "+s.getSampleLabel(),e1);
		} catch (IOException e1) {
			log.error("Error saving sample "+s.getSampleLabel(),e1);
		}
	}
	/**
	 * @return fileName composed of fixed prefix, a number,
	 * and a suffix derived from the contentType
	 */
	private String makeFileName(String contentType) {
		String suffix;
		int i = contentType.indexOf("/");
		if (i == -1){
			suffix="unknown";
		} else {
			suffix=contentType.substring(i+1);
		}
		return getFilename()+sequenceNumber+"."+suffix;
	}
	/* (non-Javadoc)
	 * @see org.apache.jmeter.samplers.SampleListener#sampleStarted(org.apache.jmeter.samplers.SampleEvent)
	 */
	public void sampleStarted(SampleEvent e)
	{
		// not used
	}

	/* (non-Javadoc)
	 * @see org.apache.jmeter.samplers.SampleListener#sampleStopped(org.apache.jmeter.samplers.SampleEvent)
	 */
	public void sampleStopped(SampleEvent e)
	{
		// not used
	}
	private String getFilename()
	{
		return getPropertyAsString(FILENAME);
	}
}

*********************** Method when SATD was removed **************************

public class ResultSaver
    extends AbstractTestElement
    implements Serializable,
    SampleListener,
    Clearable
{
	private static final Logger log = LoggingManager.getLoggerForClass();

    // File name sequence number
    private static long sequenceNumber = 0;

	public static final String FILENAME = "FileSaver.filename";

    private static synchronized long nextNumber(){
    	return ++sequenceNumber;
    }
    /*
     * Constructor is initially called once for each occurrence in the test plan
     * For GUI, several more instances are created
     * Then clear is called at start of test
     * Called several times during test startup
     * The name will not necessarily have been set at this point.
     */
	public ResultSaver(){
		super();
		//log.debug(Thread.currentThread().getName());
		//System.out.println(">> "+me+"        "+this.getName()+" "+Thread.currentThread().getName());
	}

    /*
     * Constructor for use during startup
     * (intended for non-GUI use)
     * @param name of summariser
     */
    public ResultSaver(String name){
    	this();
    	setName(name);
    }

    /*
     * This is called once for each occurrence in the test plan, before the start of the test.
     * The super.clear() method clears the name (and all other properties),
     * so it is called last.
     */
	public void clear()
	{
		//System.out.println("-- "+me+this.getName()+" "+Thread.currentThread().getName());
		super.clear();
		sequenceNumber=0; //TODO is this the right thing to do?
	}

	/**
	 * Contains the items needed to collect stats for a summariser
	 *
	 * @author sebb AT apache DOT org
	 * @version $revision$ Last updated: $date$
	 */
	private static class Totals{

	}

	/**
	 * Accumulates the sample in two SampleResult objects
	 * - one for running totals, and the other for deltas
	 *
	 * @see org.apache.jmeter.samplers.SampleListener#sampleOccurred(org.apache.jmeter.samplers.SampleEvent)
	 */
	public void sampleOccurred(SampleEvent e) {
		SampleResult s = e.getResult();
		saveSample(s);
		SampleResult []sr = s.getSubResults();
		if (sr != null){
			for (int i = 0; i < sr.length; i++){
				saveSample(sr[i]);
			}

		}
    }

	/**
	 * @param s SampleResult to save
	 */
	private void saveSample(SampleResult s) {
		nextNumber();
		String fileName=makeFileName(s.getContentType());
		log.debug("Saving "+s.getSampleLabel()+" in "+fileName);
		//System.out.println(fileName);
		File out = new File(fileName);
		FileOutputStream pw=null;
		try {
			pw = new FileOutputStream(out);
			pw.write(s.getResponseData());
			pw.close();
		} catch (FileNotFoundException e1) {
			log.error("Error creating sample file for "+s.getSampleLabel(),e1);
		} catch (IOException e1) {
			log.error("Error saving sample "+s.getSampleLabel(),e1);
		}
	}
	/**
	 * @return fileName composed of fixed prefix, a number,
	 * and a suffix derived from the contentType
	 */
	private String makeFileName(String contentType) {
		String suffix;
		int i = contentType.indexOf("/");
		if (i == -1){
			suffix="unknown";
		} else {
			suffix=contentType.substring(i+1);
		}
		return getFilename()+sequenceNumber+"."+suffix;
	}
	/* (non-Javadoc)
	 * @see org.apache.jmeter.samplers.SampleListener#sampleStarted(org.apache.jmeter.samplers.SampleEvent)
	 */
	public void sampleStarted(SampleEvent e)
	{
		// not used
	}

	/* (non-Javadoc)
	 * @see org.apache.jmeter.samplers.SampleListener#sampleStopped(org.apache.jmeter.samplers.SampleEvent)
	 */
	public void sampleStopped(SampleEvent e)
	{
		// not used
	}
	private String getFilename()
	{
		return getPropertyAsString(FILENAME);
	}
}
