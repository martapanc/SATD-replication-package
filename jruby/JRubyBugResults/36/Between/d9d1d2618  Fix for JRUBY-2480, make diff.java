diff --git a/src/org/jruby/RubyTime.java b/src/org/jruby/RubyTime.java
index ea3a69227b..09140ef2ba 100644
--- a/src/org/jruby/RubyTime.java
+++ b/src/org/jruby/RubyTime.java
@@ -1,811 +1,813 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2001 Chad Fowler <chadfowler@chadfowler.com>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004 Joey Gibson <joey@joeygibson.com>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2006 Ola Bini <ola.bini@ki.se>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import java.lang.ref.SoftReference;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.Locale;
 import java.util.Map;
 import java.util.TimeZone;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyClass;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.RubyDateFormat;
 import org.jruby.util.ByteList;
 
 import org.joda.time.DateTime;
 import org.joda.time.DateTimeZone;
 import org.joda.time.format.DateTimeFormat;
 import org.joda.time.format.DateTimeFormatter;
+import org.jruby.runtime.ClassIndex;
 
 /** The Time class.
  * 
  * @author chadfowler, jpetersen
  */
 @JRubyClass(name="Time", include="Comparable")
 public class RubyTime extends RubyObject {
     public static final String UTC = "UTC";
     private DateTime dt;
     private long usec;
     
     private final static DateTimeFormatter ONE_DAY_CTIME_FORMATTER = DateTimeFormat.forPattern("EEE MMM  d HH:mm:ss yyyy").withLocale(Locale.ENGLISH);
     private final static DateTimeFormatter TWO_DAY_CTIME_FORMATTER = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss yyyy").withLocale(Locale.ENGLISH);
 
     private final static DateTimeFormatter TO_S_FORMATTER = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss Z yyyy").withLocale(Locale.ENGLISH);
     private final static DateTimeFormatter TO_S_UTC_FORMATTER = DateTimeFormat.forPattern("EEE MMM dd HH:mm:ss 'UTC' yyyy").withLocale(Locale.ENGLISH);
 
     // There are two different popular TZ formats: legacy (AST+3:00:00, GMT-3), and
     // newer one (US/Pacific, America/Los_Angeles). This pattern is to detect
     // the legacy TZ format in order to convert it to the newer format
     // understood by Java API.
     private static final Pattern TZ_PATTERN
             = Pattern.compile("(\\D+?)([\\+-]?)(\\d+)(:\\d+)?(:\\d+)?");
     
     private static final ByteList TZ_STRING = ByteList.create("TZ");
     private static SoftReference<Map<String, DateTimeZone>> LOCAL_ZONES_CACHE
             = new SoftReference<Map<String,DateTimeZone>>(new HashMap<String,DateTimeZone>());
      
     public static DateTimeZone getLocalTimeZone(Ruby runtime) {
         RubyString tzVar = runtime.newString(TZ_STRING);
         RubyHash h = ((RubyHash)runtime.getObject().fastGetConstant("ENV"));
         IRubyObject tz = h.op_aref(runtime.getCurrentContext(), tzVar);
         if (tz == null || ! (tz instanceof RubyString)) {
             return DateTimeZone.getDefault();
         } else {
             String zone = tz.toString();
             Map<String, DateTimeZone> zonesCache = LOCAL_ZONES_CACHE.get();
             if (zonesCache != null) {
                 DateTimeZone cachedZone = zonesCache.get(zone);
                 if (cachedZone != null) {
                     return cachedZone;
                 }
             } else {
                 zonesCache = new HashMap<String, DateTimeZone>();
                 LOCAL_ZONES_CACHE = new SoftReference<Map<String,DateTimeZone>>(zonesCache);
             }
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
 
             DateTimeZone dtz = DateTimeZone.forTimeZone(TimeZone.getTimeZone(zone));
             zonesCache.put(originalZone, dtz);
             return dtz;
         }
     }
     
     public RubyTime(Ruby runtime, RubyClass rubyClass) {
         super(runtime, rubyClass);
     }
     
     public RubyTime(Ruby runtime, RubyClass rubyClass, DateTime dt) {
         super(runtime, rubyClass);
         this.dt = dt;
     }
     
     // We assume that these two time instances
     // occurred at the same time.
     private static final long BASE_TIME_MILLIS = System.currentTimeMillis();
     private static final long BASE_TIME_NANOS = System.nanoTime();
     
     private static ObjectAllocator TIME_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             long nanosPassed = System.nanoTime() - BASE_TIME_NANOS;
             long millisTime = BASE_TIME_MILLIS + nanosPassed / 1000000;
             long usecs = nanosPassed % 1000;
             DateTimeZone dtz = getLocalTimeZone(runtime);
             DateTime dt = new DateTime(millisTime, dtz);
             RubyTime rt =  new RubyTime(runtime, klass, dt);
             rt.setUSec(usecs);
             return rt;
         }
     };
     
     public static RubyClass createTimeClass(Ruby runtime) {
         RubyClass timeClass = runtime.defineClass("Time", runtime.getObject(), TIME_ALLOCATOR);
+        timeClass.index = ClassIndex.TIME;
         runtime.setTime(timeClass);
         
         timeClass.includeModule(runtime.getComparable());
         
         timeClass.defineAnnotatedMethods(RubyTime.class);
         
         return timeClass;
     }
     
     public void setUSec(long usec) {
         this.usec = usec;
     }
     
     public long getUSec() {
         return usec;
     }
     
     public void updateCal(DateTime dt) {
         this.dt = dt;
     }
     
     protected long getTimeInMillis() {
         return dt.getMillis();  // For JDK 1.4 we can use "cal.getTimeInMillis()"
     }
     
     public static RubyTime newTime(Ruby runtime, long milliseconds) {
         return newTime(runtime, new DateTime(milliseconds));
     }
     
     public static RubyTime newTime(Ruby runtime, DateTime dt) {
         return new RubyTime(runtime, runtime.getTime(), dt);
     }
     
     public static RubyTime newTime(Ruby runtime, DateTime dt, long usec) {
         RubyTime t = new RubyTime(runtime, runtime.getTime(), dt);
         t.setUSec(usec);
         return t;
     }
 
     @JRubyMethod(name = "initialize_copy", required = 1)
     public IRubyObject initialize_copy(IRubyObject original) {
         if (!(original instanceof RubyTime)) {
             throw getRuntime().newTypeError("Expecting an instance of class Time");
         }
         
         RubyTime originalTime = (RubyTime) original;
         
         // We can just use dt, since it is immutable
         dt = originalTime.dt;
         usec = originalTime.usec;
         
         return this;
     }
 
     @JRubyMethod(name = "succ")
     public RubyTime succ() {
         return newTime(getRuntime(),dt.plusSeconds(1));
     }
 
     @JRubyMethod(name = {"gmtime", "utc"})
     public RubyTime gmtime() {
         dt = dt.withZone(DateTimeZone.UTC);
         return this;
     }
 
     @JRubyMethod(name = "localtime")
     public RubyTime localtime() {
         dt = dt.withZone(getLocalTimeZone(getRuntime()));
         return this;
     }
     
     @JRubyMethod(name = {"gmt?", "utc?", "gmtime?"})
     public RubyBoolean gmt() {
         return getRuntime().newBoolean(dt.getZone().getID().equals("UTC"));
     }
     
     @JRubyMethod(name = {"getgm", "getutc"})
     public RubyTime getgm() {
         return newTime(getRuntime(), dt.withZone(DateTimeZone.UTC), getUSec());
     }
 
     @JRubyMethod(name = "getlocal")
     public RubyTime getlocal() {
         return newTime(getRuntime(), dt.withZone(getLocalTimeZone(getRuntime())), getUSec());
     }
 
     @JRubyMethod(name = "strftime", required = 1)
     public RubyString strftime(IRubyObject format) {
         final RubyDateFormat rubyDateFormat = new RubyDateFormat("-", Locale.US);
         rubyDateFormat.applyPattern(format.toString());
         rubyDateFormat.setDateTime(dt);
         String result = rubyDateFormat.format(null);
         return getRuntime().newString(result);
     }
     
     @JRubyMethod(name = ">=", required = 1)
     public IRubyObject op_ge(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) >= 0);
         }
         
         return RubyComparable.op_ge(context, this, other);
     }
     
     @JRubyMethod(name = ">", required = 1)
     public IRubyObject op_gt(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) > 0);
         }
         
         return RubyComparable.op_gt(context, this, other);
     }
     
     @JRubyMethod(name = "<=", required = 1)
     public IRubyObject op_le(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) <= 0);
         }
         
         return RubyComparable.op_le(context, this, other);
     }
     
     @JRubyMethod(name = "<", required = 1)
     public IRubyObject op_lt(ThreadContext context, IRubyObject other) {
         if (other instanceof RubyTime) {
             return getRuntime().newBoolean(cmp((RubyTime) other) < 0);
         }
         
         return RubyComparable.op_lt(context, this, other);
     }
     
     private int cmp(RubyTime other) {
         long millis = getTimeInMillis();
 		long millis_other = other.getTimeInMillis();
         long usec_other = other.usec;
         
 		if (millis > millis_other || (millis == millis_other && usec > usec_other)) {
 		    return 1;
 		} else if (millis < millis_other || (millis == millis_other && usec < usec_other)) {
 		    return -1;
 		} 
 
         return 0;
     }
     
     @JRubyMethod(name = "+", required = 1)
     public IRubyObject op_plus(IRubyObject other) {
         long time = getTimeInMillis();
 
         if (other instanceof RubyTime) {
             throw getRuntime().newTypeError("time + time ?");
         }
         long adjustment = (long) (RubyNumeric.num2dbl(other) * 1000000);
         int micro = (int) (adjustment % 1000);
         adjustment = adjustment / 1000;
 
         time += adjustment;
 
         RubyTime newTime = new RubyTime(getRuntime(), getMetaClass());
         newTime.dt = new DateTime(time).withZone(dt.getZone());
         newTime.setUSec(micro);
 
         return newTime;
     }
 
     @JRubyMethod(name = "-", required = 1)
     public IRubyObject op_minus(IRubyObject other) {
         long time = getTimeInMillis();
 
         if (other instanceof RubyTime) {
             time -= ((RubyTime) other).getTimeInMillis();
             return RubyFloat.newFloat(getRuntime(), time * 10e-4);
         }
 
         long adjustment = (long) (RubyNumeric.num2dbl(other) * 1000000);
         int micro = (int) (adjustment % 1000);
         adjustment = adjustment / 1000;
 
         time -= adjustment;
 
         RubyTime newTime = new RubyTime(getRuntime(), getMetaClass());
         newTime.dt = new DateTime(time).withZone(dt.getZone());
         newTime.setUSec(micro);
 
         return newTime;
     }
 
     @JRubyMethod(name = "===", required = 1)
     public IRubyObject op_eqq(ThreadContext context, IRubyObject other) {
         return (RubyNumeric.fix2int(callMethod(context, MethodIndex.OP_SPACESHIP, "<=>", other)) == 0) ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     @JRubyMethod(name = "<=>", required = 1)
     public IRubyObject op_cmp(IRubyObject other) {
         if (other.isNil()) {
         	return other;
         }
         
         if (other instanceof RubyTime) {
             return getRuntime().newFixnum(cmp((RubyTime) other));
         }
 
         return getRuntime().getNil();
     }
     
     @JRubyMethod(name = "eql?", required = 1)
     public IRubyObject eql_p(IRubyObject other) {
         if (other instanceof RubyTime) {
             RubyTime otherTime = (RubyTime)other; 
             return (usec == otherTime.usec && getTimeInMillis() == otherTime.getTimeInMillis()) ? getRuntime().getTrue() : getRuntime().getFalse();
         }
         return getRuntime().getFalse();
     }
 
     @JRubyMethod(name = {"asctime", "ctime"})
     public RubyString asctime() {
         DateTimeFormatter simpleDateFormat;
 
         if (dt.getDayOfMonth() < 10) {
             simpleDateFormat = ONE_DAY_CTIME_FORMATTER;
         } else {
             simpleDateFormat = TWO_DAY_CTIME_FORMATTER;
         }
         String result = simpleDateFormat.print(dt);
         return getRuntime().newString(result);
     }
 
     @JRubyMethod(name = {"to_s", "inspect"})
     public IRubyObject to_s() {
         DateTimeFormatter simpleDateFormat;
         if (dt.getZone() == DateTimeZone.UTC) {
             simpleDateFormat = TO_S_UTC_FORMATTER;
         } else {
             simpleDateFormat = TO_S_FORMATTER;
         }
 
         String result = simpleDateFormat.print(dt);
 
         return getRuntime().newString(result);
     }
 
     @JRubyMethod(name = "to_a")
     public RubyArray to_a() {
         return getRuntime().newArrayNoCopy(new IRubyObject[] { sec(), min(), hour(), mday(), month(), 
                 year(), wday(), yday(), isdst(), zone() });
     }
 
     @JRubyMethod(name = "to_f")
     public RubyFloat to_f() {
         long time = getTimeInMillis();
         time = time * 1000 + usec;
         return RubyFloat.newFloat(getRuntime(), time / 1000000.0);
     }
 
     @JRubyMethod(name = {"to_i", "tv_sec"})
     public RubyInteger to_i() {
         return getRuntime().newFixnum(getTimeInMillis() / 1000);
     }
 
     @JRubyMethod(name = {"usec", "tv_usec"})
     public RubyInteger usec() {
         return getRuntime().newFixnum(dt.getMillisOfSecond() * 1000 + getUSec());
     }
 
     public void setMicroseconds(long mic) {
         long millis = getTimeInMillis() % 1000;
         long withoutMillis = getTimeInMillis() - millis;
         withoutMillis += (mic / 1000);
         dt = dt.withMillis(withoutMillis);
         usec = mic % 1000;
     }
     
     public long microseconds() {
     	return getTimeInMillis() % 1000 * 1000 + usec;
     }
 
     @JRubyMethod(name = "sec")
     public RubyInteger sec() {
         return getRuntime().newFixnum(dt.getSecondOfMinute());
     }
 
     @JRubyMethod(name = "min")
     public RubyInteger min() {
         return getRuntime().newFixnum(dt.getMinuteOfHour());
     }
 
     @JRubyMethod(name = "hour")
     public RubyInteger hour() {
         return getRuntime().newFixnum(dt.getHourOfDay());
     }
 
     @JRubyMethod(name = {"mday", "day"})
     public RubyInteger mday() {
         return getRuntime().newFixnum(dt.getDayOfMonth());
     }
 
     @JRubyMethod(name = {"month", "mon"})
     public RubyInteger month() {
         return getRuntime().newFixnum(dt.getMonthOfYear());
     }
 
     @JRubyMethod(name = "year")
     public RubyInteger year() {
         return getRuntime().newFixnum(dt.getYear());
     }
 
     @JRubyMethod(name = "wday")
     public RubyInteger wday() {
         return getRuntime().newFixnum((dt.getDayOfWeek()%7));
     }
 
     @JRubyMethod(name = "yday")
     public RubyInteger yday() {
         return getRuntime().newFixnum(dt.getDayOfYear());
     }
 
     @JRubyMethod(name = {"gmt_offset", "gmtoff", "utc_offset"})
     public RubyInteger gmt_offset() {
         int offset = dt.getZone().getOffsetFromLocal(dt.getMillis());
         
         return getRuntime().newFixnum((int)(offset/1000));
     }
 
     @JRubyMethod(name = {"isdst", "dst?"})
     public RubyBoolean isdst() {
         return getRuntime().newBoolean(!dt.getZone().isStandardOffset(dt.getMillis()));
     }
 
     @JRubyMethod(name = "zone")
     public RubyString zone() {
         String zone = dt.getZone().getShortName(dt.getMillis());
         if(zone.equals("+00:00")) {
             zone = "GMT";
         }
         return getRuntime().newString(zone);
     }
 
     public void setDateTime(DateTime dt) {
         this.dt = dt;
     }
 
     public DateTime getDateTime() {
         return this.dt;
     }
 
     public Date getJavaDate() {
         return this.dt.toDate();
     }
 
     @JRubyMethod(name = "hash")
     public RubyFixnum hash() {
     	// modified to match how hash is calculated in 1.8.2
         return getRuntime().newFixnum((int)(((dt.getMillis() / 1000) ^ microseconds()) << 1) >> 1);
     }    
 
     @JRubyMethod(name = "_dump", optional = 1, frame = true)
     public RubyString dump(IRubyObject[] args, Block unusedBlock) {
         RubyString str = (RubyString) mdump(new IRubyObject[] { this });
         str.syncVariables(this.getVariableList());
         return str;
     }    
 
     public RubyObject mdump(final IRubyObject[] args) {
         RubyTime obj = (RubyTime)args[0];
         DateTime dt = obj.dt.withZone(DateTimeZone.UTC);
         byte dumpValue[] = new byte[8];
         int pe = 
             0x1                                 << 31 |
             (dt.getYear()-1900)                 << 14 |
             (dt.getMonthOfYear()-1)             << 10 |
             dt.getDayOfMonth()                  << 5  |
             dt.getHourOfDay();
         int se =
             dt.getMinuteOfHour()                << 26 |
             dt.getSecondOfMinute()              << 20 |
             (dt.getMillisOfSecond() * 1000 + (int)usec); // dump usec, not msec
 
         for(int i = 0; i < 4; i++) {
             dumpValue[i] = (byte)(pe & 0xFF);
             pe >>>= 8;
         }
         for(int i = 4; i < 8 ;i++) {
             dumpValue[i] = (byte)(se & 0xFF);
             se >>>= 8;
         }
         return RubyString.newString(obj.getRuntime(), new ByteList(dumpValue,false));
     }
 
     @JRubyMethod(name = "initialize", frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(Block block) {
         return this;
     }
     
     /* Time class methods */
     
     public static IRubyObject s_new(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         RubyTime time = new RubyTime(runtime, (RubyClass) recv, new DateTime(getLocalTimeZone(runtime)));
         time.callInit(args,block);
         return time;
     }
 
     @JRubyMethod(name = {"now"}, rest = true, frame = true, meta = true)
     public static IRubyObject newInstance(ThreadContext context, IRubyObject recv, IRubyObject[] args, Block block) {
         IRubyObject obj = ((RubyClass)recv).allocate();
         obj.callMethod(context, "initialize", args, block);
         return obj;
     }
     
     @JRubyMethod(name = "at", required = 1, optional = 1, meta = true)
     public static IRubyObject at(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
 
         RubyTime time = new RubyTime(runtime, (RubyClass) recv, new DateTime(getLocalTimeZone(runtime)));
 
         if (args[0] instanceof RubyTime) {
             RubyTime other = (RubyTime) args[0];
             time.dt = other.dt;
             time.setUSec(other.getUSec());
         } else {
             long seconds = RubyNumeric.num2long(args[0]);
             long millisecs = 0;
             long microsecs = 0;
             if (args.length > 1) {
                 long tmp = RubyNumeric.num2long(args[1]);
                 millisecs = tmp / 1000;
                 microsecs = tmp % 1000;
             }
             else {
                 // In the case of two arguments, MRI will discard the portion of
                 // the first argument after a decimal point (i.e., "floor").
                 // However in the case of a single argument, any portion after
                 // the decimal point is honored.
                 if (args[0] instanceof RubyFloat) {
                     double dbl = ((RubyFloat) args[0]).getDoubleValue();
                     long micro = (long) ((dbl - seconds) * 1000000);
                     millisecs = micro / 1000;
                     microsecs = micro % 1000;
                 }
             }
             time.setUSec(microsecs);
             time.dt = time.dt.withMillis(seconds * 1000 + millisecs);
         }
 
         time.callInit(IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
 
         return time;
     }
 
     @JRubyMethod(name = {"local", "mktime"}, required = 1, optional = 9, meta = true)
     public static RubyTime new_local(IRubyObject recv, IRubyObject[] args) {
         return createTime(recv, args, false);
     }
 
     @JRubyMethod(name = {"utc", "gm"}, required = 1, optional = 9, meta = true)
     public static RubyTime new_utc(IRubyObject recv, IRubyObject[] args) {
         return createTime(recv, args, true);
     }
 
     @JRubyMethod(name = "_load", required = 1, frame = true, meta = true)
     public static RubyTime load(IRubyObject recv, IRubyObject from, Block block) {
         return s_mload(recv, (RubyTime)(((RubyClass)recv).allocate()), from);
     }
 
     protected static RubyTime s_mload(IRubyObject recv, RubyTime time, IRubyObject from) {
         Ruby runtime = recv.getRuntime();
 
         DateTime dt = new DateTime(DateTimeZone.UTC);
 
         byte[] fromAsBytes = null;
         fromAsBytes = from.convertToString().getBytes();
         if(fromAsBytes.length != 8) {
             throw runtime.newTypeError("marshaled time format differ");
         }
         int p=0;
         int s=0;
         for (int i = 0; i < 4; i++) {
             p |= ((int)fromAsBytes[i] & 0xFF) << (8 * i);
         }
         for (int i = 4; i < 8; i++) {
             s |= ((int)fromAsBytes[i] & 0xFF) << (8 * (i - 4));
         }
         if ((p & (1<<31)) == 0) {
             dt = dt.withMillis(p * 1000L + s);
         } else {
             p &= ~(1<<31);
             dt = dt.withYear(((p >>> 14) & 0xFFFF) + 1900);
             dt = dt.withMonthOfYear(((p >>> 10) & 0xF) + 1);
             dt = dt.withDayOfMonth(((p >>> 5)  & 0x1F));
             dt = dt.withHourOfDay((p & 0x1F));
             dt = dt.withMinuteOfHour(((s >>> 26) & 0x3F));
             dt = dt.withSecondOfMinute(((s >>> 20) & 0x3F));
             // marsaling dumps usec, not msec
             dt = dt.withMillisOfSecond((s & 0xFFFFF) / 1000);
             dt = dt.withZone(getLocalTimeZone(runtime));
             time.setUSec((s & 0xFFFFF) % 1000);
         }
         time.setDateTime(dt);
         return time;
     }
     
     private static final String[] months = {"jan", "feb", "mar", "apr", "may", "jun",
                                             "jul", "aug", "sep", "oct", "nov", "dec"};
     private static final int[] time_min = {1, 0, 0, 0, Integer.MIN_VALUE};
     private static final int[] time_max = {31, 23, 59, 60, Integer.MAX_VALUE};
 
     private static final int ARG_SIZE = 7;
     private static RubyTime createTime(IRubyObject recv, IRubyObject[] args, boolean gmt) {
         Ruby runtime = recv.getRuntime();
         int len = ARG_SIZE;
         
         if (args.length == 10) {
             args = new IRubyObject[] { args[5], args[4], args[3], args[2], args[1], args[0], runtime.getNil() };
         } else {
             // MRI accepts additional wday argument which appears to be ignored.
             len = args.length;
             
             if (len < ARG_SIZE) {
                 IRubyObject[] newArgs = new IRubyObject[ARG_SIZE];
                 System.arraycopy(args, 0, newArgs, 0, args.length);
                 for (int i = len; i < ARG_SIZE; i++) {
                     newArgs[i] = runtime.getNil();
                 }
                 args = newArgs;
                 len = ARG_SIZE;
             }
         }
         ThreadContext context = runtime.getCurrentContext();
         
         if(args[0] instanceof RubyString) {
             args[0] = RubyNumeric.str2inum(runtime, (RubyString) args[0], 10, false);
         }
         
         int year = (int) RubyNumeric.num2long(args[0]);
         int month = 1;
         
         if (len > 1) {
             if (!args[1].isNil()) {
                 IRubyObject tmp = args[1].checkStringType();
                 if (!tmp.isNil()) {
                     String monthString = tmp.toString();
                     month = -1;
                     for (int i = 0; i < 12; i++) {
                         if (months[i].equalsIgnoreCase(monthString)) {
                             month = i+1;
                         }
                     }
                     if (month == -1) {
                         try {
                             month = Integer.parseInt(monthString);
                         } catch (NumberFormatException nfExcptn) {
                             throw runtime.newArgumentError("Argument out of range.");
                         }
                     }
                 } else {
                     month = (int)RubyNumeric.num2long(args[1]);
                 }
             }
             if (1 > month || month > 12) {
                 throw runtime.newArgumentError("Argument out of range: for month: " + month);
             }
         }
 
         int[] int_args = { 1, 0, 0, 0, 0, 0 };
 
         for (int i = 0; int_args.length >= i + 2; i++) {
             if (!args[i + 2].isNil()) {
                 if(!(args[i+2] instanceof RubyNumeric)) {
                     args[i+2] = args[i+2].callMethod(context,"to_i");
                 }
                 long value = RubyNumeric.num2long(args[i + 2]);
                 if (time_min[i] > value || value > time_max[i]) {
                     throw runtime.newArgumentError("argument out of range.");
                 }
                 int_args[i] = (int) value;
             }
         }
         
         if (0 <= year && year < 39) {
             year += 2000;
         } else if (69 <= year && year < 139) {
             year += 1900;
         }
 
         DateTime dt = new DateTime();
         if (gmt) {
             dt = dt.withZone(DateTimeZone.UTC);
         } else {
             dt = dt.withZone(getLocalTimeZone(runtime));
         }
 
         // set up with min values and then add to allow rolling over
         try {
             dt = dt.withDate(year, 1, 1)
                     .withHourOfDay(0)
                     .withMinuteOfHour(0)
                     .withSecondOfMinute(0)
                     .withMillisOfSecond(0);
             dt = dt.plusMonths(month - 1)
                     .plusDays(int_args[0] - 1)
                     .plusHours(int_args[1])
                     .plusMinutes(int_args[2])
                     .plusSeconds(int_args[3]);
         } catch (org.joda.time.IllegalFieldValueException e) {
             throw runtime.newArgumentError("time out of range");
         }
 
         RubyTime time = new RubyTime(runtime, (RubyClass) recv, dt);
         // Ignores usec if 8 args (for compatibility with parsedate) or if not supplied.
         if (args.length != 8 && !args[6].isNil()) {
             int usec = int_args[4] % 1000;
             int msec = int_args[4] / 1000;
 
             if (int_args[4] < 0) {
                 msec -= 1;
                 usec += 1000;
             }
             time.dt = dt.withMillis(dt.getMillis()+msec);
             time.setUSec(usec);
         }
 
         time.callInit(IRubyObject.NULL_ARRAY, Block.NULL_BLOCK);
         return time;
     }
 }
diff --git a/src/org/jruby/javasupport/Java.java b/src/org/jruby/javasupport/Java.java
index f0fb2e12f5..9298ff4fc0 100644
--- a/src/org/jruby/javasupport/Java.java
+++ b/src/org/jruby/javasupport/Java.java
@@ -59,1105 +59,1107 @@ import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyModule;
 import org.jruby.RubyProc;
 import org.jruby.RubyString;
 import org.jruby.RubyTime;
 import org.jruby.common.IRubyWarnings.ID;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.javasupport.proxy.JavaProxyClass;
 import org.jruby.javasupport.proxy.JavaProxyConstructor;
 import org.jruby.javasupport.util.RuntimeHelpers;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.load.Library;
 import org.jruby.util.ByteList;
 import org.jruby.util.ClassProvider;
 import org.jruby.anno.JRubyClass;
 import org.jruby.anno.JRubyMethod;
 import org.jruby.anno.JRubyModule;
 import org.jruby.runtime.callback.Callback;
 
 @JRubyModule(name = "Java")
 public class Java implements Library {
 
     public void load(Ruby runtime, boolean wrap) throws IOException {
         createJavaModule(runtime);
         runtime.getLoadService().smartLoad("builtin/javasupport");
         RubyClassPathVariable.createClassPathVariable(runtime);
     }
 
     public static RubyModule createJavaModule(Ruby runtime) {
         RubyModule javaModule = runtime.defineModule("Java");
         
         javaModule.defineAnnotatedMethods(Java.class);
 
         JavaObject.createJavaObjectClass(runtime, javaModule);
         JavaArray.createJavaArrayClass(runtime, javaModule);
         JavaClass.createJavaClassClass(runtime, javaModule);
         JavaMethod.createJavaMethodClass(runtime, javaModule);
         JavaConstructor.createJavaConstructorClass(runtime, javaModule);
         JavaField.createJavaFieldClass(runtime, javaModule);
 
         // also create the JavaProxy* classes
         JavaProxyClass.createJavaProxyModule(runtime);
 
         RubyModule javaUtils = runtime.defineModule("JavaUtilities");
         
         javaUtils.defineAnnotatedMethods(JavaUtilities.class);
 
         runtime.getJavaSupport().setConcreteProxyCallback(new Callback() {
             public IRubyObject execute(IRubyObject recv, IRubyObject[] args, Block block) {
                 Arity.checkArgumentCount(recv.getRuntime(), args, 1, 1);
                 
                 return Java.concrete_proxy_inherited(recv, args[0]);
             }
 
             public Arity getArity() {
                 return Arity.ONE_ARGUMENT;
             }
         });
 
         JavaArrayUtilities.createJavaArrayUtilitiesModule(runtime);
 
         RubyClass javaProxy = runtime.defineClass("JavaProxy", runtime.getObject(), runtime.getObject().getAllocator());
         javaProxy.defineAnnotatedMethods(JavaProxy.class);
 
         return javaModule;
     }
 
     @JRubyModule(name = "JavaUtilities")
     public static class JavaUtilities {
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject wrap(IRubyObject recv, IRubyObject arg0) {
             return Java.wrap(recv, arg0);
         }
         
         @JRubyMethod(name = "valid_constant_name?", module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject valid_constant_name_p(IRubyObject recv, IRubyObject arg0) {
             return Java.valid_constant_name_p(recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject primitive_match(IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
             return Java.primitive_match(recv, arg0, arg1);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject access(IRubyObject recv, IRubyObject arg0) {
             return Java.access(recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject matching_method(IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
             return Java.matching_method(recv, arg0, arg1);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject get_deprecated_interface_proxy(IRubyObject recv, IRubyObject arg0) {
             return Java.get_deprecated_interface_proxy(recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject get_interface_module(IRubyObject recv, IRubyObject arg0) {
             return Java.get_interface_module(recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject get_package_module(IRubyObject recv, IRubyObject arg0) {
             return Java.get_package_module(recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject get_package_module_dot_format(IRubyObject recv, IRubyObject arg0) {
             return Java.get_package_module_dot_format(recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject get_proxy_class(IRubyObject recv, IRubyObject arg0) {
             return Java.get_proxy_class(recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject is_primitive_type(IRubyObject recv, IRubyObject arg0) {
             return Java.is_primitive_type(recv, arg0);
         }
 
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject create_proxy_class(IRubyObject recv, IRubyObject arg0, IRubyObject arg1, IRubyObject arg2) {
             return Java.create_proxy_class(recv, arg0, arg1, arg2);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject get_java_class(IRubyObject recv, IRubyObject arg0) {
             return Java.get_java_class(recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject get_top_level_proxy_or_package(IRubyObject recv, IRubyObject arg0) {
             return Java.get_top_level_proxy_or_package(recv, arg0);
         }
         
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject get_proxy_or_package_under_package(IRubyObject recv, IRubyObject arg0, IRubyObject arg1) {
             return Java.get_proxy_or_package_under_package(recv, arg0, arg1);
         }
         
         @Deprecated
         @JRubyMethod(module = true, visibility = Visibility.PRIVATE)
         public static IRubyObject add_proxy_extender(IRubyObject recv, IRubyObject arg0) {
             return Java.add_proxy_extender(recv, arg0);
         }
     }
 
     @JRubyClass(name = "JavaProxy")
     public static class JavaProxy {
         @JRubyMethod(meta = true)
         public static IRubyObject new_instance_for(IRubyObject recv, IRubyObject arg0) {
             return Java.new_instance_for(recv, arg0);
         }
         
         @JRubyMethod(meta = true)
         public static IRubyObject to_java_object(IRubyObject recv) {
             return Java.to_java_object(recv);
         }
     }
     private static final ClassProvider JAVA_PACKAGE_CLASS_PROVIDER = new ClassProvider() {
 
         public RubyClass defineClassUnder(RubyModule pkg, String name, RubyClass superClazz) {
             // shouldn't happen, but if a superclass is specified, it's not ours
             if (superClazz != null) {
                 return null;
             }
             IRubyObject packageName;
             // again, shouldn't happen. TODO: might want to throw exception instead.
             if ((packageName = pkg.getInstanceVariables().fastGetInstanceVariable("@package_name")) == null) {
                 return null;
             }
             Ruby runtime = pkg.getRuntime();
             return (RubyClass) get_proxy_class(
                     runtime.getJavaSupport().getJavaUtilitiesModule(),
                     JavaClass.forNameVerbose(runtime, packageName.asJavaString() + name));
         }
 
         public RubyModule defineModuleUnder(RubyModule pkg, String name) {
             IRubyObject packageName;
             // again, shouldn't happen. TODO: might want to throw exception instead.
             if ((packageName = pkg.getInstanceVariables().fastGetInstanceVariable("@package_name")) == null) {
                 return null;
             }
             Ruby runtime = pkg.getRuntime();
             return (RubyModule) get_interface_module(
                     runtime.getJavaSupport().getJavaUtilitiesModule(),
                     JavaClass.forNameVerbose(runtime, packageName.asJavaString() + name));
         }
     };
     private static final Map<String, Boolean> JAVA_PRIMITIVES = new HashMap<String, Boolean>();
     
 
     static {
         String[] primitives = {"boolean", "byte", "char", "short", "int", "long", "float", "double"};
         for (String primitive : primitives) {
             JAVA_PRIMITIVES.put(primitive, Boolean.TRUE);
         }
     }
 
     public static IRubyObject is_primitive_type(IRubyObject recv, IRubyObject sym) {
         return recv.getRuntime().newBoolean(JAVA_PRIMITIVES.containsKey(sym.asJavaString()));
     }
 
     public static IRubyObject create_proxy_class(
             IRubyObject recv,
             IRubyObject constant,
             IRubyObject javaClass,
             IRubyObject module) {
         if (!(module instanceof RubyModule)) {
             throw recv.getRuntime().newTypeError(module, recv.getRuntime().getModule());
         }
         return ((RubyModule) module).const_set(constant, get_proxy_class(recv, javaClass));
     }
 
     public static IRubyObject get_java_class(IRubyObject recv, IRubyObject name) {
         try {
             return JavaClass.for_name(recv, name);
         } catch (Exception e) {
             return recv.getRuntime().getNil();
         }
     }
 
     /**
      * Returns a new proxy instance of type (RubyClass)recv for the wrapped java_object,
      * or the cached proxy if we've already seen this object.
      * 
      * @param recv the class for this object
      * @param java_object the java object wrapped in a JavaObject wrapper
      * @return the new or cached proxy for the specified Java object
      */
     public static IRubyObject new_instance_for(IRubyObject recv, IRubyObject java_object) {
         // FIXME: note temporary double-allocation of JavaObject as we move to cleaner interface
         if (java_object instanceof JavaObject) {
             return getInstance(((JavaObject) java_object).getValue(), (RubyClass) recv);
         }
         // in theory we should never get here, keeping around temporarily
         IRubyObject new_instance = ((RubyClass) recv).allocate();
         new_instance.getInstanceVariables().fastSetInstanceVariable("@java_object", java_object);
         return new_instance;
     }
 
     /**
      * Returns a new proxy instance of type clazz for rawJavaObject, or the cached
      * proxy if we've already seen this object.
      * 
      * @param rawJavaObject
      * @param clazz
      * @return the new or cached proxy for the specified Java object
      */
     public static IRubyObject getInstance(Object rawJavaObject, RubyClass clazz) {
         return clazz.getRuntime().getJavaSupport().getObjectProxyCache().getOrCreate(rawJavaObject, clazz);
     }
 
     /**
      * Returns a new proxy instance of a type corresponding to rawJavaObject's class,
      * or the cached proxy if we've already seen this object.  Note that primitives
      * and strings are <em>not</em> coerced to corresponding Ruby types; use
      * JavaUtil.convertJavaToUsableRubyObject to get coerced types or proxies as
      * appropriate.
      * 
      * @param runtime
      * @param rawJavaObject
      * @return the new or cached proxy for the specified Java object
      * @see JavaUtil.convertJavaToUsableRubyObject
      */
     public static IRubyObject getInstance(Ruby runtime, Object rawJavaObject) {
         if (rawJavaObject != null) {
             return runtime.getJavaSupport().getObjectProxyCache().getOrCreate(rawJavaObject,
                     (RubyClass) getProxyClass(runtime, JavaClass.get(runtime, rawJavaObject.getClass())));
         }
         return runtime.getNil();
     }
 
     // If the proxy class itself is passed as a parameter this will be called by Java#ruby_to_java    
     public static IRubyObject to_java_object(IRubyObject recv) {
         return recv.getInstanceVariables().fastGetInstanceVariable("@java_class");
     }
 
     // JavaUtilities
     /**
      * Add a new proxy extender. This is used by JavaUtilities to allow adding methods
      * to a given type's proxy and all types descending from that proxy's Java class.
      */
     @Deprecated
     public static IRubyObject add_proxy_extender(IRubyObject recv, IRubyObject extender) {
         // hacky workaround in case any users call this directly.
         // most will have called JavaUtilities.extend_proxy instead.
         recv.getRuntime().getWarnings().warn(ID.DEPRECATED_METHOD, "JavaUtilities.add_proxy_extender is deprecated - use JavaUtilities.extend_proxy instead", "add_proxy_extender", "JavaUtilities.extend_proxy");
         IRubyObject javaClassVar = extender.getInstanceVariables().fastGetInstanceVariable("@java_class");
         if (!(javaClassVar instanceof JavaClass)) {
             throw recv.getRuntime().newArgumentError("extender does not have a valid @java_class");
         }
         ((JavaClass) javaClassVar).addProxyExtender(extender);
         return recv.getRuntime().getNil();
     }
 
     public static RubyModule getInterfaceModule(Ruby runtime, JavaClass javaClass) {
         if (!javaClass.javaClass().isInterface()) {
             throw runtime.newArgumentError(javaClass.toString() + " is not an interface");
         }
         RubyModule interfaceModule;
         if ((interfaceModule = javaClass.getProxyModule()) != null) {
             return interfaceModule;
         }
         javaClass.lockProxy();
         try {
             if ((interfaceModule = javaClass.getProxyModule()) == null) {
                 interfaceModule = (RubyModule) runtime.getJavaSupport().getJavaInterfaceTemplate().dup();
                 interfaceModule.fastSetInstanceVariable("@java_class", javaClass);
                 addToJavaPackageModule(interfaceModule, javaClass);
                 javaClass.setupInterfaceModule(interfaceModule);
                 // include any interfaces we extend
                 Class<?>[] extended = javaClass.javaClass().getInterfaces();
                 for (int i = extended.length; --i >= 0;) {
                     JavaClass extendedClass = JavaClass.get(runtime, extended[i]);
                     RubyModule extModule = getInterfaceModule(runtime, extendedClass);
                     interfaceModule.includeModule(extModule);
                 }
             }
         } finally {
             javaClass.unlockProxy();
         }
         return interfaceModule;
     }
 
     public static IRubyObject get_interface_module(IRubyObject recv, IRubyObject javaClassObject) {
         Ruby runtime = recv.getRuntime();
         JavaClass javaClass;
         if (javaClassObject instanceof RubyString) {
             javaClass = JavaClass.for_name(recv, javaClassObject);
         } else if (javaClassObject instanceof JavaClass) {
             javaClass = (JavaClass) javaClassObject;
         } else {
             throw runtime.newArgumentError("expected JavaClass, got " + javaClassObject);
         }
         return getInterfaceModule(runtime, javaClass);
     }
 
     // Note: this isn't really all that deprecated, as it is used for
     // internal purposes, at least for now. But users should be discouraged
     // from calling this directly; eventually it will go away.
     public static IRubyObject get_deprecated_interface_proxy(IRubyObject recv, IRubyObject javaClassObject) {
         Ruby runtime = recv.getRuntime();
         JavaClass javaClass;
         if (javaClassObject instanceof RubyString) {
             javaClass = JavaClass.for_name(recv, javaClassObject);
         } else if (javaClassObject instanceof JavaClass) {
             javaClass = (JavaClass) javaClassObject;
         } else {
             throw runtime.newArgumentError("expected JavaClass, got " + javaClassObject);
         }
         if (!javaClass.javaClass().isInterface()) {
             throw runtime.newArgumentError("expected Java interface class, got " + javaClassObject);
         }
         RubyClass proxyClass;
         if ((proxyClass = javaClass.getProxyClass()) != null) {
             return proxyClass;
         }
         javaClass.lockProxy();
         try {
             if ((proxyClass = javaClass.getProxyClass()) == null) {
                 RubyModule interfaceModule = getInterfaceModule(runtime, javaClass);
                 RubyClass interfaceJavaProxy = runtime.fastGetClass("InterfaceJavaProxy");
                 proxyClass = RubyClass.newClass(runtime, interfaceJavaProxy);
                 proxyClass.setAllocator(interfaceJavaProxy.getAllocator());
                 proxyClass.makeMetaClass(interfaceJavaProxy.getMetaClass());
                 // parent.setConstant(name, proxyClass); // where the name should come from ?
                 proxyClass.inherit(interfaceJavaProxy);
                 proxyClass.callMethod(recv.getRuntime().getCurrentContext(), "java_class=", javaClass);
                 // including interface module so old-style interface "subclasses" will
                 // respond correctly to #kind_of?, etc.
                 proxyClass.includeModule(interfaceModule);
                 javaClass.setupProxy(proxyClass);
                 // add reference to interface module
                 if (proxyClass.fastGetConstantAt("Includable") == null) {
                     proxyClass.fastSetConstant("Includable", interfaceModule);
                 }
 
             }
         } finally {
             javaClass.unlockProxy();
         }
         return proxyClass;
     }
 
     public static RubyModule getProxyClass(Ruby runtime, JavaClass javaClass) {
         RubyClass proxyClass;
         Class<?> c;
         if ((c = javaClass.javaClass()).isInterface()) {
             return getInterfaceModule(runtime, javaClass);
         }
         if ((proxyClass = javaClass.getProxyClass()) != null) {
             return proxyClass;
         }
         javaClass.lockProxy();
         try {
             if ((proxyClass = javaClass.getProxyClass()) == null) {
 
                 if (c.isArray()) {
                     proxyClass = createProxyClass(runtime,
                             runtime.getJavaSupport().getArrayProxyClass(),
                             javaClass, true);
 
                 } else if (c.isPrimitive()) {
                     proxyClass = createProxyClass(runtime,
                             runtime.getJavaSupport().getConcreteProxyClass(),
                             javaClass, true);
 
                 } else if (c == Object.class) {
                     // java.lang.Object is added at root of java proxy classes
                     proxyClass = createProxyClass(runtime,
                             runtime.getJavaSupport().getConcreteProxyClass(),
                             javaClass, true);
                     proxyClass.getMetaClass().defineFastMethod("inherited",
                             runtime.getJavaSupport().getConcreteProxyCallback());
                     addToJavaPackageModule(proxyClass, javaClass);
 
                 } else {
                     // other java proxy classes added under their superclass' java proxy
                     proxyClass = createProxyClass(runtime,
                             (RubyClass) getProxyClass(runtime, JavaClass.get(runtime, c.getSuperclass())),
                             javaClass, false);
 
                     // include interface modules into the proxy class
                     Class<?>[] interfaces = c.getInterfaces();
                     for (int i = interfaces.length; --i >= 0;) {
                         JavaClass ifc = JavaClass.get(runtime, interfaces[i]);
                         proxyClass.includeModule(getInterfaceModule(runtime, ifc));
                     }
                     if (Modifier.isPublic(c.getModifiers())) {
                         addToJavaPackageModule(proxyClass, javaClass);
                     }
                 }
             }
         } finally {
             javaClass.unlockProxy();
         }
         return proxyClass;
     }
 
     public static IRubyObject get_proxy_class(IRubyObject recv, IRubyObject java_class_object) {
         Ruby runtime = recv.getRuntime();
         JavaClass javaClass;
         if (java_class_object instanceof RubyString) {
             javaClass = JavaClass.for_name(recv, java_class_object);
         } else if (java_class_object instanceof JavaClass) {
             javaClass = (JavaClass) java_class_object;
         } else {
             throw runtime.newTypeError(java_class_object, runtime.getJavaSupport().getJavaClassClass());
         }
         return getProxyClass(runtime, javaClass);
     }
 
     private static RubyClass createProxyClass(Ruby runtime, RubyClass baseType,
             JavaClass javaClass, boolean invokeInherited) {
         // this needs to be split, since conditional calling #inherited doesn't fit standard ruby semantics
         RubyClass.checkInheritable(baseType);
         RubyClass superClass = (RubyClass) baseType;
         RubyClass proxyClass = RubyClass.newClass(runtime, superClass);
         proxyClass.makeMetaClass(superClass.getMetaClass());
         proxyClass.setAllocator(superClass.getAllocator());
         if (invokeInherited) {
             proxyClass.inherit(superClass);
         }
         proxyClass.callMethod(runtime.getCurrentContext(), "java_class=", javaClass);
         javaClass.setupProxy(proxyClass);
         return proxyClass;
     }
 
     public static IRubyObject concrete_proxy_inherited(IRubyObject recv, IRubyObject subclass) {
         Ruby runtime = recv.getRuntime();
         ThreadContext tc = runtime.getCurrentContext();
         JavaSupport javaSupport = runtime.getJavaSupport();
         RubyClass javaProxyClass = javaSupport.getJavaProxyClass().getMetaClass();
         RuntimeHelpers.invokeAs(tc, javaProxyClass, recv, "inherited", new IRubyObject[]{subclass},
                 org.jruby.runtime.CallType.SUPER, Block.NULL_BLOCK);
         // TODO: move to Java
         return javaSupport.getJavaUtilitiesModule().callMethod(tc, "setup_java_subclass",
                 new IRubyObject[]{subclass, recv.callMethod(tc, "java_class")});
     }
 
     // package scheme 2: separate module for each full package name, constructed 
     // from the camel-cased package segments: Java::JavaLang::Object, 
     private static void addToJavaPackageModule(RubyModule proxyClass, JavaClass javaClass) {
         Class<?> clazz = javaClass.javaClass();
         String fullName;
         if ((fullName = clazz.getName()) == null) {
             return;
         }
         int endPackage = fullName.lastIndexOf('.');
         // we'll only map conventional class names to modules 
         if (fullName.indexOf('$') != -1 || !Character.isUpperCase(fullName.charAt(endPackage + 1))) {
             return;
         }
         Ruby runtime = proxyClass.getRuntime();
         String packageString = endPackage < 0 ? "" : fullName.substring(0, endPackage);
         RubyModule packageModule = getJavaPackageModule(runtime, packageString);
         if (packageModule != null) {
             String className = fullName.substring(endPackage + 1);
             if (packageModule.getConstantAt(className) == null) {
                 packageModule.const_set(runtime.newSymbol(className), proxyClass);
             }
         }
     }
 
     private static RubyModule getJavaPackageModule(Ruby runtime, String packageString) {
         String packageName;
         int length = packageString.length();
         if (length == 0) {
             packageName = "Default";
         } else {
             StringBuffer buf = new StringBuffer();
             for (int start = 0, offset = 0; start < length; start = offset + 1) {
                 if ((offset = packageString.indexOf('.', start)) == -1) {
                     offset = length;
                 }
                 buf.append(Character.toUpperCase(packageString.charAt(start))).append(packageString.substring(start + 1, offset));
             }
             packageName = buf.toString();
         }
 
         RubyModule javaModule = runtime.getJavaSupport().getJavaModule();
         IRubyObject packageModule = javaModule.getConstantAt(packageName);
         if (packageModule == null) {
             return createPackageModule(javaModule, packageName, packageString);
         } else if (packageModule instanceof RubyModule) {
             return (RubyModule) packageModule;
         } else {
             return null;
         }
     }
 
     private static RubyModule createPackageModule(RubyModule parent, String name, String packageString) {
         Ruby runtime = parent.getRuntime();
         RubyModule packageModule = (RubyModule) runtime.getJavaSupport().getPackageModuleTemplate().dup();
         packageModule.fastSetInstanceVariable("@package_name", runtime.newString(
                 packageString.length() > 0 ? packageString + '.' : packageString));
 
         // this is where we'll get connected when classes are opened using
         // package module syntax.
         packageModule.addClassProvider(JAVA_PACKAGE_CLASS_PROVIDER);
 
         parent.const_set(runtime.newSymbol(name), packageModule);
         MetaClass metaClass = (MetaClass) packageModule.getMetaClass();
         metaClass.setAttached(packageModule);
         return packageModule;
     }
     private static final Pattern CAMEL_CASE_PACKAGE_SPLITTER = Pattern.compile("([a-z][0-9]*)([A-Z])");
 
     public static RubyModule getPackageModule(Ruby runtime, String name) {
         RubyModule javaModule = runtime.getJavaSupport().getJavaModule();
         IRubyObject value;
         if ((value = javaModule.getConstantAt(name)) instanceof RubyModule) {
             return (RubyModule) value;
         }
         String packageName;
         if ("Default".equals(name)) {
             packageName = "";
         } else {
             Matcher m = CAMEL_CASE_PACKAGE_SPLITTER.matcher(name);
             packageName = m.replaceAll("$1.$2").toLowerCase();
         }
         return createPackageModule(javaModule, name, packageName);
     }
 
     public static IRubyObject get_package_module(IRubyObject recv, IRubyObject symObject) {
         return getPackageModule(recv.getRuntime(), symObject.asJavaString());
     }
 
     public static IRubyObject get_package_module_dot_format(IRubyObject recv, IRubyObject dottedName) {
         Ruby runtime = recv.getRuntime();
         RubyModule module = getJavaPackageModule(runtime, dottedName.asJavaString());
         return module == null ? runtime.getNil() : module;
     }
 
     public static RubyModule getProxyOrPackageUnderPackage(final Ruby runtime, RubyModule parentPackage, String sym) {
         IRubyObject packageNameObj = parentPackage.fastGetInstanceVariable("@package_name");
         if (packageNameObj == null) {
             throw runtime.newArgumentError("invalid package module");
         }
         String packageName = packageNameObj.asJavaString();
         final String name = sym.trim().intern();
         if (name.length() == 0) {
             throw runtime.newArgumentError("empty class or package name");
         }
         String fullName = packageName + name;
         if (Character.isLowerCase(name.charAt(0))) {
             // TODO: should check against all Java reserved names here, not just primitives
             if (JAVA_PRIMITIVES.containsKey(name)) {
                 throw runtime.newArgumentError("illegal package name component: " + name);
             // this covers the rare case of lower-case class names (and thus will
             // fail 99.999% of the time). fortunately, we'll only do this once per
             // package name. (and seriously, folks, look into best practices...)
             }
             try {
                 return getProxyClass(runtime, JavaClass.forNameQuiet(runtime, fullName));
             } catch (RaiseException re) { /* expected */
                 RubyException rubyEx = re.getException();
                 if (rubyEx.kind_of_p(runtime.getStandardError()).isTrue()) {
                     RuntimeHelpers.setErrorInfo(runtime, runtime.getNil());
                 }
             } catch (Exception e) { /* expected */ }
 
             RubyModule packageModule;
             // TODO: decompose getJavaPackageModule so we don't parse fullName
             if ((packageModule = getJavaPackageModule(runtime, fullName)) == null) {
                 return null;
             // save package module as ivar in parent, and add method to parent so
             // we don't have to come back here.
             }
             final String ivarName = ("@__pkg__" + name).intern();
             parentPackage.fastSetInstanceVariable(ivarName, packageModule);
             RubyClass singleton = parentPackage.getSingletonClass();
             singleton.addMethod(name, new org.jruby.internal.runtime.methods.JavaMethod(singleton, Visibility.PUBLIC) {
 
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                     if (args.length != 0) {
                         Arity.raiseArgumentError(runtime, args.length, 0, 0);
                     }
                     IRubyObject variable;
                     if ((variable = ((RubyModule) self).fastGetInstanceVariable(ivarName)) != null) {
                         return variable;
                     }
                     return runtime.getNil();
                 }
 
                 public Arity getArity() {
                     return Arity.noArguments();
                 }
             });
             return packageModule;
         } else {
             // upper case name, so most likely a class
             return getProxyClass(runtime, JavaClass.forNameVerbose(runtime, fullName));
 
         // FIXME: we should also support orgs that use capitalized package
         // names (including, embarrassingly, the one I work for), but this
         // should be enabled by a system property, as the expected default
         // behavior for an upper-case value should be (and is) to treat it
         // as a class name, and raise an exception if it's not found 
 
 //            try {
 //                return getProxyClass(runtime, JavaClass.forName(runtime, fullName));
 //            } catch (Exception e) {
 //                // but for those not hip to conventions and best practices,
 //                // we'll try as a package
 //                return getJavaPackageModule(runtime, fullName);
 //            }
         }
     }
 
     public static IRubyObject get_proxy_or_package_under_package(
             IRubyObject recv,
             IRubyObject parentPackage,
             IRubyObject sym) {
         Ruby runtime = recv.getRuntime();
         if (!(parentPackage instanceof RubyModule)) {
             throw runtime.newTypeError(parentPackage, runtime.getModule());
         }
         RubyModule result;
         if ((result = getProxyOrPackageUnderPackage(runtime,
                 (RubyModule) parentPackage, sym.asJavaString())) != null) {
             return result;
         }
         return runtime.getNil();
     }
 
     public static RubyModule getTopLevelProxyOrPackage(final Ruby runtime, String sym) {
         final String name = sym.trim().intern();
         if (name.length() == 0) {
             throw runtime.newArgumentError("empty class or package name");
         }
         if (Character.isLowerCase(name.charAt(0))) {
             // this covers primitives and (unlikely) lower-case class names
             try {
                 return getProxyClass(runtime, JavaClass.forNameQuiet(runtime, name));
             } catch (RaiseException re) { /* not primitive or lc class */
                 RubyException rubyEx = re.getException();
                 if (rubyEx.kind_of_p(runtime.getStandardError()).isTrue()) {
                     RuntimeHelpers.setErrorInfo(runtime, runtime.getNil());
                 }
             } catch (Exception e) { /* not primitive or lc class */ }
 
             // TODO: check for Java reserved names and raise exception if encountered
 
             RubyModule packageModule;
             // TODO: decompose getJavaPackageModule so we don't parse fullName
             if ((packageModule = getJavaPackageModule(runtime, name)) == null) {
                 return null;
             }
             RubyModule javaModule = runtime.getJavaSupport().getJavaModule();
             if (javaModule.getMetaClass().isMethodBound(name, false)) {
                 return packageModule;
             // save package module as ivar in parent, and add method to parent so
             // we don't have to come back here.
             }
             final String ivarName = ("@__pkg__" + name).intern();
             javaModule.fastSetInstanceVariable(ivarName, packageModule);
             RubyClass singleton = javaModule.getSingletonClass();
             singleton.addMethod(name, new org.jruby.internal.runtime.methods.JavaMethod(singleton, Visibility.PUBLIC) {
 
                 public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject[] args, Block block) {
                     if (args.length != 0) {
                         Arity.raiseArgumentError(runtime, args.length, 0, 0);
                     }
                     IRubyObject variable;
                     if ((variable = ((RubyModule) self).fastGetInstanceVariable(ivarName)) != null) {
                         return variable;
                     }
                     return runtime.getNil();
                 }
 
                 public Arity getArity() {
                     return Arity.noArguments();
                 }
             });
             return packageModule;
         } else {
             try {
                 return getProxyClass(runtime, JavaClass.forNameQuiet(runtime, name));
             } catch (RaiseException re) { /* not a class */
                 RubyException rubyEx = re.getException();
                 if (rubyEx.kind_of_p(runtime.getStandardError()).isTrue()) {
                     RuntimeHelpers.setErrorInfo(runtime, runtime.getNil());
                 }
             } catch (Exception e) { /* not a class */ }
 
             // upper-case package name
             // TODO: top-level upper-case package was supported in the previous (Ruby-based)
             // implementation, so leaving as is.  see note at #getProxyOrPackageUnderPackage
             // re: future approach below the top-level.
             return getPackageModule(runtime, name);
         }
     }
 
     public static IRubyObject get_top_level_proxy_or_package(IRubyObject recv, IRubyObject sym) {
         Ruby runtime = recv.getRuntime();
         RubyModule result;
         if ((result = getTopLevelProxyOrPackage(runtime, sym.asJavaString())) != null) {
             return result;
         }
         return runtime.getNil();
     }
 
     public static IRubyObject matching_method(IRubyObject recv, IRubyObject methods, IRubyObject args) {
         Map matchCache = recv.getRuntime().getJavaSupport().getMatchCache();
 
         List<Class<?>> arg_types = new ArrayList<Class<?>>();
         int alen = ((RubyArray) args).getLength();
         IRubyObject[] aargs = ((RubyArray) args).toJavaArrayMaybeUnsafe();
         for (int i = 0; i < alen; i++) {
             if (aargs[i] instanceof JavaObject) {
                 arg_types.add(((JavaClass) ((JavaObject) aargs[i]).java_class()).javaClass());
             } else {
                 arg_types.add(aargs[i].getClass());
             }
         }
 
         Map ms = (Map) matchCache.get(methods);
         if (ms == null) {
             ms = new HashMap();
             matchCache.put(methods, ms);
         } else {
             IRubyObject method = (IRubyObject) ms.get(arg_types);
             if (method != null) {
                 return method;
             }
         }
 
         int mlen = ((RubyArray) methods).getLength();
         IRubyObject[] margs = ((RubyArray) methods).toJavaArrayMaybeUnsafe();
 
         for (int i = 0; i < 2; i++) {
             for (int k = 0; k < mlen; k++) {
                 IRubyObject method = margs[k];
                 List<Class<?>> types = Arrays.asList(((ParameterTypes) method).getParameterTypes());
 
                 // Compatible (by inheritance)
                 if (arg_types.size() == types.size()) {
                     // Exact match
                     if (types.equals(arg_types)) {
                         ms.put(arg_types, method);
                         return method;
                     }
 
                     boolean match = true;
                     for (int j = 0; j < types.size(); j++) {
                         if (!(JavaClass.assignable(types.get(j), arg_types.get(j)) &&
                                 (i > 0 || primitive_match(types.get(j), arg_types.get(j)))) && !JavaUtil.isDuckTypeConvertable(arg_types.get(j), types.get(j))) {
                             match = false;
                             break;
                         }
                     }
                     if (match) {
                         ms.put(arg_types, method);
                         return method;
                     }
                 } // Could check for varargs here?
 
             }
         }
 
         Object o1 = margs[0];
 
         if (o1 instanceof JavaConstructor || o1 instanceof JavaProxyConstructor) {
             throw recv.getRuntime().newNameError("no constructor with arguments matching " + arg_types + " on object " + recv.callMethod(recv.getRuntime().getCurrentContext(), "inspect"), null);
         } else {
             throw recv.getRuntime().newNameError("no " + ((JavaMethod) o1).name() + " with arguments matching " + arg_types + " on object " + recv.callMethod(recv.getRuntime().getCurrentContext(), "inspect"), null);
         }
     }
 
     public static IRubyObject matching_method_internal(IRubyObject recv, IRubyObject methods, IRubyObject[] args, int start, int len) {
         Map matchCache = recv.getRuntime().getJavaSupport().getMatchCache();
 
         List<Class<?>> arg_types = new ArrayList<Class<?>>();
         int aend = start + len;
 
         for (int i = start; i < aend; i++) {
             if (args[i] instanceof JavaObject) {
                 arg_types.add(((JavaClass) ((JavaObject) args[i]).java_class()).javaClass());
             } else {
                 arg_types.add(args[i].getClass());
             }
         }
 
         Map ms = (Map) matchCache.get(methods);
         if (ms == null) {
             ms = new HashMap();
             matchCache.put(methods, ms);
         } else {
             IRubyObject method = (IRubyObject) ms.get(arg_types);
             if (method != null) {
                 return method;
             }
         }
 
         int mlen = ((RubyArray) methods).getLength();
         IRubyObject[] margs = ((RubyArray) methods).toJavaArrayMaybeUnsafe();
 
         mfor:
         for (int k = 0; k < mlen; k++) {
             IRubyObject method = margs[k];
             Class<?>[] types = ((ParameterTypes) method).getParameterTypes();
             // Compatible (by inheritance)
             if (len == types.length) {
                 // Exact match
                 boolean same = true;
                 for (int x = 0, y = len; x < y; x++) {
                     if (!types[x].equals(arg_types.get(x))) {
                         same = false;
                         break;
                     }
                 }
                 if (same) {
                     ms.put(arg_types, method);
                     return method;
                 }
 
                 for (int j = 0, m = len; j < m; j++) {
                     if (!(JavaClass.assignable(types[j], arg_types.get(j)) &&
                             primitive_match(types[j], arg_types.get(j)))) {
                         continue mfor;
                     }
                 }
                 ms.put(arg_types, method);
                 return method;
             }
         }
 
         mfor:
         for (int k = 0; k < mlen; k++) {
             IRubyObject method = margs[k];
             Class<?>[] types = ((ParameterTypes) method).getParameterTypes();
             // Compatible (by inheritance)
             if (len == types.length) {
                 for (int j = 0, m = len; j < m; j++) {
                     if (!JavaClass.assignable(types[j], arg_types.get(j)) && !JavaUtil.isDuckTypeConvertable(arg_types.get(j), types[j])) {
                         continue mfor;
                     }
                 }
                 ms.put(arg_types, method);
                 return method;
             }
         }
 
         Object o1 = margs[0];
 
         if (o1 instanceof JavaConstructor || o1 instanceof JavaProxyConstructor) {
             throw recv.getRuntime().newNameError("no constructor with arguments matching " + arg_types + " on object " + recv.callMethod(recv.getRuntime().getCurrentContext(), "inspect"), null);
         } else {
             throw recv.getRuntime().newNameError("no " + ((JavaMethod) o1).name() + " with arguments matching " + arg_types + " on object " + recv.callMethod(recv.getRuntime().getCurrentContext(), "inspect"), null);
         }
     }
 
     public static IRubyObject access(IRubyObject recv, IRubyObject java_type) {
         int modifiers = ((JavaClass) java_type).javaClass().getModifiers();
         return recv.getRuntime().newString(Modifier.isPublic(modifiers) ? "public" : (Modifier.isProtected(modifiers) ? "protected" : "private"));
     }
 
     public static IRubyObject valid_constant_name_p(IRubyObject recv, IRubyObject name) {
         RubyString sname = name.convertToString();
         if (sname.getByteList().length() == 0) {
             return recv.getRuntime().getFalse();
         }
         return Character.isUpperCase(sname.getByteList().charAt(0)) ? recv.getRuntime().getTrue() : recv.getRuntime().getFalse();
     }
 
     public static boolean primitive_match(Object v1, Object v2) {
         if (((Class) v1).isPrimitive()) {
             if (v1 == Integer.TYPE || v1 == Long.TYPE || v1 == Short.TYPE || v1 == Character.TYPE) {
                 return v2 == Integer.class ||
                         v2 == Long.class ||
                         v2 == Short.class ||
                         v2 == Character.class;
             } else if (v1 == Float.TYPE || v1 == Double.TYPE) {
                 return v2 == Float.class ||
                         v2 == Double.class;
             } else if (v1 == Boolean.TYPE) {
                 return v2 == Boolean.class;
             }
             return false;
         }
         return true;
     }
 
     public static IRubyObject primitive_match(IRubyObject recv, IRubyObject t1, IRubyObject t2) {
         if (((JavaClass) t1).primitive_p().isTrue()) {
             Object v1 = ((JavaObject) t1).getValue();
             Object v2 = ((JavaObject) t2).getValue();
             return primitive_match(v1, v2) ? recv.getRuntime().getTrue() : recv.getRuntime().getFalse();
         }
         return recv.getRuntime().getTrue();
     }
 
     public static IRubyObject wrap(IRubyObject recv, IRubyObject java_object) {
         return getInstance(recv.getRuntime(), ((JavaObject) java_object).getValue());
     }
 
     public static IRubyObject wrap(Ruby runtime, IRubyObject java_object) {
         return getInstance(runtime, ((JavaObject) java_object).getValue());
     }
 
     // Java methods
     @JRubyMethod(required = 1, optional = 1, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject define_exception_handler(IRubyObject recv, IRubyObject[] args, Block block) {
         String name = args[0].toString();
         RubyProc handler = null;
         if (args.length > 1) {
             handler = (RubyProc) args[1];
         } else {
             handler = recv.getRuntime().newProc(Block.Type.PROC, block);
         }
         recv.getRuntime().getJavaSupport().defineExceptionHandler(name, handler);
 
         return recv;
     }
 
     @JRubyMethod(frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject primitive_to_java(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         if (object instanceof JavaObject) {
             return object;
         }
         Ruby runtime = recv.getRuntime();
         Object javaObject;
         switch (object.getMetaClass().index) {
         case ClassIndex.NIL:
             javaObject = null;
             break;
         case ClassIndex.FIXNUM:
             javaObject = new Long(((RubyFixnum) object).getLongValue());
             break;
         case ClassIndex.BIGNUM:
             javaObject = ((RubyBignum) object).getValue();
             break;
         case ClassIndex.FLOAT:
             javaObject = new Double(((RubyFloat) object).getValue());
             break;
         case ClassIndex.STRING:
             try {
                 ByteList bytes = ((RubyString) object).getByteList();
                 javaObject = new String(bytes.unsafeBytes(), bytes.begin(), bytes.length(), "UTF8");
             } catch (UnsupportedEncodingException uee) {
                 javaObject = object.toString();
             }
             break;
         case ClassIndex.TRUE:
             javaObject = Boolean.TRUE;
             break;
         case ClassIndex.FALSE:
             javaObject = Boolean.FALSE;
             break;
+        case ClassIndex.TIME:
+            javaObject = ((RubyTime) object).getJavaDate();
+            break;
         default:
-            if (object instanceof RubyTime) {
-                javaObject = ((RubyTime) object).getJavaDate();
-            } else {
-                javaObject = object;
-            }
+            // it's not one of the types we convert, so just pass it out as-is without wrapping
+            return object;
         }
+
+        // we've found a Java type to which we've coerced the Ruby value, wrap it
         return JavaObject.wrap(runtime, javaObject);
     }
 
     /**
      * High-level object conversion utility function 'java_to_primitive' is the low-level version 
      */
     @JRubyMethod(frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject java_to_ruby(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         if (object instanceof JavaObject) {
             return JavaUtil.convertJavaToUsableRubyObject(recv.getRuntime(), ((JavaObject) object).getValue());
         }
         return object;
     }
 
     // TODO: Formalize conversion mechanisms between Java and Ruby
     /**
      * High-level object conversion utility. 
      */
     @JRubyMethod(frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject ruby_to_java(final IRubyObject recv, IRubyObject object, Block unusedBlock) {
         if (object.respondsTo("to_java_object")) {
             IRubyObject result = object.getInstanceVariables().fastGetInstanceVariable("@java_object");
             if (result == null) {
                 result = object.callMethod(recv.getRuntime().getCurrentContext(), "to_java_object");
             }
             if (result instanceof JavaObject) {
                 recv.getRuntime().getJavaSupport().getObjectProxyCache().put(((JavaObject) result).getValue(), object);
             }
             return result;
         }
 
         return primitive_to_java(recv, object, unusedBlock);
     }
 
     @JRubyMethod(frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject java_to_primitive(IRubyObject recv, IRubyObject object, Block unusedBlock) {
         if (object instanceof JavaObject) {
             return JavaUtil.convertJavaToRuby(recv.getRuntime(), ((JavaObject) object).getValue());
         }
 
         return object;
     }
 
     @JRubyMethod(required = 1, rest = true, frame = true, module = true, visibility = Visibility.PRIVATE)
     public static IRubyObject new_proxy_instance(final IRubyObject recv, IRubyObject[] args, Block block) {
         int size = Arity.checkArgumentCount(recv.getRuntime(), args, 1, -1) - 1;
         final RubyProc proc;
 
         // Is there a supplied proc argument or do we assume a block was supplied
         if (args[size] instanceof RubyProc) {
             proc = (RubyProc) args[size];
         } else {
             proc = recv.getRuntime().newProc(Block.Type.PROC, block);
             size++;
         }
 
         // Create list of interfaces to proxy (and make sure they really are interfaces)
         Class[] interfaces = new Class[size];
         for (int i = 0; i < size; i++) {
             if (!(args[i] instanceof JavaClass) || !((JavaClass) args[i]).interface_p().isTrue()) {
                 throw recv.getRuntime().newArgumentError("Java interface expected. got: " + args[i]);
             }
             interfaces[i] = ((JavaClass) args[i]).javaClass();
         }
 
         return JavaObject.wrap(recv.getRuntime(), Proxy.newProxyInstance(recv.getRuntime().getJRubyClassLoader(), interfaces, new InvocationHandler() {
 
             private Map parameterTypeCache = new ConcurrentHashMap();
 
             public Object invoke(Object proxy, Method method, Object[] nargs) throws Throwable {
                 Class[] parameterTypes = (Class[]) parameterTypeCache.get(method);
                 if (parameterTypes == null) {
                     parameterTypes = method.getParameterTypes();
                     parameterTypeCache.put(method, parameterTypes);
                 }
                 int methodArgsLength = parameterTypes.length;
                 String methodName = method.getName();
 
                 if (methodName.equals("toString") && methodArgsLength == 0) {
                     return proxy.getClass().getName();
                 } else if (methodName.equals("hashCode") && methodArgsLength == 0) {
                     return new Integer(proxy.getClass().hashCode());
                 } else if (methodName.equals("equals") && methodArgsLength == 1 && parameterTypes[0].equals(Object.class)) {
                     return Boolean.valueOf(proxy == nargs[0]);
                 }
                 Ruby runtime = recv.getRuntime();
                 int length = nargs == null ? 0 : nargs.length;
                 IRubyObject[] rubyArgs = new IRubyObject[length + 2];
                 rubyArgs[0] = JavaObject.wrap(runtime, proxy);
                 rubyArgs[1] = new JavaMethod(runtime, method);
                 for (int i = 0; i < length; i++) {
                     rubyArgs[i + 2] = JavaObject.wrap(runtime, nargs[i]);
                 }
                 return JavaUtil.convertArgument(runtime, proc.call(runtime.getCurrentContext(), rubyArgs), method.getReturnType());
             }
         }));
     }
 }
diff --git a/src/org/jruby/javasupport/JavaUtil.java b/src/org/jruby/javasupport/JavaUtil.java
index 425827db6a..fe9006a4d5 100644
--- a/src/org/jruby/javasupport/JavaUtil.java
+++ b/src/org/jruby/javasupport/JavaUtil.java
@@ -1,506 +1,512 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002 Don Schwartz <schwardo@users.sourceforge.net>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Kresten Krab Thorup <krab@gnu.org>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby.javasupport;
 
 import java.io.UnsupportedEncodingException;
 import java.math.BigDecimal;
 import java.math.BigInteger;
 import java.util.HashMap;
 import java.util.Map;
 
 import org.jruby.Ruby;
 import org.jruby.RubyBignum;
 import org.jruby.RubyBoolean;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyFloat;
 import org.jruby.RubyModule;
 import org.jruby.RubyNumeric;
 import org.jruby.RubyObject;
 import org.jruby.RubyProc;
 import org.jruby.RubyString;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 
 import org.jruby.util.ByteList;
 
 /**
  *
  * @author Jan Arne Petersen, Alan Moore
  */
 public class JavaUtil {
 
     public static Object convertRubyToJava(IRubyObject rubyObject) {
         return convertRubyToJava(rubyObject, null);
     }
     
     public interface RubyConverter {
         public Object convert(ThreadContext context, IRubyObject rubyObject);
     }
     
     public static final RubyConverter RUBY_BOOLEAN_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             return Boolean.valueOf(rubyObject.isTrue());
         }
     };
     
     public static final RubyConverter RUBY_BYTE_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return new Byte((byte) ((RubyNumeric) rubyObject.callMethod(
                         context, MethodIndex.TO_I, "to_i")).getLongValue());
             }
             return new Byte((byte) 0);
         }
     };
     
     public static final RubyConverter RUBY_SHORT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return new Short((short) ((RubyNumeric) rubyObject.callMethod(
                         context, MethodIndex.TO_I, "to_i")).getLongValue());
             }
             return new Short((short) 0);
         }
     };
     
     public static final RubyConverter RUBY_INTEGER_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return new Integer((int) ((RubyNumeric) rubyObject.callMethod(
                         context, MethodIndex.TO_I, "to_i")).getLongValue());
             }
             return new Integer(0);
         }
     };
     
     public static final RubyConverter RUBY_LONG_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_i")) {
                 return new Long(((RubyNumeric) rubyObject.callMethod(
                         context, MethodIndex.TO_I, "to_i")).getLongValue());
             }
             return new Long(0);
         }
     };
     
     public static final RubyConverter RUBY_FLOAT_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_f")) {
                 return new Float((float) ((RubyNumeric) rubyObject.callMethod(
                         context, MethodIndex.TO_F, "to_f")).getDoubleValue());
             }
             return new Float(0.0);
         }
     };
     
     public static final RubyConverter RUBY_DOUBLE_CONVERTER = new RubyConverter() {
         public Object convert(ThreadContext context, IRubyObject rubyObject) {
             if (rubyObject.respondsTo("to_f")) {
                 return new Double(((RubyNumeric) rubyObject.callMethod(
                         context, MethodIndex.TO_F, "to_f")).getDoubleValue());
             }
             return new Double(0.0);
         }
     };
     
     public static final Map<Class, RubyConverter> RUBY_CONVERTERS = new HashMap<Class, RubyConverter>();
     
     static {
         RUBY_CONVERTERS.put(Boolean.class, RUBY_BOOLEAN_CONVERTER);
         RUBY_CONVERTERS.put(Boolean.TYPE, RUBY_BOOLEAN_CONVERTER);
         RUBY_CONVERTERS.put(Byte.class, RUBY_BYTE_CONVERTER);
         RUBY_CONVERTERS.put(Byte.TYPE, RUBY_BYTE_CONVERTER);
         RUBY_CONVERTERS.put(Short.class, RUBY_SHORT_CONVERTER);
         RUBY_CONVERTERS.put(Short.TYPE, RUBY_SHORT_CONVERTER);
         RUBY_CONVERTERS.put(Integer.class, RUBY_INTEGER_CONVERTER);
         RUBY_CONVERTERS.put(Integer.TYPE, RUBY_INTEGER_CONVERTER);
         RUBY_CONVERTERS.put(Long.class, RUBY_LONG_CONVERTER);
         RUBY_CONVERTERS.put(Long.TYPE, RUBY_LONG_CONVERTER);
         RUBY_CONVERTERS.put(Float.class, RUBY_FLOAT_CONVERTER);
         RUBY_CONVERTERS.put(Float.TYPE, RUBY_FLOAT_CONVERTER);
         RUBY_CONVERTERS.put(Double.class, RUBY_DOUBLE_CONVERTER);
         RUBY_CONVERTERS.put(Double.TYPE, RUBY_DOUBLE_CONVERTER);
     }
 
     public static Object convertRubyToJava(IRubyObject rubyObject, Class javaClass) {
         if (rubyObject == null || rubyObject.isNil()) {
             return null;
         }
         
         ThreadContext context = rubyObject.getRuntime().getCurrentContext();
         
         if (rubyObject.respondsTo("java_object")) {
         	rubyObject = rubyObject.callMethod(context, "java_object");
         }
 
         if (rubyObject.respondsTo("to_java_object")) {
         	rubyObject = rubyObject.callMethod(context, "to_java_object");
         }
 
         if (rubyObject instanceof JavaObject) {
             Object value =  ((JavaObject) rubyObject).getValue();
             
             return convertArgument(rubyObject.getRuntime(), value, javaClass);
             
         } else if (javaClass == Object.class || javaClass == null) {
             /* The Java method doesn't care what class it is, but we need to
                know what to convert it to, so we use the object's own class.
                If that doesn't help, we use String to force a call to the
                object's "to_s" method. */
             javaClass = rubyObject.getJavaClass();
         }
 
         if (javaClass.isInstance(rubyObject)) {
             // rubyObject is already of the required jruby class (or subclass)
             return rubyObject;
         }
 
         if (javaClass.isPrimitive()) {
             RubyConverter converter = RUBY_CONVERTERS.get(javaClass);
             if (converter != null) {
                 return converter.convert(context, rubyObject);
             }
 
             // XXX this probably isn't good enough -AM
             String s = ((RubyString) rubyObject.callMethod(context, MethodIndex.TO_S, "to_s")).toString();
             if (s.length() > 0) {
                 return new Character(s.charAt(0));
             }
 			return new Character('\0');
         } else if (javaClass == String.class) {
             RubyString rubyString = (RubyString) rubyObject.callMethod(context, MethodIndex.TO_S, "to_s");
             ByteList bytes = rubyString.getByteList();
             try {
                 return new String(bytes.unsafeBytes(), bytes.begin(), bytes.length(), "UTF8");
             } catch (UnsupportedEncodingException uee) {
                 return new String(bytes.unsafeBytes(), bytes.begin(), bytes.length());
             }
         } else if (javaClass == ByteList.class) {
             return rubyObject.convertToString().getByteList();
         } else if (javaClass == BigInteger.class) {
          	if (rubyObject instanceof RubyBignum) {
          		return ((RubyBignum)rubyObject).getValue();
          	} else if (rubyObject instanceof RubyNumeric) {
  				return  BigInteger.valueOf (((RubyNumeric)rubyObject).getLongValue());
          	} else if (rubyObject.respondsTo("to_i")) {
          		RubyNumeric rubyNumeric = ((RubyNumeric)rubyObject.callMethod(context,MethodIndex.TO_F, "to_f"));
  				return  BigInteger.valueOf (rubyNumeric.getLongValue());
          	}
         } else if (javaClass == BigDecimal.class && !(rubyObject instanceof JavaObject)) {
          	if (rubyObject.respondsTo("to_f")) {
              	double double_value = ((RubyNumeric)rubyObject.callMethod(context,MethodIndex.TO_F, "to_f")).getDoubleValue();
              	return new BigDecimal(double_value);
          	}
         }
         try {
             return ((JavaObject) rubyObject).getValue();
         } catch (ClassCastException ex) {
             if (rubyObject.getRuntime().getDebug().isTrue()) ex.printStackTrace();
             return null;
         }
     }
 
     public static IRubyObject[] convertJavaArrayToRuby(Ruby runtime, Object[] objects) {
         IRubyObject[] rubyObjects = new IRubyObject[objects.length];
         for (int i = 0; i < objects.length; i++) {
             rubyObjects[i] = convertJavaToRuby(runtime, objects[i]);
         }
         return rubyObjects;
     }
     
     public interface JavaConverter {
         public IRubyObject convert(Ruby runtime, Object object);
     }
     
     public static final JavaConverter JAVA_DEFAULT_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) {
                 return runtime.getNil();
             }
 
             if (object instanceof IRubyObject) {
                 return (IRubyObject) object;
             }
  
             // Note: returns JavaObject instance, which is not
             // directly usable. probably too late to change this now,
             // supplying alternate method convertJavaToUsableRubyObject
             return JavaObject.wrap(runtime, object);
         }
     };
     
     public static final JavaConverter JAVA_BOOLEAN_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyBoolean.newBoolean(runtime, ((Boolean)object).booleanValue());
         }
     };
     
     public static final JavaConverter JAVA_FLOAT_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFloat.newFloat(runtime, ((Float)object).doubleValue());
         }
     };
     
     public static final JavaConverter JAVA_DOUBLE_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFloat.newFloat(runtime, ((Double)object).doubleValue());
         }
     };
     
     public static final JavaConverter JAVA_CHAR_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Character)object).charValue());
         }
     };
     
     public static final JavaConverter JAVA_BYTE_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Byte)object).byteValue());
         }
     };
     
     public static final JavaConverter JAVA_SHORT_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Short)object).shortValue());
         }
     };
     
     public static final JavaConverter JAVA_INT_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Integer)object).intValue());
         }
     };
     
     public static final JavaConverter JAVA_LONG_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyFixnum.newFixnum(runtime, ((Long)object).longValue());
         }
     };
     
     public static final JavaConverter JAVA_STRING_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyString.newUnicodeString(runtime, (String)object);
         }
     };
     
     public static final JavaConverter BYTELIST_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyString.newString(runtime, (ByteList)object);
         }
     };
     
     public static final JavaConverter JAVA_BIGINTEGER_CONVERTER = new JavaConverter() {
         public IRubyObject convert(Ruby runtime, Object object) {
             if (object == null) return runtime.getNil();
             return RubyBignum.newBignum(runtime, (BigInteger)object);
         }
     };
     
     private static final Map<Class,JavaConverter> JAVA_CONVERTERS =
         new HashMap<Class,JavaConverter>();
     
     static {
         JAVA_CONVERTERS.put(Byte.class, JAVA_BYTE_CONVERTER);
         JAVA_CONVERTERS.put(Byte.TYPE, JAVA_BYTE_CONVERTER);
         JAVA_CONVERTERS.put(Short.class, JAVA_SHORT_CONVERTER);
         JAVA_CONVERTERS.put(Short.TYPE, JAVA_SHORT_CONVERTER);
         JAVA_CONVERTERS.put(Character.class, JAVA_CHAR_CONVERTER);
         JAVA_CONVERTERS.put(Character.TYPE, JAVA_CHAR_CONVERTER);
         JAVA_CONVERTERS.put(Integer.class, JAVA_INT_CONVERTER);
         JAVA_CONVERTERS.put(Integer.TYPE, JAVA_INT_CONVERTER);
         JAVA_CONVERTERS.put(Long.class, JAVA_LONG_CONVERTER);
         JAVA_CONVERTERS.put(Long.TYPE, JAVA_LONG_CONVERTER);
         JAVA_CONVERTERS.put(Float.class, JAVA_FLOAT_CONVERTER);
         JAVA_CONVERTERS.put(Float.TYPE, JAVA_FLOAT_CONVERTER);
         JAVA_CONVERTERS.put(Double.class, JAVA_DOUBLE_CONVERTER);
         JAVA_CONVERTERS.put(Double.TYPE, JAVA_DOUBLE_CONVERTER);
         JAVA_CONVERTERS.put(Boolean.class, JAVA_BOOLEAN_CONVERTER);
         JAVA_CONVERTERS.put(Boolean.TYPE, JAVA_BOOLEAN_CONVERTER);
         
         JAVA_CONVERTERS.put(String.class, JAVA_STRING_CONVERTER);
         
         JAVA_CONVERTERS.put(ByteList.class, BYTELIST_CONVERTER);
         
         JAVA_CONVERTERS.put(BigInteger.class, JAVA_BIGINTEGER_CONVERTER);
 
     }
     
     public static JavaConverter getJavaConverter(Class clazz) {
         JavaConverter converter = JAVA_CONVERTERS.get(clazz);
         
         if (converter == null) {
             converter = JAVA_DEFAULT_CONVERTER;
         }
         
         return converter;
     }
 
     /**
      * Converts object to the corresponding Ruby type; however, for non-primitives,
      * a JavaObject instance is returned. This must be subsequently wrapped by
      * calling one of Java.wrap, Java.java_to_ruby, Java.new_instance_for, or
      * Java.getInstance, depending on context.
      * 
      * @param runtime
      * @param object 
      * @return corresponding Ruby type, or a JavaObject instance
      */
     public static IRubyObject convertJavaToRuby(Ruby runtime, Object object) {
         if (object == null) {
             return runtime.getNil();
         }
         return convertJavaToRuby(runtime, object, object.getClass());
     }
 
     public static IRubyObject convertJavaToRuby(Ruby runtime, Object object, Class javaClass) {
         return getJavaConverter(javaClass).convert(runtime, object);
     }
     
     /**
      * Returns a usable RubyObject; for types that are not converted to Ruby native
      * types, a Java proxy will be returned. 
      * 
      * @param runtime
      * @param object
      * @return corresponding Ruby type, or a functional Java proxy
      */
     public static IRubyObject convertJavaToUsableRubyObject(Ruby runtime, Object object) {
         if (object == null) return runtime.getNil();
+        
+        // if it's already IRubyObject, don't re-wrap (JRUBY-2480)
+        if (object instanceof IRubyObject) {
+            return (IRubyObject)object;
+        }
+        
         JavaConverter converter = JAVA_CONVERTERS.get(object.getClass());
         if (converter == null || converter == JAVA_DEFAULT_CONVERTER) {
             return Java.getInstance(runtime, object);
         }
         return converter.convert(runtime, object);
     }
 
     public static Class<?> primitiveToWrapper(Class<?> type) {
         if (type.isPrimitive()) {
             if (type == Integer.TYPE) {
                 return Integer.class;
             } else if (type == Double.TYPE) {
                 return Double.class;
             } else if (type == Boolean.TYPE) {
                 return Boolean.class;
             } else if (type == Byte.TYPE) {
                 return Byte.class;
             } else if (type == Character.TYPE) {
                 return Character.class;
             } else if (type == Float.TYPE) {
                 return Float.class;
             } else if (type == Long.TYPE) {
                 return Long.class;
             } else if (type == Void.TYPE) {
                 return Void.class;
             } else if (type == Short.TYPE) {
                 return Short.class;
             }
         }
         return type;
     }
 
     public static Object convertArgument(Ruby runtime, Object argument, Class<?> parameterType) {
         if (argument == null) {
           if(parameterType.isPrimitive()) {
             throw runtime.newTypeError("primitives do not accept null");
           } else {
             return null;
           }
         }
         
         if (argument instanceof JavaObject) {
             argument = ((JavaObject) argument).getValue();
             if (argument == null) {
                 return null;
             }
         }
         Class<?> type = primitiveToWrapper(parameterType);
         if (type == Void.class) {
             return null;
         }
         if (argument instanceof Number) {
             final Number number = (Number) argument;
             if (type == Long.class) {
                 return new Long(number.longValue());
             } else if (type == Integer.class) {
                 return new Integer(number.intValue());
             } else if (type == Byte.class) {
                 return new Byte(number.byteValue());
             } else if (type == Character.class) {
                 return new Character((char) number.intValue());
             } else if (type == Double.class) {
                 return new Double(number.doubleValue());
             } else if (type == Float.class) {
                 return new Float(number.floatValue());
             } else if (type == Short.class) {
                 return new Short(number.shortValue());
             }
         }
         if (isDuckTypeConvertable(argument.getClass(), parameterType)) {
             RubyObject rubyObject = (RubyObject) argument;
             if (!rubyObject.respondsTo("java_object")) {
                 IRubyObject javaUtilities = runtime.getJavaSupport().getJavaUtilitiesModule();
                 IRubyObject javaInterfaceModule = Java.get_interface_module(javaUtilities, JavaClass.get(runtime, parameterType));
                 if (!((RubyModule)javaInterfaceModule).isInstance(rubyObject)) {
                     rubyObject.extend(new IRubyObject[] {javaInterfaceModule});
                 }
                 ThreadContext context = runtime.getCurrentContext();
                 if (rubyObject instanceof RubyProc) {
                     // Proc implementing an interface, pull in the catch-all code that lets the proc get invoked
                     // no matter what method is called on the interface
                     rubyObject.instance_eval(context, runtime.newString("extend Proc::CatchAll"), Block.NULL_BLOCK);
                 }
                 JavaObject jo = (JavaObject) rubyObject.instance_eval(context, runtime.newString("send :__jcreate_meta!"), Block.NULL_BLOCK);
                 return jo.getValue();
             }
         }
         return argument;
     }
     
     public static boolean isDuckTypeConvertable(Class providedArgumentType, Class parameterType) {
         return parameterType.isInterface() && !parameterType.isAssignableFrom(providedArgumentType) 
             && RubyObject.class.isAssignableFrom(providedArgumentType);
     }
 }
diff --git a/src/org/jruby/runtime/ClassIndex.java b/src/org/jruby/runtime/ClassIndex.java
index 0f32be18bb..3b8a0959ca 100644
--- a/src/org/jruby/runtime/ClassIndex.java
+++ b/src/org/jruby/runtime/ClassIndex.java
@@ -1,42 +1,43 @@
 /*
  * ClassIndex.java
  *
  * Created on January 1, 2007, 7:50 PM
  *
  * To change this template, choose Tools | Template Manager
  * and open the template in the editor.
  */
 
 package org.jruby.runtime;
 
 /**
  *
  * @author headius
  */
 public class ClassIndex {
     public static final int NO_INDEX = 0;
     public static final int FIXNUM = 1;
     public static final int BIGNUM = 2;
     public static final int ARRAY = 3;
     public static final int STRING = 4;
     public static final int NIL = 5;
     public static final int TRUE = 6;
     public static final int FALSE = 7;
     public static final int SYMBOL = 8;
     public static final int REGEXP = 9;
     public static final int HASH = 10;
     public static final int FLOAT = 11;
     public static final int MODULE = 12;
     public static final int CLASS = 13;
     public static final int OBJECT = 14;
     public static final int STRUCT = 15;
     public static final int INTEGER = 16;
     public static final int NUMERIC = 17;
     public static final int RANGE = 18;
-    public static final int MAX_CLASSES = 19;
+    public static final int TIME = 19;
+    public static final int MAX_CLASSES = 20;
     
     /** Creates a new instance of ClassIndex */
     public ClassIndex() {
     }
     
 }
diff --git a/test/jruby_index b/test/jruby_index
index de4e5ff9f8..6d02c1a81f 100644
--- a/test/jruby_index
+++ b/test/jruby_index
@@ -1,79 +1,80 @@
 # Our own test/unit-based tests
 # NOTE: test_globals comes first here because it has tests that $? be nil
 test_globals
 test_argf
 test_array
 test_array_subclass_behavior
 test_autoload
 test_backquote
 test_big_decimal
 test_bignum
 test_block
 test_block_arg_processing
 test_cache_map_leak
 test_case
 test_class
 test_command_line_switches
 test_comparable
 test_core_arities
 test_crazy_blocks
 test_date_time
 test_defined
 test_default_constants
 test_dir
 #test_digest2
 test_dup_clone_taint_freeze
 test_env
 test_etc
 test_eval
 test_eval_with_binding
 test_file
 test_flip
 test_frame_self
 test_hash
 test_higher_javasupport
 test_iconv
 test_io
 test_load
 test_math
 test_nkf
 test_java_accessible_object
 test_java_extension
 test_jruby_internals
 compiler/test_jrubyc
 test_launching_by_shell_script
 test_local_jump_error
 test_marshal_gemspec
 test_method_missing
 test_methods
 test_pack
 test_primitive_to_java
 test_process
 test_proc_visibility
 test_parsing
 test_random
 test_rbconfig
 test_socket
 test_string_java_bytes
 test_string_printf
 test_string_sub
 test_string_to_number
 test_symbol
 test_tb_yaml
 test_timeout
 test_thread
 test_threaded_nonlocal_return
 test_time_nil_ops
 test_variables
 test_vietnamese_charset
 #test_trace_func
 test_zlib
 test_yaml
 test_system_error
 
 # these tests are last because they pull in libraries that can affect others
 test_base64_strangeness
 test_loading_builtin_libraries
 test_rewriter
 test_load_compiled_ruby_class_from_classpath
 test_null_channel
+test_irubyobject_java_passing
diff --git a/test/org/jruby/test/JRUBY_2480_A.java b/test/org/jruby/test/JRUBY_2480_A.java
new file mode 100644
index 0000000000..99ec63f84d
--- /dev/null
+++ b/test/org/jruby/test/JRUBY_2480_A.java
@@ -0,0 +1,13 @@
+package org.jruby.test;
+
+public class JRUBY_2480_A {
+    private JRUBY_2480_B b;
+
+    public JRUBY_2480_A(JRUBY_2480_B b) {
+	this.b = b;
+    }
+	
+    public Object doIt(Object a) {
+	return b.foo(a);
+    }
+}
diff --git a/test/org/jruby/test/JRUBY_2480_B.java b/test/org/jruby/test/JRUBY_2480_B.java
new file mode 100644
index 0000000000..49571f8856
--- /dev/null
+++ b/test/org/jruby/test/JRUBY_2480_B.java
@@ -0,0 +1,6 @@
+package org.jruby.test;
+
+public interface JRUBY_2480_B {
+    public Object foo(Object a);
+}
+
diff --git a/test/test_irubyobject_java_passing.rb b/test/test_irubyobject_java_passing.rb
new file mode 100644
index 0000000000..aeb7696e30
--- /dev/null
+++ b/test/test_irubyobject_java_passing.rb
@@ -0,0 +1,31 @@
+require 'test/unit'
+require 'java'
+require 'pp'
+
+include_class 'org.jruby.test.JRUBY_2480_A'
+include_class 'org.jruby.test.JRUBY_2480_B'
+
+# JRUBY-2480, uncoercible Ruby objects getting wrapped when passing through Java code
+class TestIrubyobjectJavaPassing < Test::Unit::TestCase
+  class C
+    include JRUBY_2480_B
+  
+    def foo(o)
+      o.color
+    end
+  end
+  
+  class Color
+    attr_reader :color
+  
+    def initialize(color)
+      @color = color
+    end
+  end
+
+  def test_passing_irubyobject_through
+    a = JRUBY_2480_A.new(C.new)
+    result = a.doIt(Color.new("red"))
+    assert_equal("red", result)
+  end
+end
