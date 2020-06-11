package com.mkyong;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.json.JSONException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.comprehendmedical.AWSComprehendMedical;
import com.amazonaws.services.comprehendmedical.AWSComprehendMedicalClient;
import com.amazonaws.services.comprehendmedical.model.DetectEntitiesRequest;
import com.amazonaws.services.comprehendmedical.model.DetectEntitiesResult;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;


@CrossOrigin
@RestController
@RequestMapping({ "/mainController" })
public class WelcomeController {
    
    private PostNewTarget postNewTarget = new PostNewTarget();
    
    private String patientDetails;

    @PostMapping("/patientDetails")
     public void generateArMEDPrescription(@RequestBody PatientDetails patientDetails) throws JSONException, URISyntaxException, AddressException, MessagingException, IOException {	
    	final String QR_CODE_IMAGE_PATH = "/home/ar_workspace/qr-image/QRCode.jpg";
    //	final String QR_CODE_IMAGE_PATH = "/Users/rjampala/Desktop/Noffice/azurehackathon/armed-teamnucleus/ARMED-BE/ar_workspace/qr-image/QRCode.jpg";
		  this.patientDetails = patientDetails.getDetails();
		 // sendMail(patientDetails.getEmail());
				AWSCredentialsProvider credentials = new AWSStaticCredentialsProvider(
						new BasicAWSCredentials("", ""));  //Generate token and add it
				System.out.println(Regions.getCurrentRegion());
				AWSComprehendMedical client = AWSComprehendMedicalClient.builder().withCredentials(credentials)
						.withRegion(Regions.US_EAST_2).build();

				DetectEntitiesRequest request = new DetectEntitiesRequest();
				request.setText(patientDetails.getDetails());
				//"The patient has been diagnosed with Type 2 Diabetes with excess sugar in blood vessels and is adivised to follow the mentioned medication routine for next 2 months. CaloMist - 50 mcg daily before sleep."

				DetectEntitiesResult result = client.detectEntities(request);
				System.out.println(result.getEntities().get(0) + "\n");
			    System.out.println(result.getEntities().get(1));
				//System.out.println(result.getEntities().get(2));
				
				
				ArrayList types = new ArrayList();
				HashMap<String,String> medMap = new HashMap<String, String>();
				
				
				int i=0;
				for (i=0; i< result.getEntities().size(); i++){
					if(result.getEntities().get(i).getCategory().contentEquals("ANATOMY") || result.getEntities().get(i).getCategory().contentEquals("MEDICAL_CONDITION")) {
					medMap.put(result.getEntities().get(i).getCategory() + " " + result.getEntities().get(i).getType(), result.getEntities().get(i).getText());
					} else {
						medMap.put(result.getEntities().get(i).getCategory() + result.getEntities().get(i).getCategory() + " " + result.getEntities().get(i).getType(), result.getEntities().get(i).getText());
						for (int j=0; j <result.getEntities().get(i).getAttributes().size();j++ ) {
							medMap.put(result.getEntities().get(i).getCategory() + " " + result.getEntities().get(i).getAttributes().get(j).getType(),result.getEntities().get(i).getAttributes().get(j).getText());
						}
					}
					
				}
				//System.out.println(medMap.get("MEDICATION BRAND_NAME"));
				
				String emailContent = 
						  "\n<MEDICAL_CONDITION>" + medMap.get("MEDICAL_CONDITION DX_NAME") + "</MEDICAL_CONDITION>" + "\n\n"  +
						  "<ANATOMY DIRECTION>" + medMap.get("ANATOMY DIRECTION") + "</ANATOMY DIRECTION>" + "\n\n" + 
						  "<ANATOMY SYSTEM_ORGAN_SITE>" + medMap.get("ANATOMY SYSTEM_ORGAN_SITE") + "<ANATOMY SYSTEM_ORGAN_SITE>" + "\n\n" + 
						  "<MEDICATION GENERIC_NAME>" + medMap.get("MEDICATIONMEDICATION GENERIC_NAME") + "</MEDICATIONMEDICATION GENERIC_NAME>" + "\n\n" +
						  "<MEDICATION DOSAGE>" + medMap.get("MEDICATION DOSAGE") + "</MEDICATION DOSAGE>" + "\n\n" + 
						  "<MEDICATION FREQUENCY>" + medMap.get("MEDICATION FREQUENCY") + "</MEDICATION FREQUENCY>";
				
				emailContent = 
						 "Medical Condition : " +  medMap.get("MEDICAL_CONDITION DX_NAME") + "<br>" +
								 "Anatomy Direction : " +  medMap.get("ANATOMY DIRECTION") + "<br>" +
								 "Anatomy system organ site : " +  medMap.get("ANATOMY SYSTEM_ORGAN_SITE") + "<br>" +
								 "Medication generic name : " +  medMap.get("MEDICATIONMEDICATION GENERIC_NAME") + "<br>" +
								 "medication dosage : " +  medMap.get("MEDICATION DOSAGE") + "<br>" +
								 "medication frequency : " +  medMap.get("MEDICATION FREQUENCY") + "<br>";
				
				emailContent = "";
				
				if(medMap.get("MEDICAL_CONDITION DX_NAME")!=null) {
					emailContent = emailContent + "Medical Condition : " +  medMap.get("MEDICAL_CONDITION DX_NAME") + "<br>";
					
				}if(medMap.get("ANATOMY DIRECTION")!=null) {
					emailContent = emailContent + "Anatomy Direction : " +  medMap.get("ANATOMY DIRECTION") + "<br>";
					
				}if(medMap.get("ANATOMY SYSTEM_ORGAN_SITE")!=null) {
					emailContent = emailContent + "Anatomy system organ site : " +  medMap.get("ANATOMY SYSTEM_ORGAN_SITE") + "<br>";
					
				}if(medMap.get("MEDICATIONMEDICATION GENERIC_NAME")!=null) {
					emailContent = emailContent + "Medication generic name : " +  medMap.get("MEDICATIONMEDICATION GENERIC_NAME") + "<br>";
					
				}if(medMap.get("MEDICATION DOSAGE")!=null) {
					emailContent = emailContent + "medication dosage : " +  medMap.get("MEDICATION DOSAGE") + "<br>";
					
				}if(medMap.get("MEDICATION FREQUENCY")!=null) {
					emailContent = emailContent + "medication frequency : " +  medMap.get("MEDICATION FREQUENCY") + "<br>";
					
				}
						 
		    	
		    	System.out.println(emailContent);
		    	
				
				 try {
			         generateQRCodeImage(medMap.get("MEDICAL_CONDITION DX_NAME") +  medMap.get("ANATOMY SYSTEM_ORGAN_SITE") + medMap.get("MEDICATION DOSAGE") + medMap.get("MEDICATION FREQUENCY") , 350, 350, QR_CODE_IMAGE_PATH);
				 //  generateQRCodeImage("MEDICAL_CONDITION DX_NAME" +  "ANATOMY DIRECTION" + "MEDICATION DOSAGE ONLY" + "MEDICATION FREQUENCY" , 350, 350, QR_CODE_IMAGE_PATH);
			        } catch (WriterException e) {
			            System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
			        } catch (IOException e) {
			            System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
			        }
				
		    //	ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Mail.xml");
		    	 
			    	//MailMail mm = (MailMail) context.getBean("mailMail");
			        //mm.sendMail("shauryabajaj1@gmail.com","absaxena1996@gmail.com ", "emailContent");
			 
				 try {
				 generateQRCodeEnabledPrescription(patientDetails, "/home/ar_workspace/blank-prescription/Blank_Prescription.pdf","/home/ar_workspace/generated-prescription/Generated_Prescription.pdf","/home/ar_workspace/qr-image/QRCode.jpg");
				//	 generateQRCodeEnabledPrescription(patientDetails, "/Users/rjampala/Desktop/Noffice/azurehackathon/armed-teamnucleus/ARMED-BE/ar_workspace/blank-prescription/Blank_Prescription.pdf","/Users/rjampala/Desktop/Noffice/azurehackathon/armed-teamnucleus/ARMED-BE/ar_workspace/generated-prescription/Generated_Prescription.pdf","/Users/rjampala/Desktop/Noffice/azurehackathon/armed-teamnucleus/ARMED-BE/ar_workspace/qr-image/QRCode.jpg");
						
					 sendMail(emailContent, patientDetails.getEmail());
					 postNewTarget.postTarget();
				 } catch (WriterException e) {
			            System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
			        } catch (IOException e) {
			            System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
			        }
		    }
		    
			static void generateQRCodeImage(String text, int width, int height, String filePath)
		            throws WriterException, IOException {
		        QRCodeWriter qrCodeWriter = new QRCodeWriter();
		        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

		        Path path = FileSystems.getDefault().getPath(filePath);
		        MatrixToImageWriter.writeToPath(bitMatrix, "JPG", path);
		        System.out.println("\n\nQR code generated");
				//System.out.println("\n\nGenerated QR code Enabled Prescription");

		    }
			
			 void generateQRCodeEnabledPrescription(PatientDetails patientDetails, String sourcefilePath, String targetFilePath, String QR_CODE_IMAGE_PATH)
		            throws WriterException, IOException {
				// Modify PDF located at "source" and save to "target"
				PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourcefilePath), new PdfWriter(targetFilePath));
				// Document to add layout elements: paragraphs, images etc
				Document document = new Document(pdfDocument);

				// Load image from disk
				ImageData imageData = ImageDataFactory.create(QR_CODE_IMAGE_PATH);
				// Create layout image object and provide parameters. Page number = 1
				Image image = new Image(imageData).scaleAbsolute(200, 200).setFixedPosition(400, 450);
				// This adds the image to the page
				document.add(image);

				// Don't forget to close the document.
				// When you use Document, you should close it rather than PdfDocument instance
				document.close();
				
				
				
				PdfDocument pdfDoc =
					    new PdfDocument(new PdfReader(targetFilePath), new PdfWriter("/home/ar_workspace/qr-enabled-prescription/QREnabledPrescription.pdf")); 
				
			/*	PdfDocument pdfDoc =
					    new PdfDocument(new PdfReader(targetFilePath), new PdfWriter("/Users/rjampala/Desktop/Noffice/azurehackathon/armed-teamnucleus/ARMED-BE/ar_workspace/qr-enabled-prescription/QREnabledPrescription.pdf"));
			*/	
				String tempPatientDetails = this.patientDetails;
				tempPatientDetails = tempPatientDetails.substring(0, tempPatientDetails.length());
				String[] pD = tempPatientDetails.split(" ");
				int pDSize = pD.length;
				int pDLines = (int)pDSize / 11;
				System.out.println(tempPatientDetails);
				pDLines = pDLines + 1;
				int j = 0, r= 0,i,k=350;
				for(i=0;i<pDLines;i++) {
				    String singleLine="";
				    for(j=r;j<pDSize;j++) {
				        singleLine = singleLine.concat(pD[j].concat(" "));
				        if((j-1)%10==0 && j>9) {
				            r = j+1;
				            break;
				        }
				    }
				    PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
	                canvas.beginText().setFontAndSize(
	                        PdfFontFactory.createFont(FontConstants.HELVETICA), 12)
	                        .moveText(100, k)
	                        .showText(singleLine)
	                        .endText();
	                k = k - 20;
				}
				
				
				PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
				canvas.beginText().setFontAndSize(
				        PdfFontFactory.createFont(FontConstants.HELVETICA), 12)
				        .moveText(170, 535)
				        .showText(patientDetails.getName())
				        .endText();
				/*
				PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
				canvas.beginText().setFontAndSize(
				        PdfFontFactory.createFont(FontConstants.HELVETICA), 12)
				        .moveText(100, 350)
				        .showText("The patient has been diagnosed with Multiple Sclerosis and is advised ")
				        .endText();
				
				PdfCanvas canvas2 = new PdfCanvas(pdfDoc.getFirstPage());
				System.out.println("in pdf gen\n");
				canvas.beginText().setFontAndSize(
				        PdfFontFactory.createFont(FontConstants.HELVETICA), 12)
				        .moveText(100, 330)
				        .showText("to follow the mentioned medication routine for next 6 months.")
				        .endText();
				
				
				
				PdfCanvas canvas3 = new PdfCanvas(pdfDoc.getFirstPage());
				canvas.beginText().setFontAndSize(
				        PdfFontFactory.createFont(FontConstants.HELVETICA), 12)
				        .moveText(100, 310)
				        .showText("Fingolimode(Gilenya) - 50 mcg daily before sleep.")
				        .endText();
				
				 */
				pdfDoc.close();
				
		    	
	}
			 
			 private void sendMail(String content, String email) throws AddressException, MessagingException, IOException {
				   Properties props = new Properties();
				   props.put("mail.smtp.auth", "true");
				   props.put("mail.smtp.starttls.enable", "true");
				   props.put("mail.smtp.host", "smtp.gmail.com");
				   props.put("mail.smtp.port", "587");
				   
				   Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				      protected PasswordAuthentication getPasswordAuthentication() {
				         return new PasswordAuthentication("armed.teamnucleus.sbajaj.rjampala@gmail.com", "");
				      }
				   });
				   
				   Message msg = new MimeMessage(session);
				   msg.setFrom(new InternetAddress("shauryabajaj1@gmail.com", false));

				   msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
				   msg.setSubject("ArMED Medical Prescription");
				   msg.setContent("Dear Paitent, Thank you for using the ArMED Portal. Here is your prescription. You can scan the QR code with your app to experience Augmented Reality.", "text/html");
				   msg.setContent("These are the extracted medical information", "text/html");
				   msg.setContent(content, "text/html");
				 //  msg.setContent("These are the extracted information using AI", "text/html");
				   
				   
				   msg.setSentDate(new Date());
				   
				   MimeBodyPart messageBodyPart = new MimeBodyPart();
				   messageBodyPart.setContent("Dear Paitent, Thank you for using the ArMED Portal. Here is your prescription. You can scan the QR code with your app to experience Augmented Reality.<br>", "text/html");
				   
				   MimeBodyPart messageBodyPart1 = new MimeBodyPart();
				   messageBodyPart1.setContent("These are the extracted medical information<br>", "text/html");
				   
				   MimeBodyPart messageBodyPart2 = new MimeBodyPart();
				   messageBodyPart2.setContent(content+"<br>", "text/html");
				   
				   System.out.println(content);
				   
				   MimeBodyPart messageBodyPart9 = new MimeBodyPart();
				   messageBodyPart9.setContent("Download our apk from the below link, install it in your mobile and scan the QR code to experience Augmented Reality.<br>", "text/html");
				   

				   MimeBodyPart messageBodyPart10 = new MimeBodyPart();
				   messageBodyPart10.setContent("<a href='https://github.com/Shaurya1234/ArMED-Microsoft/blob/master/ARHealth_DIT.apk?raw=true'>Android package(APK)</a><br>", "text/html");
				   
				   MimeBodyPart messageBodyPart5 = new MimeBodyPart();
				   messageBodyPart5.setContent("Regards,<br>", "text/html");
				   
				   MimeBodyPart messageBodyPart6 = new MimeBodyPart();
				   messageBodyPart6.setContent("Team Nucleus<br>", "text/html");
				   
				   MimeBodyPart messageBodyPart7 = new MimeBodyPart();
				   messageBodyPart7.setContent("Shaurya Bajaj<br>", "text/html");
				   
				   MimeBodyPart messageBodyPart8 = new MimeBodyPart();
				   messageBodyPart8.setContent("Ritesh Jampala<br>", "text/html");
				   
				   MimeBodyPart messageBodyPart3 = new MimeBodyPart();
				   messageBodyPart3.setContent("Please find attached pdf<br>", "text/html");

				   MimeBodyPart messageBodyPart4 = new MimeBodyPart();
				   messageBodyPart4.setContent("ARMED QR ENABLED PDF<br>", "text/html");

				   Multipart multipart = new MimeMultipart();
				   multipart.addBodyPart(messageBodyPart);
				   multipart.addBodyPart(messageBodyPart1);
				   multipart.addBodyPart(messageBodyPart2);
				   multipart.addBodyPart(messageBodyPart3);
				   multipart.addBodyPart(messageBodyPart9);
				   multipart.addBodyPart(messageBodyPart10);
				   multipart.addBodyPart(messageBodyPart5);
				   multipart.addBodyPart(messageBodyPart6);
				   multipart.addBodyPart(messageBodyPart7);
				   multipart.addBodyPart(messageBodyPart8);
				   MimeBodyPart attachPart = new MimeBodyPart();

				   attachPart.attachFile("/home/ar_workspace/qr-enabled-prescription/QREnabledPrescription.pdf");
				  // attachPart.attachFile("/Users/rjampala/Desktop/Noffice/azurehackathon/armed-teamnucleus/ARMED-BE/ar_workspace/qr-enabled-prescription/QREnabledPrescription.pdf");
					  
				   multipart.addBodyPart(attachPart);
				   msg.setContent(multipart);
				   Transport.send(msg);  
				   System.out.println("mail sent\n");
				}

}
