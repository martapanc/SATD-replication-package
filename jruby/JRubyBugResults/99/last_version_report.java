public JavaObject static_value() {

    if (Ruby.isSecurityRestricted())
        return null;
    else {
        try {
            // TODO: Only setAccessible to account for pattern found by
            // accessing constants included from a non-public interface.
            // (aka java.util.zip.ZipConstants being implemented by many
            // classes)
            field.setAccessible(true);
            return JavaObject.wrap(getRuntime(), field.get(null));
        } catch (IllegalAccessException iae) {
            throw getRuntime().newTypeError("illegal static value access: " + iae.getMessage());
        }
    }
}
