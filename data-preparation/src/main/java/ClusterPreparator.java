import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.ImmutableTable.Builder;
import com.google.common.collect.Lists;

/**
 * 
 * cluster representation as .tsv file.
 */
public class ClusterPreparator {
	private String clusterFile;
	private File rawCluster = null;
	private String dir;

	private ClusterPreparator() {

	}

	public static ClusterPreparator of() {
		return new ClusterPreparator();
	}

	public ClusterPreparator useRawClusterFile(String file) {
		this.clusterFile = file;
		Preconditions.checkArgument(!Strings.isNullOrEmpty(file));
		this.rawCluster = new File(this.clusterFile);
		Preconditions.checkArgument(this.rawCluster.isFile(),
				"please provide a file as parameter");
		this.dir = this.rawCluster.getParent();
		return this;
	}

	/**
	 * will use the same directory as the raw input cluster file.
	 */
	/**
	 * 
	 */
	public void prepare() {
		Scanner clusterScanner = null;
		try {
			final Builder<String, String, Double> clusterBuilder = new ImmutableTable.Builder<String, String, Double>();
			clusterScanner = new Scanner(this.rawCluster);

			while (clusterScanner.hasNextLine()) {
				String dataPointLine = clusterScanner.nextLine();

				Iterable<String> dataPointSplitted = Splitter.on("\t")
						.omitEmptyStrings().trimResults().split(dataPointLine);
				ArrayList<String> cells = Lists.newArrayList(dataPointSplitted);
				String dataPoint = cells.get(0);
				List<String> features = cells.subList(1, cells.size());
				for (String cell : features) {
					String[] elements = cell.split(":");
					if (hasRightFormat(elements)) {
						clusterBuilder.put(dataPoint, elements[0],
								Double.parseDouble(elements[1]));
					}
				}

			}

			ImmutableTable<String, String, Double> cluster = clusterBuilder
					.build();

			ImmutableSet<String> columnHeaders = cluster.columnKeySet();
			String tmp = "DATAPOINT,";
			String headers = Joiner.on(",").join(columnHeaders);
			headers = tmp + headers;

			PrintWriter out = new PrintWriter(new File(this.dir
					+ "/cluster.txt"));
			out.println(headers);

			ImmutableSet<String> rowsNames = cluster.rowKeySet();
			for (String rowName : rowsNames) {
				String valuesAsLine = rowName + ",";
				ArrayList<Double> values = Lists.newArrayList();
				for (String colName : columnHeaders) {
					Double value = cluster.get(rowName, colName);
					values.add(value);

				}
				String valuesAsStr = Joiner.on(",").useForNull("0.0")
						.join(values);
				out.println(valuesAsLine + valuesAsStr);
			}
			out.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			clusterScanner.close();
		}

	}

	private boolean hasRightFormat(String[] elements) {
		boolean cellHasRightFormat = elements.length == 2;
		return cellHasRightFormat;
	}
}
