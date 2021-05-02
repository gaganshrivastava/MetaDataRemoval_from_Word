package in.jvahome.lambdaFunction;

import java.awt.Color;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.aspose.words.BuiltInDocumentProperties;
import com.aspose.words.CustomDocumentProperties;
import com.aspose.words.Document;
import com.aspose.words.DocumentProperty;
import com.aspose.words.Field;
import com.aspose.words.FieldType;
import com.aspose.words.Footnote;
import com.aspose.words.FootnoteType;
import com.aspose.words.NodeCollection;
import com.aspose.words.NodeType;
import com.aspose.words.Run;
import com.aspose.words.Section;
import com.aspose.words.StructuredDocumentTag;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.json.simple.JSONValue;

import java.util.Map;

//public class Lambda_Handler implements RequestHandler<Object, Object>{
public class Lambda_Handler implements RequestHandler<JSONObject, Object> {

	static String bucketName = "sanitized-bucket";
	static String keyName = "";
	static String dstKey = "";

	@Override
	public JSONObject handleRequest(JSONObject event, Context arg1) {

		// TODO Auto-generated method stub

		System.out.println("Welcome to the lambda");
		System.out.println("Input Event = " + event);

		
		 //for the cloud watch event to trigger the lambda at every 5 mins
		if (event.get("detail-type") != null && ((String) event.get("detail-type")).equals("Scheduled Event")) {
			System.out.println("Event triggered by Cloud Watch Events ");
			
			// create a json object with dummy data
			//event = sampleInput();
			
			try {
				keyName = "bb41fc75-4e28-4c9b-862e-fe79393aa8a2/1619791769802/bb41fc75-4e28-4c9b-862e-fe79393aa8a216197917698021.vnd.openxmlformats-officedocument.wordprocessingml.document";
				dstKey = keyName;
				main();
				keyName = "";
				dstKey = keyName;
				return null;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Error while processsing the dummy file");
				e.printStackTrace();
			}
		}
		
		
		
		// https://www.geeksforgeeks.org/parse-json-java/
		
		
		try {

			Map selectedOptions = ((Map) event.get("selectedOptions"));
			Iterator<Map.Entry> itr1 = selectedOptions.entrySet().iterator();
			while (itr1.hasNext()) {
				Map.Entry pair = itr1.next();
				// System.out.println(pair.getKey() + " : " + pair.getValue());
				if (pair.getKey() == "stripMetaData") {
					pair.setValue(0);
				}
			}

			/*
			List a = (ArrayList) event.get("files");
			LinkedHashMap<String, String> file = (LinkedHashMap<String, String>) a.get(0); // 0 means just the first array elemnt
			System.out.println("KEY = " + file.get("key"));
			String inputKey = file.get("key");
			keyName = inputKey;
			dstKey = inputKey;
			*/
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("Invalid Input = " + event);
			return event;

		}

		try {
			
			List a = (ArrayList) event.get("files");
			System.out.println("Length of file = " + a.size());
			for( int i=0;i<a.size() ;i++) {
				LinkedHashMap<String, String> file = (LinkedHashMap<String, String>) a.get(i); // 0 means just the first array elemnt
				System.out.println("KEY = " + file.get("key"));
				String inputKey = file.get("key");
				keyName = inputKey;
				dstKey = inputKey;
				main();
			}
			
		} catch (Exception e) {
			// TODO: handle exception

			System.out.println("Exception occureed at main");
			System.out.println(e);
			// return null;
			e.printStackTrace();

		}

		return event;

	}

	public static void main() throws Exception {
		System.out.println("Inside main methiod");
		System.out.println("Bucket Name = " + bucketName);
		System.out.println("Input Key = " + keyName);

		// Download the image from S3 into a stream
		AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
		S3Object s3Object = s3Client.getObject(new GetObjectRequest(bucketName, keyName));
		InputStream objectData = s3Object.getObjectContent();
		System.out.println("Input stream object created");

		// Document doc = new Document("InputTestDocWMetaData2.docx");
		Document doc = new Document(objectData);
		System.out.println("file is picked from local");

		BuiltInDocumentProperties BuiltInProperties = doc.getBuiltInDocumentProperties();
		CustomDocumentProperties CustomProperties = doc.getCustomDocumentProperties();

		System.out.println("Before Removal");
		System.out.println("Total Built properties = " + BuiltInProperties.getCount());
		System.out.println("Total Customer properties = " + CustomProperties.getCount());

		BuiltInProperties.clear();
		CustomProperties.clear();

		System.out.println("After Removal");
		System.out.println("Total Built properties = " + BuiltInProperties.getCount());
		System.out.println("Total Customer properties = " + CustomProperties.getCount());

		// removing macros
		doc.removeMacros();
		System.out.println("Macros Removed");

		// removing personal info
		doc.setRemovePersonalInformation(true);
		System.out.println("Personal Inforamtion removed");

		// removing comment
		NodeCollection comment = doc.getChildNodes(NodeType.COMMENT, true);
		comment.clear();
		System.out.println("Comments Removed");

		// removes all types of fields from Word document
		doc.getRange().getFields().clear();
		System.out.println("fileds Removed");

		// removes all instances of a particular type of field from Word document
		for (Field f : doc.getRange().getFields()) {
			if (f.getType() == FieldType.FIELD_MERGE_FIELD) {
				f.remove();
			}
		}

		System.out.println("Filed instance Removed");

		// removing all revision
		doc.getRevisions().rejectAll();
		System.out.println("Revisios has been removed");

		for (Footnote footnote : (Iterable<Footnote>) doc.getChildNodes(NodeType.FOOTNOTE, true))
			if (footnote.getFootnoteType() == FootnoteType.FOOTNOTE)
				footnote.remove();

		System.out.println("Foot Note is removed");

		for (Run run : (Iterable<Run>) doc.getChildNodes(NodeType.RUN, true))
			if (run.getFont().getSize() < 5 || run.getFont().getHidden()
					|| run.getFont().getColor() == new Color(255, 255, 255, 255))
				run.remove();

		System.out.println("child nodes workds less than 5 size is removed");

		// To remove attached template from Word document
		doc.setAttachedTemplate("");

		// To remove built-in and custom document properties
		for (DocumentProperty prop : doc.getBuiltInDocumentProperties()) {
			doc.getBuiltInDocumentProperties().remove(prop.getName());
		}
		doc.getBuiltInDocumentProperties().clear();

		for (DocumentProperty prop : doc.getCustomDocumentProperties()) {
			doc.getCustomDocumentProperties().remove(prop.getName());
		}
		doc.getCustomDocumentProperties().clear();

		// To remove all headers/footers along with its content (watermarks etc)
		for (Section sec : doc.getSections()) {
			sec.deleteHeaderFooterShapes();
			sec.clearHeadersFooters();
		}

		// to remove content controls from Word document
		for (StructuredDocumentTag contentControl : (Iterable<StructuredDocumentTag>) doc
				.getChildNodes(NodeType.STRUCTURED_DOCUMENT_TAG, true)) {
			contentControl.removeAllChildren();
			contentControl.remove();
		}
		
		doc.setAttachedTemplate("");

		doc.save("/tmp/" + "InputTestDocWMetaData2_updated.docx");
		System.out.println("Saved to local-----------------");

		// Uploading to S3 destination bucket
		File putfile = new File("/tmp/" + "InputTestDocWMetaData2_updated.docx");
		System.out.println("File object is created");
		System.out.println("Writing to: " + bucketName + "/" + dstKey);
		try {
			s3Client.putObject(bucketName, dstKey, putfile);
		} catch (AmazonServiceException e) {
			System.err.println(e.getErrorMessage());
			System.exit(1);
		}

		System.out.println("File uplaoded on s3");
		
		if(putfile.delete())
        {
            System.out.println("File deleted successfully from Local");
        }
		
	}

	public static JSONObject sampleInput() {

		//String demmyData = "{\n  \"files\": [\n   \n    {\n      \"displayName\": \"CS351_HW1.pdf\",\n      \"key\": \"a0aa0f2c-da69-4420-b5ea-bcd06b93ffb1/1617646540564/a0aa0f2c-da69-4420-b5ea-bcd06b93ffb116176465405641.vnd.openxmlformats-officedocument.wordprocessingml.document\",\n      \"location\": \"https://unsanitized-bucket.s3.amazonaws.com//bb41fc75-4e28-4c9b-862e-fe79393aa8a2/1617215163866/bb41fc75-4e28-4c9b-862e-fe79393aa8a216172151638661.pdf\"\n    }\n  ],\n  \"selectedOptions\": {\n    \"stripMetaData\": 0,\n    \"textExtract\": 0,\n    \"virusScan\": 0,\n    \"imageRecognition\": 0,\n    \"convertDocument\": 0,\n    \"documentClassification\": 0,\n    \"redactPII\": 0\n  },\n  \"userID\": \"userID\",\n  \"submitTime\": \"epsilonTime\"\n}";

		String s = "{\r\n" + "    \"submitTime\": \"epsilonTime\",\r\n" + "    \"selectedOptions\": {\r\n"
				+ "        \"stripMetaData\": 1,\r\n" + "        \"textExtract\": 0,\r\n"
				+ "        \"virusScan\": 0,\r\n" + "        \"imageRecognition\": 0,\r\n"
				+ "        \"convertDocument\": 0,\r\n" + "        \"documentClassification\": 0,\r\n"
				+ "        \"redactPII\": 0\r\n" + "    },\r\n" + "    \"files\": [\r\n" + "        {\r\n"
				+ "            \"displayName\": \"In_Class_Classification.docx\",\r\n"
				+ "            \"key\": \"a0aa0f2c-da69-4420-b5ea-bcd06b93ffb1/1617646540564/a0aa0f2c-da69-4420-b5ea-bcd06b93ffb116176465405641.vnd.openxmlformats-officedocument.wordprocessingml.document\",\r\n"
				+ "            \"location\": \"https://unsanitized-bucket.s3.amazonaws.com//bb41fc75-4e28-4c9b-862e-fe79393aa8a2/1617215163866/bb41fc75-4e28-4c9b-862e-fe79393aa8a216172151638660.vnd.openxmlformats-officedocument.wordprocessingml.document\"\r\n"
				+ "        }\r\n" + "    ],\r\n" + "    \"userID\": \"userID\"\r\n" + "}";

		Object obj = JSONValue.parse(s);
		JSONObject jsonObject = (JSONObject) obj;
		System.out.println("Inside sampleInput --");
		System.out.println(jsonObject.get("files"));

		//JSONObject eventPayload = new JSONObject();
		//eventPayload.put("event", jsonObject);

		// return eventPayload;
		return jsonObject;
	}

}
