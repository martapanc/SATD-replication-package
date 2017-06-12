File path: src/org/jruby/ast/executable/AbstractScript.java
Comment: / FIXME: Big fat hack here
Initial commit id: 0b01fd39
Final commit id: 04602b8b
   Bugs between [       0]:

   Bugs after [       2]:
36dd3a0424 Fix JRUBY-4339: Kernel.load with wrap=true does not protect the global namespace of calling program
ea7757221b Fix for JRUBY-4037: Ruby 1.8 compatibility: /#{/\w/}/uo does not work as expected

Start block index: 174
End block index: 193
    private BlockBody createBlockBody(ThreadContext context, int index, String descriptor) throws NumberFormatException {
        String[] firstSplit = descriptor.split(",");
        String[] secondSplit = firstSplit[2].split(";");

        // FIXME: Big fat hack here, because scope names are expected to be interned strings by the parser
        for (int i = 0; i < secondSplit.length; i++) {
            secondSplit[i] = secondSplit[i].intern();
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
