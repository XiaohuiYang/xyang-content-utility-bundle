package com.adobe.acs.imp.performcetest;

import com.adobe.acs.imp.core.model.Campaign;

public interface CampaignVisitor {
	
	void accept(Campaign campaign);

}
