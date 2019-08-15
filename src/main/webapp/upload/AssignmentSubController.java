package com.af.project.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.af.project.model.Method;

@RequestMapping("/assignment")
@RestController
public class AssignmentSubController {

	private static String UPLOAD_DIR = "upload";
	String line = null;
	String[][][] list;
	String Path,fileName;

	
	
	@CrossOrigin("http://localhost:3000")
	@PostMapping("/upload")
	public String upload(@RequestParam("file") MultipartFile file,HttpServletRequest request) {
		
		try {

			fileName = file.getOriginalFilename().toString();
			Path = request.getServletContext().getRealPath("")+UPLOAD_DIR+File.separator+fileName;
			saveFile(file.getInputStream(),Path);
			this.getMethods(Path, fileName);
			
			return "http://localhost:8080/upload/"+fileName;
		}catch(Exception e) {
			return e.getMessage().toString();
		}
	}

	private void saveFile(InputStream inputStream,String path) {
		try {
			OutputStream outputStream= new FileOutputStream(new File(path));
			int read = 0;
			byte[] bytes = new byte[1024];
			while((read = inputStream.read(bytes))!= -1) {
				outputStream.write(bytes,0,read);
			}
			outputStream.flush();
			outputStream.close();
					
		}catch(Exception e) {
			
		}
	}
	
	
	
	public void getMethods(String path,String filename){
		String line = null;
		ArrayList<Method> methodList = new ArrayList<>();
		ArrayList<String> lineList = new ArrayList<>();


        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(path);

            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            int i=1;
            boolean dataTypeStatus = false;
            boolean methodStatus = false;
            boolean foundMethod = false;
            boolean arrayMethod = false;

            String previousWord = "";
            String methodName = "";
            int scopeBracketCount=0;
            int startLine = 0;
            int endLine = 0;
            
            
            
            while((line = bufferedReader.readLine()) != null) {
            		//add the code lines to the array list
            		lineList.add(line);
            		//split the line in to small parts
	            	String[] parts = line.split("(?<!^)\\b");

	            	for (String part : parts) {
	            	
	            		String[] spaceSplit = part.split(" ");
	            		
	            		for (String spacePart: spaceSplit) {
	            			
	            			String word = spacePart.replace(" ", "");
	            			boolean dataType;
	            			
	            			if(arrayMethod) {
	            				dataType = false;
	            			}else {
	            				//check for data type
	            				dataType = this.search(word);
	            			}
	            			if(dataType) {		            	
	            				dataTypeStatus = true;	            		
	            			}else if(arrayMethod){
	            				//check for the '>' in the methods that return ArrayList or List
	            				if(previousWord.contains(">")) {
	            					arrayMethod = false;
	            					methodStatus = true;
	            				}
	            			}else {	            		
	            				if(dataTypeStatus) {
	            					//check for the '<' in the methods that return ArrayList or List
	            					if (word.contains("<")) {
	        							arrayMethod = true;
	        						}else {
	        							methodStatus = true;
	            					}	            		
	            				}else {	            			
	            					if(methodStatus) {		
	            						//check for the '(' to find the method name because always there should be a '(' after the method name
	            						if(word.contains("(")) {
	            							methodName = previousWord;
	            							startLine = i;
	            							foundMethod = true;
	            						}
	            						methodStatus = false;
		            				}
		            			}            					            			
	            				dataTypeStatus = false;
		            		}
	            			
	            			if(foundMethod) {
	            				//checking the start of the scope
		            			if(word.contains("{")) {
		            						            				
		            				scopeBracketCount++;	            				
		            			}
		            			//check the end of the scope
		            			if(word.contains("}")){	            				
		            				scopeBracketCount--;
		            				//check the end of the method
		            				if(scopeBracketCount==0) {
		            					foundMethod = false;
		            					endLine = i;
		            					//create a Method type object
		            					Method method = new Method(methodName,startLine,endLine);
		            					//add the method to the array list
		            					methodList.add(method);
		            					
		            					}
		            			}
	            			}            			
		            		previousWord = word;	            		
		            	}            		           	   
	            	}	         
	                i++;
//            	}
            }   

            // Always close files.
            bufferedReader.close();         
          
            
            for(Method m:checkDirectRecurtion(methodList,lineList)) {
            	System.out.println("---------Recursion method---------------------");

            	System.out.println(m.getName()+"----"+m.getStartLine()+"------"+m.getEndLine());
            }
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                filename + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + filename + "'");                  
            // Or we could just do this: 
            // ex.printStackTrace();
        }
        
	}
	
	//search the data types
	public boolean search(String word) {
		String names[] = {"String","void","boolean","double","int","float","loan","short","char","ArrayList","List","HashMap"};
	
		for(String str: names) {
			
		    if(str.toLowerCase().equals(word.toLowerCase())) {
		    
		    	//System.out.println("Data type :"+word);
		        return true;
		    }
		}
		return false;
	}
	
	public void displayMethods(ArrayList<Method> method) {
		for(Method m:method) {
			System.out.println("---"+m.getName()+"-----Starts at: "+m.getStartLine()+"--------Ends at: "+m.getEndLine());
		}
	}
	
	public ArrayList<Method> checkDirectRecurtion(ArrayList<Method> methods,ArrayList<String> lines) {
		
		ArrayList<Method> recursiveMethods = new ArrayList<>();
		boolean recursiveStatus = false;
		for(Method method:methods) {
			
			String methodName = method.getName();
			
			for(int i=method.getStartLine();i<method.getEndLine();i++) {
				
				//if(!lines.get(i).replace(" ", "").substring(0, 2).equals("//")) {
			
				String[] parts = lines.get(i).split("(?<!^)\\b");
	            	
	            	for (String part : parts) {
	            	
	            		String[] spaceSplit = part.split(" ");
	            		
	            		for (String spacePart: spaceSplit) {
	            		
	            			
	            			String word = spacePart.replace(" ", "");
	            			//check for the found method name is a legal method (sometimes there can be a string with method name"
	            			if(recursiveStatus) {
	            				
	            				if(word.contains("(")) {
	            					
            						recursiveMethods.add(method);

	            				}
	            				recursiveStatus = false;
	            			}
	            			for(Method newMethod:methods) {
	    					
	            				//check for the recursive method name
	            				if(word.equals(newMethod.getName())) {
	            					//check for direct recursion
	            					if(newMethod.getName().equals(method.getName())) {
	    							
	            						if(word.contains("(")) {
			            					
			            					recursiveMethods.add(method);
			            				
			            				}else {
			            					
			            					recursiveStatus = true;
			            				
			            				}
	    							//check for indirect recursion	
	    							}else {
	    								
	    								boolean newRecursiveStatus = false;
	    								
	    								for(int x=newMethod.getStartLine();x<newMethod.getEndLine();x++) {
	    								
	    									//if(!lines.get(x).replace(" ", "").substring(0, 2).equals("//")) {
		    								
	    									String[] recursivePart = lines.get(x).split("(?<!^)\\b");
		    					            	
		    					            	for (String rPart : recursivePart) {
		    					            	
		    					            		String[] rSplit = rPart.split(" ");
		    					            		
		    					            		for (String spaceRPart: rSplit) {
		    					            		
		    					            			String rWord = spaceRPart.replace(" ", "");
		    					            
		    					            			if(newRecursiveStatus) {
		    					            				
		    					            				if(rWord.contains("(")) {
		    					            					
		    				            						recursiveMethods.add(method);

		    					            				}
		    					            				newRecursiveStatus = false;
		    					            			}
		    					            			
		    					            			if(rWord.equals(method.getName())) {
		    			    							
		    					            				if(rWord.contains("(")) {
		    					            					
		    					            					recursiveMethods.add(method);
		    					            				
		    					            				}else {
		    					            					
		    					            					newRecursiveStatus = true;
		    					            				
		    					            				}
		    					            				
		    					            			}
		    					            		}
		    					            	}

	    								//}
	    							}
	    						}
	    					}
	            		}
	            	}
	           	}
	        }
			//}
		}
		
		return recursiveMethods;
		
	}
	
}
