File path: src/org/jruby/javasupport/JavaInterfaceTemplate.java
Comment: / TODO: WRONG - get interfaces from class
Initial commit id: 18d03923
Final commit id: 557c40cd
   Bugs between [       0]:

   Bugs after [       0]:


Start block index: 191
End block index: 206
                    public IRubyObject call(ThreadContext context, IRubyObject self, RubyModule clazz, String name, IRubyObject arg) {
                        // TODO: WRONG - get interfaces from class
                        if (arg.respondsTo("java_object")) {
                            IRubyObject interfaces = self.getMetaClass().getInstanceVariables().fastGetInstanceVariable("@java_interfaces");
                            assert interfaces instanceof RubyArray : "interface list was not an array";

                            return context.getRuntime().newBoolean(((RubyArray)interfaces)
                                    .op_diff(
                                        ((JavaClass)
                                            ((JavaObject)arg.dataGetStruct()).java_class()
                                        ).interfaces()
                                    ).equals(RubyArray.newArray(context.getRuntime())));
                        } else {
                            return RuntimeHelpers.invoke(context, self, "old_eqq", arg);
                        }
                    }
