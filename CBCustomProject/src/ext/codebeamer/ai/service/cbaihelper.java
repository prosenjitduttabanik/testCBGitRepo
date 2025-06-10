package ext.codebeamer.ai.service;

public class cbaihelper {

	
	//Step 1: Utility for Encryption (AES 256)
    public static byte[] encrypt(byte[] data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // AES 256
        return keyGen.generateKey();
    }
    
    // Step 2: Read File Content
    public static String readDocumentContent(File file) throws Exception {
        if (file.getName().endsWith(".pdf")) {
            PDDocument doc = PDDocument.load(file);
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(doc);
            doc.close();
            return text;
        } else if (file.getName().endsWith(".docx")) {
            XWPFDocument docx = new XWPFDocument(new FileInputStream(file));
            XWPFWordExtractor extractor = new XWPFWordExtractor(docx);
            String text = extractor.getText();
            extractor.close();
            return text;
        } else {
            throw new IllegalArgumentException("Unsupported file type");
        }
    
    
        //Step 3: Send to OpenAI API using GPT-3.5 Turbo
        
        public static String sendToOpenAI(String text, String openaiApiKey) throws IOException {
            OkHttpClient client = new OkHttpClient();
            MediaType JSON = MediaType.parse("application/json");

            JSONObject json = new JSONObject()
                .put("model", "gpt-3.5-turbo")
                .put("messages", new JSONArray()
                    .put(new JSONObject().put("role", "system").put("content", "You are an ALM assistant for medical devices."))
                    .put(new JSONObject().put("role", "user").put("content", 
                          "Based on the following document, extract requirements, risks, and test cases and create traceability links :\n\n" + text)));

            RequestBody body = RequestBody.create(JSON, json.toString());
            Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer " + openaiApiKey)
                .post(body)
                .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        }
        
        
        //Step 4: Main Method implement All Steps
	
        public static void main(String[] args) throws Exception {
            File file = new File("example.pdf"); // or .docx
            SecretKey key = EncryptUtil.generateKey();
            String content = readDocumentContent(file);
            
            byte[] encrypted = EncryptUtil.encrypt(content.getBytes(StandardCharsets.UTF_8), key);
            System.out.println("Encrypted size: " + encrypted.length);

            // Decrypt and send for processing
            String decrypted = new String(encrypted); // in real usage, decrypt here
            String result = sendToOpenAI(decrypted, "your-openai-api-key");

            System.out.println("Generated Artifacts:\n" + result);
        }
        
        
}
