File path: src/org/jruby/runtime/Iter.java
Comment: IXME convert to enum ?
Initial commit id: 1c1c8553
Final commit id: 24befe5c
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 33
End block index: 70
 * @author  jpetersen
 * FIXME convert to enum ?
 */
public final class Iter {
    /** No block given */
    public static final Iter ITER_NOT = new Iter("NOT");
    /** Block given before last method call ("previous") */
    public static final Iter ITER_PRE = new Iter("PRE");
    /** Is currently a block*/
    public static final Iter ITER_CUR = new Iter("CUR");

    private final String debug;

    private Iter(final String debug) {
        this.debug = debug;
    }

    public final boolean isNot() {
        return this == ITER_NOT;
    }

    public final boolean isPre() {
        return this == ITER_PRE;
    }

    public final boolean isCur() {
        return this == ITER_CUR;
    }

    public boolean isBlockGiven() {
        return !isNot();
    }

    @Override
    public String toString() {
        return debug;
    }
}
