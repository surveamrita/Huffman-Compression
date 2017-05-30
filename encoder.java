import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.BitSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class encoder {
	private ConcurrentHashMap<Integer, Integer> frequencyTable;
	private static ConcurrentHashMap<Integer, String> codeTable;
	private BufferedReader bufferedReader;
	private CacheOptFourAryHeap cacheOptFourAryHeap;
	
	public encoder(String PATH) throws NumberFormatException, IOException {
		
		encodeFile(PATH);
	}
	
	public void encodeFile(String PATH)throws NumberFormatException, IOException{
		frequencyTable = calculateFrequencies(PATH);
		build_tree_using_4way_heap(frequencyTable);
		codeTable = new ConcurrentHashMap<Integer, String>();
		generateHuffmanCodes(cacheOptFourAryHeap.getMin(), "");
		writeFile(PATH);
		}
	
	public ConcurrentHashMap<Integer, Integer> calculateFrequencies(String PATH) throws NumberFormatException, IOException{
		frequencyTable = new ConcurrentHashMap<Integer, Integer>();
		bufferedReader = new BufferedReader(new FileReader(new File(PATH)));
		String nextLine="";
		while((nextLine = bufferedReader.readLine())!=null ){
			if(!nextLine.equalsIgnoreCase("")){
				if(frequencyTable.containsKey(Integer.parseInt(nextLine))){
					frequencyTable.put(Integer.parseInt(nextLine), frequencyTable.get(Integer.parseInt(nextLine))+1);
				}else{
					frequencyTable.put(Integer.parseInt(nextLine), 1);
				}
			}
		}
		return frequencyTable;
	}
	
		
	private CacheOptFourAryHeap build_tree_using_4way_heap(ConcurrentHashMap<Integer,Integer> frequencyTable) {
		//create 4-way heap
		cacheOptFourAryHeap = createFourAryHeap(frequencyTable);
				
		//create Huffman Tree
		cacheOptFourAryHeap = createHuffmanTreeUsingFourAryHeap(cacheOptFourAryHeap);
		return cacheOptFourAryHeap;
	}
	
	public CacheOptFourAryHeap createFourAryHeap(ConcurrentHashMap<Integer,Integer> frequencyTable){
		cacheOptFourAryHeap = new CacheOptFourAryHeap(frequencyTable.size());
		Iterator<Integer> it = frequencyTable.keySet().iterator();
        while (it.hasNext()) {
        	Integer key = (Integer)it.next();
        	Integer value = frequencyTable.get(key);
        	if(value!=null){
            Node node = new Node(value,key);
            node.setToLeaf();
            cacheOptFourAryHeap.insert(node);
            }
         }
        return cacheOptFourAryHeap;
	}
	
	public static CacheOptFourAryHeap createHuffmanTreeUsingFourAryHeap(CacheOptFourAryHeap cacheOptFourAryHeap){
		while (cacheOptFourAryHeap.getSize() >=2) {
    		Node first = cacheOptFourAryHeap.extractMin();
    		Node second = cacheOptFourAryHeap.extractMin();
    		int internalFreq =  first.getFreq() + second.getFreq();
    		Node internal = new Node();
    		internal.setLeft(first);
    		internal.setRight(second);
    		internal.setFreq(internalFreq);
    		cacheOptFourAryHeap.insert(internal);
    	}
		return cacheOptFourAryHeap;
	}
	
	private void generateHuffmanCodes(Node node, String code) {             
        if(node != null) {                      
                if(node.isLeaf()){
                        codeTable.put(node.getKey(), code);
                }else {
                        generateHuffmanCodes(node.getLeft(), code + "0");
                        generateHuffmanCodes(node.getRight(), code + "1");
                }
        }
	}
	
	private void writeFile(String PATH) {
        try {	//Write Code Table
        		String CODE_TABLE_PATH = PATH.substring(0,PATH.lastIndexOf("\\")+1) + "code_table.txt";
        		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(CODE_TABLE_PATH));
        		Iterator<Integer> it = codeTable.keySet().iterator();
        		while (it.hasNext()) {
    	        	Integer key = (Integer)it.next();
    	        	String value = codeTable.get(key);
    	        	if(value!=null){
    	        		bufferedWriter.write(key.toString()+" "+value);
    	        		bufferedWriter.newLine();
    	        		}
        		}
        		bufferedWriter.flush();  		
                
        		//Write Encoded File
        		bufferedReader = new BufferedReader(new FileReader(PATH));
        		String ENCODED_FILE_PATH = PATH.substring(0,PATH.lastIndexOf("\\")+1) + "encoded.bin";
        		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(ENCODED_FILE_PATH));
                writeBinaryText(bufferedReader, bos);
        		bos.flush();
                bos.close();
        		
        }
        catch(Exception e) {
                System.out.println(e);
        }
}
	public void writeBinaryText(BufferedReader br, BufferedOutputStream bos) throws IOException{
		BitSet buffer = new BitSet(Integer.MAX_VALUE);
		int index = 0;
		String huffmanCode;
		String nextVal;
		while((nextVal=br.readLine())!= null) {
			 if (!nextVal.trim().equals("")) {
				 huffmanCode = codeTable.get(Integer.parseInt(nextVal));
			     for( char bit: huffmanCode.toCharArray()) {
			    	 if(bit=='1'){
			    		 buffer.set(index, true);
			    	 	 index++;	
			    	 }
			    	 else{
			    		 buffer.set(index,false);
			    		 index++;
			    	 }
			    
			     }
			 }
		 }
		bos.write(buffer.toByteArray());
	}

	public static void main(String[] args) throws IOException {
		if(args.length==0){
			System.out.println("Path for input file is not entered!");
			System.exit(1);
		}else{
		String PATH = args[0];
		new encoder(PATH);
		}
	}
	


	

}
