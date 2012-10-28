import static org.junit.Assert.*;

import org.junit.Test;

public class TestClusterViz {

	@Test
	public void test() {

		ClusterPreparator.of()
				.useRawClusterFile("C:/Users/larysa/Desktop/tmp/out.txt")
				.prepare();
	}

}
