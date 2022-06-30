import java.util.NoSuchElementException;
import java.util.LinkedList;
import java.lang.SuppressWarnings;
  
// --== CS400 Project One File Header ==--
// Name: James Gui
// CSL Username: gui
// Email: jgui2@wisc.edu
// Lecture #: <004 @4:00pm>

// This class creates a Hashtable that stores Key Value pairs
// Impliments MapADT interface

public class HashtableMap <KeyType, ValueType> implements MapADT <KeyType, ValueType>{

    protected int hash_Map_Capacity;
    protected double collision_Max_Percent = 0.75f;
    protected LinkedList<KeyTypeAndValueType>[] bucketArray; //note, this is a raw type, should check while paraming

    protected boolean tableEmpty = true;

    //Unique data structure for Key value pair, essentially a struct w/ data points
    public class KeyTypeAndValueType {

        KeyType key;
        ValueType value;

        //allows initialization of key and value
        public void SetValues(KeyType keyToSet, ValueType valueToSet)
        {
            this.key = keyToSet;
            this.value = valueToSet;
        }
    };

    //This constructor allows user to set capacity, initializes bucket array
    public HashtableMap(int capacity)
    {
        //initializes the hashtable
        //README this is raw type
        this.bucketArray = new LinkedList[capacity];

        //initializes array of Linkedlists
        for(int i = 0; i < capacity; i++)
        {
            this.bucketArray[i] = new LinkedList<KeyTypeAndValueType>();
        }

        this.hash_Map_Capacity = capacity;

        
    }

    //this constructor has auto capacity of 20 
    //constructor for initializing a Hashmap
    public HashtableMap(){
        // initializes the hashtable with default capacity = 20
        this.hash_Map_Capacity = 20;

        this.bucketArray = new LinkedList[this.hash_Map_Capacity];

        //initializes array of Linkedlists
        for(int i = 0; i < this.hash_Map_Capacity; i++)
        {
            this.bucketArray[i] = new LinkedList<KeyTypeAndValueType>();
        }
    }

    /*
    * puts value into hashtable, returns true if successful, false if not
    * if false, no modification is done to the table
    * index is based on absolute value of the key's hashcode % array capacity
    * uses linked chaining
    * params
    * Keytype key
    * Valuetype value
    */
    @Override
    public boolean put(KeyType key, ValueType value)
    {
        if(containsKey(key) || key == null)
        {
            System.out.printf("issue, key already in array, cannot continue");
            return false;
        }

        KeyTypeAndValueType newValue = new KeyTypeAndValueType();
        newValue.SetValues(key, value);

        //this is the hashing algorithm 
        //the reason this is not capacity is b/c capacity isn't updated while length changes. 
        int keyValue = CalculateKeyIndex(key);

        //adds value to bucket 
        bucketArray[keyValue].add(newValue);//TODO add error checking here
        
        if(tableEmpty)
        {
            tableEmpty = false;
        }

        double sizeValue = size()/bucketArray.length;

        if(sizeValue >= collision_Max_Percent)
        {
            helperResizeTable();
        }
        return true;
    }

    /*
    * Resizes table then rehashes
    * Returns error on failure. 
    * recursive for each bucket
    * README please find a way to increase efficency of this system
    */
    public void helperResizeTable()
    {
        //Java is pass by value right? 
        //TODO check if the value is copied into the table 
        LinkedList<KeyTypeAndValueType>[] tempLinkedList = bucketArray;
        LinkedList<KeyTypeAndValueType>[] newLinkedList = new LinkedList[ (bucketArray.length * 2) ];

        for(int i = 0; i < newLinkedList.length; i++)
        {
            newLinkedList[i] = new LinkedList<KeyTypeAndValueType>();
        }

        bucketArray = newLinkedList;

        
        
        for(int i = 0; i < tempLinkedList.length; i ++)
        {
            if(tempLinkedList[i].size() > 0)
            {
                for(int x = 0; x < tempLinkedList[i].size(); x++)
                {
                    KeyTypeAndValueType tempKeyValuePair = (KeyTypeAndValueType) tempLinkedList[i].get(x);
                    KeyType key = tempKeyValuePair.key; 

                    int keyValue = CalculateKeyIndex(key);
                    bucketArray[keyValue].add(tempKeyValuePair);
                }
            }
        }
    }

    /*
    * Retrieves the value of the key from the array
    * returns Valuetype upon success
    * returns null upon failure
    * params
    * Keytype key
    */
    @Override
	public ValueType get(KeyType key) throws NoSuchElementException
    {
        try{
            
            ValueType returnValueType = null;
            int keyValue = CalculateKeyIndex(key); //this just calculates index from hash

            //README: why is there a get and contin? feels like a ton of redundency there.... 

            if(!containsKey(key))
            {
                NoSuchElementException exp = new NoSuchElementException();
                throw exp;
            }

            for(int i = 0; i < bucketArray[keyValue].size(); i++)
            {
                KeyTypeAndValueType tempValue = (KeyTypeAndValueType) bucketArray[keyValue].get(i);

                if(tempValue.key.equals(key))
                {
                    returnValueType = tempValue.value; //changes return value to value that should be returned
                    return returnValueType;
                }
            }

            return returnValueType; //this will be null if no values are found
        }
        catch(NoSuchElementException e)
        {
            return null;
        }
        
    }

    /*
    * Returns number of key-value pairs stored in the collection
    * Returns 0 on failure
    */
    @Override
	public int size()
    {
        int numOfItems = 0;
        if(tableEmpty)
        {
            return numOfItems;
        }

        for (int x = 0; x < bucketArray.length; x++){
            numOfItems += bucketArray[x].size();
        }

        return numOfItems;
    }

    /*
    * returns True if table contains the key already
    * returns false if array does not contain the ky
    * params
    * key
    */
    @Override
	public boolean containsKey(KeyType key)
    {
        int keyValue = CalculateKeyIndex(key);
        
        for(int i = 0; i < bucketArray[keyValue].size(); i++)
        {
            //TODO make sure checkvalue is of the same type
            KeyTypeAndValueType checkValue = (KeyTypeAndValueType) bucketArray[keyValue].get(i);

            if(checkValue.key.equals(key))
            {
                return true;
            }
        }

        //README I intentially didn't add a check for the entire array as it should never show up, 
        //In case it does theres bigger issues. 
        //TODO testing only, have a check entire array
        
        return false;
    }

    /*
    * Removes the key from the table 
    * Removes the reference to the value associated with the key 
    * Returns null if key cannot be found
    * params
    * KeyType key
    */
    @Override
	public ValueType remove(KeyType key)
    {
        if(key == null)
        {
            return null;
        }

        int keyValue = CalculateKeyIndex(key);
        
        for(int i = 0; i < bucketArray[keyValue].size(); i++)
        {
            KeyTypeAndValueType checkValue = (KeyTypeAndValueType) bucketArray[keyValue].get(i);

            if(checkValue.key.equals(key))
            {
                ValueType returnValue = (ValueType) checkValue.value;
                bucketArray[keyValue].remove(i);
                checkValue = null;
                return returnValue;
            }
        }

        return null;
    }

    /*
    * clears hashtable of all values
    * does NOT remove the list, only empties the list. 
    * does not resize the table...
    */
    @Override
	public void clear()
    {
        if(tableEmpty)
        {
            System.out.print("Table is already empty brotha");
            return;
        }

        for(int i = 0; i < bucketArray.length; i++)
        {
            bucketArray[i].clear();
        }
    }

    public int CalculateKeyIndex(KeyType key)
    {   
        int returnInt = 0;
        if(key == null)
        {
            System.out.println("issue occurred");
            return 0;
        }

        returnInt = ( Math.abs( key.hashCode() ) ) % bucketArray.length;

        return returnInt;
    }

    /*
    //testing only
    protected void AddElements()
    { 
        KeyType key = (KeyType) "6";
        ValueType value = (ValueType) "something 6";
        KeyType key2 = (KeyType) "8";
        ValueType value2 = (ValueType) "something 8";
        KeyType key3 = (KeyType) "9";
        ValueType value3 = (ValueType) "something 9";
        KeyType key4 = (KeyType) "7";
        ValueType value4 = (ValueType) "something 7";
        put(key, value);
        put(key2, value2);
        put(key3, value3);

        System.out.println("this is get key4 " + get(key4));
        System.out.println("this is contains key4 " + containsKey(key4));

        System.out.println("this is get key " + get(key4));
        System.out.println("this is contains key " + containsKey(key));
        System.out.println("");
    }

    //README testing only
    protected void PrintElements()
    {
        
        for(int i = 0; i < bucketArray.length; i++)
        {
            for(int x = 0; x < bucketArray[i].size(); x++)
            {
                KeyTypeAndValueType tempType = (KeyTypeAndValueType) bucketArray[i].get(x);
                int keyValue = CalculateKeyIndex(tempType.key);
            }
        }
        System.out.println("This is bucket size " + bucketArray.length);
        
    }*/
    public static void main(String[] args) {
        /*
        HashtableMap testMap = new HashtableMap();
        testMap.AddElements();
        testMap.PrintElements();
        System.out.println("What I have for num of elements " + testMap.size());
        */
    }
}
