/*
 * University of Le Havre 
 * Aboozar Rajabi
 */

package etu.lehavre.aisproject;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import dk.dma.ais.message.AisMessage;
import dk.dma.ais.message.AisMessage5;
import dk.dma.ais.message.AisPositionMessage;
import dk.dma.ais.proprietary.IProprietarySourceTag;
import dk.dma.ais.reader.AisReader;
import dk.dma.ais.reader.AisReaders;

import org.apache.commons.lang3.ArrayUtils;

//import etu.lehavre.aisproject.AIS2CSV2;

import org.apache.commons.lang3.StringEscapeUtils;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;




public class Controller {
    
	private static AISstorage aisStorage;

    public Controller(String mongoURIString) {
        final MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURIString));
        final MongoDatabase aisDatabase = mongoClient.getDatabase("classe");
        
        AisReader reader = null;
        /*
        //window version
     	File folder = new File("C:/ais/2016/localoutput/");
     	File[] listOfFiles = folder.listFiles();
     	
     	for (int i = 0; i < listOfFiles.length; i++) {
     		File file = listOfFiles[i];
     		if (file.isFile() && file.getName().endsWith(".txt")) {
     			System.out.println(file.getName());
     			reader = AisReaders.createReaderFromInputStream(new FileInputStream(file.getAbsoluteFile()));
     			Path filePath = Paths.get(file.getAbsolutePath());
     			aisStorage = new AISstorage(aisDatabase, reader, filePath);        
     	  } 
     	}
     	*/
        //linux version, stream file reading
		final Path dir = Paths.get("c:/ais/2017/Aishuboutput/10-Copy/");//.toAbsolutePath();
		System.out.println(dir);
		DirectoryStream<Path> dirStream;
		
		try {
			dirStream = Files.newDirectoryStream(dir);
			for (Iterator<Path> iterator = dirStream.iterator(); iterator.hasNext();) {
				Path p = iterator.next();
				
				if(p.toAbsolutePath().toString().endsWith("txt")) {
					//System.out.println(p.toAbsolutePath().toString());
					reader = AisReaders.createReaderFromInputStream(new FileInputStream(p.toAbsolutePath().toString()));
	     			Path filePath = Paths.get(p.toAbsolutePath().toString());
	     			aisStorage = new AISstorage(aisDatabase, reader, filePath); 
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

		
		
    }
    
    
    public static void main(String[] args) throws IOException, InterruptedException { 
    	
    	//AisReader reader = AisReaders.createReaderFromInputStream(new FileInputStream("file3.txt"));
    	
    	
    	new Controller("mongodb://172.17.26.9");
    	//new Controller("mongodb://127.0.0.1");
    	
    }

 
}
