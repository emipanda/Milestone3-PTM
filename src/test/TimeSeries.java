package test;


import javax.imageio.IIOException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class TimeSeries {

	//new Data-Type which stores column name and an arrayList of the values for each column.
	public class Columns{
		private String name;
		private ArrayList<Float> floats;

		public Columns(String name){
			this.name = name;
			this.floats = new ArrayList<Float>();
		}
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public ArrayList<Float> getFloats() {
			return floats;
		}

		public void setFloats(ArrayList<Float> floats) {
			this.floats = floats;
		}


	}

	//Data-Members
	private String csvName;
	private Columns[] cols;
	private int numOfValues;
	private int numOfFeatures;

	public int getNumOfValues() {
		if(this.getCols() != null)
			return this.getCols()[0].getFloats().size();
		return 0;
	}

	public void setNumOfValues(int numOfValues) {
		this.numOfValues = numOfValues;
	}

	public int getNumOfFeatures(){
		if(this.getCols() != null)
			return this.getCols().length;
		return 0;
	}



	//TimeSeries Ctor
	public TimeSeries(String csvFileName) {
		this.csvName = csvFileName;
		this.readCsv();

	}
	//returns value by column name and index.
	public float getValue(String colName, int index) throws Exception {
		for (int i = 0; i < cols.length; i++) {
			if(getCols()[i].getName().equals(colName))
				return getCols()[i].getFloats().get(index);
		}
			throw new Exception("Column or Index not Found");
	}

	public float[] getValuesArr(String colName){
		float[] valuesArr = new float[getNumOfValues()];
		ArrayList<Float> valuesAL = new ArrayList<Float>();
		for (int i = 0; i < cols.length; i++) {
			if(getCols()[i].getName().equals(colName))
				valuesAL = getCols()[i].getFloats();
		}
		for (int k = 0; k < getNumOfValues(); k++) {
			valuesArr[k] = valuesAL.get(k);
		}
		return  valuesArr;
	}
	//returns all the table
	public Columns[] getCols() {
		return cols;
	}

	public void readCsv(){
		String line = "";
		String splitBy = ",";
		try {
			BufferedReader br = new BufferedReader(new FileReader(csvName)); //Reading the csv file
			int i = 0 ;
			while ((line = br.readLine()) != null)   //returns a Boolean value
			{
				String[] values = line.split(splitBy);    // use comma as separator
				if (i == 0) {
					this.cols = new Columns[values.length]; //number of columns (A,B,C,D)
					for (int j = 0; j < values.length; j++) {
							this.cols[j] = new Columns(values[j]);
					}
					i++;
				}
				else {
					for (int j = 0; j < values.length; j++) {
						cols[j].getFloats().add(Float.parseFloat(values[j]));
					}
				}
			}
			br.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
