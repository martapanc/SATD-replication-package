    public synchronized ByteList read(int number) throws IOException, BadDescriptorException {
        checkReadable();
        ensureReadNonBuffered();
        
        ByteList byteList = new ByteList(number);
        
        // TODO this should entry into error handling somewhere
        int bytesRead = descriptor.read(number, byteList);
        
        return byteList;
    }
