package test;

import java.util.ArrayList;
import java.util.List;

public class SimpleAnomalyDetector implements TimeSeriesAnomalyDetector {
	
	public ArrayList<CorrelatedFeatures> correlatedFeatures = new ArrayList<CorrelatedFeatures>();
	public final float threshold = (float) 0.7;


	@Override
	public void learnNormal(TimeSeries ts) {
		int numOfFeatures = ts.getNumOfFeatures();
		int numOfValues = ts.getNumOfValues();
		int i = 0;
		float maxPearsonVal;
		String f1Name = null, f2Name = null;
		float PearsonCorrVal;
		float[] feature1 = new float[numOfValues];
		float[] feature2 = new float[numOfValues];
		while(i < numOfFeatures) {
			maxPearsonVal = 0;
			for (int j = i + 1; j < numOfFeatures; j++) {

				//convert ArrayLists to Array of floats
				for (int k = 0; k < numOfValues; k++) {
					feature1[k] = ts.getCols()[i].getFloats().get(k);
					feature2[k] = ts.getCols()[j].getFloats().get(k);
				}
				PearsonCorrVal = Math.abs(StatLib.pearson(feature1, feature2));
				//find max Pearson value and save columns names according to comparison
				if (PearsonCorrVal > maxPearsonVal) {
					maxPearsonVal = PearsonCorrVal;
					f1Name = ts.getCols()[i].getName();
					f2Name = ts.getCols()[j].getName();
				}
			}
			//found correlated features -> adding them to list
			if(maxPearsonVal >= threshold){
				Line line = StatLib.linear_reg(getArrayOfPoints(ts.getValuesArr(f1Name),ts.getValuesArr(f2Name)));
				float maxLineDevThreshold = getMaxDevThreshold(line,getArrayOfPoints(ts.getValuesArr(f1Name),ts.getValuesArr(f2Name)));
				CorrelatedFeatures cf = new CorrelatedFeatures(f1Name,f2Name,maxPearsonVal,line,maxLineDevThreshold);
				correlatedFeatures.add(cf);
			}
			i++;
		}
	}
	public float getMaxDevThreshold(Line l, Point[] points){
		float maxDev = 0;
		for (int i = 0; i < points.length; i++) {
			if(maxDev < StatLib.dev(points[i],l)){
				maxDev = StatLib.dev(points[i],l);
			}
		}
		return maxDev;
	}
	public Point[] getArrayOfPoints(float[] f1, float[] f2){
		Point[] points = new Point[f1.length];
		for (int i = 0; i < f1.length; i++) {
			Point p = new Point(f1[i],f2[i]);
			points[i] = p;
		}
		return points;
	}


	@Override
	public List<AnomalyReport> detect(TimeSeries ts) {
		ArrayList<AnomalyReport> anomalyReports = new ArrayList<>();
		String feature1,feature2;
		float[] f1Values = new float[ts.getNumOfValues()];
		float[] f2Values = new float[ts.getNumOfValues()];
		List<CorrelatedFeatures> cf = getNormalModel();
		Point[] testPoints;

		//running ofer correlated list -> {<A,C,/,3>, <B,D,/,5>}
		for(CorrelatedFeatures c : cf){
			feature1 = c.feature1;
			feature2 = c.feature2;
			long timeStep;
			AnomalyReport ar;

			f1Values = ts.getValuesArr(feature1);
			f2Values = ts.getValuesArr(feature2);

			//new linear regression -> y=ax+b
			Line line = StatLib.linear_reg(getArrayOfPoints(f1Values,f2Values));

			testPoints = getArrayOfPoints(f1Values,f2Values);
			int index = 1;
			for ( Point point : testPoints) {
				if(StatLib.dev(point,line) > c.threshold*1.05) {
					timeStep = index;
					ar = new AnomalyReport(feature1+"-"+feature2,timeStep);
					anomalyReports.add(ar);
				}
				index++;
			}
		}
		return anomalyReports;
	}

	public List<CorrelatedFeatures> getNormalModel(){
		return correlatedFeatures;
	}
}
