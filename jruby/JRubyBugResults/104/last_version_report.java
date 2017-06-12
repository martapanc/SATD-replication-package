        public Object executeForeign(VirtualFrame frame, TruffleObject receiver, Object[] arguments) {
            if (node == null) {
                CompilerDirectives.transferToInterpreterAndInvalidate();
                this.node = ForeignObjectAccessNode.getAccess(Execute.create(Receiver.create(), arguments.length));
            }
            return node.executeForeign(frame, receiver, arguments);
        }
