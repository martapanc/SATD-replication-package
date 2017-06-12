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
