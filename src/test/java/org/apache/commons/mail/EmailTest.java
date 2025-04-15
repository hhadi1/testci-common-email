package org.apache.commons.mail;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.mail.Session;
import java.util.Date;

public class EmailTest {
    private static final String[] TEST_EMAILS = {"ab@bc.com", "a.b@c.org", 
    		"abcdefghijklmnopqrst@abcdefghijklmnopqrst.com.bd"};

    /* Concrete Email Class for Testing */
    private EmailConcrete email;
    
    @Before
    public void setUp() {
        email = new EmailConcrete();
    }

    @After
    public void tearDown() {
        email = null;
    }

    // Test case for 'addBcc'
    @Test
    public void testAddBcc() throws Exception {
    	// Test Case #1: Add multiple BCC addresses
        email.addBcc(TEST_EMAILS);
        assertEquals(TEST_EMAILS.length, email.getBccAddresses().size());
    }

    // Test case for 'addCc'
    @Test
    public void testAddCc() throws Exception {
    	// Test Case #1: Add a single CC address
        email.addCc("cc@email.com");
        assertEquals(1, email.getCcAddresses().size());
    }

    // Test case for 'addHeader'
    @Test
    public void testAddHeader() {
        // Test Case #1: Valid header
        email.addHeader("Name", "Value");

        // Test Case #2: Empty name
        try {
            email.addHeader("", "Value");
            fail("Expected IllegalArgumentException for empty name.");
        } catch (IllegalArgumentException e) {
            assertEquals("name can not be null or empty", e.getMessage());
        }

        // Test Case #3: Empty value
        try {
            email.addHeader("Name", "");
            fail("Expected IllegalArgumentException for empty value.");
        } catch (IllegalArgumentException e) {
            assertEquals("value can not be null or empty", e.getMessage());
        }

        // Test Case #4: Both empty name and value
        try {
            email.addHeader("", "");
            fail("Expected IllegalArgumentException for both empty name and value.");
        } catch (IllegalArgumentException e) {
            assertEquals("name can not be null or empty", e.getMessage());
        }
    }


    // Test case for 'addReplyTo'
    @Test
    public void testAddReplyTo() throws Exception {
        // Test Case #1: Add a reply-to address
        email.addReplyTo("addReply@example.com", "Reply User");
        assertEquals(1, email.getReplyToAddresses().size());
    }

    // Test case for 'buildMimeMessage'
    @Test
    public void testBuildMimeMessage() throws Exception {
        // Test Case #1: Successfully build MimeMessage
        email.setSubject("Test Subject");
        email.setMsg("This is a message.");
        email.setFrom("setFrom@example.com");
        email.addTo("addTo@example.com");
        email.setHostName("Host123");

        email.buildMimeMessage();

        // Test Case #2: Verify message content
        Object messageContent = email.getMimeMessage().getContent();
        assertNotNull("Message content should not be null", messageContent);
        assertEquals("This is a message.", messageContent.toString());

        // Test Case #3: Calling buildMimeMessage() twice should throw an exception
        try {
            email.buildMimeMessage();
            fail("Expected IllegalStateException when calling buildMimeMessage() twice.");
        } catch (IllegalStateException e) {
            assertEquals("The MimeMessage is already built.", e.getMessage());
        }

        // Test Case #4: Missing From address
        Email emailNoFrom = new EmailConcrete();
        emailNoFrom.setSubject("Test Subject");
        emailNoFrom.setMsg("This is a message.");
        emailNoFrom.addTo("addTo@example.com");
        emailNoFrom.setHostName("Host123");
        
        try {
            emailNoFrom.buildMimeMessage();
            fail("Expected EmailException for missing From address.");
        } catch (EmailException e) {
            assertEquals("From address required", e.getMessage());
        }

        // Test Case #5: Missing recipients
        Email emailNoRecipients = new EmailConcrete();
        emailNoRecipients.setSubject("Test Subject");
        emailNoRecipients.setMsg("This is a message.");
        emailNoRecipients.setFrom("setFrom@example.com");
        emailNoRecipients.setHostName("Host123");

        try {
            emailNoRecipients.buildMimeMessage();
            fail("Expected EmailException for missing recipients.");
        } catch (EmailException e) {
            assertEquals("At least one receiver address required", e.getMessage());
        }

        // Test Case #6: Adding CC and BCC
        Email emailWithCCBCC = new EmailConcrete();
        emailWithCCBCC.setSubject("Test Subject");
        emailWithCCBCC.setMsg("This is a message.");
        emailWithCCBCC.setFrom("setFrom@example.com");
        emailWithCCBCC.addTo("addTo@example.com");
        emailWithCCBCC.setHostName("Host123");
        emailWithCCBCC.addCc("cc@example.com");
        emailWithCCBCC.addBcc("bcc@example.com");
        emailWithCCBCC.addReplyTo("addReply@example.com", "Reply Name");
        
        emailWithCCBCC.buildMimeMessage();
        assertEquals(1, emailWithCCBCC.getMimeMessage().getRecipients(javax.mail.Message.RecipientType.CC).length);
        assertEquals(1, emailWithCCBCC.getMimeMessage().getRecipients(javax.mail.Message.RecipientType.BCC).length);
        assertEquals(1, emailWithCCBCC.getMimeMessage().getReplyTo().length);

        // Test Case #6: Adding Headers
        Email emailWithHeaders = new EmailConcrete();
        emailWithHeaders.setSubject("Test Subject");
        emailWithHeaders.setMsg("This is a message.");
        emailWithHeaders.setFrom("setFrom@example.com");
        emailWithHeaders.addTo("addTo@example.com");
        emailWithHeaders.setHostName("Host123");
        emailWithHeaders.addHeader("Header Name", "Header Value");

        
        emailWithHeaders.buildMimeMessage();
        assertEquals("Header Value", emailWithHeaders.getMimeMessage().getHeader("Header Name")[0]);

    }

    // Test case for 'getHostName'
    @Test
    public void testGetHostName() {
        // Test Case #1: Set and retrieve host name
        email.setHostName("Host123");
        assertEquals("Host123", email.getHostName());

        // Test Case #2: Retrieve host name when not set
        Email newEmail = new EmailConcrete();
        assertNull(newEmail.getHostName());
    }

    // Test case for 'getMailSession'
    @Test
    public void testMailSession() throws Exception {
        // Test Case #1: Valid mail session
        email.setHostName("smtp.example.com");
        Session session = email.getMailSession();
        assertNotNull("Mail session should not be null", session);
        assertEquals("smtp.example.com", session.getProperty("mail.smtp.host"));

        Email emailWithSSL = new EmailConcrete();
        emailWithSSL.setHostName("Host123");
        emailWithSSL.setSSLOnConnect(true);
        session = emailWithSSL.getMailSession();
        assertNotNull("SSL Socket Factory should exist", session.getProperty("mail.smtp.socketFactory.class"));
        assertEquals("javax.net.ssl.SSLSocketFactory", session.getProperty("mail.smtp.socketFactory.class"));

        // Test Case #2: Missing host name
        Email emailWithoutHost = new EmailConcrete();
        
        try {
            emailWithoutHost.getMailSession();
            fail("Expected EmailException due to missing hostname.");
        } catch (EmailException e) {
            assertEquals("Cannot find valid hostname for mail session", e.getMessage());
        }
    }

    // Test case for 'getSentDate'
    @Test
    public void testGetSentDate() {
        // Test Case #1: Set and retrieve sent date
        Date sentDate = new Date();
        email.setSentDate(sentDate);
        assertEquals(sentDate, email.getSentDate());

        // Test Case #2: Retrieve default sent date
        Email newEmail = new EmailConcrete();
        assertNotNull(newEmail.getSentDate());
    }

    // Test case for 'getSocketConnectionTimeout'
    @Test
    public void testGetSocketConnectionTimeout() {
        // Test Case #1: Verify default socket connection timeout
        int timeout = email.getSocketConnectionTimeout();
        assertEquals(60000, timeout);
    }

    // Test case for 'setFrom'
    @Test
    public void testSetFrom() throws Exception {
        // Test Case #1: Set valid From address
        email.setFrom("sentFrom@example.com");
        assertEquals("sentFrom@example.com", email.getFromAddress().getAddress());

        // Test Case #2: Invalid email format
        try {
            email.setFrom("Invalid Email");
            fail("Expected an exception for invalid email format.");
        } catch (Exception e) {
            assertTrue(true);
        }
    }
}
