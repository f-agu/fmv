package org.fagu.fmv.mymedia.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;


/**
 * @author Oodrive
 * @author f.agu
 * @created 20 mars 2025 15:08:26
 */
public class ColorKMeans {

	private final List<Color> centroids;

	public ColorKMeans(int k, List<Color> trainingColors) {
		centroids = trainKMeans(k, trainingColors);
	}

	public int classify(Color color) {
		int bestIndex = - 1;
		double minDist = Double.MAX_VALUE;
		for(int i = 0; i < centroids.size(); i++) {
			double dist = colorDistance(color, centroids.get(i));
			if(dist < minDist) {
				minDist = dist;
				bestIndex = i;
			}
		}
		return bestIndex;
	}

	public List<Color> getCentroids() {
		return centroids;
	}

	// ******************************************

	private static List<Color> trainKMeans(int k, List<Color> colors) {
		List<DoublePoint> points = new ArrayList<>();
		for(Color color : colors) {
			points.add(new DoublePoint(new double[] {color.getRed(), color.getGreen(), color.getBlue()}));
		}

		KMeansPlusPlusClusterer<DoublePoint> clusterer = new KMeansPlusPlusClusterer<>(k, 100);
		List<CentroidCluster<DoublePoint>> clusters = clusterer.cluster(points);

		// Récupérer les centres des 32 catégories
		List<Color> centroids = new ArrayList<>();
		double[] coords = null;
		for(CentroidCluster<DoublePoint> cluster : clusters) {
			coords = cluster.getCenter().getPoint();
			centroids.add(new Color((int)coords[0], (int)coords[1], (int)coords[2]));
		}
		return Collections.unmodifiableList(centroids);
	}

	private double colorDistance(Color c1, Color c2) {
		int dr = c1.getRed() - c2.getRed();
		int dg = c1.getGreen() - c2.getGreen();
		int db = c1.getBlue() - c2.getBlue();
		return Math.sqrt(dr * dr + dg * dg + db * db);
	}

}
