90/report.java
Satd-method: public long lseek(ChannelFD fd, long offset, int type) {
********************************************
********************************************
90/After/623bc5974  Only treate -1 ret from  diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            if (ret < 0) errno = Errno.valueOf(posix.errno());
+            if (ret == -1) errno = Errno.valueOf(posix.errno());

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
lseek(
-                (pos = posix.lseek(fd, 0, PosixShim.SEEK_CUR)) >= 0 &&
+                (pos = posix.lseek(fd, 0, PosixShim.SEEK_CUR)) != -1 &&

Lines added containing method: 1. Lines removed containing method: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* errnoFromException
* position
********************************************
********************************************
90/After/d9090c3aa  Use lseekLong to get lse diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***

-            int ret = posix.lseek(fd.chNative.getFD(), offset, type);
+            long ret = posix.lseekLong(fd.chNative.getFD(), offset, type);

Lines added: 1. Lines removed: 1. Tot = 2
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
lseek(
-            int ret = posix.lseek(fd.chNative.getFD(), offset, type);

Lines added containing method: 0. Lines removed containing method: 1. Tot = 1
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* errnoFromException
* position
********************************************
********************************************
90/After/f15612339  Only set EAGAIN when wri diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
lseek(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* errnoFromException
* position
********************************************
********************************************
