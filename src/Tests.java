import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;

public class Tests {

   /*
    * Tests insertion and deletion when no index node existed(all nodes are leafnode)
    * */
   @Test
   public void custom1(){
      Character alphabet[] = new Character[] { 'c','a'};
      String alphabetStrings[] = new String[alphabet.length];
      for (int i = 0; i < alphabet.length; i++) {
        alphabetStrings[i] = (alphabet[i]).toString();
      }
       BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
       Utils.bulkInsert(tree, alphabet, alphabetStrings);

       String test = Utils.outputTree(tree);
       String correct = "[(a,a);(c,c);]$%%";
       
       assertEquals(correct, test);

       tree.delete('a');
       tree.delete('b');
       tree.delete('c');
       test = Utils.outputTree(tree);
       correct = "[]$%%";  
       assertEquals(correct, test);

       tree.insert(new Character('a'),"a");
       tree.insert(new Character('b'),"b");
       tree.insert(new Character('c'),"c");
       test = Utils.outputTree(tree);
       correct = "[(a,a);(b,b);(c,c);]$%%"; 
       assertEquals(correct, test);       
   }
   
   /*
    * just one index node
    * */
   @Test
   public void custom2(){
      Character alphabet[] = new Character[] { 'a','b','z','m','e','n','f','c','p'};
      String alphabetStrings[] = new String[alphabet.length];
      for (int i = 0; i < alphabet.length; i++) {
        alphabetStrings[i] = (alphabet[i]).toString();
      }
       BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
       Utils.bulkInsert(tree, alphabet, alphabetStrings);

       String test = Utils.outputTree(tree);
       String correct = "@e/m/@%%[(a,a);(b,b);(c,c);]#[(e,e);(f,f);]#[(m,m);(n,n);(p,p);(z,z);]$%%";     
       assertEquals(correct, test);
       
       /*Search test*/
       for(Character k:alphabet)
          tree.search(k);
       
       tree.search(' ');
       
       /*
        * DELETE FROM LEFT CHILD       
       tree.delete('a');
       tree.delete('b');
       test = Utils.outputTree(tree);
       correct = "@m/@%%[(c,c);(e,e);(f,f);]#[(m,m);(n,n);(p,p);(z,z);]$%%";  
       assertEquals(correct, test);
        */
       
       /*
        * DELETE FROM MIDDLE CHILD     
       tree.delete('a');
       tree.delete('f');
       test = Utils.outputTree(tree);
       correct = "@m/@%%[(b,b);(c,c);(e,e);]#[(m,m);(n,n);(p,p);(z,z);]$%%";  
       assertEquals(correct, test);
        */

       /*
        * DELETE FROM RIGHT CHILD  */     
       tree.delete('n');
       tree.delete('z');
       tree.delete('p');
       test = Utils.outputTree(tree);
       correct = "@e/@%%[(a,a);(b,b);(c,c);]#[(e,e);(f,f);(m,m);]$%%";  
       assertEquals(correct, test);
        
       
       for(Character k:alphabet)
          tree.delete(k);
       test = Utils.outputTree(tree);
       correct = "[]$%%";  
       assertEquals(correct, test);

       
       /*Search test in empty btree*/
       for(Character k:alphabet)
          tree.search(k);
       
       tree.search(' ');
       
       //Check insertion after removing all elements
       tree.insert(new Character('a'),"a");
       test = Utils.outputTree(tree);
       correct = "[(a,a);]$%%"; 
       assertEquals(correct, test);       
   }
   
  // add some nodes, see if it comes out right, delete one, see if it's right
	@Test
	public void testHybrid1() {
	  Character alphabet[] = new Character[] { 'a','b','c','d','e','f','g' };
	  String alphabetStrings[] = new String[alphabet.length];
	  for (int i = 0; i < alphabet.length; i++) {
	    alphabetStrings[i] = (alphabet[i]).toString();
	  }
		BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
		Utils.bulkInsert(tree, alphabet, alphabetStrings);

		String test = Utils.outputTree(tree);
		String correct = 
		    "@c/e/@%%[(a,a);(b,b);]#[(c,c);(d,d);]#[(e,e);(f,f);(g,g);]$%%";
		
		
		assertEquals(correct, test);

		tree.delete('a');
		
		test = Utils.outputTree(tree);
		correct = "@e/@%%[(b,b);(c,c);(d,d);]#[(e,e);(f,f);(g,g);]$%%";
		assertEquals(correct, test);

	}

  // add some nodes, see if it comes out right, delete one, see if it's right
  @Test
  public void testHybrid2() {
    Integer primeNumbers[] = new Integer[] { 2, 4, 5, 7, 8, 9, 10, 11, 12, 13, 14,
        15, 16 };
    String primeNumberStrings[] = new String[primeNumbers.length];
    for (int i = 0; i < primeNumbers.length; i++) {
      primeNumberStrings[i] = (primeNumbers[i]).toString();
    }
    BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
    Utils.bulkInsert(tree, primeNumbers, primeNumberStrings);

    String test = Utils.outputTree(tree);
    String correct = "@10/@%%@5/8/@@12/14/@%%[(2,2);(4,4);]#[(5,5);(7,7);]#[(8,8);(9,9);]$[(10,10);(11,11);]#[(12,12);(13,13);]#[(14,14);(15,15);(16,16);]$%%";
    assertEquals(test, correct);

    tree.delete(2);
    test = Utils.outputTree(tree);
    correct = "@8/10/12/14/@%%[(4,4);(5,5);(7,7);]#[(8,8);(9,9);]#[(10,10);(11,11);]#[(12,12);(13,13);]#[(14,14);(15,15);(16,16);]$%%";
    assertEquals(test, correct);

  }
	
	
	// testing proper leaf node merging behaviour
	@Test
	public void testDeleteLeafNodeMerge() {
		Integer testNumbers[] = new Integer[] { 2, 4, 7, 8, 5, 6, 3 };
    String testNumberStrings[] = new String[testNumbers.length];
    for (int i = 0; i < testNumbers.length; i++) {
      testNumberStrings[i] = (testNumbers[i]).toString();
    }
		BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
		Utils.bulkInsert(tree, testNumbers, testNumberStrings);
		
		String test;
		tree.delete(6);
		tree.delete(7);
		tree.delete(8);
		test = Utils.outputTree(tree);

		String result = "[(2,2);(3,3);(4,4);(5,5);]$%%";
		assertEquals(result, test);
	}
	
	
	//Testing appropriate depth and node invariants on a big tree
  @Test
  public void testLargeTree() {
     
     final int TESTSIZE = 100000;//22-23
     
    BPlusTree<Integer, Integer> tree = new BPlusTree<Integer, Integer>();
    ArrayList<Integer> numbers = new ArrayList<Integer>(TESTSIZE);
    for (int i = 0; i<TESTSIZE; i++) {
      numbers.add(i);
    }
    Collections.shuffle(numbers);
    for (int i = 0; i <TESTSIZE; i++) {
      tree.insert(numbers.get(i), numbers.get(i));
    }
    testTreeInvariants(tree);
    assertTrue(treeDepth(tree.root) < 11);
  }

  public <K extends Comparable<K>,T>void testTreeInvariants(BPlusTree<K,T>tree){
    for (Node<K,T> child : ((IndexNode<K,T>)(tree.root)).children)
      testNodeInvariants(child);
  }
  
  public <K extends Comparable<K>, T> void testNodeInvariants(Node<K,T> node) {
	  assertFalse(node.keys.size() > 2 * BPlusTree.D);
    assertFalse(node.keys.size() < BPlusTree.D);
	  if (!(node.isLeafNode))
	    for (Node<K,T> child : ((IndexNode<K,T>)node).children)
	      testNodeInvariants(child);
	}
  
  public <K extends Comparable<K>, T>  int treeDepth(Node<K,T> node) {
    if (node.isLeafNode)
      return 1; 
    int childDepth = 0;
    int maxDepth = 0;
    for (Node<K,T> child : ((IndexNode<K,T>)node).children) {
      childDepth = treeDepth(child);
      if (childDepth > maxDepth)
        maxDepth = childDepth;
    }
    return (1+maxDepth);
  }
}
