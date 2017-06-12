diff --git a/src/jregex/Matcher.java b/src/jregex/Matcher.java
index a20c8fd6a1..e200f70008 100644
--- a/src/jregex/Matcher.java
+++ b/src/jregex/Matcher.java
@@ -291,2006 +291,2006 @@ public class Matcher implements MatchResult{
       char[] mychars=data;
       boolean shared=this.shared;
       if(mychars==null || shared || mychars.length<len){
          mychars=new char[len];
          shared=false;
       }
       int count=0;
       int c;
       while((c=in.read(mychars,count,len))>=0){
          len-=c;
          count+=c;
          if(len==0) break;
       }
       setTarget(mychars,0,count,shared);
    }
    
    private void setAll(Reader in)throws IOException{
       char[] mychars=data;
       int free;
       boolean shared=this.shared;
       if(mychars==null || shared){
          mychars=new char[free=1024];
          shared=false;
       }
       else free=mychars.length;
       int count=0;
       int c;
       while((c=in.read(mychars,count,free))>=0){
          free-=c;
          count+=c;
          if(free==0){
             int newsize=count*3;
             char[] newchars=new char[newsize];
             System.arraycopy(mychars,0,newchars,0,count);
             mychars=newchars;
             free=newsize-count;
             shared=false;
          }
       }
       setTarget(mychars,0,count,shared);
    }
    
    private final String getString(int start,int end){
       String src=cache;
       if(src!=null){
          int co=cacheOffset;
          return src.substring(start-co,end-co);
       }
       int tOffset,tEnd,tLen=(tEnd=this.end)-(tOffset=this.offset);
       char[] data=this.data;
       if((end-start)>=(tLen/3)){
          //it makes sence to make a cache
          cache=src=new String(data,tOffset,tLen);
          cacheOffset=tOffset;
          cacheLength=tLen;
          return src.substring(start-tOffset,end-tOffset);
       }
       return new String(data,start,end-start);
    }
    
   /* Matching */
    
   /**
    * Tells whether the entire target matches the beginning of the pattern.
    * The whole pattern is also regarded as its beginning.<br>
    * This feature allows to find a mismatch by examining only a beginning part of 
    * the target (as if the beginning of the target doesn't match the beginning of the pattern, then the entire target 
    * also couldn't match).<br>
    * For example the following assertions yield <code>true<code>:<pre>
    *   Pattern p=new Pattern("abcd"); 
    *   p.matcher("").matchesPrefix();
    *   p.matcher("a").matchesPrefix();
    *   p.matcher("ab").matchesPrefix();
    *   p.matcher("abc").matchesPrefix();
    *   p.matcher("abcd").matchesPrefix();
    * </pre>
    * and the following yield <code>false<code>:<pre>
    *   p.matcher("b").isPrefix();
    *   p.matcher("abcdef").isPrefix();
    *   p.matcher("x").isPrefix();
    * </pre>
    * @return true if the entire target matches the beginning of the pattern
    */
    public final boolean matchesPrefix(){
       setPosition(0);
       return search(ANCHOR_START|ACCEPT_INCOMPLETE|ANCHOR_END);
    }
    
   /**
    * Just an old name for isPrefix().<br>
    * Retained for backwards compatibility.
    * @deprecated Replaced by isPrefix()
    */
    public final boolean isStart(){
       return matchesPrefix();
    }
    
   /**
    * Tells whether a current target matches the whole pattern.
    * For example the following yields the <code>true<code>:<pre>
    *   Pattern p=new Pattern("\\w+"); 
    *   p.matcher("a").matches();
    *   p.matcher("ab").matches();
    *   p.matcher("abc").matches();
    * </pre>
    * and the following yields the <code>false<code>:<pre>
    *   p.matcher("abc def").matches();
    *   p.matcher("bcd ").matches();
    *   p.matcher(" bcd").matches();
    *   p.matcher("#xyz#").matches();
    * </pre>
    * @return whether a current target matches the whole pattern.
    */
    public final boolean matches(){
 if(called) setPosition(0);
       return search(ANCHOR_START|ANCHOR_END);
    }
    
   /**
    * Just a combination of setTarget(String) and matches().
    * @param s the target string;
    * @return whether the specified string matches the whole pattern.
    */
    public final boolean matches(String s){
       setTarget(s);
       return search(ANCHOR_START|ANCHOR_END);
    }
    
   /**
    * Allows to set a position the subsequent find()/find(int) will start from.
    * @param pos the position to start from;
    * @see Matcher#find()
    * @see Matcher#find(int)
    */
    public void setPosition(int pos){
       wOffset=offset+pos;
       wEnd=-1;
       called=false;
       flush();
    }
 
    public void setOffset(int offset){
        this.offset = offset;
        wOffset=offset;
        wEnd=-1;
        called=false;
        flush();
    }
    
   /**
    * Searches through a target for a matching substring, starting from just after the end of last match.
    * If there wasn't any search performed, starts from zero.
    * @return <code>true</code> if a match found.
    */
    public final boolean find(){
       if(called) skip();
       return search(0);
    }
    
   /**
    * Searches through a target for a matching substring, starting from just after the end of last match.
    * If there wasn't any search performed, starts from zero.
    * @param anchors a zero or a combination(bitwise OR) of ANCHOR_START,ANCHOR_END,ANCHOR_LASTMATCH,ACCEPT_INCOMPLETE
    * @return <code>true</code> if a match found.
    */
    public final boolean find(int anchors){
       if(called) skip();
       return search(anchors);
    }
    
    
   /**
    * The same as  findAll(int), but with default behaviour;
    */
    public MatchIterator findAll(){
       return findAll(0);
    }
    
   /**
    * Returns an iterator over the matches found by subsequently calling find(options), the search starts from the zero position.
    */
    public MatchIterator findAll(final int options){
       //setPosition(0);
       return new MatchIterator(){
          private boolean checked=false;
          private boolean hasMore=false;
          public boolean hasMore(){
             if(!checked) check();
             return hasMore;
          }
          public MatchResult nextMatch(){
             if(!checked) check();
             if(!hasMore) throw new NoSuchElementException();
             checked=false;
             return Matcher.this;
          }
          private final void check(){
             hasMore=find(options);
             checked=true;
          }
          public int count(){
             if(!checked) check();
             if(!hasMore) return 0;
             int c=1;
             while(find(options))c++;
             checked=false;
             return c;
          }
       };
    }
    
   /**
    * Continues to search from where the last search left off.
    * The same as proceed(0).
    * @see Matcher#proceed(int)
    */
    public final boolean proceed(){
       return proceed(0);
    }
    
   /**
    * Continues to search from where the last search left off using specified options:<pre>
    * Matcher m=new Pattern("\\w+").matcher("abc");
    * while(m.proceed(0)){
    *    System.out.println(m.group(0));
    * }
    * </pre>
    * Output:<pre>
    * abc
    * ab
    * a
    * bc
    * b
    * c
    * </pre>
    * For example, let's find all odd nubmers occuring in a text:<pre>
    *    Matcher m=new Pattern("\\d+").matcher("123");
    *    while(m.proceed(0)){
    *       String match=m.group(0);
    *       if(isOdd(Integer.parseInt(match))) System.out.println(match);
    *    }
    *    
    *    static boolean isOdd(int i){
    *       return (i&1)>0;
    *    }
    * </pre>
    * This outputs:<pre>
    * 123
    * 1
    * 23
    * 3
    * </pre>
    * Note that using <code>find()</code> method we would find '123' only.
    * @param options search options, some of ANCHOR_START|ANCHOR_END|ANCHOR_LASTMATCH|ACCEPT_INCOMPLETE; zero value(default) stands for usual search for substring.
    */
    public final boolean proceed(int options){
 //System.out.println("next() : top="+top);
       if(called){
          if(top==null){
             wOffset++;
          }
       }
       return search(0);
    }
    
   /**
    * Sets the current search position just after the end of last match.
    */
    public final void skip(){
       int we=wEnd;
       if(wOffset==we){ //requires special handling
          //if no variants at 'wOutside',advance pointer and clear
          if(top==null){ 
             wOffset++;
             flush();
          }
          //otherwise, if there exist a variant, 
          //don't clear(), i.e. allow it to match
          return;
       }
       else{
          if(we<0) wOffset=0;
          else wOffset=we;
       }
       //rflush(); //rflush() works faster on simple regexes (with a small group/branch number)
       flush();
    }
    
    private final void init(){
       //wOffset=-1;
 //System.out.println("init(): offset="+offset+", end="+end);
       wOffset=offset;
       wEnd=-1;
       called=false;
       flush();
    }
    
   /**
    * Resets the internal state.
    */
    private final void flush(){
       top=null;
       defaultEntry.reset(0);
       
 /*
 int c=0;
 SearchEntry se=first;
 while(se!=null){
    c++;
    se=se.on;
 }
 System.out.println("queue: allocated="+c+", truncating to "+minQueueLength);
 new Exception().printStackTrace();
 */
       
       first.reset(minQueueLength);
       //first.reset(0);
       for(int i=memregs.length-1;i>0;i--){
          MemReg mr=memregs[i];
          mr.in=mr.out=-1;
       }
       for(int i=memregs.length-1;i>0;i--){
          MemReg mr=memregs[i];
          mr.in=mr.out=-1;
       }
       called=false;
    }
    
    //reverse flush
    //may work significantly faster,
    //need testing
    private final void rflush(){
       SearchEntry entry=top;
       top=null;
       MemReg[] memregs=this.memregs;
       int[] counters=this.counters;
       while(entry!=null){
          SearchEntry next=entry.sub;
          SearchEntry.popState(entry,memregs,counters);
          entry=next;
       }
       SearchEntry.popState(defaultEntry,memregs,counters);
    }
    
   /**
    */
    public String toString(){
       return getString(wOffset,wEnd);
    }
    
    public Pattern pattern(){
       return re;
    }
    
    public String target(){
       return getString(offset,end);
    }
    
   /**
    */
    public char[] targetChars(){
       shared=true;
       return data;
    }
    
   /**
    */
    public int targetStart(){
       return offset;
    }
    
   /**
    */
    public int targetEnd(){
       return end;
    }
    
    public char charAt(int i){
       int in=this.wOffset;
       int out=this.wEnd;
       if(in<0 || out<in) throw new IllegalStateException("unassigned");
       return data[in+i];
    }
    
    public char charAt(int i,int groupId){
       MemReg mr=bounds(groupId);
       if(mr==null) throw new IllegalStateException("group #"+groupId+" is not assigned");
       int in=mr.in;
       if(i<0 || i>(mr.out-in)) throw new StringIndexOutOfBoundsException(""+i);
       return data[in+i];
    }
    
    public final int length(){
       return wEnd-wOffset;
    }
    
   /**
    */
    public final int start(){
       return wOffset-offset;
    }
    
   /**
    */
    public final int end(){
       return wEnd-offset;
    }
    
   /**
    */
    public String prefix(){
       return getString(offset,wOffset);
    }
    
   /**
    */
    public String suffix(){
       return getString(wEnd,end);
    }
    
   /**
    */
    public int groupCount(){
       return memregs.length;
    }
    
   /**
    */
    public String group(int n){
       MemReg mr=bounds(n);
       if(mr==null) return null;
       return getString(mr.in,mr.out);
    }
    
   /**
    */
    public String group(String name){
       Integer id=re.groupId(name);
       if(id==null) throw new IllegalArgumentException("<"+name+"> isn't defined");
       return group(id.intValue());
    }
    
   /**
    */
    public boolean getGroup(int n,TextBuffer tb){
       MemReg mr=bounds(n);
       if(mr==null) return false;
       int in;
       tb.append(data,in=mr.in,mr.out-in);
       return true;
    }
    
   /**
    */
    public boolean getGroup(String name,TextBuffer tb){
       Integer id=re.groupId(name);
       if(id==null) throw new IllegalArgumentException("unknown group: \""+name+"\"");
       return getGroup(id.intValue(),tb);
    }
    
   /**
    */
    public boolean getGroup(int n,StringBuffer sb){
       MemReg mr=bounds(n);
       if(mr==null) return false;
       int in;
       sb.append(data,in=mr.in,mr.out-in);
       return true;
    }
    
   /**
    */
    public boolean getGroup(String name,StringBuffer sb){
       Integer id=re.groupId(name);
       if(id==null) throw new IllegalArgumentException("unknown group: \""+name+"\"");
       return getGroup(id.intValue(),sb);
    }
    
   /**
    */
    public String[] groups(){
       MemReg[] memregs=this.memregs;
       String[] groups=new String[memregs.length];
       int in,out;
       MemReg mr;
       for(int i=0;i<memregs.length;i++){
          in=(mr=memregs[i]).in;
          out=mr.out;
          if((in=mr.in)<0 || mr.out<in) continue;
          groups[i]=getString(in,out);
       }
       return groups;
    }
    
   /**
    */
    public Vector groupv(){
       MemReg[] memregs=this.memregs;
       Vector v=new Vector();
       int in,out;
       MemReg mr;
       for(int i=0;i<memregs.length;i++){
          mr=bounds(i);
          if(mr==null){
             v.addElement("empty");
             continue;
          }
          String s=getString(mr.in,mr.out);
          v.addElement(s);
       }
       return v;
    }
    
    private final MemReg bounds(int id){
 //System.out.println("Matcher.bounds("+id+"):");
       MemReg mr;
       if(id>=0){
          mr=memregs[id];
       }
       else switch(id){
          case PREFIX:
             mr=prefixBounds;
             if(mr==null) prefixBounds=mr=new MemReg(PREFIX);
             mr.in=offset;
             mr.out=wOffset;
             break;
          case SUFFIX:
             mr=suffixBounds;
             if(mr==null) suffixBounds=mr=new MemReg(SUFFIX);
             mr.in=wEnd;
             mr.out=end;
             break;
          case TARGET:
             mr=targetBounds;
             if(mr==null) targetBounds=mr=new MemReg(TARGET);
             mr.in=offset;
             mr.out=end;
             break;
          default:
             throw new IllegalArgumentException("illegal group id: "+id+"; must either nonnegative int, or MatchResult.PREFIX, or MatchResult.SUFFIX");
       }
 //System.out.println("  mr=["+mr.in+","+mr.out+"]");
       int in;
       if((in=mr.in)<0 || mr.out<in) return null;
       return mr;
    }
    
   /**
    */
    public final boolean isCaptured(){
       return wOffset>=0 && wEnd>=wOffset;
    }
    
   /**
    */
    public final boolean isCaptured(int id){
       return bounds(id)!=null;
    }
    
   /**
    */
    public final boolean isCaptured(String groupName){
       Integer id=re.groupId(groupName);
       if(id==null) throw new IllegalArgumentException("unknown group: \""+groupName+"\"");
       return isCaptured(id.intValue());
    }
    
   /**
    */
    public final int length(int id){
       MemReg mr=bounds(id);
       return mr.out-mr.in;
    }
    
   /**
    */
    public final int start(int id){
       return bounds(id).in-offset;
    }
    
   /**
    */
    public final int end(int id){
       return bounds(id).out-offset;
    }
    
    private final boolean search(int anchors){
       called=true;
       final int end=this.end;
       int offset=this.offset;
       char[] data=this.data;
       int wOffset=this.wOffset;
       int wEnd=this.wEnd;
       
       MemReg[] memregs=this.memregs;
       int[] counters=this.counters;
       LAEntry[] lookaheads=this.lookaheads;
       
       //int memregCount=memregs.length;
       //int cntCount=counters.length;
       int memregCount=this.memregCount;
       int cntCount=this.counterCount;
       
       SearchEntry defaultEntry=this.defaultEntry;
       SearchEntry first=this.first;
       SearchEntry top=this.top;
       SearchEntry actual=null;
       int cnt,regLen;
       int i;
       
       final boolean matchEnd=(anchors&ANCHOR_END)>0;
       final boolean allowIncomplete=(anchors&ACCEPT_INCOMPLETE)>0;
       
       Pattern re=this.re;
       Term root=re.root;
       Term term;
       if(top==null){
          if((anchors&ANCHOR_START)>0){
             term=re.root0;  //raw root
             root=startAnchor;
          }
          else if((anchors&ANCHOR_LASTMATCH)>0){
             term=re.root0;  //raw root
             root=lastMatchAnchor;
          }
          else{
             term=root;  //optimized root
          }
          i=wOffset;
          actual=first;
          SearchEntry.popState(defaultEntry,memregs,counters);
       }
       else{
          top=(actual=top).sub;
          term=actual.term;
          i=actual.index;
          SearchEntry.popState(actual,memregs,counters);
       }
       cnt=actual.cnt;
       regLen=actual.regLen;
       
       main:
       while(wOffset<=end){
          matchHere:
          for(;;){
      /*
      System.out.print("char: "+i+", term: ");
      System.out.print(term.toString());
 
      System.out.print(" // mrs:{");
      for(int dbi=0;dbi<memregs.length;dbi++){
         System.out.print('[');
         System.out.print(memregs[dbi].in);
         System.out.print(',');
         System.out.print(memregs[dbi].out);
         System.out.print(']');
         System.out.print(' ');
      }
      System.out.print("}, crs:{");
      for(int dbi=0;dbi<counters.length;dbi++){
         System.out.print(counters[dbi]);
         if(dbi<counters.length-1)System.out.print(',');
      }
      System.out.println("}");
      */
             int memreg,cntreg;
             char c;
             switch(term.type){
                case Term.FIND:{
                   int jump=find(data,i+term.distance,end,term.target); //don't eat the last match
                   if(jump<0) break main; //return false
                   i+=jump;
                   wOffset=i; //force window to move
                   if(term.eat){
                      if(i==end) break;
                      i++;
                   }
                   term=term.next;
                   continue matchHere;
                }
                case Term.FINDREG:{
                   MemReg mr=memregs[term.target.memreg];
                   int sampleOff=mr.in;
                   int sampleLen=mr.out-sampleOff;
                   //if(sampleOff<0 || sampleLen<0) throw new Error("backreference used before definition: \\"+term.memreg);
                   /*@since 1.2*/
                   if(sampleOff<0 || sampleLen<0){
                      break;
                   }
                   else if(sampleLen==0){
                      term=term.next;
                      continue matchHere;
                   }
                   int jump=findReg(data,i+term.distance,sampleOff,sampleLen,term.target,end); //don't eat the last match
                   if(jump<0) break main; //return false
                   i+=jump;
                   wOffset=i; //force window to move
                   if(term.eat){
                      i+=sampleLen;
                      if(i>end) break;
                   }
                   term=term.next;
                   continue matchHere;
                }
                case Term.VOID:
                   term=term.next;
                   continue matchHere;
                
                case Term.CHAR:
                   //can only be 1-char-wide
                   //  \/
                   if(i>=end || data[i]!=term.c) break;
 //System.out.println("CHAR: "+data[i]+", i="+i);
                   i++;
                   term=term.next;
                   continue matchHere;
                
                case Term.ANY_CHAR:
                   //can only be 1-char-wide
                   //  \/
                   if(i>=end) break;
                   i++;
                   term=term.next;
                   continue matchHere;
                
                case Term.ANY_CHAR_NE:
                   //can only be 1-char-wide
                   //  \/
                   if(i>=end || data[i]=='\n') break;
                   i++;
                   term=term.next;
                   continue matchHere;
                
                case Term.END:
                   if(i>=end){  //meets
                      term=term.next;
                      continue matchHere;
                   }
                   break; 
                   
                case Term.END_EOL:  //perl's $
                   if(i>=end){  //meets
                      term=term.next;
                      continue matchHere;
                   }
                   else{
                      boolean matches=
                         i>=end |
                         ((i+1)==end && data[i]=='\n');
                         
                      if(matches){
                         term=term.next;
                         continue matchHere;
                      }
                      else break; 
                   }
                   
                case Term.LINE_END:
                   if(i>=end){  //meets
                      term=term.next;
                      continue matchHere;
                   }
                   else{
                      /*
                      if(((c=data[i])=='\r' || c=='\n') &&
                            (c=data[i-1])!='\r' && c!='\n'){
                         term=term.next;
                         continue matchHere;
                      }
                      */
                      //5 aug 2001
                      if(data[i]=='\n'){
                         term=term.next;
                         continue matchHere;
                      }
                   }
                   break; 
                   
                case Term.START: //Perl's "^"
                   if(i==offset){  //meets
                      term=term.next;
                      continue matchHere;
                   }
                   //break; 
                   
                   //changed on 27-04-2002
                   //due to a side effect: if ALLOW_INCOMPLETE is enabled,
                   //the anchorStart moves up to the end and succeeds 
                   //(see comments at the last lines of matchHere, ~line 1830)
                   //Solution: if there are some entries on the stack ("^a|b$"),
                   //try them; otherwise it's a final 'no'
                   //if(top!=null) break;
                   //else break main;
                   
                   //changed on 25-05-2002
                   //rationale: if the term is startAnchor, 
                   //it's the root term by definition, 
                   //so if it doesn't match, the entire pattern 
                   //couldn't match too;
                   //otherwise we could have the following problem: 
                   //"c|^a" against "abc" finds only "a"
                   if(top!=null) break;
                   if(term!=startAnchor) break;
                   else break main;
                   
                case Term.LAST_MATCH_END:
                   if(i==wEnd || wEnd == -1){  //meets
                      term=term.next;
                      continue matchHere;
                   }
                   break main; //return false
                   
                case Term.LINE_START:
                   if(i==offset){  //meets
                      term=term.next;
                      continue matchHere;
                   }
                   else if(i<end){
                      /*
                      if(((c=data[i-1])=='\r' || c=='\n') &&
                            (c=data[i])!='\r' && c!='\n'){
                         term=term.next;
                         continue matchHere;
                      }
                      */
                      //5 aug 2001
                      //if((c=data[i-1])=='\r' || c=='\n'){ ??
                      if((c=data[i-1])=='\n'){
                         term=term.next;
                         continue matchHere;
                      }
                   }
                   break; 
                   
                case Term.BITSET:{
                   //can only be 1-char-wide
                   //  \/
                   if(i>=end) break;
                   c=data[i];
                   if(!(c<=255 && term.bitset[c])^term.inverse) break;
                   i++;
                   term=term.next;
                   continue matchHere;
                }
                case Term.BITSET2:{
                   //can only be 1-char-wide
                   //  \/
                   if(i>=end) break;
                   c=data[i];
                   boolean[] arr=term.bitset2[c>>8];
                   if(arr==null || !arr[c&255]^term.inverse) break;
                   i++;
                   term=term.next;
                   continue matchHere;
                }
                case Term.BOUNDARY:{
                   boolean ch1Meets=false,ch2Meets=false;
                   boolean[] bitset=term.bitset;
                   test1:{
                      int j=i-1;
                      //if(j<offset || j>=end) break test1;
                      if(j<offset) break test1;
                      c= data[j];
                      ch1Meets= (c<256 && bitset[c]);
                   }
                   test2:{
                      //if(i<offset || i>=end) break test2;
                      if(i>=end) break test2;
                      c= data[i];
                      ch2Meets= (c<256 && bitset[c]);
                   }
                   if(ch1Meets^ch2Meets^term.inverse){  //meets
                      term=term.next;
                      continue matchHere;
                   }
                   else break;
                }
                case Term.UBOUNDARY:{
                   boolean ch1Meets=false,ch2Meets=false;
                   boolean[][] bitset2=term.bitset2;
                   test1:{
                      int j=i-1;
                      //if(j<offset || j>=end) break test1;
                      if(j<offset) break test1;
                      c= data[j];
                      boolean[] bits=bitset2[c>>8];
                      ch1Meets= bits!=null && bits[c&0xff];
                   }
                   test2:{
                      //if(i<offset || i>=end) break test2;
                      if(i>=end) break test2;
                      c= data[i];
                      boolean[] bits=bitset2[c>>8];
                      ch2Meets= bits!=null && bits[c&0xff];
                   }
                   if(ch1Meets^ch2Meets^term.inverse){  //is boundary ^ inv
                      term=term.next;
                      continue matchHere;
                   }
                   else break;
                }
                case Term.DIRECTION:{
                   boolean ch1Meets=false,ch2Meets=false;
                   boolean[] bitset=term.bitset;
                   boolean inv=term.inverse;
 //System.out.println("i="+i+", inv="+inv+", bitset="+CharacterClass.stringValue0(bitset));
                   int j=i-1;
                   //if(j>=offset && j<end){
                   if(j>=offset){
                      c= data[j];
                      ch1Meets= c<256 && bitset[c];
 //System.out.println("    ch1Meets="+ch1Meets);
                   }
                   if(ch1Meets^inv) break;
                   
                   //if(i>=offset && i<end){
                   if(i<end){
                      c= data[i];
                      ch2Meets= c<256 && bitset[c];
 //System.out.println("    ch2Meets="+ch2Meets);
                   }
                   if(!ch2Meets^inv) break;
 
 //System.out.println("    Ok");
                   
                   term=term.next;
                   continue matchHere;
                }
                case Term.UDIRECTION:{
                   boolean ch1Meets=false,ch2Meets=false;
                   boolean[][] bitset2=term.bitset2;
                   boolean inv=term.inverse;
                   int j=i-1;
                   
                   //if(j>=offset && j<end){
                   if(j>=offset){
                      c= data[j];
                      boolean[] bits=bitset2[c>>8];
                      ch1Meets= bits!=null && bits[c&0xff];
                   }
                   if(ch1Meets^inv) break;
                   
                   //if(i>=offset && i<end){
                   if(i<end){
                      c= data[i];
                      boolean[] bits=bitset2[c>>8];
                      ch2Meets= bits!=null && bits[c&0xff];
                   }
                   if(!ch2Meets^inv) break;
                   
                   term=term.next;
                   continue matchHere;
                }
                case Term.REG:{
                   MemReg mr=memregs[term.memreg];
                   int sampleOffset=mr.in;
                   int sampleOutside=mr.out;
                   int rLen;
                   if(sampleOffset<0 || (rLen=sampleOutside-sampleOffset)<0){
                      break;
                   }
                   else if(rLen==0){
                      term=term.next;
                      continue matchHere;
                   }
                   
                   // don't prevent us from reaching the 'end'
                   if((i+rLen)>end) break;
                   
                   if(compareRegions(data,sampleOffset,i,rLen,end)){
                      i+=rLen;
                      term=term.next;
                      continue matchHere;
                   }
                   break;
                }
                case Term.REG_I:{
                   MemReg mr=memregs[term.memreg];
                   int sampleOffset=mr.in;
                   int sampleOutside=mr.out;
                   int rLen;
                   if(sampleOffset<0 || (rLen=sampleOutside-sampleOffset)<0){
                      break;
                   }
                   else if(rLen==0){
                      term=term.next;
                      continue matchHere;
                   }
                   
                   // don't prevent us from reaching the 'end'
                   if((i+rLen)>end) break;
                   
                   if(compareRegionsI(data,sampleOffset,i,rLen,end)){
                      i+=rLen;
                      term=term.next;
                      continue matchHere;
                   }
                   break;
                }
                case Term.REPEAT_0_INF:{
-//System.out.println("REPEAT, i="+i+", term.minCount="+term.minCount+", term.maxCount="+term.maxCount);
+                   //System.out.println("REPEAT, i="+i+", term.minCount="+term.minCount+", term.maxCount="+term.maxCount);
                   //i+=(cnt=repeat(data,i,end,term.target));
                   if((cnt=repeat(data,i,end,term.target))<=0){
                      term=term.next;
                      continue;
                   }
                   i+=cnt;
                   
                   //branch out the backtracker (that is term.failNext, see Term.make*())
                   actual.cnt=cnt;
                   actual.term=term.failNext;
                   actual.index=i;
                   actual=(top=actual).on;
                   if(actual==null){
                         actual=new SearchEntry();
                         top.on=actual;
                         actual.sub=top;
                   }
                   term=term.next;
                   continue;
                }
                case Term.REPEAT_MIN_INF:{
 //System.out.println("REPEAT, i="+i+", term.minCount="+term.minCount+", term.maxCount="+term.maxCount);
                   cnt=repeat(data,i,end,term.target);
                   if(cnt<term.minCount) break;
                   i+=cnt;
                   
                   //branch out the backtracker (that is term.failNext, see Term.make*())
                   actual.cnt=cnt;
                   actual.term=term.failNext;
                   actual.index=i;
                   actual=(top=actual).on;
                   if(actual==null){
                         actual=new SearchEntry();
                         top.on=actual;
                         actual.sub=top;
                   }
                   term=term.next;
                   continue;
                }
                case Term.REPEAT_MIN_MAX:{
 //System.out.println("REPEAT, i="+i+", term.minCount="+term.minCount+", term.maxCount="+term.maxCount);
                   int out1=end;
                   int out2=i+term.maxCount;
                   cnt=repeat(data,i,out1<out2? out1: out2,term.target);
                   if(cnt<term.minCount) break;
                   i+=cnt;
                   
                   //branch out the backtracker (that is term.failNext, see Term.make*())
                   actual.cnt=cnt;
                   actual.term=term.failNext;
                   actual.index=i;
                   actual=(top=actual).on;
                   if(actual==null){
                         actual=new SearchEntry();
                         top.on=actual;
                         actual.sub=top;
                   }
                   term=term.next;
                   continue;
                }
                case Term.REPEAT_REG_MIN_INF:{
                	 MemReg mr=memregs[term.memreg];
                   int sampleOffset=mr.in;
                   int sampleOutside=mr.out;
                   //if(sampleOffset<0) throw new Error("register is referred before definition: "+term.memreg);
                   //if(sampleOutside<0 || sampleOutside<sampleOffset) throw new Error("register is referred within definition: "+term.memreg);
                   /*@since 1.2*/
                   int bitset;
                   if(sampleOffset<0 || (bitset=sampleOutside-sampleOffset)<0){
                      break;
                   }
                   else if(bitset==0){
                      term=term.next;
                      continue matchHere;
                   }
                   
                   cnt=0;
                   
                   while(compareRegions(data,i,sampleOffset,bitset,end)){
                      cnt++;
                      i+=bitset;
                   }
                   
                   if(cnt<term.minCount) break;
                   
                   actual.cnt=cnt;
                   actual.term=term.failNext;
                   actual.index=i;
                   actual.regLen=bitset;
                   actual=(top=actual).on;
                   if(actual==null){
                      actual=new SearchEntry();
                      top.on=actual;
                      actual.sub=top;
                   }
                   term=term.next;
                   continue;
                }
                case Term.REPEAT_REG_MIN_MAX:{
                	   MemReg mr=memregs[term.memreg];
                   int sampleOffset=mr.in;
                   int sampleOutside=mr.out;
                   //if(sampleOffset<0) throw new Error("register is referred before definition: "+term.memreg);
                   //if(sampleOutside<0 || sampleOutside<sampleOffset) throw new Error("register is referred within definition: "+term.memreg);
                   /*@since 1.2*/
                   int bitset;
                   if(sampleOffset<0 || (bitset=sampleOutside-sampleOffset)<0){
                      break;
                   }
                   else if(bitset==0){
                      term=term.next;
                      continue matchHere;
                   }
                   
                   cnt=0;
                   int countBack=term.maxCount;
                   while(countBack>0 && compareRegions(data,i,sampleOffset,bitset,end)){
                      cnt++;
                      i+=bitset;
                      countBack--;
                   }
                   
                   if(cnt<term.minCount) break;
                   
                   actual.cnt=cnt;
                   actual.term=term.failNext;
                   actual.index=i;
                   actual.regLen=bitset;
                   actual=(top=actual).on;
                   if(actual==null){
                      actual=new SearchEntry();
                      top.on=actual;
                      actual.sub=top;
                   }
                   term=term.next;
                   continue;
                }
                case Term.BACKTRACK_0:
 //System.out.println("<<");
                   cnt=actual.cnt;
                   if(cnt>0){
                      cnt--;
                      i--;
                      actual.cnt=cnt;
                      actual.index=i;
                      actual.term=term;
                      actual=(top=actual).on;
                      if(actual==null){
                         actual=new SearchEntry();
                         top.on=actual;
                         actual.sub=top;
                      }
                      term=term.next;
                      continue;
                   }
                   else break;
                
                case Term.BACKTRACK_MIN:
 //System.out.println("<<");
                   cnt=actual.cnt;
                   if(cnt>term.minCount){
                      cnt--;
                      i--;
                      actual.cnt=cnt;
                      actual.index=i;
                      actual.term=term;
                      actual=(top=actual).on;
                      if(actual==null){
                         actual=new SearchEntry();
                         top.on=actual;
                         actual.sub=top;
                      }
                      term=term.next;
                      continue;
                   }
                   else break;
                
                case Term.BACKTRACK_FIND_MIN:{
 //System.out.print("<<<[cnt=");
                   cnt=actual.cnt;
 //System.out.print(cnt+", minCnt=");
 //System.out.print(term.minCount+", target=");
 //System.out.print(term.target+"]");
                   int minCnt;
                   if(cnt>(minCnt=term.minCount)){
                      int start=i+term.distance;
                      if(start>end){
                         int exceed=start-end;
                         cnt-=exceed;
                         if(cnt<=minCnt) break;
                         i-=exceed;
                         start=end;
                      }
                      int back=findBack(data,i+term.distance,cnt-minCnt,term.target);
 //System.out.print("[back="+back+"]");
                      if(back<0) break;
                      
                      //cnt-=back;
                      //i-=back;
                      if((cnt-=back)<=minCnt){
                         i-=back;
                         if(term.eat)i++;
                         term=term.next;
                         continue;
                      }
                      i-=back;
                      
                      actual.cnt=cnt;
                      actual.index=i;
                      
                      if(term.eat)i++;
                      
                      actual.term=term;
                      actual=(top=actual).on;
                      if(actual==null){
                         actual=new SearchEntry();
                         top.on=actual;
                         actual.sub=top;
                      }
                      term=term.next;
                      continue;
                   }
                   else break;
                }
                
                case Term.BACKTRACK_FINDREG_MIN:{
 //System.out.print("<<<[cnt=");
                   cnt=actual.cnt;
 //System.out.print(cnt+", minCnt=");
 //System.out.print(term.minCount+", target=");
 //System.out.print(term.target);
 //System.out.print("reg=<"+memregs[term.target.memreg].in+","+memregs[term.target.memreg].out+">]");
                   int minCnt;
                   if(cnt>(minCnt=term.minCount)){
                      int start=i+term.distance;
                      if(start>end){
                         int exceed=start-end;
                         cnt-=exceed;
                         if(cnt<=minCnt) break;
                         i-=exceed;
                         start=end;
                      }
                      MemReg mr=memregs[term.target.memreg];
                      int sampleOff=mr.in;
                      int sampleLen=mr.out-sampleOff;
                      //if(sampleOff<0 || sampleLen<0) throw new Error("backreference used before definition: \\"+term.memreg);
                      //int back=findBackReg(data,i+term.distance,sampleOff,sampleLen,cnt-minCnt,term.target,end);
                      //if(back<0) break;
                      /*@since 1.2*/
                      int back;
                      if(sampleOff<0 || sampleLen<0){ 
                      //the group is not def., as in the case of '(\w+)\1'
                      //treat as usual BACKTRACK_MIN
                         cnt--;
                         i--;
                         actual.cnt=cnt;
                         actual.index=i;
                         actual.term=term;
                         actual=(top=actual).on;
                         if(actual==null){
                            actual=new SearchEntry();
                            top.on=actual;
                            actual.sub=top;
                         }
                         term=term.next;
                         continue;
                      }
                      else if(sampleLen==0){
                         back=1;
                      }
                      else{
                         back=findBackReg(data,i+term.distance,sampleOff,sampleLen,cnt-minCnt,term.target,end);
 //System.out.print("[back="+back+"]");
                         if(back<0) break;
                      }
                      cnt-=back;
                      i-=back;
                      actual.cnt=cnt;
                      actual.index=i;
                      
                      if(term.eat)i+=sampleLen;
                      
                      actual.term=term;
                      actual=(top=actual).on;
                      if(actual==null){
                         actual=new SearchEntry();
                         top.on=actual;
                         actual.sub=top;
                      }
                      term=term.next;
                      continue;
                   }
                   else break;
                }
                
                case Term.BACKTRACK_REG_MIN:
 //System.out.println("<<");
                   cnt=actual.cnt;
                   if(cnt>term.minCount){
                      regLen=actual.regLen;
                      cnt--;
                      i-=regLen;
                      actual.cnt=cnt;
                      actual.index=i;
                      actual.term=term;
                      //actual.regLen=regLen;
                      actual=(top=actual).on;
                      if(actual==null){
                         actual=new SearchEntry();
                         top.on=actual;
                         actual.sub=top;
                      }
                      term=term.next;
                      continue;
                   }
                   else break;
                
                case Term.GROUP_IN:{
                   memreg=term.memreg;
                   //memreg=0 is a regex itself; we don't need to handle it
                   //because regex bounds already are in wOffset and wEnd
                   if(memreg>0){
                      //MemReg mr=memregs[memreg];
                      //saveMemregState((top!=null)? top: defaultEntry,memreg,mr);
                      //mr.in=i;
                      
                      memregs[memreg].tmp=i; //assume
                   }
                   term=term.next;
                   continue;
                }
                case Term.GROUP_OUT:
                   memreg=term.memreg;
                   //see above
                   if(memreg>0){
                      //if(term.saveState)saveMemregState((top!=null)? top: defaultEntry,memreg,memregs);
                      
                      MemReg mr=memregs[memreg];
                      SearchEntry.saveMemregState((top!=null)? top: defaultEntry,memreg,mr);
                      mr.in=mr.tmp; //commit
                      mr.out=i;
                   }
                   term=term.next;
                   continue;
                
                case Term.PLOOKBEHIND_IN:{
                   int tmp=i-term.distance;
                   if(tmp<offset) break;
 //System.out.println("term="+term+", next="+term.next);
                	 LAEntry le=lookaheads[term.lookaheadId];
                	 le.index=i;
                   i=tmp;
                   le.actual=actual;
                   le.top=top;
                   term=term.next;
                   continue;
                }
                case Term.INDEPENDENT_IN:
                case Term.PLOOKAHEAD_IN:{
                	 LAEntry le=lookaheads[term.lookaheadId];
                	 le.index=i;
                   le.actual=actual;
                   le.top=top;
                   term=term.next;
                   continue;
                }
                case Term.LOOKBEHIND_CONDITION_OUT:
                case Term.LOOKAHEAD_CONDITION_OUT:
                case Term.PLOOKAHEAD_OUT:
                case Term.PLOOKBEHIND_OUT:{
                   LAEntry le=lookaheads[term.lookaheadId];
                	   i=le.index;
                   actual=le.actual;
                   top=le.top;
                   term=term.next;
                   continue;
                }
                case Term.INDEPENDENT_OUT:{
                   LAEntry le=lookaheads[term.lookaheadId];
                	   actual=le.actual;
                   top=le.top;
                   term=term.next;
                   continue;
                }
                case Term.NLOOKBEHIND_IN:{
                   int tmp=i-term.distance;
                   if(tmp<offset){
                      term=term.failNext;
                      continue;
                   }
                   LAEntry le=lookaheads[term.lookaheadId];
                   le.actual=actual;
                   le.top=top;
                   
                   actual.term=term.failNext;
                   actual.index=i;
                   i=tmp;
                   actual=(top=actual).on;
                   if(actual==null){
                      actual=new SearchEntry();
                      top.on=actual;
                      actual.sub=top;
                   }
                   term=term.next;
                   continue;
                }
                case Term.NLOOKAHEAD_IN:{
                   LAEntry le=lookaheads[term.lookaheadId];
                   le.actual=actual;
                   le.top=top;
                   
                   actual.term=term.failNext;
                   actual.index=i;
                   actual=(top=actual).on;
                   if(actual==null){
                      actual=new SearchEntry();
                      top.on=actual;
                      actual.sub=top;
                   }
                   
                   term=term.next;
                   continue;
                }
                case Term.NLOOKBEHIND_OUT:
                case Term.NLOOKAHEAD_OUT:{
                   LAEntry le=lookaheads[term.lookaheadId];
                	 actual=le.actual;
                   top=le.top;
                   break;
                }
                case Term.LOOKBEHIND_CONDITION_IN:{
                   int tmp=i-term.distance;
                   if(tmp<offset){
                      term=term.failNext;
                      continue;
                   }
                   LAEntry le=lookaheads[term.lookaheadId];
                   le.index=i;
                   le.actual=actual;
                   le.top=top;
                   
                   actual.term=term.failNext;
                   actual.index=i;
                   actual=(top=actual).on;
                   if(actual==null){
                      actual=new SearchEntry();
                      top.on=actual;
                      actual.sub=top;
                   }
                   
                   i=tmp;
                   
                   term=term.next;
                   continue;
                }
                case Term.LOOKAHEAD_CONDITION_IN:{
                   LAEntry le=lookaheads[term.lookaheadId];
                   le.index=i;
                   le.actual=actual;
                   le.top=top;
                   
                   actual.term=term.failNext;
                   actual.index=i;
                   actual=(top=actual).on;
                   if(actual==null){
                      actual=new SearchEntry();
                      top.on=actual;
                      actual.sub=top;
                   }
                   
                   term=term.next;
                   continue;
                }
                case Term.MEMREG_CONDITION:{
                   MemReg mr=memregs[term.memreg];
                   int sampleOffset=mr.in;
                   int sampleOutside=mr.out;
                   if(sampleOffset>=0 && sampleOutside>=0 && sampleOutside>=sampleOffset){
                      term=term.next;
                   }
                   else{
                      term=term.failNext;
                   }
                   continue;
                }
                case Term.BRANCH_STORE_CNT_AUX1:
                   actual.regLen=regLen;
                case Term.BRANCH_STORE_CNT:
                   actual.cnt=cnt;
                case Term.BRANCH:
                   actual.term=term.failNext;
                   actual.index=i;
                   actual=(top=actual).on;
                   if(actual==null){
                      actual=new SearchEntry();
                      top.on=actual;
                      actual.sub=top;
                   }
                   term=term.next;
                   continue;
 
                case Term.SUCCESS:
 //System.out.println("success, matchEnd="+matchEnd+", i="+i+", end="+end);
                   if(!matchEnd || i==end){
                      this.wOffset=memregs[0].in=wOffset;
                      this.wEnd=memregs[0].out=i;
                      this.top=top;
                      return true;
                   }
                   else break;
 
                case Term.CNT_SET_0:
                   cnt=0;
                   term=term.next;
                   continue;
 
                case Term.CNT_INC:
                   cnt++;
                   term=term.next;
                   continue;
                
                case Term.CNT_GT_EQ:
                   if(cnt>=term.maxCount){
                      term=term.next;
                      continue;
                   }
                   else break;
                
                case Term.READ_CNT_LT:
                   cnt=actual.cnt;
                   if(cnt<term.maxCount){
                      term=term.next;
                      continue;
                   }
                   else break;
                
                case Term.CRSTORE_CRINC:{
                	 int cntvalue=counters[cntreg=term.cntreg];
                   SearchEntry.saveCntState((top!=null)? top: defaultEntry,cntreg,cntvalue);
                   counters[cntreg]=++cntvalue;
                   term=term.next;
                   continue;
                }
                case Term.CR_SET_0:
                   counters[term.cntreg]=0;
 
                   term=term.next;
                   continue;
 
                case Term.CR_LT:
                   if(counters[term.cntreg]<term.maxCount){
                      term=term.next;
                      continue;
                   }
                   else break;
 
                case Term.CR_GT_EQ:
                   if(counters[term.cntreg]>=term.maxCount){
                      term=term.next;
                      continue;
                   }
                   else break;
                                
                default:
                   throw new Error("unknown term type: "+term.type);
             }
             
             //if(top==null) break matchHere;
             if(allowIncomplete && i==end){
                //an attempt to implement matchesPrefix()
                //not sure it's a good way
                //27-04-2002: just as expencted, 
                //the side effect was found (and POSSIBLY fixed); 
                //see the case Term.START
                return true;
             }
             if(top==null){
                break matchHere;
             }
             
             //pop the stack
             top=(actual=top).sub;
             term=actual.term;
             i=actual.index;
 //System.out.println("***POP*** :  branch to #"+term.instanceNum+" at "+i);
             if(actual.isState){
                SearchEntry.popState(actual,memregs,counters);
             }
          }
          
          if(defaultEntry.isState)SearchEntry.popState(defaultEntry,memregs,counters);
          
          term=root;
          //wOffset++;
          //i=wOffset;
          i=++wOffset;
       }
       this.wOffset=wOffset;
       this.top=top;
       
       return false;
    }
    
    private static final boolean compareRegions(char[] arr, int off1, int off2, int len,int out){
 //System.out.print("out="+out+", off1="+off1+", off2="+off2+", len="+len+", reg1="+new String(arr,off1,len)+", reg2="+new String(arr,off2,len));
       int p1=off1+len-1;
       int p2=off2+len-1;
       if(p1>=out || p2>=out){
 //System.out.println(" : out");
          return false;
       }
       for(int c=len;c>0;c--,p1--,p2--){
          if(arr[p1]!=arr[p2]){
 //System.out.println(" : no");
          	  return false;
          }
       }
 //System.out.println(" : yes");
       return true;
    }
    
    private static final boolean compareRegionsI(char[] arr, int off1, int off2, int len,int out){
       int p1=off1+len-1;
       int p2=off2+len-1;
       if(p1>=out || p2>=out){
          return false;
       }
       char c1,c2;
       for(int c=len;c>0;c--,p1--,p2--){
          if((c1=arr[p1])!=Character.toLowerCase(c2=arr[p2]) &&
             c1!=Character.toUpperCase(c2) &&
             c1!=Character.toTitleCase(c2)) return false;
       }
       return true;
    }
    
    //repeat while matches
    private static final int repeat(char[] data,int off,int out,Term term){
 //System.out.print("off="+off+", out="+out+", term="+term);
       switch(term.type){
          case Term.CHAR:{
             char c=term.c;
             int i=off;
             while(i<out){
                if(data[i]!=c) break;
                i++;
             }
 //System.out.println(", returning "+(i-off));
             return i-off;
          }
          case Term.ANY_CHAR:{
             return out-off;
          }
          case Term.ANY_CHAR_NE:{
             int i=off;
             while(i<out){
                if(data[i]=='\n') break;
                i++;
             }
             return i-off;
          }
          case Term.BITSET:{
             boolean[] arr=term.bitset;
             int i=off;
             char c;
             if(term.inverse) while(i<out){
                if((c=data[i])<=255 && arr[c]) break;
                else i++;
             }
             else while(i<out){
                if((c=data[i])<=255 && arr[c]) i++;
                else break;
             }
             return i-off;
          }
          case Term.BITSET2:{
             int i=off;
             boolean[][] bitset2=term.bitset2;
             char c;
             if(term.inverse) while(i<out){
                boolean[] arr=bitset2[(c=data[i])>>8];
                if(arr!=null && arr[c&0xff]) break;
                else i++;
             }
             else while(i<out){
                boolean[] arr=bitset2[(c=data[i])>>8];
                if(arr!=null && arr[c&0xff]) i++;
                else break;
             }
             return i-off;
          }
       }
       throw new Error("this kind of term can't be quantified:"+term.type);
    }
    
    //repeat while doesn't match
    private static final int find(char[] data,int off,int out,Term term){
 //System.out.print("off="+off+", out="+out+", term="+term);
       if(off>=out) return -1;
       switch(term.type){
          case Term.CHAR:{
             char c=term.c;
             int i=off;
             while(i<out){
                if(data[i]==c) break;
                i++;
             }
 //System.out.println(", returning "+(i-off));
             return i-off;
          }
          case Term.BITSET:{
             boolean[] arr=term.bitset;
             int i=off;
             char c;
             if(!term.inverse) while(i<out){
                if((c=data[i])<=255 && arr[c]) break;
                else i++;
             }
             else while(i<out){
                if((c=data[i])<=255 && arr[c]) i++;
                else break;
             }
             return i-off;
          }
          case Term.BITSET2:{
             int i=off;
             boolean[][] bitset2=term.bitset2;
             char c;
             if(!term.inverse) while(i<out){
                boolean[] arr=bitset2[(c=data[i])>>8];
                if(arr!=null && arr[c&0xff]) break;
                else i++;
             }
             else while(i<out){
                boolean[] arr=bitset2[(c=data[i])>>8];
                if(arr!=null && arr[c&0xff]) i++;
                else break;
             }
             return i-off;
          }
       }
       throw new IllegalArgumentException("can't seek this kind of term:"+term.type);
    }
    
    
    private static final int findReg(char[] data,int off,int regOff,int regLen,Term term,int out){
 //System.out.print("off="+off+", out="+out+", term="+term);
       if(off>=out) return -1;
       int i=off;
       if(term.type==Term.REG){
          while(i<out){
             if(compareRegions(data,i,regOff,regLen,out)) break;
             i++;
          }
       }
       else if(term.type==Term.REG_I){
          while(i<out){
             if(compareRegionsI(data,i,regOff,regLen,out)) break;
             i++;
          }
       }
       else throw new IllegalArgumentException("wrong findReg() target:"+term.type);
       return off-i;
    }
    
    private static final int findBack(char[] data,int off,int maxCount,Term term){
-//System.out.print("off="+off+", maxCount="+maxCount+", term="+term);
+ //System.out.print("off="+off+", maxCount="+maxCount+", term="+term);
       switch(term.type){
          case Term.CHAR:{
             char c=term.c;
             int i=off;
             int iMin=off-maxCount;
             for(;;){
                if(data[--i]==c) break;
                if(i<=iMin) return -1; 
             }
 //System.out.println(", returning "+(off-i));
             return off-i;
          }
          case Term.BITSET:{
             boolean[] arr=term.bitset;
             int i=off;
             char c;
             int iMin=off-maxCount;
             if(!term.inverse) for(;;){
                if((c=data[--i])<=255 && arr[c]) break;
                if(i<=iMin) return -1;
             }
             else for(;;){
                if((c=data[--i])>255 || !arr[c]) break;
                if(i<=iMin) return -1;
             }
             return off-i;
          }
          case Term.BITSET2:{
             boolean[][] bitset2=term.bitset2;
             int i=off;
             char c;
             int iMin=off-maxCount;
             if(!term.inverse) for(;;){
                boolean[] arr=bitset2[(c=data[--i])>>8];
                if(arr!=null && arr[c&0xff]) break;
                if(i<=iMin) return -1;
             }
             else for(;;){
                boolean[] arr=bitset2[(c=data[--i])>>8];
                if(arr==null || arr[c&0xff]) break;
                if(i<=iMin) return -1;
             }
             return off-i;
          }
       }
       throw new IllegalArgumentException("can't find this kind of term:"+term.type);
    }
    
    private static final int findBackReg(char[] data,int off,int regOff,int regLen,int maxCount,Term term,int out){
       //assume that the cases when regLen==0 or maxCount==0 are handled by caller
       int i=off;
       int iMin=off-maxCount;
       if(term.type==Term.REG){
          /*@since 1.2*/
          char first=data[regOff];
          regOff++;
          regLen--;
          for(;;){
             i--;
             if(data[i]==first && compareRegions(data,i+1,regOff,regLen,out)) break;
             if(i<=iMin) return -1;
          }
       }
       else if(term.type==Term.REG_I){
          /*@since 1.2*/
          char c=data[regOff];
          char firstLower=Character.toLowerCase(c);
          char firstUpper=Character.toUpperCase(c);
          char firstTitle=Character.toTitleCase(c);
          regOff++;
          regLen--;
          for(;;){
             i--;
             if(((c=data[i])==firstLower || c==firstUpper || c==firstTitle) && compareRegionsI(data,i+1,regOff,regLen,out)) break;
             if(i<=iMin) return -1;
          }
          return off-i;
       }
       else throw new IllegalArgumentException("wrong findBackReg() target type :"+term.type);
       return off-i;
    }
    
    public String toString_d(){
       StringBuffer s=new StringBuffer();
       s.append("counters: ");
       s.append(counters==null? 0: counters.length);
 
       s.append("\r\nmemregs: ");
       s.append(memregs.length);
       for(int i=0;i<memregs.length;i++) s.append("\r\n #"+i+": ["+memregs[i].in+","+memregs[i].out+"](\""+getString(memregs[i].in,memregs[i].out)+"\")");
    
       s.append("\r\ndata: ");
       if(data!=null)s.append(data.length);
       else s.append("[none]");
       
       s.append("\r\noffset: ");
       s.append(offset);
    
       s.append("\r\nend: ");
       s.append(end);
    
       s.append("\r\nwOffset: ");
       s.append(wOffset);
    
       s.append("\r\nwEnd: ");
       s.append(wEnd);
    
       s.append("\r\nregex: ");
       s.append(re);
       return s.toString();
    }
 }
 
 class SearchEntry{
    Term term;
    int index;
    int cnt;
    int regLen;
    
    boolean isState;
    
    SearchEntry sub,on;
    
    private static class MState{
       int index,in,out;
       MState next,prev;
    }
    
    private static class CState{
       int index,value;
       CState next,prev;
    }
    
    private MState mHead,mCurrent;
    private CState cHead,cCurrent;
    
    final static void saveMemregState(SearchEntry entry,int memreg, MemReg mr){
 //System.out.println("saveMemregState("+entry+","+memreg+"):");
       entry.isState=true;
       MState current=entry.mCurrent;
       if(current==null){
          MState head=entry.mHead;
          if(head==null) entry.mHead=entry.mCurrent=current=new MState();
          else current=head;
       }
       else{
          MState next=current.next;
          if(next==null){
             current.next=next=new MState();
             next.prev=current;
          }
          current=next;
       }
       current.index=memreg;
       current.in=mr.in;
       current.out=mr.out;
       entry.mCurrent=current;
    }
    
    final static void saveCntState(SearchEntry entry,int cntreg,int value){
       entry.isState=true;
       CState current=entry.cCurrent;
       if(current==null){
          CState head=entry.cHead;
          if(head==null) entry.cHead=entry.cCurrent=current=new CState();
          else current=head;
       }
       else{
          CState next=current.next;
          if(next==null){
             current.next=next=new CState();
             next.prev=current;
          }
          current=next;
       }
       current.index=cntreg;
       current.value=value;
       entry.cCurrent=current;
    }
    
    final static void popState(SearchEntry entry, MemReg[] memregs, int[] counters){
 //System.out.println("popState("+entry+"):");
       MState ms=entry.mCurrent;
       while(ms!=null){
          MemReg mr=memregs[ms.index];
          mr.in=ms.in;
          mr.out=ms.out;
          ms=ms.prev;
       }
       CState cs=entry.cCurrent;
       while(cs!=null){
          counters[cs.index]=cs.value;
          cs=cs.prev;
       }
       entry.mCurrent=null;
       entry.cCurrent=null;
       entry.isState=false;
    }
    
    final void reset(int restQueue){
       term=null;
       index=cnt=regLen=0;
       
       mCurrent=null;
       cCurrent=null;
       isState=false;
       
       SearchEntry on=this.on;
       if(on!=null){
          if(restQueue>0) on.reset(restQueue-1);
          else{
             this.on=null;
             on.sub=null;
          }
       }
       //sub=on=null;      
    }
 }
 
 class MemReg{
    int index;
    
    int in=-1,out=-1;
    int tmp=-1;  //for assuming at GROUP_IN
    
    MemReg(int index){
       this.index=index;
    }
    
    void reset(){
       in=out=-1;
    }
 }
 
 class LAEntry{
    int index;
    SearchEntry top,actual;
 }
diff --git a/src/jregex/Pattern.java b/src/jregex/Pattern.java
index 6462b52126..5a4cca22ea 100644
--- a/src/jregex/Pattern.java
+++ b/src/jregex/Pattern.java
@@ -1,410 +1,444 @@
 /**
  * Copyright (c) 2001, Sergey A. Samokhodkin
  * All rights reserved.
  * 
  * Redistribution and use in source and binary forms, with or without modification, 
  * are permitted provided that the following conditions are met:
  * 
  * - Redistributions of source code must retain the above copyright notice, 
  * this list of conditions and the following disclaimer. 
  * - Redistributions in binary form 
  * must reproduce the above copyright notice, this list of conditions and the following 
  * disclaimer in the documentation and/or other materials provided with the distribution.
  * - Neither the name of jregex nor the names of its contributors may be used 
  * to endorse or promote products derived from this software without specific prior 
  * written permission. 
  * 
  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
  * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
  * IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
  * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
  * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
  * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY 
  * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  * 
  * @version 1.2_01
  */
 
 package jregex;
 
 import java.io.*;
 import java.util.*;
 
 /**
  * A handle for a precompiled regular expression.<br>
  * To match a regular expression <code>myExpr</code> against a text <code>myString</code> one should first create a Pattern object:<pre>
  * Pattern p=new Pattern(myExpr);
  * </pre>
  * then obtain a Matcher object:<pre>
  * Matcher matcher=p.matcher(myText);
  * </pre>
  * The latter is an automaton that actually performs a search. It provides the following methods:
  * <li> search for matching substrings : matcher.find() or matcher.findAll();
  * <li> test whether the text matches the whole pattern : matcher.matches();
  * <li> test whether the text matches the beginning of the pattern : matcher.matchesPrefix();
  * <li> search with custom options : matcher.find(int options)
  * <p>
  * <b>Flags</b><br>
  * Flags (see REFlags interface) change the meaning of some regular expression elements at compiletime.
  * These flags may be passed both as string(see Pattern(String,String)) and as bitwise OR of:
  * <li><b>REFlags.IGNORE_CASE</b> - enables case insensitivity
  * <li><b>REFlags.MULTILINE</b> - forces "^" and "$" to match both at the start and the end of line;
  * <li><b>REFlags.DOTALL</b> - forces "." to match eols('\r' and '\n' in ASCII);
  * <li><b>REFlags.IGNORE_SPACES</b> - literal spaces in expression are ignored for better readability;
  * <li><b>REFlags.UNICODE</b> - the predefined classes('\w','\d',etc) are referenced to Unicode;
  * <li><b>REFlags.XML_SCHEMA</b> - permits XML Schema regular expressions syntax extentions.
  * <p>
  * <b>Multithreading</b><br>
  * Pattern instances are thread-safe, i.e. the same Pattern object may be used 
  * by any number of threads simultaniously. On the other hand, the Matcher objects 
  * are NOT thread safe, so, given a Pattern instance, each thread must obtain 
  * and use its own Matcher.
  * 
  * @see        REFlags
  * @see        Matcher
  * @see        Matcher#setTarget(java.lang.String)
  * @see        Matcher#setTarget(java.lang.String,int,int)
  * @see        Matcher#setTarget(char[],int,int)
  * @see        Matcher#setTarget(java.io.Reader,int)
  * @see        MatchResult
  * @see        MatchResult#group(int)
  * @see        MatchResult#start(int)
  * @see        MatchResult#end(int)
  * @see        MatchResult#length(int)
  * @see        MatchResult#charAt(int,int)
  * @see        MatchResult#prefix()
  * @see        MatchResult#suffix()
  */
 
 public class Pattern implements Serializable,REFlags{
    String stringRepr;
    
    // tree entry
    Term root,root0;
    
    // required number of memory slots
    int memregs;
    
    // required number of iteration counters
    int counters;
    
    // number of lookahead groups
    int lookaheads;
    
    Hashtable namedGroupMap;
    
    protected Pattern() throws PatternSyntaxException{}
    
   /**
    * Compiles an expression with default flags.
    * @param      <code>regex</code>   the Perl5-compatible regular expression string.
    * @exception  PatternSyntaxException  if the argument doesn't correspond to perl5 regex syntax.
    * @see        Pattern#Pattern(java.lang.String,java.lang.String)
    * @see        Pattern#Pattern(java.lang.String,int)
    */
    public Pattern(String regex) throws PatternSyntaxException{
       this(regex,DEFAULT);
    }
       
   /**
    * Compiles a regular expression using Perl5-style flags.
    * The flag string should consist of letters 'i','m','s','x','u','X'(the case is significant) and a hyphen.
    * The meaning of letters:
    * <ul>
    * <li><b>i</b> - case insensitivity, corresponds to REFLlags.IGNORE_CASE;
    * <li><b>m</b> - multiline treatment(BOLs and EOLs affect the '^' and '$'), corresponds to REFLlags.MULTILINE flag;
    * <li><b>s</b> - single line treatment('.' matches \r's and \n's),corresponds to REFLlags.DOTALL;
    * <li><b>x</b> - extended whitespace comments (spaces and eols in the expression are ignored), corresponds to REFLlags.IGNORE_SPACES.
    * <li><b>u</b> - predefined classes are regarded as belonging to Unicode, corresponds to REFLlags.UNICODE; this may yield some performance penalty.
    * <li><b>X</b> - compatibility with XML Schema, corresponds to REFLlags.XML_SCHEMA.
    * </ul>
    * @param      <code>regex</code>  the Perl5-compatible regular expression string.
    * @param      <code>flags</code>  the Perl5-compatible flags.
    * @exception  PatternSyntaxException  if the argument doesn't correspond to perl5 regex syntax.
    * see REFlags
    */
    public Pattern(String regex,String flags) throws PatternSyntaxException{
       stringRepr=regex;
       compile(regex,parseFlags(flags));
    }
    
   /**
    * Compiles a regular expression using REFlags.
    * The <code>flags</code> parameter is a bitwise OR of the folloing values:
    * <ul>
    * <li><b>REFLlags.IGNORE_CASE</b> - case insensitivity, corresponds to '<b>i</b>' letter;
    * <li><b>REFLlags.MULTILINE</b> - multiline treatment(BOLs and EOLs affect the '^' and '$'), corresponds to '<b>m</b>';
    * <li><b>REFLlags.DOTALL</b> - single line treatment('.' matches \r's and \n's),corresponds to '<b>s</b>';
    * <li><b>REFLlags.IGNORE_SPACES</b> - extended whitespace comments (spaces and eols in the expression are ignored), corresponds to '<b>x</b>'.
    * <li><b>REFLlags.UNICODE</b> - predefined classes are regarded as belonging to Unicode, corresponds to '<b>u</b>'; this may yield some performance penalty.
    * <li><b>REFLlags.XML_SCHEMA</b> - compatibility with XML Schema, corresponds to '<b>X</b>'.
    * </ul>
    * @param      <code>regex</code>  the Perl5-compatible regular expression string.
    * @param      <code>flags</code>  the Perl5-compatible flags.
    * @exception  PatternSyntaxException  if the argument doesn't correspond to perl5 regex syntax.
    * see REFlags
    */
    public Pattern(String regex, int flags) throws PatternSyntaxException{
       compile(regex,flags);
    }
    
   /*
    //java.util.regex.* compatibility
    public static Pattern compile(String regex,int flags) throws PatternSyntaxException{
       Pattern p=new Pattern();
       p.compile(regex,flags);
       return flags;
    }
    */
    
    protected void compile(String regex,int flags) throws PatternSyntaxException{
       stringRepr=regex;
       Term.makeTree(regex,flags,this);
+      //      printTree(root, 0);
    }
+    private void printTree(Term val, int depth) {
+        for(int i=0;i<depth;i++) {
+            System.err.print(" ");
+        }
+        if(val == null) {
+            System.err.println("null");
+        } else if(depth > 15) {
+            System.err.println("to deep");
+        } else {
+            System.err.println("" + val.type + "==" + val);
+            if(val.next != null) {
+                for(int i=0;i<depth;i++) {
+                    System.err.print("-");
+                }
+                System.err.println("-next:");
+                printTree(val.next, depth+1);
+            }
+            if(val.failNext != null) {
+            for(int i=0;i<depth;i++) {
+                System.err.print("-");
+            }
+            System.err.println("-failNext:");
+            printTree(val.failNext, depth+1);
+            }
+            if(val.target != null) {
+            for(int i=0;i<depth;i++) {
+                System.err.print("-");
+            }
+            System.err.println("-target:");
+            printTree(val.target, depth+1);
+            }
+        }
+    }
    
   /**
    * How many capturing groups this expression includes?
    */
    public int groupCount(){
       return memregs;
    }
    
   /**
    * Get numeric id for a group name.
    * @return <code>null</code> if no such name found.
    * @see MatchResult#group(java.lang.String)
    * @see MatchResult#isCaptured(java.lang.String)
    */
    public Integer groupId(String name){
       return ((Integer)namedGroupMap.get(name));
    }
    
   /**
    * A shorthand for Pattern.matcher(String).matches().<br>
    * @param s the target
    * @return true if the entire target matches the pattern 
    * @see Matcher#matches()
    * @see Matcher#matches(String)
    */
    public boolean matches(String s){
       return matcher(s).matches();
    }
    
   /**
    * A shorthand for Pattern.matcher(String).matchesPrefix().<br>
    * @param s the target
    * @return true if the entire target matches the beginning of the pattern
    * @see Matcher#matchesPrefix()
    */
    public boolean startsWith(String s){
       return matcher(s).matchesPrefix();
    }
    
   /**
    * Returns a targetless matcher.
    * Don't forget to supply a target.
    */
    public Matcher matcher(){
       return new Matcher(this);
    }
    
   /**
    * Returns a matcher for a specified string.
    */
    public Matcher matcher(String s){
       Matcher m=new Matcher(this);
       m.setTarget(s);
       return m;
    }
       
   /**
    * Returns a matcher for a specified region.
    */
    public Matcher matcher(char[] data,int start,int end){
       Matcher m=new Matcher(this);
       m.setTarget(data,start,end);
       return m;
    }
       
   /**
    * Returns a matcher for a match result (in a performance-friendly way).
    * <code>groupId</code> parameter specifies which group is a target.
    * @param groupId which group is a target; either positive integer(group id), or one of MatchResult.MATCH,MatchResult.PREFIX,MatchResult.SUFFIX,MatchResult.TARGET.
    */
    public Matcher matcher(MatchResult res,int groupId){
       Matcher m=new Matcher(this);
       if(res instanceof Matcher){
          m.setTarget((Matcher)res,groupId);
       }
       else{
          m.setTarget(res.targetChars(),res.start(groupId)+res.targetStart(),res.length(groupId));
       }
       return m;
    }
       
   /**
    * Just as above, yet with symbolic group name.
    * @exception NullPointerException if there is no group with such name
    */
    public Matcher matcher(MatchResult res,String groupName){
       Integer id=res.pattern().groupId(groupName);
       if(id==null) throw new IllegalArgumentException("group not found:"+groupName);
       int group=id.intValue();
       return matcher(res,group);
    }
       
   /**
    * Returns a matcher taking a text stream as target.
    * <b>Note that this is not a true POSIX-style stream matching</b>, i.e. the whole length of the text is preliminary read and stored in a char array.
    * @param text a text stream
    * @param len the length to read from a stream; if <code>len</code> is <code>-1</code>, the whole stream is read in.
    * @exception IOException indicates an IO problem
    * @exception OutOfMemoryException if a stream is too lengthy
    */
    public Matcher matcher(Reader text,int length)throws IOException{
       Matcher m=new Matcher(this);
       m.setTarget(text,length);
       return m;
    }
    
   /**
    * Returns a replacer of a pattern by specified perl-like expression.
    * Such replacer will substitute all occurences of a pattern by an evaluated expression
    * ("$&" and "$0" will substitute by the whole match, "$1" will substitute by group#1, etc).
    * Example:<pre>
    * String text="The quick brown fox jumped over the lazy dog";
    * Pattern word=new Pattern("\\w+");
    * System.out.println(word.replacer("[$&]").replace(text));
    * //prints "[The] [quick] [brown] [fox] [jumped] [over] [the] [lazy] [dog]"
    * Pattern swap=new Pattern("(fox|dog)(.*?)(fox|dog)");
    * System.out.println(swap.replacer("$3$2$1").replace(text));
    * //prints "The quick brown dog jumped over the lazy fox"
    * Pattern scramble=new Pattern("(\\w+)(.*?)(\\w+)");
    * System.out.println(scramble.replacer("$3$2$1").replace(text));
    * //prints "quick The fox brown over jumped lazy the dog"
    * </pre>
    * @param expr a perl-like expression, the "$&" and "${&}" standing for whole match, the "$N" and "${N}" standing for group#N, and "${Foo}" standing for named group Foo.
    * @see Replacer
    */
    public Replacer replacer(String expr){
       return new Replacer(this,expr);
    }
    
   /**
    * Returns a replacer will substitute all occurences of a pattern 
    * through applying a user-defined substitution model.
    * @param model a Substitution object which is in charge for match substitution
    * @see Replacer
    */
    public Replacer replacer(Substitution model){
       return new Replacer(this,model);
    }
    
   /**
    * Tokenizes a text by an occurences of the pattern.
    * Note that a series of adjacent matches are regarded as a single separator.
    * The same as new RETokenizer(Pattern,String);
    * @see RETokenizer 
    * @see RETokenizer#RETokenizer(jregex.Pattern,java.lang.String)
    * 
    */
    public RETokenizer tokenizer(String text){
       return new RETokenizer(this,text);
    }
    
   /**
    * Tokenizes a specified region by an occurences of the pattern.
    * Note that a series of adjacent matches are regarded as a single separator.
    * The same as new RETokenizer(Pattern,char[],int,int);
    * @see RETokenizer 
    * @see RETokenizer#RETokenizer(jregex.Pattern,char[],int,int)
    */
    public RETokenizer tokenizer(char[] data,int off,int len){
       return new RETokenizer(this,data,off,len);
    }
    
   /**
    * Tokenizes a specified region by an occurences of the pattern.
    * Note that a series of adjacent matches are regarded as a single separator.
    * The same as new RETokenizer(Pattern,Reader,int);
    * @see RETokenizer 
    * @see RETokenizer#RETokenizer(jregex.Pattern,java.io.Reader,int)
    */
    public RETokenizer tokenizer(Reader in,int length) throws IOException{
       return new RETokenizer(this,in,length);
    }
    
    public String toString(){
       return stringRepr;
    }
    
   /**
    * Returns a less or more readable representation of a bytecode for the pattern.
    */
    public String toString_d(){
       return root.toStringAll();
    }
    
    static int parseFlags(String flags)throws PatternSyntaxException{
       boolean enable=true;
       int len=flags.length();
       int result=DEFAULT;
       for(int i=0;i<len;i++){
          char c=flags.charAt(i);
          switch(c){
             case '+':
                enable=true;
                break;
             case '-':
                enable=false;
                break;
             default:
                int flag=getFlag(c);
                if(enable) result|=flag;
                else result&=(~flag);
          }
       }
       return result;
    }
    
    static int parseFlags(char[] data,int start,int len)throws PatternSyntaxException{
       boolean enable=true;
       int result=DEFAULT;
       for(int i=0;i<len;i++){
          char c=data[start+i];
          switch(c){
             case '+':
                enable=true;
                break;
             case '-':
                enable=false;
                break;
             default:
                int flag=getFlag(c);
                if(enable) result|=flag;
                else result&=(~flag);
          }
       }
       return result;
    }
    
    private static int getFlag(char c)throws PatternSyntaxException{
       switch(c){
          case 'i':
             return IGNORE_CASE;
          case 'm':
             return MULTILINE|DOTALL;
             //         case 's':
             //            return DOTALL;
          case 'x':
             return IGNORE_SPACES;
             //         case 'u':
             //            return UNICODE;
             //         case 'X':
             //            return XML_SCHEMA;
       }
       throw new PatternSyntaxException("unknown flag: "+c);
    }
 }
diff --git a/src/org/jruby/RubyFile.java b/src/org/jruby/RubyFile.java
index 5e8607f670..3db0e264c1 100644
--- a/src/org/jruby/RubyFile.java
+++ b/src/org/jruby/RubyFile.java
@@ -1,1132 +1,1150 @@
 /*
  ***** BEGIN LICENSE BLOCK *****
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
  * Copyright (C) 2002 Benoit Cerrina <b.cerrina@wanadoo.fr>
  * Copyright (C) 2002-2004 Jan Arne Petersen <jpetersen@uni-bonn.de>
  * Copyright (C) 2002-2004 Anders Bengtsson <ndrsbngtssn@yahoo.se>
  * Copyright (C) 2003 Joey Gibson <joey@joeygibson.com>
  * Copyright (C) 2004-2007 Thomas E Enebo <enebo@acm.org>
  * Copyright (C) 2004-2007 Charles O Nutter <headius@headius.com>
  * Copyright (C) 2004 Stefan Matthias Aust <sma@3plus4.de>
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
 
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.Reader;
 import java.nio.channels.FileChannel;
 import java.nio.channels.FileLock;
 import org.jruby.anno.JRubyMethod;
 
 import org.jruby.runtime.Arity;
 import org.jruby.runtime.Block;
 import org.jruby.runtime.CallbackFactory;
 import org.jruby.runtime.MethodIndex;
 import org.jruby.runtime.ObjectAllocator;
 import org.jruby.runtime.ThreadContext;
 import org.jruby.runtime.Visibility;
 import org.jruby.runtime.builtin.IRubyObject;
 import org.jruby.util.ByteList;
 import org.jruby.util.DirectoryAsFileException;
 import org.jruby.util.IOHandler;
 import org.jruby.util.IOHandlerNull;
 import org.jruby.util.IOHandlerSeekable;
 import org.jruby.util.IOHandlerUnseekable;
 import org.jruby.util.IOModes;
 import org.jruby.util.JRubyFile;
 import org.jruby.util.ShellLauncher;
 import org.jruby.util.IOHandler.InvalidValueException;
 
 /**
  * Ruby File class equivalent in java.
  **/
 public class RubyFile extends RubyIO {
     public static final int LOCK_SH = 1;
     public static final int LOCK_EX = 2;
     public static final int LOCK_NB = 4;
     public static final int LOCK_UN = 8;
 
     private static final int FNM_NOESCAPE = 1;
     private static final int FNM_PATHNAME = 2;
     private static final int FNM_DOTMATCH = 4;
     private static final int FNM_CASEFOLD = 8;
 
     static final boolean IS_WINDOWS;
     static {
         String osname = System.getProperty("os.name");
         IS_WINDOWS = osname != null && osname.toLowerCase().indexOf("windows") != -1;
     }
 
     protected String path;
     private FileLock currentLock;
     
     public RubyFile(Ruby runtime, RubyClass type) {
         super(runtime, type);
     }
     
     public RubyFile(Ruby runtime, String path) {
         this(runtime, path, open(runtime, path));
     }
     
     // use static function because constructor call must be first statement in above constructor
     private static InputStream open(Ruby runtime, String path) {
         try {
             return new FileInputStream(path);
         } catch (FileNotFoundException e) {
             throw runtime.newIOError(e.getMessage());
         }
     }
     
     // XXX This constructor is a hack to implement the __END__ syntax.
     //     Converting a reader back into an InputStream doesn't generally work.
     public RubyFile(Ruby runtime, String path, final Reader reader) {
         this(runtime, path, new InputStream() {
             public int read() throws IOException {
                 return reader.read();
             }
         });
     }
     
     public RubyFile(Ruby runtime, String path, InputStream in) {
         super(runtime, runtime.getFile());
         this.path = path;
         try {
             this.handler = new IOHandlerUnseekable(runtime, in, null);
         } catch (IOException e) {
             throw runtime.newIOError(e.getMessage());
         }
         this.modes = handler.getModes();
         registerIOHandler(handler);
     }
 
     private static ObjectAllocator FILE_ALLOCATOR = new ObjectAllocator() {
         public IRubyObject allocate(Ruby runtime, RubyClass klass) {
             RubyFile instance = new RubyFile(runtime, klass);
             
             instance.setMetaClass(klass);
             
             return instance;
         }
     };
     
     public static RubyClass createFileClass(Ruby runtime) {
         RubyClass fileClass = runtime.defineClass("File", runtime.getIO(), FILE_ALLOCATOR);
         runtime.setFile(fileClass);
         CallbackFactory callbackFactory = runtime.callbackFactory(RubyFile.class);   
         RubyClass fileMetaClass = fileClass.getMetaClass();
         RubyString separator = runtime.newString("/");
         
         fileClass.kindOf = new RubyModule.KindOf() {
                 public boolean isKindOf(IRubyObject obj, RubyModule type) {
                     return obj instanceof RubyFile;
                 }
             };
 
         separator.freeze();
         fileClass.defineConstant("SEPARATOR", separator);
         fileClass.defineConstant("Separator", separator);
         
         if (File.separatorChar == '\\') {
             RubyString altSeparator = runtime.newString("\\");
             altSeparator.freeze();
             fileClass.defineConstant("ALT_SEPARATOR", altSeparator);
         } else {
             fileClass.defineConstant("ALT_SEPARATOR", runtime.getNil());
         }
         
         RubyString pathSeparator = runtime.newString(File.pathSeparator);
         pathSeparator.freeze();
         fileClass.defineConstant("PATH_SEPARATOR", pathSeparator);
         
         // TODO: These were missing, so we're not handling them elsewhere?
         // FIXME: The old value, 32786, didn't match what IOModes expected, so I reference
         // the constant here. THIS MAY NOT BE THE CORRECT VALUE.
         fileClass.fastSetConstant("BINARY", runtime.newFixnum(IOModes.BINARY));
         fileClass.fastSetConstant("FNM_NOESCAPE", runtime.newFixnum(FNM_NOESCAPE));
         fileClass.fastSetConstant("FNM_CASEFOLD", runtime.newFixnum(FNM_CASEFOLD));
         fileClass.fastSetConstant("FNM_SYSCASE", runtime.newFixnum(FNM_CASEFOLD));
         fileClass.fastSetConstant("FNM_DOTMATCH", runtime.newFixnum(FNM_DOTMATCH));
         fileClass.fastSetConstant("FNM_PATHNAME", runtime.newFixnum(FNM_PATHNAME));
         
         // Create constants for open flags
         fileClass.fastSetConstant("RDONLY", runtime.newFixnum(IOModes.RDONLY));
         fileClass.fastSetConstant("WRONLY", runtime.newFixnum(IOModes.WRONLY));
         fileClass.fastSetConstant("RDWR", runtime.newFixnum(IOModes.RDWR));
         fileClass.fastSetConstant("CREAT", runtime.newFixnum(IOModes.CREAT));
         fileClass.fastSetConstant("EXCL", runtime.newFixnum(IOModes.EXCL));
         fileClass.fastSetConstant("NOCTTY", runtime.newFixnum(IOModes.NOCTTY));
         fileClass.fastSetConstant("TRUNC", runtime.newFixnum(IOModes.TRUNC));
         fileClass.fastSetConstant("APPEND", runtime.newFixnum(IOModes.APPEND));
         fileClass.fastSetConstant("NONBLOCK", runtime.newFixnum(IOModes.NONBLOCK));
         
         // Create constants for flock
         fileClass.fastSetConstant("LOCK_SH", runtime.newFixnum(RubyFile.LOCK_SH));
         fileClass.fastSetConstant("LOCK_EX", runtime.newFixnum(RubyFile.LOCK_EX));
         fileClass.fastSetConstant("LOCK_NB", runtime.newFixnum(RubyFile.LOCK_NB));
         fileClass.fastSetConstant("LOCK_UN", runtime.newFixnum(RubyFile.LOCK_UN));
         
         // Create Constants class
         RubyModule constants = fileClass.defineModuleUnder("Constants");
         
         // TODO: These were missing, so we're not handling them elsewhere?
         constants.fastSetConstant("BINARY", runtime.newFixnum(32768));
         constants.fastSetConstant("FNM_NOESCAPE", runtime.newFixnum(1));
         constants.fastSetConstant("FNM_CASEFOLD", runtime.newFixnum(8));
         constants.fastSetConstant("FNM_DOTMATCH", runtime.newFixnum(4));
         constants.fastSetConstant("FNM_PATHNAME", runtime.newFixnum(2));
         
         // Create constants for open flags
         constants.fastSetConstant("RDONLY", runtime.newFixnum(IOModes.RDONLY));
         constants.fastSetConstant("WRONLY", runtime.newFixnum(IOModes.WRONLY));
         constants.fastSetConstant("RDWR", runtime.newFixnum(IOModes.RDWR));
         constants.fastSetConstant("CREAT", runtime.newFixnum(IOModes.CREAT));
         constants.fastSetConstant("EXCL", runtime.newFixnum(IOModes.EXCL));
         constants.fastSetConstant("NOCTTY", runtime.newFixnum(IOModes.NOCTTY));
         constants.fastSetConstant("TRUNC", runtime.newFixnum(IOModes.TRUNC));
         constants.fastSetConstant("APPEND", runtime.newFixnum(IOModes.APPEND));
         constants.fastSetConstant("NONBLOCK", runtime.newFixnum(IOModes.NONBLOCK));
         
         // Create constants for flock
         constants.fastSetConstant("LOCK_SH", runtime.newFixnum(RubyFile.LOCK_SH));
         constants.fastSetConstant("LOCK_EX", runtime.newFixnum(RubyFile.LOCK_EX));
         constants.fastSetConstant("LOCK_NB", runtime.newFixnum(RubyFile.LOCK_NB));
         constants.fastSetConstant("LOCK_UN", runtime.newFixnum(RubyFile.LOCK_UN));
         
         // TODO Singleton methods: blockdev?, chardev?, directory?
         // TODO Singleton methods: executable?, executable_real?,
         // TODO Singleton methods: ftype, grpowned?, lchmod, lchown, link, owned?
         // TODO Singleton methods: pipe?, readlink, setgid?, setuid?, socket?,
         // TODO Singleton methods: stat, sticky?, symlink?, umask
         
         runtime.getFileTest().extend_object(fileClass);
         
         // atime and ctime are implemented like mtime, since we don't have an atime API in Java
         
         // TODO: Define instance methods: lchmod, lchown, lstat
         // atime and ctime are implemented like mtime, since we don't have an atime API in Java
         
         fileClass.defineAnnotatedMethods(RubyFile.class);
         fileClass.dispatcher = callbackFactory.createDispatcher(fileClass);
         
         return fileClass;
     }
     
     public void openInternal(String newPath, IOModes newModes) {
         this.path = newPath;
         this.modes = newModes;
         
         try {
             if (newPath.equals("/dev/null")) {
                 handler = new IOHandlerNull(getRuntime(), newModes);
             } else {
                 handler = new IOHandlerSeekable(getRuntime(), newPath, newModes);
             }
             
             registerIOHandler(handler);
         } catch (InvalidValueException e) {
         	throw getRuntime().newErrnoEINVALError();
         } catch (DirectoryAsFileException e) {
             throw getRuntime().newErrnoEISDirError();
         } catch (FileNotFoundException e) {
         	throw getRuntime().newErrnoENOENTError();
         } catch (IOException e) {
             throw getRuntime().newIOError(e.getMessage());
 		}
     }
     
     @JRubyMethod
     public IRubyObject close() {
         // Make sure any existing lock is released before we try and close the file
         if (currentLock != null) {
             try {
                 currentLock.release();
             } catch (IOException e) {
                 throw getRuntime().newIOError(e.getMessage());
             }
         }
         return super.close();
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject flock(IRubyObject lockingConstant) {
         FileChannel fileChannel = handler.getFileChannel();
         int lockMode = RubyNumeric.num2int(lockingConstant);
 
         try {
             switch (lockMode) {
                 case LOCK_UN:
                 case LOCK_UN | LOCK_NB:
                     if (currentLock != null) {
                         currentLock.release();
                         currentLock = null;
 
                         return getRuntime().newFixnum(0);
                     }
                     break;
                 case LOCK_EX:
                     if (currentLock != null) {
                         currentLock.release();
                         currentLock = null;
                     }
                     currentLock = fileChannel.lock();
                     if (currentLock != null) {
                         return getRuntime().newFixnum(0);
                     }
 
                     break;
                 case LOCK_EX | LOCK_NB:
                     if (currentLock != null) {
                         currentLock.release();
                         currentLock = null;
                     }
                     currentLock = fileChannel.tryLock();
                     if (currentLock != null) {
                         return getRuntime().newFixnum(0);
                     }
 
                     break;
                 case LOCK_SH:
                     if (currentLock != null) {
                         currentLock.release();
                         currentLock = null;
                     }
 
                     currentLock = fileChannel.lock(0L, Long.MAX_VALUE, true);
                     if (currentLock != null) {
                         return getRuntime().newFixnum(0);
                     }
 
                     break;
                 case LOCK_SH | LOCK_NB:
                     if (currentLock != null) {
                         currentLock.release();
                         currentLock = null;
                     }
 
                     currentLock = fileChannel.tryLock(0L, Long.MAX_VALUE, true);
                     if (currentLock != null) {
                         return getRuntime().newFixnum(0);
                     }
 
                     break;
                 default:
             }
         } catch (IOException ioe) {
             if (getRuntime().getDebug().isTrue()) {
                 ioe.printStackTrace(System.err);
             }
             // Return false here
         } catch (java.nio.channels.OverlappingFileLockException ioe) {
             if (getRuntime().getDebug().isTrue()) {
                 ioe.printStackTrace(System.err);
             }
             // Return false here
         }
 
         return getRuntime().getFalse();
     }
 
     @JRubyMethod(required = 1, optional = 2, frame = true, visibility = Visibility.PRIVATE)
     public IRubyObject initialize(IRubyObject[] args, Block block) {
         if (args.length == 0) {
             throw getRuntime().newArgumentError(0, 1);
         }
         else if (args.length < 3) {
             IRubyObject fd = args[0].convertToTypeWithCheck(getRuntime().getFixnum(), MethodIndex.TO_INT, "to_int");
             if (!fd.isNil()) {
                 args[0] = fd;
                 return super.initialize(args, block);
             }
         }
 
         getRuntime().checkSafeString(args[0]);
         path = args[0].toString();
         modes = args.length > 1 ? getModes(getRuntime(), args[1]) : new IOModes(getRuntime(), IOModes.RDONLY);
 
         // One of the few places where handler may be null.
         // If handler is not null, it indicates that this object
         // is being reused.
         if (handler != null) {
             close();
         }
         openInternal(path, modes);
 
         if (block.isGiven()) {
             // getRuby().getRuntime().warn("File::new does not take block; use File::open instead");
         }
         return this;
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject chmod(IRubyObject arg) {
         RubyInteger mode = arg.convertToInteger();
 
         if (!new File(path).exists()) {
             throw getRuntime().newErrnoENOENTError("No such file or directory - " + path);
         }
 
         int result = Ruby.getPosix().chmod(path, (int) mode.getLongValue());
 
         return getRuntime().newFixnum(result);
     }
 
     @JRubyMethod(required = 2)
     public IRubyObject chown(IRubyObject arg, IRubyObject arg2) {
         RubyInteger owner = arg.convertToInteger();
         RubyInteger group = arg2.convertToInteger();
         if (!new File(path).exists()) {
             throw getRuntime().newErrnoENOENTError("No such file or directory - " + path);
         }
 
         int result = Ruby.getPosix().chown(path, (int) owner.getLongValue(), (int) group.getLongValue());
 
         return RubyFixnum.newFixnum(getRuntime(), result);
     }
 
-    @JRubyMethod(name = {"mtime", "atime", "ctime"})
-    public IRubyObject mtime() {
+    @JRubyMethod(name = {"atime", "ctime"})
+    public IRubyObject atime() {
         return getRuntime().newTime(JRubyFile.create(getRuntime().getCurrentDirectory(), this.path).getParentFile().lastModified());
     }
 
+    @JRubyMethod(name = {"mtime"})
+    public IRubyObject mtime() {
+        return getRuntime().newTime(JRubyFile.create(getRuntime().getCurrentDirectory(), this.path).lastModified());
+    }
+
     @JRubyMethod
     public RubyString path() {
         return getRuntime().newString(path);
     }
 
     @JRubyMethod
     public IRubyObject stat() {
         return getRuntime().newRubyFileStat(path);
     }
 
     @JRubyMethod(required = 1)
     public IRubyObject truncate(IRubyObject arg) {
         RubyInteger newLength = arg.convertToInteger();
         if (newLength.getLongValue() < 0) {
             throw getRuntime().newErrnoEINVALError("invalid argument: " + path);
         }
         try {
             handler.truncate(newLength.getLongValue());
         } catch (IOHandler.PipeException e) {
             throw getRuntime().newErrnoESPIPEError();
         } catch (IOException e) {
             // Should we do anything?
         }
 
         return RubyFixnum.zero(getRuntime());
     }
 
     public String toString() {
         return "RubyFile(" + path + ", " + modes + ", " + fileno + ")";
     }
 
     // TODO: This is also defined in the MetaClass too...Consolidate somewhere.
     private static IOModes getModes(Ruby runtime, IRubyObject object) {
         if (object instanceof RubyString) {
             return new IOModes(runtime, ((RubyString) object).toString());
         } else if (object instanceof RubyFixnum) {
             return new IOModes(runtime, ((RubyFixnum) object).getLongValue());
         }
 
         throw runtime.newTypeError("Invalid type for modes");
     }
 
     @JRubyMethod
     public IRubyObject inspect() {
         StringBuffer val = new StringBuffer();
         val.append("#<File:").append(path);
         if(!isOpen()) {
             val.append(" (closed)");
         }
         val.append(">");
         return getRuntime().newString(val.toString());
     }
     
     /* File class methods */
     
     @JRubyMethod(required = 1, optional = 1, meta = true)
     public static IRubyObject basename(IRubyObject recv, IRubyObject[] args) {
         Arity.checkArgumentCount(recv.getRuntime(), args, 1, 2);
         
         String name = RubyString.stringValue(args[0]).toString();
 
         // MRI-compatible basename handling for windows drive letter paths
         if (IS_WINDOWS) {
             if (name.length() > 1 && name.charAt(1) == ':' && Character.isLetter(name.charAt(0))) {
                 switch (name.length()) {
                 case 2:
                     return recv.getRuntime().newString("").infectBy(args[0]);
                 case 3:
                     return recv.getRuntime().newString(name.substring(2)).infectBy(args[0]);
                 default:
                     switch (name.charAt(2)) {
                     case '/':
                     case '\\':
                         break;
                     default:
                         // strip c: away from relative-pathed name
                         name = name.substring(2);
                         break;
                     }
                     break;
                 }
             }
         }
 
         while (name.length() > 1 && name.charAt(name.length() - 1) == '/') {
             name = name.substring(0, name.length() - 1);
         }
         
         // Paths which end in "/" or "\\" must be stripped off.
         int slashCount = 0;
         int length = name.length();
         for (int i = length - 1; i >= 0; i--) {
             char c = name.charAt(i);
             if (c != '/' && c != '\\') {
                 break;
             }
             slashCount++;
         }
         if (slashCount > 0 && length > 1) {
             name = name.substring(0, name.length() - slashCount);
         }
         
         int index = name.lastIndexOf('/');
         if (index == -1) {
             // XXX actually only on windows...
             index = name.lastIndexOf('\\');
         }
         
         if (!name.equals("/") && index != -1) {
             name = name.substring(index + 1);
         }
         
         if (args.length == 2) {
             String ext = RubyString.stringValue(args[1]).toString();
             if (".*".equals(ext)) {
                 index = name.lastIndexOf('.');
                 if (index > 0) {  // -1 no match; 0 it is dot file not extension
                     name = name.substring(0, index);
                 }
             } else if (name.endsWith(ext)) {
                 name = name.substring(0, name.length() - ext.length());
             }
         }
         return recv.getRuntime().newString(name).infectBy(args[0]);
     }
     
     @JRubyMethod(required = 2, rest = true, meta = true)
     public static IRubyObject chmod(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         Arity.checkArgumentCount(runtime, args, 2, -1);
         
         int count = 0;
         RubyInteger mode = args[0].convertToInteger();
         for (int i = 1; i < args.length; i++) {
             IRubyObject filename = args[i];
             
             if (!RubyFileTest.exist_p(filename, filename.convertToString()).isTrue()) {
                 throw runtime.newErrnoENOENTError("No such file or directory - " + filename);
             }
             
             boolean result = 0 == Ruby.getPosix().chmod(filename.toString(), (int)mode.getLongValue());
             if (result) {
                 count++;
             }
         }
         
         return runtime.newFixnum(count);
     }
     
     @JRubyMethod(required = 3, rest = true, meta = true)
     public static IRubyObject chown(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         Arity.checkArgumentCount(runtime, args, 3, -1);
         
         int count = 0;
         RubyInteger owner = args[0].convertToInteger();
         RubyInteger group = args[1].convertToInteger();
         for (int i = 2; i < args.length; i++) {
             IRubyObject filename = args[i];
             
             if (!RubyFileTest.exist_p(filename, filename.convertToString()).isTrue()) {
                 throw runtime.newErrnoENOENTError("No such file or directory - " + filename);
             }
             
             boolean result = 0 == Ruby.getPosix().chown(filename.toString(), (int)owner.getLongValue(), (int)group.getLongValue());
             if (result) {
                 count++;
             }
         }
         
         return runtime.newFixnum(count);
     }
     
     @JRubyMethod(required = 1, meta = true)
     public static IRubyObject dirname(IRubyObject recv, IRubyObject arg) {
         RubyString filename = RubyString.stringValue(arg);
         String jfilename = filename.toString();
         String name = jfilename.replace('\\', '/');
         boolean trimmedSlashes = false;
 
         while (name.length() > 1 && name.charAt(name.length() - 1) == '/') {
             trimmedSlashes = true;
             name = name.substring(0, name.length() - 1);
         }
 
         String result;
         if (IS_WINDOWS && name.length() == 2 &&
                 isWindowsDriveLetter(name.charAt(0)) && name.charAt(1) == ':') {
             // C:\ is returned unchanged (after slash trimming)
             if (trimmedSlashes) {
                 result = jfilename.substring(0, 3);
             } else {
                 result = jfilename.substring(0, 2) + '.';
             }
         } else {
             //TODO deal with UNC names
             int index = name.lastIndexOf('/');
             if (index == -1) return recv.getRuntime().newString(".");
             if (index == 0) return recv.getRuntime().newString("/");
 
             // Include additional path separator (e.g. C:\myfile.txt becomes C:\, not C:)
             if (IS_WINDOWS && index == 2 && 
                     isWindowsDriveLetter(name.charAt(0)) && name.charAt(1) == ':') {
                 index++;
             }
             
             result = jfilename.substring(0, index);
          }
 
          return recv.getRuntime().newString(result).infectBy(filename);
 
     }
 
     private static boolean isWindowsDriveLetter(char c) {
         return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
     }
 
     
     /**
      * Returns the extension name of the file. An empty string is returned if 
      * the filename (not the entire path) starts or ends with a dot.
      * @param recv
      * @param arg Path to get extension name of
      * @return Extension, including the dot, or an empty string
      */
     @JRubyMethod(required = 1, meta = true)
     public static IRubyObject extname(IRubyObject recv, IRubyObject arg) {
         IRubyObject baseFilename = basename(recv, new IRubyObject[] { arg });
         String filename = RubyString.stringValue(baseFilename).toString();
         String result = "";
         
         int dotIndex = filename.lastIndexOf(".");
         if (dotIndex > 0  && dotIndex != (filename.length() - 1)) {
             // Dot is not at beginning and not at end of filename. 
             result = filename.substring(dotIndex);
         }
 
         return recv.getRuntime().newString(result);
     }
     
     /**
      * Converts a pathname to an absolute pathname. Relative paths are 
      * referenced from the current working directory of the process unless 
      * a second argument is given, in which case it will be used as the 
      * starting point. If the second argument is also relative, it will 
      * first be converted to an absolute pathname.
      * @param recv
      * @param args 
      * @return Resulting absolute path as a String
      */
     @JRubyMethod(required = 1, optional = 2, meta = true)
     public static IRubyObject expand_path(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         Arity.checkArgumentCount(runtime, args, 1, 2);
         
         String relativePath = RubyString.stringValue(args[0]).toString();
         String cwd = null;
         
         // Handle ~user paths 
         relativePath = expandUserPath(recv, relativePath);
         
         // If there's a second argument, it's the path to which the first 
         // argument is relative.
         if (args.length == 2 && !args[1].isNil()) {
             
             String cwdArg = RubyString.stringValue(args[1]).toString();
             
             // Handle ~user paths.
             cwd = expandUserPath(recv, cwdArg);
             
             // If the path isn't absolute, then prepend the current working
             // directory to the path.
             if ( cwd.charAt(0) != '/' ) {
                 cwd = JRubyFile.create(runtime.getCurrentDirectory(), cwd)
                     .getAbsolutePath();
             }
             
         } else {
             // If there's no second argument, simply use the working directory 
             // of the runtime.
             cwd = runtime.getCurrentDirectory();
         }
         
         // Something wrong we don't know the cwd...
         // TODO: Is this behavior really desirable? /mov
         if (cwd == null) return runtime.getNil();
         
         /* The counting of slashes that follows is simply a way to adhere to 
          * Ruby's UNC (or something) compatibility. When Ruby's expand_path is 
          * called with "//foo//bar" it will return "//foo/bar". JRuby uses 
          * java.io.File, and hence returns "/foo/bar". In order to retain 
          * java.io.File in the lower layers and provide full Ruby 
          * compatibility, the number of extra slashes must be counted and 
          * prepended to the result.
          */ 
         
         // Find out which string to check.
         String padSlashes = "";
         if (relativePath.length() > 0 && relativePath.charAt(0) == '/') {
             padSlashes = countSlashes(relativePath);
         } else if (cwd.length() > 0 && cwd.charAt(0) == '/') {
             padSlashes = countSlashes(cwd);
         }
         
         JRubyFile path = JRubyFile.create(cwd, relativePath);
 
         return runtime.newString(padSlashes + canonicalize(path.getAbsolutePath()));
     }
     
     /**
      * This method checks a path, and if it starts with ~, then it expands 
      * the path to the absolute path of the user's home directory. If the 
      * string does not begin with ~, then the string is simply retuned 
      * unaltered.
      * @param recv
      * @param path Path to check
      * @return Expanded path
      */
     private static String expandUserPath( IRubyObject recv, String path ) {
         
         int pathLength = path.length();
 
         if (pathLength >= 1 && path.charAt(0) == '~') {
             // Enebo : Should ~frogger\\foo work (it doesnt in linux ruby)?
             int userEnd = path.indexOf('/');
             
             if (userEnd == -1) {
                 if (pathLength == 1) {
                     // Single '~' as whole path to expand
                     path = RubyDir.getHomeDirectoryPath(recv).toString();
                 } else {
                     // No directory delimeter.  Rest of string is username
                     userEnd = pathLength;
                 }
             }
             
             if (userEnd == 1) {
                 // '~/...' as path to expand
                 path = RubyDir.getHomeDirectoryPath(recv).toString() +
                         path.substring(1);
             } else if (userEnd > 1){
                 // '~user/...' as path to expand
                 String user = path.substring(1, userEnd);
                 IRubyObject dir = RubyDir.getHomeDirectoryPath(recv, user);
                 
                 if (dir.isNil()) {
                     Ruby runtime = recv.getRuntime();
                     throw runtime.newArgumentError("user " + user + " does not exist");
                 }
                 
                 path = "" + dir +
                         (pathLength == userEnd ? "" : path.substring(userEnd));
             }
         }
         return path;
     }
     
     /**
      * Returns a string consisting of <code>n-1</code> slashes, where 
      * <code>n</code> is the number of slashes at the beginning of the input 
      * string.
      * @param stringToCheck
      * @return
      */
     private static String countSlashes( String stringToCheck ) {
         
         // Count number of extra slashes in the beginning of the string.
         int slashCount = 0;
         for (int i = 0; i < stringToCheck.length(); i++) {
             if (stringToCheck.charAt(i) == '/') {
                 slashCount++;
             } else {
                 break;
             }
         }
 
         // If there are N slashes, then we want N-1.
         if (slashCount > 0) {
             slashCount--;
         }
         
         // Prepare a string with the same number of redundant slashes so that 
         // we easily can prepend it to the result.
         byte[] slashes = new byte[slashCount];
         for (int i = 0; i < slashCount; i++) {
             slashes[i] = '/';
         }
         return new String(slashes); 
         
     }
 
     private static String canonicalize(String path) {
         return canonicalize(null, path);
     }
 
     private static String canonicalize(String canonicalPath, String remaining) {
 
         if (remaining == null) return canonicalPath;
 
         String child;
         int slash = remaining.indexOf('/');
         if (slash == -1) {
             child = remaining;
             remaining = null;
         } else {
             child = remaining.substring(0, slash);
             remaining = remaining.substring(slash + 1);
         }
 
         if (child.equals(".")) {
             // skip it
             if (canonicalPath != null && canonicalPath.length() == 0 ) canonicalPath += "/";
         } else if (child.equals("..")) {
             if (canonicalPath == null) throw new IllegalArgumentException("Cannot have .. at the start of an absolute path");
             int lastDir = canonicalPath.lastIndexOf('/');
             if (lastDir == -1) {
                 canonicalPath = "";
             } else {
                 canonicalPath = canonicalPath.substring(0, lastDir);
             }
         } else if (canonicalPath == null) {
             canonicalPath = child;
         } else {
             canonicalPath += "/" + child;
         }
 
         return canonicalize(canonicalPath, remaining);
     }
 
     /**
      * Returns true if path matches against pattern The pattern is not a regular expression;
      * instead it follows rules similar to shell filename globbing. It may contain the following
      * metacharacters:
      *   *:  Glob - match any sequence chars (re: .*).  If like begins with '.' then it doesn't.
      *   ?:  Matches a single char (re: .).
      *   [set]:  Matches a single char in a set (re: [...]).
      *
      */
     @JRubyMethod(name = {"fnmatch", "fnmatch?"}, required = 2, optional = 1, meta = true)
     public static IRubyObject fnmatch(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         int flags;
         if (Arity.checkArgumentCount(runtime, args, 2, 3) == 3) {
             flags = RubyNumeric.num2int(args[2]);
         } else {
             flags = 0;
         }
         
         ByteList pattern = args[0].convertToString().getByteList();
         ByteList path = args[1].convertToString().getByteList();
         if (org.jruby.util.Dir.fnmatch(pattern.bytes, pattern.begin, pattern.realSize , path.bytes, path.begin, path.realSize, flags) == 0) {
             return runtime.getTrue();
         }
         return runtime.getFalse();
     }
 
     /*
      * Fixme:  This does not have exact same semantics as RubyArray.join, but they
      * probably could be consolidated (perhaps as join(args[], sep, doChomp)).
      */
     @JRubyMethod(rest = true, meta = true)
     public static RubyString join(IRubyObject recv, IRubyObject[] args) {
         boolean isTainted = false;
         StringBuffer buffer = new StringBuffer();
         
         for (int i = 0; i < args.length; i++) {
             if (args[i].isTaint()) {
                 isTainted = true;
             }
             String element;
             if (args[i] instanceof RubyString) {
                 element = args[i].toString();
             } else if (args[i] instanceof RubyArray) {
                 // Fixme: Need infinite recursion check to put [...] and not go into a loop
                 element = join(recv, ((RubyArray) args[i]).toJavaArray()).toString();
             } else {
                 element = args[i].convertToString().toString();
             }
             
             chomp(buffer);
             if (i > 0 && !element.startsWith("/") && !element.startsWith("\\")) {
                 buffer.append("/");
             }
             buffer.append(element);
         }
         
         RubyString fixedStr = RubyString.newString(recv.getRuntime(), buffer.toString());
         fixedStr.setTaint(isTainted);
         return fixedStr;
     }
     
     private static void chomp(StringBuffer buffer) {
         int lastIndex = buffer.length() - 1;
         
         while (lastIndex >= 0 && (buffer.lastIndexOf("/") == lastIndex || buffer.lastIndexOf("\\") == lastIndex)) {
             buffer.setLength(lastIndex);
             lastIndex--;
         }
     }
     
     @JRubyMethod(name = {"lstat", "stat"}, required = 1, meta = true)
     public static IRubyObject lstat(IRubyObject recv, IRubyObject filename) {
         RubyString name = RubyString.stringValue(filename);
         return recv.getRuntime().newRubyFileStat(name.toString());
     }
     
-    @JRubyMethod(name = {"mtime", "atime", "ctime"}, required = 1, meta = true)
+    @JRubyMethod(name = {"atime", "ctime"}, required = 1, meta = true)
+    public static IRubyObject atime(IRubyObject recv, IRubyObject filename) {
+        Ruby runtime = recv.getRuntime();
+        RubyString name = RubyString.stringValue(filename);
+        JRubyFile file = JRubyFile.create(runtime.getCurrentDirectory(), name.toString());
+
+        if (!file.exists()) {
+            throw runtime.newErrnoENOENTError("No such file or directory - " + name.toString());
+        }
+        
+        return runtime.newTime(file.getParentFile().lastModified());
+    }
+    
+    @JRubyMethod(name = {"mtime"}, required = 1, meta = true)
     public static IRubyObject mtime(IRubyObject recv, IRubyObject filename) {
         Ruby runtime = recv.getRuntime();
         RubyString name = RubyString.stringValue(filename);
         JRubyFile file = JRubyFile.create(runtime.getCurrentDirectory(), name.toString());
 
         if (!file.exists()) {
             throw runtime.newErrnoENOENTError("No such file or directory - " + name.toString());
         }
         
         return runtime.newTime(file.lastModified());
     }
     
     @JRubyMethod(required = 1, rest = true, frame = true, meta = true)
     public static IRubyObject open(IRubyObject recv, IRubyObject[] args, Block block) {
         return open(recv, args, true, block);
     }
     
     public static IRubyObject open(IRubyObject recv, IRubyObject[] args, boolean tryToYield, Block block) {
         Ruby runtime = recv.getRuntime();
         Arity.checkArgumentCount(runtime, args, 1, -1);
         ThreadContext tc = runtime.getCurrentContext();
         
         RubyString pathString = RubyString.stringValue(args[0]);
         runtime.checkSafeString(pathString);
         String path = pathString.toString();
         
         IOModes modes =
                 args.length >= 2 ? getModes(runtime, args[1]) : new IOModes(runtime, IOModes.RDONLY);
         RubyFile file = new RubyFile(runtime, (RubyClass) recv);
         
         RubyInteger fileMode =
                 args.length >= 3 ? args[2].convertToInteger() : null;
         
         file.openInternal(path, modes);
         
         if (fileMode != null) {
             chmod(recv, new IRubyObject[] {fileMode, pathString});
         }
         
         if (tryToYield && block.isGiven()) {
             try {
                 return block.yield(tc, file);
             } finally {
                 file.close();
             }
         }
         
         return file;
     }
     
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject rename(IRubyObject recv, IRubyObject oldName, IRubyObject newName) {
         Ruby runtime = recv.getRuntime();
         RubyString oldNameString = RubyString.stringValue(oldName);
         RubyString newNameString = RubyString.stringValue(newName);
         runtime.checkSafeString(oldNameString);
         runtime.checkSafeString(newNameString);
         JRubyFile oldFile = JRubyFile.create(runtime.getCurrentDirectory(), oldNameString.toString());
         JRubyFile newFile = JRubyFile.create(runtime.getCurrentDirectory(), newNameString.toString());
         
         if (!oldFile.exists() || !newFile.getParentFile().exists()) {
             throw runtime.newErrnoENOENTError("No such file or directory - " + oldNameString + " or " + newNameString);
         }
         oldFile.renameTo(JRubyFile.create(runtime.getCurrentDirectory(), newNameString.toString()));
         
         return RubyFixnum.zero(runtime);
     }
     
     @JRubyMethod(name = "size?", required = 1, meta = true)
     public static IRubyObject size_p(IRubyObject recv, IRubyObject filename) {
         long size = 0;
         
         try {
             FileInputStream fis = new FileInputStream(new File(filename.toString()));
             FileChannel chan = fis.getChannel();
             size = chan.size();
             chan.close();
             fis.close();
         } catch (IOException ioe) {
             // missing files or inability to open should just return nil
         }
         
         if (size == 0) return recv.getRuntime().getNil();
         
         return recv.getRuntime().newFixnum(size);
     }
     
     @JRubyMethod(required = 1, meta = true)
     public static RubyArray split(IRubyObject recv, IRubyObject arg) {
         RubyString filename = RubyString.stringValue(arg);
         
         return filename.getRuntime().newArray(dirname(recv, filename),
                 basename(recv, new IRubyObject[] { filename }));
     }
     
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject symlink(IRubyObject recv, IRubyObject from, IRubyObject to) {
         Ruby runtime = recv.getRuntime();
         
         try {
             int result = new ShellLauncher(runtime).runAndWait(new IRubyObject[] {
                 runtime.newString("ln"), runtime.newString("-s"), from, to
             });
             return runtime.newFixnum(result);
         } catch (Exception e) {
             throw runtime.newNotImplementedError("symlinks");
         }
     }
 
     @JRubyMethod(name = "symlink?", required = 1, meta = true)
     public static IRubyObject symlink_p(IRubyObject recv, IRubyObject arg1) {
         Ruby runtime = recv.getRuntime();
         RubyString filename = RubyString.stringValue(arg1);
         
         JRubyFile file = JRubyFile.create(runtime.getCurrentDirectory(), filename.toString());
         
         try {
             // Only way to determine symlink is to compare canonical and absolute files
             // However symlinks in containing path must not produce false positives, so we check that first
             File absoluteParent = file.getAbsoluteFile().getParentFile();
             File canonicalParent = file.getAbsoluteFile().getParentFile().getCanonicalFile();
             
             if (canonicalParent.getAbsolutePath().equals(absoluteParent.getAbsolutePath())) {
                 // parent doesn't change when canonicalized, compare absolute and canonical file directly
                 return file.getAbsolutePath().equals(file.getCanonicalPath()) ? runtime.getFalse() : runtime.getTrue();
             }
             
             // directory itself has symlinks (canonical != absolute), so build new path with canonical parent and compare
             file = JRubyFile.create(runtime.getCurrentDirectory(), canonicalParent.getAbsolutePath() + "/" + file.getName());
             return file.getAbsolutePath().equals(file.getCanonicalPath()) ? runtime.getFalse() : runtime.getTrue();
         } catch (IOException ioe) {
             // problem canonicalizing the file; nothing we can do but return false
             return runtime.getFalse();
         }
     }
     
     // Can we produce IOError which bypasses a close?
     @JRubyMethod(required = 2, meta = true)
     public static IRubyObject truncate(IRubyObject recv, IRubyObject arg1, IRubyObject arg2) {
         Ruby runtime = recv.getRuntime();
         RubyString filename = arg1.convertToString(); // TODO: SafeStringValue here
         RubyInteger newLength = arg2.convertToInteger(); 
         
         if (newLength.getLongValue() < 0) {
             throw runtime.newErrnoEINVALError("invalid argument: " + filename);
         }
         
         IRubyObject[] args = new IRubyObject[] { filename, runtime.newString("w+") };
         RubyFile file = (RubyFile) open(recv, args, false, null);
         file.truncate(newLength);
         file.close();
         
         return RubyFixnum.zero(runtime);
     }
     
     /**
      * This method does NOT set atime, only mtime, since Java doesn't support anything else.
      */
     @JRubyMethod(required = 2, rest = true, meta = true)
     public static IRubyObject utime(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         Arity.checkArgumentCount(runtime, args, 2, -1);
         
         // Ignore access_time argument since Java does not support it.
         
         long mtime;
         if (args[1] instanceof RubyTime) {
             mtime = ((RubyTime) args[1]).getJavaDate().getTime();
         } else if (args[1] instanceof RubyNumeric) {
             mtime = RubyNumeric.num2long(args[1]);
         } else {
             mtime = 0;
         }
         
         for (int i = 2, j = args.length; i < j; i++) {
             RubyString filename = RubyString.stringValue(args[i]);
             runtime.checkSafeString(filename);
             JRubyFile fileToTouch = JRubyFile.create(runtime.getCurrentDirectory(),filename.toString());
             
             if (!fileToTouch.exists()) {
                 throw runtime.newErrnoENOENTError(" No such file or directory - \"" + filename + "\"");
             }
             
             fileToTouch.setLastModified(mtime);
         }
         
         return runtime.newFixnum(args.length - 2);
     }
     
     @JRubyMethod(name = {"unlink", "delete"}, rest = true, meta = true)
     public static IRubyObject unlink(IRubyObject recv, IRubyObject[] args) {
         Ruby runtime = recv.getRuntime();
         
         for (int i = 0; i < args.length; i++) {
             RubyString filename = RubyString.stringValue(args[i]);
             runtime.checkSafeString(filename);
             JRubyFile lToDelete = JRubyFile.create(runtime.getCurrentDirectory(),filename.toString());
             
             if (!lToDelete.exists()) {
                 throw runtime.newErrnoENOENTError(" No such file or directory - \"" + filename + "\"");
             }
             
             if (!lToDelete.delete()) return runtime.getFalse();
         }
         
         return runtime.newFixnum(args.length);
     }
 }
diff --git a/test/externals/ruby_test/bench/core/bench_array.rb b/test/externals/ruby_test/bench/core/bench_array.rb
index 5c595ee25f..3a5a6f5837 100644
--- a/test/externals/ruby_test/bench/core/bench_array.rb
+++ b/test/externals/ruby_test/bench/core/bench_array.rb
@@ -1,431 +1,431 @@
 #######################################################################
 # bench_array.rb
 #
 # Benchmark suite for the Array methods.  Deprecated methods and
 # aliases are not benchmarked.
 #######################################################################
 require "benchmark"
 
-MAX = 200000
+MAX = 2_000_000
 
 Benchmark.bm(35) do |x|
    x.report("Array[]"){
       MAX.times{ Array["a", 1, "b", true, nil] }
    }
 
    x.report("Array.new(int)"){
       MAX.times{ Array.new(3) }
    }
 
    x.report("Array.new(int, obj)"){
       MAX.times{ Array.new(3, "hi") }
    }
 
    x.report("Array.new(array)"){
       MAX.times{ Array.new([1,2,3]) }
    }
 
    x.report("Array.new(size){ block }"){
       MAX.times{ Array.new(3){|i|} }
    }
 
    x.report("Array#&"){
       array1 = [1,2,3]
       array2 = [3,4,5]
       MAX.times{ array1 & array2 }
    }
 
    x.report("Array#* (int)"){
       array = [1,2,3]
       MAX.times{ array * 10 }
    }
 
    x.report("Array#* (join)"){
       array = [1,2,3]
       MAX.times{ array * "-" }
    }
 
    x.report("Array#-"){
       array1 = [1,2,3,4,1,2,3]
       array2 = [2,3,4]
       MAX.times{ array1 - array2 }
    }
 
    x.report("Array#<<"){
       array = [1,2,3]
       MAX.times{ array << "a" }
    }
 
    x.report("Array#<=>"){
       array1 = ["a", "b", "c"]
       array2 = [1, 2, 3]
       MAX.times{ array1 <=> array2 }
    }
 
    x.report("Array#=="){
       array1 = [1, 2, 3]
       array2 = [1, 2, "3"]
       MAX.times{ array1 == array2 }
    }
 
    x.report("Array#[]"){
       array = ["a", 1, "b", true, nil]
       MAX.times{ array[2] }
    }
 
    x.report("Array#[]="){
       array = [0, 1, 2]
       MAX.times{ array[1] = 5 }
    }
 
    x.report("Array#|"){
       array1 = [1, 2, 3]
       array2 = [3, 4, 5]
       MAX.times{ array1 | array2 }
    }
 
    x.report("Array#assoc"){
       array1 = ["a", "b", "c"]
       array = [array1]
       MAX.times{ array.assoc("a") }
    }
    
    x.report("Array#at"){
       array = ["a", 1, "b", true, nil]
       MAX.times{ array.at(2) }
    }
 
    x.report("Array#clear"){
       array = [1, 2, 3]
       MAX.times{ array.clear }
    }
 
    x.report("Array#collect"){
       array = [1,2,3,4]
       MAX.times{ array.collect{ |e| } }
    }
 
    x.report("Array#collect!"){
       array = [1,2,3,4]
       MAX.times{ array.collect!{ |e| } }
    }
 
    x.report("Array#compact"){
       array = [1, nil, "two", nil, false]
       MAX.times{ array.compact }
    }
 
    x.report("Array#compact!"){
       array = [1, nil, "two", nil, false]
       MAX.times{ array.compact! }
    }
 
    x.report("Array#concat"){
       array1 = ["a", "b"]
       array2 = ["c", 1, "d"]
       MAX.times{ array1.concat(array2) }
    }
 
    x.report("Array#delete(obj)"){
       array = ["a", "b", 1]
       MAX.times{ array.delete("b") }
    }
 
    x.report("Array#delete(obj){ block }"){
       array = ["a", "b", 1]
       MAX.times{ array.delete("b"){ 1 } }
    }
 
    x.report("Array#delete_at"){
       array = [1, 2, 3, 4]
       MAX.times{ array.delete_at(2) }
    }
 
    x.report("Array#delete_if"){
       array = [1, 2, 3, 4]
       MAX.times{ array.delete_if{ |e| e == 3 } }
    }
 
    x.report("Array#each"){
       array = [1, 2, 3, 4]
       MAX.times{ array.each{ |e| } }
    }
 
    x.report("Array#each_index"){
       array = [1, 2, 3, 4, 5]
       MAX.times{ array.each_index{ |i| } }
    }
 
    x.report("Array#empty?"){
       array = []
       MAX.times{ array.empty? }
    }
 
    x.report("Array#eql?"){
       array1 = ["a", "b", "c"]
       array2 = ["a", "b", "c"]
       MAX.times{ array1.eql?(array2) }
    }
 
    x.report("Array#fetch(index)"){
       array = [1, 2, 3, 4, 5]      
       MAX.times{ array.fetch(0) }
    }
 
    x.report("Array#fetch(index, default)"){
       array = [1, 2, 3, 4, 5]      
       MAX.times{ array.fetch(9, "a") }
    }
 
    x.report("Array#fetch(index){ block }"){
       array = [1, 2, 3, 4, 5]      
       MAX.times{ array.fetch(9){ |i| } }
    }
 
    x.report("Array#fill(obj)"){
       array = ["a", "b", "c", "d"]
       MAX.times{ array.fill("x") }
    }
 
    x.report("Array#fill(obj, start)"){
       array = ["a", "b", "c", "d"]
       MAX.times{ array.fill("x", 2) }
    }
 
    x.report("Array#fill(obj, start, length)"){
       array = ["a", "b", "c", "d"]
       MAX.times{ array.fill("x", 0, 1) }
    }
 
    x.report("Array#fill(obj, range)"){
       array = ["a", "b", "c", "d"]
       MAX.times{ array.fill("x", 0..1) }
    }
 
    x.report("Array#fill{ block }"){
       array = ["a", "b", "c", "d"]
       MAX.times{ array.fill{ 7 } }
    }
 
    x.report("Array#fill(start){ block }"){
       array = ["a", "b", "c", "d"]
       MAX.times{ array.fill(-3){ 7 } }
    }
 
    x.report("Array#fill(start, length){ block }"){
       array = ["a", "b", "c", "d"]
       MAX.times{ array.fill(0,1){ 7 } }
    }
 
    x.report("Array#fill(range){ block }"){
       array = ["a", "b", "c", "d"]
       MAX.times{ array.fill(0..1){ 7 } }
    }
 
    x.report("Array#first"){
       array = [1,2,3]
       MAX.times{ array.first }
    }
 
    x.report("Array#flatten"){
       array = [[1,2,3],[4,5,6]]
       MAX.times{ array.flatten }
    }
 
    x.report("Array#flatten!"){
       array = [[1,2,3],[4,5,6]]
       MAX.times{ array.flatten! }
    }
 
    x.report("Array#include?"){
       array = ["one", 2, "three", nil]
       MAX.times{ array.include?("three") }
    }
 
    x.report("Array#index"){
       array = [1,2,3,"four"]
       MAX.times{ array.include?("four") }
    }
 
    # Pathological in Ruby 1.8.3 and earlier
    x.report("Array#insert"){
       array = [1,2,3,4]
-      MAX.times{ array.insert(2, "a", "b") }
+      (MAX/10).times{ array.insert(2, "a", "b") }
    }
 
    x.report("Array#join"){
       array = [1,2,3,4]
       MAX.times{ array.join }
    }
 
    x.report("Array#last"){
       array = [1,2,3,4]
       MAX.times{ array.last }
    }
 
    x.report("Array#length"){
       array = [1,2,3,4]
       MAX.times{ array.length }
    }
 
    x.report("Array#nitems"){
       array = [1, nil, "two", nil]
       MAX.times{ array.nitems }
    }
 
    # TODO: Add more variations of Array#pack later
    x.report("Array#pack"){
       array = ["a", "b", "c"]
       MAX.times{ array.pack("A3A3A3") }
    }
 
    x.report("Array#pop"){
       array = [1,2,3]
       MAX.times{ array.pop }
    }
 
    x.report("Array#push"){
       array = [1,2,3]
       MAX.times{ array.push("one","two","three") }
    }
 
    x.report("Array#rassoc"){
       array = [ [1,"one"], [2,"two"], [3,"three"], [4,"four"] ]
       MAX.times{ array.rassoc("two") }
    }
 
    x.report("Array#reject"){
       array = [1, 2, 3, 4]
       MAX.times{ array.reject{ |e| } }
    }
 
    x.report("Array#reject!"){
       array = [1, 2, 3, 4]
       MAX.times{ array.reject!{ |e| } }
    }
 
    x.report("Array#replace"){
       array = [1,2,3]
       MAX.times{ array.replace([4,5,6]) }
    }
 
    x.report("Array#reverse"){
       array = [1,2,3,4]
       MAX.times{ array.reverse }
    }
 
    x.report("Array#reverse!"){
       array = [1,2,3,4]
       MAX.times{ array.reverse! }
    }
 
    x.report("Array#reverse_each"){
       array = [1,2,3,4]
       MAX.times{ array.reverse_each{ |e| } }
    }
 
    x.report("Array#rindex"){
       array = [1,2,3,2]
       MAX.times{ array.rindex(2) }
    }
 
    x.report("Array#shift"){
       array = [1,2,3,4]
       MAX.times{ array.shift }
    }
 
    x.report("Array#slice(int)"){
       array = [1,2,3,4]
       MAX.times{ array.slice(2) }
    }
 
    x.report("Array#slice(start, length)"){
       array = [1,2,3,4,5]
       MAX.times{ array.slice(2,2) }
    }
 
    x.report("Array#slice(range)"){
       array = [1,2,3,4,5]
       MAX.times{ array.slice(2..4) }
    }
 
    x.report("Array#slice!(int)!"){
       array = [1,2,3,4]
       MAX.times{ array.slice!(2) }
    }
 
    x.report("Array#slice!(start, length)"){
       array = [1,2,3,4,5]
       MAX.times{ array.slice!(2,2) }
    }
 
    x.report("Array#slice!(range)"){
       array = [1,2,3,4,5]
       MAX.times{ array.slice!(2..4) }
    }
 
    x.report("Array#sort"){
       array = [2,3,1,4]
       MAX.times{ array.sort }
    }
 
    x.report("Array#sort{ block }"){
       array = [2,3,1,4]
       MAX.times{ array.sort{ |a,b| a <=> b } }
    }
 
    x.report("Array#sort!"){
       array = [2,3,1,4]
       MAX.times{ array.sort! }
    }
 
    x.report("Array#sort!{ block }"){
       array = [2,3,1,4]
       MAX.times{ array.sort!{ |a,b| a <=> b } }
    }
 
    x.report("Array#to_a"){
       array = [1,2,3,4]
       MAX.times{ array.to_a }
    }
 
    x.report("Array#to_ary"){
       array = [1,2,3,4]
       MAX.times{ array.to_ary }
    }
 
    x.report("Array#to_s"){
       array = [1,2,3,4]
       MAX.times{ array.to_s }
    }
 
    x.report("Array#transpose"){
       array = [ [1,2], [3,4], [5,6] ]
       MAX.times{ array.transpose }
    }
 
    x.report("Array#uniq"){
       array = [1,2,3,1,2,3,4]
       MAX.times{ array.uniq }
    }
 
    x.report("Array#uniq!"){
       array = [1,2,3,1,2,3,4]
       MAX.times{ array.uniq! }
    }
 
    x.report("Array#unshift"){
       array = [1,2,3,4]
       MAX.times{ array.unshift }
    }
 
    x.report("Array#values_at(int)"){
       array = [1,2,3,4]
       MAX.times{ array.values_at(0, 2) }
    }
 
    x.report("Array#values_at(range)"){
       array = [1,2,3,4]
       MAX.times{ array.values_at(0..2) }
    }
 end
diff --git a/test/externals/ruby_test/bench/core/bench_hash.rb b/test/externals/ruby_test/bench/core/bench_hash.rb
index d3d6b92bf3..ebf23dc1ad 100644
--- a/test/externals/ruby_test/bench/core/bench_hash.rb
+++ b/test/externals/ruby_test/bench/core/bench_hash.rb
@@ -1,222 +1,222 @@
 ##############################################################
 # bench_hash.rb
 #
 # Benchmark suite for the Hash class and instance methods.
 ##############################################################
 require "benchmark"
 
-MAX = 200000
+MAX = 2_000_000
 
 Benchmark.bm(30) do |x|
    x.report("Hash[]"){
       MAX.times{ Hash["a","b","c","d"] }
    }
 
    x.report("Hash.new"){
       MAX.times{ Hash.new }
    }
 
    x.report("Hash.new(obj)"){
       MAX.times{ Hash.new(0) }
    }
 
    x.report("Hash.new{ block }"){
       MAX.times{ Hash.new{ |k,v| } }
    }
 
    x.report("Hash#=="){
       hash1 = {"a"=>1, "b"=>2, "c"=>3}
       hash2 = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash1 == hash2 }
    }
 
    x.report("Hash#[]"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash["a"] }
    }
 
    x.report("Hash#[]="){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash["a"] = 4 }
    }
 
    x.report("Hash#clear"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.clear }
    }
 
    x.report("Hash#default"){
       hash = Hash.new(0)
       MAX.times{ hash.default }
    }
 
    x.report("Hash#default(key)"){
       hash = Hash.new(0)
       MAX.times{ hash.default(2) }
    }
 
    x.report("Hash#default="){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.default = 5 }
    }
 
    x.report("Hash#default_proc"){
       hash = Hash.new{ |k,v| }
       MAX.times{ hash.default_proc }
    }
 
    x.report("Hash#delete(key)"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.delete("a") }
    }
 
    x.report("Hash#delete(key){ block }"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.delete("a"){ |e| } }
    }
 
    x.report("Hash#delete_if"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.delete_if{ |k,v| } }
    }
 
    x.report("Hash#each"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.each{ |e| } }
    }
 
    x.report("Hash#each_key"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.each_key{ |k| } }
    }
 
    x.report("Hash#each_value"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.each_value{ |v| } }
    }
 
    x.report("Hash#empty?"){
       hash = {}
       MAX.times{ hash.empty? }
    }
 
    x.report("Hash#fetch"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.fetch("a") }
    }
 
    x.report("Hash#fetch{ block }"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.fetch("a"){ |e| } }
    }
 
    x.report("Hash#has_key?"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.has_key?("b") }
    }
 
    x.report("Hash#has_value?"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.has_value?("b") }
    }
 
    x.report("Hash#index"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.index("b") }
    }
 
    x.report("Hash#invert"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.invert }
    }
 
    x.report("Hash#keys"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.keys } 
    }
 
    x.report("Hash#length"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.length } 
    }
 
    x.report("Hash#merge(hash)"){
       hash1 = {"a"=>1, "b"=>2, "c"=>3}
       hash2 = {"d"=>4, "e"=>5, "f"=>6}
       MAX.times{ hash1.merge(hash2) } 
    }
 
    x.report("Hash#merge(hash){ block }"){
       hash1 = {"a"=>1, "b"=>2, "c"=>3}
       hash2 = {"d"=>4, "e"=>5, "f"=>6}
       MAX.times{ hash1.merge(hash2){ |k,o,n| } }
    }
 
    x.report("Hash#merge!(hash){ block }"){
       hash1 = {"a"=>1, "b"=>2, "c"=>3}
       hash2 = {"d"=>4, "e"=>5, "f"=>6}
       MAX.times{ hash1.merge!(hash2){ |k,o,n| } }
    }
 
    # TODO: Make a better sample case for Hash#rehash
    x.report("Hash#rehash"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.rehash }
    }
 
    x.report("Hash#reject"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.reject{ |k,v| } }
    }
 
    x.report("Hash#reject!"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.reject!{ |k,v| } }
    }
 
    x.report("Hash#replace"){
       hash1 = {"a"=>1, "b"=>2, "c"=>3}
       hash2 = {"d"=>4, "e"=>5, "f"=>6}
       MAX.times{ hash1.replace(hash2) }
    }
 
    x.report("Hash#select"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.select{ |k,v| } }
    }
 
    x.report("Hash#shift"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.shift }
    }
 
    x.report("Hash#sort"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.sort{ |k,v| k <=> v } }
    }
 
    x.report("Hash#to_a"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.to_a }
    }
 
    x.report("Hash#to_hash"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.to_hash }
    }
 
    x.report("Hash#to_s"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.to_s }
    }
 
    x.report("Hash#values"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.values }
    }
 
    x.report("Hash#values_at"){
       hash = {"a"=>1, "b"=>2, "c"=>3}
       MAX.times{ hash.values_at("b") }
    }
 end
diff --git a/test/externals/ruby_test/bench/core/bench_integer.rb b/test/externals/ruby_test/bench/core/bench_integer.rb
index 0e121f566f..9667f4d97e 100644
--- a/test/externals/ruby_test/bench/core/bench_integer.rb
+++ b/test/externals/ruby_test/bench/core/bench_integer.rb
@@ -1,60 +1,60 @@
 #######################################################################
 # bench_integer.rb
 #
 # Benchmark suite for the Integer methods.  Deprecated methods and
 # aliases are not benchmarked. To avoid using Integer methods within
 # the test suite, we use for loops instead of Integer#times.
 #######################################################################
 require "benchmark"
 
-MAX = 200000
+MAX = 2_000_000
 
 Benchmark.bm(35) do |x|
    x.report("Integer#chr"){
       for i in 1..MAX
          65.chr
       end
    }
 
    x.report("Integer#downto"){
       for i in 1..MAX
          10.downto(1){|n| }
       end
    }
 
    x.report("Integer#floor"){
       for i in 1..MAX
          (-1).floor
       end
    }
 
    x.report("Integer#integer?"){
       for i in 1..MAX
          100.integer?
       end
    }
 
    x.report("Integer#next"){
       for i in 1..MAX
          100.next
       end
    }
 
    x.report("Integer#times"){
       for i in 1..MAX
          10.times{ |n| }
       end
    }
 
    x.report("Integer#to_i"){
       for i in 1..MAX
          10.to_i
       end
    }
 
    x.report("Integer#upto"){
       for i in 1..MAX
          0.upto(10){ |n| }
       end
    }
 end
diff --git a/test/externals/ruby_test/bench/core/bench_string.rb b/test/externals/ruby_test/bench/core/bench_string.rb
index 44248099e8..64fb49dec9 100644
--- a/test/externals/ruby_test/bench/core/bench_string.rb
+++ b/test/externals/ruby_test/bench/core/bench_string.rb
@@ -1,570 +1,570 @@
 ###############################################################
 # bench_string.rb
 #
 # Benchmark suite for the String class and all its methods.
 ###############################################################
 require "benchmark"
 
-MAX = 50000
+MAX = 5_000_000
 
 Benchmark.bm(30) do |x|
    x.report("String.new"){
       MAX.times{ String.new("test") }
    }
 
    x.report("String#%"){
       string = "%05d"
       number = 123
       MAX.times{ string % number }
    }
 
    x.report("String#*"){
       string = "test"
       int = 3
       MAX.times{ string * int }
    }
 
    x.report("String#+"){
       string1 = "hello"
       string2 = "world"
       MAX.times{ string1 + string2 }
    }
 
    x.report("String#<<"){
       string1 = "hello"
       string2 = "world"
       MAX.times{ string1 << string2 }
    }
 
    x.report("String#<=>"){
       string1 = "abcdef"
       string2 = "abcdef"
       MAX.times{ string1 <=> string2 }
    }
 
    x.report("String#=="){
       string1 = "abc"
       string2 = "abcd"
       MAX.times{ string1 == string2 }
    }
 
    x.report("String#==="){
       string1 = "Hello"
       string2 = "HellO"
       MAX.times{ string1 === string2 }
    }
 
    x.report("String#=~"){
       string = "hello"
       MAX.times{ string =~ /\w+/ }
    }
 
    x.report("String#[int]"){
       string = "hello"
       MAX.times{ string[1] }
    }
 
    x.report("String#[int,int]"){
       string = "hello"
       MAX.times{ string[1,2] }
    }
 
    x.report("String#[range]"){
       string = "hello"
       MAX.times{ string[1..2] }
    }
 
    x.report("String#[regexp]"){
       string = "hello"
       MAX.times{ string[/\w+/] }
    }
 
    x.report("String#[regexp,int]"){
       string = "hello"
       MAX.times{ string[/\w+/,1] }
    }
 
    x.report("String#[string]"){
       string = "hello"
       MAX.times{ string["lo"] }
    }
 
    # TODO: Fix
    #x.report("String#~"){
    #   string = "hello"
    #   MAX.times{ ~ string }
    #}
 
    x.report("String#capitalize"){
       string = "hello"
       MAX.times{ string.capitalize }
    }
 
    x.report("String#capitalize!"){
       string = "hello"
       MAX.times{ string.capitalize! }
    }
 
    x.report("String#casecmp"){
       string1 = "hello"
       string2 = "HELLO"
       MAX.times{ string1.casecmp(string2) }
    }
 
    x.report("String#center"){
       string = "hello"
       MAX.times{ string.center(4) }
    }
 
    x.report("String#chomp"){
       string = "hello\n"
       MAX.times{ string.chomp }
    }
 
    x.report("String#chomp!"){
       string = "hello\n"
       MAX.times{ string.chomp! }
    }
 
    x.report("String#chop"){
       string = "hello"
       MAX.times{ string.chop }
    }
 
    x.report("String#chop!"){
       string = "hello"
       MAX.times{ string.chop! }
    }
 
    x.report("String#count(string)"){
       string = "hello"
       MAX.times{ string.count("lo") }
    }
 
    x.report("String#count(^string)"){
       string = "hello"
       MAX.times{ string.count("^l") }
    }
 
    x.report("String#crypt"){
       string = "hello"
       MAX.times{ string.crypt("sh") }
    }
 
    x.report("String#delete"){
       string = "hello"
       MAX.times{ string.delete("lo") }
    }
 
    x.report("String#delete!"){
       string = "hello"
       MAX.times{ string.delete!("lo") }
    }
 
    x.report("String#downcase"){
       string = "HELLO"
       MAX.times{ string.downcase }
    }
 
    x.report("String#downcase!"){
       string = "HELLO"
       MAX.times{ string.downcase! }
    }
 
    x.report("String#dump"){
       string = "hello&%"
       MAX.times{ string.dump }
    }
 
    x.report("String#each"){
       string = "hello\nworld"
       MAX.times{ string.each{ |e| } }
    }
 
    x.report("String#each_byte"){
       string = "hello"
       MAX.times{ string.each_byte{ |e| } }
    }
 
    x.report("String#empty?"){
       string = ""
       MAX.times{ string.empty? }
    }
 
    x.report("String#eql?"){
       string1= "hello"
       string2= "hello"
       MAX.times{ string1.eql?(string2) }
    }
 
    x.report("String#gsub(regexp, repl)"){
       string = "hello"
       MAX.times{ string.gsub(/[aeiou]/, '*') }
    }
 
    x.report("String#gsub(regexp){ block }"){
       string = "hello"
       MAX.times{ string.gsub(/./){ |s| } }
    }
 
    x.report("String#gsub!(regexp){ block }"){
       string = "hello"
       MAX.times{ string.gsub!(/./){ |s| } }
    }
 
    x.report("String#hex"){
       string = "0x0a"
       MAX.times{ string.hex }
    }
 
    x.report("String#include?"){
       string = "hello"
       MAX.times{ string.include?("lo") }
    }
 
    x.report("String#index(string)"){
       string = "hello"
       MAX.times{ string.index("e") }
    }
 
    x.report("String#index(string, offset)"){
       string = "hello"
       MAX.times{ string.index("e", -1) }
    }
 
    x.report("String#index(int)"){
       string = "hello"
       MAX.times{ string.index(1) }
    }
 
    x.report("String#index(int, offset)"){
       string = "hello"
       MAX.times{ string.index(1, -1) }
    }
 
    x.report("String#index(regexp)"){
       string = "hello"
       MAX.times{ string.index(/[aeiou]/) }
    }
 
    x.report("String#index(regexp, offset)"){
       string = "hello"
       MAX.times{ string.index(/[aeiou]/, -1) }
    }
 
    x.report("String#insert"){
       string = "hello"
-      MAX.times{ string.insert(2, "world") }
+      (MAX/10).times{ string.insert(2, "world") }
    }
 
    x.report("String#intern"){
       string = "hello"
       MAX.times{ string.intern }
    }
 
    x.report("String#length"){
       string = "hello"
       MAX.times{ string.length }
    }
 
    x.report("String#ljust"){
       string = "hello"
       MAX.times{ string.ljust(10) }
    }
 
    x.report("String#lstrip"){
       string = "   hello"
       MAX.times{ string.lstrip }
    }
 
    x.report("String#lstrip!"){
       string = "   hello"
       MAX.times{ string.lstrip! }
    }
 
    x.report("String#match(regexp)"){
       string = "hello"
       MAX.times{ string.match(/lo/) }
    }
 
    x.report("String#match(string)"){
       string = "hello"
       MAX.times{ string.match("lo") }
    }
    
    x.report("String#oct"){
       string = "123"
       MAX.times{ string.oct }
    }
 
    x.report("String#replace"){
       string = "hello"
       MAX.times{ string.replace("world") }
    }
 
    x.report("String#reverse"){
       string = "hello"
       MAX.times{ string.reverse }
    }
 
    x.report("String#reverse!"){
       string = "hello"
       MAX.times{ string.reverse! }
    }
 
    x.report("String#rindex(string)"){
       string = "hello"
       MAX.times{ string.rindex("e") }
    }
 
    x.report("String#rindex(string, int)"){
       string = "hello"
       MAX.times{ string.rindex("e",1) }
    }
 
    x.report("String#rindex(int, int)"){
       string = "hello"
       MAX.times{ string.rindex(1,1) }
    }
 
    x.report("String#rindex(regexp)"){
       string = "hello"
       MAX.times{ string.rindex(/[aeiou]/) }
    }
 
    x.report("String#rindex(regexp, int)"){
       string = "hello"
       MAX.times{ string.rindex(/[aeiou]/, 1) }
    }
 
    x.report("String#rjust(width)"){
       string = "hello"
       MAX.times{ string.rjust(10) }
    }
 
    x.report("String#rjust(width, padding)"){
       string = "hello"
       MAX.times{ string.rjust(10, "-") }
    }
 
    x.report("String#rstrip"){
       string = "hello    "
       MAX.times{ string.rstrip }
    }
 
    x.report("String#rstrip!"){
       string = "hello    "
       MAX.times{ string.rstrip! }
    }
 
    x.report("String#scan"){
       string = "cruel world"
       MAX.times{ string.scan(/\w+/) }
    }
 
    x.report("String#scan{ block }"){
       string = "cruel world"
       MAX.times{ string.scan(/\w+/){ |w| } }
    }
 
    x.report("String#slice(int)"){
       string = "hello"
       MAX.times{ string.slice(1) }
    }
 
    x.report("String#slice(int, int)"){
       string = "hello"
       MAX.times{ string.slice(1,3) }
    }
 
    x.report("String#slice(range)"){
       string = "hello"
       MAX.times{ string.slice(1..3) }
    }
 
    x.report("String#slice(regexp)"){
       string = "hello"
       MAX.times{ string.slice(/ell/) }
    }
 
    x.report("String#slice(string)"){
       string = "hello"
       MAX.times{ string.slice("lo") }
    }
 
    x.report("String#split"){
       string = "now is the time"
       MAX.times{ string.split }
    }
 
    x.report("String#split(string)"){
       string = "now-is-the-time"
       MAX.times{ string.split("-") }
    }
 
    x.report("String#split(string, limit)"){
       string = "now-is-the-time"
       MAX.times{ string.split("-", 2) }
    }
 
    x.report("String#split(regexp)"){
       string = "now-is-the-time"
       MAX.times{ string.split(/\w+/) }
    }
 
    x.report("String#split(regexp, limit)"){
       string = "now-is-the-time"
       MAX.times{ string.split(/\w+/, 2) }
    }
 
    x.report("String#squeeze"){
       string = "foo    moo    hello"
       MAX.times{ string.squeeze }
    }
 
    x.report("String#squeeze(char)"){
       string = "foo    moo    hello"
       MAX.times{ string.squeeze(" ") }
    }
 
    x.report("String#squeeze!"){
       string = "foo    moo    hello"
       MAX.times{ string.squeeze! }
    }
 
    x.report("String#squeeze!(char)"){
       string = "foo    moo    hello"
       MAX.times{ string.squeeze!(" ") }
    }
 
    x.report("String#strip"){
       string = "   hello    "
       MAX.times{ string.strip }
    }
 
    x.report("String#strip!"){
       string = "   hello    "
       MAX.times{ string.strip! }
    }
 
    x.report("String#sub(pattern, repl)"){
       string = "hello"
       MAX.times{ string.sub(/[aeiou]/, '*') }
    }
 
    x.report("String#sub(pattern){ block }"){
       string = "hello"
       MAX.times{ string.sub(/./){ |s| } }
    }
 
    x.report("String#sub!(pattern, repl)"){
       string = "hello"
       MAX.times{ string.sub!(/[aeiou]/, '*') }
    }
 
    x.report("String#sub!(pattern){ block }"){
       string = "hello"
       MAX.times{ string.sub!(/./){ |s| } }
    }
 
    x.report("String#succ"){
       string = "hello"
       MAX.times{ string.succ }
    }
 
    x.report("String#succ!"){
       string = "hello"
       MAX.times{ string.succ! }
    }
 
    x.report("String#sum"){
       string = "now is the time"
       MAX.times{ string.sum }
    }
 
    x.report("String#sum(int)"){
       string = "now is the time"
       MAX.times{ string.sum(8) }
    }
 
    x.report("String#swapcase"){
       string = "Hello"
       MAX.times{ string.swapcase }
    }
 
    x.report("String#swapcase!"){
       string = "Hello"
       MAX.times{ string.swapcase! }
    }
 
    x.report("String#to_f"){
       string = "123.45"
       MAX.times{ string.to_f }
    }
 
    x.report("String#to_i"){
       string = "12345"
       MAX.times{ string.to_i }
    }
 
    x.report("String#to_i(base)"){
       string = "12345"
       MAX.times{ string.to_i(8) }
    }
 
    x.report("String#to_s"){
       string = "hello"
       MAX.times{ string.to_s }
    }
 
    x.report("String#to_str"){
       string = "hello"
       MAX.times{ string.to_str }
    }
 
    x.report("String#to_sym"){
       string = "hello"
       MAX.times{ string.to_sym }
    }
 
    x.report("String#tr"){
       string = "hello"
       MAX.times{ string.tr("el","ip") }
    }
 
    x.report("String#tr!"){
       string = "hello"
       MAX.times{ string.tr!("el","ip") }
    }
 
    x.report("String#tr_s"){
       string = "hello"
       MAX.times{ string.tr_s("l","r") }
    }
 
    x.report("String#tr_s!"){
       string = "hello"
       MAX.times{ string.tr_s!("l","r") }
    }
    
    # TODO: Add more variations for String#unpack
    x.report("String#unpack"){
       string = "hello"
       MAX.times{ string.unpack("A5") }
    }
 
    x.report("String#upcase"){
       string = "heLLo"
       MAX.times{ string.upcase }
    }
 
    x.report("String#upcase!"){
       string = "heLLo"
       MAX.times{ string.upcase! }
    }
 
    x.report("String#upto"){
       string = "a1"
       MAX.times{ string.upto("b6"){ |s| } }
    }
 end
