                public void nextValue(BodyCompiler context, Object object, int index) {
                    ArrayNode arguments = (ArrayNode)object;
                    Node argNode = arguments.get(index);
                    switch (argNode.getNodeType()) {
                    case MULTIPLEASGN19NODE:
                        compileMultipleAsgn19Assignment(argNode, context, false);
                        break;
                    case ARGUMENTNODE:
                        int varIndex = ((ArgumentNode)argNode).getIndex();
                        context.getVariableCompiler().assignLocalVariable(varIndex, false);
                        break;
                    default:
                        throw new NotCompilableException("unknown argument type: " + argNode);
                    }
                }
