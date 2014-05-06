package com.adobe.yxh;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletException;

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


@Component(metatype = true, label = "Logger Remove",
description = "Servlet for get Logger")
@Service
@Property(name = "sling.servlet.paths", value = "/bin/log/logremove", propertyPrivate = true)

public class RemoteLoggerRemoveServlet extends SlingAllMethodsServlet{
	private static final long serialVersionUID = 1L;
	Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Reference
	SlingSettingsService slingSettings;
	
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		String dirPath = slingSettings.getAbsolutePathWithinSlingHome("logs");
		try {
			response.setHeader("Header-Name", "Header Value");
			response.setContentType("text/plain");
			File dir  = new File(dirPath);
			File[] logs = dir.listFiles();
			for (File item:logs) {
				if (item.getName().startsWith("xyang") && !item.getName().equalsIgnoreCase("xyang000.log")) {
					String name = item.getName();
					if (item.delete()) {
						response.getWriter().println(name + " is deleted.");
					}
				}
			}
			response.flushBuffer();
		} catch (FileNotFoundException e) {
			try {
				response.getWriter().write("File Not Found "+ dirPath);
			} catch (IOException e1) {
				log.error("IOException", e1);
			}
		}catch (Exception e) {
			log.error("Exception", e);
			
		}
	}

}
