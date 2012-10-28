import org.junit.Test;

public class TestClusterViz {

	@Test
	public void test() {

		//TODO: use configurations for all data paths.
		ClusterPreparator.of()
				.useRawClusterFile("C:/Users/larysa/Desktop/tmp/out.txt")
				.prepare();
	}

}
