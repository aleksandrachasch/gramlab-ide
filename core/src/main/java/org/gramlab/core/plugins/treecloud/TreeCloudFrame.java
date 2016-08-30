package org.gramlab.core.plugins.treecloud;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.batik.apps.rasterizer.SVGConverterException;
import org.apache.batik.swing.JSVGCanvas;
import org.w3c.dom.svg.SVGDocument;

public class TreeCloudFrame extends JFrame{
	
	public Tree currentTree;
	
	private JMenu createFileMenu(){
		JMenu m = new JMenu("File");
		
		JMenu save = new JMenu("Save As");
		JMenuItem svg = new JMenuItem(new AbstractAction("SVG"){
			public void actionPerformed(ActionEvent e){
				try {
					fileChooser("svg");
				} catch (IOException | TransformerFactoryConfigurationError | TransformerException
						| SVGConverterException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		JMenuItem pdf = new JMenuItem(new AbstractAction("PDF"){
			public void actionPerformed(ActionEvent e){
				try {
					fileChooser("pdf");
				} catch (IOException | TransformerFactoryConfigurationError | TransformerException
						| SVGConverterException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		
		JMenuItem jpg = new JMenuItem(new AbstractAction("JPEG"){
			public void actionPerformed(ActionEvent e){
				try {
					fileChooser("jpg");
				} catch (IOException | TransformerFactoryConfigurationError | TransformerException
						| SVGConverterException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		JMenuItem newick = new JMenuItem(new AbstractAction("Newick"){
			public void actionPerformed(ActionEvent e){
				try {
					fileChooser("newick");
				} catch (IOException | TransformerFactoryConfigurationError | TransformerException
						| SVGConverterException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		m.add(save);
		save.add(svg);
		save.add(pdf);
		save.add(jpg);
		save.add(newick);
		return m;
	}
	
	private JMenu createEditMenu(){
		JMenu m = new JMenu("Edit");
		JMenu colormode = new JMenu("Color Mode");
		JMenuItem rb  = new JMenuItem(new AbstractAction("Red & Blue"){
			public void actionPerformed(ActionEvent e){
				try {
					changeColor("Red & blue");
				} catch (IOException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		JMenuItem grayscale = new JMenuItem(new AbstractAction("Grayscale"){
			public void actionPerformed(ActionEvent e){
				try {
					changeColor("Grayscale");
				} catch (IOException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		JMenuItem target = new JMenuItem(new AbstractAction("Target"){
			public void actionPerformed(ActionEvent e){
				try {
					changeColor("Target");
				} catch (IOException | InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		colormode.add(rb);
		colormode.add(grayscale);
		colormode.add(target);
		m.add(colormode);
		return m;
	}
	
	private JMenu createHelpMenu(){
		JMenu m = new JMenu("Help");
		JMenuItem manual = new JMenuItem(new AbstractAction("TreeCloud Manual"){
			public void actionPerformed(ActionEvent e){
				try {
					Desktop.getDesktop().open(new File("C:/ManualTreecloud.pdf"));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		JMenuItem website = new JMenuItem(new AbstractAction("Website"){
			public void actionPerformed(ActionEvent e){
				try {
					Desktop.getDesktop().browse(new URI("http://treecloud.univ-mlv.fr/"));
				} catch (IOException | URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		m.add(manual);
		m.add(website);
		return m;
	}
	
	
	
	private JMenuBar createMenuBar(){
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(createFileMenu());
		menuBar.add(createEditMenu());
		menuBar.add(createHelpMenu());
		return menuBar;
	}
	
	private void fileChooser(String format) throws IOException, TransformerFactoryConfigurationError, TransformerException, SVGConverterException{
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File(currentTree.corpuspath));
		chooser.setMultiSelectionEnabled(false);
		chooser.setDialogType(JFileChooser.SAVE_DIALOG);
		int ret = chooser.showSaveDialog(null);
		if(ret == JFileChooser.APPROVE_OPTION){
			if(format=="svg"){
				String f = chooser.getSelectedFile().getAbsolutePath() + ".svg";
				TreeExport.exportAsSvg(currentTree.svgDoc, f);
			}else if(format=="pdf"){
				String f = chooser.getSelectedFile().getAbsolutePath() + ".pdf";
				TreeExport.exportAsPdf(currentTree.svgDoc, f);
			}else if(format=="jpg"){
				String f = chooser.getSelectedFile().getAbsolutePath() + ".jpg";
				TreeExport.exportAsJpeg(currentTree.svgDoc, f);
			}else{
				String f = chooser.getSelectedFile().getAbsolutePath() + ".newick";
				TreeExport.exportAsNewick(currentTree.newick, f);
			}
		}
	}
	
	private void changeColor(String color) throws IOException, InterruptedException{
		Tree tmp = new Tree();
		tmp.setCorpusPath(currentTree.corpuspath);
		tmp.findFiles(tmp.corpuspath);
		tmp.removestopwords = currentTree.removestopwords;
		tmp.setLanguage(currentTree.language);
		tmp.setNumberOfTaxa(currentTree.numberoftaxa);
		tmp.setColorMode(color);
		
		tmp.setDistanceMatrix();
		tmp.performNJ();
		tmp.performEqualAngle();
		tmp.drawTree();
		this.getContentPane().removeAll();
		JSVGCanvas c = new JSVGCanvas();
		c.setSVGDocument((SVGDocument) tmp.svgDoc);
		this.getContentPane().add(c);
		this.revalidate();
		this.repaint();
	}
	
	public void paintFrame(){
		this.setTitle("TreeCloud");
		this.setSize(500, 500);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setJMenuBar(createMenuBar());
		JSVGCanvas c = new JSVGCanvas();
		c.setSVGDocument((SVGDocument) currentTree.svgDoc);
		this.getContentPane().add(c);
		this.setVisible(true);
	}

}
