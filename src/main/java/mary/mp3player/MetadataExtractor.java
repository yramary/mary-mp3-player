package mary.mp3player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class MetadataExtractor {

public static String getMetadata(String path) {


try {

InputStream input = new FileInputStream(new File(path));
ContentHandler handler = new DefaultHandler();
Metadata metadata = new Metadata();
Parser parser = new Mp3Parser();
ParseContext parseCtx = new ParseContext();
parser.parse(input, handler, metadata, parseCtx);
input.close();


System.out.println("Title: " + metadata.get("title"));
System.out.println("Artists: " + metadata.get("xmpDM:artist"));
System.out.println("Genre: " + metadata.get("xmpDM:genre"));

return ("Title: " + metadata.get("title") + " Artist: " + metadata.get("xmpDM:artist") + " Genre: " + metadata.get("xmpDM:genre"));

} catch (FileNotFoundException e) {
e.printStackTrace();
} catch (IOException e) {
e.printStackTrace();
} catch (SAXException e) {
e.printStackTrace();
} catch (TikaException e) {
e.printStackTrace();
}
return "unknown";
}

public static String getMetaArtist(String path) {
	
	try {
	
	InputStream input = new FileInputStream(new File(path));
	ContentHandler handler = new DefaultHandler();
	Metadata metadata = new Metadata();
	Parser parser = new Mp3Parser();
	ParseContext parseCtx = new ParseContext();
	parser.parse(input, handler, metadata, parseCtx);
	input.close();
	//System.out.println("meta artist " + metadata.get("xmpDM:artist"));
	return metadata.get("xmpDM:artist");
	}catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
	e.printStackTrace();
	} catch (SAXException e) {
	e.printStackTrace();
	} catch (TikaException e) {
	e.printStackTrace();
	}
	return "UNKNOWN";
	
	
}

public static String getMetaAlbum(String path) {
	try {
		
	InputStream input = new FileInputStream(new File(path));
	ContentHandler handler = new DefaultHandler();
	Metadata metadata = new Metadata();
	Parser parser = new Mp3Parser();
	ParseContext parseCtx = new ParseContext();
	parser.parse(input, handler, metadata, parseCtx);
	input.close();
	//System.out.println("meta album " + metadata.get("xmpDM:album"));
	return metadata.get("xmpDM:album");
	}catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
	e.printStackTrace();
	} catch (SAXException e) {
	e.printStackTrace();
	} catch (TikaException e) {
	e.printStackTrace();
	}
	return "UNKNOWN";
	
}

public static String getMetaTitle(String path) {
	try {
		
	InputStream input = new FileInputStream(new File(path));
	ContentHandler handler = new DefaultHandler();
	Metadata metadata = new Metadata();
	Parser parser = new Mp3Parser();
	ParseContext parseCtx = new ParseContext();
	parser.parse(input, handler, metadata, parseCtx);
	input.close();
	//System.out.println("meta title " + metadata.get("title"));
	return metadata.get("title");
	}catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
	e.printStackTrace();
	} catch (SAXException e) {
	e.printStackTrace();
	} catch (TikaException e) {
	e.printStackTrace();
	}
	return "UNKNOWN";
	
}


}
