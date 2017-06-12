    public static DateTimeZone getTimeZone(Ruby runtime, String zone) {
        DateTimeZone cachedZone = runtime.getTimezoneCache().get(zone);

        if (cachedZone != null) return cachedZone;

        String originalZone = zone;
        TimeZone tz = TimeZone.getTimeZone(getEnvTimeZone(runtime).toString());

        // Value of "TZ" property is of a bit different format,
        // which confuses the Java's TimeZone.getTimeZone(id) method,
        // and so, we need to convert it.

        Matcher tzMatcher = TZ_PATTERN.matcher(zone);
        if (tzMatcher.matches()) {                    
            String sign = tzMatcher.group(2);
            String hours = tzMatcher.group(3);
            String minutes = tzMatcher.group(4);
                
            // GMT+00:00 --> Etc/GMT, see "MRI behavior"
            // comment below.
            if (("00".equals(hours) || "0".equals(hours)) &&
                    (minutes == null || ":00".equals(minutes) || ":0".equals(minutes))) {
                zone = "Etc/GMT";
            } else {
                // Invert the sign, since TZ format and Java format
                // use opposite signs, sigh... Also, Java API requires
                // the sign to be always present, be it "+" or "-".
                sign = ("-".equals(sign)? "+" : "-");

                // Always use "GMT" since that's required by Java API.
                zone = "GMT" + sign + hours;

                if (minutes != null) {
                    zone += minutes;
                }
            }
            
            tz = TimeZone.getTimeZone(zone);
        } else {
            if (LONG_TZNAME.containsKey(zone)) tz.setID(LONG_TZNAME.get(zone.toUpperCase()));
        }

        // MRI behavior: With TZ equal to "GMT" or "UTC", Time.now
        // is *NOT* considered as a proper GMT/UTC time:
        //   ENV['TZ']="GMT"
        //   Time.now.gmt? ==> false
        //   ENV['TZ']="UTC"
        //   Time.now.utc? ==> false
        // Hence, we need to adjust for that.
        if ("GMT".equalsIgnoreCase(zone) || "UTC".equalsIgnoreCase(zone)) {
            zone = "Etc/" + zone;
            tz = TimeZone.getTimeZone(zone);
        }

        DateTimeZone dtz = DateTimeZone.forTimeZone(tz);
        runtime.getTimezoneCache().put(originalZone, dtz);
        return dtz;
    }
