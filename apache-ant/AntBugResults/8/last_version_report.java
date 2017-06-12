private Map<ZipEntry, NameAndComment> populateFromCentralDirectory()
    throws IOException {
    HashMap<ZipEntry, NameAndComment> noUTF8Flag =
        new HashMap<ZipEntry, NameAndComment>();

    positionAtCentralDirectory();

    byte[] signatureBytes = new byte[WORD];
    archive.readFully(signatureBytes);
    long sig = ZipLong.getValue(signatureBytes);

    if (sig != CFH_SIG && startsWithLocalFileHeader()) {
        throw new IOException("central directory is empty, can't expand"
                              + " corrupt archive.");
    }

    while (sig == CFH_SIG) {
        readCentralDirectoryEntry(noUTF8Flag);
        archive.readFully(signatureBytes);
        sig = ZipLong.getValue(signatureBytes);
    }
    return noUTF8Flag;
}
