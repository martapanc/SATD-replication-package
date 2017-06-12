File path: src/org/jruby/RubyThread.java
Comment: / TODO: no java stringity
Initial commit id: c4c035be
Final commit id: ba93d1ae
   Bugs between [      13]:
803fcef21c Fix #361
92b38c48cd Fix JRUBY-5732
6bd8b11484 Fix JRUBY-6896: nil treated as 0 in call to Thread#join
caa7f5ad7b Fix JRUBY-6553
9a55736b5d Fix JRUBY-5610: Socket#accept_nonblock unconditionally raises Errno::EAGAIN
fd5172ead5 Fix JRUBY-5289: Thread#priority should default to zero
373815bee9 Fix for JRUBY-5093:
d9f4b889d9 Add a fix for JRUBY-5076
25b848f5fc Fix for JRUBY-4767: JRuby and open-uri File handle issue
a4d33150bc Final fixes and test for JRUBY-4264: threadContextMap leaks RubyThread on death of adopted thread
520454f6ec Fix for JRUBY-3928: Net::HTTP doesn't timeout as expected when using timeout.rb
6ce486ad82 Fix for JRUBY-3799: Bug in native timeout module causes hang when waiting on IO
0fe97c2c5d Fix for JRUBY-3740: Thread#wakeup not working
   Bugs after [       0]:


Start block index: 709
End block index: 718
    public synchronized IRubyObject status() {
        if (threadImpl.isAlive()) {
            // TODO: no java stringity
            return getRuntime().newString(status.toString().toLowerCase());
        } else if (exitingException != null) {
            return getRuntime().getNil();
        } else {
            return getRuntime().getFalse();
        }
    }
