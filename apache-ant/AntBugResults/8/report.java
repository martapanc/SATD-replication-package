File path: src/main/org/apache/tools/zip/ZipFile.java
Comment: FIXME this is actually not very cpu cycles friendly as we are converting from
Initial commit id: a05d1f12
Final commit id: dee95e3a
   Bugs between [      11]:
c4c9d2552 clean up Inflater instance as some JDKs won't do it for us.  PR 42696.  Submitted by Mounir
6cd0de102 Parse central directory part of ZIP extra fields.  PR 46637
8264511a2 use nio for decoding of names - merge from commons-compress rev 746933
3cb22aa1c Support more modern encoding flag where archives signal filenames as UTF-8.  Based on submissions by Wolfgang Glas to commons-compress and TAMURA Kent to Ant.  PR 45548.
817fd3199 fix a bunch of findbugs reported problems in the zip, tar and bzip2 classes.  PR 46661
601cdf67b Make sure ZIP archive is closed even when a RuntimeExpection occurs.  PR 46559
9b4b922d2 fail early if ZipFile is applied to a non-ZIP archive.  PR 45463.  Suggested by Alison Winters.
7d174c342 Made up my mind on the fix for PR 35000.  Empty != broken, so make it two separate use cases.
80e8b2977 an archive with an empty central directory is broken.  PR 35000.  Submitted by Thomas Aglassinger.
32f2e37a9 JDK 1.2 is EOL and documentation is no more available. Point to JDK 5 API PR: 37203
b977b55da <unzip> and <untar> could leave streams open.  PR 34893
   Bugs after [       1]:
362376d62 Merge handling of duplicate entries from Commons Compress' ZipFile - needed to fix PR 54967

Start block index: 248
End block index: 327
    private void populateFromCentralDirectory()
        throws IOException {
        positionAtCentralDirectory();

        byte[] cfh = new byte[CFH_LEN];

        byte[] signatureBytes = new byte[4];
        archive.readFully(signatureBytes);
        long sig = ZipLong.getValue(signatureBytes);
        final long cfh_sig = ZipLong.getValue(ZipOutputStream.CFH_SIG);
        while (sig == cfh_sig) {
            archive.readFully(cfh);
            int off = 0;
            ZipEntry ze = new ZipEntry();

            int versionMadeBy = ZipShort.getValue(cfh, off);
            off += 2;
            ze.setPlatform((versionMadeBy >> 8) & 0x0F);

            off += 4; // skip version info and general purpose byte

            ze.setMethod(ZipShort.getValue(cfh, off));
            off += 2;

            // FIXME this is actually not very cpu cycles friendly as we are converting from
            // dos to java while the underlying Sun implementation will convert
            // from java to dos time for internal storage...
            long time = dosToJavaTime(ZipLong.getValue(cfh, off));
            ze.setTime(time);
            off += 4;

            ze.setCrc(ZipLong.getValue(cfh, off));
            off += 4;

            ze.setCompressedSize(ZipLong.getValue(cfh, off));
            off += 4;

            ze.setSize(ZipLong.getValue(cfh, off));
            off += 4;

            int fileNameLen = ZipShort.getValue(cfh, off);
            off += 2;

            int extraLen = ZipShort.getValue(cfh, off);
            off += 2;

            int commentLen = ZipShort.getValue(cfh, off);
            off += 2;

            off += 2; // disk number

            ze.setInternalAttributes(ZipShort.getValue(cfh, off));
            off += 2;

            ze.setExternalAttributes(ZipLong.getValue(cfh, off));
            off += 4;

            byte[] fileName = new byte[fileNameLen];
            archive.readFully(fileName);
            ze.setName(getString(fileName));


            // LFH offset,
            OffsetEntry offset = new OffsetEntry();
            offset.headerOffset = ZipLong.getValue(cfh, off);
            // data offset will be filled later
            entries.put(ze, offset);

            nameMap.put(ze.getName(), ze);

            archive.skipBytes(extraLen);

            byte[] comment = new byte[commentLen];
            archive.readFully(comment);
            ze.setComment(getString(comment));

            archive.readFully(signatureBytes);
            sig = ZipLong.getValue(signatureBytes);
        }
    }
