// =================================================================                                                                   
// Copyright (C) 2011-2013 Pierre Lison (plison@ifi.uio.no)                                                                            
//                                                                                                                                     
// This library is free software; you can redistribute it and/or                                                                       
// modify it under the terms of the GNU Lesser General Public License                                                                  
// as published by the Free Software Foundation; either version 2.1 of                                                                 
// the License, or (at your option) any later version.                                                                                 
//                                                                                                                                     
// This library is distributed in the hope that it will be useful, but                                                                 
// WITHOUT ANY WARRANTY; without even the implied warranty of                                                                          
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU                                                                    
// Lesser General Public License for more details.                                                                                     
//                                                                                                                                     
// You should have received a copy of the GNU Lesser General Public                                                                    
// License along with this program; if not, write to the Free Software                                                                 
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA                                                                           
// 02111-1307, USA.                                                                                                                    
// =================================================================                                                                   

package opendial.domains;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import org.junit.Test;

import opendial.arch.ConfigurationSettings;
import opendial.arch.DialException;
import opendial.arch.DialogueState;
import opendial.arch.DialogueSystem;
import opendial.arch.Logger;
import opendial.bn.Assignment;
import opendial.bn.distribs.ProbDistribution;
import opendial.bn.distribs.discrete.SimpleTable;
import opendial.bn.nodes.BNode;
import opendial.bn.nodes.ChanceNode;
import opendial.bn.values.DoubleVal;
import opendial.bn.values.Value;
import opendial.bn.values.ValueFactory;
import opendial.common.InferenceChecks;
import opendial.common.MiscUtils;
import opendial.gui.GUIFrame;
import opendial.inference.ImportanceSampling;
import opendial.inference.InferenceAlgorithm;
import opendial.inference.NaiveInference;
import opendial.inference.queries.ProbQuery;
import opendial.inference.queries.UtilQuery;
import opendial.inference.VariableElimination;
import opendial.readers.XMLDomainReader;

/**
 * 
 *
 * @author  Pierre Lison (plison@ifi.uio.no)
 * @version $Date::                      $
 *
 */
public class BasicRuleTest2 {

	// logger
	public static Logger log = new Logger("BasicRuleTest", Logger.Level.DEBUG);

	public static final String domainFile = "domains//testing//domain2.xml";
	public static final String domainFile2 = "domains//testing//domain3.xml";

	static Domain domain;

	static InferenceChecks inference;
	static DialogueSystem system;

	static {
		try { 
			domain = XMLDomainReader.extractDomain(domainFile); 
			inference = new InferenceChecks();
			ConfigurationSettings.getInstance().activatePlanner(false);
			ConfigurationSettings.getInstance().activatePruning(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@Test
	public void test() throws DialException, InterruptedException {
				
		system = new DialogueSystem(domain);
		system.startSystem(); 
		MiscUtils.waitUntilStable(system);
		
		system.getState().getNetwork().getNode("a_u^p'").setId("a_u^p");
	 	ProbQuery query = new ProbQuery(system.getState(),"a_u^p");
	 	inference.checkProb(query, new Assignment("a_u^p", "Ask(A)"), 0.63);
	 	inference.checkProb(query, new Assignment("a_u^p", "Ask(B)"), 0.27);
	 	inference.checkProb(query, new Assignment("a_u^p", "None"), 0.1);
	 			
		SimpleTable table = new SimpleTable();
		table.addRow(new Assignment("a_u", "Ask(B)"), 0.8);
		table.addRow(new Assignment("a_u", "None"), 0.2); 
		 
		system.getState().addContent(table, "test");
		MiscUtils.waitUntilStable(system);
		
		query = new ProbQuery(system.getState(),"a_u^p");
	 	inference.checkProb(query, new Assignment("a_u^p", "Ask(A)"), 0.0516);
	 	inference.checkProb(query, new Assignment("a_u^p", "Ask(B)"), 0.907);
	 	inference.checkProb(query, new Assignment("a_u^p", "None"), 0.041);

	 	ProbQuery query2 = new ProbQuery(system.getState(),"i_u");
	 	inference.checkProb(query2, new Assignment("i_u", "Want(A)"), 0.080);
	 	inference.checkProb(query2, new Assignment("i_u", "Want(B)"), 0.9197);

	 	ProbQuery query3 = new ProbQuery(system.getState(),"a_u'");
	 	inference.checkProb(query3, new Assignment("a_u'", "Ask(B)"), 0.918);
	 	inference.checkProb(query3, new Assignment("a_u'", "None"), 0.0820);

	 	ProbQuery query4 = new ProbQuery(system.getState(),"a_u'");
	 	inference.checkProb(query4, new Assignment("a_u'", "Ask(B)"), 0.918);
	 	inference.checkProb(query4, new Assignment("a_u'", "None"), 0.0820);	
	}
	
	
	@Test
	public void test2() throws DialException, InterruptedException {
		 			
		system = new DialogueSystem(domain);
		system.startSystem(); 
		MiscUtils.waitUntilStable(system);
		
		system.getState().getNetwork().getNode("u_u2^p'").setId("u_u2^p");
	 	ProbQuery query = new ProbQuery(system.getState(),"u_u2^p");
	 	inference.checkProb(query, new Assignment("u_u2^p", "Do A"), 0.216);
	 	inference.checkProb(query, new Assignment("u_u2^p", "Please do C"), 0.027);
	 	inference.checkProb(query, new Assignment("u_u2^p", "Could you do B?"), 0.054);
	 	inference.checkProb(query, new Assignment("u_u2^p", "Could you do A?"), 0.162);
	 	inference.checkProb(query, new Assignment("u_u2^p", "none"), 0.19);
	 			
	
		SimpleTable table = new SimpleTable();
		table.addRow(new Assignment("u_u2", "Please do B"), 0.4);
		table.addRow(new Assignment("u_u2", "Do B"), 0.4); 
		 
		system.getState().addContent(table, "test2");
		MiscUtils.waitUntilStable(system);
		
		query = new ProbQuery(system.getState(),"i_u2");
	 	inference.checkProb(query, new Assignment("i_u2", "Want(B)"), 0.6542);
	 	inference.checkProb(query, new Assignment("i_u2", "Want(A)"), 0.1963);
	 	inference.checkProb(query, new Assignment("i_u2", "Want(C)"), 0.0327);
	 	inference.checkProb(query, new Assignment("i_u2", "none"), 0.1168);
	}
	
	
	@Test
	public void test3() throws DialException, InterruptedException {

		system = new DialogueSystem(domain);
		system.startSystem(); 
		MiscUtils.waitUntilStable(system);
		
	 	UtilQuery query = new UtilQuery(system.getState(),"a_m'");
	 	inference.checkUtil(query, new Assignment("a_m'", "Do(A)"), 0.6);
	 	inference.checkUtil(query, new Assignment("a_m'", "Do(B)"), -2.6);
	 
		SimpleTable table = new SimpleTable();
		table.addRow(new Assignment("a_u", "Ask(B)"), 0.8);
		table.addRow(new Assignment("a_u", "None"), 0.2); 
		 
		system.getState().getNetwork().removeNodes(system.getState().getNetwork().getUtilityNodeIds());
		system.getState().getNetwork().getNode("a_u^p'").setId("a_u^p");
		system.getState().addContent(table, "test");
		MiscUtils.waitUntilStable(system);

	 	query = new UtilQuery(system.getState(),"a_m''");
	 	inference.checkUtil(query, new Assignment("a_m''", "Do(A)"), -4.357);
	 	inference.checkUtil(query, new Assignment("a_m''", "Do(B)"), 2.357);	
	}
	
	

	@Test
	public void test4() throws DialException, InterruptedException {
		
		Domain domain2 = XMLDomainReader.extractDomain(domainFile2); 
		DialogueSystem system2 = new DialogueSystem(domain2);
		system2.startSystem(); 
		MiscUtils.waitUntilStable(system2);

		UtilQuery query = new UtilQuery(system2.getState(),Arrays.asList("a_m3'", "obj(a_m3)'"));

	 	inference.checkUtil(query, new Assignment(new Assignment("a_m3'", "Do"),
	 			new Assignment("obj(a_m3)'", "A")), 0.3);
	 	inference.checkUtil(query, new Assignment(new Assignment("a_m3'", "Do"),
	 			new Assignment("obj(a_m3)'", "B")), -1.7);
	 	inference.checkUtil(query, new Assignment(new Assignment("a_m3'", "SayHi"),
	 			new Assignment("obj(a_m3)'", "None")), -0.9);
	 	
	 	assertEquals(6, (new ImportanceSampling()).queryUtility(query).getTable().size());
	}
	


}
