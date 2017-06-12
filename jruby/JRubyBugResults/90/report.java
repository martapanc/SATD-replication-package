File path: core/src/main/java/org/jruby/util/io/PosixShim.java
Comment: / TODO: It's perhaps just a coincidence that all the channels for
Initial commit id: ecc42a6c
Final commit id: 16c98901
   Bugs between [       0]:

   Bugs after [       3]:
d9090c3aa9 Use lseekLong to get lseek return value. Fixes #3817.
623bc59746 Only treate -1 ret from lseek as error. Fixes #3435.
f156123392 Only set EAGAIN when written == 0 and length > 0. Fixes #2957

Start block index: 33
End block index: 67
    public long lseek(ChannelFD fd, long offset, int type) {
        clear();

        if (fd.chSeek != null) {
            int adj = 0;
            try {
                switch (type) {
                    case SEEK_SET:
                        return fd.chSeek.position(offset).position();
                    case SEEK_CUR:
                        return fd.chSeek.position(fd.chSeek.position() - adj + offset).position();
                    case SEEK_END:
                        return fd.chSeek.position(fd.chSeek.size() + offset).position();
                    default:
                        errno = Errno.EINVAL;
                        return -1;
                }
            } catch (IllegalArgumentException e) {
                errno = Errno.EINVAL;
                return -1;
            } catch (IOException ioe) {
                errno = Helpers.errnoFromException(ioe);
                return -1;
            }
        } else if (fd.chSelect != null) {
            // TODO: It's perhaps just a coincidence that all the channels for
            // which we should raise are instanceof SelectableChannel, since
            // stdio is not...so this bothers me slightly. -CON
            errno = Errno.EPIPE;
            return -1;
        } else {
            errno = Errno.EPIPE;
            return -1;
        }
    }
