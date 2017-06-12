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
        } else if (fd.chNative != null) {
            // native channel, use native lseek
            int ret = posix.lseek(fd.chNative.getFD(), offset, type);
            if (ret < 0) errno = Errno.valueOf(posix.errno());
            return ret;
        }

        // For other channel types, we can't get at a native descriptor to lseek, and we can't use FileChannel
        // .position, so we have to treat them as unseekable and raise EPIPE
        errno = Errno.EPIPE;
        return -1;
    }
