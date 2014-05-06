package com.adobe.yxh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;

import org.apache.commons.io.IOUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.settings.SlingSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Component(metatype = true, label = "Logger Come On",
description = "Servlet for get Logger")
@Service
@Property(name = "sling.servlet.paths", value = "/bin/log/logcome", propertyPrivate = true)

public class RemoteLoggerServlet extends SlingAllMethodsServlet{
	private static final long serialVersionUID = 1L;
	private static final String REMOTE_LOGGER_FILENAME = "filename";
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Reference
	SlingSettingsService slingSettings;
	
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		String logfileName = request.getParameter(REMOTE_LOGGER_FILENAME);
		String filePath = slingSettings.getAbsolutePathWithinSlingHome(logfileName);
		InputStream inputstream = null;
		try {
			inputstream = new FileInputStream(new File(filePath));
			response.setHeader("Header-Name", "Header Value");
			response.setContentType("text/plain");
			IOUtils.copy(inputstream, response.getOutputStream());
			response.flushBuffer();

		} catch (FileNotFoundException e) {
			try {
				response.getWriter().write("File Not Found "+ filePath);
			} catch (IOException e1) {
				log.error("IOException", e1);
			}
		}catch (Exception e) {
			log.error("Exception", e);
			
		}finally {
			if (inputstream != null )
				try {
					inputstream.close();
				} catch (IOException e) {
					log.error("Final",e);
				}
		}
	
	}

}
