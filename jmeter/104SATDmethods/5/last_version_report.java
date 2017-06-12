    public void setValue(Object value) {
        if (!(value instanceof Double)) {
            setText("#N/A");
            return;
        }
        double rate = ((Double) value).doubleValue();
        if (Double.compare(rate,Double.MAX_VALUE)==0){
            setText("#N/A");
            return;
        }

        String unit = "sec";

        if (rate < 1.0) {
            rate *= 60.0;
            unit = "min";
        }
        if (rate < 1.0) {
            rate *= 60.0;
            unit = "hour";
        }
        setText(formatter.format(rate) + "/" + unit);
    }
