public Operand buildConstDecl(Node node, IR_Scope s) {
    Operand       val;
    ConstDeclNode constDeclNode = (ConstDeclNode) node;
    Node          constNode     = constDeclNode.getConstNode();

    if (constNode == null) {
        val = build(constDeclNode.getValueNode(), s);
        s.setConstantValue(constDeclNode.getName(), val);
    } else if (constNode.getNodeType() == NodeType.COLON2NODE) {
        Operand module = build(((Colon2Node) constNode).getLeftNode(), s);
        val = build(constDeclNode.getValueNode(), s);
        s.addInstr(new PUT_CONST_Instr(module, constDeclNode.getName(), val);
    } else { // colon3, assign in Object
        val = build(constDeclNode.getValueNode(), s);
        s.addInstr(new PUT_CONST_Instr(s.getSelf(), constDeclNode.getName(), val));
    }

    return val;
}
