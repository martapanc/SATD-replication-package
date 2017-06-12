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
