File path: src/core/org/apache/jmeter/save/SaveGraphicsService.java
Comment: Yuck: TIFFImageEncoder uses Error to report runtime problems
Initial commit id: b6069f5b
Final commit id: ff88dfcd
   Bugs between [       1]:
5d6aec5db Bug 57193: Add param and return tags to javadoc Bugzilla Id: 57193
   Bugs after [       0]:


Start block index: 151
End block index: 172
    public void saveTIFFWithBatik(String filename, BufferedImage image) {
        File outfile = new File(filename);
        OutputStream fos = createFile(outfile);
        if (fos == null) {
            return;
        }
        TIFFEncodeParam param = new TIFFEncodeParam();
        TIFFImageEncoder encoder = new TIFFImageEncoder(fos, param);
        try {
            encoder.encode(image);
        } catch (IOException e) {
            JMeterUtils.reportErrorToUser("TIFFImageEncoder reported: "+e.getMessage(), "Problem creating image file");
        // Yuck: TIFFImageEncoder uses Error to report runtime problems
        } catch (Error e) {
            JMeterUtils.reportErrorToUser("TIFFImageEncoder reported: "+e.getMessage(), "Problem creating image file");
            if (e.getClass() != Error.class){// rethrow other errors
                throw e;
            }
        } finally {
            JOrphanUtils.closeQuietly(fos);
        }
    }

*********************** Method when SATD was removed **************************

    public void saveTIFFWithBatik(String filename, BufferedImage image) {
        File outfile = new File(filename);
        OutputStream fos = createFile(outfile);
        if (fos == null) {
            return;
        }
        TIFFEncodeParam param = new TIFFEncodeParam();
        TIFFImageEncoder encoder = new TIFFImageEncoder(fos, param);
        try {
            encoder.encode(image);
        } catch (IOException e) {
            JMeterUtils.reportErrorToUser("TIFFImageEncoder reported: "+e.getMessage(), "Problem creating image file");
        } catch (Error e) { // NOSONAR TIFFImageEncoder uses Error to report runtime problems
            JMeterUtils.reportErrorToUser("TIFFImageEncoder reported: "+e.getMessage(), "Problem creating image file");
            if (e.getClass() != Error.class){// NOSONAR rethrow other errors
                throw e;
            }
        } finally {
            JOrphanUtils.closeQuietly(fos);
        }
    }
