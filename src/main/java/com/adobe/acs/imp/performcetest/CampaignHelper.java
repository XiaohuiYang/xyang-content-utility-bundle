package com.adobe.acs.imp.performcetest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;

import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;

public class CampaignHelper {
	
	static Logger log =  LoggerFactory.getLogger(CampaignHelper.class);
	
	public static List<Hit> getAllCampaigns(ResourceResolver resovler) {
		QueryBuilder builder = resovler.adaptTo(QueryBuilder.class);
		log.debug("------Begin get all Campaigns' query.--------");
		Map<String, String> queryParams = new HashMap<String, String>();
		//queryParams.put("path", "/content/dam/imp/campaigns");
		queryParams.put("property", "sling:resourceType");
		queryParams.put("property.value", "imp/components/campaign");
		queryParams.put("p.hits", "full");
		queryParams.put("p.nodedepth", "1");
		queryParams.put("p.limit", "-1");
		Query query = builder.createQuery(PredicateGroup.create(queryParams), resovler.adaptTo(Session.class));
		List<Hit> list = query.getResult().getHits();
		log.debug("------End get all Campaigns' query.--------" +  list.size());
		return list;		
	}

}
