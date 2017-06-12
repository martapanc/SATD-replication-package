diff --git a/src/org/jruby/javasupport/util/RuntimeHelpers.java b/src/org/jruby/javasupport/util/RuntimeHelpers.java
index b383831cf3..c481ec0fda 100644
--- a/src/org/jruby/javasupport/util/RuntimeHelpers.java
+++ b/src/org/jruby/javasupport/util/RuntimeHelpers.java
@@ -1459,1237 +1459,1237 @@ public class RuntimeHelpers {
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five, String six, String seven, String eight) {
         return new String[] {one, two, three, four, five, six, seven, eight};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five, String six, String seven, String eight, String nine) {
         return new String[] {one, two, three, four, five, six, seven, eight, nine};
     }
     
     public static String[] constructStringArray(String one, String two, String three, String four, String five, String six, String seven, String eight, String nine, String ten) {
         return new String[] {one, two, three, four, five, six, seven, eight, nine, ten};
     }
     
     public static final int MAX_SPECIFIC_ARITY_HASH = 3;
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString(runtime, key1, value1);
         return hash;
     }
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString(runtime, key1, value1);
         hash.fastASetCheckString(runtime, key2, value2);
         return hash;
     }
     
     public static RubyHash constructHash(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2, IRubyObject key3, IRubyObject value3) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString(runtime, key1, value1);
         hash.fastASetCheckString(runtime, key2, value2);
         hash.fastASetCheckString(runtime, key3, value3);
         return hash;
     }
     
     public static RubyHash constructHash19(Ruby runtime, IRubyObject key1, IRubyObject value1) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString19(runtime, key1, value1);
         return hash;
     }
     
     public static RubyHash constructHash19(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString19(runtime, key1, value1);
         hash.fastASetCheckString19(runtime, key2, value2);
         return hash;
     }
     
     public static RubyHash constructHash19(Ruby runtime, IRubyObject key1, IRubyObject value1, IRubyObject key2, IRubyObject value2, IRubyObject key3, IRubyObject value3) {
         RubyHash hash = RubyHash.newHash(runtime);
         hash.fastASetCheckString19(runtime, key1, value1);
         hash.fastASetCheckString19(runtime, key2, value2);
         hash.fastASetCheckString19(runtime, key3, value3);
         return hash;
     }
 
     public static IRubyObject undefMethod(ThreadContext context, Object nameArg) {
         RubyModule module = context.getRubyClass();
 
         String name = (nameArg instanceof String) ?
             (String) nameArg : nameArg.toString();
 
         if (module == null) {
             throw context.getRuntime().newTypeError("No class to undef method '" + name + "'.");
         }
 
         module.undef(context, name);
 
         return context.getRuntime().getNil();
     }
     
     public static IRubyObject defineAlias(ThreadContext context, IRubyObject self, Object newNameArg, Object oldNameArg) {
         Ruby runtime = context.getRuntime();
         RubyModule module = context.getRubyClass();
    
         if (module == null || self instanceof RubyFixnum || self instanceof RubySymbol){
             throw runtime.newTypeError("no class to make alias");
         }
 
         String newName = (newNameArg instanceof String) ?
             (String) newNameArg : newNameArg.toString();
         String oldName = (oldNameArg instanceof String) ? 
             (String) oldNameArg : oldNameArg.toString();
 
         module.defineAlias(newName, oldName);
         module.callMethod(context, "method_added", runtime.newSymbol(newName));
    
         return runtime.getNil();
     }
     
     public static IRubyObject negate(IRubyObject value, Ruby runtime) {
         if (value.isTrue()) return runtime.getFalse();
         return runtime.getTrue();
     }
     
     public static IRubyObject stringOrNil(ByteList value, ThreadContext context) {
         if (value == null) return context.nil;
         return RubyString.newStringShared(context.runtime, value);
     }
     
     public static void preLoad(ThreadContext context, String[] varNames) {
         StaticScope staticScope = context.getRuntime().getStaticScopeFactory().newLocalScope(null, varNames);
         preLoadCommon(context, staticScope, false);
     }
 
     public static void preLoad(ThreadContext context, String scopeString, boolean wrap) {
         StaticScope staticScope = decodeRootScope(context, scopeString);
         preLoadCommon(context, staticScope, wrap);
     }
 
     private static void preLoadCommon(ThreadContext context, StaticScope staticScope, boolean wrap) {
         RubyClass objectClass = context.getRuntime().getObject();
         IRubyObject topLevel = context.getRuntime().getTopSelf();
         if (wrap) {
             staticScope.setModule(RubyModule.newModule(context.runtime));
         } else {
             staticScope.setModule(objectClass);
         }
         DynamicScope scope = DynamicScope.newDynamicScope(staticScope);
 
         // Each root node has a top-level scope that we need to push
         context.preScopedBody(scope);
         context.preNodeEval(objectClass, topLevel);
     }
     
     public static void postLoad(ThreadContext context) {
         context.postNodeEval();
         context.postScopedBody();
     }
     
     public static void registerEndBlock(Block block, Ruby runtime) {
         runtime.pushExitBlock(runtime.newProc(Block.Type.LAMBDA, block));
     }
     
     public static IRubyObject match3(RubyRegexp regexp, IRubyObject value, ThreadContext context) {
         if (value instanceof RubyString) {
             return regexp.op_match(context, value);
         } else {
             return value.callMethod(context, "=~", regexp);
         }
     }
 
     public static IRubyObject match3_19(RubyRegexp regexp, IRubyObject value, ThreadContext context) {
         if (value instanceof RubyString) {
             return regexp.op_match19(context, value);
         } else {
             return value.callMethod(context, "=~", regexp);
         }
     }
     
     public static IRubyObject getErrorInfo(Ruby runtime) {
         return runtime.getGlobalVariables().get("$!");
     }
     
     public static void setErrorInfo(Ruby runtime, IRubyObject error) {
         runtime.getGlobalVariables().set("$!", error);
     }
 
     public static IRubyObject setLastLine(Ruby runtime, ThreadContext context, IRubyObject value) {
         return context.getCurrentScope().setLastLine(value);
     }
 
     public static IRubyObject getLastLine(Ruby runtime, ThreadContext context) {
         return context.getCurrentScope().getLastLine(runtime);
     }
 
     public static IRubyObject setBackref(Ruby runtime, ThreadContext context, IRubyObject value) {
         if (!value.isNil() && !(value instanceof RubyMatchData)) throw runtime.newTypeError(value, runtime.getMatchData());
         return context.getCurrentScope().setBackRef(value);
     }
 
     public static IRubyObject getBackref(Ruby runtime, ThreadContext context) {
         IRubyObject backref = context.getCurrentScope().getBackRef(runtime);
         if (backref instanceof RubyMatchData) ((RubyMatchData)backref).use();
         return backref;
     }
     
     public static IRubyObject preOpAsgnWithOrAnd(IRubyObject receiver, ThreadContext context, IRubyObject self, CallSite varSite) {
         return varSite.call(context, self, receiver);
     }
     
     public static IRubyObject postOpAsgnWithOrAnd(IRubyObject receiver, IRubyObject value, ThreadContext context, IRubyObject self, CallSite varAsgnSite) {
         varAsgnSite.call(context, self, receiver, value);
         return value;
     }
     
     public static IRubyObject opAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject arg, CallSite varSite, CallSite opSite, CallSite opAsgnSite) {
         IRubyObject var = varSite.call(context, self, receiver);
         IRubyObject result = opSite.call(context, self, var, arg);
         opAsgnSite.call(context, self, receiver, result);
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, self, receiver);
         IRubyObject result = opSite.call(context, self, var, value);
         elementAsgnSite.call(context, self, receiver, result);
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject arg, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, self, receiver, arg);
         IRubyObject result = opSite.call(context, self, var, value);
         elementAsgnSite.call(context, self, receiver, arg, result);
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject arg1, IRubyObject arg2, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, self, receiver, arg1, arg2);
         IRubyObject result = opSite.call(context, self, var, value);
         elementAsgnSite.call(context, self, receiver, arg1, arg2, result);
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject arg1, IRubyObject arg2, IRubyObject arg3, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, self, receiver, arg1, arg2, arg3);
         IRubyObject result = opSite.call(context, self, var, value);
         elementAsgnSite.call(context, self, receiver, new IRubyObject[] {arg1, arg2, arg3, result});
 
         return result;
     }
     
     public static IRubyObject opElementAsgnWithMethod(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject[] args, IRubyObject value, CallSite elementSite, CallSite opSite, CallSite elementAsgnSite) {
         IRubyObject var = elementSite.call(context, self, receiver);
         IRubyObject result = opSite.call(context, self, var, value);
         elementAsgnSite.call(context, self, receiver, appendToObjectArray(args, result));
 
         return result;
     }
 
     
     public static IRubyObject opElementAsgnWithOrPartTwoOneArg(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject arg, IRubyObject value, CallSite asetSite) {
         asetSite.call(context, self, receiver, arg, value);
         return value;
     }
     
     public static IRubyObject opElementAsgnWithOrPartTwoTwoArgs(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject[] args, IRubyObject value, CallSite asetSite) {
         asetSite.call(context, self, receiver, args[0], args[1], value);
         return value;
     }
     
     public static IRubyObject opElementAsgnWithOrPartTwoThreeArgs(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject[] args, IRubyObject value, CallSite asetSite) {
         asetSite.call(context, self, receiver, new IRubyObject[] {args[0], args[1], args[2], value});
         return value;
     }
     
     public static IRubyObject opElementAsgnWithOrPartTwoNArgs(ThreadContext context, IRubyObject self, IRubyObject receiver, IRubyObject[] args, IRubyObject value, CallSite asetSite) {
         IRubyObject[] newArgs = new IRubyObject[args.length + 1];
         System.arraycopy(args, 0, newArgs, 0, args.length);
         newArgs[args.length] = value;
         asetSite.call(context, self, receiver, newArgs);
         return value;
     }
 
     public static RubyArray arrayValue(IRubyObject value) {
         Ruby runtime = value.getRuntime();
         return arrayValue(runtime.getCurrentContext(), runtime, value);
     }
     
     public static RubyArray arrayValue(ThreadContext context, Ruby runtime, IRubyObject value) {
         IRubyObject tmp = value.checkArrayType();
 
         if (tmp.isNil()) {
             // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can
             // remove this hack too.
 
             if (value.respondsTo("to_a") && value.getMetaClass().searchMethod("to_a").getImplementationClass() != runtime.getKernel()) {
                 IRubyObject avalue = value.callMethod(context, "to_a");
                 if (!(avalue instanceof RubyArray)) {
                     if (runtime.is1_9() && avalue.isNil()) {
                         return runtime.newArray(value);
                     } else {
                         throw runtime.newTypeError("`to_a' did not return Array");
                     }
                 }
                 return (RubyArray)avalue;
             } else {
                 return runtime.newArray(value);
             }
         }
         return (RubyArray)tmp;
     }
 
     public static IRubyObject aryToAry(IRubyObject value) {
         if (value instanceof RubyArray) return value;
 
         if (value.respondsTo("to_ary")) {
             return TypeConverter.convertToType(value, value.getRuntime().getArray(), "to_ary", false);
         }
 
         return value.getRuntime().newArray(value);
     }
 
     public static IRubyObject aValueSplat(IRubyObject value) {
         if (!(value instanceof RubyArray) || ((RubyArray) value).length().getLongValue() == 0) {
             return value.getRuntime().getNil();
         }
 
         RubyArray array = (RubyArray) value;
 
         return array.getLength() == 1 ? array.first() : array;
     }
 
     public static IRubyObject aValueSplat19(IRubyObject value) {
         if (!(value instanceof RubyArray)) {
             return value.getRuntime().getNil();
         }
 
         return (RubyArray) value;
     }
 
     public static RubyArray splatValue(IRubyObject value) {
         if (value.isNil()) {
             return value.getRuntime().newArray(value);
         }
 
         return arrayValue(value);
     }
 
     public static RubyArray splatValue19(IRubyObject value) {
         if (value.isNil()) {
             return value.getRuntime().newEmptyArray();
         }
 
         return arrayValue(value);
     }
     
     public static IRubyObject unsplatValue19(IRubyObject argsResult) {
         if (argsResult instanceof RubyArray) {
             RubyArray array = (RubyArray) argsResult;
                     
             if (array.size() == 1) {
                 IRubyObject newResult = array.eltInternal(0);
                 if (!((newResult instanceof RubyArray) && ((RubyArray) newResult).size() == 0)) {
                     argsResult = newResult;
                 }
             }
         }        
         return argsResult;
     }
         
     public static IRubyObject[] splatToArguments(IRubyObject value) {
         Ruby runtime = value.getRuntime();
         
         if (value.isNil()) {
             return runtime.getSingleNilArray();
         }
         
         return splatToArgumentsCommon(runtime, value);
     }
     
     public static IRubyObject[] splatToArguments19(IRubyObject value) {
         Ruby runtime = value.getRuntime();
         
         if (value.isNil()) {
             return IRubyObject.NULL_ARRAY;
         }
         
         return splatToArgumentsCommon(runtime, value);
     }
     
     private static IRubyObject[] splatToArgumentsCommon(Ruby runtime, IRubyObject value) {
         
         if (value.isNil()) {
             return runtime.getSingleNilArray();
         }
         
         IRubyObject tmp = value.checkArrayType();
 
         if (tmp.isNil()) {
             return convertSplatToJavaArray(runtime, value);
         }
         return ((RubyArray)tmp).toJavaArrayMaybeUnsafe();
     }
     
     private static IRubyObject[] convertSplatToJavaArray(Ruby runtime, IRubyObject value) {
         // Object#to_a is obsolete.  We match Ruby's hack until to_a goes away.  Then we can
         // remove this hack too.
 
         RubyClass metaClass = value.getMetaClass();
         DynamicMethod method = metaClass.searchMethod("to_a");
         if (method.isUndefined() || method.getImplementationClass() == runtime.getKernel()) {
             return new IRubyObject[] {value};
         }
 
         IRubyObject avalue = method.call(runtime.getCurrentContext(), value, metaClass, "to_a");
         if (!(avalue instanceof RubyArray)) {
             if (runtime.is1_9() && avalue.isNil()) {
                 return new IRubyObject[] {value};
             } else {
                 throw runtime.newTypeError("`to_a' did not return Array");
             }
         }
         return ((RubyArray)avalue).toJavaArray();
     }
     
     public static IRubyObject[] argsCatToArguments(IRubyObject[] args, IRubyObject cat) {
         IRubyObject[] ary = splatToArguments(cat);
         return argsCatToArgumentsCommon(args, ary, cat);
     }
     
     public static IRubyObject[] argsCatToArguments19(IRubyObject[] args, IRubyObject cat) {
         IRubyObject[] ary = splatToArguments19(cat);
         return argsCatToArgumentsCommon(args, ary, cat);
     }
     
     private static IRubyObject[] argsCatToArgumentsCommon(IRubyObject[] args, IRubyObject[] ary, IRubyObject cat) {
         if (ary.length > 0) {
             IRubyObject[] newArgs = new IRubyObject[args.length + ary.length];
             System.arraycopy(args, 0, newArgs, 0, args.length);
             System.arraycopy(ary, 0, newArgs, args.length, ary.length);
             args = newArgs;
         }
         
         return args;
     }
 
     public static void addInstanceMethod(RubyModule containingClass, String name, DynamicMethod method, Visibility visibility, ThreadContext context, Ruby runtime) {
         containingClass.addMethod(name, method);
 
         RubySymbol sym = runtime.fastNewSymbol(name);
         if (visibility == Visibility.MODULE_FUNCTION) {
             addModuleMethod(containingClass, name, method, context, sym);
         }
 
         callNormalMethodHook(containingClass, context, sym);
     }
 
     private static void addModuleMethod(RubyModule containingClass, String name, DynamicMethod method, ThreadContext context, RubySymbol sym) {
         containingClass.getSingletonClass().addMethod(name, new WrapperMethod(containingClass.getSingletonClass(), method, Visibility.PUBLIC));
         containingClass.callMethod(context, "singleton_method_added", sym);
     }
 
     private static void callNormalMethodHook(RubyModule containingClass, ThreadContext context, RubySymbol name) {
         // 'class << state.self' and 'class << obj' uses defn as opposed to defs
         if (containingClass.isSingleton()) {
             callSingletonMethodHook(((MetaClass) containingClass).getAttached(), context, name);
         } else {
             containingClass.callMethod(context, "method_added", name);
         }
     }
 
     private static void callSingletonMethodHook(IRubyObject receiver, ThreadContext context, RubySymbol name) {
         receiver.callMethod(context, "singleton_method_added", name);
     }
 
     private static DynamicMethod constructNormalMethod(
             MethodFactory factory,
             String javaName,
             String name,
             RubyModule containingClass,
             ISourcePosition position,
             int arity,
             StaticScope scope,
             Visibility visibility,
             Object scriptObject,
             CallConfiguration callConfig,
             String parameterDesc) {
         
         DynamicMethod method;
 
         if (name.equals("initialize") || name.equals("initialize_copy") || visibility == Visibility.MODULE_FUNCTION) {
             visibility = Visibility.PRIVATE;
         }
         
         if (RubyInstanceConfig.LAZYHANDLES_COMPILE) {
             method = factory.getCompiledMethodLazily(
                     containingClass,
                     javaName,
                     Arity.createArity(arity),
                     visibility,
                     scope,
                     scriptObject,
                     callConfig,
                     position,
                     parameterDesc);
         } else {
             method = factory.getCompiledMethod(
                     containingClass,
                     javaName,
                     Arity.createArity(arity),
                     visibility,
                     scope,
                     scriptObject,
                     callConfig,
                     position,
                     parameterDesc);
         }
 
         return method;
     }
 
     private static DynamicMethod constructSingletonMethod(
             MethodFactory factory,
             String javaName,
             RubyClass rubyClass,
             ISourcePosition position,
             int arity,
             StaticScope scope,
             Object scriptObject,
             CallConfiguration callConfig,
             String parameterDesc) {
         
         if (RubyInstanceConfig.LAZYHANDLES_COMPILE) {
             return factory.getCompiledMethodLazily(
                     rubyClass,
                     javaName,
                     Arity.createArity(arity),
                     Visibility.PUBLIC,
                     scope,
                     scriptObject,
                     callConfig,
                     position,
                     parameterDesc);
         } else {
             return factory.getCompiledMethod(
                     rubyClass,
                     javaName,
                     Arity.createArity(arity),
                     Visibility.PUBLIC,
                     scope,
                     scriptObject,
                     callConfig,
                     position,
                     parameterDesc);
         }
     }
 
     public static String encodeScope(StaticScope scope) {
         StringBuilder namesBuilder = new StringBuilder();
 
         boolean first = true;
         for (String name : scope.getVariables()) {
             if (!first) namesBuilder.append(';');
             first = false;
             namesBuilder.append(name);
         }
         namesBuilder
                 .append(',')
                 .append(scope.getRequiredArgs())
                 .append(',')
                 .append(scope.getOptionalArgs())
                 .append(',')
                 .append(scope.getRestArg());
 
         return namesBuilder.toString();
     }
 
     public static StaticScope decodeRootScope(ThreadContext context, String scopeString) {
         String[][] decodedScope = decodeScopeDescriptor(scopeString);
         StaticScope scope = context.getRuntime().getStaticScopeFactory().newLocalScope(null, decodedScope[1]);
         setAritiesFromDecodedScope(scope, decodedScope[0]);
         return scope;
     }
 
     public static StaticScope decodeLocalScope(ThreadContext context, String scopeString) {
         String[][] decodedScope = decodeScopeDescriptor(scopeString);
         StaticScope scope = context.getRuntime().getStaticScopeFactory().newLocalScope(context.getCurrentScope().getStaticScope(), decodedScope[1]);
         setAritiesFromDecodedScope(scope, decodedScope[0]);
         return scope;
     }
 
     public static StaticScope decodeBlockScope(ThreadContext context, String scopeString) {
         String[][] decodedScope = decodeScopeDescriptor(scopeString);
         StaticScope scope = context.getRuntime().getStaticScopeFactory().newBlockScope(context.getCurrentScope().getStaticScope(), decodedScope[1]);
         setAritiesFromDecodedScope(scope, decodedScope[0]);
         return scope;
     }
 
     private static String[][] decodeScopeDescriptor(String scopeString) {
         String[] scopeElements = scopeString.split(",");
         String[] scopeNames = scopeElements[0].length() == 0 ? new String[0] : getScopeNames(scopeElements[0]);
         return new String[][] {scopeElements, scopeNames};
     }
 
     private static void setAritiesFromDecodedScope(StaticScope scope, String[] scopeElements) {
         scope.setArities(Integer.parseInt(scopeElements[1]), Integer.parseInt(scopeElements[2]), Integer.parseInt(scopeElements[3]));
     }
 
     public static StaticScope createScopeForClass(ThreadContext context, String scopeString) {
         StaticScope scope = decodeLocalScope(context, scopeString);
         scope.determineModule();
 
         return scope;
     }
 
     private static void performNormalMethodChecks(RubyModule containingClass, Ruby runtime, String name) throws RaiseException {
 
         if (containingClass == runtime.getDummy()) {
             throw runtime.newTypeError("no class/module to add method");
         }
 
         if (containingClass == runtime.getObject() && name.equals("initialize")) {
             runtime.getWarnings().warn(ID.REDEFINING_DANGEROUS, "redefining Object#initialize may cause infinite loop");
         }
 
         if (name.equals("__id__") || name.equals("__send__")) {
             runtime.getWarnings().warn(ID.REDEFINING_DANGEROUS, "redefining `" + name + "' may cause serious problem");
         }
     }
 
     private static RubyClass performSingletonMethodChecks(Ruby runtime, IRubyObject receiver, String name) throws RaiseException {
 
         if (runtime.getSafeLevel() >= 4 && !receiver.isTaint()) {
             throw runtime.newSecurityError("Insecure; can't define singleton method.");
         }
 
         if (receiver instanceof RubyFixnum || receiver instanceof RubySymbol) {
             throw runtime.newTypeError("can't define singleton method \"" + name + "\" for " + receiver.getMetaClass().getBaseName());
         }
 
         if (receiver.isFrozen()) {
             throw runtime.newFrozenError("object");
         }
         
         RubyClass rubyClass = receiver.getSingletonClass();
 
         if (runtime.getSafeLevel() >= 4 && rubyClass.getMethods().get(name) != null) {
             throw runtime.newSecurityError("redefining method prohibited.");
         }
         
         return rubyClass;
     }
     
     public static IRubyObject arrayEntryOrNil(RubyArray array, int index) {
         if (index < array.getLength()) {
             return array.eltInternal(index);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayEntryOrNilZero(RubyArray array) {
         if (0 < array.getLength()) {
             return array.eltInternal(0);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayEntryOrNilOne(RubyArray array) {
         if (1 < array.getLength()) {
             return array.eltInternal(1);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayEntryOrNilTwo(RubyArray array) {
         if (2 < array.getLength()) {
             return array.eltInternal(2);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayPostOrNil(RubyArray array, int pre, int post, int index) {
         if (pre + post < array.getLength()) {
             return array.eltInternal(array.getLength() - post + index);
         } else if (pre + index < array.getLength()) {
             return array.eltInternal(pre + index);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayPostOrNilZero(RubyArray array, int pre, int post) {
         if (pre + post < array.getLength()) {
             return array.eltInternal(array.getLength() - post + 0);
         } else if (pre + 0 < array.getLength()) {
             return array.eltInternal(pre + 0);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayPostOrNilOne(RubyArray array, int pre, int post) {
         if (pre + post < array.getLength()) {
             return array.eltInternal(array.getLength() - post + 1);
         } else if (pre + 1 < array.getLength()) {
             return array.eltInternal(pre + 1);
         } else {
             return array.getRuntime().getNil();
         }
     }
 
     public static IRubyObject arrayPostOrNilTwo(RubyArray array, int pre, int post) {
         if (pre + post < array.getLength()) {
             return array.eltInternal(array.getLength() - post + 2);
         } else if (pre + 2 < array.getLength()) {
             return array.eltInternal(pre + 2);
         } else {
             return array.getRuntime().getNil();
         }
     }
     
     public static RubyArray subarrayOrEmpty(RubyArray array, Ruby runtime, int index) {
         if (index < array.getLength()) {
             return createSubarray(array, index);
         } else {
             return RubyArray.newEmptyArray(runtime);
         }
     }
 
     public static RubyArray subarrayOrEmpty(RubyArray array, Ruby runtime, int index, int post) {
         if (index + post < array.getLength()) {
             return createSubarray(array, index, post);
         } else {
             return RubyArray.newEmptyArray(runtime);
         }
     }
     
     public static RubyModule checkIsModule(IRubyObject maybeModule) {
         if (maybeModule instanceof RubyModule) return (RubyModule)maybeModule;
         
         throw maybeModule.getRuntime().newTypeError(maybeModule + " is not a class/module");
     }
     
     public static IRubyObject getGlobalVariable(Ruby runtime, String name) {
         return runtime.getGlobalVariables().get(name);
     }
     
     public static IRubyObject setGlobalVariable(IRubyObject value, Ruby runtime, String name) {
         return runtime.getGlobalVariables().set(name, value);
     }
 
     public static IRubyObject getInstanceVariable(IRubyObject self, Ruby runtime, String internedName) {
         IRubyObject result = self.getInstanceVariables().getInstanceVariable(internedName);
         if (result != null) return result;
         if (runtime.isVerbose()) warnAboutUninitializedIvar(runtime, internedName);
         return runtime.getNil();
     }
 
     private static void warnAboutUninitializedIvar(Ruby runtime, String internedName) {
         runtime.getWarnings().warning(ID.IVAR_NOT_INITIALIZED, "instance variable " + internedName + " not initialized");
     }
 
     public static IRubyObject setInstanceVariable(IRubyObject value, IRubyObject self, String name) {
         return self.getInstanceVariables().setInstanceVariable(name, value);
     }
 
     public static RubyProc newLiteralLambda(ThreadContext context, Block block, IRubyObject self) {
         return RubyProc.newProc(context.getRuntime(), block, Block.Type.LAMBDA);
     }
 
     public static void fillNil(IRubyObject[]arr, int from, int to, Ruby runtime) {
         IRubyObject nils[] = runtime.getNilPrefilledArray();
         int i;
 
         for (i = from; i + Ruby.NIL_PREFILLED_ARRAY_SIZE < to; i += Ruby.NIL_PREFILLED_ARRAY_SIZE) {
             System.arraycopy(nils, 0, arr, i, Ruby.NIL_PREFILLED_ARRAY_SIZE);
         }
         System.arraycopy(nils, 0, arr, i, to - i);
     }
 
     public static void fillNil(IRubyObject[]arr, Ruby runtime) {
         fillNil(arr, 0, arr.length, runtime);
     }
 
     public static boolean isFastSwitchableString(IRubyObject str) {
         return str instanceof RubyString;
     }
 
     public static boolean isFastSwitchableSingleCharString(IRubyObject str) {
         return str instanceof RubyString && ((RubyString)str).getByteList().length() == 1;
     }
 
     public static int getFastSwitchString(IRubyObject str) {
         ByteList byteList = ((RubyString)str).getByteList();
         return byteList.hashCode();
     }
 
     public static int getFastSwitchSingleCharString(IRubyObject str) {
         ByteList byteList = ((RubyString)str).getByteList();
         return byteList.get(0);
     }
 
     public static boolean isFastSwitchableSymbol(IRubyObject sym) {
         return sym instanceof RubySymbol;
     }
 
     public static boolean isFastSwitchableSingleCharSymbol(IRubyObject sym) {
         return sym instanceof RubySymbol && ((RubySymbol)sym).asJavaString().length() == 1;
     }
 
     public static int getFastSwitchSymbol(IRubyObject sym) {
         String str = ((RubySymbol)sym).asJavaString();
         return str.hashCode();
     }
 
     public static int getFastSwitchSingleCharSymbol(IRubyObject sym) {
         String str = ((RubySymbol)sym).asJavaString();
         return (int)str.charAt(0);
     }
 
     public static Block getBlock(ThreadContext context, IRubyObject self, Node node) {
         IterNode iter = (IterNode)node;
         iter.getScope().determineModule();
 
         // Create block for this iter node
         // FIXME: We shouldn't use the current scope if it's not actually from the same hierarchy of static scopes
         if (iter.getBlockBody() instanceof InterpretedBlock) {
             return InterpretedBlock.newInterpretedClosure(context, iter.getBlockBody(), self);
         } else {
             return Interpreted19Block.newInterpretedClosure(context, iter.getBlockBody(), self);
         }
     }
 
     public static Block getBlock(Ruby runtime, ThreadContext context, IRubyObject self, Node node, Block aBlock) {
         return RuntimeHelpers.getBlockFromBlockPassBody(runtime, node.interpret(runtime, context, self, aBlock), aBlock);
     }
 
     /**
      * Equivalent to rb_equal in MRI
      *
      * @param context
      * @param a
      * @param b
      * @return
      */
     public static RubyBoolean rbEqual(ThreadContext context, IRubyObject a, IRubyObject b) {
         Ruby runtime = context.getRuntime();
         if (a == b) return runtime.getTrue();
         IRubyObject res = invokedynamic(context, a, OP_EQUAL, b);
         return runtime.newBoolean(res.isTrue());
     }
 
     public static void traceLine(ThreadContext context) {
         String name = context.getFrameName();
         RubyModule type = context.getFrameKlazz();
         context.getRuntime().callEventHooks(context, RubyEvent.LINE, context.getFile(), context.getLine(), name, type);
     }
 
     public static void traceClass(ThreadContext context) {
         String name = context.getFrameName();
         RubyModule type = context.getFrameKlazz();
         context.getRuntime().callEventHooks(context, RubyEvent.CLASS, context.getFile(), context.getLine(), name, type);
     }
 
     public static void traceEnd(ThreadContext context) {
         String name = context.getFrameName();
         RubyModule type = context.getFrameKlazz();
         context.getRuntime().callEventHooks(context, RubyEvent.END, context.getFile(), context.getLine(), name, type);
     }
 
     /**
      * Some of this code looks scary.  All names for an alias or undef is a
      * fitem in 1.8/1.9 grammars.  This means it is guaranteed to be either
      * a LiteralNode of a DSymbolNode.  Nothing else is possible.  Also
      * Interpreting a DSymbolNode will always yield a RubySymbol.
      */
     public static String interpretAliasUndefName(Node nameNode, Ruby runtime,
             ThreadContext context, IRubyObject self, Block aBlock) {
         String name;
 
         if (nameNode instanceof LiteralNode) {
             name = ((LiteralNode) nameNode).getName();
         } else {
             assert nameNode instanceof DSymbolNode: "Alias or Undef not literal or dsym";
             name = ((RubySymbol) nameNode.interpret(runtime, context, self, aBlock)).asJavaString();
         }
 
         return name;
     }
 
     /**
      * Used by the compiler to simplify arg checking in variable-arity paths
      *
      * @param context thread context
      * @param args arguments array
      * @param min minimum required
      * @param max maximum allowed
      */
     public static void checkArgumentCount(ThreadContext context, IRubyObject[] args, int min, int max) {
         checkArgumentCount(context, args.length, min, max);
     }
 
     /**
      * Used by the compiler to simplify arg checking in variable-arity paths
      *
      * @param context thread context
      * @param args arguments array
      * @param req required number
      */
     public static void checkArgumentCount(ThreadContext context, IRubyObject[] args, int req) {
         checkArgumentCount(context, args.length, req, req);
     }
     
     public static void checkArgumentCount(ThreadContext context, int length, int min, int max) {
         int expected = 0;
         if (length < min) {
             expected = min;
         } else if (max > -1 && length > max) {
             expected = max;
         } else {
             return;
         }
         throw context.getRuntime().newArgumentError(length, expected);
     }
 
     public static boolean isModuleAndHasConstant(IRubyObject left, String name) {
         return left instanceof RubyModule && ((RubyModule) left).getConstantFromNoConstMissing(name, false) != null;
     }
 
     public static ByteList getDefinedConstantOrBoundMethod(IRubyObject left, String name) {
         if (isModuleAndHasConstant(left, name)) return Node.CONSTANT_BYTELIST;
         if (left.getMetaClass().isMethodBound(name, true)) return Node.METHOD_BYTELIST;
         return null;
     }
 
     public static RubyModule getSuperClassForDefined(Ruby runtime, RubyModule klazz) {
         RubyModule superklazz = klazz.getSuperClass();
 
         if (superklazz == null && klazz.isModule()) superklazz = runtime.getObject();
 
         return superklazz;
     }
 
     public static boolean isGenerationEqual(IRubyObject object, int generation) {
         RubyClass metaClass;
         if (object instanceof RubyBasicObject) {
             metaClass = ((RubyBasicObject)object).getMetaClass();
         } else {
             metaClass = object.getMetaClass();
         }
         return metaClass.getGeneration() == generation;
     }
 
     public static String[] getScopeNames(String scopeNames) {
         StringTokenizer toker = new StringTokenizer(scopeNames, ";");
         ArrayList list = new ArrayList(10);
         while (toker.hasMoreTokens()) {
             list.add(toker.nextToken().intern());
         }
         return (String[])list.toArray(new String[list.size()]);
     }
 
     public static IRubyObject[] arraySlice1N(IRubyObject arrayish) {
         arrayish = aryToAry(arrayish);
         RubyArray arrayish2 = ensureMultipleAssignableRubyArray(arrayish, arrayish.getRuntime(), true);
         return new IRubyObject[] {arrayEntryOrNilZero(arrayish2), subarrayOrEmpty(arrayish2, arrayish2.getRuntime(), 1)};
     }
 
     public static IRubyObject arraySlice1(IRubyObject arrayish) {
         arrayish = aryToAry(arrayish);
         RubyArray arrayish2 = ensureMultipleAssignableRubyArray(arrayish, arrayish.getRuntime(), true);
         return arrayEntryOrNilZero(arrayish2);
     }
 
     public static RubyClass metaclass(IRubyObject object) {
         return object instanceof RubyBasicObject ?
             ((RubyBasicObject)object).getMetaClass() :
             object.getMetaClass();
     }
 
     public static String rawBytesToString(byte[] bytes) {
         // stuff bytes into chars
         char[] chars = new char[bytes.length];
         for (int i = 0; i < bytes.length; i++) chars[i] = (char)bytes[i];
         return new String(chars);
     }
 
     public static byte[] stringToRawBytes(String string) {
         char[] chars = string.toCharArray();
         byte[] bytes = new byte[chars.length];
         for (int i = 0; i < chars.length; i++) bytes[i] = (byte)chars[i];
         return bytes;
     }
 
     public static String encodeCaptureOffsets(int[] scopeOffsets) {
         char[] encoded = new char[scopeOffsets.length * 2];
         for (int i = 0; i < scopeOffsets.length; i++) {
             int offDepth = scopeOffsets[i];
             char off = (char)(offDepth & 0xFFFF);
             char depth = (char)(offDepth >> 16);
             encoded[2 * i] = off;
             encoded[2 * i + 1] = depth;
         }
         return new String(encoded);
     }
 
     public static int[] decodeCaptureOffsets(String encoded) {
         char[] chars = encoded.toCharArray();
         int[] scopeOffsets = new int[chars.length / 2];
         for (int i = 0; i < scopeOffsets.length; i++) {
             char off = chars[2 * i];
             char depth = chars[2 * i + 1];
             scopeOffsets[i] = (((int)depth) << 16) | (int)off;
         }
         return scopeOffsets;
     }
 
     public static IRubyObject match2AndUpdateScope(IRubyObject receiver, ThreadContext context, IRubyObject value, String scopeOffsets) {
         DynamicScope scope = context.getCurrentScope();
         IRubyObject match = ((RubyRegexp)receiver).op_match(context, value);
-        updateScopeWithCaptures(context, scope, decodeCaptureOffsets(scopeOffsets), value);
+        updateScopeWithCaptures(context, scope, decodeCaptureOffsets(scopeOffsets), match);
         return match;
     }
 
     public static IRubyObject match2AndUpdateScope19(IRubyObject receiver, ThreadContext context, IRubyObject value, String scopeOffsets) {
         DynamicScope scope = context.getCurrentScope();
         IRubyObject match = ((RubyRegexp)receiver).op_match19(context, value);
-        updateScopeWithCaptures(context, scope, decodeCaptureOffsets(scopeOffsets), value);
+        updateScopeWithCaptures(context, scope, decodeCaptureOffsets(scopeOffsets), match);
         return match;
     }
 
     public static void updateScopeWithCaptures(ThreadContext context, DynamicScope scope, int[] scopeOffsets, IRubyObject result) {
         Ruby runtime = context.runtime;
         if (result.isNil()) { // match2 directly calls match so we know we can count on result
             IRubyObject nil = runtime.getNil();
 
             for (int i = 0; i < scopeOffsets.length; i++) {
                 scope.setValue(nil, scopeOffsets[i], 0);
             }
         } else {
             RubyMatchData matchData = (RubyMatchData)scope.getBackRef(runtime);
             // FIXME: Mass assignment is possible since we know they are all locals in the same
             //   scope that are also contiguous
             IRubyObject[] namedValues = matchData.getNamedBackrefValues(runtime);
 
             for (int i = 0; i < scopeOffsets.length; i++) {
                 scope.setValue(namedValues[i], scopeOffsets[i] & 0xffff, scopeOffsets[i] >> 16);
             }
         }
     }
 
     public static RubyArray argsPush(RubyArray first, IRubyObject second) {
         return ((RubyArray)first.dup()).append(second);
     }
 
     public static RubyArray argsCat(IRubyObject first, IRubyObject second) {
         Ruby runtime = first.getRuntime();
         IRubyObject secondArgs;
         if (runtime.is1_9()) {
             secondArgs = RuntimeHelpers.splatValue19(second);
         } else {
             secondArgs = RuntimeHelpers.splatValue(second);
         }
 
         return ((RubyArray)RuntimeHelpers.ensureRubyArray(runtime, first).dup()).concat(secondArgs);
     }
 
     public static String encodeParameterList(ArgsNode argsNode) {
         StringBuilder builder = new StringBuilder();
         
         boolean added = false;
         if (argsNode.getPre() != null) {
             for (Node preNode : argsNode.getPre().childNodes()) {
                 if (added) builder.append(';');
                 added = true;
                 if (preNode instanceof MultipleAsgn19Node) {
                     builder.append("nil");
                 } else {
                     builder.append("q").append(((ArgumentNode)preNode).getName());
                 }
             }
         }
 
         if (argsNode.getOptArgs() != null) {
             for (Node optNode : argsNode.getOptArgs().childNodes()) {
                 if (added) builder.append(';');
                 added = true;
                 builder.append("o");
                 if (optNode instanceof OptArgNode) {
                     builder.append(((OptArgNode)optNode).getName());
                 } else if (optNode instanceof LocalAsgnNode) {
                     builder.append(((LocalAsgnNode)optNode).getName());
                 } else if (optNode instanceof DAsgnNode) {
                     builder.append(((DAsgnNode)optNode).getName());
                 }
             }
         }
 
         if (argsNode.getRestArg() >= 0) {
             if (added) builder.append(';');
             added = true;
             if (argsNode.getRestArgNode() instanceof UnnamedRestArgNode) {
                 if (((UnnamedRestArgNode) argsNode.getRestArgNode()).isStar()) builder.append("R");
             } else {
                 builder.append("r").append(argsNode.getRestArgNode().getName());
             }
         }
 
         if (argsNode.getPost() != null) {
             for (Node postNode : argsNode.getPost().childNodes()) {
                 if (added) builder.append(';');
                 added = true;
                 if (postNode instanceof MultipleAsgn19Node) {
                     builder.append("nil");
                 } else {
                     builder.append("q").append(((ArgumentNode)postNode).getName());
                 }
             }
         }
 
         if (argsNode.getBlock() != null) {
             if (added) builder.append(';');
             added = true;
             builder.append("b").append(argsNode.getBlock().getName());
         }
 
         if (!added) builder.append("NONE");
 
         return builder.toString();
     }
 
     public static RubyArray parameterListToParameters(Ruby runtime, String[] parameterList, boolean isLambda) {
         RubyArray parms = RubyArray.newEmptyArray(runtime);
 
         for (String param : parameterList) {
             if (param.equals("NONE")) break;
 
             RubyArray elem = RubyArray.newEmptyArray(runtime);
             if (param.equals("nil")) {
                 // marker for masgn args (the parens in "a, b, (c, d)"
                 elem.add(RubySymbol.newSymbol(runtime, isLambda ? "req" : "opt"));
                 parms.add(elem);
                 continue;
             }
 
             if (param.charAt(0) == 'q') {
                 // required/normal arg
                 elem.add(RubySymbol.newSymbol(runtime, isLambda ? "req" : "opt"));
             } else if (param.charAt(0) == 'r') {
                 // named rest arg
                 elem.add(RubySymbol.newSymbol(runtime, "rest"));
             } else if (param.charAt(0) == 'R') {
                 // unnamed rest arg (star)
                 elem.add(RubySymbol.newSymbol(runtime, "rest"));
                 parms.add(elem);
                 continue;
             } else if (param.charAt(0) == 'o') {
                 // optional arg
                 elem.add(RubySymbol.newSymbol(runtime, "opt"));
                 if (param.length() == 1) {
                     // no name; continue
                     parms.add(elem);
                     continue;
                 }
             } else if (param.charAt(0) == 'b') {
                 // block arg
                 elem.add(RubySymbol.newSymbol(runtime, "block"));
             }
             elem.add(RubySymbol.newSymbol(runtime, param.substring(1)));
             parms.add(elem);
         }
 
         return parms;
     }
 
     public static ByteList getDefinedCall(ThreadContext context, IRubyObject self, IRubyObject receiver, String name) {
         RubyClass metaClass = receiver.getMetaClass();
         DynamicMethod method = metaClass.searchMethod(name);
         Visibility visibility = method.getVisibility();
 
         if (visibility != Visibility.PRIVATE &&
                 (visibility != Visibility.PROTECTED || metaClass.getRealClass().isInstance(self)) && !method.isUndefined()) {
             return Node.METHOD_BYTELIST;
         }
 
         if (context.getRuntime().is1_9() && receiver.callMethod(context, "respond_to_missing?",
             new IRubyObject[]{context.getRuntime().newSymbol(name), context.getRuntime().getFalse()}).isTrue()) {
             return Node.METHOD_BYTELIST;
         }
         return null;
     }
 
     public static ByteList getDefinedNot(Ruby runtime, ByteList definition) {
         if (definition != null && runtime.is1_9()) {
             definition = Node.METHOD_BYTELIST;
         }
 
         return definition;
     }
     
     public static IRubyObject invokedynamic(ThreadContext context, IRubyObject self, int index) {
         RubyClass metaclass = self.getMetaClass();
         String name = MethodIndex.METHOD_NAMES[index];
         return getMethodCached(context, metaclass, index, name).call(context, self, metaclass, name);
     }
     
     public static IRubyObject invokedynamic(ThreadContext context, IRubyObject self, int index, IRubyObject arg0) {
         RubyClass metaclass = self.getMetaClass();
         String name = MethodIndex.METHOD_NAMES[index];
         return getMethodCached(context, metaclass, index, name).call(context, self, metaclass, name, arg0);
     }
     
     private static DynamicMethod getMethodCached(ThreadContext context, RubyClass metaclass, int index, String name) {
         if (metaclass.index >= ClassIndex.MAX_CLASSES) return metaclass.searchMethod(name);
         return context.runtimeCache.getMethod(context, metaclass, metaclass.index * (index + 1), name);
     }
     
     public static IRubyObject lastElement(IRubyObject[] ary) {
         return ary[ary.length - 1];
     }
     
     public static RubyString appendAsString(RubyString target, IRubyObject other) {
         return target.append(other.asString());
     }
     
     public static RubyString appendAsString19(RubyString target, IRubyObject other) {
         return target.append19(other.asString());
     }
 
     /**
      * We need to splat incoming array to a block when |a, *b| (any required +
      * rest) or |a, b| (>1 required).
      */
     public static boolean needsSplat19(int requiredCount, boolean isRest) {
         return (isRest && requiredCount > 0) || (!isRest && requiredCount > 1);
     }
 
     // . Array given to rest should pass itself
     // . Array with rest + other args should extract array
     // . Array with multiple values and NO rest should extract args if there are more than one argument
     // Note: In 1.9 alreadyArray is only relevent from our internal Java code in core libs.  We never use it
     // from interpreter or JIT.  FIXME: Change core lib consumers to stop using alreadyArray param.
     public static IRubyObject[] restructureBlockArgs19(IRubyObject value, boolean needsSplat, boolean alreadyArray) {
         if (value != null && !(value instanceof RubyArray) && needsSplat) value = RuntimeHelpers.aryToAry(value);
 
         IRubyObject[] parameters;
         if (value == null) {
             parameters = IRubyObject.NULL_ARRAY;
         } else if (value instanceof RubyArray && (alreadyArray || needsSplat)) {
             parameters = ((RubyArray) value).toJavaArray();
         } else {
             parameters = new IRubyObject[] { value };
         }
 
         return parameters;
     }
 }
