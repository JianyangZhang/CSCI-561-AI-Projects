import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

public class homework {
    // String and = "^", or = "|", implied = "=>";
    static int negcount = 0, andcount = 0, orcount = 0, preIndex = 0;
    ParseTree root;
    static LinkedList<String> pred = new LinkedList<String>();
    static LinkedList<String> s = new LinkedList<String>();
    static LinkedList<String> t1 = new LinkedList<String>();
    static String string1="",string2=""; 
    static LinkedList<String> cnfkb = new LinkedList<String>();
    static LinkedList<String> treekb = new LinkedList<String>();
    static LinkedList<LinkedList<String>> tempunify = new LinkedList<LinkedList<String>>();
    static HashMap<String,String> substitution=new HashMap<String,String>();
    static HashMap<String, LinkedList<FinalKB>> finalkb = new HashMap<String,LinkedList<FinalKB>>();
    static Tokenizer tokenizer = new Tokenizer();

    public static void main(String[] args) {

        tokenizer.add("\\(", 1);
        tokenizer.add("\\)", 2);
        tokenizer.add("\\,", 3);
        tokenizer.add("\\#", 4);
        tokenizer.add("\\&", 5);
        tokenizer.add("\\|", 6);
        tokenizer.add("\\~", 7);
        tokenizer.add("[a-zA-Z][a-zA-Z0-9_]*", 8);
        int questionsize = 0;// ,counter=0;

        String file = "C:\\input.txt";
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String str = null;
            ArrayList<String> lines = new ArrayList<String>();
            while ((str = in.readLine()) != null) {
                lines.add(str.trim());
            }
            String[] linesArray = lines.toArray(new String[lines.size()]);
            questionsize = Integer.parseInt(linesArray[0]);
            // System.out.println(questionsize);
            String questions[][] = new String[questionsize][4];
            for (int x = 1; x <= questionsize; x++) {

               // String s;
               /* if (linesArray[x].charAt(0) == '~') {
                    s = linesArray[x].charAt(1) + "";
                } else {
                    s = linesArray[x].charAt(0) + "";
                }
                */
               // questions[x - 1][0] = s;
                questions[x - 1][0] = linesArray[x];
                questions[x - 1][1] = Negate(linesArray[x]);
                questions[x - 1][2] = processQuestions(questions[x - 1][1]);
                questions[x - 1][3] = processQuestions(questions[x - 1][0]);
               
               
                FinalKB fkb=new FinalKB();
                fkb.premise=questions[x - 1][1] ;
               // fkb.conclusion=null;
               
                
                if(finalkb.containsKey(questions[x-1][2]))
                {
                    LinkedList<FinalKB> storingtemp= finalkb.get(questions[x-1][2]);
                    storingtemp.add(fkb);
                    finalkb.remove(questions[x-1][2]);
                    finalkb.put(questions[x-1][2], storingtemp);

                }
                else
                {
                	 LinkedList<FinalKB> tempqts= new LinkedList<FinalKB>();
                	tempqts.add(fkb);
                	finalkb.put(questions[x-1][2],tempqts);
                }

                printfinalkb(finalkb);

            }
            
            for (int x1 = questionsize + 2; x1 < linesArray.length; x1++) {

                s.clear();
                pred.clear();
                t1.clear();
                negcount = 0;
                andcount = 0;
                orcount = 0;
                preIndex = 0;
                treekb.clear();
                // cnfkb.clear();
                System.out.println("**************");
                System.out.println("Original: " + linesArray[x1].replaceAll("\\s+", ""));
                CreateToken(linesArray[x1].replaceAll("\\s+", "").replaceAll("=>", "#"));
                System.out.println("cnfkb: "+cnfkb);
             }
            linesArray = null;
            ProcessCnfKB(cnfkb);
            printfinalkb(finalkb);
            
            for (int x = 0; x < questions.length; x++) {
            	System.out.println("questch");
            	System.out.println(questions[x][0]+" "+questions[x][1]+" "+questions[x][2]+" "+questions[x][3]);
            	tempunify.clear();            	
            	Ask(questions[x][2],questions[x][1]); 
            	//ask should return true or false
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

	private static String processQuestions(String questions) {
    	//System.out.println("Questions "+questions);
    	char[] c=questions.toCharArray();
    	int ch=0;
    	String returnstr="";
    	while(c[ch]!='(')
    	{
    		returnstr=(returnstr+String.valueOf(c[ch]));
    		ch++;
    	}
		//System.out.println("return str: "+returnstr);
    	return returnstr;
		
	}

	private static void Ask(String pred,String premise) {
		
    	System.out.println("*****ASK*****");
    	System.out.println(pred+" "+premise);    	
    	
    	LinkedList<String> arg1=findArgs(premise);
    	int questionarglen=arg1.size();
    	LinkedList<String> arg2 =new LinkedList<String>();
    	String negatedpremise=Negate(premise);
    	System.out.println("neg: "+ negatedpremise);
    	String negatedpred=Negate(pred);
    	System.out.println("neg pred: "+ negatedpred);
    	   	
    	if(finalkb.containsKey(negatedpred))
    	{
    		System.out.println("inside ask ");
    		    		
    		LinkedList<FinalKB> value= (finalkb.get(negatedpred));
    		 for(int i=0; i<value.size(); i++)
             {
    			 arg2=findArgs(value.get(i).premise);
    			 if(questionarglen==arg2.size())
    			 {
    				 System.out.println("here: ");
    				 System.out.println("kb match: "+value.get(i).premise+"|"+value.get(i).conclusion);
    				 unify(premise,value.get(i),arg1,arg2);
    				 /*String temp=
    				 value.get(i).premise;
    				 fl.conclusion=value.get(i).conclusion;
    				 tempunify.add(fl);
    				 System.out.println("sise: "+tempunify.size());
    				 */
    			 }
    			 else
    			 {
    				 System.out.println("False");
    			 }
             }
    	}
    	else
    	{
    		System.out.println("False");
       	}
   	}

	private static String unify(String sent1, FinalKB finalKB2, LinkedList<String> arg1, LinkedList<String> arg2) {
		System.out.println("sent1: "+sent1+" sent2 "+finalKB2.premise+" "+finalKB2.conclusion);
		//String term="",var ="";
		if(arg1.size()==1 && arg2.size()==1)
		{
			if(isConstant(arg1.get(0))&&isConstant(arg2.get(0)))
			{
				if(!arg1.get(0).equals(arg2.get(0)))
				{
					//no unification possible
				}
				else
				{
					//return match
				}
			}
			else if(!(isConstant(arg1.get(0)))&& isConstant(arg2.get(0)))
			{
				//unify conlusion with arg1
			}
			else if((isConstant(arg1.get(0)))&& !isConstant(arg2.get(0)))
			{
				//unify conlusion with arg1
			}
			else
			{
				// standarize variables
			}
		}
		else
		{
			
		}
		
		return null;
		
	}

	private static boolean isConstant(String string) {
		System.out.println("arg1: "+string);//'//'.toString().charAt(0));
		if(Character.isUpperCase(((CharSequence) string).charAt(0)))
			return true;
		return false;
	}

	private static LinkedList<String> findArgs(String sent) {
		System.out.println("input to arg "+ sent);
		char[] c=sent.toCharArray();
		LinkedList<String> args= new LinkedList<String>();
		//System.out.println(c.length);
		
		int ch1=0;
		 
		String temp="";
		while(c[ch1]!='(')
		{
			ch1++;
		}
		//System.out.println(ch1);	
		
		
		for(int ch=(ch1+1);ch<c.length;ch++)
		{
			//System.out.println(ch+" "+c[ch]);
			if(c[ch]==')')
			{	
				//System.out.println("temp 1: "+temp);
				args.add(""+temp+"");
				temp="";
			
				break;
			}
			else if(c[ch]=='(')
			{
				continue;
			}
			else if(c[ch]==',')
			{
				//System.out.println("temp 2: "+temp);
				args.add(""+temp+"");
				temp="";
			}
			else
			{
				temp=(temp+String.valueOf(c[ch]));
		//System.out.println("temp 3: "+temp);
			}
		}
		System.out.println("ARGS: "+args+""+args.size());
		
		return args;
	}

	private static void printfinalkb(HashMap<String, LinkedList<FinalKB>> finalkb2) {
        System.out.println("******KB******");
        for (Object name: finalkb.keySet())
        {
            String key = name.toString();
            LinkedList<FinalKB> value = finalkb.get(name);
            for(int i=0; i<value.size(); i++)
            {

                System.out.println(key + " *** "+value.get(i).premise+" *** "+value.get(i).conclusion);
                //System.out.println(fkb1.premise);
            }
        }
    }

    private static void ProcessCnfKB(LinkedList<String> cnfkbprocess) {

        for (int x = 0; x < cnfkbprocess.size(); x++) {
            String temp=cnfkbprocess.get(x)+"";
            System.out.println("cnf clauses: "+temp);
            separateOR(temp);
        }
    }
   
    private static void separateOR(String temp) {
        LinkedList <String> clauses=new LinkedList<String>();
        char[] c=temp.toCharArray();
        String tmp="";
        for(int i=0; i<c.length; i++)
        {
            //System.out.println("c: "+c[i]);

            if(c[i]==('|'))
            {
                clauses.add(tmp);
                tmp="";
            }
            else
            {
                tmp=(tmp+String.valueOf(c[i]));
            }
            //System.out.println("tmp: "+tmp);
        }
        clauses.add(tmp);
        //System.out.println("clAUSES: "+clauses);
        storeClause(clauses);

    }

    private static void storeClause(LinkedList<String> clauses) {
        //System.out.println("storing clause: "+clauses);

        String temp="";
        String predicate="",conclusion="";
        for(int i=0; i<clauses.size(); i++)
        {
            LinkedList<FinalKB> linkobj = new LinkedList<FinalKB>();
            FinalKB fkb=new FinalKB();
            temp = clauses.get(i);
            fkb.premise=temp;

            //System.out.println("temp here: " + temp);
            char[] c = temp.toCharArray();
            int ch = 0;
            while (c[ch] != '(') {
                predicate = (predicate + String.valueOf(c[ch]));
                ch++;
            }
            //System.out.println("predicate: " + predicate);

            for(int i1=0; i1<clauses.size(); i1++)
            {
                if(i1==i)
                    continue;
                else
                {
                    conclusion =(conclusion + clauses.get(i1)+"|");

                }

            }
            if(conclusion.endsWith("|"))
                conclusion=(conclusion.substring(0, conclusion.length()-1));
            //System.out.println("subs: "+conclusion.substring(0, conclusion.length()-1));
            //System.out.println("conclusion: " +conclusion );
            fkb.conclusion=conclusion;

            if(finalkb.containsKey(predicate))
            {
                LinkedList<FinalKB> storingtemp= finalkb.get(predicate);
                storingtemp.add(fkb);
                finalkb.remove(predicate);
                finalkb.put(predicate, storingtemp);

            }
            else
            {
                linkobj.add(fkb);
                finalkb.put(predicate, linkobj);
            }
            predicate="";
            conclusion="";
            //System.out.println(fkb.conclusion);
        }

    }

    private static void CreateToken(String str) {
        try {
            tokenizer.tokenize(str);
            for (Tokenizer.Token tok : tokenizer.getTokens()) {
                // System.out.println(tok.sequence);
                s.add(tok.sequence);

            }
            // System.out.println("stack "+s);
            callParser();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    private static void callParser() {
        System.out.println("s: " + s);
        String demo = "", demo2;
        int implcount = 0;
        for (Iterator<String> iter = s.iterator(); iter.hasNext();) {
            demo = iter.next();
            iter.remove();
            if (demo.equals("#"))
                implcount++;
            if (demo.equals("~"))
                negcount++;
            if (demo.equals("&"))
                andcount++;
            if (demo.equals("|"))
                orcount++;

            if (Character.isUpperCase(demo.charAt(0))) {
                for (;;) {
                    if (iter.hasNext())
                        ;
                    {
                        demo2 = iter.next();

                        iter.remove();
                        if (demo2.equals(")")) {
                            demo += demo2;
                            break;
                        } else {
                            demo += demo2;
                        }
                        // iter.remove();
                    }

                }
                pred.add(demo);
                // System.out.println("demo :"+demo);
            } else {
                pred.add(demo);

                // System.out.println("demo :"+demo);
            }
        }
        System.out.println("pred: " + pred);
        if (implcount > 0) {
            removeImplication(implcount);
        } else if (negcount > 0) {
            resolveNegation();
        } else if (andcount > 0 && orcount != 0) {
            // andDistribution();
            //System.out.println("here" + orcount);
            printPreFix();
        } else if (orcount == 0 && andcount > 0) {
            separateAND(pred);
        } else
            removeExtraParanthesis();
    }

    private static void removeImplication(int count) {
        // System.out.println(count);
        String lhs = "";
        int brace = 0;
        int l = 0;
        // String temp="";
        while (count > 0) {
            brace = 0;
            for (Iterator<String> iter = pred.iterator(); iter.hasNext();) {

                lhs = iter.next();
                // iter.remove();
                if (lhs.equals("#")) {
                    // System.out.println("here");
                    int index1 = pred.indexOf(lhs);
                    pred.remove(index1);
                    pred.add(index1, "|");
                    // System.out.println("pred hash : "+pred);
                    if (pred.get(index1 - 1).equals(")")) {
                        brace++;
                        l = index1 - 2;
                        // System.out.println("brace:"+brace);
                        while (brace > 0) {

                            if (pred.get(l).equals(")"))
                                brace++;
                            else if (pred.get(l).equals("("))
                                brace--;
                            // System.out.println("Brace:"+brace+l+pred.get(l));
                            l--;
                        }
                        pred.add(l + 1, "~");
                        negcount++;
                    } else {
                        pred.add(index1 - 1, "~");
                        negcount++;
                    }
                    break;
                }

            }

            // System.out.println("temp: "+temp);
            count--;
        }
        System.out.println("implied pred : " + pred);
        resolveNegation();
    }

    private static void resolveNegation() {
        String neg = "";
        int brace = 0, l = 0;
        // System.out.println("count: "+negcount);
        while (negcount > 0) {

            for (Iterator<String> iter = pred.iterator(); iter.hasNext();) {
                neg = iter.next();
                // System.out.println("neg: "+neg);
                if (neg.equals("~")) {
                    int index1 = pred.indexOf(neg);

                    if (pred.get(index1 + 1).equals("(")) {
                        // System.out.println("before pred : "+pred);

                        brace++;
                        l = index1 + 2;
                        // System.out.println("brace:"+brace + " "+ l);
                        while (brace > 0) {
                            // System.out.println(pred.get(l));
                            if (pred.get(l).equals("("))
                                brace++;
                            else if (pred.get(l).equals(")"))
                                brace--;
                            else if (pred.get(l).equals("&")) {
                                pred.remove(l);
                                pred.add(l, "|");
                                andcount--;
                            } else if (pred.get(l).equals("|")) {
                                pred.remove(l);
                                pred.add(l, "&");
                                andcount++;
                            } else if (!pred.get(l).equals("~")) {
                                String temp = pred.get(l);
                                String s = String.valueOf(temp.charAt(0));
                                if (s.equals("~")) {
                                    temp = temp.substring(1);
                                } else {
                                    temp = ("~" + temp);
                                }
                                pred.remove(l);
                                pred.add(l, temp);

                            }
                            l++;
                        }
                        pred.remove(index1);

                    } else {
                        // System.out.println("here2");
                        String temp2 = (pred.get(index1 + 1));
                        // System.out.println("temp2: " + pred.get(index1 + 1));
                        if (!(temp2.equals(")") && temp2.equals("(") && temp2.equals("&") && temp2.equals(")")
                                && temp2.equals("~"))) {
                            if (neg.equals("~")) {
                                // System.out.println("here3");
                                String s = String.valueOf(temp2.charAt(0));
                                // System.out.println("temp2chr1: " + s);
                                if (s.equals("~")) {
                                    temp2 = temp2.substring(1);
                                    // System.out.println("temp2 if: " + temp2);
                                    if (temp2.equals("")) {
                                        // System.out.println("here4");
                                        pred.remove(index1);
                                        pred.remove(index1);
                                        break;
                                    }

                                } else {
                                    temp2 = ("~" + temp2);
                                }
                            }
                        } else {
                            temp2 = ("~" + temp2);
                            /*
                             * pred.remove(index1 + 1); pred.add(index1 + 1,
                             * temp2); pred.remove(index1);
                             */
                        }

                        pred.remove(index1 + 1);
                        pred.add(index1 + 1, temp2);
                        pred.remove(index1);
                    }
                    break;
                }

            }

            negcount--;

        }
        System.out.println("neg pred : " + pred);
        if (andcount > 0)
            // andDistribution();
            printPreFix();

        else
            // printPreFix();
            removeExtraParanthesis();

    }
    
    private static void printPreFix() {
        // System.out.println("here");
        Stack<String> stack = new Stack<String>();
        String demo;
        LinkedList<String> prefix = new LinkedList<String>();

        for (int i = pred.size() - 1; i >= 0; i--) {
            // System.out.println("prefix: "+prefix);
            // System.out.println("stack: "+stack);
            demo = pred.get(i);
            //
            //System.out.println("demo: "+demo);
            if (Character.isLetter(demo.charAt(0)) || (demo.charAt(0) == '~' && Character.isLetter(demo.charAt(1)))) {
                prefix.addFirst(pred.get(i));// + prefix;

            } else if (demo.equals("(")) {
                prefix.addFirst(stack.pop() + "");
            } else if (demo.equals(")")) {
                continue;
            } else {
                stack.push(pred.get(i));
            }
            // System.out.println("pefix: "+prefix);
        }
        System.out.println("prefix with brackets: " + prefix);
        homework tree = new homework();
        LinkedList<String> in = pred;
        LinkedList<String> pre = prefix;
        for (Iterator<String> iter = in.iterator(); iter.hasNext();) {
            demo = iter.next();
            if (demo.equals("(") || demo.equals(")")) {
                iter.remove();
            }
        }
        for (Iterator<String> iter = pre.iterator(); iter.hasNext();) {
            demo = iter.next();
            if (demo.equals("(") || demo.equals(")")) {
                iter.remove();
            }
        }
        System.out.println("inorder temp: " + in);
        System.out.println("prefix temp: " + prefix);
        //int len = in.size();
        // System.out.println("here");
        ParseTree root = tree.buildTree(pre);// 0, len - 1);
        // System.out.
        //System.out.println("here");
        System.out.println("Inorder traversal of constructed tree is : ");
        printInorder(root);
        System.out.println();
        System.out.println("Preorder traversal of constructed tree is : ");

        printpreorder(root);
        //
        ParseTree transformedRoot = root;
        System.out.println("we r done");
        while (true) {
        	string2= "";
        	string1= "";
            transformedRoot = reconstruct(root);
            //append(trasnformedRoot);
            //append(root);
            printInorder(transformedRoot);
            System.out.println("TR2/n");
            if (root.equals(transformedRoot)) {
                break;
            }
            root = transformedRoot;
        }
        //homework.printInorder(reconstruct(root));
        homework.treeKBAddInorder(root);
        // System.out.println("we r done ddd");
        System.out.println();
        System.out.println("Tree KB: " + treekb);
        separateAND(treekb);
        System.out.println();
    }

    private static void separateAND(LinkedList<String> andclause) {
        String tempclause = "";
        String demo = "";
        // System.out.println("clause:" + andclause);
        for (int i = 0; i < andclause.size(); i++) {
            demo = andclause.get(i);
            // System.out.println("demo: "+demo);
            if (demo.equals("(") || demo.equals(")")) {
                // System.out.println("andclause: "+andclause);
                andclause.remove(i);
            }
        }
        andclause.add("*");
        // System.out.println("andclause: "+andclause);
        for (int i = 0; i <= andclause.size(); i++) {
            // System.out.println("iter: "+i+" "+andclause.get(i));
            if ((!andclause.get(i).equals("&")) && (!andclause.get(i).equals("*"))) {
                // System.out.println("temp");
                tempclause += andclause.get(i);
                // System.out.println("tempclause 1: "+tempclause);
            } else {
                // System.out.println("tempclause 2: "+tempclause);
                // System.out.println(tempclause.length());
                if (tempclause.length() > 0) {
                    cnfkb.add(tempclause);
                    tempclause = "";

                }
            }
            //System.out.println("cnf: " + cnfkb);
        }
        // System.out.println("cnf kb: "+cnfkb);
        // System.out.println("cnf: "+cnfkb);
        // System.exit(0);

    }

    private static void treeKBAddInorder(ParseTree node) {
        // System.out.println("herehfkjhh");
        if (node == null)
            return;
        treeKBAddInorder(node.left);
        // System.out.print("value: "+node.value + " ");
        treekb.add(node.value + "");
        treeKBAddInorder(node.right);

    }

    private ParseTree buildTree(LinkedList<String> prefix) {
        /*
         * def insert
         *
         * Insert each token in the expression from left to right:
         *
         * (0) If the tree is empty, the first token in the expression (must be
         * an operator) becomes the root
         *
         * (1) Else if the last inserted token is an operator, then insert the
         * token as the left child of the last inserted node.
         *
         * (2) Else if the last inserted token is an operand, backtrack up the
         * tree starting from the last inserted node and find the first node
         * with a NULL right child, insert the token there. **Note**: don't
         * insert into the last inserted node. end def
         */

        ParseTree root = new ParseTree();
        ParseTree pt = new ParseTree();
        pt.value = (String) prefix.get(0);
        root = pt;
        if (prefix.size() > 0) {
            String demo;
            demo = (String) prefix.get(1);
            // System.out.println("hi"+ demo);
            ParseTree pt2 = new ParseTree();
            pt2.value = demo;
            pt.left = pt2;
            // System.out.println("hi");
            pt2.parent = pt;
            String demo2;
            pt = pt.left;
            // System.out.println(prefix.size());
            // System.out.println("left: "+pt.getLeft().value+" right:
            // "+pt.getRight().value);
            if (prefix.size() > 1) {
                for (int i = 2; i < prefix.size(); i++) { // System.out.println("hello");
                    // System.out.println("left:
                    // "+pt.left.value+"
                    // right:
                    // "+pt.right.value);
                    // System.out.println(i);
                    ParseTree pt3 = new ParseTree();
                    demo = (String) prefix.get(i);
                    demo2 = (String) prefix.get(i - 1);
                    if (demo2.equals("|") || demo2.equals("&")) {

                        pt3.value = demo;
                        pt.left = pt3;
                        pt3.parent = pt;
                        pt = pt.left;
                        // System.out.println(i);
                    } else {

                        while (true) {
                            pt = pt.parent;
                            // System.out.println("he");
                            // pt = pt.parent;

                            // System.out.println("hi " +pt3.parent.value);
                            if (pt.right == null) {
                                //System.out.println("ja " + pt.value + " " + x);
                                break;
                            } else
                                continue;
                            //pt = pt.parent;
                            //x++;
                        }

                        // System.out.println("gjgj "+i);
                        pt3.value = demo;
                        pt3.parent = pt;
                        pt.right = pt3;
                        pt = pt.right;
                        // pt=pt.left;

                    }
                }
            }
        }
        // System.out.println("root: "+root.value);
        //printpreorder(root);
        //System.out.println("Inorder:");
        //printInorder(root);
        // preorder(reconstruct(root));
        // System.out.println("root: "+root.value);
        return root;
    }

    static void printInorder(ParseTree node) {
        if (node == null)
            return;
        printInorder(node.left);
        System.out.print(node.value + " ");
        printInorder(node.right);
    }

    static void printpreorder(ParseTree node) {
        if (node == null)
            return;
        System.out.print(node.value + " ");
        printpreorder(node.left);
        printpreorder(node.right);
    }

    private static ParseTree reconstruct(ParseTree root) {
        //System.out.println();
        //System.out.println("root value: "+root.value);
        if (root.value.equals("|") || root.value.equals("&")) {
            // System.out.println("Root left: "+root.left.value);
            ParseTree left = reconstruct(root.left);
            //System.out.println("left: "+left.value);
            //System.out.println("Root right: "+root.right.value);
            ParseTree right = reconstruct(root.right);
            //System.out.println("right: "+right.value);
            //System.out.println("left: "+right);
            root.left = left;
            root.right = right;
            ParseTree temp = distribute(root, left, right);
            //System.out.println("temp tree: ");
            //printInorder(temp);
            //System.out.println();
            return temp;
        }
        return root;
    }

    private static ParseTree distribute(ParseTree parent, ParseTree leftChild, ParseTree rightChild) {
        //System.out.println("distribute: " + parent.value + " " + leftChild.value + " " + rightChild.value);
       /* if (!(leftChild.value.equals("|")) && !(leftChild.value.equals("&")) && !(rightChild.value.equals("|"))
                && !(rightChild.value.equals("&"))) {
            System.out.println("There is nothing to do");
            return parent;
        }*/
        if (parent.value.equals("|")) {
            //System.out.println("parent OR");

            /*
             * Apply distributive laws and return the new branch for example:
             */
            if (leftChild.value.equals("&"))
                // if ( (leftChild instanceof operator) || !(rightChild instanceof
                // Operator) ){
            {
                ParseTree operatorLeftChild = leftChild.getLeft();
                ParseTree operatorRightChild = leftChild.getRight();

                /*
                 * Applying distributive laws: rightChild OR (operatorLeftChild
                 * AND operatorRightChild) -> (rightChild OR operatorLeftChild)
                 * AND (rightChild OR operatorRightChild)
                 */
                //System.out.println("left and");
                //printInorder(parent);

                //System.out.println("left and");
                /*System.out.println("op lft: "+operatorLeftChild.value);
                System.out.println("mid op left ");
                printInorder(operatorLeftChild);
                System.out.println("op rght: "+operatorRightChild.value);
                */ParseTree newBranch = new ParseTree();
                newBranch.value = "&";
                /* new Left child */
                ParseTree newLeftChild = new ParseTree();
                newLeftChild.value = "|";
                newLeftChild.setRight(rightChild);
                newLeftChild.setLeft(operatorLeftChild);
                /* new Richt Child */
                ParseTree newRightChild = new ParseTree();
                newRightChild.value = "|";
                newRightChild.setRight(rightChild);
                newRightChild.setLeft(operatorRightChild);
                /* Setting the new Branch */
                newBranch.setLeft(newLeftChild);
                newBranch.setRight(newRightChild);
                /*
                System.out.println("new Branch Left: " + newBranch.value);
                System.out.println("new left: " + newBranch.left.value);
                System.out.println("new right: " + newBranch.right.value);
                printInorder(newBranch);
                System.out.println();
                */
                return newBranch;

            }
            else if (rightChild.value.equals("&")) {
                ParseTree operatorLeftChild = rightChild.left;
                ParseTree operatorRightChild = rightChild.right;

                System.out.println("Right and ");
                ParseTree newBranch = new ParseTree();
                newBranch.value = "&";
                /* new Left child */
                ParseTree newLeftChild = new ParseTree();
                newLeftChild.value = "|";
                newLeftChild.setLeft(leftChild);
                newLeftChild.setRight(operatorLeftChild);
                /* new Right Child */
                ParseTree newRightChild = new ParseTree();
                newRightChild.value = "|";
                newRightChild.setLeft(leftChild);
                newRightChild.setRight(operatorRightChild);
                /* Setting the new Branch */
                newBranch.setLeft(newLeftChild);
                newBranch.setRight(newRightChild);

                
                //System.out.println("new Branch Right: " + newBranch.value);
                //System.out.println("new left: " + newBranch.left.value);
                //System.out.println("new Right: " + newBranch.right.value);
                //System.out.println();
                //printInorder(newBranch);
                //System.out.println();
                 
                return newBranch;

            }
        }
        return parent;
    }

    private static void removeExtraParanthesis() {
        String demo = "";
        for (Iterator<String> iter = pred.iterator(); iter.hasNext();) {
            demo = iter.next();
            if (demo.equals("(") || demo.equals(")")) {
                iter.remove();
            }
        }
        System.out.println("after removing extra paranthesis" + pred);

        String demostr ="";
        for(int x=0; x<pred.size(); x++)
            demostr+=pred.get(x);
        //System.out.println("demostr: "+demostr);
        cnfkb.add(demostr + "");
        System.out.println("cnf kb: " + cnfkb);
    }

    private static String Negate(String string) {

        String s = String.valueOf(string.charAt(0));
        if (s.equals("~")) {
            return string.substring(1);
        } else
            return ('~' + string);

    }

}
 	