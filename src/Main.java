
public class Main {

   /**
    * @param args
    */
   public static void main(String[] args) {
       BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
       tree.insert('c', "c");
       tree.insert('a', "a");

       String test = Utils.outputTree(tree); System.out.println(test);
   }

}
