package org.gramlab.core.plugins.treecloud;


import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.xml.transform.TransformerException;

import org.apache.batik.apps.rasterizer.SVGConverterException;
import org.apache.batik.transcoder.TranscoderException;
import org.gramlab.core.gramlab.project.GramlabProjectManager;
import org.gramlab.core.umlv.unitex.common.project.manager.GlobalProjectManager;
import ro.fortsoft.pf4j.Extension;


/**
 * Main class of the TreeCloud plugin
 * @author Aleksandra Chashchina
 *
 */
//@Extension
public class TreeCloud{
	
public JMenu AddMenu() throws IOException, InterruptedException, TransformerException, SVGConverterException, TranscoderException{
		
		JMenu m = new JMenu("TreeCloud");
		
		JMenuItem tr = new JMenuItem(new AbstractAction("Build TreeCloud"){
		/*
		 * Get parameters from Gramlab
		 */
			public void actionPerformed(ActionEvent e){
				String c = GlobalProjectManager.getAs(GramlabProjectManager.class).getCurrentProject().getCorpusDirectory().getAbsolutePath();
				String lang = GlobalProjectManager.getAs(GramlabProjectManager.class).getCurrentProject().getLanguage();
						
				Tree t = new Tree();
				t.setCorpusPath(c);
				System.out.println(t.corpuspath);
				t.findFiles(t.corpuspath);
				while(true){
				if(t.concordhtml==null){
						JOptionPane.showMessageDialog(null, "Cannot build TreeCloud as there is no Concordance in current project.", "Error", JOptionPane.CLOSED_OPTION);
				break;	
				};	
				t.removestopwords = false;
				t.setLanguage(lang);
				t.setNumberOfTaxa(30);
				t.colormode = "Red & blue";
				
				try {
					t.setDistanceMatrix();
				} catch (IOException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				t.performNJ();
				t.performEqualAngle();
				
				t.drawTree();
				TreeCloudFrame f = new TreeCloudFrame();
				f.currentTree = t;
				f.paintFrame();
				break;
			}
			}
		
	    });
		m.add(tr);
		return m;
}
		
		//TreeExport.exportAsJpeg(t.getSvgDoc(), "C:/mytestoutput.jpg");
		//TreeExport.exportAsSvg(t.getSvgDoc(), "C:/mytestout.svg");
		//TreeExport.exportAsNewick(t.getNewickTree(), "C:/newicktestoutput.newick");
	}


