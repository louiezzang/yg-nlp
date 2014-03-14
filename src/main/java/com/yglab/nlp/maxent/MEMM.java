package com.yglab.nlp.maxent;

import java.util.List;

import com.yglab.nlp.maxent.quasinewton.QNTrainer;
import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Datum;
import com.yglab.nlp.model.EventStream;



/**
 * Class for creating maximum-entropy markov model.
 * 
 * @author Younggue Bae
 */
public class MEMM {

	/**
	 * Trains a maximum-entropy markov model.
	 * 
	 * @param trainData
	 */
	public static final AbstractModel trainModel(EventStream<?, ? extends Datum> is) {
		QNTrainer trainer = new QNTrainer();
		return trainer.trainModel(is.getOutputStream());
	}

	/**
	 * Decodes the best tag sequences.
	 * 
	 * @param model
	 * @param candidates
	 * @return
	 */
	public static final List<Datum> decode(AbstractModel model, List<List<Datum>> candidates) {
		Viterbi viterbi = new Viterbi(model.getLabelIndex(), model.getFeatureIndex());
		return viterbi.decode(candidates, model.getWeights());
	}

}