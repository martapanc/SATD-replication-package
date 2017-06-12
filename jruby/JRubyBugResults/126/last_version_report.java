    private IRubyObject read19(IRubyObject[] args) {
        checkReadable();

        Ruby runtime = getRuntime();
        IRubyObject str = runtime.getNil();
        int len = 0;
        boolean binary = false;

        switch (args.length) {
        case 2:
            str = args[1];
            if (!str.isNil()) {
                str = str.convertToString();
                ((RubyString)str).modify();
            }
        case 1:
            if (!args[0].isNil()) {
                len = RubyNumeric.fix2int(args[0]);

                if (len < 0) {
                    throw getRuntime().newArgumentError("negative length " + len + " given");
                }
                if ((len > 0 && isEndOfString()) || data.eof) {
                    data.eof = true;
                    if (!str.isNil()) ((RubyString)str).resize(0);
                    return getRuntime().getNil();
                }
                binary = true;
                break;
            }
        case 0:
            len = data.internal.getByteList().length();

            if (len <= data.pos) {
                data.eof = true;
                if (str.isNil()) {
                    str = runtime.newString();
                } else {
                    ((RubyString)str).resize(0);
                }

                return str;
            } else {
                len -= data.pos;
            }
            break;
        default:
            getRuntime().newArgumentError(args.length, 0);
        }

        if (str.isNil()) {
            str = strioSubstr(runtime, data.pos, len);
            if (binary) ((RubyString)str).setEncoding(ASCIIEncoding.INSTANCE);
        } else {
            int rest = data.internal.size() - data.pos;
            if (len > rest) len = rest;
            ((RubyString)str).resize(len);
            ByteList strByteList = ((RubyString)str).getByteList();
            byte[] strBytes = strByteList.getUnsafeBytes();
            ByteList dataByteList = data.internal.getByteList();
            byte[] dataBytes = dataByteList.getUnsafeBytes();
            System.arraycopy(dataBytes, dataByteList.getBegin() + data.pos, strBytes, strByteList.getBegin(), len);
            if (binary) {
                ((RubyString)str).setEncoding(ASCIIEncoding.INSTANCE);
            } else {
                ((RubyString)str).setEncoding(data.internal.getEncoding());
            }
        }
        data.pos += ((RubyString)str).size();
        return str;
    }
