//Alec Romine
//al411896
//SkipList
//
//
import java.io.*;
import java.util.*;


class Node<T>
{
    public T data;
    public Integer height;
    
    public ArrayList<Node<T>> nextAtHeight;

    Node(Integer height)
    {   
        Integer x = 0;
        
        this.nextAtHeight = new ArrayList<>(height);
        this.height = height;
        this.data = null;
        
        while(x < height)
        {
            this.nextAtHeight.add(null);
            x++;
        }

    }

    Node(Integer height, T data)
    {
        
        int x = 0;
        
        this.nextAtHeight = new ArrayList<>(height);
        this.height = height;
        this.data = data;
        
        while(x < height)
        {
            this.nextAtHeight.add(null);
            x++;
        }
        
    }

    public T value()
    {
        return this.data;
    }
    
    public int height()
    {       
        return this.height;
    }

    public Node<T> next(Integer level)
    {   
        if(level > this.height-1)
            return null;
        
        return this.nextAtHeight.get(level);
    }
    
    //Optional methods
    public void setNext(int level, Node<T> node)
    {
        this.nextAtHeight.set(level, node);
    }
    //will grow node one level
    public void grow()
    {
        this.nextAtHeight.add(null);
        this.height++;
    }
    
    //returns true if it grew a level
    public boolean maybegrow()
    {   
        Random ranNum = new Random();
        boolean grow = ranNum.nextBoolean();
        
        if (grow)
        {
            this.nextAtHeight.add(null);
            this.height++;
        }
        
        return grow;

    }
    //will trim node to specified height
    public void trim(int height)
    {
        while (this.height > height)
        {
            this.nextAtHeight.remove(this.height-1);
            this.height--;
        }
    }
}

public class SkipList<T extends Comparable<T>>
{   
    private Node<T> head;
    private Integer listLength = 0;
    
    SkipList()
    {   
       //initializing head node with one null pointer 
       this.head = new Node<>(1);
       this.listLength = 0;
    }

    SkipList(Integer height)
    {
       //initializing head node with null pointers
       this.head = new Node<>(height);
       this.listLength = 0;
    }

    public int size()
    {
        //This doesnt't include the head node
        return listLength;
    }

    public int height()
    {        
        
        return this.head.height;
    }

    public Node<T> head()
    {
        return this.head;
    }
    //searches for spot to insert, stops at postion just before new node, then inserts
    public void insert(T data)
    { 
        Node<T> current = this.head;
        Integer counter = 0, height, cmp;
        boolean found = false;
        ArrayList<Node<T>> holdMyPointer = new ArrayList<>();
        ArrayList<Node<T>> waitForYourPointer = new ArrayList<>();
        
        //iniizlaze array to null
        while(counter < this.head.height())
        {
            holdMyPointer.add(null);
            waitForYourPointer.add(null);
            counter++;
        }
        
        counter = 0;
        
        //set the height to the height of the head node after poosible growth 
        height = this.head.height-1;
        
        //search for the correct spot for this node
        //only probes data in the node after current at various height
        while(!found)
        {
            //is the next node at this height there
            if(current.next(height) != null)
            {   
                cmp = data.compareTo(current.next(height).value());
                //is the next node bigger than me (data wise)
                //If equal to key, still check lower level and find first occurence at base level
                if(cmp <= 0 && height > 0)
                {
                    //hold pointer to current node so we can upste its pointers later
                    waitForYourPointer.set(height,current); 
                    
                    //store this pointer to potentially add to the new node
                    // depends on the height
                    holdMyPointer.set(height, current.next(height));
                    //lets check the next row down
                    height--;                    
                }
                // Is the next node smaller than me (data wise)
                else if (cmp > 0)
                {   
                    //move to that node
                    current = current.next(height);
                }
                else if(cmp == 0 && height == 0)
                {
                    //found the first node with this value and insert right before it
                    found = true;
                    
                    holdMyPointer.set(height,current.next(height));
                    
                    //hold pointer to current node so we can upste its pointers later
                    waitForYourPointer.set(height,current);
                }
                //If on bottom level and next.data is bigger  
                else if (height == 0 && cmp < 0)
                {
                    //place new node after current node
                    //store pointer of next node for new node
                    holdMyPointer.set(height,current.next(height));
                    
                    //hold pointer to current node so we can update its pointers later
                    waitForYourPointer.set(height,current);                    
                    found = true;
                }
                
            }
            //if pointer at this level is null and not at base height
            else if(height > 0)
            {
                //hold pointer to current node so we can update its pointers later
                waitForYourPointer.set(height,current);  
                holdMyPointer.set(height, null);
                //If the pointer is null and youre not at the bottom row,
                //so check the next row down 
                height--;
            }
            //case of the last null pointer at base height
            // for starting cases and appending tail nodes
            else
            {
                //hold pointer to current node so we can update its pointers later
                waitForYourPointer.set(height,current);  
                //reached the end of the list without finding a node
                //insert at the end of list
                holdMyPointer.set(height, null);
                
                found = true;
            }
            
            
            
        }       
     
        //Create new node of random height
        Node<T> newNode = new Node<>(generateRandomHeight(this.head.height),data);
     
        
        while(counter < newNode.height)
        {            
            //update the pointers at each level of the node with next node at that level
            newNode.setNext(counter,holdMyPointer.get(counter));
            // updating all the nodes behind this one to point here until until this nodes max height 
            waitForYourPointer.get(counter).setNext(counter, newNode);
            
            counter++;
        }
        
        //determine if this insert will cause the length to break height threshhold
        if(this.head.height < getMaxHeight(listLength+1))
        {
            growSkipList();
        }
        
        this.listLength++;
        
        //System.out.println("new node height = "+newNode.height+" and data:"+newNode.data);
        //System.out.println("max height = "+this.head.height);
        //System.out.println("The list length is "+(this.listLength));
        //System.out.println("");
        
    }
    //Insert node with a specific height
    public void insert(T data, int height)
    {
        Node<T> current = this.head;
        Integer counter = 0,tempheight,cmp;
        boolean found = false;
        ArrayList<Node<T>> holdMyPointer = new ArrayList<>();
        ArrayList<Node<T>> waitForYourPointer = new ArrayList<>();
        
        //iniizlaze array to null
        while(counter < this.head.height())
        {
            holdMyPointer.add(null);
            waitForYourPointer.add(null);
            counter++;
        }        
        counter = 0;        
        
        //set the height to the height of the head node after poosible growth 
        tempheight = this.head.height-1;
        
        //search for the correct spot for this node
        //only probes data in the node after current at various height
        while(!found)
        {
            //is the next node at this height there
            if(current.next(tempheight) != null)
            {
                cmp = data.compareTo(current.next(tempheight).value());
                //is the next node bigger than me (data wise)
                if(cmp <= 0 && tempheight > 0)
                {
                    //hold pointer to current node so we can upste its pointers later
                    waitForYourPointer.set(tempheight,current); 
                    
                    //store this pointer to potentially add to the new node
                    // depends on the height
                    holdMyPointer.set(tempheight, current.next(tempheight));
                    //lets check the next row down
                    tempheight--;                    
                }
                // Is the next node smaller than me (data wise)
                else if (cmp > 0)
                {   
                    //move to that node
                    current = current.next(tempheight);
                }
                else if(cmp == 0 && tempheight == 0)
                {
                    //found the first node with this value and insert right before it
                    found = true;
                    
                    holdMyPointer.set(tempheight,current.next(tempheight));
                    
                    //hold pointer to current node so we can upste its pointers later
                    waitForYourPointer.set(tempheight,current);
                }
                //If on bottom level and next.data is bigger  
                else if (tempheight == 0 && cmp < 0)
                {
                    //place new node after current node
                    //store pointer of next node for new node
                    holdMyPointer.set(tempheight,current.next(tempheight));
                    
                    //hold pointer to current node so we can update its pointers later
                    waitForYourPointer.set(tempheight,current);                    
                    found = true;
                }
                
            }
            //if pointer at this level is null and not at base height
            else if(tempheight > 0)
            {
                //hold pointer to current node so we can update its pointers later
                waitForYourPointer.set(tempheight,current);  
                holdMyPointer.set(tempheight, null);
                //If the pointer is null and youre not at the bottom row,
                //so check the next row down 
                tempheight--;
            }
            //case of the last null pointer at base height
            // for starting cases and appending tail nodes
            else
            {
                //hold pointer to current node so we can update its pointers later
                waitForYourPointer.set(tempheight,current);  
                holdMyPointer.set(tempheight, null);
                //reached the end of the list without finding a node
                //insert at the end of list
                
                found = true;
            }          
        }       
     
        //check if height is too large
        height = (height > this.head().height()) ? this.head().height(): height;
        
        //create node of specific height
        Node<T> newNode = new Node<>(height,data);
        //insert new data
        
        while(counter < newNode.height())
        {            
            //update the pointers at each level of the node with next node at that level
            newNode.setNext(counter,holdMyPointer.get(counter));
            // updating all the nodes behind this one to point here until until this nodes max height 
            waitForYourPointer.get(counter).setNext(counter, newNode);
            
            counter++;
        }
        
        //determine if this insert will cause the length to break height threshhold
        if(this.head.height < getMaxHeight(listLength+1))
        {
            growSkipList();
        }
        
        this.listLength++;
                
    }

    public void delete(T data)
    {
        Node<T> current = this.head;
        Integer counter = 0, tempHeight = 0, height,cmp;
        boolean found = false;
        ArrayList<Node<T>> waitForYourPointer = new ArrayList<>();
        
        //iniizlaze array to null
        while(counter < this.head.height())
        {
            waitForYourPointer.add(null);
            counter++;
        }
        
        counter = 0;
        
        //set the height to the height of the head node after poosible growth 
        height = this.head.height-1;
        
        //search for the correct spot for this node
        //only probes data in the node after current at various height
        while(!found)
        {
            //is the next node at this height there
            if(current.next(height) != null)
            {   
                cmp = data.compareTo(current.next(height).value()); 
                //is the next node at this height bigger than me (data wise)
                if(cmp <= 0 && height > 0)
                {
                    //hold pointer to current node so we can update its pointers later
                    waitForYourPointer.set(height,current);
                    
                    //lets check the next row down
                    height--;                    
                }
                // Is the next node smaller than me (data wise)
                else if (cmp > 0)
                {   
                    //move to that node
                    current = current.next(height);
                }
                else if(cmp == 0 && height == 0)
                {
                    //found the first node with this value
                    found = true;
                    
                    tempHeight = current.next(height).height;
                    //hold pointer to current node so we can upste its pointers later
                    waitForYourPointer.set(height,current);
                }
                //If on bottom level and next.data is bigger  
                else if (height == 0 && cmp < 0)
                {           
                    //node doesn't exist
                    return;
                }
                
            }
            //if pointer at this level is null and not at base height
            else if(height > 0)
            {
                //hold pointer to current node so we can update its pointers later
                waitForYourPointer.set(height,current);  
                    
                //If the pointer is null and youre not at the bottom row,
                //so check the next row down 
                height--;
            }
            //case of the last null pointer at base height
            else
            {
               //reached the end of the list without finding a node
               return;
            }    
            
        }
        
        Node<T> temp = current.next(0);
        
        //System.out.println("deleted data: "+temp.value());
        
        //if (found) delete the next node
        //Cut it out of the pointer chain 
        while(counter < tempHeight)
        {
            waitForYourPointer.get(counter).setNext(counter, temp.next(counter));
            counter++;
        }
        
        //determine if this deletion will cause the length to break height threshhold
        if(this.head.height > getMaxHeight(listLength-1))
        {
            trimSkipList();
        }
        
        this.listLength--;
        
        //System.out.println("node deleted");
        //System.out.println("max height = "+this.head.height);
        //System.out.println("The list length is "+this.listLength);
        //System.out.println("");
        
    } 
    
    //returns true on first instance of Node<data>
    //doesn't ensure that it found the first instance of the Node<data>
    public boolean contains(T data)
    {
        Node<T> current = this.head;
        Integer counter = 1,cmp;
        Integer height = this.head.height-1;
        
     
        
        while(counter <= this.listLength)
        {
            counter++;
            //is the next node at this height there
            if(current.next(height) != null)
            {   
                
                cmp = data.compareTo(current.next(height).data);
                //is the next node bigger than me (data wise)
                //and not on base level
                if(cmp < 0 && height > 0)
                {
                    //lets check the next row down
                    height--;                    
                }
                // Is the next node smaller than me (data wise)
                else if (cmp > 0)
                {   
                    //move to that node
                    current = current.next(height);
                }
                else if(cmp == 0)
                {
                    //found a duplicate node
                    return true;
                
                }
                
            }
            //if pointer at this level is null and not at base height
            else if(height > 0)
            {      
                //check the next level down 
                height--;
            }
            //case of the last null pointer at base height
            //node does not exist in the list
            else
            {   
                return false;
            }  
            
        }
        
        
        return false;
    }

    public Node<T> get(T data)
    {
        Node<T> current = this.head;
        Integer height,cmp;
        boolean found = false;
        
        //set the height to the height of the head node after poosible growth 
        height = this.head.height-1;
        
        //search for the correct spot for this node
        //only probes data in the node after current at various height
        while(!found)
        {
            //is the next node at this height there
            if(current.next(height) != null)
            {   
                cmp = data.compareTo(current.next(height).data);
                //is the next node at this height bigger than me (data wise)
                if(cmp <= 0 && height > 0)
                {
                    //lets check the next row down
                    height--;                    
                }
                // Is the next node smaller than me (data wise)
                else if (cmp > 0)
                {   
                    //move to that node
                    current = current.next(height);
                }
                else if(cmp == 0 && height == 0)
                {
                    //found the first node with this value
                    found = true;
                    
                    return current.next(0);
                }
                //If on bottom level and next.data is bigger  
                else if (height == 0 && cmp < 0)
                {           
                    //node doesn't exist
                    return null;
                }
                
            }
            //if pointer at this level is null and not at base height
            else if(height > 0)
            {     
                //If the pointer is null and youre not at the bottom row,
                //so check the next row down 
                height--;
            }
            //case of the last null pointer at base height
            else
            {
               //reached the end of the list without finding a node
               return null;
            }    
            
        }
               
        return null;
    }

    public static double difficultyRating()
    {
        return 4;
    }

    public static double hoursSpent()
    {
        return 10;
    }
    
    //Optional methods
    //gets the maximum height a node may be determined by the length of the skip list
    private int getMaxHeight(int n)
    {
        int i; 
            
        if(n<=2)
        {
            i=1;            
        }
        else if(n == 3 || n == 4 )
        {
            i=2;
        }
        else if(n>=5 && n<=8)
        {
            i=3;
        }
        else
        {
            i = (int)Math.ceil((Math.log10(n)/Math.log10(2)));
        }
        //System.out.println("Max height of "+i+" with "+n+" nodes");
        
        return i;
    }
    
    //generates heights with this distribution
    // 50% = 1 25% = 2 12.5% = 3 6.75% = 4 ...... so on and so forth until maxHeight
    private static int generateRandomHeight(int maxHeight)
    {
        int height = 1;
        Random ranNum = new Random();
        
        //there is a 50% chance the height grows with every iteration of the loop
        //
        while(ranNum.nextBoolean() && height < maxHeight)
        {
            height++;
        }
        
        return height;
        
    }
    //50% chance to grow the tallest nodes
    //links the tallest nodes together
    
    private void growSkipList()
    {
            Node<T> liftTemp = this.head;
            Node<T> lastToGrow = this.head;
            Integer oldHeight = this.head.height-1;
            
            
            //Manually grow head node 
            this.head.grow();
            
        //go through and grow half of the taller nodes 
        //hold the pointers of the nodes that grow  to link up later            
        while(liftTemp.next(oldHeight) != null)
        {   
            liftTemp = liftTemp.next(oldHeight);
               
            //if the node grows then link to last one
            //save to link to next one
            if(liftTemp.maybegrow())
            {   
                lastToGrow.nextAtHeight.add(oldHeight+1,liftTemp);
                lastToGrow = liftTemp;
            }
               
        }
            
            

    }
    //always trims top level and any null pointer that head has
    private void trimSkipList()
    {
       Node<T> current = this.head;
       Node<T> next;
       
        int realHeight = getMaxHeight(this.listLength-1);       
        this.head.height = realHeight;       
        
       //stores pointer to next node max height node before chopping off the head 
       //will miss head node if list is empty
       while(current != null)
       {          
           //traverse the bottom level
           next = current.next(0);  
           
           //and lop off their heads 
           current.trim(realHeight);
           
           current  = next;
       }  
           
    }
    
}
