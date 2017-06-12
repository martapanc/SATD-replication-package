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
 * Copyright (C) 2007 Charles O Nutter <headius@headius.com>
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

package org.jruby.libraries;

import java.util.WeakHashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.jruby.CompatVersion;
import org.jruby.Ruby;
import org.jruby.RubyObject;
import org.jruby.RubyClass;
import org.jruby.RubyThread;
import org.jruby.anno.JRubyClass;
import org.jruby.anno.JRubyMethod;
import org.jruby.exceptions.RaiseException;
import org.jruby.runtime.Block;
import org.jruby.runtime.ExecutionContext;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.load.Library;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.threading.DaemonThreadFactory;
import org.jruby.runtime.CallbackFactory;

import static org.jruby.runtime.Visibility.*;

/**
 * A basic implementation of Ruby 1.9 Fiber library.
 */
public class FiberLibrary implements Library {
    public void load(final Ruby runtime, boolean wrap) {
        RubyClass cFiber = runtime.defineClass("Fiber", runtime.getObject(), new ObjectAllocator() {
            public IRubyObject allocate(Ruby runtime, RubyClass klazz) {
                return new Fiber(runtime, klazz);
            }
        });

        cFiber.defineAnnotatedMethods(Fiber.class);
        cFiber.defineAnnotatedMethods(FiberMeta.class);

        if (runtime.getExecutor() != null) {
            executor = runtime.getExecutor();
        } else {
            executor = Executors.newCachedThreadPool(new DaemonThreadFactory());
        }
    }

    private Executor executor;

    public enum FiberState {
        NOT_STARTED,
        STARTED,
        YIELDED,
        RUNNING,
        FINISHED
    }

    @JRubyClass(name="Fiber")
    public class Fiber extends RubyObject implements ExecutionContext {
        private final Object yieldLock = new Object();
        private final Map<Object, IRubyObject> contextVariables = new WeakHashMap<Object, IRubyObject>();
        private Block block;
        private IRubyObject result;
        private RubyThread parent;
        private Runnable runnable;
        private FiberState state = FiberState.NOT_STARTED;

        @JRubyMethod(rest = true, visibility = PRIVATE)
        public IRubyObject initialize(ThreadContext context, final IRubyObject[] args, Block block) {
            this.block = block;
            final Ruby runtime = context.getRuntime();
            this.parent = context.getThread();
            this.result = runtime.getNil();
            this.runnable = new Runnable() {
                public void run() {
                    synchronized (yieldLock) {
                        state = FiberState.STARTED;
                        ThreadContext context = runtime.getCurrentContext();
                        context.setFiber(Fiber.this);
                        try {
                            result = Fiber.this.block.yieldArray(context, result, null, null);
                        } catch (RaiseException re) {
                            // re-raise exception in parent thread
                            parent.raise(new IRubyObject[] {re.getException()}, Block.NULL_BLOCK);
                        } finally {
                            yieldLock.notify();
                            state = FiberState.FINISHED;
                        }
                    }
                }
            };
            // FIXME: Make thread pool threads daemons if necessary
            return this;
        }

        public Fiber(Ruby runtime, RubyClass type) {
            super(runtime, type);
        }

        @JRubyMethod(rest = true, compat = CompatVersion.RUBY1_9)
        public IRubyObject resume(ThreadContext context, IRubyObject[] args) {
            synchronized (yieldLock) {
                // FIXME: Broken but behaving
                if (args.length == 0) {
                    result = context.getRuntime().getNil();
                } else if (args.length == 1) {
                    result = args[0];
                } else {
                    result = context.getRuntime().newArrayNoCopyLight(args);
                }
                try {
                    switch (state) {
                    case NOT_STARTED:
                        executor.execute(runnable);
                        yieldLock.wait();
                        break;
                    case RUNNING:
                        throw context.getRuntime().newFiberError("double resume");
                    case FINISHED:
                        throw context.getRuntime().newFiberError("dead fiber called");
                    case YIELDED:
                        state = FiberState.RUNNING;
                        yieldLock.notify();
                        yieldLock.wait();
                        break;
                    default:
                        throw context.getRuntime().newFiberError("fiber in an unknown state");
                    }
                } catch (InterruptedException ie) {
                    throw context.getRuntime().newConcurrencyError(ie.getLocalizedMessage());
                }
            }
            return result;
        }

        // This should only be defined after require 'fiber'
        @JRubyMethod(name = "transfer", rest = true, compat = CompatVersion.RUBY1_9)
        public IRubyObject transfer(ThreadContext context, IRubyObject[] args) {
            synchronized (yieldLock) {
                yieldLock.notify();
                try {
                    yieldLock.wait();
                } catch (InterruptedException ie) {
                    throw context.getRuntime().newConcurrencyError(ie.getLocalizedMessage());
                }
            }
            return result;
        }

        // This should only be defined after require 'fiber'
        @JRubyMethod(name = "alive?", compat = CompatVersion.RUBY1_9)
        public IRubyObject alive_p(ThreadContext context) {
            return context.getRuntime().newBoolean(state != FiberState.FINISHED);
        }

        public Map<Object, IRubyObject> getContextVariables() {
            return contextVariables;
        }
    }

    public static class FiberMeta {
        @JRubyMethod(compat = CompatVersion.RUBY1_9, rest = true, meta = true)
        public static IRubyObject yield(ThreadContext context, IRubyObject recv, IRubyObject[] args) {
            Fiber fiber = context.getFiber();
            if (fiber == null) {
                throw context.getRuntime().newFiberError("can't yield from root fiber");
            }

            // FIXME: Broken but behaving
            if (args.length == 0) {
                fiber.result = context.getRuntime().getNil();
            } else if (args.length == 1) {
                fiber.result = args[0];
            } else {
                fiber.result = context.getRuntime().newArrayNoCopyLight(args);
            }
            synchronized (fiber.yieldLock) {
                fiber.yieldLock.notify();
                try {
                    fiber.state = FiberState.YIELDED;
                    fiber.yieldLock.wait();
                } catch (InterruptedException ie) {
                    throw context.getRuntime().newConcurrencyError(ie.getLocalizedMessage());
                }
            }
            return context.getRuntime().getNil();
        }
    }

    /** These methods get loaded when you require 'fiber'. */
    public static class ExtLibrary implements Library {
        @SuppressWarnings("deprecation")
        public void load(final Ruby runtime, boolean wrap) {
            RubyClass cFiber = runtime.getClass("Fiber");
            cFiber.defineAnnotatedMethods(FiberExtMeta.class);
        }

        public static class FiberExtMeta {
            @JRubyMethod(compat = CompatVersion.RUBY1_9, meta = true)
            public static IRubyObject current(ThreadContext context, IRubyObject recv) {
                return context.getRuntime().getCurrentContext().getFiber();
            }
        }
    }
}
