package com.yglab.nlp.perceptron;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.yglab.nlp.io.AbstractPlainTextWriter;
import com.yglab.nlp.model.AbstractModel;
import com.yglab.nlp.model.Index;



/**
 * This abstract class writes the training model file.
 * 
 * @author Younggue Bae
 */
public class PerceptronModelPlainTextWriter extends AbstractPlainTextWriter<AbstractModel> {

	@Override
	public void write(AbstractModel model, File file) throws IOException {
		PerceptronModel perceptronModel = (PerceptronModel) model;
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getPath(), false), "UTF-8"));
		
		Index labelIndex = perceptronModel.getLabelIndex();
		Index featureIndex = perceptronModel.getFeatureIndex();
		double[][] weights = perceptronModel.getWeights();
		
		StringBuilder labelTitles = new StringBuilder();
		writer.write("# of labels = " + labelIndex.size() + "\n");
		writer.write("------------------------------------------------------\n");
		writer.write(" labelIndex" + "\t" + "label" + "\n");
		writer.write("------------------------------------------------------\n");
		for (int i = 0; i < labelIndex.size(); i++) {
			writer.write(" " + i + "\t" + labelIndex.get(i) + "\n");
			if (i < labelIndex.size() - 1) {
				labelTitles.append("label[" + i + "]:" + labelIndex.get(i)).append("\t");
			}
			else {
				labelTitles.append("label[" + i + "]:" + labelIndex.get(i));
			}
		}
		writer.write("------------------------------------------------------\n");
		
		writer.write("\n");

		writer.write("# of features = " + featureIndex.size() + "\n");
		writer.write("------------------------------------------------------\n");
		writer.write(" featureIndex" + "\t" + "feature\n");
		writer.write("------------------------------------------------------\n");
		for (int i = 0; i < featureIndex.size(); i++) {
			writer.write(" " + i + "\t" + featureIndex.get(i) + "\n");
		}
		writer.write("------------------------------------------------------\n");
		
		writer.write("\n");
		
		String header = " featureIndex" + "\t" + labelTitles.toString();
		String bar = "-";
		for (int i = 0; i < header.length() + 10; i++) {
			bar += "-";
		}
		writer.write(bar + "\n");
		writer.write(header + "\n");
		writer.write(bar + "\n");
		for (int i = 0; i < featureIndex.size(); i++) {
			StringBuilder val = new StringBuilder();
			val.append(i).append("\t");
			for (int j = 0; j < labelIndex.size(); j++) {
				if (j < labelIndex.size() - 1) {
					val.append(weights[j][i]).append("\t");
				}
				else {
					val.append(weights[j][i]);
				}
			}
			writer.write(val.toString() + "\n");
		}
		writer.write(bar + "\n");
		
		writer.close();
	}

}
