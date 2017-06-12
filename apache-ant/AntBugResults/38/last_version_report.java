public void printElementDecl(PrintWriter out, Project p,
                             String name, Class<?> element) {

    if (visited.containsKey(name)) {
        return;
    }
    visited.put(name, "");

    IntrospectionHelper ih = null;
    try {
        ih = IntrospectionHelper.getHelper(p, element);
    } catch (Throwable t) {
        /*
         * TODO - failed to load the class properly.
         *
         * should we print a warning here?
         */
        return;
    }

    StringBuffer sb = new StringBuffer("<!ELEMENT ");
    sb.append(name).append(" ");

    if (org.apache.tools.ant.types.Reference.class.equals(element)) {
        sb.append("EMPTY>").append(LINE_SEP);
        sb.append("<!ATTLIST ").append(name);
        sb.append(LINE_SEP).append("          id ID #IMPLIED");
        sb.append(LINE_SEP).append("          refid IDREF #IMPLIED");
        sb.append(">").append(LINE_SEP);
        out.println(sb);
        return;
    }

    Vector<String> v = new Vector<String>();
    if (ih.supportsCharacters()) {
        v.addElement("#PCDATA");
    }

    if (TaskContainer.class.isAssignableFrom(element)) {
        v.addElement(TASKS);
    }

    Enumeration<String> e = ih.getNestedElements();
    while (e.hasMoreElements()) {
        v.addElement(e.nextElement());
    }

    if (v.isEmpty()) {
        sb.append("EMPTY");
    } else {
        sb.append("(");
        final int count = v.size();
        for (int i = 0; i < count; i++) {
            if (i != 0) {
                sb.append(" | ");
            }
            sb.append(v.elementAt(i));
        }
        sb.append(")");
        if (count > 1 || !v.elementAt(0).equals("#PCDATA")) {
            sb.append("*");
        }
    }
    sb.append(">");
    out.println(sb);

    sb = new StringBuffer("<!ATTLIST ");
    sb.append(name);
    sb.append(LINE_SEP).append("          id ID #IMPLIED");

    e = ih.getAttributes();
    while (e.hasMoreElements()) {
        String attrName = (String) e.nextElement();
        if ("id".equals(attrName)) {
            continue;
        }

        sb.append(LINE_SEP).append("          ")
            .append(attrName).append(" ");
        Class<?> type = ih.getAttributeType(attrName);
        if (type.equals(java.lang.Boolean.class)
            || type.equals(java.lang.Boolean.TYPE)) {
            sb.append(BOOLEAN).append(" ");
        } else if (Reference.class.isAssignableFrom(type)) {
            sb.append("IDREF ");
        } else if (EnumeratedAttribute.class.isAssignableFrom(type)) {
            try {
                EnumeratedAttribute ea =
                    (EnumeratedAttribute) type.newInstance();
                String[] values = ea.getValues();
                if (values == null
                    || values.length == 0
                    || !areNmtokens(values)) {
                    sb.append("CDATA ");
                } else {
                    sb.append("(");
                    for (int i = 0; i < values.length; i++) {
                        if (i != 0) {
                            sb.append(" | ");
                        }
                        sb.append(values[i]);
                    }
                    sb.append(") ");
                }
            } catch (InstantiationException ie) {
                sb.append("CDATA ");
            } catch (IllegalAccessException ie) {
                sb.append("CDATA ");
            }
        } else if (type.getSuperclass() != null
                   && type.getSuperclass().getName().equals("java.lang.Enum")) {
            try {
                Object[] values = (Object[]) type.getMethod("values", (Class[])  null)
                    .invoke(null, (Object[]) null);
                if (values.length == 0) {
                    sb.append("CDATA ");
                } else {
                    sb.append('(');
                    for (int i = 0; i < values.length; i++) {
                        if (i != 0) {
                            sb.append(" | ");
                        }
                        sb.append(type.getMethod("name", (Class[]) null)
                                  .invoke(values[i], (Object[]) null));
                    }
                    sb.append(") ");
                }
            } catch (Exception x) {
                sb.append("CDATA ");
            }
        } else {
            sb.append("CDATA ");
        }
        sb.append("#IMPLIED");
    }
    sb.append(">").append(LINE_SEP);
    out.println(sb);

    final int count = v.size();
    for (int i = 0; i < count; i++) {
        String nestedName = (String) v.elementAt(i);
        if (!"#PCDATA".equals(nestedName)
            && !TASKS.equals(nestedName)
            && !TYPES.equals(nestedName)) {
            printElementDecl(out, p, nestedName, ih.getElementType(nestedName));
        }
    }
}
