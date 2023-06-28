package webdriver;
//ok
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Patcher {
	public static String getRandomString(int len) {
	    String AB = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	    SecureRandom rnd = new SecureRandom();
	    StringBuilder sb = new StringBuilder(len);
	    for (int i = 0; i < len; i++)
	      sb.append(AB.charAt(rnd.nextInt(AB.length()))); 
	    return sb.toString();
	  }
	  
	  public static String patchChromeDriver(String path) throws IOException {
	    Path paths = Paths.get(path, new String[0]);
	    Charset charset = StandardCharsets.ISO_8859_1;
	    String content = new String(Files.readAllBytes(paths), charset);
	    String pattern = "(\\{window.*\\;\\})";
	    Pattern p = Pattern.compile(pattern);
	    Matcher m = p.matcher(content);
	    System.err.println(path);
	    if (!m.find())
	      return path; 
	    String c = m.group(1);
	    StringBuffer bf = new StringBuffer();
	    String nc = "{console.log(\"what!\")}";
	    bf.append(nc);
	    for (int i = nc.length(); i < c.length(); i++)
	      bf.append("\t"); 
	    content = content.replace(c, bf.toString());
	    Path temppath = Files.createTempFile("chromedriver", ".exe");
	    Files.write(temppath, content.getBytes(charset), new java.nio.file.OpenOption[0]);
	    try {
	      Files.move(temppath, paths, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
	      System.err.println(paths);
	      return path;
	    } catch (Exception exception) {
	      final String temp = temppath.toAbsolutePath().toString();
	      Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	              public void run() {
	                (new File(temp)).delete();
	              }
	            }));
	      System.err.println(temp);
	      return temp;
	    } 
	  }
//	public static String patchChromeDriver(String path) throws IOException {
//		Path paths = Paths.get(path);
//		Charset charset = StandardCharsets.ISO_8859_1;
//		String content = new String(Files.readAllBytes(paths), charset);
//		if(!content.contains("cdc_")) return path;
//		Path temppath = Files.createTempFile("chromedriver", ".exe");
//		content = content.replace("cdc_", "abc_");
//		Files.write(temppath, content.getBytes(charset));
//		try {
//			Files.move(temppath, paths, StandardCopyOption.REPLACE_EXISTING);
//			return path;
//		}catch(Exception e) {
//		}
//		String temp = temppath.toAbsolutePath().toString();
//		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
//		    public void run() {
//		        (new File(temp)).delete();
//		    }
//		}));
//		return temp;
//	}
}
