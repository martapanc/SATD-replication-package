32/report.java
Satd-method: public boolean equals(Object obj) {
********************************************
********************************************
32/After/ HHH-5163  dc00c4dc_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
equals(
+		if ( ! this.equals( create( transformer, aliases, includeInTuple ) ) ) {
+	public boolean equals(Object o) {
+		if ( !Arrays.equals( includeInTuple, that.includeInTuple ) ) {
+		if ( !Arrays.equals( includeInTransformIndex, that.includeInTransformIndex ) ) {
+		assert transformer.equals( transformer2 ): "deep copy issue";
+				assert key.getResultTransformer().equals( key2.getResultTransformer() ): "deep copy issue";
+				assertFalse( yogiAddress1.getAddressType().equals( yogiAddress2.getAddressType() ) );
+				assertFalse( yogiAddress1.getAddressType().equals( yogiAddress2.getAddressType() ) );
-				assertFalse( yogiAddress1.getAddressType().equals( yogiAddress2.getAddressType() ) );

Lines added containing method: 8. Lines removed containing method: 1. Tot = 9
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isInstance
********************************************
********************************************
32/After/ HHH-5616  34c2839d_diff.java
————————————————————————————————————————————————
*** Lines Changed in Satd-Method: ***


Lines added: 0. Lines removed: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls OF Satd-Method in Diff: ***
equals(

Lines added containing method: 0. Lines removed containing method: 0. Tot = 0
————————————————————————————————————————————————
*** Changed calls of methods FROM Satd-Method in Diff: ***
Method calls found: 
* isInstance
********************************************
********************************************
