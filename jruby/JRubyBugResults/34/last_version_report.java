    private static int parseSignalString(Ruby runtime, String value) {
        int startIndex = 0;
        boolean negative = value.startsWith("-");
        
        if (value.startsWith("-")) startIndex++;
        String signalName = value.startsWith("SIG", startIndex)
                ? value 
                : "SIG" + value.substring(startIndex);
        
        try {
            int signalValue = Signal.valueOf(signalName).value();
            return negative ? -signalValue : signalValue;

        } catch (IllegalArgumentException ex) {
            throw runtime.newArgumentError("unsupported name `SIG" + signalName + "'");
        }
        
    }
