diff --git a/src/main/org/apache/tools/zip/ZipFile.java b/src/main/org/apache/tools/zip/ZipFile.java
index 2336916fc..fba3ab298 100644
--- a/src/main/org/apache/tools/zip/ZipFile.java
+++ b/src/main/org/apache/tools/zip/ZipFile.java
@@ -1,749 +1,972 @@
 /*
  *  Licensed to the Apache Software Foundation (ASF) under one or more
  *  contributor license agreements.  See the NOTICE file distributed with
  *  this work for additional information regarding copyright ownership.
  *  The ASF licenses this file to You under the Apache License, Version 2.0
  *  (the "License"); you may not use this file except in compliance with
  *  the License.  You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  *  Unless required by applicable law or agreed to in writing, software
  *  distributed under the License is distributed on an "AS IS" BASIS,
  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *  See the License for the specific language governing permissions and
  *  limitations under the License.
  *
  */
 
 package org.apache.tools.zip;
 
+import java.io.EOFException;
 import java.io.File;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.RandomAccessFile;
-import java.util.Calendar;
+import java.util.Arrays;
 import java.util.Collections;
-import java.util.Date;
+import java.util.Comparator;
 import java.util.Enumeration;
 import java.util.HashMap;
-import java.util.HashSet;
+import java.util.LinkedHashMap;
 import java.util.Map;
-import java.util.zip.CRC32;
 import java.util.zip.Inflater;
 import java.util.zip.InflaterInputStream;
 import java.util.zip.ZipException;
 
+import static org.apache.tools.zip.ZipConstants.DWORD;
+import static org.apache.tools.zip.ZipConstants.SHORT;
+import static org.apache.tools.zip.ZipConstants.WORD;
+import static org.apache.tools.zip.ZipConstants.ZIP64_MAGIC;
+import static org.apache.tools.zip.ZipConstants.ZIP64_MAGIC_SHORT;
+
 /**
  * Replacement for <code>java.util.ZipFile</code>.
  *
  * <p>This class adds support for file name encodings other than UTF-8
  * (which is required to work on ZIP files created by native zip tools
  * and is able to skip a preamble like the one found in self
  * extracting archives.  Furthermore it returns instances of
  * <code>org.apache.tools.zip.ZipEntry</code> instead of
  * <code>java.util.zip.ZipEntry</code>.</p>
  *
  * <p>It doesn't extend <code>java.util.zip.ZipFile</code> as it would
  * have to reimplement all methods anyway.  Like
  * <code>java.util.ZipFile</code>, it uses RandomAccessFile under the
- * covers and supports compressed and uncompressed entries.</p>
+ * covers and supports compressed and uncompressed entries.  As of
+ * Apache Ant 1.9.0 it also transparently supports Zip64
+ * extensions and thus individual entries and archives larger than 4
+ * GB or with more than 65536 entries.</p>
  *
  * <p>The method signatures mimic the ones of
  * <code>java.util.zip.ZipFile</code>, with a couple of exceptions:
  *
  * <ul>
  *   <li>There is no getName method.</li>
  *   <li>entries has been renamed to getEntries.</li>
  *   <li>getEntries and getEntry return
  *   <code>org.apache.tools.zip.ZipEntry</code> instances.</li>
  *   <li>close is allowed to throw IOException.</li>
  * </ul>
  *
  */
 public class ZipFile {
     private static final int HASH_SIZE = 509;
-    private static final int SHORT     =   2;
-    private static final int WORD      =   4;
-    private static final int NIBLET_MASK = 0x0f;
-    private static final int BYTE_SHIFT = 8;
+    static final int NIBLET_MASK = 0x0f;
+    static final int BYTE_SHIFT = 8;
     private static final int POS_0 = 0;
     private static final int POS_1 = 1;
     private static final int POS_2 = 2;
     private static final int POS_3 = 3;
 
     /**
-     * Maps ZipEntrys to Longs, recording the offsets of the local
-     * file headers.
+     * Maps ZipEntrys to two longs, recording the offsets of
+     * the local file headers and the start of entry data.
      */
-    private final Map entries = new HashMap(HASH_SIZE);
+    private final Map<ZipEntry, OffsetEntry> entries =
+        new LinkedHashMap<ZipEntry, OffsetEntry>(HASH_SIZE);
 
     /**
      * Maps String to ZipEntrys, name -> actual entry.
      */
-    private final Map nameMap = new HashMap(HASH_SIZE);
+    private final Map<String, ZipEntry> nameMap =
+        new HashMap<String, ZipEntry>(HASH_SIZE);
 
     private static final class OffsetEntry {
         private long headerOffset = -1;
         private long dataOffset = -1;
     }
 
     /**
      * The encoding to use for filenames and the file comment.
      *
      * <p>For a list of possible values see <a
      * href="http://java.sun.com/j2se/1.5.0/docs/guide/intl/encoding.doc.html">http://java.sun.com/j2se/1.5.0/docs/guide/intl/encoding.doc.html</a>.
      * Defaults to the platform's default character encoding.</p>
      */
-    private String encoding = null;
+    private final String encoding;
 
     /**
      * The zip encoding to use for filenames and the file comment.
      */
     private final ZipEncoding zipEncoding;
 
     /**
+     * File name of actual source.
+     */
+    private final String archiveName;
+
+    /**
      * The actual data source.
      */
-    private RandomAccessFile archive;
+    private final RandomAccessFile archive;
 
     /**
      * Whether to look for and use Unicode extra fields.
      */
     private final boolean useUnicodeExtraFields;
 
     /**
+     * Whether the file is closed.
+     */
+    private boolean closed;
+
+    /**
      * Opens the given file for reading, assuming the platform's
      * native encoding for file names.
      *
      * @param f the archive.
      *
      * @throws IOException if an error occurs while reading the file.
      */
     public ZipFile(File f) throws IOException {
         this(f, null);
     }
 
     /**
      * Opens the given file for reading, assuming the platform's
      * native encoding for file names.
      *
      * @param name name of the archive.
      *
      * @throws IOException if an error occurs while reading the file.
      */
     public ZipFile(String name) throws IOException {
         this(new File(name), null);
     }
 
     /**
      * Opens the given file for reading, assuming the specified
      * encoding for file names, scanning unicode extra fields.
      *
      * @param name name of the archive.
-     * @param encoding the encoding to use for file names
+     * @param encoding the encoding to use for file names, use null
+     * for the platform's default encoding
      *
      * @throws IOException if an error occurs while reading the file.
      */
     public ZipFile(String name, String encoding) throws IOException {
         this(new File(name), encoding, true);
     }
 
     /**
      * Opens the given file for reading, assuming the specified
      * encoding for file names and scanning for unicode extra fields.
      *
      * @param f the archive.
      * @param encoding the encoding to use for file names, use null
      * for the platform's default encoding
      *
      * @throws IOException if an error occurs while reading the file.
      */
     public ZipFile(File f, String encoding) throws IOException {
         this(f, encoding, true);
     }
 
     /**
      * Opens the given file for reading, assuming the specified
      * encoding for file names.
      *
      * @param f the archive.
      * @param encoding the encoding to use for file names, use null
      * for the platform's default encoding
      * @param useUnicodeExtraFields whether to use InfoZIP Unicode
      * Extra Fields (if present) to set the file names.
      *
      * @throws IOException if an error occurs while reading the file.
      */
     public ZipFile(File f, String encoding, boolean useUnicodeExtraFields)
         throws IOException {
+        this.archiveName = f.getAbsolutePath();
         this.encoding = encoding;
         this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
         this.useUnicodeExtraFields = useUnicodeExtraFields;
         archive = new RandomAccessFile(f, "r");
         boolean success = false;
         try {
-            Map entriesWithoutUTF8Flag = populateFromCentralDirectory();
+            Map<ZipEntry, NameAndComment> entriesWithoutUTF8Flag =
+                populateFromCentralDirectory();
             resolveLocalFileHeaderData(entriesWithoutUTF8Flag);
             success = true;
         } finally {
             if (!success) {
                 try {
+                    closed = true;
                     archive.close();
                 } catch (IOException e2) {
                     // swallow, throw the original exception instead
                 }
             }
         }
     }
 
     /**
      * The encoding to use for filenames and the file comment.
      *
      * @return null if using the platform's default character encoding.
      */
     public String getEncoding() {
         return encoding;
     }
 
     /**
      * Closes the archive.
      * @throws IOException if an error occurs closing the archive.
      */
     public void close() throws IOException {
+        // this flag is only written here and read in finalize() which
+        // can never be run in parallel.
+        // no synchronization needed.
+        closed = true;
+
         archive.close();
     }
 
     /**
      * close a zipfile quietly; throw no io fault, do nothing
      * on a null parameter
      * @param zipfile file to close, can be null
      */
     public static void closeQuietly(ZipFile zipfile) {
         if (zipfile != null) {
             try {
                 zipfile.close();
             } catch (IOException e) {
                 //ignore
             }
         }
     }
 
     /**
      * Returns all entries.
+     *
+     * <p>Entries will be returned in the same order they appear
+     * within the archive's central directory.</p>
+     *
      * @return all entries as {@link ZipEntry} instances
      */
-    public Enumeration getEntries() {
+    public Enumeration<ZipEntry> getEntries() {
         return Collections.enumeration(entries.keySet());
     }
 
     /**
-     * Returns a named entry - or <code>null</code> if no entry by
+     * Returns all entries in physical order.
+     *
+     * <p>Entries will be returned in the same order their contents
+     * appear within the archive.</p>
+     *
+     * @return all entries as {@link ZipEntry} instances
+     *
+     * @since Ant 1.9.0
+     */
+    public Enumeration<ZipEntry> getEntriesInPhysicalOrder() {
+        ZipEntry[] allEntries =
+            entries.keySet().toArray(new ZipEntry[0]);
+        Arrays.sort(allEntries, OFFSET_COMPARATOR);
+        return Collections.enumeration(Arrays.asList(allEntries));
+    }
+
+    /**
+     * Returns a named entry - or {@code null} if no entry by
      * that name exists.
      * @param name name of the entry.
      * @return the ZipEntry corresponding to the given name - or
-     * <code>null</code> if not present.
+     * {@code null} if not present.
      */
     public ZipEntry getEntry(String name) {
-        return (ZipEntry) nameMap.get(name);
+        return nameMap.get(name);
+    }
+
+    /**
+     * Whether this class is able to read the given entry.
+     *
+     * <p>May return false if it is set up to use encryption or a
+     * compression method that hasn't been implemented yet.</p>
+     */
+    public boolean canReadEntryData(ZipEntry ze) {
+        return ZipUtil.canHandleEntryData(ze);
     }
 
     /**
      * Returns an InputStream for reading the contents of the given entry.
+     *
      * @param ze the entry to get the stream for.
      * @return a stream to read the entry from.
      * @throws IOException if unable to create an input stream from the zipentry
-     * @throws ZipException if the zipentry has an unsupported
-     * compression method
+     * @throws ZipException if the zipentry uses an unsupported feature
      */
     public InputStream getInputStream(ZipEntry ze)
         throws IOException, ZipException {
-        OffsetEntry offsetEntry = (OffsetEntry) entries.get(ze);
+        OffsetEntry offsetEntry = entries.get(ze);
         if (offsetEntry == null) {
             return null;
         }
+        ZipUtil.checkRequestedFeatures(ze);
         long start = offsetEntry.dataOffset;
         BoundedInputStream bis =
             new BoundedInputStream(start, ze.getCompressedSize());
         switch (ze.getMethod()) {
             case ZipEntry.STORED:
                 return bis;
             case ZipEntry.DEFLATED:
                 bis.addDummy();
                 final Inflater inflater = new Inflater(true);
                 return new InflaterInputStream(bis, inflater) {
+                    @Override
                     public void close() throws IOException {
                         super.close();
                         inflater.end();
                     }
                 };
             default:
                 throw new ZipException("Found unsupported compression method "
                                        + ze.getMethod());
         }
     }
 
+    /**
+     * Ensures that the close method of this zipfile is called when
+     * there are no more references to it.
+     * @see #close()
+     */
+    @Override
+    protected void finalize() throws Throwable {
+        try {
+            if (!closed) {
+                System.err.println("Cleaning up unclosed ZipFile for archive "
+                                   + archiveName);
+                close();
+            }
+        } finally {
+            super.finalize();
+        }
+    }
+
+    /**
+     * Length of a "central directory" entry structure without file
+     * name, extra fields or comment.
+     */
     private static final int CFH_LEN =
         /* version made by                 */ SHORT
         /* version needed to extract       */ + SHORT
         /* general purpose bit flag        */ + SHORT
         /* compression method              */ + SHORT
         /* last mod file time              */ + SHORT
         /* last mod file date              */ + SHORT
         /* crc-32                          */ + WORD
         /* compressed size                 */ + WORD
         /* uncompressed size               */ + WORD
         /* filename length                 */ + SHORT
         /* extra field length              */ + SHORT
         /* file comment length             */ + SHORT
         /* disk number start               */ + SHORT
         /* internal file attributes        */ + SHORT
         /* external file attributes        */ + WORD
         /* relative offset of local header */ + WORD;
 
+    private static final long CFH_SIG =
+        ZipLong.getValue(ZipOutputStream.CFH_SIG);
+
     /**
      * Reads the central directory of the given archive and populates
      * the internal tables with ZipEntry instances.
      *
      * <p>The ZipEntrys will know all data that can be obtained from
      * the central directory alone, but not the data that requires the
      * local file header or additional data to be read.</p>
      *
-     * @return a Map&lt;ZipEntry, NameAndComment>&gt; of
-     * zipentries that didn't have the language encoding flag set when
-     * read.
+     * @return a map of zipentries that didn't have the language
+     * encoding flag set when read.
      */
-    private Map populateFromCentralDirectory()
+    private Map<ZipEntry, NameAndComment> populateFromCentralDirectory()
         throws IOException {
-        HashMap noUTF8Flag = new HashMap();
+        HashMap<ZipEntry, NameAndComment> noUTF8Flag =
+            new HashMap<ZipEntry, NameAndComment>();
 
         positionAtCentralDirectory();
 
-        byte[] cfh = new byte[CFH_LEN];
-
         byte[] signatureBytes = new byte[WORD];
         archive.readFully(signatureBytes);
         long sig = ZipLong.getValue(signatureBytes);
-        final long cfhSig = ZipLong.getValue(ZipOutputStream.CFH_SIG);
-        if (sig != cfhSig && startsWithLocalFileHeader()) {
+
+        if (sig != CFH_SIG && startsWithLocalFileHeader()) {
             throw new IOException("central directory is empty, can't expand"
                                   + " corrupt archive.");
         }
-        while (sig == cfhSig) {
-            archive.readFully(cfh);
-            int off = 0;
-            ZipEntry ze = new ZipEntry();
 
-            int versionMadeBy = ZipShort.getValue(cfh, off);
-            off += SHORT;
-            ze.setPlatform((versionMadeBy >> BYTE_SHIFT) & NIBLET_MASK);
+        while (sig == CFH_SIG) {
+            readCentralDirectoryEntry(noUTF8Flag);
+            archive.readFully(signatureBytes);
+            sig = ZipLong.getValue(signatureBytes);
+        }
+        return noUTF8Flag;
+    }
+
+    /**
+     * Reads an individual entry of the central directory, creats an
+     * ZipEntry from it and adds it to the global maps.
+     *
+     * @param noUTF8Flag map used to collect entries that don't have
+     * their UTF-8 flag set and whose name will be set by data read
+     * from the local file header later.  The current entry may be
+     * added to this map.
+     */
+    private void
+        readCentralDirectoryEntry(Map<ZipEntry, NameAndComment> noUTF8Flag)
+        throws IOException {
+        byte[] cfh = new byte[CFH_LEN];
 
-            off += SHORT; // skip version info
+        archive.readFully(cfh);
+        int off = 0;
+        ZipEntry ze = new ZipEntry();
 
-            final int generalPurposeFlag = ZipShort.getValue(cfh, off);
-            final boolean hasUTF8Flag = 
-                (generalPurposeFlag & ZipOutputStream.UFT8_NAMES_FLAG) != 0;
-            final ZipEncoding entryEncoding =
-                hasUTF8Flag ? ZipEncodingHelper.UTF8_ZIP_ENCODING : zipEncoding;
+        int versionMadeBy = ZipShort.getValue(cfh, off);
+        off += SHORT;
+        ze.setPlatform((versionMadeBy >> BYTE_SHIFT) & NIBLET_MASK);
 
-            off += SHORT;
+        off += SHORT; // skip version info
 
-            ze.setMethod(ZipShort.getValue(cfh, off));
-            off += SHORT;
+        final GeneralPurposeBit gpFlag = GeneralPurposeBit.parse(cfh, off);
+        final boolean hasUTF8Flag = gpFlag.usesUTF8ForNames();
+        final ZipEncoding entryEncoding =
+            hasUTF8Flag ? ZipEncodingHelper.UTF8_ZIP_ENCODING : zipEncoding;
+        ze.setGeneralPurposeBit(gpFlag);
 
-            // FIXME this is actually not very cpu cycles friendly as we are converting from
-            // dos to java while the underlying Sun implementation will convert
-            // from java to dos time for internal storage...
-            long time = dosToJavaTime(ZipLong.getValue(cfh, off));
-            ze.setTime(time);
-            off += WORD;
+        off += SHORT;
 
-            ze.setCrc(ZipLong.getValue(cfh, off));
-            off += WORD;
+        ze.setMethod(ZipShort.getValue(cfh, off));
+        off += SHORT;
 
-            ze.setCompressedSize(ZipLong.getValue(cfh, off));
-            off += WORD;
+        long time = ZipUtil.dosToJavaTime(ZipLong.getValue(cfh, off));
+        ze.setTime(time);
+        off += WORD;
 
-            ze.setSize(ZipLong.getValue(cfh, off));
-            off += WORD;
+        ze.setCrc(ZipLong.getValue(cfh, off));
+        off += WORD;
 
-            int fileNameLen = ZipShort.getValue(cfh, off);
-            off += SHORT;
+        ze.setCompressedSize(ZipLong.getValue(cfh, off));
+        off += WORD;
 
-            int extraLen = ZipShort.getValue(cfh, off);
-            off += SHORT;
+        ze.setSize(ZipLong.getValue(cfh, off));
+        off += WORD;
 
-            int commentLen = ZipShort.getValue(cfh, off);
-            off += SHORT;
+        int fileNameLen = ZipShort.getValue(cfh, off);
+        off += SHORT;
 
-            off += SHORT; // disk number
+        int extraLen = ZipShort.getValue(cfh, off);
+        off += SHORT;
 
-            ze.setInternalAttributes(ZipShort.getValue(cfh, off));
-            off += SHORT;
+        int commentLen = ZipShort.getValue(cfh, off);
+        off += SHORT;
 
-            ze.setExternalAttributes(ZipLong.getValue(cfh, off));
-            off += WORD;
+        int diskStart = ZipShort.getValue(cfh, off);
+        off += SHORT;
 
-            byte[] fileName = new byte[fileNameLen];
-            archive.readFully(fileName);
-            ze.setName(entryEncoding.decode(fileName));
+        ze.setInternalAttributes(ZipShort.getValue(cfh, off));
+        off += SHORT;
 
-            // LFH offset,
-            OffsetEntry offset = new OffsetEntry();
-            offset.headerOffset = ZipLong.getValue(cfh, off);
-            // data offset will be filled later
-            entries.put(ze, offset);
+        ze.setExternalAttributes(ZipLong.getValue(cfh, off));
+        off += WORD;
 
-            nameMap.put(ze.getName(), ze);
+        byte[] fileName = new byte[fileNameLen];
+        archive.readFully(fileName);
+        ze.setName(entryEncoding.decode(fileName), fileName);
 
-            byte[] cdExtraData = new byte[extraLen];
-            archive.readFully(cdExtraData);
-            ze.setCentralDirectoryExtra(cdExtraData);
+        // LFH offset,
+        OffsetEntry offset = new OffsetEntry();
+        offset.headerOffset = ZipLong.getValue(cfh, off);
+        // data offset will be filled later
+        entries.put(ze, offset);
 
-            byte[] comment = new byte[commentLen];
-            archive.readFully(comment);
-            ze.setComment(entryEncoding.decode(comment));
+        nameMap.put(ze.getName(), ze);
 
-            archive.readFully(signatureBytes);
-            sig = ZipLong.getValue(signatureBytes);
+        byte[] cdExtraData = new byte[extraLen];
+        archive.readFully(cdExtraData);
+        ze.setCentralDirectoryExtra(cdExtraData);
+
+        setSizesAndOffsetFromZip64Extra(ze, offset, diskStart);
+
+        byte[] comment = new byte[commentLen];
+        archive.readFully(comment);
+        ze.setComment(entryEncoding.decode(comment));
+
+        if (!hasUTF8Flag && useUnicodeExtraFields) {
+            noUTF8Flag.put(ze, new NameAndComment(fileName, comment));
+        }
+    }
+
+    /**
+     * If the entry holds a Zip64 extended information extra field,
+     * read sizes from there if the entry's sizes are set to
+     * 0xFFFFFFFFF, do the same for the offset of the local file
+     * header.
+     *
+     * <p>Ensures the Zip64 extra either knows both compressed and
+     * uncompressed size or neither of both as the internal logic in
+     * ExtraFieldUtils forces the field to create local header data
+     * even if they are never used - and here a field with only one
+     * size would be invalid.</p>
+     */
+    private void setSizesAndOffsetFromZip64Extra(ZipEntry ze,
+                                                 OffsetEntry offset,
+                                                 int diskStart)
+        throws IOException {
+        Zip64ExtendedInformationExtraField z64 =
+            (Zip64ExtendedInformationExtraField)
+            ze.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
+        if (z64 != null) {
+            boolean hasUncompressedSize = ze.getSize() == ZIP64_MAGIC;
+            boolean hasCompressedSize = ze.getCompressedSize() == ZIP64_MAGIC;
+            boolean hasRelativeHeaderOffset =
+                offset.headerOffset == ZIP64_MAGIC;
+            z64.reparseCentralDirectoryData(hasUncompressedSize,
+                                            hasCompressedSize,
+                                            hasRelativeHeaderOffset,
+                                            diskStart == ZIP64_MAGIC_SHORT);
+
+            if (hasUncompressedSize) {
+                ze.setSize(z64.getSize().getLongValue());
+            } else if (hasCompressedSize) {
+                z64.setSize(new ZipEightByteInteger(ze.getSize()));
+            }
+
+            if (hasCompressedSize) {
+                ze.setCompressedSize(z64.getCompressedSize().getLongValue());
+            } else if (hasUncompressedSize) {
+                z64.setCompressedSize(new ZipEightByteInteger(ze.getCompressedSize()));
+            }
 
-            if (!hasUTF8Flag && useUnicodeExtraFields) {
-                noUTF8Flag.put(ze, new NameAndComment(fileName, comment));
+            if (hasRelativeHeaderOffset) {
+                offset.headerOffset =
+                    z64.getRelativeHeaderOffset().getLongValue();
             }
         }
-        return noUTF8Flag;
     }
 
+    /**
+     * Length of the "End of central directory record" - which is
+     * supposed to be the last structure of the archive - without file
+     * comment.
+     */
     private static final int MIN_EOCD_SIZE =
         /* end of central dir signature    */ WORD
         /* number of this disk             */ + SHORT
         /* number of the disk with the     */
         /* start of the central directory  */ + SHORT
         /* total number of entries in      */
         /* the central dir on this disk    */ + SHORT
         /* total number of entries in      */
         /* the central dir                 */ + SHORT
         /* size of the central directory   */ + WORD
         /* offset of start of central      */
         /* directory with respect to       */
         /* the starting disk number        */ + WORD
         /* zipfile comment length          */ + SHORT;
 
+    /**
+     * Maximum length of the "End of central directory record" with a
+     * file comment.
+     */
     private static final int MAX_EOCD_SIZE = MIN_EOCD_SIZE
-        /* maximum length of zipfile comment */ + 0xFFFF;
+        /* maximum length of zipfile comment */ + ZIP64_MAGIC_SHORT;
 
+    /**
+     * Offset of the field that holds the location of the first
+     * central directory entry inside the "End of central directory
+     * record" relative to the start of the "End of central directory
+     * record".
+     */
     private static final int CFD_LOCATOR_OFFSET =
         /* end of central dir signature    */ WORD
         /* number of this disk             */ + SHORT
         /* number of the disk with the     */
         /* start of the central directory  */ + SHORT
         /* total number of entries in      */
         /* the central dir on this disk    */ + SHORT
         /* total number of entries in      */
         /* the central dir                 */ + SHORT
         /* size of the central directory   */ + WORD;
 
     /**
-     * Searches for the &quot;End of central dir record&quot;, parses
+     * Length of the "Zip64 end of central directory locator" - which
+     * should be right in front of the "end of central directory
+     * record" if one is present at all.
+     */
+    private static final int ZIP64_EOCDL_LENGTH =
+        /* zip64 end of central dir locator sig */ WORD
+        /* number of the disk with the start    */
+        /* start of the zip64 end of            */
+        /* central directory                    */ + WORD
+        /* relative offset of the zip64         */
+        /* end of central directory record      */ + DWORD
+        /* total number of disks                */ + WORD;
+
+    /**
+     * Offset of the field that holds the location of the "Zip64 end
+     * of central directory record" inside the "Zip64 end of central
+     * directory locator" relative to the start of the "Zip64 end of
+     * central directory locator".
+     */
+    private static final int ZIP64_EOCDL_LOCATOR_OFFSET =
+        /* zip64 end of central dir locator sig */ WORD
+        /* number of the disk with the start    */
+        /* start of the zip64 end of            */
+        /* central directory                    */ + WORD;
+
+    /**
+     * Offset of the field that holds the location of the first
+     * central directory entry inside the "Zip64 end of central
+     * directory record" relative to the start of the "Zip64 end of
+     * central directory record".
+     */
+    private static final int ZIP64_EOCD_CFD_LOCATOR_OFFSET =
+        /* zip64 end of central dir        */
+        /* signature                       */ WORD
+        /* size of zip64 end of central    */
+        /* directory record                */ + DWORD
+        /* version made by                 */ + SHORT
+        /* version needed to extract       */ + SHORT
+        /* number of this disk             */ + WORD
+        /* number of the disk with the     */
+        /* start of the central directory  */ + WORD
+        /* total number of entries in the  */
+        /* central directory on this disk  */ + DWORD
+        /* total number of entries in the  */
+        /* central directory               */ + DWORD
+        /* size of the central directory   */ + DWORD;
+
+    /**
+     * Searches for either the &quot;Zip64 end of central directory
+     * locator&quot; or the &quot;End of central dir record&quot;, parses
      * it and positions the stream at the first central directory
      * record.
      */
     private void positionAtCentralDirectory()
         throws IOException {
+        boolean found = tryToLocateSignature(MIN_EOCD_SIZE + ZIP64_EOCDL_LENGTH,
+                                             MAX_EOCD_SIZE + ZIP64_EOCDL_LENGTH,
+                                             ZipOutputStream
+                                             .ZIP64_EOCD_LOC_SIG);
+        if (!found) {
+            // not a ZIP64 archive
+            positionAtCentralDirectory32();
+        } else {
+            positionAtCentralDirectory64();
+        }
+    }
+
+    /**
+     * Parses the &quot;Zip64 end of central directory locator&quot;,
+     * finds the &quot;Zip64 end of central directory record&quot; using the
+     * parsed information, parses that and positions the stream at the
+     * first central directory record.
+     */
+    private void positionAtCentralDirectory64()
+        throws IOException {
+        skipBytes(ZIP64_EOCDL_LOCATOR_OFFSET);
+        byte[] zip64EocdOffset = new byte[DWORD];
+        archive.readFully(zip64EocdOffset);
+        archive.seek(ZipEightByteInteger.getLongValue(zip64EocdOffset));
+        byte[] sig = new byte[WORD];
+        archive.readFully(sig);
+        if (sig[POS_0] != ZipOutputStream.ZIP64_EOCD_SIG[POS_0]
+            || sig[POS_1] != ZipOutputStream.ZIP64_EOCD_SIG[POS_1]
+            || sig[POS_2] != ZipOutputStream.ZIP64_EOCD_SIG[POS_2]
+            || sig[POS_3] != ZipOutputStream.ZIP64_EOCD_SIG[POS_3]
+            ) {
+            throw new ZipException("archive's ZIP64 end of central "
+                                   + "directory locator is corrupt.");
+        }
+        skipBytes(ZIP64_EOCD_CFD_LOCATOR_OFFSET
+                  - WORD /* signature has already been read */);
+        byte[] cfdOffset = new byte[DWORD];
+        archive.readFully(cfdOffset);
+        archive.seek(ZipEightByteInteger.getLongValue(cfdOffset));
+    }
+
+    /**
+     * Searches for the &quot;End of central dir record&quot;, parses
+     * it and positions the stream at the first central directory
+     * record.
+     */
+    private void positionAtCentralDirectory32()
+        throws IOException {
+        boolean found = tryToLocateSignature(MIN_EOCD_SIZE, MAX_EOCD_SIZE,
+                                             ZipOutputStream.EOCD_SIG);
+        if (!found) {
+            throw new ZipException("archive is not a ZIP archive");
+        }
+        skipBytes(CFD_LOCATOR_OFFSET);
+        byte[] cfdOffset = new byte[WORD];
+        archive.readFully(cfdOffset);
+        archive.seek(ZipLong.getValue(cfdOffset));
+    }
+
+    /**
+     * Searches the archive backwards from minDistance to maxDistance
+     * for the given signature, positions the RandomaccessFile right
+     * at the signature if it has been found.
+     */
+    private boolean tryToLocateSignature(long minDistanceFromEnd,
+                                         long maxDistanceFromEnd,
+                                         byte[] sig) throws IOException {
         boolean found = false;
-        long off = archive.length() - MIN_EOCD_SIZE;
+        long off = archive.length() - minDistanceFromEnd;
         final long stopSearching =
-            Math.max(0L, archive.length() - MAX_EOCD_SIZE);
+            Math.max(0L, archive.length() - maxDistanceFromEnd);
         if (off >= 0) {
-            final byte[] sig = ZipOutputStream.EOCD_SIG;
             for (; off >= stopSearching; off--) {
                 archive.seek(off);
                 int curr = archive.read();
                 if (curr == -1) {
                     break;
                 }
                 if (curr == sig[POS_0]) {
                     curr = archive.read();
                     if (curr == sig[POS_1]) {
                         curr = archive.read();
                         if (curr == sig[POS_2]) {
                             curr = archive.read();
                             if (curr == sig[POS_3]) {
                                 found = true;
                                 break;
                             }
                         }
                     }
                 }
             }
         }
-        if (!found) {
-            throw new ZipException("archive is not a ZIP archive");
+        if (found) {
+            archive.seek(off);
+        }
+        return found;
+    }
+
+    /**
+     * Skips the given number of bytes or throws an EOFException if
+     * skipping failed.
+     */ 
+    private void skipBytes(final int count) throws IOException {
+        int totalSkipped = 0;
+        while (totalSkipped < count) {
+            int skippedNow = archive.skipBytes(count - totalSkipped);
+            if (skippedNow <= 0) {
+                throw new EOFException();
+            }
+            totalSkipped += skippedNow;
         }
-        archive.seek(off + CFD_LOCATOR_OFFSET);
-        byte[] cfdOffset = new byte[WORD];
-        archive.readFully(cfdOffset);
-        archive.seek(ZipLong.getValue(cfdOffset));
     }
 
     /**
      * Number of bytes in local file header up to the &quot;length of
      * filename&quot; entry.
      */
     private static final long LFH_OFFSET_FOR_FILENAME_LENGTH =
         /* local file header signature     */ WORD
         /* version needed to extract       */ + SHORT
         /* general purpose bit flag        */ + SHORT
         /* compression method              */ + SHORT
         /* last mod file time              */ + SHORT
         /* last mod file date              */ + SHORT
         /* crc-32                          */ + WORD
         /* compressed size                 */ + WORD
         /* uncompressed size               */ + WORD;
 
     /**
      * Walks through all recorded entries and adds the data available
      * from the local file header.
      *
      * <p>Also records the offsets for the data to read from the
      * entries.</p>
      */
-    private void resolveLocalFileHeaderData(Map entriesWithoutUTF8Flag)
+    private void resolveLocalFileHeaderData(Map<ZipEntry, NameAndComment>
+                                            entriesWithoutUTF8Flag)
         throws IOException {
-        Enumeration e = Collections.enumeration(new HashSet(entries.keySet()));
-        while (e.hasMoreElements()) {
-            ZipEntry ze = (ZipEntry) e.nextElement();
-            OffsetEntry offsetEntry = (OffsetEntry) entries.get(ze);
+        // changing the name of a ZipEntry is going to change
+        // the hashcode - see COMPRESS-164
+        // Map needs to be reconstructed in order to keep central
+        // directory order
+        Map<ZipEntry, OffsetEntry> origMap =
+            new LinkedHashMap<ZipEntry, OffsetEntry>(entries);
+        entries.clear();
+        for (Map.Entry<ZipEntry, OffsetEntry> ent : origMap.entrySet()) {
+            ZipEntry ze = ent.getKey();
+            OffsetEntry offsetEntry = ent.getValue();
             long offset = offsetEntry.headerOffset;
             archive.seek(offset + LFH_OFFSET_FOR_FILENAME_LENGTH);
             byte[] b = new byte[SHORT];
             archive.readFully(b);
             int fileNameLen = ZipShort.getValue(b);
             archive.readFully(b);
             int extraFieldLen = ZipShort.getValue(b);
             int lenToSkip = fileNameLen;
             while (lenToSkip > 0) {
                 int skipped = archive.skipBytes(lenToSkip);
                 if (skipped <= 0) {
-                    throw new RuntimeException("failed to skip file name in"
-                                               + " local file header");
+                    throw new IOException("failed to skip file name in"
+                                          + " local file header");
                 }
                 lenToSkip -= skipped;
-            }            
+            }
             byte[] localExtraData = new byte[extraFieldLen];
             archive.readFully(localExtraData);
             ze.setExtra(localExtraData);
-            /*dataOffsets.put(ze,
-                            new Long(offset + LFH_OFFSET_FOR_FILENAME_LENGTH
-                                     + SHORT + SHORT + fileNameLen + extraFieldLen));
-            */
             offsetEntry.dataOffset = offset + LFH_OFFSET_FOR_FILENAME_LENGTH
                 + SHORT + SHORT + fileNameLen + extraFieldLen;
 
             if (entriesWithoutUTF8Flag.containsKey(ze)) {
-                // changing the name of a ZipEntry is going to change
-                // the hashcode
-                // - see https://issues.apache.org/jira/browse/COMPRESS-164
-                entries.remove(ze);
-                setNameAndCommentFromExtraFields(ze,
-                                                 (NameAndComment)
-                                                 entriesWithoutUTF8Flag.get(ze));
-                entries.put(ze, offsetEntry);
+                String orig = ze.getName();
+                NameAndComment nc = entriesWithoutUTF8Flag.get(ze);
+                ZipUtil.setNameAndCommentFromExtraFields(ze, nc.name,
+                                                         nc.comment);
+                if (!orig.equals(ze.getName())) {
+                    nameMap.remove(orig);
+                    nameMap.put(ze.getName(), ze);
+                }
             }
-        }
-    }
-
-    /**
-     * Convert a DOS date/time field to a Date object.
-     *
-     * @param zipDosTime contains the stored DOS time.
-     * @return a Date instance corresponding to the given time.
-     */
-    protected static Date fromDosTime(ZipLong zipDosTime) {
-        long dosTime = zipDosTime.getValue();
-        return new Date(dosToJavaTime(dosTime));
-    }
-
-    /*
-     * Converts DOS time to Java time (number of milliseconds since epoch).
-     */
-    private static long dosToJavaTime(long dosTime) {
-        Calendar cal = Calendar.getInstance();
-        // CheckStyle:MagicNumberCheck OFF - no point
-        cal.set(Calendar.YEAR, (int) ((dosTime >> 25) & 0x7f) + 1980);
-        cal.set(Calendar.MONTH, (int) ((dosTime >> 21) & 0x0f) - 1);
-        cal.set(Calendar.DATE, (int) (dosTime >> 16) & 0x1f);
-        cal.set(Calendar.HOUR_OF_DAY, (int) (dosTime >> 11) & 0x1f);
-        cal.set(Calendar.MINUTE, (int) (dosTime >> 5) & 0x3f);
-        cal.set(Calendar.SECOND, (int) (dosTime << 1) & 0x3e);
-        // CheckStyle:MagicNumberCheck ON
-        return cal.getTime().getTime();
-    }
-
-
-    /**
-     * Retrieve a String from the given bytes using the encoding set
-     * for this ZipFile.
-     *
-     * @param bytes the byte array to transform
-     * @return String obtained by using the given encoding
-     * @throws ZipException if the encoding cannot be recognized.
-     */
-    protected String getString(byte[] bytes) throws ZipException {
-        try {
-            return ZipEncodingHelper.getZipEncoding(encoding).decode(bytes);
-        } catch (IOException ex) {
-            throw new ZipException("Failed to decode name: " + ex.getMessage());
+            entries.put(ze, offsetEntry);
         }
     }
 
     /**
      * Checks whether the archive starts with a LFH.  If it doesn't,
      * it may be an empty archive.
      */
     private boolean startsWithLocalFileHeader() throws IOException {
         archive.seek(0);
         final byte[] start = new byte[WORD];
         archive.readFully(start);
         for (int i = 0; i < start.length; i++) {
             if (start[i] != ZipOutputStream.LFH_SIG[i]) {
                 return false;
             }
         }
         return true;
     }
 
     /**
-     * If the entry has Unicode*ExtraFields and the CRCs of the
-     * names/comments match those of the extra fields, transfer the
-     * known Unicode values from the extra field.
-     */
-    private void setNameAndCommentFromExtraFields(ZipEntry ze,
-                                                  NameAndComment nc) {
-        UnicodePathExtraField name = (UnicodePathExtraField)
-            ze.getExtraField(UnicodePathExtraField.UPATH_ID);
-        String originalName = ze.getName();
-        String newName = getUnicodeStringIfOriginalMatches(name, nc.name);
-        if (newName != null && !originalName.equals(newName)) {
-            ze.setName(newName);
-            nameMap.remove(originalName);
-            nameMap.put(newName, ze);
-        }
-
-        if (nc.comment != null && nc.comment.length > 0) {
-            UnicodeCommentExtraField cmt = (UnicodeCommentExtraField)
-                ze.getExtraField(UnicodeCommentExtraField.UCOM_ID);
-            String newComment =
-                getUnicodeStringIfOriginalMatches(cmt, nc.comment);
-            if (newComment != null) {
-                ze.setComment(newComment);
-            }
-        }
-    }
-
-    /**
-     * If the stored CRC matches the one of the given name, return the
-     * Unicode name of the given field.
-     *
-     * <p>If the field is null or the CRCs don't match, return null
-     * instead.</p>
-     */
-    private String getUnicodeStringIfOriginalMatches(AbstractUnicodeExtraField f,
-                                                     byte[] orig) {
-        if (f != null) {
-            CRC32 crc32 = new CRC32();
-            crc32.update(orig);
-            long origCRC32 = crc32.getValue();
-
-            if (origCRC32 == f.getNameCRC32()) {
-                try {
-                    return ZipEncodingHelper
-                        .UTF8_ZIP_ENCODING.decode(f.getUnicodeName());
-                } catch (IOException ex) {
-                    // UTF-8 unsupported?  should be impossible the
-                    // Unicode*ExtraField must contain some bad bytes
-
-                    // TODO log this anywhere?
-                    return null;
-                }
-            }
-        }
-        return null;
-    }
-
-    /**
      * InputStream that delegates requests to the underlying
      * RandomAccessFile, making sure that only bytes from a certain
      * range can be read.
      */
     private class BoundedInputStream extends InputStream {
         private long remaining;
         private long loc;
         private boolean addDummyByte = false;
 
         BoundedInputStream(long start, long remaining) {
             this.remaining = remaining;
             loc = start;
         }
 
+        @Override
         public int read() throws IOException {
             if (remaining-- <= 0) {
                 if (addDummyByte) {
                     addDummyByte = false;
                     return 0;
                 }
                 return -1;
             }
             synchronized (archive) {
                 archive.seek(loc++);
                 return archive.read();
             }
         }
 
+        @Override
         public int read(byte[] b, int off, int len) throws IOException {
             if (remaining <= 0) {
                 if (addDummyByte) {
                     addDummyByte = false;
                     b[off] = 0;
                     return 1;
                 }
                 return -1;
             }
 
             if (len <= 0) {
                 return 0;
             }
 
             if (len > remaining) {
                 len = (int) remaining;
             }
             int ret = -1;
             synchronized (archive) {
                 archive.seek(loc);
                 ret = archive.read(b, off, len);
             }
             if (ret > 0) {
                 loc += ret;
                 remaining -= ret;
             }
             return ret;
         }
 
         /**
          * Inflater needs an extra dummy byte for nowrap - see
          * Inflater's javadocs.
          */
         void addDummy() {
             addDummyByte = true;
         }
     }
 
     private static final class NameAndComment {
         private final byte[] name;
         private final byte[] comment;
         private NameAndComment(byte[] name, byte[] comment) {
             this.name = name;
             this.comment = comment;
         }
     }
+
+    /**
+     * Compares two ZipEntries based on their offset within the archive.
+     *
+     * <p>Won't return any meaningful results if one of the entries
+     * isn't part of the archive at all.</p>
+     *
+     * @since Ant 1.9.0
+     */
+    private final Comparator<ZipEntry> OFFSET_COMPARATOR =
+        new Comparator<ZipEntry>() {
+        public int compare(ZipEntry e1, ZipEntry e2) {
+            if (e1 == e2) {
+                return 0;
+            }
+
+            OffsetEntry off1 = entries.get(e1);
+            OffsetEntry off2 = entries.get(e2);
+            if (off1 == null) {
+                return 1;
+            }
+            if (off2 == null) {
+                return -1;
+            }
+            long val = (off1.headerOffset - off2.headerOffset);
+            return val == 0 ? 0 : val < 0 ? -1 : +1;
+        }
+    };
 }
