File path: core/src/main/java/org/jruby/javasupport/binding/ConstantField.java
Comment: / TODO: catch exception if constant is already set by other
Initial commit id: 5755383a
Final commit id: 55927e21
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 19
End block index: 25
public class ConstantField {
    static final int CONSTANT = Modifier.FINAL | Modifier.PUBLIC | Modifier.STATIC;
    final Field field;
    public ConstantField(Field field) {
        this.field = field;
    }
    void install(final RubyModule proxy) {
        if (proxy.getConstantAt(field.getName()) == null) {
            // TODO: catch exception if constant is already set by other
            // thread
            try {
                proxy.setConstant(field.getName(), JavaUtil.convertJavaToUsableRubyObject(proxy.getRuntime(), field.get(null)));
            } catch (IllegalAccessException iae) {
                // if we can't read it, we don't set it
            }
        }
    }
    static boolean isConstant(final Field field) {
        return (field.getModifiers() & CONSTANT) == CONSTANT &&
            Character.isUpperCase(field.getName().charAt(0));
    }
}
