/*
 * This B+ Tree Solution is written by Cem Iskir, and Sairam Sanapureddy
 * */
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

/**
 * BPlusTree Class Assumptions: 1. No duplicate keys inserted 2. Order D:
 * D<=number of keys in a node <=2*D 3. All keys are non-negative
 */
//Cem-Changed BPlusTreeEmpty to BPlusTree
public class BPlusTree<K extends Comparable<K>, T> {

	public Node<K,T> root;
	public static final int D = 2;

	
	/*Returns the leaf Node that the given key existed on*/
	private Node<K,T> searchLeaf(K key)
	{
      int index = 0;
      Node<K,T> current = root;
      
      while(!current.isLeafNode)
      {
         index = 0;
         
         //Traversing through the keys of the current node
         for(K curkey : current.keys)
         {
            // curkey > key
            if(curkey.compareTo(key) > 0)
               break;
            else
               ++index;
         }
         
         if(current.keys.size() > 0)
            current = ((IndexNode<K,T>)current).children.get(index);         
      }
      return current;
	}
	
	
	
	
	
	/**
	 * Searchs the value for a specific key
	 * 
	 * @author Cem
	 * @param key
	 * @return value
	 */
	public T search(K key) {

	   //Getting the leaf that key should be on (key may not be existed)
      Node<K,T> current = searchLeaf(key);

      //If given node is valid, and no error occured
      if(current!=null)
      {
         //will be used for accessing the value
         int index = 0;
         
         for(K curkey : current.keys)
         {
            // curkey == key
            if(curkey.compareTo(key) == 0)
               return ((LeafNode<K,T>)current).values.get(index);
            index++;
         }        
      }
      
		return null;
	}

   /**
	 * TODO Insert a key/value pair into the BPlusTree
	 * 
	 * @param key
	 * @param value
	 */
	public void insert(K key, T value) {
	   //If parameters is not valid
      if(key == null | value == null)
      {
         System.err.println("Given key and/or value to insert function is empty");
         return;
      }
   
	   //First entry to be inserted
	   if(root == null)
	      root = new LeafNode<K,T>(key,value);
      //all other cases where there is entry in the tree, and given key-value pair is valid
	   else
	      insertMe(key,value);
	}

	
	public void addSortedToKeyOrValue(Node<K,T> current, K key, T value) {
	   //will be used for accessing the value
      int index = 0;
         
      for(K curkey : current.keys)
      {
         // curkey > key
         if(curkey.compareTo(key) > 0)
            break;
         else
            ++index;
      }
	   current.keys.add(index, key);
	   ((LeafNode<K,T>)current).values.add(index, value);
	}
	
	  public void addSortedToIndexNode(Node<K,T> current, K key, Node<K,T> childNode) {
	      //will be used for accessing the value
	      int index = 0;
	         
	      for(K curkey : current.keys)
	      {
	         // curkey > key
	         if(curkey.compareTo(key) > 0)
	            break;
	         else
	            ++index;
	      }
	      current.keys.add(index, key);
	      ((IndexNode<K,T>)current).children.add(index+1, childNode);
	   }
	
	
   public void insertMe(K key, T value) {
      int index = 0;
      Node<K,T> current = root;
      ArrayList<Node<K,T>> parentNodes = new ArrayList<Node<K,T>>();
      
      //Go till the leafNode
      while(!current.isLeafNode)
      {

         //PARENT NODE LOGIC
         //Appending the parent nodes
         if(parentNodes.size() == 0)
            parentNodes.add(current);
         //There is at least on item in the list
         else
         {
            //The variables below holds the number of elements in each 
            int current_key_size = current.keys.size();
            
            //if there is space in the node, we don't need to split it so remove all the previous parents(we don't need to hold them anymore)
            if(current_key_size < 2*D)
               parentNodes.clear();
            parentNodes.add(current);
         }

         index = 0;

         //Traversing through the keys of the current node
         for(K curkey : current.keys)
         {
            // curkey > key
            if(curkey.compareTo(key) > 0)
               break;
            else
               ++index;
         }
         
         if(current.keys.size() > 0)
            current = ((IndexNode<K,T>)current).children.get(index);
      }
      
      addSortedToKeyOrValue(current,key,value);
      
      //Overflow occured
      if(current.isOverflowed())
      {

         //current holds left child
         Node<K,T> leftPart = current;

         Map.Entry<K, Node<K,T>> rightPartPair = splitLeafNode((LeafNode<K,T>)leftPart);
         K middlekey = rightPartPair.getKey();
         //right child
         Node<K,T> rightPart = rightPartPair.getValue();
         
         
         if(parentNodes.size() > 0)
         {
            int number_of_parents = parentNodes.size();

            while(number_of_parents > 0)
            {

               //Current parent
               leftPart = parentNodes.get(number_of_parents-1);
               
               //add the new key & children to the current parent
               addSortedToIndexNode(leftPart,middlekey,rightPart);
               
               if(leftPart.isOverflowed())
               {
                  //Returning the entry - removing the middle element which is the key
                  rightPartPair = splitIndexNode((IndexNode<K,T>)leftPart);
                  rightPart = rightPartPair.getValue();
                  middlekey = rightPartPair.getKey();
                  
                  
                  //(indexLeaf.keys.remove(D),newNode);
                  //SPLIT index
                  //set newkey and node
               }
               else
               {
                  rightPartPair = null;
                  rightPart = null;
                  middlekey = null;
               }

               //reducing the number of parents one more level
               --number_of_parents;                              
               //leftPart = parentNodes.get(number_of_parents-1);
            }
            
            //if root, update root- get(0) size check size check
            if( middlekey != null)
            {
               Node<K,T> indexRoot = new IndexNode<K, T>(middlekey, leftPart, rightPart);
               root = indexRoot;
            }
            
         }
         //No previous Index Node
         else
         {
            //In case there was no IndexNode before, create a new one, and assign it as a root
            Node<K,T> indexRoot = new IndexNode<K, T>(middlekey, leftPart, rightPart);
            root = indexRoot;
         }
      }
   }

	
	
	
	
	/**
	 * Split a leaf node and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @author Cem
	 * @param leaf
	 * @return the key/node pair as an Entry
	 */
	//Cem-Splits the given leaf
	public Entry<K, Node<K,T>> splitLeafNode(LeafNode<K,T> leaf) {
	   if(leaf != null)
	   {	
	      /*keys and values that will be inserted to the new LeafNode*/
         ArrayList<K> newKeys = new ArrayList<K>();
         ArrayList<T> newValues = new ArrayList<T>();
         
	      //Insert into the specific position
         int index = D;
         //How many items to copy
         int copyItems = leaf.keys.size();
         
         while(index < copyItems)
         {
            newValues.add(leaf.values.remove(D));
            newKeys.add(leaf.keys.remove(D));
            ++index;
         }
	      
         //Creating the LeafNode
         Node<K,T> newNode = new LeafNode<K,T>(newKeys, newValues);
         newNode.isLeafNode = true;
         
         //Setting the previous and next values
         LeafNode<K,T> next = leaf.nextLeaf;
         
         ((LeafNode<K,T>)newNode).nextLeaf = next;
         ((LeafNode<K,T>)newNode).previousLeaf = leaf;
         
         if(next != null)
            next.previousLeaf = ((LeafNode<K,T>)newNode);
         leaf.nextLeaf = ((LeafNode<K,T>)newNode);
         
         Map.Entry<K, Node<K,T>> entry = new AbstractMap.SimpleEntry<K, Node<K,T>>(newNode.keys.get(0),newNode);
         return entry;
	   }
	   else
	      return null;
	}

	/**
	 * split an indexNode and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @author Cem
	 * @param index
	 * @return new key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitIndexNode(IndexNode<K,T> indexLeaf) {
      if(indexLeaf != null)
      {  
         //keys and children that will be inserted to the new LeafNode
         ArrayList<K> newKeys = new ArrayList<K>();
         ArrayList<Node<K,T>> newChildren = new ArrayList<Node<K,T>>();
         
         //Insert into the specific position
         int index = D + 1;
         //How many items to copy
         int copyItems = indexLeaf.keys.size();
         
         while(index < copyItems)
         {
            newChildren.add(indexLeaf.children.remove(D+1));
            newKeys.add(indexLeaf.keys.remove(D+1));
            ++index;
         }
         newChildren.add(indexLeaf.children.remove(D+1));
         
         //Creating the LeafNode
         Node<K,T> newNode = new IndexNode<K,T>(newKeys, newChildren);
         newNode.isLeafNode = false;
         
         //Returning the entry - removing the middle element which is the key
         Map.Entry<K, Node<K,T>> entry = new AbstractMap.SimpleEntry<K, Node<K,T>>(indexLeaf.keys.remove(D),newNode);
         return entry;
         
      }
      else
         return null;
	}
	
	//Helper method to get sibling. It gets left sibling by default, right if left doesn't exist
	private Node<K,T> getSibling(Node<K,T> current, IndexNode<K,T> parent){
		
		int index = parent.children.indexOf(current);
		if(index == 0){
			return parent.children.get(index+1);
		}
		else{
			return parent.children.get(index - 1);
		}
	}
	
	
	
	
	private Node<K,T> delete_recur(IndexNode<K,T> parent, Node<K,T> cur_node, K Key, Node<K,T> oldchild){
		
		if(!cur_node.isLeafNode){
			//Handle Non-Leaf case here
			IndexNode<K,T> N= (IndexNode<K,T>) cur_node;
			
			//Find pointer to the choose subtree
			int i;
			for(i=0; i < cur_node.keys.size();i++)
			{
				if(Key.compareTo(cur_node.keys.get(i)) < 0 || i > cur_node.keys.size())
					break;
			}
			
			//recursive delete
			oldchild = delete_recur(N, N.children.get(i),Key,oldchild);
			
			//usual case - child not deleted
			if(oldchild == null)
				return null; //delete doesn't go further
			else {
				//discard childnode from N
				if(parent == null && N.keys.size()==0){
					N.children.remove(oldchild);
					this.root = N.children.get(0);
					return null;
				}
				
				N.keys.remove(N.children.indexOf(oldchild)-1);
				N.children.remove(oldchild);
				
				if(parent == null && N.keys.size()==0){
					this.root = N.children.get(0);
					return null;		
				}
				
				if(!N.isUnderflowed()){
					return null;
				}
				else{
					
				if(parent != null){
					IndexNode<K,T> Insibling = (IndexNode<K,T>)getSibling(N,parent);
					int num_child_N = N.children.size();
					int num_child_sibling = Insibling.children.size();
					int child_split = (num_child_N + num_child_sibling)/2;
					int parent_key_index;
					
					//Check if there is room for merge
					if(Insibling.keys.size()+N.keys.size() < 2*D)
					{ //Merge
						
						
						if(parent.children.indexOf(N) > parent.children.indexOf(Insibling)){
							//Node on left is Insibling
							
							oldchild = parent.children.get(parent.children.indexOf(N));
							
							//pull splitting key from parent down onto node on left
							Insibling.keys.add(parent.keys.remove(parent.children.indexOf(N)-1));
							
							//move all entries from right node to node on left
							Insibling.keys.addAll(N.keys);
							Insibling.children.addAll(N.children);
							
							//Discard empty Node
							N.keys.clear();
							N.children.clear();
							
						}
						else{
							//Node on left is N
							
							oldchild = parent.children.get(parent.children.indexOf(Insibling));
							
							//pull splitting key from parent down onto node on left
							N.keys.add(parent.keys.remove(parent.children.indexOf(Insibling)-1));
							
							//move all entries from right node to node on left
							N.keys.addAll(Insibling.keys);
							N.children.addAll(Insibling.children);
							
							//Discard empty Node
							Insibling.keys.clear();
							Insibling.children.clear();
						}
					
						return oldchild;
					}
					else{
						//Redistribute
						
						if(parent.children.indexOf(N) > parent.children.indexOf(Insibling)){
							//redistribute from left to right through parent
							
							
							while(Insibling.children.size() > child_split){
								N.children.add(0,Insibling.children.remove(Insibling.children.size()-1));
							}
							
							parent_key_index = parent.children.indexOf(Insibling)-1;
							N.keys.add(0,parent.keys.get(parent_key_index));
							while(Insibling.keys.size() > D+1){
								N.keys.add(0,Insibling.keys.remove(Insibling.keys.size()-1));
							}
							parent.keys.set(parent_key_index,Insibling.keys.remove(Insibling.keys.size()-1));
							
						}
						else{
							//redistribute from right to left through parent
							while(Insibling.children.size() > child_split){
								N.children.add(Insibling.children.remove(0));
							}
							parent_key_index = parent.children.indexOf(Insibling)-1;
							N.keys.add(parent.keys.get(parent_key_index));
							while(Insibling.keys.size() > D+1){
								N.keys.add(Insibling.keys.remove(0));
							}
							
							parent.keys.set(parent_key_index,Insibling.keys.remove(0));
							
						}

						return null;
					}
				}
				
				return null;	
			}
		}
			
		}
		else{
			//Handle Leaf-Node case here
			LeafNode<K,T> L = (LeafNode<K,T>)cur_node;
			int key_index = L.keys.indexOf(Key);
			L.keys.remove(key_index);
			L.values.remove(key_index);
			
			
			if(!L.isUnderflowed())
			{
				//usual case
				return null;
			}
			else{
				//Handle leaf underflow
				
				//get a sibling of L
				LeafNode<K,T> Leafsibling = (LeafNode<K,T>)getSibling(L,parent);
				
				//Check if there is room for merge
				if(Leafsibling.keys.size()+L.keys.size() <= 2*D)
				{ //Merge
					
					if(parent.children.indexOf(L) > parent.children.indexOf(Leafsibling)){
						//Node on left is Leafsibling
						oldchild = parent.children.get(parent.children.indexOf(L));	
						
						//move all entries from right node to node on left
						Leafsibling.keys.addAll(L.keys);
						Leafsibling.values.addAll(L.values);
						
						//Discard empty Node and adjust sibling pointers
						L.keys.clear();
						L.values.clear();
						Leafsibling.nextLeaf = L.nextLeaf;
						if(L.nextLeaf != null){
							L.nextLeaf.previousLeaf = Leafsibling;
						}
					}
					else{
						//Node on left is L
						oldchild = parent.children.get(parent.children.indexOf(Leafsibling));
						
						//move all entries from right node to node on left
						L.keys.addAll(Leafsibling.keys);
						L.values.addAll(Leafsibling.values);
						
						//Discard empty Node  and adjust sibling pointers
						Leafsibling.keys.clear();
						Leafsibling.values.clear();
						L.nextLeaf = Leafsibling.nextLeaf;
						if(Leafsibling.nextLeaf != null){
							Leafsibling.nextLeaf.previousLeaf = L;
						}
					}
					return oldchild;			
			
				}
				else{
					//Redistribute
					int half = (L.keys.size()+ Leafsibling.keys.size())/2;
					
					//redistribute evenly
					while(L.keys.size() < half){
						
						//when L is on the left
						if(parent.children.indexOf(L) < parent.children.indexOf(Leafsibling)){
							L.keys.add(Leafsibling.keys.remove(0));
							L.values.add(Leafsibling.values.remove(0));
						}
						else{
						 //L is on right
							L.keys.add(0,Leafsibling.keys.remove(Leafsibling.keys.size()-1));
							L.values.add(0,Leafsibling.values.remove(Leafsibling.values.size()-1));
						}
						
					}
					
					//Find entry in parent to node on right
					int indexR;
					if(parent.children.indexOf(L) < parent.children.indexOf(Leafsibling))
						indexR = parent.children.indexOf(Leafsibling);
					else
						indexR = parent.children.indexOf(L);
					
					
					//replace key value in parent by new low-key value
					parent.keys.set(indexR-1,parent.children.get(indexR).keys.get(0));
					
					return null;
				}
				
			}
			
		}
	
	}

	/**
	 * TODO Delete a key/value pair from this B+Tree
	 * 
	 * @param key
	 */
	public void delete(K key) {
		
		if(search(key) != null){
			//Delete only when a Key has a particular value
			if(root.isLeafNode){
				
				LeafNode<K,T> L = (LeafNode<K,T>)root;
				int key_index = L.keys.indexOf(key);
				L.keys.remove(key_index);
				L.values.remove(key_index);
				
			}
			else{
				IndexNode<K,T> N = (IndexNode<K,T>)root;
				delete_recur(null,N,key,null);
			}
		}
		

	}

	/**
	 * TODO Handle LeafNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleLeafNodeUnderflow(LeafNode<K,T> left, LeafNode<K,T> right,
			IndexNode<K,T> parent) {
		return -1;

	}

	/**
	 * TODO Handle IndexNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleIndexNodeUnderflow(IndexNode<K,T> leftIndex,
			IndexNode<K,T> rightIndex, IndexNode<K,T> parent) {
		return -1;
	}

}
