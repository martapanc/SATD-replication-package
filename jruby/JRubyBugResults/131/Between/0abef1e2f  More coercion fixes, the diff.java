diff --git a/src/org/jruby/RubyHash.java b/src/org/jruby/RubyHash.java
index ed4f8eb1e2..df8cd517e5 100644
--- a/src/org/jruby/RubyHash.java
+++ b/src/org/jruby/RubyHash.java
@@ -1,819 +1,820 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2001 Chad Fowler <chadfowler@chadfowler.com>
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2006 Ola Bini <Ola.Bini@ki.se>
  * Copyright (C) 2006 Tim Azzopardi <tim@tigerfive.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import java.io.IOException;
 import java.util.AbstractCollection;
 import java.util.AbstractSet;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import java.util.Set;
 import org.jruby.javasupport.JavaUtil;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.ClassIndex;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.runtime.marshal.MarshalStream;
 import org.jruby.runtime.marshal.UnmarshalStream;
 
 /** Implementation of the Hash class.
  *
  * @author  jpetersen
  */
 public class RubyHash extends RubyObject implements Map {
     private Map valueMap;
     // Place we capture any explicitly set proc so we can return it for default_proc
     private IRubyObject capturedDefaultProc;
     private static final Callback NIL_DEFAULT_VALUE  = new Callback() {
         public IRubyObject execute(IRubyObject recv, IRubyObject[] args, Block block) {
             return recv.getRuntime().getNil();
         }
     
         public Arity getArity() {
             return Arity.optional();
         }
     };
     
     // Holds either default value or default proc.  Executing whatever is here will return the
     // correct default value.
     private Callback defaultValueCallback;
     
     private boolean isRehashing = false;
 
     public RubyHash(Ruby runtime) {
         this(runtime, runtime.getNil());
     }
 
     public RubyHash(Ruby runtime, IRubyObject defaultValue) {
         super(runtime, runtime.getClass("Hash"));
         this.valueMap = new HashMap();
         this.capturedDefaultProc = runtime.getNil();
         setDefaultValue(defaultValue);
     }
 
     public RubyHash(Ruby runtime, Map valueMap, IRubyObject defaultValue) {
         super(runtime, runtime.getClass("Hash"));
         this.valueMap = new HashMap(valueMap);
         this.capturedDefaultProc = runtime.getNil();
         setDefaultValue(defaultValue);
     }
     
     public int getNativeTypeIndex() {
         return ClassIndex.HASH;
     }
     
     public IRubyObject getDefaultValue(IRubyObject[] args, Block unusedBlock) {
         if(defaultValueCallback == null || (args.length == 0 && !capturedDefaultProc.isNil())) {
             return getRuntime().getNil();
         }
         return defaultValueCallback.execute(this, args, Block.NULL_BLOCK);
     }
 
     public IRubyObject setDefaultValue(final IRubyObject defaultValue) {
         capturedDefaultProc = getRuntime().getNil();
         if (defaultValue == getRuntime().getNil()) {
             defaultValueCallback = NIL_DEFAULT_VALUE;
         } else {
             defaultValueCallback = new Callback() {
                 public IRubyObject execute(IRubyObject recv, IRubyObject[] args, Block unusedBlock) {
                     return defaultValue;
                 }
 
                 public Arity getArity() {
                     return Arity.optional();
                 }
             };
         }
         
         return defaultValue;
     }
 
     public void setDefaultProc(final RubyProc newProc) {
         final IRubyObject self = this;
         capturedDefaultProc = newProc;
         defaultValueCallback = new Callback() {
             public IRubyObject execute(IRubyObject recv, IRubyObject[] args, Block unusedBlock) {
                 IRubyObject[] nargs = args.length == 0 ? new IRubyObject[] { self } :
                      new IRubyObject[] { self, args[0] };
 
                 return newProc.call(nargs);
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         };
     }
     
     public IRubyObject default_proc(Block unusedBlock) {
         return capturedDefaultProc;
     }
 
     public Map getValueMap() {
         return valueMap;
     }
 
     public void setValueMap(Map valueMap) {
         this.valueMap = valueMap;
     }
 
 	/**
 	 * gets an iterator on a copy of the keySet.
 	 * modifying the iterator will NOT modify the map.
 	 * if the map is modified while iterating on this iterator, the iterator
 	 * will not be invalidated but the content will be the same as the old one.
 	 * @return the iterator
 	 **/
 	private Iterator keyIterator() {
 		return new ArrayList(valueMap.keySet()).iterator();
 	}
 
 	private Iterator valueIterator() {
 		return new ArrayList(valueMap.values()).iterator();
 	}
 
 
 	/**
 	 * gets an iterator on the entries.
 	 * modifying this iterator WILL modify the map.
 	 * the iterator will be invalidated if the map is modified.
 	 * @return the iterator
 	 */
 	private Iterator modifiableEntryIterator() {
 		return valueMap.entrySet().iterator();
 	}
 
 	/**
 	 * gets an iterator on a copy of the entries.
 	 * modifying this iterator will NOT modify the map.
 	 * if the map is modified while iterating on this iterator, the iterator
 	 * will not be invalidated but the content will be the same as the old one.
 	 * @return the iterator
 	 */
 	private Iterator entryIterator() {
 		return new ArrayList(valueMap.entrySet()).iterator();		//in general we either want to modify the map or make sure we don't when we use this, so skip the copy
 	}
 
     /** rb_hash_modify
      *
      */
     public void modify() {
     	testFrozen("Hash");
         if (isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError("Insecure: can't modify hash");
         }
     }
 
     private int length() {
         return valueMap.size();
     }
 
     // Hash methods
 
     public static RubyHash newHash(Ruby runtime) {
     	return new RubyHash(runtime);
     }
 
 	public static RubyHash newHash(Ruby runtime, Map valueMap, IRubyObject defaultValue) {
 		assert defaultValue != null;
 		
 		return new RubyHash(runtime, valueMap, defaultValue);
 	}
 
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         if (block.isGiven()) {
             setDefaultProc(getRuntime().newProc(false, block));
         } else if (args.length > 0) {
             modify();
 
             setDefaultValue(args[0]);
         }
         return this;
     }
     
     public IRubyObject inspect() {
         if(!getRuntime().registerInspecting(this)) {
             return getRuntime().newString("{...}");
         }
         try {
             final String sep = ", ";
             final String arrow = "=>";
             final StringBuffer sb = new StringBuffer("{");
             boolean firstEntry = true;
         
             ThreadContext context = getRuntime().getCurrentContext();
         
             for (Iterator iter = valueMap.entrySet().iterator(); iter.hasNext(); ) {
                 Map.Entry entry = (Map.Entry) iter.next();
                 IRubyObject key = (IRubyObject) entry.getKey();
                 IRubyObject value = (IRubyObject) entry.getValue();
                 if (!firstEntry) {
                     sb.append(sep);
                 }
             
                 sb.append(key.callMethod(context, "inspect")).append(arrow);
                 sb.append(value.callMethod(context, "inspect"));
                 firstEntry = false;
             }
             sb.append("}");
             return getRuntime().newString(sb.toString());
         } finally {
             getRuntime().unregisterInspecting(this);
         }
     }
 
     public RubyFixnum rb_size() {
         return getRuntime().newFixnum(length());
     }
 
     public RubyBoolean empty_p() {
         return length() == 0 ? getRuntime().getTrue() : getRuntime().getFalse();
     }
 
     public RubyArray to_a() {
         Ruby runtime = getRuntime();
         RubyArray result = RubyArray.newArray(runtime, length());
         
         for(Iterator iter = valueMap.entrySet().iterator(); iter.hasNext();) {
             Map.Entry entry = (Map.Entry) iter.next();
             result.append(RubyArray.newArray(runtime, (IRubyObject) entry.getKey(), (IRubyObject) entry.getValue()));
         }
         return result;
     }
 
     public IRubyObject to_s() {
         if(!getRuntime().registerInspecting(this)) {
             return getRuntime().newString("{...}");
         }
         try {
             return to_a().to_s();
         } finally {
             getRuntime().unregisterInspecting(this);
         }
     }
 
     public RubyHash rehash() {
         modify();
         try {
             isRehashing = true;
             valueMap = new HashMap(valueMap);
         } finally {
             isRehashing = false;
         }
         return this;
     }
 
     public RubyHash to_hash() {
         return this;
     }
 
     public IRubyObject aset(IRubyObject key, IRubyObject value) {
         modify();
         
         if (!(key instanceof RubyString) || valueMap.get(key) != null) {
             valueMap.put(key, value);
         } else {
             IRubyObject realKey = key.dup();
             realKey.setFrozen(true);
             valueMap.put(realKey, value);
         }
         return value;
     }
 
     public IRubyObject aref(IRubyObject key) {
         IRubyObject value = (IRubyObject) valueMap.get(key);
 
         return value != null ? value : callMethod(getRuntime().getCurrentContext(), "default", new IRubyObject[] {key});
     }
 
     public IRubyObject fetch(IRubyObject[] args, Block block) {
         if (args.length < 1) {
             throw getRuntime().newArgumentError(args.length, 1);
         }
         IRubyObject key = args[0];
         IRubyObject result = (IRubyObject) valueMap.get(key);
         if (result == null) {
             if (args.length > 1) return args[1]; 
                 
             if (block.isGiven()) return block.yield(getRuntime().getCurrentContext(), key); 
 
             throw getRuntime().newIndexError("key not found");
         }
         return result;
     }
 
 
     public RubyBoolean has_key(IRubyObject key) {
         return getRuntime().newBoolean(valueMap.containsKey(key));
     }
 
     public RubyBoolean has_value(IRubyObject value) {
         return getRuntime().newBoolean(valueMap.containsValue(value));
     }
 
 	public RubyHash each(Block block) {
 		return eachInternal(false, block);
 	}
 
 	public RubyHash each_pair(Block block) {
 		return eachInternal(true, block);
 	}
 
     protected RubyHash eachInternal(boolean aValue, Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
         for (Iterator iter = entryIterator(); iter.hasNext();) {
             checkRehashing();
             Map.Entry entry = (Map.Entry) iter.next();
             block.yield(context, getRuntime().newArray((IRubyObject)entry.getKey(), (IRubyObject)entry.getValue()), null, null, aValue);
         }
         return this;
     }
 
 	
 
     private void checkRehashing() {
         if (isRehashing) {
             throw getRuntime().newIndexError("rehash occured during iteration");
         }
     }
 
     public RubyHash each_value(Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
 		for (Iterator iter = valueIterator(); iter.hasNext();) {
             checkRehashing();
 			IRubyObject value = (IRubyObject) iter.next();
 			block.yield(context, value);
 		}
 		return this;
 	}
 
 	public RubyHash each_key(Block block) {
         ThreadContext context = getRuntime().getCurrentContext();
 		for (Iterator iter = keyIterator(); iter.hasNext();) {
 			checkRehashing();
             IRubyObject key = (IRubyObject) iter.next();
 			block.yield(context, key);
 		}
 		return this;
 	}
 
 	public RubyArray sort(Block block) {
 		return (RubyArray) to_a().sort_bang(block);
 	}
 
     public IRubyObject index(IRubyObject value) {
         for (Iterator iter = valueMap.keySet().iterator(); iter.hasNext(); ) {
             Object key = iter.next();
             if (value.equals(valueMap.get(key))) {
                 return (IRubyObject) key;
             }
         }
         return getRuntime().getNil();
     }
 
     public RubyArray indices(IRubyObject[] indices) {
         RubyArray values = RubyArray.newArray(getRuntime(), indices.length);
 
         for (int i = 0; i < indices.length; i++) {
             values.append(aref(indices[i]));
         }
 
         return values;
     }
 
     public RubyArray keys() {
         return RubyArray.newArray(getRuntime(), valueMap.keySet());
     }
 
     public RubyArray rb_values() {
         return RubyArray.newArray(getRuntime(), valueMap.values());
     }
 
     public IRubyObject equal(IRubyObject other) {
         if (this == other) {
             return getRuntime().getTrue();
         } else if (!(other instanceof RubyHash)) {
             return getRuntime().getFalse();
         } else if (length() != ((RubyHash)other).length()) {
             return getRuntime().getFalse();
         }
 
         for (Iterator iter = modifiableEntryIterator(); iter.hasNext();) {
             checkRehashing();
             Map.Entry entry = (Map.Entry) iter.next();
 
             Object value = ((RubyHash)other).valueMap.get(entry.getKey());
             if (value == null || !entry.getValue().equals(value)) {
                 return getRuntime().getFalse();
             }
         }
         return getRuntime().getTrue();
     }
 
     public RubyArray shift() {
 		modify();
         Iterator iter = modifiableEntryIterator();
         Map.Entry entry = (Map.Entry)iter.next();
         iter.remove();
         return RubyArray.newArray(getRuntime(), (IRubyObject)entry.getKey(), (IRubyObject)entry.getValue());
     }
 
 	public IRubyObject delete(IRubyObject key, Block block) {
 		modify();
 		IRubyObject result = (IRubyObject) valueMap.remove(key);
         
 		if (result != null) return result;
 		if (block.isGiven()) return block.yield(getRuntime().getCurrentContext(), key);
 
 		return getDefaultValue(new IRubyObject[] {key}, null);
 	}
 
 	public RubyHash delete_if(Block block) {
 		reject_bang(block);
 		return this;
 	}
 
 	public RubyHash reject(Block block) {
 		RubyHash result = (RubyHash) dup();
 		result.reject_bang(block);
 		return result;
 	}
 
 	public IRubyObject reject_bang(Block block) {
 		modify();
 		boolean isModified = false;
         ThreadContext context = getRuntime().getCurrentContext();
 		for (Iterator iter = keyIterator(); iter.hasNext();) {
 			IRubyObject key = (IRubyObject) iter.next();
 			IRubyObject value = (IRubyObject) valueMap.get(key);
 			IRubyObject shouldDelete = block.yield(context, getRuntime().newArray(key, value), null, null, true);
 			if (shouldDelete.isTrue()) {
 				valueMap.remove(key);
 				isModified = true;
 			}
 		}
 
 		return isModified ? this : getRuntime().getNil(); 
 	}
 
 	public RubyHash rb_clear() {
 		modify();
 		valueMap.clear();
 		return this;
 	}
 
 	public RubyHash invert() {
 		RubyHash result = newHash(getRuntime());
 		
 		for (Iterator iter = modifiableEntryIterator(); iter.hasNext();) {
 			Map.Entry entry = (Map.Entry) iter.next();
 			result.aset((IRubyObject) entry.getValue(), 
 					(IRubyObject) entry.getKey());
 		}
 		return result;
 	}
 
     public RubyHash update(IRubyObject freshElements, Block block) {
         modify();
+        
         RubyHash freshElementsHash =
-            (RubyHash) freshElements.convertToTypeWithCheck(RubyHash.class, "Hash", "to_hash");
+            (RubyHash) freshElements.convertToTypeWithCheck(getRuntime().getClass("Hash"), "to_hash");
         ThreadContext ctx = getRuntime().getCurrentContext();
         if (block.isGiven()) {
             Map other = freshElementsHash.valueMap;
             for(Iterator iter = other.keySet().iterator();iter.hasNext();) {
                 IRubyObject key = (IRubyObject)iter.next();
                 IRubyObject oval = (IRubyObject)valueMap.get(key);
                 if(null == oval) {
                     valueMap.put(key,other.get(key));
                 } else {
                     valueMap.put(key,block.yield(ctx, getRuntime().newArrayNoCopy(new IRubyObject[]{key,oval,(IRubyObject)other.get(key)})));
                 }
             }
         } else {
             valueMap.putAll(freshElementsHash.valueMap);
         }
         return this;
     }
     
     public RubyHash merge(IRubyObject freshElements, Block block) {
         return ((RubyHash) dup()).update(freshElements, block);
     }
 
     public RubyHash replace(IRubyObject replacement) {
         modify();
         RubyHash replacementHash =
-            (RubyHash) replacement.convertToTypeWithCheck(RubyHash.class, "Hash", "to_hash");
+            (RubyHash) replacement.convertToTypeWithCheck(getRuntime().getClass("Hash"), "to_hash");
         valueMap.clear();
         valueMap.putAll(replacementHash.valueMap);
         defaultValueCallback = replacementHash.defaultValueCallback;
         return this;
     }
 
     public RubyArray values_at(IRubyObject[] argv) {
         RubyArray result = RubyArray.newArray(getRuntime());
         for (int i = 0; i < argv.length; i++) {
             result.append(aref(argv[i]));
         }
         return result;
     }
     
     public boolean hasNonProcDefault() {
         return defaultValueCallback != NIL_DEFAULT_VALUE;
     }
 
     // FIXME:  Total hack to get flash in Rails marshalling/unmarshalling in session ok...We need
     // to totally change marshalling to work with overridden core classes.
     public static void marshalTo(RubyHash hash, MarshalStream output) throws IOException {
         output.writeInt(hash.getValueMap().size());
 
         for (Iterator iter = hash.entryIterator(); iter.hasNext();) {
                 Map.Entry entry = (Map.Entry) iter.next();
 
                 output.dumpObject((IRubyObject) entry.getKey());
                 output.dumpObject((IRubyObject) entry.getValue());
         }
 		
         // handle default value
         if (hash.hasNonProcDefault()) {
             output.dumpObject(hash.defaultValueCallback.execute(null, NULL_ARRAY, null));
         }
     }
 
     public static RubyHash unmarshalFrom(UnmarshalStream input, boolean defaultValue) throws IOException {
         RubyHash result = newHash(input.getRuntime());
         input.registerLinkTarget(result);
         int size = input.unmarshalInt();
         for (int i = 0; i < size; i++) {
             IRubyObject key = input.unmarshalObject();
             IRubyObject value = input.unmarshalObject();
             result.aset(key, value);
         }
         if (defaultValue) {
             result.setDefaultValue(input.unmarshalObject());
         }
         return result;
     }
 
     public Class getJavaClass() {
         return Map.class;
     }
 	
     // Satisfy java.util.Set interface (for Java integration)
 
 	public boolean isEmpty() {
 		return valueMap.isEmpty();
 	}
 
 	public boolean containsKey(Object key) {
 		return keySet().contains(key);
 	}
 
 	public boolean containsValue(Object value) {
 		IRubyObject element = JavaUtil.convertJavaToRuby(getRuntime(), value);
 		
 		for (Iterator iter = valueMap.values().iterator(); iter.hasNext(); ) {
 			if (iter.next().equals(element)) {
 				return true;
 			}
 		}
 		return false;
 	}
 
 	public Object get(Object key) {
 		return JavaUtil.convertRubyToJava((IRubyObject) valueMap.get(JavaUtil.convertJavaToRuby(getRuntime(), key)));
 	}
 
 	public Object put(Object key, Object value) {
 		return valueMap.put(JavaUtil.convertJavaToRuby(getRuntime(), key),
 				JavaUtil.convertJavaToRuby(getRuntime(), value));
 	}
 
 	public Object remove(Object key) {
 		return valueMap.remove(JavaUtil.convertJavaToRuby(getRuntime(), key));
 	}
 
 	public void putAll(Map map) {
 		for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
 			Object key = iter.next();
 			
 			put(key, map.get(key));
 		}
 	}
 
 
 	public Set entrySet() {
 		return new ConversionMapEntrySet(getRuntime(), valueMap.entrySet());
 	}
 
 	public int size() {
 		return valueMap.size();
 	}
 
 	public void clear() {
 		valueMap.clear();
 	}
 
 	public Collection values() {
 		return new AbstractCollection() {
 			public Iterator iterator() {
 				return new IteratorAdapter(entrySet().iterator()) {
 					public Object next() {
 						return ((Map.Entry) super.next()).getValue();
 					}
 				};
 			}
 
 			public int size() {
 				return RubyHash.this.size();
 			}
 
 			public boolean contains(Object v) {
 				return RubyHash.this.containsValue(v);
 			}
 		};
 	}
 	
 	public Set keySet() {
 		return new AbstractSet() {
 			public Iterator iterator() {
 				return new IteratorAdapter(entrySet().iterator()) {
 					public Object next() {
 						return ((Map.Entry) super.next()).getKey();
 					}
 				};
 			}
 
 			public int size() {
 				return RubyHash.this.size();
 			}
 		};
 	}	
 
 	/**
 	 * Convenience adaptor for delegating to an Iterator.
 	 *
 	 */
 	private static class IteratorAdapter implements Iterator {
 		private Iterator iterator;
 		
 		public IteratorAdapter(Iterator iterator) {
 			this.iterator = iterator;
 		}
 		public boolean hasNext() {
 			return iterator.hasNext();
 		}
 		public Object next() {
 			return iterator.next();
 		}
 		public void remove() {
 			iterator.remove();
 		}		
 	}
 	
 	
     /**
      * Wraps a Set of Map.Entry (See #entrySet) such that JRuby types are mapped to Java types and vice verce.
      *
      */
     private static class ConversionMapEntrySet extends AbstractSet {
 		protected Set mapEntrySet;
 		protected Ruby runtime;
 
 		public ConversionMapEntrySet(Ruby runtime, Set mapEntrySet) {
 			this.mapEntrySet = mapEntrySet;
 			this.runtime = runtime;
 		}
         public Iterator iterator() {
             return new ConversionMapEntryIterator(runtime, mapEntrySet.iterator());
         }
         public boolean contains(Object o) {
             if (!(o instanceof Map.Entry)) {
                 return false;
             }
             return mapEntrySet.contains(getRubifiedMapEntry((Map.Entry) o));
         }
         
         public boolean remove(Object o) {
             if (!(o instanceof Map.Entry)) {
                 return false;
             }
             return mapEntrySet.remove(getRubifiedMapEntry((Map.Entry) o));
         }
 		public int size() {
 			return mapEntrySet.size();
 		}
         public void clear() {
         	mapEntrySet.clear();
         }
 		private Entry getRubifiedMapEntry(final Map.Entry mapEntry) {
 			return new Map.Entry(){
 				public Object getKey() {
 					return JavaUtil.convertJavaToRuby(runtime, mapEntry.getKey());
 				}
 				public Object getValue() {
 					return JavaUtil.convertJavaToRuby(runtime, mapEntry.getValue());
 				}
 				public Object setValue(Object arg0) {
 					// This should never get called in this context, but if it did...
 					throw new UnsupportedOperationException("unexpected call in this context");
 				}
             };
 		}
     }    
     
     /**
      * Wraps a RubyHash#entrySet#iterator such that the Map.Entry returned by next() will have its key and value 
      * mapped from JRuby types to Java types where applicable.
      */
     private static class ConversionMapEntryIterator implements Iterator {
         private Iterator iterator;
 		private Ruby runtime;
 
         public ConversionMapEntryIterator(Ruby runtime, Iterator iterator) {
             this.iterator = iterator;
             this.runtime = runtime;            
         }
 
         public boolean hasNext() {
             return iterator.hasNext();
         }
 
         public Object next() {
             return new ConversionMapEntry(runtime, ((Map.Entry) iterator.next())); 
         }
 
         public void remove() {
             iterator.remove();
         }
     }
     
    
     /**
      * Wraps a Map.Entry from RubyHash#entrySet#iterator#next such that the the key and value 
      * are mapped from/to JRuby/Java types where applicable.
      */
     private static class ConversionMapEntry implements Map.Entry {
         private Entry entry;
 		private Ruby runtime;
 
         public ConversionMapEntry(Ruby runtime, Map.Entry entry) {
             this.entry = entry;
             this.runtime = runtime;
         }
         
         public Object getKey() {
             IRubyObject rubyObject = (IRubyObject) entry.getKey();
             return JavaUtil.convertRubyToJava(rubyObject, Object.class); 
         }
         
         public Object getValue() {
             IRubyObject rubyObject = (IRubyObject) entry.getValue();
             return JavaUtil.convertRubyToJava(rubyObject, Object.class); 
         }
         
         public Object setValue(Object value) {
             return entry.setValue(JavaUtil.convertJavaToRuby(runtime, value));            
         }
     }
     
 }
diff --git a/src/org/jruby/RubyKernel.java b/src/org/jruby/RubyKernel.java
index 849c619574..9c2ae6c19d 100644
--- a/src/org/jruby/RubyKernel.java
+++ b/src/org/jruby/RubyKernel.java
@@ -1,971 +1,971 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2001 Chad Fowler <chadfowler@chadfowler.com>
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2005 Kiel Hodges <jruby-devel@selfsosoft.com>
  * Copyright (C) 2006 Evan Buswell <evan@heron.sytes.net>
  * Copyright (C) 2006 Ola Bini <ola@ologix.com>
  * Copyright (C) 2006 Michael Studman <codehaus@michaelstudman.com>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * Copyright (C) 2007 Nick Sieger <nicksieger@gmail.com>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.IOException;
 import java.util.Calendar;
 import java.util.Iterator;
 
 import org.jruby.ast.util.ArgsUtil;
 import org.jruby.exceptions.JumpException;
 import org.jruby.exceptions.MainExitException;
 import org.jruby.exceptions.RaiseException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.builtin.meta.FileMetaClass;
 import org.jruby.runtime.builtin.meta.IOMetaClass;
 import org.jruby.runtime.load.IAutoloadMethod;
 import org.jruby.runtime.load.LoadService;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.Sprintf;
 
 /**
  * Note: For CVS history, see KernelModule.java.
  *
  * @author jpetersen
  */
 public class RubyKernel {
     public final static Class IRUBY_OBJECT = IRubyObject.class;
 
     public static RubyModule createKernelModule(Ruby runtime) {
         RubyModule module = runtime.defineModule("Kernel");
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyKernel.class);
         CallbackFactory objectCallbackFactory = runtime.callbackFactory(RubyObject.class);
 
         module.defineFastModuleFunction("Array", callbackFactory.getFastSingletonMethod("new_array", IRUBY_OBJECT));
         module.defineFastModuleFunction("Float", callbackFactory.getFastSingletonMethod("new_float", IRUBY_OBJECT));
         module.defineFastModuleFunction("Integer", callbackFactory.getFastSingletonMethod("new_integer", IRUBY_OBJECT));
         module.defineFastModuleFunction("String", callbackFactory.getFastSingletonMethod("new_string", IRUBY_OBJECT));
         module.defineFastModuleFunction("`", callbackFactory.getFastSingletonMethod("backquote", IRUBY_OBJECT));
         module.defineFastModuleFunction("abort", callbackFactory.getFastOptSingletonMethod("abort"));
         module.defineModuleFunction("at_exit", callbackFactory.getSingletonMethod("at_exit"));
         module.defineFastModuleFunction("autoload", callbackFactory.getFastSingletonMethod("autoload", IRUBY_OBJECT, IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("autoload?", callbackFactory.getFastSingletonMethod("autoload_p", IRUBY_OBJECT));
         module.defineModuleFunction("binding", callbackFactory.getSingletonMethod("binding"));
         module.defineModuleFunction("block_given?", callbackFactory.getSingletonMethod("block_given"));
         module.defineModuleFunction("callcc", callbackFactory.getOptSingletonMethod("callcc"));
         module.defineModuleFunction("caller", callbackFactory.getOptSingletonMethod("caller"));
         module.defineModuleFunction("catch", callbackFactory.getSingletonMethod("rbCatch", IRUBY_OBJECT));
         module.defineFastModuleFunction("chomp", callbackFactory.getFastOptSingletonMethod("chomp"));
         module.defineFastModuleFunction("chomp!", callbackFactory.getFastOptSingletonMethod("chomp_bang"));
         module.defineFastModuleFunction("chop", callbackFactory.getFastSingletonMethod("chop"));
         module.defineFastModuleFunction("chop!", callbackFactory.getFastSingletonMethod("chop_bang"));
         module.defineModuleFunction("eval", callbackFactory.getOptSingletonMethod("eval"));
         module.defineFastModuleFunction("exit", callbackFactory.getFastOptSingletonMethod("exit"));
         module.defineFastModuleFunction("exit!", callbackFactory.getFastOptSingletonMethod("exit_bang"));
         module.defineModuleFunction("fail", callbackFactory.getOptSingletonMethod("raise"));
         // TODO: Implement Kernel#fork
         module.defineFastModuleFunction("format", callbackFactory.getFastOptSingletonMethod("sprintf"));
         module.defineFastModuleFunction("gets", callbackFactory.getFastOptSingletonMethod("gets"));
         module.defineFastModuleFunction("global_variables", callbackFactory.getFastSingletonMethod("global_variables"));
         module.defineModuleFunction("gsub", callbackFactory.getOptSingletonMethod("gsub"));
         module.defineModuleFunction("gsub!", callbackFactory.getOptSingletonMethod("gsub_bang"));
         // TODO: Add deprecation to Kernel#iterator? (maybe formal deprecation mech.)
         module.defineModuleFunction("iterator?", callbackFactory.getSingletonMethod("block_given"));
         module.defineModuleFunction("lambda", callbackFactory.getSingletonMethod("proc"));
         module.defineModuleFunction("load", callbackFactory.getOptSingletonMethod("load"));
         module.defineFastModuleFunction("local_variables", callbackFactory.getFastSingletonMethod("local_variables"));
         module.defineModuleFunction("loop", callbackFactory.getSingletonMethod("loop"));
         // Note: method_missing is documented as being in Object, but ruby appears to stick it in Kernel.
         module.defineModuleFunction("method_missing", callbackFactory.getOptSingletonMethod("method_missing"));
         module.defineModuleFunction("open", callbackFactory.getOptSingletonMethod("open"));
         module.defineFastModuleFunction("p", callbackFactory.getFastOptSingletonMethod("p"));
         module.defineFastModuleFunction("print", callbackFactory.getFastOptSingletonMethod("print"));
         module.defineFastModuleFunction("printf", callbackFactory.getFastOptSingletonMethod("printf"));
         module.defineModuleFunction("proc", callbackFactory.getSingletonMethod("proc"));
         // TODO: implement Kernel#putc
         module.defineFastModuleFunction("putc", callbackFactory.getFastSingletonMethod("putc", IRubyObject.class));
         module.defineFastModuleFunction("puts", callbackFactory.getFastOptSingletonMethod("puts"));
         module.defineModuleFunction("raise", callbackFactory.getOptSingletonMethod("raise"));
         module.defineFastModuleFunction("rand", callbackFactory.getFastOptSingletonMethod("rand"));
         module.defineFastModuleFunction("readline", callbackFactory.getFastOptSingletonMethod("readline"));
         module.defineFastModuleFunction("readlines", callbackFactory.getFastOptSingletonMethod("readlines"));
         module.defineModuleFunction("require", callbackFactory.getSingletonMethod("require", IRUBY_OBJECT));
         module.defineModuleFunction("scan", callbackFactory.getSingletonMethod("scan", IRUBY_OBJECT));
         module.defineFastModuleFunction("select", callbackFactory.getFastOptSingletonMethod("select"));
         module.defineModuleFunction("set_trace_func", callbackFactory.getSingletonMethod("set_trace_func", IRUBY_OBJECT));
         module.defineFastModuleFunction("sleep", callbackFactory.getFastSingletonMethod("sleep", IRUBY_OBJECT));
         module.defineFastModuleFunction("split", callbackFactory.getFastOptSingletonMethod("split"));
         module.defineFastModuleFunction("sprintf", callbackFactory.getFastOptSingletonMethod("sprintf"));
         module.defineFastModuleFunction("srand", callbackFactory.getFastOptSingletonMethod("srand"));
         module.defineModuleFunction("sub", callbackFactory.getOptSingletonMethod("sub"));
         module.defineModuleFunction("sub!", callbackFactory.getOptSingletonMethod("sub_bang"));
         // Skipping: Kernel#syscall (too system dependent)
         module.defineFastModuleFunction("system", callbackFactory.getFastOptSingletonMethod("system"));
         // TODO: Implement Kernel#exec differently?
         module.defineFastModuleFunction("exec", callbackFactory.getFastOptSingletonMethod("system"));
         module.defineFastModuleFunction("test", callbackFactory.getFastOptSingletonMethod("test"));
         module.defineModuleFunction("throw", callbackFactory.getOptSingletonMethod("rbThrow"));
         // TODO: Implement Kernel#trace_var
         module.defineModuleFunction("trap", callbackFactory.getOptSingletonMethod("trap"));
         // TODO: Implement Kernel#untrace_var
         module.defineFastModuleFunction("warn", callbackFactory.getFastSingletonMethod("warn", IRUBY_OBJECT));
         
         // Defined p411 Pickaxe 2nd ed.
         module.defineModuleFunction("singleton_method_added", callbackFactory.getSingletonMethod("singleton_method_added", IRUBY_OBJECT));
         module.defineModuleFunction("singleton_method_removed", callbackFactory.getSingletonMethod("singleton_method_removed", IRUBY_OBJECT));
         module.defineModuleFunction("singleton_method_undefined", callbackFactory.getSingletonMethod("singleton_method_undefined", IRUBY_OBJECT));
         
         // Object methods
         module.defineFastPublicModuleFunction("==", objectCallbackFactory.getFastMethod("obj_equal", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("===", objectCallbackFactory.getFastMethod("equal", IRUBY_OBJECT));
 
         module.defineAlias("eql?", "==");
         module.defineFastPublicModuleFunction("to_s", objectCallbackFactory.getFastMethod("to_s"));
         module.defineFastPublicModuleFunction("nil?", objectCallbackFactory.getFastMethod("nil_p"));
         module.defineFastPublicModuleFunction("to_a", callbackFactory.getFastSingletonMethod("to_a"));
         module.defineFastPublicModuleFunction("hash", objectCallbackFactory.getFastMethod("hash"));
         module.defineFastPublicModuleFunction("id", objectCallbackFactory.getFastMethod("id_deprecated"));
         module.defineFastPublicModuleFunction("object_id", objectCallbackFactory.getFastMethod("id"));
         module.defineAlias("__id__", "object_id");
         module.defineFastPublicModuleFunction("is_a?", objectCallbackFactory.getFastMethod("kind_of", IRUBY_OBJECT));
         module.defineAlias("kind_of?", "is_a?");
         module.defineFastPublicModuleFunction("dup", objectCallbackFactory.getFastMethod("dup"));
         module.defineFastPublicModuleFunction("equal?", objectCallbackFactory.getFastMethod("same", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("type", objectCallbackFactory.getFastMethod("type_deprecated"));
         module.defineFastPublicModuleFunction("class", objectCallbackFactory.getFastMethod("type"));
         module.defineFastPublicModuleFunction("inspect", objectCallbackFactory.getFastMethod("inspect"));
         module.defineFastPublicModuleFunction("=~", objectCallbackFactory.getFastMethod("match", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("clone", objectCallbackFactory.getFastMethod("rbClone"));
         module.defineFastPublicModuleFunction("display", objectCallbackFactory.getFastOptMethod("display"));
         module.defineFastPublicModuleFunction("extend", objectCallbackFactory.getFastOptMethod("extend"));
         module.defineFastPublicModuleFunction("freeze", objectCallbackFactory.getFastMethod("freeze"));
         module.defineFastPublicModuleFunction("frozen?", objectCallbackFactory.getFastMethod("frozen"));
         module.defineFastModuleFunction("initialize_copy", objectCallbackFactory.getFastMethod("initialize_copy", IRUBY_OBJECT));
         module.definePublicModuleFunction("instance_eval", objectCallbackFactory.getOptMethod("instance_eval"));
         module.defineFastPublicModuleFunction("instance_of?", objectCallbackFactory.getFastMethod("instance_of", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("instance_variables", objectCallbackFactory.getFastMethod("instance_variables"));
         module.defineFastPublicModuleFunction("instance_variable_get", objectCallbackFactory.getFastMethod("instance_variable_get", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("instance_variable_set", objectCallbackFactory.getFastMethod("instance_variable_set", IRUBY_OBJECT, IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("method", objectCallbackFactory.getFastMethod("method", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("methods", objectCallbackFactory.getFastOptMethod("methods"));
         module.defineFastPublicModuleFunction("private_methods", objectCallbackFactory.getFastMethod("private_methods"));
         module.defineFastPublicModuleFunction("protected_methods", objectCallbackFactory.getFastMethod("protected_methods"));
         module.defineFastPublicModuleFunction("public_methods", objectCallbackFactory.getFastOptMethod("public_methods"));
         module.defineFastModuleFunction("remove_instance_variable", objectCallbackFactory.getMethod("remove_instance_variable", IRUBY_OBJECT));
         module.defineFastPublicModuleFunction("respond_to?", objectCallbackFactory.getFastOptMethod("respond_to"));
         module.definePublicModuleFunction("send", objectCallbackFactory.getOptMethod("send"));
         module.defineAlias("__send__", "send");
         module.defineFastPublicModuleFunction("singleton_methods", objectCallbackFactory.getFastOptMethod("singleton_methods"));
         module.defineFastPublicModuleFunction("taint", objectCallbackFactory.getFastMethod("taint"));
         module.defineFastPublicModuleFunction("tainted?", objectCallbackFactory.getFastMethod("tainted"));
         module.defineFastPublicModuleFunction("untaint", objectCallbackFactory.getFastMethod("untaint"));
 
         runtime.setRespondToMethod(module.searchMethod("respond_to?"));
 
         return module;
     }
 
     public static IRubyObject at_exit(IRubyObject recv, Block block) {
         return recv.getRuntime().pushExitBlock(recv.getRuntime().newProc(false, block));
     }
 
     public static IRubyObject autoload_p(final IRubyObject recv, IRubyObject symbol) {
         String name = symbol.asSymbol();
         if (recv instanceof RubyModule) {
             name = ((RubyModule)recv).getName() + "::" + name;
         }
         
         IAutoloadMethod autoloadMethod = recv.getRuntime().getLoadService().autoloadFor(name);
         if(autoloadMethod == null) return recv.getRuntime().getNil();
 
         return recv.getRuntime().newString(autoloadMethod.file());
     }
 
     public static IRubyObject autoload(final IRubyObject recv, IRubyObject symbol, final IRubyObject file) {
         final LoadService loadService = recv.getRuntime().getLoadService();
         final String baseName = symbol.asSymbol();
         String nm = baseName;
         if(recv instanceof RubyModule) {
             nm = ((RubyModule)recv).getName() + "::" + nm;
         }
         loadService.addAutoload(nm, new IAutoloadMethod() {
                 public String file() {
                     return file.toString();
                 }
             /**
              * @see org.jruby.runtime.load.IAutoloadMethod#load(Ruby, String)
              */
             public IRubyObject load(Ruby runtime, String name) {
                 loadService.require(file.toString());
                 if(recv instanceof RubyModule) {
                     return ((RubyModule)recv).getConstant(baseName);
                 }
                 return runtime.getObject().getConstant(baseName);
             }
         });
         return recv;
     }
 
     public static IRubyObject method_missing(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         if (args.length == 0) {
             throw recv.getRuntime().newArgumentError("no id given");
         }
 
         String name = args[0].asSymbol();
         String description = null;
         if("inspect".equals(name) || "to_s".equals(name)) {
             description = recv.anyToString().toString();
         } else {
             description = recv.inspect().toString();
         }
         boolean noClass = description.length() > 0 && description.charAt(0) == '#';
         ThreadContext tc = runtime.getCurrentContext();
         Visibility lastVis = tc.getLastVisibility();
         if(null == lastVis) {
             lastVis = Visibility.PUBLIC;
         }
         CallType lastCallType = tc.getLastCallType();
         String format = lastVis.errorMessageFormat(lastCallType, name);
         // FIXME: Modify sprintf to accept Object[] as well...
         String msg = Sprintf.sprintf(runtime.newString(format), 
                 runtime.newArray(new IRubyObject[] { 
                         runtime.newString(name), 
                         runtime.newString(description),
                         runtime.newString(noClass ? "" : ":"), 
                         runtime.newString(noClass ? "" : recv.getType().getName())
                 })).toString();
         
         throw lastCallType == CallType.VARIABLE ? runtime.newNameError(msg, name) : runtime.newNoMethodError(msg, name);
     }
 
     public static IRubyObject open(IRubyObject recv, IRubyObject[] args, Block block) {
         Arity.checkArgumentCount(recv.getRuntime(), args,1,3);
         String arg = args[0].convertToString().toString();
         Ruby runtime = recv.getRuntime();
 
         if (arg.startsWith("|")) {
             String command = arg.substring(1);
             // exec process, create IO with process
             try {
                 Process p = new ShellLauncher(runtime).run(RubyString.newString(runtime,command));
                 RubyIO io = new RubyIO(runtime, p);
                 
                 if (block.isGiven()) {
                     try {
                         block.yield(recv.getRuntime().getCurrentContext(), io);
                         return runtime.getNil();
                     } finally {
                         io.close();
                     }
                 }
 
                 return io;
             } catch (IOException ioe) {
                 throw runtime.newIOErrorFromException(ioe);
             }
         } 
 
         return ((FileMetaClass) runtime.getClass("File")).open(args, block);
     }
 
     public static IRubyObject gets(IRubyObject recv, IRubyObject[] args) {
         return ((RubyArgsFile) recv.getRuntime().getGlobalVariables().get("$<")).gets(args);
     }
 
     public static IRubyObject abort(IRubyObject recv, IRubyObject[] args) {
         if(Arity.checkArgumentCount(recv.getRuntime(), args,0,1) == 1) {
             recv.getRuntime().getGlobalVariables().get("$stderr").callMethod(recv.getRuntime().getCurrentContext(),"puts",args[0]);
         }
         throw new MainExitException(1,true);
     }
 
     public static IRubyObject new_array(IRubyObject recv, IRubyObject object) {
-        IRubyObject value = object.convertToTypeWithCheck("Array", "to_ary");
+        IRubyObject value = object.convertToType(recv.getRuntime().getArray(), "to_ary", false, true, true);
         
         if (value.isNil()) {
             DynamicMethod method = object.getMetaClass().searchMethod("to_a");
             
             if (method.getImplementationClass() == recv.getRuntime().getKernel()) {
                 return recv.getRuntime().newArray(object);
             }
             
             // Strange that Ruby has custom code here and not convertToTypeWithCheck equivalent.
             value = object.callMethod(recv.getRuntime().getCurrentContext(), "to_a");
             if (value.getMetaClass() != recv.getRuntime().getClass("Array")) {
                 throw recv.getRuntime().newTypeError("`to_a' did not return Array");
                
             }
         }
         
         return value;
     }
     
     public static IRubyObject new_float(IRubyObject recv, IRubyObject object) {
         if(object instanceof RubyFixnum){
             return RubyFloat.newFloat(object.getRuntime(), ((RubyFixnum)object).getDoubleValue());
         }else if(object instanceof RubyFloat){
             return object;
         }else if(object instanceof RubyBignum){
             return RubyFloat.newFloat(object.getRuntime(), RubyBignum.big2dbl((RubyBignum)object));
         }else if(object instanceof RubyString){
             if(((RubyString)object).getValue().length() == 0){ // rb_cstr_to_dbl case
                 throw recv.getRuntime().newArgumentError("invalid value for Float(): " + object.inspect());
             }
             return RubyNumeric.str2fnum(recv.getRuntime(),(RubyString)object,true);
         }else if(object.isNil()){
             throw recv.getRuntime().newTypeError("can't convert nil into Float");
         } else {
             RubyFloat rFloat = object.convertToFloat();
             if(Double.isNaN(rFloat.getDoubleValue())){
                 recv.getRuntime().newArgumentError("invalid value for Float()");
         }
             return rFloat;
     }
     }
     
     public static IRubyObject new_integer(IRubyObject recv, IRubyObject object) {
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         if(object instanceof RubyString) {
             return RubyNumeric.str2inum(recv.getRuntime(),(RubyString)object,0,true);
                     }
         return object.callMethod(context,"to_i");
     }
     
     public static IRubyObject new_string(IRubyObject recv, IRubyObject object) {
         return object.callMethod(recv.getRuntime().getCurrentContext(), "to_s");
     }
     
     
     public static IRubyObject p(IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         ThreadContext context = recv.getRuntime().getCurrentContext();
 
         for (int i = 0; i < args.length; i++) {
             if (args[i] != null) {
                 defout.callMethod(context, "write", args[i].callMethod(context, "inspect"));
                 defout.callMethod(context, "write", recv.getRuntime().newString("\n"));
             }
         }
         return recv.getRuntime().getNil();
     }
 
     /** rb_f_putc
      */
     public static IRubyObject putc(IRubyObject recv, IRubyObject ch) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         return defout.callMethod(recv.getRuntime().getCurrentContext(), "putc", ch);
     }
 
     public static IRubyObject puts(IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         defout.callMethod(context, "puts", args);
 
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject print(IRubyObject recv, IRubyObject[] args) {
         IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
         ThreadContext context = recv.getRuntime().getCurrentContext();
 
         defout.callMethod(context, "print", args);
 
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject printf(IRubyObject recv, IRubyObject[] args) {
         if (args.length != 0) {
             IRubyObject defout = recv.getRuntime().getGlobalVariables().get("$>");
 
             if (!(args[0] instanceof RubyString)) {
                 defout = args[0];
                 args = ArgsUtil.popArray(args);
             }
 
             ThreadContext context = recv.getRuntime().getCurrentContext();
 
             defout.callMethod(context, "write", RubyKernel.sprintf(recv, args));
         }
 
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject readline(IRubyObject recv, IRubyObject[] args) {
         IRubyObject line = gets(recv, args);
 
         if (line.isNil()) {
             throw recv.getRuntime().newEOFError();
         }
 
         return line;
     }
 
     public static RubyArray readlines(IRubyObject recv, IRubyObject[] args) {
         return ((RubyArgsFile) recv.getRuntime().getGlobalVariables().get("$<")).readlines(args);
     }
 
     /** Returns value of $_.
      *
      * @throws TypeError if $_ is not a String or nil.
      * @return value of $_ as String.
      */
     private static RubyString getLastlineString(Ruby runtime) {
         IRubyObject line = runtime.getCurrentContext().getLastline();
 
         if (line.isNil()) {
             throw runtime.newTypeError("$_ value need to be String (nil given).");
         } else if (!(line instanceof RubyString)) {
             throw runtime.newTypeError("$_ value need to be String (" + line.getMetaClass().getName() + " given).");
         } else {
             return (RubyString) line;
         }
     }
 
     public static IRubyObject sub_bang(IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(recv.getRuntime()).sub_bang(args, block);
     }
 
     public static IRubyObject sub(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = (RubyString) getLastlineString(recv.getRuntime()).dup();
 
         if (!str.sub_bang(args, block).isNil()) {
             recv.getRuntime().getCurrentContext().setLastline(str);
         }
 
         return str;
     }
 
     public static IRubyObject gsub_bang(IRubyObject recv, IRubyObject[] args, Block block) {
         return getLastlineString(recv.getRuntime()).gsub_bang(args, block);
     }
 
     public static IRubyObject gsub(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString str = (RubyString) getLastlineString(recv.getRuntime()).dup();
 
         if (!str.gsub_bang(args, block).isNil()) {
             recv.getRuntime().getCurrentContext().setLastline(str);
         }
 
         return str;
     }
 
     public static IRubyObject chop_bang(IRubyObject recv) {
         return getLastlineString(recv.getRuntime()).chop_bang();
     }
 
     public static IRubyObject chop(IRubyObject recv) {
         RubyString str = getLastlineString(recv.getRuntime());
 
         if (str.getValue().length() > 0) {
             str = (RubyString) str.dup();
             str.chop_bang();
             recv.getRuntime().getCurrentContext().setLastline(str);
         }
 
         return str;
     }
 
     public static IRubyObject chomp_bang(IRubyObject recv, IRubyObject[] args) {
         return getLastlineString(recv.getRuntime()).chomp_bang(args);
     }
 
     public static IRubyObject chomp(IRubyObject recv, IRubyObject[] args) {
         RubyString str = getLastlineString(recv.getRuntime());
         RubyString dup = (RubyString) str.dup();
 
         if (dup.chomp_bang(args).isNil()) {
             return str;
         } 
 
         recv.getRuntime().getCurrentContext().setLastline(dup);
         return dup;
     }
 
     public static IRubyObject split(IRubyObject recv, IRubyObject[] args) {
         return getLastlineString(recv.getRuntime()).split(args);
     }
 
     public static IRubyObject scan(IRubyObject recv, IRubyObject pattern, Block block) {
         return getLastlineString(recv.getRuntime()).scan(pattern, block);
     }
 
     public static IRubyObject select(IRubyObject recv, IRubyObject[] args) {
         return IOMetaClass.select_static(recv.getRuntime(), args);
     }
 
     public static IRubyObject sleep(IRubyObject recv, IRubyObject seconds) {
         long milliseconds = (long) (seconds.convertToFloat().getDoubleValue() * 1000);
         long startTime = System.currentTimeMillis();
         
         RubyThread rubyThread = recv.getRuntime().getThreadService().getCurrentContext().getThread();
         try {
             rubyThread.sleep(milliseconds);
         } catch (InterruptedException iExcptn) {
         }
 
         return recv.getRuntime().newFixnum(
                 Math.round((System.currentTimeMillis() - startTime) / 1000.0));
     }
 
     // FIXME: Add at_exit and finalizers to exit, then make exit_bang not call those.
     public static IRubyObject exit(IRubyObject recv, IRubyObject[] args) {
         recv.getRuntime().secure(4);
 
         int status = 1;
         if (args.length > 0) {
             RubyObject argument = (RubyObject)args[0];
             if (argument instanceof RubyFixnum) {
                 status = RubyNumeric.fix2int(argument);
             } else {
                 status = argument.isFalse() ? 1 : 0;
             }
         }
 
         throw recv.getRuntime().newSystemExit(status);
     }
 
     public static IRubyObject exit_bang(IRubyObject recv, IRubyObject[] args) {
         return exit(recv, args);
     }
 
 
     /** Returns an Array with the names of all global variables.
      *
      */
     public static RubyArray global_variables(IRubyObject recv) {
         RubyArray globalVariables = recv.getRuntime().newArray();
 
         Iterator iter = recv.getRuntime().getGlobalVariables().getNames();
         while (iter.hasNext()) {
             String globalVariableName = (String) iter.next();
 
             globalVariables.append(recv.getRuntime().newString(globalVariableName));
         }
 
         return globalVariables;
     }
 
     /** Returns an Array with the names of all local variables.
      *
      */
     public static RubyArray local_variables(IRubyObject recv) {
         final Ruby runtime = recv.getRuntime();
         RubyArray localVariables = runtime.newArray();
         
         String[] names = runtime.getCurrentContext().getCurrentScope().getAllNamesInScope();
         for (int i = 0; i < names.length; i++) {
             localVariables.append(runtime.newString(names[i]));
         }
 
         return localVariables;
     }
 
     public static RubyBinding binding(IRubyObject recv, Block block) {
         // FIXME: Pass block into binding
         return recv.getRuntime().newBinding();
     }
 
     public static RubyBoolean block_given(IRubyObject recv, Block block) {
         return recv.getRuntime().newBoolean(recv.getRuntime().getCurrentContext().getPreviousFrame().getBlock().isGiven());
     }
 
     public static IRubyObject sprintf(IRubyObject recv, IRubyObject[] args) {
         if (args.length == 0) {
             throw recv.getRuntime().newArgumentError("sprintf must have at least one argument");
         }
 
         RubyString str = RubyString.stringValue(args[0]);
 
         RubyArray newArgs = recv.getRuntime().newArrayNoCopy(args);
         newArgs.shift();
 
         return str.format(newArgs);
     }
 
     public static IRubyObject raise(IRubyObject recv, IRubyObject[] args, Block block) {
         // FIXME: Pass block down?
         Arity.checkArgumentCount(recv.getRuntime(), args, 0, 3); 
         Ruby runtime = recv.getRuntime();
 
         if (args.length == 0) {
             IRubyObject lastException = runtime.getGlobalVariables().get("$!");
             if (lastException.isNil()) {
                 throw new RaiseException(runtime, runtime.getClass("RuntimeError"), "", false);
             } 
             throw new RaiseException((RubyException) lastException);
         }
 
         IRubyObject exception;
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         if (args.length == 1) {
             if (args[0] instanceof RubyString) {
                 throw new RaiseException((RubyException)runtime.getClass("RuntimeError").newInstance(args, block));
             }
             
             if (!args[0].respondsTo("exception")) {
                 throw runtime.newTypeError("exception class/object expected");
             }
             exception = args[0].callMethod(context, "exception");
         } else {
             if (!args[0].respondsTo("exception")) {
                 throw runtime.newTypeError("exception class/object expected");
             }
             
             exception = args[0].callMethod(context, "exception", args[1]);
         }
         
         if (!exception.isKindOf(runtime.getClass("Exception"))) {
             throw runtime.newTypeError("exception object expected");
         }
         
         if (args.length == 3) {
             ((RubyException) exception).set_backtrace(args[2]);
         }
         
         throw new RaiseException((RubyException) exception);
     }
     
     /**
      * Require.
      * MRI allows to require ever .rb files or ruby extension dll (.so or .dll depending on system).
      * we allow requiring either .rb files or jars.
      * @param recv ruby object used to call require (any object will do and it won't be used anyway).
      * @param name the name of the file to require
      **/
     public static IRubyObject require(IRubyObject recv, IRubyObject name, Block block) {
         if (recv.getRuntime().getLoadService().require(name.toString())) {
             return recv.getRuntime().getTrue();
         }
         return recv.getRuntime().getFalse();
     }
 
     public static IRubyObject load(IRubyObject recv, IRubyObject[] args, Block block) {
         RubyString file = args[0].convertToString();
         recv.getRuntime().getLoadService().load(file.toString());
         return recv.getRuntime().getTrue();
     }
 
     public static IRubyObject eval(IRubyObject recv, IRubyObject[] args, Block block) {
         if (args == null || args.length == 0) {
             throw recv.getRuntime().newArgumentError(args.length, 1);
         }
             
         RubyString src = args[0].convertToString();
         IRubyObject scope = null;
         String file = "(eval)";
         
         if (args.length > 1) {
             if (!args[1].isNil()) {
                 scope = args[1];
             }
             
             if (args.length > 2) {
                 file = args[2].toString();
             }
         }
         // FIXME: line number is not supported yet
         //int line = args.length > 3 ? RubyNumeric.fix2int(args[3]) : 1;
 
         recv.getRuntime().checkSafeString(src);
         ThreadContext context = recv.getRuntime().getCurrentContext();
         
         if (scope == null) {
             scope = recv.getRuntime().newBinding();
         }
         
         return recv.evalWithBinding(context, src, scope, file);
     }
 
     public static IRubyObject callcc(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
         runtime.getWarnings().warn("Kernel#callcc: Continuations are not implemented in JRuby and will not work");
         IRubyObject cc = runtime.getClass("Continuation").callMethod(runtime.getCurrentContext(),"new");
         cc.dataWrapStruct(block);
         return block.yield(runtime.getCurrentContext(),cc);
     }
 
     public static IRubyObject caller(IRubyObject recv, IRubyObject[] args, Block block) {
         int level = args.length > 0 ? RubyNumeric.fix2int(args[0]) : 1;
 
         if (level < 0) {
             throw recv.getRuntime().newArgumentError("negative level(" + level + ')');
         }
         
         return recv.getRuntime().getCurrentContext().createBacktrace(level, false);
     }
 
     public static IRubyObject rbCatch(IRubyObject recv, IRubyObject tag, Block block) {
         ThreadContext context = recv.getRuntime().getCurrentContext();
         try {
             context.pushCatch(tag.asSymbol());
             return block.yield(context, tag);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ThrowJump &&
                 je.getTarget().equals(tag.asSymbol())) {
                     return (IRubyObject) je.getValue();
             }
             throw je;
         } finally {
             context.popCatch();
         }
     }
 
     public static IRubyObject rbThrow(IRubyObject recv, IRubyObject[] args, Block block) {
         Ruby runtime = recv.getRuntime();
 
         String tag = args[0].asSymbol();
         String[] catches = runtime.getCurrentContext().getActiveCatches();
 
         String message = "uncaught throw '" + tag + '\'';
 
         //Ordering of array traversal not important, just intuitive
         for (int i = catches.length - 1 ; i >= 0 ; i--) {
             if (tag.equals(catches[i])) {
                 //Catch active, throw for catch to handle
                 JumpException je = new JumpException(JumpException.JumpType.ThrowJump);
 
                 je.setTarget(tag);
                 je.setValue(args.length > 1 ? args[1] : runtime.getNil());
                 throw je;
             }
         }
 
         //No catch active for this throw
         throw runtime.newNameError(message, tag);
     }
 
     public static IRubyObject trap(IRubyObject recv, IRubyObject[] args, Block block) {
         // FIXME: We can probably fake some basic signals, but obviously can't do everything. For now, stub.
         return recv.getRuntime().getNil();
     }
     
     public static IRubyObject warn(IRubyObject recv, IRubyObject message) {
         IRubyObject out = recv.getRuntime().getObject().getConstant("STDERR");
         RubyIO io = (RubyIO) out.convertToType("IO", "to_io", true); 
 
         io.puts(new IRubyObject[] { message });
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject set_trace_func(IRubyObject recv, IRubyObject trace_func, Block block) {
         if (trace_func.isNil()) {
             recv.getRuntime().setTraceFunction(null);
         } else if (!(trace_func instanceof RubyProc)) {
             throw recv.getRuntime().newTypeError("trace_func needs to be Proc.");
         } else {
             recv.getRuntime().setTraceFunction((RubyProc) trace_func);
         }
         return trace_func;
     }
 
     public static IRubyObject singleton_method_added(IRubyObject recv, IRubyObject symbolId, Block block) {
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject singleton_method_removed(IRubyObject recv, IRubyObject symbolId, Block block) {
         return recv.getRuntime().getNil();
     }
 
     public static IRubyObject singleton_method_undefined(IRubyObject recv, IRubyObject symbolId, Block block) {
         return recv.getRuntime().getNil();
     }
     
     
     public static RubyProc proc(IRubyObject recv, Block block) {
         return recv.getRuntime().newProc(true, block);
     }
 
     public static IRubyObject loop(IRubyObject recv, Block block) {
         ThreadContext context = recv.getRuntime().getCurrentContext();
         while (true) {
             try {
                 block.yield(context, recv.getRuntime().getNil());
 
                 Thread.yield();
             } catch (JumpException je) {
                 // JRUBY-530, specifically the Kernel#loop case:
                 // Kernel#loop always takes a block.  But what we're looking
                 // for here is breaking an iteration where the block is one 
                 // used inside loop's block, not loop's block itself.  Set the 
                 // appropriate flag on the JumpException if this is the case
                 // (the FCALLNODE case in EvaluationState will deal with it)
                 if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                     if (je.getTarget() != null && je.getTarget() != block) {
                         je.setBreakInKernelLoop(true);
                     }
                 }
                  
                 throw je;
             }
         }
     }
     public static IRubyObject test(IRubyObject recv, IRubyObject[] args) {
         int cmd = (int) args[0].convertToInteger().getLongValue();
         Ruby runtime = recv.getRuntime();
         File pwd = new File(recv.getRuntime().getCurrentDirectory());
         File file1 = new File(pwd, args[1].toString());
         Calendar calendar;
         switch (cmd) {
         //        ?A  | Time    | Last access time for file1
         //        ?b  | boolean | True if file1 is a block device
         //        ?c  | boolean | True if file1 is a character device
         //        ?C  | Time    | Last change time for file1
         //        ?d  | boolean | True if file1 exists and is a directory
         //        ?e  | boolean | True if file1 exists
         //        ?f  | boolean | True if file1 exists and is a regular file
         case 'f':
             return RubyBoolean.newBoolean(runtime, file1.isFile());
         //        ?g  | boolean | True if file1 has the \CF{setgid} bit
         //            |         | set (false under NT)
         //        ?G  | boolean | True if file1 exists and has a group
         //            |         | ownership equal to the caller's group
         //        ?k  | boolean | True if file1 exists and has the sticky bit set
         //        ?l  | boolean | True if file1 exists and is a symbolic link
         //        ?M  | Time    | Last modification time for file1
         case 'M':
             calendar = Calendar.getInstance();
             calendar.setTimeInMillis(file1.lastModified());
             return RubyTime.newTime(runtime, calendar);
         //        ?o  | boolean | True if file1 exists and is owned by
         //            |         | the caller's effective uid
         //        ?O  | boolean | True if file1 exists and is owned by
         //            |         | the caller's real uid
         //        ?p  | boolean | True if file1 exists and is a fifo
         //        ?r  | boolean | True if file1 is readable by the effective
         //            |         | uid/gid of the caller
         //        ?R  | boolean | True if file is readable by the real
         //            |         | uid/gid of the caller
         //        ?s  | int/nil | If file1 has nonzero size, return the size,
         //            |         | otherwise return nil
         //        ?S  | boolean | True if file1 exists and is a socket
         //        ?u  | boolean | True if file1 has the setuid bit set
         //        ?w  | boolean | True if file1 exists and is writable by
         //            |         | the effective uid/gid
         //        ?W  | boolean | True if file1 exists and is writable by
         //            |         | the real uid/gid
         //        ?x  | boolean | True if file1 exists and is executable by
         //            |         | the effective uid/gid
         //        ?X  | boolean | True if file1 exists and is executable by
         //            |         | the real uid/gid
         //        ?z  | boolean | True if file1 exists and has a zero length
         //
         //        Tests that take two files:
         //
         //        ?-  | boolean | True if file1 and file2 are identical
         //        ?=  | boolean | True if the modification times of file1
         //            |         | and file2 are equal
         //        ?<  | boolean | True if the modification time of file1
         //            |         | is prior to that of file2
         //        ?>  | boolean | True if the modification time of file1
         //            |         | is after that of file2
         }
         throw RaiseException.createNativeRaiseException(runtime, 
             new UnsupportedOperationException("test flag " + ((char) cmd) + " is not implemented"));
     }
 
     public static IRubyObject backquote(IRubyObject recv, IRubyObject aString) {
         Ruby runtime = recv.getRuntime();
         ByteArrayOutputStream output = new ByteArrayOutputStream();
         
         int resultCode = new ShellLauncher(runtime).runAndWait(new IRubyObject[] {aString}, output);
         
         recv.getRuntime().getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
         
         return RubyString.newString(recv.getRuntime(), output.toByteArray());
     }
     
     public static RubyInteger srand(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         long oldRandomSeed = runtime.getRandomSeed();
 
         if (args.length > 0) {
             RubyInteger integerSeed = 
                 (RubyInteger) args[0].convertToType("Integer", "to_i", true);
             runtime.setRandomSeed(integerSeed.getLongValue());
         } else {
             // Not sure how well this works, but it works much better than
             // just currentTimeMillis by itself.
             runtime.setRandomSeed(System.currentTimeMillis() ^
               recv.hashCode() ^ runtime.incrementRandomSeedSequence() ^
               runtime.getRandom().nextInt(Math.abs((int)runtime.getRandomSeed())));
         }
         runtime.getRandom().setSeed(runtime.getRandomSeed());
         return runtime.newFixnum(oldRandomSeed);
     }
 
     public static RubyNumeric rand(IRubyObject recv, IRubyObject[] args) {
         long ceil;
         if (args.length == 0) {
             ceil = 0;
         } else if (args.length == 1) {
             RubyInteger integerCeil = (RubyInteger) args[0].convertToType("Integer", "to_i", true);
             ceil = integerCeil.getLongValue();
             ceil = Math.abs(ceil);
         } else {
             throw recv.getRuntime().newArgumentError("wrong # of arguments(" + args.length + " for 1)");
         }
 
         if (ceil == 0) {
             double result = recv.getRuntime().getRandom().nextDouble();
             return RubyFloat.newFloat(recv.getRuntime(), result);
         }
         if(ceil > Integer.MAX_VALUE) {
             return recv.getRuntime().newFixnum(recv.getRuntime().getRandom().nextLong()%ceil);
         } else {
             return recv.getRuntime().newFixnum(recv.getRuntime().getRandom().nextInt((int)ceil));
         }
     }
 
     public static RubyBoolean system(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         int resultCode = new ShellLauncher(runtime).runAndWait(args);
         recv.getRuntime().getGlobalVariables().set("$?", RubyProcess.RubyStatus.newProcessStatus(runtime, resultCode));
         return runtime.newBoolean(resultCode == 0);
     }
     
     public static RubyArray to_a(IRubyObject recv) {
         recv.getRuntime().getWarnings().warn("default 'to_a' will be obsolete");
         return recv.getRuntime().newArray(recv);
     }
 }
diff --git a/src/org/jruby/RubyObject.java b/src/org/jruby/RubyObject.java
index def580c6c9..047bd174ab 100644
--- a/src/org/jruby/RubyObject.java
+++ b/src/org/jruby/RubyObject.java
@@ -1,1401 +1,1394 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2001 Chad Fowler <chadfowler@chadfowler.com>
  * Copyright (C) 2001 Alan Moore <alan_moore@gmx.net>
  * Copyright (C) 2001-2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2001-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2004-2006 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2005 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Ola Bini <ola.bini@ki.se>
  * Copyright (C) 2006 Miguel Covarrubias <mlcovarrubias@gmail.com>
  * 
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby;
 
 import org.jruby.evaluator.EvaluationState;
 import org.jruby.exceptions.JumpException;
 import org.jruby.internal.runtime.methods.DynamicMethod;
 import org.jruby.lexer.yacc.ISourcePosition;
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.runtime.callback.Callback;
 import org.jruby.util.IdUtil;
 import org.jruby.util.Sprintf;
 import org.jruby.util.collections.SinglyLinkedList;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Map;
 import org.jruby.runtime.ClassIndex;
 
 /**
  *
  * @author  jpetersen
  */
 public class RubyObject implements Cloneable, IRubyObject {
     // The class of this object
     private RubyClass metaClass;
 
     // The instance variables of this object.
     protected Map instanceVariables;
 
     // The two properties frozen and taint
     private boolean frozen;
     private boolean taint;
     protected boolean isTrue = true;
 
     public RubyObject(Ruby runtime, RubyClass metaClass) {
         this(runtime, metaClass, runtime.isObjectSpaceEnabled());
     }
 
     public RubyObject(Ruby runtime, RubyClass metaClass, boolean useObjectSpace) {
         this.metaClass = metaClass;
         this.frozen = false;
         this.taint = false;
 
         // Do not store any immediate objects into objectspace.
         if (useObjectSpace && !isImmediate()) {
             runtime.getObjectSpace().add(this);
         }
 
         // FIXME are there objects who shouldn't be tainted?
         // (mri: OBJSETUP)
         taint |= runtime.getSafeLevel() >= 3;
     }
 
     public void attachToObjectSpace() {
         getRuntime().getObjectSpace().add(this);
     }
     
     /**
      * This is overridden in the other concrete Java builtins to provide a fast way
      * to determine what type they are.
      */
     public int getNativeTypeIndex() {
         return ClassIndex.OBJECT;
     }
     
     /*
      *  Is object immediate (def: Fixnum, Symbol, true, false, nil?).
      */
     public boolean isImmediate() {
     	return false;
     }
 
     /**
      * Create a new meta class.
      *
      * @since Ruby 1.6.7
      */
     public RubyClass makeMetaClass(RubyClass superClass, SinglyLinkedList parentCRef) {
         RubyClass klass = new MetaClass(getRuntime(), superClass, getMetaClass().getAllocator(), parentCRef);
         setMetaClass(klass);
 		
         klass.setInstanceVariable("__attached__", this);
 
         if (this instanceof RubyClass && isSingleton()) { // could be pulled down to RubyClass in future
             klass.setMetaClass(klass);
             klass.setSuperClass(((RubyClass)this).getSuperClass().getRealClass().getMetaClass());
         } else {
             klass.setMetaClass(superClass.getRealClass().getMetaClass());
         }
         
         // use same ClassIndex as metaclass, since we're technically still of that type 
         klass.index = superClass.index;
         return klass;
     }
         
     public boolean isSingleton() {
         return false;
     }
 
     public Class getJavaClass() {
         return IRubyObject.class;
     }
     
     public static void puts(Object obj) {
         System.out.println(obj.toString());
     }
 
     /**
      * This method is just a wrapper around the Ruby "==" method,
      * provided so that RubyObjects can be used as keys in the Java
      * HashMap object underlying RubyHash.
      */
     public boolean equals(Object other) {
         return other == this || other instanceof IRubyObject && callMethod(getRuntime().getCurrentContext(), "==", (IRubyObject) other).isTrue();
     }
 
     public String toString() {
         return callMethod(getRuntime().getCurrentContext(), "to_s").toString();
     }
 
     /** Getter for property ruby.
      * @return Value of property ruby.
      */
     public Ruby getRuntime() {
         return metaClass.getRuntime();
     }
     
     public boolean safeHasInstanceVariables() {
         return instanceVariables != null && instanceVariables.size() > 0;
     }
     
     public Map safeGetInstanceVariables() {
         return instanceVariables == null ? null : getInstanceVariablesSnapshot();
     }
 
     public IRubyObject removeInstanceVariable(String name) {
         return (IRubyObject) getInstanceVariables().remove(name);
     }
 
     /**
      * Returns an unmodifiable snapshot of the current state of instance variables.
      * This method synchronizes access to avoid deadlocks.
      */
     public Map getInstanceVariablesSnapshot() {
         synchronized(getInstanceVariables()) {
             return Collections.unmodifiableMap(new HashMap(getInstanceVariables()));
         }
     }
 
     public Map getInstanceVariables() {
     	// TODO: double checking may or may not be safe enough here
     	if (instanceVariables == null) {
 	    	synchronized (this) {
 	    		if (instanceVariables == null) {
                             instanceVariables = Collections.synchronizedMap(new HashMap());
 	    		}
 	    	}
     	}
         return instanceVariables;
     }
 
     public void setInstanceVariables(Map instanceVariables) {
         this.instanceVariables = Collections.synchronizedMap(instanceVariables);
     }
 
     /**
      * if exist return the meta-class else return the type of the object.
      *
      */
     public RubyClass getMetaClass() {
         return metaClass;
     }
 
     public void setMetaClass(RubyClass metaClass) {
         this.metaClass = metaClass;
     }
 
     /**
      * Gets the frozen.
      * @return Returns a boolean
      */
     public boolean isFrozen() {
         return frozen;
     }
 
     /**
      * Sets the frozen.
      * @param frozen The frozen to set
      */
     public void setFrozen(boolean frozen) {
         this.frozen = frozen;
     }
 
     /** rb_frozen_class_p
     *
     */
    protected void testFrozen(String message) {
        if (isFrozen()) {
            throw getRuntime().newFrozenError(message + getMetaClass().getName());
        }
    }
 
    protected void checkFrozen() {
        testFrozen("can't modify frozen ");
    }
 
     /**
      * Gets the taint.
      * @return Returns a boolean
      */
     public boolean isTaint() {
         return taint;
     }
 
     /**
      * Sets the taint.
      * @param taint The taint to set
      */
     public void setTaint(boolean taint) {
         this.taint = taint;
     }
 
     public boolean isNil() {
         return false;
     }
 
     public final boolean isTrue() {
         return isTrue;
     }
 
     public final boolean isFalse() {
         return !isTrue;
     }
 
     public boolean respondsTo(String name) {
         if(getMetaClass().searchMethod("respond_to?") == getRuntime().getRespondToMethod()) {
             return getMetaClass().isMethodBound(name, false);
         } else {
             return callMethod(getRuntime().getCurrentContext(),"respond_to?",getRuntime().newSymbol(name)).isTrue();
         }
     }
 
     public boolean isKindOf(RubyModule type) {
         return getMetaClass().hasModuleInHierarchy(type);
     }
 
     /** rb_singleton_class
      *
      */    
     public RubyClass getSingletonClass() {
         RubyClass klass;
         
         if (getMetaClass().isSingleton() && getMetaClass().getInstanceVariable("__attached__") == this) {
             klass = getMetaClass();            
         } else {
             klass = makeMetaClass(getMetaClass(), getMetaClass().getCRef());
         }
         
         klass.setTaint(isTaint());
         klass.setFrozen(isFrozen());
         
         return klass;
     }
     
     /** rb_singleton_class_clone
      *
      */
     public RubyClass getSingletonClassClone() {
        RubyClass klass = getMetaClass();
 
        if (!klass.isSingleton()) {
            return klass;
 		}
        
        MetaClass clone = new MetaClass(getRuntime(), klass.getSuperClass(), getMetaClass().getAllocator(), getMetaClass().getCRef());
        clone.setFrozen(klass.isFrozen());
        clone.setTaint(klass.isTaint());
 
        if (this instanceof RubyClass) {
            clone.setMetaClass(clone);
        } else {
            clone.setMetaClass(klass.getSingletonClassClone());
        }
        
        if (klass.safeHasInstanceVariables()) {
            clone.setInstanceVariables(new HashMap(klass.getInstanceVariables()));
        }
 
        klass.cloneMethods(clone);
 
        clone.getMetaClass().setInstanceVariable("__attached__", clone);
 
        return clone;
     }    
 
     /** rb_define_singleton_method
      *
      */
     public void defineSingletonMethod(String name, Callback method) {
         getSingletonClass().defineMethod(name, method);
     }
 
     /** init_copy
      * 
      */
     public static void initCopy(IRubyObject clone, IRubyObject original) {
         assert original != null;
         assert !clone.isFrozen() : "frozen object (" + clone.getMetaClass().getName() + ") allocated";
 
         clone.setInstanceVariables(new HashMap(original.getInstanceVariables()));
         /* FIXME: finalizer should be dupped here */
         clone.callMethod(clone.getRuntime().getCurrentContext(), "initialize_copy", original);
     }
 
     /** OBJ_INFECT
      *
      */
     public IRubyObject infectBy(IRubyObject obj) {
         setTaint(isTaint() || obj.isTaint());
 
         return this;
     }
 
     public IRubyObject callSuper(ThreadContext context, IRubyObject[] args, Block block) {
         RubyModule klazz = context.getFrameKlazz();
 
         RubyClass superClass = klazz.getSuperClass();
         
         assert superClass != null : "Superclass should always be something for " + klazz.getBaseName();
 
         return callMethod(context, superClass, context.getFrameName(), args, CallType.SUPER, block);
     }    
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args) {
         return callMethod(context, getMetaClass(), name, args, CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, Block block) {
         return callMethod(context, getMetaClass(), name, args, CallType.FUNCTIONAL, block);
     }
     
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name,
             IRubyObject[] args, CallType callType) {
         return callMethod(context, getMetaClass(), name, args, callType, Block.NULL_BLOCK);
     }
     
     public IRubyObject callMethod(ThreadContext context, String name,
             IRubyObject[] args, CallType callType, Block block) {
         return callMethod(context, getMetaClass(), name, args, callType, block);
     }
 
     public IRubyObject callMethod(ThreadContext context,byte switchValue, String name,
                                   IRubyObject arg) {
         return callMethod(context,getMetaClass(),switchValue,name,new IRubyObject[]{arg},CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context,byte switchValue, String name,
                                   IRubyObject[] args) {
         return callMethod(context,getMetaClass(),switchValue,name,args,CallType.FUNCTIONAL, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context, byte switchValue, String name,
                                   IRubyObject[] args, CallType callType) {
         return callMethod(context,getMetaClass(),switchValue,name,args,callType, Block.NULL_BLOCK);
     }
     
     /**
      * Used by the compiler to ease calling indexed methods, also to handle visibility.
      * NOTE: THIS IS NOT THE SAME AS THE SWITCHVALUE VERSIONS.
      */
     public IRubyObject compilerCallMethodWithIndex(ThreadContext context, byte methodIndex, String name, IRubyObject[] args, IRubyObject self, CallType callType, Block block) {
         RubyModule module = getMetaClass();
         
         if (module.index != 0) {
             return callMethod(context, module, getRuntime().getSelectorTable().table[module.index][methodIndex], name, args, callType, block);
         }
         
         return compilerCallMethod(context, name, args, self, callType, block);
     }
     
     /**
      * Used by the compiler to handle visibility
      */
     public IRubyObject compilerCallMethod(ThreadContext context, String name,
             IRubyObject[] args, IRubyObject self, CallType callType, Block block) {
         assert args != null;
         DynamicMethod method = null;
         RubyModule rubyclass = getMetaClass();
         method = rubyclass.searchMethod(name);
         
         IRubyObject mmResult = callMethodMissingIfNecessary(context, this, method, name, args, self, callType, block);
         if (mmResult != null) {
             return mmResult;
         }
 
         return method.call(context, this, rubyclass, name, args, false, block);
     }
     
     public static IRubyObject callMethodMissingIfNecessary(ThreadContext context, IRubyObject receiver, DynamicMethod method, String name,
             IRubyObject[] args, IRubyObject self, CallType callType, Block block) {
         if (method.isUndefined() ||
             !(name.equals("method_missing") ||
               method.isCallableFrom(self, callType))) {
 
             if (callType == CallType.SUPER) {
                 throw self.getRuntime().newNameError("super: no superclass method '" + name + "'", name);
             }
 
             // store call information so method_missing impl can use it
             context.setLastCallStatus(callType);
 
             if (name.equals("method_missing")) {
                 return RubyKernel.method_missing(self, args, block);
             }
 
 
             IRubyObject[] newArgs = new IRubyObject[args.length + 1];
             System.arraycopy(args, 0, newArgs, 1, args.length);
             newArgs[0] = RubySymbol.newSymbol(self.getRuntime(), name);
 
             return receiver.callMethod(context, "method_missing", newArgs, block);
         }
         
         // kludgy.
         return null;
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, byte switchvalue, String name,
             IRubyObject[] args, CallType callType) {
         return callMethod(context, rubyclass, switchvalue, name, args, callType, Block.NULL_BLOCK);
     }
 
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, byte switchvalue, String name,
             IRubyObject[] args, CallType callType, Block block) {
         return callMethod(context, rubyclass, name, args, callType, block);
     }
     
     /**
      *
      */
     public IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, String name,
             IRubyObject[] args, CallType callType, Block block) {
         assert args != null;
         DynamicMethod method = null;
         method = rubyclass.searchMethod(name);
         
         IRubyObject mmResult = callMethodMissingIfNecessary(context, this, method, name, args, context.getFrameSelf(), callType, block);
         if (mmResult != null) {
             return mmResult;
         }
 
         return method.call(context, this, rubyclass, name, args, false, block);
     }
 
     public IRubyObject callMethod(ThreadContext context, String name) {
         return callMethod(context, name, IRubyObject.NULL_ARRAY, null, Block.NULL_BLOCK);
     }
 
     public IRubyObject callMethod(ThreadContext context, String name, Block block) {
         return callMethod(context, name, IRubyObject.NULL_ARRAY, null, block);
     }
 
     /**
      * rb_funcall
      *
      */
     public IRubyObject callMethod(ThreadContext context, String name, IRubyObject arg) {
         return callMethod(context, name, new IRubyObject[] { arg });
     }
 
     public IRubyObject instance_variable_get(IRubyObject var) {
     	String varName = var.asSymbol();
 
     	if (!IdUtil.isInstanceVariable(varName)) {
     		throw getRuntime().newNameError("`" + varName + "' is not allowable as an instance variable name", varName);
     	}
 
     	IRubyObject variable = getInstanceVariable(varName);
 
     	// Pickaxe v2 says no var should show NameError, but ruby only sends back nil..
     	return variable == null ? getRuntime().getNil() : variable;
     }
 
     public IRubyObject getInstanceVariable(String name) {
         return (IRubyObject) getInstanceVariables().get(name);
     }
 
     public IRubyObject instance_variable_set(IRubyObject var, IRubyObject value) {
     	String varName = var.asSymbol();
 
     	if (!IdUtil.isInstanceVariable(varName)) {
     		throw getRuntime().newNameError("`" + varName + "' is not allowable as an instance variable name", varName);
     	}
 
     	return setInstanceVariable(var.asSymbol(), value);
     }
 
     public IRubyObject setInstanceVariable(String name, IRubyObject value,
             String taintError, String freezeError) {
         if (isTaint() && getRuntime().getSafeLevel() >= 4) {
             throw getRuntime().newSecurityError(taintError);
         }
         testFrozen(freezeError);
 
         getInstanceVariables().put(name, value);
 
         return value;
     }
 
     /** rb_iv_set / rb_ivar_set
      *
      */
     public IRubyObject setInstanceVariable(String name, IRubyObject value) {
         return setInstanceVariable(name, value,
                 "Insecure: can't modify instance variable", "");
     }
 
     public Iterator instanceVariableNames() {
         return getInstanceVariables().keySet().iterator();
     }
 
     public void callInit(IRubyObject[] args, Block block) {
         callMethod(getRuntime().getCurrentContext(), "initialize", args, block);
     }
 
     /** rb_to_id
      *
      */
     public String asSymbol() {
         throw getRuntime().newTypeError(inspect().toString() + " is not a symbol");
     }
 
     public static String trueFalseNil(IRubyObject v) {
         return trueFalseNil(v.getMetaClass().getName());
     }
 
     public static String trueFalseNil(String v) {
         if("TrueClass".equals(v)) {
             return "true";
         } else if("FalseClass".equals(v)) {
             return "false";
         } else if("NilClass".equals(v)) {
             return "nil";
         }
         return v;
     }
 
     public RubyArray convertToArray() {
-        return (RubyArray) convertToType("Array", "to_ary", true);
+        return (RubyArray) convertToType(getRuntime().getArray(), "to_ary", true);
     }
 
     public RubyFloat convertToFloat() {
-        return (RubyFloat) convertToType("Float", "to_f", true);
+        return (RubyFloat) convertToType(getRuntime().getClass("Float"), "to_f", true);
     }
 
     public RubyInteger convertToInteger() {
-        return (RubyInteger) convertToType("Integer", "to_int", true);
+        return (RubyInteger) convertToType(getRuntime().getClass("Integer"), "to_int", true);
     }
 
     public RubyString convertToString() {
-        return (RubyString) convertToType("String", "to_str", true);
+        return (RubyString) convertToType(getRuntime().getString(), "to_str", true);
     }
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToTypeWithCheck(java.lang.String, java.lang.String)
      */
-    public IRubyObject convertToTypeWithCheck(Class cls, String targetType, String convertMethod) {
-        if(cls.isInstance(this)) {
-            return this;
-        }
-
-        IRubyObject value = convertToType(targetType, convertMethod, false);
-        if (value.isNil()) {
-            return value;
-        }
-
-        if (!cls.isInstance(value)) {
-            throw getRuntime().newTypeError(value.getMetaClass().getName() + "#" + convertMethod +
-                    " should return " + targetType);
-        }
-
-        return value;
+    public IRubyObject convertToTypeWithCheck(String targetType, String convertMethod) {
+        return convertToType(getRuntime().getClass(targetType), convertMethod, false, true, false);
     }
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToTypeWithCheck(java.lang.String, java.lang.String)
      */
-    public IRubyObject convertToTypeWithCheck(String targetType, String convertMethod) {
-        if (targetType.equals(getMetaClass().getName())) {
-            return this;
-        }
-
-        IRubyObject value = convertToType(targetType, convertMethod, false);
-        if (value.isNil()) {
-            return value;
-        }
-
-        if (!targetType.equals(value.getMetaClass().getName())) {
-            throw getRuntime().newTypeError(value.getMetaClass().getName() + "#" + convertMethod +
-                    " should return " + targetType);
-        }
-
-        return value;
+    public IRubyObject convertToTypeWithCheck(RubyClass targetType, String convertMethod) {
+        return convertToType(targetType, convertMethod, false, true, false);
     }
 
     /*
      * @see org.jruby.runtime.builtin.IRubyObject#convertToType(java.lang.String, java.lang.String, boolean)
      */
     public IRubyObject convertToType(String targetType, String convertMethod, boolean raise) {
-        // No need to convert something already of the correct type.
-        // XXXEnebo - Could this pass actual class reference instead of String?
-        if (targetType.equals(getMetaClass().getName())) {
+        return convertToType(getRuntime().getClass(targetType), convertMethod, raise, false, false);
+    }
+
+    /*
+     * @see org.jruby.runtime.builtin.IRubyObject#convertToType(java.lang.String, java.lang.String, boolean)
+     */
+    public IRubyObject convertToType(RubyClass targetType, String convertMethod, boolean raise) {
+        return convertToType(targetType, convertMethod, raise, false, false);
+    }
+    
+    public IRubyObject convertToType(RubyClass targetType, String convertMethod, boolean raiseOnMissingMethod, boolean raiseOnWrongTypeResult, boolean allowNilThrough) {
+        if (isKindOf(targetType)) {
             return this;
         }
         
         if (!respondsTo(convertMethod)) {
-            if (raise) {
+            if (raiseOnMissingMethod) {
                 throw getRuntime().newTypeError(
                     "can't convert " + trueFalseNil(getMetaClass().getName()) + " into " + trueFalseNil(targetType));
             } 
 
             return getRuntime().getNil();
         }
-        return callMethod(getRuntime().getCurrentContext(), convertMethod);
+        
+        IRubyObject value = callMethod(getRuntime().getCurrentContext(), convertMethod);
+        
+        if (allowNilThrough && value.isNil()) {
+            return value;
+        }
+        
+        if (raiseOnWrongTypeResult && !value.isKindOf(targetType)) {
+            throw getRuntime().newTypeError(getMetaClass().getName() + "#" + convertMethod +
+                    " should return " + targetType);
+        }
+        
+        return value;
     }
 
     /** rb_obj_as_string
      */
     public RubyString asString() {
         if (this instanceof RubyString) return (RubyString) this;
         
         IRubyObject str = this.callMethod(getRuntime().getCurrentContext(), "to_s");
         
         if (!(str instanceof RubyString)) str = anyToString();
 
         return (RubyString) str;
     }
     
     /** rb_check_string_type
      *
      */
     public IRubyObject checkStringType() {
         IRubyObject str = convertToTypeWithCheck("String","to_str");
         if(!str.isNil() && !(str instanceof RubyString)) {
             str = getRuntime().newString("");
         }
         return str;
     }
 
     /** rb_check_array_type
     *
     */    
     public IRubyObject checkArrayType() {
         return convertToTypeWithCheck("Array","to_ary");
     }
 
     /** specific_eval
      *
      */
     public IRubyObject specificEval(RubyModule mod, IRubyObject[] args, Block block) {
         if (block.isGiven()) {
             if (args.length > 0) throw getRuntime().newArgumentError(args.length, 0);
 
             return yieldUnder(mod, block);
         }
         ThreadContext tc = getRuntime().getCurrentContext();
 
         if (args.length == 0) {
 		    throw getRuntime().newArgumentError("block not supplied");
 		} else if (args.length > 3) {
 		    String lastFuncName = tc.getFrameName();
 		    throw getRuntime().newArgumentError(
 		        "wrong # of arguments: " + lastFuncName + "(src) or " + lastFuncName + "{..}");
 		}
 		/*
 		if (ruby.getSecurityLevel() >= 4) {
 			Check_Type(argv[0], T_STRING);
 		} else {
 			Check_SafeStr(argv[0]);
 		}
 		*/
         
         // We just want the TypeError if the argument doesn't convert to a String (JRUBY-386)
         args[0].convertToString();
         
 		IRubyObject file = args.length > 1 ? args[1] : getRuntime().newString("(eval)");
 		IRubyObject line = args.length > 2 ? args[2] : RubyFixnum.one(getRuntime());
 
 		Visibility savedVisibility = tc.getCurrentVisibility();
         tc.setCurrentVisibility(Visibility.PUBLIC);
 		try {
 		    return evalUnder(mod, args[0], file, line);
 		} finally {
             tc.setCurrentVisibility(savedVisibility);
 		}
     }
 
     public IRubyObject evalUnder(RubyModule under, IRubyObject src, IRubyObject file, IRubyObject line) {
         /*
         if (ruby_safe_level >= 4) {
         	Check_Type(src, T_STRING);
         } else {
         	Check_SafeStr(src);
         	}
         */
         return under.executeUnder(new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                 IRubyObject source = args[1];
                 IRubyObject filename = args[2];
                 // FIXME: lineNumber is not supported
                 //IRubyObject lineNumber = args[3];
 
                 return args[0].evalSimple(source.getRuntime().getCurrentContext(),
                                   source, ((RubyString) filename).toString());
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         }, new IRubyObject[] { this, src, file, line }, Block.NULL_BLOCK);
     }
 
     private IRubyObject yieldUnder(RubyModule under, Block block) {
         return under.executeUnder(new Callback() {
             public IRubyObject execute(IRubyObject self, IRubyObject[] args, Block block) {
                 ThreadContext context = getRuntime().getCurrentContext();
 
                 Visibility savedVisibility = block.getVisibility();
 
                 block.setVisibility(Visibility.PUBLIC);
                 try {
                     IRubyObject valueInYield = args[0];
                     IRubyObject selfInYield = args[0];
                     return block.yield(context, valueInYield, selfInYield, context.getRubyClass(), false);
                     //TODO: Should next and return also catch here?
                 } catch (JumpException je) {
                 	if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 		return (IRubyObject) je.getValue();
                 	} 
 
                     throw je;
                 } finally {
                     block.setVisibility(savedVisibility);
                 }
             }
 
             public Arity getArity() {
                 return Arity.optional();
             }
         }, new IRubyObject[] { this }, block);
     }
 
     /* (non-Javadoc)
      * @see org.jruby.runtime.builtin.IRubyObject#evalWithBinding(org.jruby.runtime.builtin.IRubyObject, org.jruby.runtime.builtin.IRubyObject, java.lang.String)
      */
     public IRubyObject evalWithBinding(ThreadContext context, IRubyObject src, IRubyObject scope, String file) {
         // both of these are ensured by the (very few) callers
         assert !scope.isNil();
         assert file != null;
 
         ThreadContext threadContext = getRuntime().getCurrentContext();
 
         ISourcePosition savedPosition = threadContext.getPosition();
         IRubyObject result = getRuntime().getNil();
 
         IRubyObject newSelf = null;
 
         if (!(scope instanceof RubyBinding)) {
             if (scope instanceof RubyProc) {
                 scope = ((RubyProc) scope).binding();
             } else {
                 // bomb out, it's not a binding or a proc
                 throw getRuntime().newTypeError("wrong argument type " + scope.getMetaClass() + " (expected Proc/Binding)");
             }
         }
 
         Block blockOfBinding = ((RubyBinding)scope).getBlock();
         try {
             // Binding provided for scope, use it
             threadContext.preEvalWithBinding(blockOfBinding);
             newSelf = threadContext.getFrameSelf();
 
             result = EvaluationState.eval(getRuntime(), threadContext, getRuntime().parse(src.toString(), file, blockOfBinding.getDynamicScope()), newSelf, blockOfBinding);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 throw getRuntime().newLocalJumpError("unexpected return");
             } else if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw getRuntime().newLocalJumpError("unexpected break");
             }
             throw je;
         } finally {
             threadContext.postEvalWithBinding();
 
             // restore position
             threadContext.setPosition(savedPosition);
         }
         return result;
     }
 
     /* (non-Javadoc)
      * @see org.jruby.runtime.builtin.IRubyObject#evalSimple(org.jruby.runtime.builtin.IRubyObject, java.lang.String)
      */
     public IRubyObject evalSimple(ThreadContext context, IRubyObject src, String file) {
         // this is ensured by the callers
         assert file != null;
 
         ISourcePosition savedPosition = context.getPosition();
 
         // no binding, just eval in "current" frame (caller's frame)
         try {
             return EvaluationState.eval(getRuntime(), context, getRuntime().parse(src.toString(), file, context.getCurrentScope()), this, Block.NULL_BLOCK);
         } catch (JumpException je) {
             if (je.getJumpType() == JumpException.JumpType.ReturnJump) {
                 throw getRuntime().newLocalJumpError("unexpected return");
             } else if (je.getJumpType() == JumpException.JumpType.BreakJump) {
                 throw getRuntime().newLocalJumpError("unexpected break");
             }
             throw je;
         } finally {
             // restore position
             context.setPosition(savedPosition);
         }
     }
 
     // Methods of the Object class (rb_obj_*):
 
     /** rb_obj_equal
      *
      */
     public IRubyObject obj_equal(IRubyObject obj) {
         return this == obj ? getRuntime().getTrue() : getRuntime().getFalse();
 //        if (isNil()) {
 //            return getRuntime().newBoolean(obj.isNil());
 //        }
 //        return getRuntime().newBoolean(this == obj);
     }
 
 	public IRubyObject same(IRubyObject other) {
 		return this == other ? getRuntime().getTrue() : getRuntime().getFalse();
 	}
 
     
     /** rb_obj_init_copy
      * 
      */
 	public IRubyObject initialize_copy(IRubyObject original) {
 	    if (this == original) return this;
 	    
 	    checkFrozen();
         
         if (getMetaClass().getRealClass() != original.getMetaClass().getRealClass()) {
 	            throw getRuntime().newTypeError("initialize_copy should take same class object");
 	    }
 
 	    return this;
 	}
 
     /**
      * respond_to?( aSymbol, includePriv=false ) -> true or false
      *
      * Returns true if this object responds to the given method. Private
      * methods are included in the search only if the optional second
      * parameter evaluates to true.
      *
      * @return true if this responds to the given method
      */
     public RubyBoolean respond_to(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 1, 2);
 
         String name = args[0].asSymbol();
         boolean includePrivate = args.length > 1 ? args[1].isTrue() : false;
 
         return getRuntime().newBoolean(getMetaClass().isMethodBound(name, !includePrivate));
     }
 
     /** Return the internal id of an object.
      *
      * <i>CRuby function: rb_obj_id</i>
      *
      */
     public synchronized RubyFixnum id() {
         return getRuntime().newFixnum(getRuntime().getObjectSpace().idOf(this));
     }
 
     public synchronized RubyFixnum id_deprecated() {
         getRuntime().getWarnings().warn("Object#id will be deprecated; use Object#object_id");
         return getRuntime().newFixnum(getRuntime().getObjectSpace().idOf(this));
     }
     
     public RubyFixnum hash() {
         return getRuntime().newFixnum(System.identityHashCode(this));
     }
 
     public int hashCode() {
         IRubyObject hashValue = callMethod(getRuntime().getCurrentContext(), "hash");
         
         if (hashValue instanceof RubyFixnum) return (int) RubyNumeric.fix2long(hashValue); 
         
         return System.identityHashCode(this);
     }
 
     /** rb_obj_type
      *
      */
     public RubyClass type() {
         return getMetaClass().getRealClass();
     }
 
     public RubyClass type_deprecated() {
         getRuntime().getWarnings().warn("Object#type is deprecated; use Object#class");
         return type();
     }
 
     /** rb_obj_clone
      *  should be overriden only by: Proc, Method, UnboundedMethod, Binding
      */
     public IRubyObject rbClone() {
         if (isImmediate()) { // rb_special_const_p(obj) equivalent
             throw getRuntime().newTypeError("can't clone " + getMetaClass().getName());
         }
         
         IRubyObject clone = doClone();
         clone.setMetaClass(getSingletonClassClone());
         clone.setTaint(isTaint());
         initCopy(clone, this);
         clone.setFrozen(isFrozen());
         return clone;
     }
 
     // Hack: allow RubyModule and RubyClass to override the allocation and return the the correct Java instance
     // Cloning a class object doesn't work otherwise and I don't really understand why --sma
     protected IRubyObject doClone() {
         RubyClass realClass = getMetaClass().getRealClass();
     	return realClass.getAllocator().allocate(getRuntime(), realClass);
     }
 
     public IRubyObject display(IRubyObject[] args) {
         IRubyObject port = args.length == 0
             ? getRuntime().getGlobalVariables().get("$>") : args[0];
 
         port.callMethod(getRuntime().getCurrentContext(), "write", this);
 
         return getRuntime().getNil();
     }
 
     /** rb_obj_dup
      *  should be overriden only by: Proc
      */
     public IRubyObject dup() {
         if (isImmediate()) {
             throw getRuntime().newTypeError("can't dup " + getMetaClass().getName());
         }        
         
         IRubyObject dup = doClone();    
 
         dup.setMetaClass(type());
         dup.setFrozen(false);
         dup.setTaint(isTaint());
         
         initCopy(dup, this);
 
         return dup;
     }
 
     /** rb_obj_tainted
      *
      */
     public RubyBoolean tainted() {
         return getRuntime().newBoolean(isTaint());
     }
 
     /** rb_obj_taint
      *
      */
     public IRubyObject taint() {
         getRuntime().secure(4);
         if (!isTaint()) {
         	testFrozen("object");
             setTaint(true);
         }
         return this;
     }
 
     /** rb_obj_untaint
      *
      */
     public IRubyObject untaint() {
         getRuntime().secure(3);
         if (isTaint()) {
         	testFrozen("object");
             setTaint(false);
         }
         return this;
     }
 
     /** Freeze an object.
      *
      * rb_obj_freeze
      *
      */
     public IRubyObject freeze() {
         if (getRuntime().getSafeLevel() >= 4 && isTaint()) {
             throw getRuntime().newSecurityError("Insecure: can't freeze object");
         }
         setFrozen(true);
         return this;
     }
 
     /** rb_obj_frozen_p
      *
      */
     public RubyBoolean frozen() {
         return getRuntime().newBoolean(isFrozen());
     }
 
     /** rb_obj_inspect
      *
      */
     public IRubyObject inspect() {
         if ((!isImmediate()) &&
                 // TYPE(obj) == T_OBJECT
                 !(this instanceof RubyClass) &&
                 this != getRuntime().getObject() &&
                 this != getRuntime().getClass("Module") &&
                 !(this instanceof RubyModule) &&
                 safeHasInstanceVariables()) {
 
             StringBuffer part = new StringBuffer();
             String cname = getMetaClass().getRealClass().getName();
             part.append("#<").append(cname).append(":0x");
             part.append(Integer.toHexString(System.identityHashCode(this)));
             if(!getRuntime().registerInspecting(this)) {
                 /* 6:tags 16:addr 1:eos */
                 part.append(" ...>");
                 return getRuntime().newString(part.toString());
             }
             try {
                 String sep = "";
                 Map iVars = getInstanceVariablesSnapshot();
                 for (Iterator iter = iVars.keySet().iterator(); iter.hasNext();) {
                     String name = (String) iter.next();
                     if(IdUtil.isInstanceVariable(name)) {
                         part.append(sep);
                         part.append(" ");
                         part.append(name);
                         part.append("=");
                         part.append(((IRubyObject)(iVars.get(name))).callMethod(getRuntime().getCurrentContext(), "inspect"));
                         sep = ",";
                     }
                 }
                 part.append(">");
                 return getRuntime().newString(part.toString());
             } finally {
                 getRuntime().unregisterInspecting(this);
             }
         }
         return callMethod(getRuntime().getCurrentContext(), "to_s");
     }
 
     /** rb_obj_is_instance_of
      *
      */
     public RubyBoolean instance_of(IRubyObject type) {
         return getRuntime().newBoolean(type() == type);
     }
 
 
     public RubyArray instance_variables() {
         ArrayList names = new ArrayList();
         for(Iterator iter = getInstanceVariablesSnapshot().keySet().iterator();iter.hasNext();) {
             String name = (String) iter.next();
 
             // Do not include constants which also get stored in instance var list in classes.
             if (IdUtil.isInstanceVariable(name)) {
                 names.add(getRuntime().newString(name));
             }
         }
         return getRuntime().newArray(names);
     }
 
     /** rb_obj_is_kind_of
      *
      */
     public RubyBoolean kind_of(IRubyObject type) {
         // TODO: Generalize this type-checking code into IRubyObject helper.
         if (!type.isKindOf(getRuntime().getClass("Module"))) {
             // TODO: newTypeError does not offer enough for ruby error string...
             throw getRuntime().newTypeError(type, getRuntime().getClass("Module"));
         }
 
         return getRuntime().newBoolean(isKindOf((RubyModule)type));
     }
 
     /** rb_obj_methods
      *
      */
     public IRubyObject methods(IRubyObject[] args) {
     	Arity.checkArgumentCount(getRuntime(), args, 0, 1);
 
     	if (args.length == 0) {
     		args = new IRubyObject[] { getRuntime().getTrue() };
     	}
 
         return getMetaClass().instance_methods(args);
     }
 
 	public IRubyObject public_methods(IRubyObject[] args) {
         return getMetaClass().public_instance_methods(args);
 	}
 
     /** rb_obj_protected_methods
      *
      */
     public IRubyObject protected_methods() {
         return getMetaClass().protected_instance_methods(new IRubyObject[] { getRuntime().getTrue()});
     }
 
     /** rb_obj_private_methods
      *
      */
     public IRubyObject private_methods() {
         return getMetaClass().private_instance_methods(new IRubyObject[] { getRuntime().getTrue()});
     }
 
     /** rb_obj_singleton_methods
      *
      */
     // TODO: This is almost RubyModule#instance_methods on the metaClass.  Perhaps refactor.
     public RubyArray singleton_methods(IRubyObject[] args) {
         boolean all = true;
         if(Arity.checkArgumentCount(getRuntime(), args,0,1) == 1) {
             all = args[0].isTrue();
         }
 
         RubyArray result = getRuntime().newArray();
 
         for (RubyClass type = getMetaClass(); type != null && ((type instanceof MetaClass) || (all && type.isIncluded()));
              type = type.getSuperClass()) {
         	for (Iterator iter = type.getMethods().entrySet().iterator(); iter.hasNext(); ) {
                 Map.Entry entry = (Map.Entry) iter.next();
                 DynamicMethod method = (DynamicMethod) entry.getValue();
 
                 // We do not want to capture cached methods
                 if (method.getImplementationClass() != type && !(all && type.isIncluded())) {
                 	continue;
                 }
 
                 RubyString methodName = getRuntime().newString((String) entry.getKey());
                 if (method.getVisibility().isPublic() && ! result.includes(methodName)) {
                     result.append(methodName);
                 }
             }
         }
 
         return result;
     }
 
     public IRubyObject method(IRubyObject symbol) {
         return getMetaClass().newMethod(this, symbol.asSymbol(), true);
     }
 
     public IRubyObject anyToString() {
         String cname = getMetaClass().getRealClass().getName();
         /* 6:tags 16:addr 1:eos */
         RubyString str = getRuntime().newString("#<" + cname + ":0x" + Integer.toHexString(System.identityHashCode(this)) + ">");
         str.setTaint(isTaint());
         return str;
     }
 
     public IRubyObject to_s() {
     	return anyToString();
     }
 
     public IRubyObject instance_eval(IRubyObject[] args, Block block) {
         return specificEval(getSingletonClass(), args, block);
     }
 
     public IRubyObject extend(IRubyObject[] args) {
         Arity.checkArgumentCount(getRuntime(), args, 1, -1);
 
         // Make sure all arguments are modules before calling the callbacks
         RubyClass module = getRuntime().getClass("Module");
         for (int i = 0; i < args.length; i++) {
             if (!args[i].isKindOf(module)) {
                 throw getRuntime().newTypeError(args[i], module);
             }
         }
 
         for (int i = 0; i < args.length; i++) {
             args[i].callMethod(getRuntime().getCurrentContext(), "extend_object", this);
             args[i].callMethod(getRuntime().getCurrentContext(), "extended", this);
         }
         return this;
     }
 
     public IRubyObject inherited(IRubyObject arg, Block block) {
     	return getRuntime().getNil();
     }
     public IRubyObject initialize(IRubyObject[] args, Block block) {
     	return getRuntime().getNil();
     }
 
     public IRubyObject method_missing(IRubyObject[] args, Block block) {
         if (args.length == 0) {
             throw getRuntime().newArgumentError("no id given");
         }
 
         String name = args[0].asSymbol();
         String description = null;
         if("inspect".equals(name) || "to_s".equals(name)) {
             description = anyToString().toString();
         } else {
             description = inspect().toString();
         }
         boolean noClass = description.length() > 0 && description.charAt(0) == '#';
         Ruby runtime = getRuntime();
         ThreadContext tc = runtime.getCurrentContext();
         Visibility lastVis = tc.getLastVisibility();
         if(null == lastVis) {
             lastVis = Visibility.PUBLIC;
         }
         CallType lastCallType = tc.getLastCallType();
         String format = lastVis.errorMessageFormat(lastCallType, name);
         String msg = Sprintf.sprintf(runtime.newString(format), 
                 runtime.newArray(new IRubyObject[] { 
                         runtime.newString(name), runtime.newString(description),
                         runtime.newString(noClass ? "" : ":"), 
                         runtime.newString(noClass ? "" : getType().getName())
                 })).toString();
 
         if (lastCallType == CallType.VARIABLE) {
         	throw getRuntime().newNameError(msg, name);
         }
         throw getRuntime().newNoMethodError(msg, name);
     }
 
     /**
      * send( aSymbol  [, args  ]*   ) -> anObject
      *
      * Invokes the method identified by aSymbol, passing it any arguments
      * specified. You can use __send__ if the name send clashes with an
      * existing method in this object.
      *
      * <pre>
      * class Klass
      *   def hello(*args)
      *     "Hello " + args.join(' ')
      *   end
      * end
      *
      * k = Klass.new
      * k.send :hello, "gentle", "readers"
      * </pre>
      *
      * @return the result of invoking the method identified by aSymbol.
      */
     public IRubyObject send(IRubyObject[] args, Block block) {
         if (args.length < 1) {
             throw getRuntime().newArgumentError("no method name given");
         }
         String name = args[0].asSymbol();
 
         IRubyObject[] newArgs = new IRubyObject[args.length - 1];
         System.arraycopy(args, 1, newArgs, 0, newArgs.length);
 
         return callMethod(getRuntime().getCurrentContext(), name, newArgs, CallType.FUNCTIONAL, block);
     }
     
     public IRubyObject nil_p() {
     	return getRuntime().getFalse();
     }
     
     public IRubyObject match(IRubyObject arg) {
     	return getRuntime().getFalse();
     }
     
    public IRubyObject remove_instance_variable(IRubyObject name, Block block) {
        String id = name.asSymbol();
 
        if (!IdUtil.isInstanceVariable(id)) {
            throw getRuntime().newNameError("wrong instance variable name " + id, id);
        }
        if (!isTaint() && getRuntime().getSafeLevel() >= 4) {
            throw getRuntime().newSecurityError("Insecure: can't remove instance variable");
        }
        testFrozen("class/module");
 
        IRubyObject variable = removeInstanceVariable(id); 
        if (variable != null) {
            return variable;
        }
 
        throw getRuntime().newNameError("instance variable " + id + " not defined", id);
    }
     
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#getType()
      */
     public RubyClass getType() {
         return type();
     }
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#scanArgs()
      */
     public IRubyObject[] scanArgs(IRubyObject[] args, int required, int optional) {
         int total = required+optional;
         int real = Arity.checkArgumentCount(getRuntime(), args,required,total);
         IRubyObject[] narr = new IRubyObject[total];
         System.arraycopy(args,0,narr,0,real);
         for(int i=real; i<total; i++) {
             narr[i] = getRuntime().getNil();
         }
         return narr;
     }
 
     private transient Object dataStruct;
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#dataWrapStruct()
      */
     public synchronized void dataWrapStruct(Object obj) {
         this.dataStruct = obj;
     }
 
     /**
      * @see org.jruby.runtime.builtin.IRubyObject#dataGetStruct()
      */
     public synchronized Object dataGetStruct() {
         return dataStruct;
     }
 
     /** rb_equal
      * 
      */
     public IRubyObject equal(IRubyObject other) {
         if(this == other || callMethod(getRuntime().getCurrentContext(), "==",other).isTrue()){
             return getRuntime().getTrue();
         }
  
         return getRuntime().getFalse();
     }
     
     public final IRubyObject equalInternal(final ThreadContext context, final IRubyObject other){
         if (this == other) return getRuntime().getTrue();
         return callMethod(context, "==", other);
     }
 }
diff --git a/src/org/jruby/runtime/builtin/IRubyObject.java b/src/org/jruby/runtime/builtin/IRubyObject.java
index d4b5cd26ac..7f10fee1d3 100644
--- a/src/org/jruby/runtime/builtin/IRubyObject.java
+++ b/src/org/jruby/runtime/builtin/IRubyObject.java
@@ -1,569 +1,600 @@
 /***** BEGIN LICENSE BLOCK *****
  * Version: CPL 1.0/GPL 2.0/LGPL 2.1
  *
  * The contents of this file are subject to the Common Public
  * License Version 1.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of
  * the License at http://www.eclipse.org/legal/cpl-v10.html
  *
  * Software distributed under the License is distributed on an "AS
  * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
  * implied. See the License for the specific language governing
  * rights and limitations under the License.
  *
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2004 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
  * Copyright (C) 2006 Ola Bini <ola.bini@ki.se>
  *
  * Alternatively, the contents of this file may be used under the terms of
  * either of the GNU General Public License Version 2 or later (the "GPL"),
  * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
  * in which case the provisions of the GPL or the LGPL are applicable instead
  * of those above. If you wish to allow use of your version of this file only
  * under the terms of either the GPL or the LGPL, and not to allow others to
  * use your version of this file under the terms of the CPL, indicate your
  * decision by deleting the provisions above and replace them with the notice
  * and other provisions required by the GPL or the LGPL. If you do not delete
  * the provisions above, a recipient may use your version of this file under
  * the terms of any one of the CPL, the GPL or the LGPL.
  ***** END LICENSE BLOCK *****/
 package org.jruby.runtime.builtin;
 
 import java.util.Iterator;
 import java.util.Map;
 
 import org.jruby.Ruby;
 import org.jruby.RubyArray;
 import org.jruby.RubyClass;
 import org.jruby.RubyFloat;
 import org.jruby.RubyInteger;
 import org.jruby.RubyModule;
 import org.jruby.RubyFixnum;
 import org.jruby.RubyString;
 import org.jruby.ast.Node;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallType;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.callback.Callback;
 
 /** Object is the parent class of all classes in Ruby. Its methods are
  * therefore available to all objects unless explicitly overridden.
  *
  * @author  jpetersen
  */
 public interface IRubyObject {
     /**
      *
      */
     public static final IRubyObject[] NULL_ARRAY = new IRubyObject[0];
     
     /**
      * Return the ClassIndex value for the native type this object was
      * constructed from. Particularly useful for determining marshalling
      * format. All instances of subclasses of Hash, for example
      * are of Java type RubyHash, and so should utilize RubyHash marshalling
      * logic in addition to user-defined class marshalling logic.
      *
      * @return the ClassIndex of the native type this object was constructed from
      */
     int getNativeTypeIndex();
     
     /**
      * Gets a copy of the instance variables for this object, if any exist.
      * Returns null if this object has no instance variables.
      * "safe" in that it doesn't cause the instance var map to be created.
      *
      * @return A snapshot of the instance vars, or null if none.
      */
     Map safeGetInstanceVariables();
     
     /**
      * Returns true if the object has any instance variables, false otherwise.
      * "safe" in that it doesn't cause the instance var map to be created.
      *
      * @return true if the object has instance variables, false otherwise.
      */
     boolean safeHasInstanceVariables();
     
     /**
      * RubyMethod getInstanceVar.
      * @param string
      * @return RubyObject
      */
     IRubyObject getInstanceVariable(String string);
     
     /**
      * RubyMethod setInstanceVar.
      * @param string
      * @param rubyObject
      * @return RubyObject
      */
     IRubyObject setInstanceVariable(String string, IRubyObject rubyObject);
     
     /**
      *
      * @return
      */
     Map getInstanceVariables();
     
     /**
      *
      * @return
      */
     void setInstanceVariables(Map instanceVariables);
     
     /**
      *
      * @return
      */
     Map getInstanceVariablesSnapshot();
     
     /**
      *
      * @param context
      * @param rubyclass
      * @param name
      * @param args
      * @param callType
      * @param block
      * @return
      */
     IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, String name, IRubyObject[] args, CallType callType, Block block);
     /**
      *
      * @param context
      * @param rubyclass
      * @param switchvalue
      * @param name
      * @param args
      * @param callType
      * @param block
      * @return
      */
     IRubyObject callMethod(ThreadContext context, RubyModule rubyclass, byte switchvalue, String name, IRubyObject[] args, CallType callType, Block block);
     /**
      *
      * @param context
      * @param switchValue
      * @param name
      * @param arg
      * @return
      */
     IRubyObject callMethod(ThreadContext context, byte switchValue, String name, IRubyObject arg);
     /**
      *
      * @param context
      * @param switchValue
      * @param name
      * @param args
      * @return
      */
     IRubyObject callMethod(ThreadContext context, byte switchValue, String name, IRubyObject[] args);
     /**
      *
      * @param context
      * @param switchValue
      * @param name
      * @param args
      * @param callType
      * @return
      */
     IRubyObject callMethod(ThreadContext context, byte switchValue, String name, IRubyObject[] args, CallType callType);
     /**
      *
      * @param context
      * @param name
      * @param args
      * @param callType
      * @return
      */
     IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, CallType callType);
     /**
      *
      * @param context
      * @param name
      * @param args
      * @param callType
      * @param block
      * @return
      */
     IRubyObject callMethod(ThreadContext context, String name, IRubyObject[] args, CallType callType, Block block);
     // Used by the compiler, to allow visibility checks
     /**
      *
      * @param context
      * @param name
      * @param args
      * @param caller
      * @param callType
      * @param block
      * @return
      */
     IRubyObject compilerCallMethod(ThreadContext context, String name, IRubyObject[] args, IRubyObject caller, CallType callType, Block block);
     /**
      *
      * @param context
      * @param methodIndex
      * @param name
      * @param args
      * @param caller
      * @param callType
      * @param block
      * @return
      */
     IRubyObject compilerCallMethodWithIndex(ThreadContext context, byte methodIndex, String name, IRubyObject[] args, IRubyObject caller, CallType callType, Block block);
     /**
      *
      * @param context
      * @param args
      * @param block
      * @return
      */
     IRubyObject callSuper(ThreadContext context, IRubyObject[] args, Block block);
     /**
      *
      * @param context
      * @param string
      * @return
      */
     IRubyObject callMethod(ThreadContext context, String string);
     /**
      *
      * @param context
      * @param string
      * @param aBlock
      * @return
      */
     IRubyObject callMethod(ThreadContext context, String string, Block aBlock);
     /**
      *
      * @param context
      * @param string
      * @param arg
      * @return
      */
     IRubyObject callMethod(ThreadContext context, String string, IRubyObject arg);
     /**
      *
      * @param context
      * @param method
      * @param rubyArgs
      * @return
      */
     IRubyObject callMethod(ThreadContext context, String method, IRubyObject[] rubyArgs);
     /**
      *
      * @param context
      * @param method
      * @param rubyArgs
      * @param block
      * @return
      */
     IRubyObject callMethod(ThreadContext context, String method, IRubyObject[] rubyArgs, Block block);
     
     /**
      * RubyMethod isNil.
      * @return boolean
      */
     boolean isNil();
     
     /**
      *
      * @return
      */
     boolean isTrue();
     
     /**
      * RubyMethod isTaint.
      * @return boolean
      */
     boolean isTaint();
     
     /**
      * RubyMethod setTaint.
      * @param b
      */
     void setTaint(boolean b);
     
     /**
      * RubyMethod isFrozen.
      * @return boolean
      */
     boolean isFrozen();
     
     /**
      * RubyMethod setFrozen.
      * @param b
      */
     void setFrozen(boolean b);
     
     /**
      *
      * @return
      */
     boolean isImmediate();
     
     /**
      * RubyMethod isKindOf.
      * @param rubyClass
      * @return boolean
      */
     boolean isKindOf(RubyModule rubyClass);
     
     /**
      * Infect this object using the taint of another object
      * @param obj
      * @return
      */
     IRubyObject infectBy(IRubyObject obj);
     
     /**
      * RubyMethod getRubyClass.
      * @return
      */
     RubyClass getMetaClass();
     
     /**
      *
      * @param metaClass
      */
     void setMetaClass(RubyClass metaClass);
     
     /**
      * RubyMethod getSingletonClass.
      * @return RubyClass
      */
     RubyClass getSingletonClass();
     
     /**
      * RubyMethod getType.
      * @return RubyClass
      */
     RubyClass getType();
     
     /**
      * RubyMethod respondsTo.
      * @param string
      * @return boolean
      */
     boolean respondsTo(String string);
     
     /**
      * RubyMethod getRuntime.
      * @return
      */
     Ruby getRuntime();
     
     /**
      * RubyMethod getJavaClass.
      * @return Class
      */
     Class getJavaClass();
     
     /**
      * Evaluate the given string under the specified binding object. If the binding is not a Proc or Binding object
      * (RubyProc or RubyBinding) throw an appropriate type error.
      * @param context TODO
      * @param evalString The string containing the text to be evaluated
      * @param binding The binding object under which to perform the evaluation
      * @param file The filename to use when reporting errors during the evaluation
      * @return An IRubyObject result from the evaluation
      */
     IRubyObject evalWithBinding(ThreadContext context, IRubyObject evalString, IRubyObject binding, String file);
     
     /**
      * Evaluate the given string.
      * @param context TODO
      * @param evalString The string containing the text to be evaluated
      * @param file The filename to use when reporting errors during the evaluation
      * @return An IRubyObject result from the evaluation
      */
     IRubyObject evalSimple(ThreadContext context, IRubyObject evalString, String file);
     
     /**
      * Convert the object into a symbol name if possible.
      *
      * @return String the symbol name
      */
     String asSymbol();
     
     /** rb_obj_as_string
      * @return
      */
     RubyString asString();
     
     /**
      * Methods which perform to_xxx if the object has such a method
      * @return
      */
     RubyArray convertToArray();
     /**
      *
      * @return
      */
     RubyFloat convertToFloat();
     /**
      *
      * @return
      */
     RubyInteger convertToInteger();
     /**
      *
      * @return
      */
     RubyString convertToString();
     
     /**
      * Converts this object to type 'targetType' using 'convertMethod' method (MRI: convert_type).
      *
      * @param targetType is the type we are trying to convert to
      * @param convertMethod is the method to be called to try and convert to targeType
      * @param raiseOnError will throw an Error if conversion does not work
      * @return the converted value
+     * @deprecated
      */
     IRubyObject convertToType(String targetType, String convertMethod, boolean raiseOnError);
     
     /**
+     * Converts this object to type 'targetType' using 'convertMethod' method (MRI: convert_type).
+     *
+     * @param targetType is the type we are trying to convert to
+     * @param convertMethod is the method to be called to try and convert to targeType
+     * @param raiseOnError will throw an Error if conversion does not work
+     * @return the converted value
+     */
+    IRubyObject convertToType(RubyClass targetType, String convertMethod, boolean raiseOnError);
+    
+    /**
      * Higher level conversion utility similiar to convertToType but it can throw an
      * additional TypeError during conversion (MRI: rb_check_convert_type).
      *
      * @param targetType is the type we are trying to convert to
      * @param convertMethod is the method to be called to try and convert to targeType
      * @return the converted value
+     * @deprecated
      */
     IRubyObject convertToTypeWithCheck(String targetType, String convertMethod);
-    IRubyObject convertToTypeWithCheck(Class cls, String targetType, String convertMethod); 
+    
+    /**
+     * Higher level conversion utility similiar to convertToType but it can throw an
+     * additional TypeError during conversion (MRI: rb_check_convert_type).
+     *
+     * @param targetType is the type we are trying to convert to
+     * @param convertMethod is the method to be called to try and convert to targeType
+     * @return the converted value
+     */
+    IRubyObject convertToTypeWithCheck(RubyClass targetType, String convertMethod);
    
     /**
+     * 
+     * @param targetType 
+     * @param convertMethod 
+     * @param raiseOnMissingMethod 
+     * @param raiseOnWrongTypeResult 
+     * @return 
+     */
+    public IRubyObject convertToType(RubyClass targetType, String convertMethod, boolean raiseOnMissingMethod, boolean raiseOnWrongTypeResult, boolean allowNilThrough);
+
+    /**
      * RubyMethod dup.
      * @return
      */
     IRubyObject dup();
     
     /**
      * RubyMethod inspect.
      * @return String
      */
     IRubyObject inspect();
     
     /**
      * RubyMethod rbClone.
      * @return IRubyObject
      */
     IRubyObject rbClone();
     
     
     /**
      *
      * @param args
      * @param block
      */
     public void callInit(IRubyObject[] args, Block block);
     
     /**
      * RubyMethod defineSingletonMethod.
      * @param name
      * @param callback
      */
     void defineSingletonMethod(String name, Callback callback);
     
     
     /**
      *
      * @return
      */
     boolean isSingleton();
     
     /**
      *
      * @return
      */
     Iterator instanceVariableNames();
     
     /**
      * rb_scan_args
      *
      * This method will take the arguments specified, fill in an array and return it filled
      * with nils for every argument not provided. It's guaranteed to always return a new array.
      *
      * @param args the arguments to check
      * @param required the amount of required arguments
      * @param optional the amount of optional arguments
      * @return a new array containing all arguments provided, and nils in those spots not provided.
      *
      */
     IRubyObject[] scanArgs(IRubyObject[] args, int required, int optional);
     
     /**
      * Our version of Data_Wrap_Struct.
      *
      * This method will just set a private pointer to the object provided. This pointer is transient
      * and will not be accessible from Ruby.
      *
      * @param obj the object to wrap
      */
     void dataWrapStruct(Object obj);
     
     /**
      * Our version of Data_Get_Struct.
      *
      * Returns a wrapped data value if there is one, otherwise returns null.
      *
      * @return the object wrapped.
      */
     Object dataGetStruct();
     
     /**
      *
      * @return
      */
     RubyFixnum id();
     
     /**
      *
      * @return
      */
     IRubyObject anyToString();
     
     /**
      *
      * @return
      */
     IRubyObject checkStringType();
     
     /**
      *
      * @return
      */
     IRubyObject checkArrayType();
     
     /**
      *
      * @param context
      * @param other
      * @return
      */
     IRubyObject equalInternal(final ThreadContext context, final IRubyObject other);
     
     /**
      *
      */
     void attachToObjectSpace();
     
     /**
      *
      * @param args
      * @param block
      * @return
      */
     IRubyObject send(IRubyObject[] args, Block block);
     /**
      *
      * @param method
      * @return
      */
     IRubyObject method(IRubyObject method);
 }
diff --git a/test/testHash.rb b/test/testHash.rb
index 9dcdc09df0..75cbdbfdfd 100644
--- a/test/testHash.rb
+++ b/test/testHash.rb
@@ -1,121 +1,137 @@
 require 'test/minirunit'
 test_check "Test hash:"
 
 h = {1=>2,3=>4,5=>6}
 h.replace({1=>100})
 test_equal({1=>100}, h)
 
 h = {1=>2,3=>4}
 h2 = {3=>300, 4=>400}
 h.update(h2)
 test_equal(2, h[1])
 test_equal(300, h[3])
 test_equal(400, h[4])
 
 num = 0
 h1 = { "a" => 100, "b" => 200 }
 h2 = { "b" => 254, "c" => 300 }
 h1.merge!(h2) {|k,o,n| num += 1; o+n }
 test_equal(h1,{"a"=>100, "b"=>454, "c"=>300})
 test_equal(num, 1)
 
 h = {1=>2,3=>4}
 test_exception(IndexError) { h.fetch(10) }
 test_equal(2, h.fetch(1))
 test_equal("hello", h.fetch(10, "hello"))
 test_equal("hello 10", h.fetch(10) { |e| "hello #{e}" })
 
 h = {}
 k1 = [1]
 h[k1] = 1
 k1[0] = 100
 test_equal(nil, h[k1])
 h.rehash
 test_equal(1, h[k1])
 
 h = {1=>2,3=>4,5=>6}
 test_equal([2, 6], h.values_at(1, 5))
 
 h = {1=>2,3=>4}
 test_equal(1, h.index(2))
 test_equal(nil, h.index(10))
 test_equal(nil, h.default_proc)
 h.default = :hello
 test_equal(nil, h.default_proc)
 test_equal(1, h.index(2))
 test_equal(nil, h.index(10))
 
 h = Hash.new {|h,k| h[k] = k.to_i*10 }
 
 test_ok(!nil, h.default_proc)
 test_equal(100, h[10])
 test_equal(20, h.default(2))
 
 #behavior change in 1.8.5 led to this:
 test_equal(nil, h.default)
 
 h.default = 5
 test_equal(5,h.default)
 test_equal(nil, h.default_proc)
 
 test_equal(5, h[12])
 
 class << h
  def default(k); 2; end
 end
 
 test_equal(nil, h.default_proc)
 test_equal(2, h[30])
 
 # test that extensions of the base classes are typed correctly
 class HashExt < Hash
 end
 test_equal(HashExt, HashExt.new.class)
 test_equal(HashExt, HashExt[:foo => :bar].class)
 
 # make sure hash yields look as expected (copied from MRI iterator test)
 
 class H
   def each
     yield [:key, :value]
   end
 end
 
 [{:key=>:value}, H.new].each {|h|
   h.each{|a| test_equal([:key, :value], a)}
   h.each{|*a| test_equal([[:key, :value]], a)}
   h.each{|k,v| test_equal([:key, :value], [k,v])}
 }
 
 # each_pair should splat args correctly
 {:a=>:b}.each_pair do |*x|
         test_equal(:a,x[0])
         test_equal(:b,x[1])
 end
 
 # Test hash coercion
 class MyHash
   def initialize(hash)
     @hash = hash
   end
   def to_hash
     @hash
   end
 end
 
+class SubHash < Hash
+end
+
 x = {:a => 1, :b => 2}
 x.update(MyHash.new({:a => 10, :b => 20}))
 test_equal(10, x[:a])
 test_equal(20, x[:b])
 test_exception(TypeError) { x.update(MyHash.new(4)) }
 
 x = {:a => 1, :b => 2}
+sub2 = SubHash.new()
+sub2[:a] = 10
+sub2[:b] = 20
+x.update(MyHash.new(sub2))
+test_equal(10, x[:a])
+test_equal(20, x[:b])
+
+x = {:a => 1, :b => 2}
 x.replace(MyHash.new({:a => 10, :b => 20}))
 test_equal(10, x[:a])
 test_equal(20, x[:b])
 test_exception(TypeError) { x.replace(MyHash.new(4)) }
 
+x = {:a => 1, :b => 2}
+x.replace(MyHash.new(sub2))
+test_equal(10, x[:a])
+test_equal(20, x[:b])
+
 class H1 < Hash
 end
 
 test_no_exception{ H1.new.clone }
diff --git a/test/testKernel.rb b/test/testKernel.rb
index 11e93a58ad..b68222108e 100644
--- a/test/testKernel.rb
+++ b/test/testKernel.rb
@@ -1,159 +1,160 @@
 require 'test/minirunit'
 test_check "kernel"
 
 test_ok(! eval("defined? some_unknown_variable"))
 x = 1
 test_equal(1, eval("x"))
 eval("x = 2")
 test_equal(2, x)
 eval("unknown = 3")
 test_equal(2, x)     # Make sure eval() didn't destroy locals
 test_ok(! defined? unknown)
 test_equal(nil, true && defined?(Bogus))
 
 # JRUBY-117 - to_a should be public
 test_equal(["to_a"], Object.public_instance_methods.grep(/to_a/))
 # JRUBY-117 - remove_instance_variable should be private
 test_equal(["remove_instance_variable"], Object.private_instance_methods.grep(/remove_instance_variable/))
 
 # JRUBY-116 (Array())
 class A1; def to_ary; [1]; end; end
 class A2; def to_a  ; [2]; end; end
 class A3; def to_ary;   3; end; end
 class A4; def to_a  ;   4; end; end
 class A5; def to_ary; [5]; end; def to_a  ; [:no]; end; end
 class A6; def to_ary; :no; end; def to_a  ;   [6]; end; end
 class A7; end
 class A8; def to_ary; nil; end; end
 class A9; def to_a  ; nil; end; end
 
 
 test_equal([], Array(nil))
 # No warning for this first case either
 test_equal([1], Array(1))
 test_equal([1], Array(A1.new))
 test_equal([2], Array(A2.new))
 test_exception(TypeError) { Array(A3.new) }
 test_exception(TypeError) { Array(A4.new) }
 test_equal([5], Array(A5.new))
 test_exception(TypeError) { Array(A6.new) }
 a = A7.new
+
 test_equal([a], Array(a))
 a = A8.new
 test_equal([a], Array(a))
 test_exception(TypeError) { Array(A9.new) }
 
 test_equal(10,Integer("0xA"))
 test_equal(8,Integer("010"))
 test_equal(2,Integer("0b10"))
 
 test_equal(1.0,Float("1"))
 test_equal(10.0,Float("1e1"))
 
 test_exception(ArgumentError) { Integer("abc") }
 test_exception(ArgumentError) { Integer("x10") }
 test_exception(ArgumentError) { Integer("xxxx10000000000000000000000000000000000000000000000000000") }
 
 test_exception(ArgumentError) { Float("abc") }
 test_exception(ArgumentError) { Float("x10") }
 test_exception(ArgumentError) { Float("xxxx10000000000000000000000000000000000000000000000000000") }
 
 # JRUBY-214 - load should call to_str on arg 0
 class Foo
   def to_str
     "test/requireTarget.rb"
   end
 end
 
 test_no_exception { load Foo.new }
 test_exception(TypeError) { load Object.new }
 
 
 #Previously Kernel.raise, Kernel.sprintf, Kernel.iterator? & Kernel.exec were all made private
 #as they were aliased rather than defined. Checking that this is no longer the case
 test_exception(RuntimeError) { Kernel.raise }
 test_exception(ArgumentError) { Kernel.printf "%d", 's' }
 test_no_exception { Kernel.sprintf "Helllo" }
 test_no_exception { Kernel.iterator? }
 if File.exists?("/bin/true")
   test_no_exception { Kernel.exec "/bin/true" }
 end
 
 test_no_exception {
     catch :fred do
         throw :fred
     end
 }
 
 test_exception(NameError) {
     catch :fred do
         throw :wilma
     end
 }
 
 # test that NameError is raised at the throw, not at the catch
 test_no_exception {
     catch :fred do
         begin
             throw :wilma
             test_fail("NameError should have been raised")
         rescue NameError => e
             test_ok(true)
         end
     end
 }
 
 test_no_exception {
     catch :fred1 do
         catch :fred2 do
             catch :fred3 do
                 throw :fred1
                 test_fail("should have jumped to fred1 catch")
             end
             test_fail("should have jumped to fred1 catch")
         end
     end
 }    
 
 test_no_exception {
     catch :fred1 do
         catch :fred2 do
             catch :fred3 do
                 throw :fred2
                 test_fail("should have jumped to fred2 catch")
             end
         end
     end
 }
 
 test_exception(NameError) {
     catch :fred1 do
         catch :fred2 do
             catch :fred1 do
                 throw :fred2
                 test_fail("should have jumped to after fred2 catch")
             end
             test_fail("should have jumped to after fred2 catch")
         end
         test_ok(true)
         throw :wilma
     end
 }
 
 test_exception(NameError) {
     throw :wilma
 }
 
 test_exception(NameError) {
     catch :fred1 do
         catch :fred2 do
             catch :fred3 do
             end
         end
     end
     throw :fred2
     test_fail("catch stack should have been cleaned up")
 }
 
 ##### format %
 test_exception(TypeError) { "%01.3f" % nil }
\ No newline at end of file
