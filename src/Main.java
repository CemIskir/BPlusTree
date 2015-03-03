import static org.junit.Assert.assertEquals;


public class Main {

   /**
    * @param args
    */
   public static void main(String[] args) {
      // TODO Auto-generated method stub
      Integer primeNumbers[] = new Integer[] { 2, 4, 5, 7, 8, 9, 10, 11, 12, 13, 14,
         15, 16,  17,18};
     String primeNumberStrings[] = new String[primeNumbers.length];
     for (int i = 0; i < primeNumbers.length; i++) {
       primeNumberStrings[i] = (primeNumbers[i]).toString();
     }
     BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
     Utils.bulkInsert(tree, primeNumbers, primeNumberStrings);

     String test = Utils.outputTree(tree);
     System.out.println(test);
     /*
     
     tree.insert(19,"19");
     
     String test1 = Utils.outputTree(tree);
     System.out.println(test1);
     
     tree.delete(19);
     */
     
     tree.delete(8);
     
     String test2 = Utils.outputTree(tree);
     System.out.println(test2);
     
     

     /*
     String correct = "@10/@%%@5/8/@@12/14/@%%[(2,2);(4,4);]#[(5,5);(7,7);]#[(8,8);(9,9);]$[(10,10);(11,11);]#[(12,12);(13,13);]#[(14,14);(15,15);(16,16);]$%%";
     assertEquals(test, correct);
     for(Integer i=1;i<17;i++)
     {
        String k = tree.search(i);
     System.out.println(k+"==");
     }*/
   }
}
