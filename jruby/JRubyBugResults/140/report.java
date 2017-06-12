File path: src/org/jruby/RubyTime.java
Comment: hen MET choose CET timezone to work around Joda
Initial commit id: 55f03b63
Final commit id: 0a93732b
   Bugs between [      12]:
0a93732b75 Fix JRUBY-3560: Time.local ignores provided time zone
dcf1146839 Fix for JRUBY-5008: [19] Time.new can accept parameters
06778cfaf3 Fix for JRUBY-5056: Time marshal format differs between Ruby and JRuby
fe79900910 Fix JRUBY-4842: Time#utc_offset is incorrect in small time range. Specs are in RubySpec a186c01.
6e0d3d549c fixes JRUBY-4166: [1.9] most of the specs for Time#inspect, Time#+ and Time#- passing
a3a0d23083 Fix for JRUBY-4784: Lost implicit conversion from ruby Time to java.util.Date
1b919d492b Fixes (and more) for JRUBY-4737: Compatibility issue with Spring property from 1.4.0 to 1.5.0.RC1
011aa5b87c fixes JRUBY-3907: Time.strftime allows tokens deriving from GNU C library
025450fd95 fixes JRUBY-3914: In 1.9 mode, Time.now == nil should return false, not nil
3aadd8a941 Fix by Colin Jones for JRUBY-3498: New failure in MRI's test_time
d5e194d55a Fix by Aurelian Oancea for JRUBY-3671: RubySpec: Time.times is an obsolete method
98d7de504c Fix fir JRUBY-2788: Make Time.now monotonically increasing
   Bugs after [      14]:
5ac6e9479c Time#localtime was missing the : when passing HH:MM to getTimeZone. Fixes #747
b67c1a6142 Fix #565.
43d635d38a Fix #591.
1c2e4ce9c9 Fix #591.
57cda3a7d7 Fix JRUBY-6811: Time.at rounding errors below milliseconds
d72c9f4f88 Fix JRUBY-6952
969bf6f5b7 Implement Time#round. This fixes JRUBY-6813.
0ac40a96db Fix JRUBY-6809: Time::utc sub-millisecond inaccuracy, causes incorrect date in Rails
75c5448980 Attempt to fix JRUBY-6650.  This now seems to produce/load the same output as MRI
ac0aff594c Fix JRUBY-6631: Time#nsec always returns 0
8749c50e51 Fix JRUBY-6386: time.localtime not taking any arguments
abeb961582 Fix JRUBY-5315: Time constructors don't support fractional seconds
ac1cd2ff34 Revert "Fix JRUBY-5315: Time constructors don't support fractional seconds"
e603a5ed65 Fix JRUBY-5315: Time constructors don't support fractional seconds

Start block index: 99
End block index: 155
    public static DateTimeZone getTimeZone(Ruby runtime, String zone) {
        DateTimeZone cachedZone = runtime.getTimezoneCache().get(zone);

        if (cachedZone != null) return cachedZone;

        String originalZone = zone;

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
            if (("00".equals(hours) || "0".equals(hours))
                    && (minutes == null || ":00".equals(minutes) || ":0".equals(minutes))) {
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
        }

        // For JRUBY-2759, when MET choose CET timezone to work around Joda
        if ("MET".equalsIgnoreCase(zone)) {
            zone = "CET";
        }

        DateTimeZone dtz = DateTimeZone.forTimeZone(TimeZone.getTimeZone(zone));
        runtime.getTimezoneCache().put(originalZone, dtz);
        return dtz;
    }
