import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

public class decoder {
	private static ConcurrentHashMap<Integer, String> codeTable;
	private BufferedReader bufferedReader;
	private Node root;
	public decoder(String ENCODED_FILE_PATH, String CODE_TABLE_PATH) throws IOException {
		decodeFile(ENCODED_FILE_PATH, CODE_TABLE_PATH);
	}
	
	private void decodeFile(String ENCODED_FILE_PATH, String CODE_TABLE_PATH) throws IOException {
		reconstructCodeTable(CODE_TABLE_PATH);
		Node root = createHuffmanTree();
		writeDecodedFile(root, ENCODED_FILE_PATH);
	}

	private void writeDecodedFile(Node root, String ENCODED_FILE_PATH) throws IOException {
		Node node = root;
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(ENCODED_FILE_PATH));
        bufferedReader = new BufferedReader(new FileReader(ENCODED_FILE_PATH));
        File file = new File(ENCODED_FILE_PATH.substring(0,ENCODED_FILE_PATH.lastIndexOf("\\")+1) + "decoded.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        Integer key;
        int character; 
        byte[] byt = Files.readAllBytes(new File(ENCODED_FILE_PATH).toPath());
        for(byte b: byt) {
        	     for(int i = 0; i < 8; ++i) {
                        if(node.isLeaf()) {
                                key = node.getKey();
                                bufferedWriter.write(key.toString());
                                bufferedWriter.newLine();
                                node = root;

                        }
                        int bit = (b<<(7-i)) & 0x80;
                        if(bit == 0x80) {
                                node = node.getRight();
                        }
                        else
                                node = node.getLeft();
                }
                
        }
        bufferedWriter.flush();
        bufferedWriter.close();
	}

	private Node createHuffmanTree() {
		root = new Node();
		Iterator it = codeTable.keySet().iterator();
        while (it.hasNext()) {
        	Node node = root;// This will be the root node
    		Integer key = (Integer)it.next();
        	String value = codeTable.get(key);
        	int i=0;
        	for(i=0;i<value.length()-1;i++){
        		if(value.charAt(i) == '0'){
        			if(node.getLeft()==null){
        				node.setLeft(new Node());
        				node = node.getLeft();
        			}else{
        				node = node.getLeft();
        			}
        		}else if(value.charAt(i) == '1'){
        			if(node.getRight()==null){
        				node.setRight(new Node());
        				node = node.getRight();
        			}else{
        				node = node.getRight();
        			}
        	}
        		
        }
        	if (value.charAt(i) == '0') {

    			if (node.getLeft() == null) {
    				node.setLeft(new Node());
    			}
    			node.getLeft().setKey(key);
    			node.getLeft().setToLeaf();
    		} else {
    			if (node.getRight() == null) {
    				node.setRight(new Node());
    			}
    			node.getRight().setKey(key);
    			node.getRight().setToLeaf();

    		}
        }
        return root;
		
	}

	public void reconstructCodeTable(String CODE_TABLE_PATH) throws IOException{
		codeTable = new ConcurrentHashMap<Integer, String>();
		bufferedReader =  new BufferedReader(new FileReader(CODE_TABLE_PATH));
		String nextLine="";
		while((nextLine = bufferedReader.readLine())!= null && !(nextLine.equalsIgnoreCase(""))){
			String[] line = nextLine.split(" ");
			codeTable.put(Integer.parseInt(line[0]), line[1]);
		}
	}
	
	
	public static void main(String[] args) throws IOException {
		if(args.length==0){
			System.out.println("Path for input file is not entered!");
			System.exit(1);
		}else{
		String ENCODED_FILE_PATH = args[0];
		String CODE_TABLE_PATH = args[1];
		new decoder(ENCODED_FILE_PATH, CODE_TABLE_PATH);
		
		}
		
	}
}
