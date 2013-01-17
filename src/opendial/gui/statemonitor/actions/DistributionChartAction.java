package opendial.gui.statemonitor.actions;

import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;

import org.jfree.util.Log;

import opendial.arch.Settings;
import opendial.arch.Logger;
import opendial.bn.distribs.ProbDistribution;
import opendial.gui.statemonitor.DistributionViewer;
import opendial.gui.statemonitor.DialogueStatePopup;
import opendial.inference.InferenceAlgorithm;
import opendial.inference.queries.ProbQuery;
import opendial.state.DialogueState;

@SuppressWarnings("serial")
public final class DistributionChartAction extends AbstractAction {

	// logger
	public static Logger log = new Logger("DistributionChartAction", Logger.Level.DEBUG);

	/**
	 * 
	 */
	private DialogueStatePopup graphViewerPopupMenu;
	String queryVariable;
	
	public DistributionChartAction(DialogueStatePopup graphViewerPopupMenu, String name, String queryVariable) {
		super(name);
		this.graphViewerPopupMenu = graphViewerPopupMenu;
		this.queryVariable = queryVariable;
	}
	/**
	 *
	 * @param arg0
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		try {
			InferenceAlgorithm algorithm = Settings.inferenceAlgorithm.newInstance();
			DialogueState state = this.graphViewerPopupMenu.getViewer().getDialogueState();
			ProbDistribution distrib = algorithm.queryProb(new ProbQuery(state, queryVariable)); 
			DistributionViewer.showDistributionViewer(distrib);
			this.graphViewerPopupMenu.getViewer().getPickedVertexState().pick(queryVariable, false);
		} catch (Exception e) {
			DialogueStatePopup.log.debug("problem performing the inference for P(" + queryVariable +") " +
					"aborting action: " + e.toString());
		}
		
	}
	
}