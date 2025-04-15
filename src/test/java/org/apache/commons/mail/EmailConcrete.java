package org.apache.commons.mail;

import java.util.Map;

public class EmailConcrete extends Email{
	
	@Override
	public Email setMsg(String msg) throws EmailException {
	    if (msg == null) {
	        throw new EmailException("Message cannot be null");
	    }
	    this.content = msg;
	    this.contentType = "text/plain";
	    return this;
	}

	/**
	 * @return headers
	 */
	public Map<String, String> getHeaders()
	{
		return this.headers;
	}
	
	public String getContentType()
	{
		return this.contentType;
	}
}
