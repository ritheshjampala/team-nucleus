package com.mkyong;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.mkyong.SignatureBuilder;


// See the Vuforia Web Services Developer API Specification - https://developer.vuforia.com/resources/dev-guide/adding-target-cloud-database-api

public class PostNewTarget implements TargetStatusListener {

    //Server Keys
    private String accessKey = "cc7afc6459e6c196ceee9cda90269fdcc649e384";
    private String secretKey = "dffdd09f222631cf31305fde6d16adad9a2cd244";
    private String url = "https://vws.vuforia.com";
    private String targetName = "AR-QR-CodeDB1";
    private String imageLocation = "/home/ar_workspace/qr-image/QRCode.jpg";
    private TargetStatusPoller targetStatusPoller;
    private final float pollingIntervalMinutes = 60;//poll at 1-hour interval
    
    public String postTarget() throws URISyntaxException, ClientProtocolException, IOException, JSONException {
        HttpPost postRequest = new HttpPost();
        HttpClient client = new DefaultHttpClient();
        postRequest.setURI(new URI(url + "/targets"));
        JSONObject requestBody = new JSONObject();
        setRequestBody(requestBody);
        postRequest.setEntity(new StringEntity(requestBody.toString()));
        setHeaders(postRequest); // Must be done after setting the body
        
        HttpResponse response = client.execute(postRequest);
        String responseBody = EntityUtils.toString(response.getEntity());
        System.out.println(responseBody);
        
        JSONObject jobj = new JSONObject(responseBody);
        
        String uniqueTargetId = jobj.has("target_id") ? jobj.getString("target_id") : "";
        System.out.println("\nCreated target with id: " + uniqueTargetId);
        
        return uniqueTargetId;
    }
    
    private void setRequestBody(JSONObject requestBody) throws IOException, JSONException {
        File imageFile = new File(imageLocation);
        if(!imageFile.exists()) {
            System.out.println("File location does not exist!");
            System.exit(1);
        }
        byte[] image = FileUtils.readFileToByteArray(imageFile);
        requestBody.put("name", targetName); // Mandatory
        requestBody.put("width", 1); // Mandatory
        requestBody.put("image", Base64.encodeBase64String(image)); // Mandatory
        requestBody.put("active_flag", 1); // Optional
        requestBody.put("application_metadata", Base64.encodeBase64String("https://www.youtube.com/watch?v=ZI-cIhzYick".getBytes())); // Optional
    }
    
    private void setHeaders(HttpUriRequest request) {
        SignatureBuilder sb = new SignatureBuilder();
        request.setHeader(new BasicHeader("Date", DateUtils.formatDate(new Date()).replaceFirst("[+]00:00$", "")));
        request.setHeader(new BasicHeader("Content-Type", "application/json"));
        request.setHeader("Authorization", "VWS " + accessKey + ":" + sb.tmsSignature(request, secretKey));
    }
    
    /**
     * Posts a new target to the Cloud database; 
     * then starts a periodic polling until 'status' of created target is reported as 'success'.
     */
    public void postTargetThenPollStatus() {
        String createdTargetId = "";
        try {
            createdTargetId = postTarget();
        } catch (URISyntaxException | IOException | JSONException e) {
            e.printStackTrace();
            return;
        }
    
        // Poll the target status until the 'status' is 'success'
        // The TargetState will be passed to the OnTargetStatusUpdate callback 
        if (createdTargetId != null && !createdTargetId.isEmpty()) {
            targetStatusPoller = new TargetStatusPoller(pollingIntervalMinutes, createdTargetId, accessKey, secretKey, this );
            targetStatusPoller.startPolling();
        }
    }
    
    // Called with each update of the target status received by the TargetStatusPoller
    @Override
    public void OnTargetStatusUpdate(TargetState target_state) {
        if (target_state.hasState) {
            
            String status = target_state.getStatus();
            
            System.out.println("Target status is: " + (status != null ? status : "unknown"));
            
            if (target_state.getActiveFlag() == true && "success".equalsIgnoreCase(status)) {
                
                targetStatusPoller.stopPolling();
                
                System.out.println("Target is now in 'success' status");
            }
        }
    }
    
    
    public static void main(String[] args) throws URISyntaxException, ClientProtocolException, IOException, JSONException {
        PostNewTarget p = new PostNewTarget();
        p.postTargetThenPollStatus();
    }
    
}

