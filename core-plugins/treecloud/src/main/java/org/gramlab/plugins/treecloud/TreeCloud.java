package org.gramlab.plugins.treecloud;

import ro.fortsoft.pf4j.PluginWrapper;
import com.github.zafarkhaja.semver.Version;
import org.gramlab.api.ConcordanceTree;
import org.gramlab.api.GramLabPlugin;
import ro.fortsoft.pf4j.Extension;

/**
 * A class that "connects" TreeCloud plugin with GramLab.
 * All classes necessary for plugin work (i.e., TreeCloudFunctions,
 * NJ-algorithm, layout technique implementation) will be
 * situated in org.gramlab.plugins.treecloud package.
 * 
 * @author Aleksandra Chashchina
 *
 */

public class TreeCloud extends GramLabPlugin{
	public TreeCloud(PluginWrapper wrapper) {
		super(wrapper);
	}
	
	
	@Override
	public void onInstall() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onUpgrade(Version previousVersion) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onUninstall() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onException() {
		// TODO Auto-generated method stub
		
	}
	
	@Extension
	public static class TreeCloudConcordance implements ConcordanceTree{
		
		/*
		 * here should be defined a method that will return 
		 * a JFrame with a picture of the tree.
		 */
	}
	

}
