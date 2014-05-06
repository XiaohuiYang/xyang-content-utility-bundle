package com.adobe.acs.imp.performcetest;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.result.Hit;

@Component(immediate=true, label="Campaign query2", metatype=true)
@Service
@Property(name="sling.servlet.paths", value="/bin/xyang/allcampaigns2")
public class CampaignQuery2 extends SlingAllMethodsServlet{

	private static final long serialVersionUID = 3869722423773214699L;
	Logger log = LoggerFactory.getLogger(this.getClass());
	@Property(label="end date, format like 'yyyy-MM-dd'", value="2014-01-01")
	private static final String PROPERTY_ENDDATE="enddate";

	@Property(label="start date,format like 'yyyy-MM-dd'",value="2013-01-01")
	private static final String PROPERTY_STARTDATE="startdate";
	private ResourceResolver resolver;
	
	private Calendar eDate =  Calendar.getInstance();
	private Calendar sDate =  Calendar.getInstance();
	
	protected void activate(ComponentContext ctx) {
		final Dictionary<?, ?> props = ctx.getProperties();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			sDate.setTime(df.parse((String)props.get(PROPERTY_STARTDATE)));
			eDate.setTime(df.parse((String)props.get(PROPERTY_ENDDATE)));
		} catch (ParseException e) {
			log.debug("", e);
		}
	}
	
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	@Override
	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		resolver = request.getResourceResolver();
		StringBuilder sb = new StringBuilder();
		log.debug("--------------get all campaign servlet-------");
		List<Hit> list = CampaignHelper2.getAllCampaigns(resolver);
		List<Hit> campaigns = null;
		sb.append("Get Hits : " + list.size() + "\r\n");
		log.debug("-----------beign to loop all the hits------" + list.size());
		campaigns = filter(list);
		log.debug("--------end to loop all the hits-----" + campaigns.size());
		sb.append("\r\nAfter filter : " + campaigns.size());
		try {
			response.getWriter().print(sb.toString());
			response.getWriter().flush();
		}catch(IOException e) {
				log.error("Error", e);
		}
		finally {
			if (resolver != null) {
				resolver.close();
			}
		}

	}

	private List<Hit> filter(List<Hit> list) {
		List <Hit> campaigns = new ArrayList<Hit>();
		Session session = resolver.adaptTo(Session.class);
		for (Hit hit:list) {
			String path = "";
			try {
				path = hit.getPath();
				javax.jcr.Property startValue;
//				Node cam = hit.getNode();
					Node cam = session.getNode(hit.getPath());
//					startValue = cam.getProperty("jcr:mixinTypes");
//					Calendar startDate = startValue.getDate();
//					if (startDate.after(eDate)) {
//						continue;
//					}
//					javax.jcr.Property endValue = cam.getProperty("endDate");
//					Calendar endDate = endValue.getDate();
//					if (endDate.before(sDate)) {
//						continue;
//					}
					campaigns.add(hit);
			} catch (PathNotFoundException e) {
				//log.debug("Campaign does not have property :"+ path);
				continue;
			} catch (RepositoryException e) {
				log.debug("Unkown error.", e);
				continue;
			}
		}
		return campaigns;
	}

}
