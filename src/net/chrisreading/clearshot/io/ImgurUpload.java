package net.chrisreading.clearshot.io;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.imageio.ImageIO;

import org.json.JSONException;
import org.json.JSONObject;

import com.sun.org.apache.xml.internal.security.utils.Base64;

/**
 * Uploads an image to imgur
 */
public class ImgurUpload {

	private String clientID = "24c27223247c4e0";

	private BufferedImage image;
	private HttpURLConnection conn;
	private String response; // response from imgur
	
	public ImgurUpload(BufferedImage image) {
		this.image = image;
		
		try {
			createConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Uploads the image and then returns with a response
	 * @param data Image data (should be encoded to Base64)
	 * @return Imgur's response
	 * @throws IOException
	 */
	public void upload() throws IOException {
		// Write to the connection
		StringBuilder builder = new StringBuilder();
		OutputStream output = conn.getOutputStream();
		output.write(convertImage(image).getBytes());
		output.flush();
		
		// Read response from imgur
	    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    String line;
	    while ((line = rd.readLine()) != null) {
	        builder.append(line).append("\n");
	    }
	    
	    output.close();
	    rd.close();
	    
	    response = builder.toString();
	}
	
	/**
	 * Creates a connection with the Imgur API url
	 * @throws IOException
	 */
	private void createConnection() throws IOException {
		URL url = new URL("https://api.imgur.com/3/image");
		conn = (HttpURLConnection) url.openConnection();
		
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization", "Client-ID " + clientID);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	}
	
	/**
	 * Converts the image to Base64
	 * @param image Image to convert
	 * @return
	 * @throws IOException
	 */
	private String convertImage(BufferedImage image) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(image, "png", out);
		byte[] byteImage = out.toByteArray();
		String dataImage = Base64.encode(byteImage);
		
		return URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(dataImage, "UTF-8");
	}
	
	/**
	 * Get the link for the image uploaded
	 * @return String of the link
	 * @throws JSONException
	 */
	public String getLink() {
		try {
			JSONObject object = new JSONObject(response);
			return object.getJSONObject("data").getString("link");	
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	

}
