File path: core/src/main/java/org/jruby/ext/stringio/RubyStringIO.java
Comment: / Yow...this is still ugly
Initial commit id: e14ea63b
Final commit id: 95db8a8b
   Bugs between [       0]:

   Bugs after [       1]:
6e4c3d38f4 StringIO#read of frozen string should not raise. Fixes #1008.

Start block index: 955
End block index: 1056
    private IRubyObject read19(IRubyObject[] args) {
        checkReadable();

        ByteList buf = null;
        int length = 0;
        boolean lengthGiven = false;
        int oldLength = 0;
        RubyString originalString = null;

        switch (args.length) {
        case 2:
            if (!args[1].isNil()) {
                originalString = args[1].convertToString();
                // must let original string know we're modifying, so shared buffers aren't damaged
                originalString.modify();
                buf = originalString.getByteList();
            }
        case 1:
            if (!args[0].isNil()) {
                length = RubyNumeric.fix2int(args[0]);
                lengthGiven = true;
                oldLength = length;

                if (length < 0) {
                    throw getRuntime().newArgumentError("negative length " + length + " given");
                }
                if (length > 0 && isEndOfString()) {
                    data.eof = true;
                    if (buf != null) buf.setRealSize(0);
                    return getRuntime().getNil();
                } else if (data.eof) {
                    if (buf != null) buf.setRealSize(0);
                    return getRuntime().getNil();
                }
                break;
            }
        case 0:
            oldLength = -1;
            length = data.internal.getByteList().length();

            if (length <= data.pos) {
                data.eof = true;
                if (buf == null) {
                    buf = new ByteList();
                } else {
                    buf.setRealSize(0);
                }

                return makeString(getRuntime(), buf, true);
            } else {
                length -= data.pos;
            }
            break;
        default:
            getRuntime().newArgumentError(args.length, 0);
        }

        if (buf == null) {
            int internalLength = data.internal.getByteList().length();

            if (internalLength > 0) {
                if (internalLength >= data.pos + length) {
                    buf = new ByteList(data.internal.getByteList(), (int) data.pos, length);
                } else {
                    int rest = (int) (data.internal.getByteList().length() - data.pos);

                    if (length > rest) length = rest;
                    buf = new ByteList(data.internal.getByteList(), (int) data.pos, length);
                }
            }
        } else {
            int rest = (int) (data.internal.getByteList().length() - data.pos);

            if (length > rest) length = rest;

            // Yow...this is still ugly
            byte[] target = buf.getUnsafeBytes();
            if (target.length > length) {
                System.arraycopy(data.internal.getByteList().getUnsafeBytes(), (int) data.pos, target, 0, length);
                buf.setBegin(0);
                buf.setRealSize(length);
            } else {
                target = new byte[length];
                System.arraycopy(data.internal.getByteList().getUnsafeBytes(), (int) data.pos, target, 0, length);
                buf.setBegin(0);
                buf.setRealSize(length);
                buf.setUnsafeBytes(target);
            }
        }

        if (buf == null) {
            if (!data.eof) buf = new ByteList();
            length = 0;
        } else {
            length = buf.length();
            data.pos += length;
        }

        if (oldLength < 0 || oldLength > length) data.eof = true;

        return originalString != null ? originalString : makeString(getRuntime(), buf, !lengthGiven);
    }
