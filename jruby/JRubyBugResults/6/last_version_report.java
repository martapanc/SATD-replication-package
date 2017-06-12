    private BlockBody createBlockBody(ThreadContext context, int index, String descriptor) throws NumberFormatException {
        String[] firstSplit = descriptor.split(",");
        String[] secondSplit;

        if (firstSplit[2].length() == 0) {
            secondSplit = new String[0];
        } else {
            secondSplit = firstSplit[2].split(";");

            // FIXME: Big fat hack here, because scope names are expected to be interned strings by the parser
            for (int i = 0; i < secondSplit.length; i++) {
                secondSplit[i] = secondSplit[i].intern();
            }
        }

        BlockBody body = RuntimeHelpers.createCompiledBlockBody(
                context,
                this,
                firstSplit[0],
                Integer.parseInt(firstSplit[1]),
                secondSplit,
                Boolean.valueOf(firstSplit[3]),
                Integer.parseInt(firstSplit[4]),
                Boolean.valueOf(firstSplit[5]));
        return blockBodies[index] = body;
    }
