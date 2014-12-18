package parser;

import org.junit.Test;

import at.ac.tuwien.ase09.parser.PWatchToEPLCompiler;

public class PWatchToEPLCompilerTest {

	@Test
	public void simpleTest() {
		String pwatchExpression = "ENTERPRISEVALUE > 10.0";
		Long valuePaperId = 1L;
		String epl = PWatchToEPLCompiler.compile(pwatchExpression, valuePaperId);
		System.out.println(epl);
	}
}
