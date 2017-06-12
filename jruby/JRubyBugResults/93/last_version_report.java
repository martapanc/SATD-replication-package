    public String toString() {
        StringBuilder buf = new StringBuilder("ModeFlags: ");
        
        if (isAppendable()) buf.append("APPENDABLE ");
        if (isBinary()) buf.append("BINARY ");
        if (isCreate()) buf.append("CREATE ");
        if (isExclusive()) buf.append("EXCLUSIVE ");
        if (isReadOnly()) buf.append("READONLY ");
        if (isText()) buf.append("TEXT ");
        if (isTruncate()) buf.append("TRUNCATE ");
        if (isWritable()) buf.append("WRITABLE ");
        
        return buf.toString();
    }
