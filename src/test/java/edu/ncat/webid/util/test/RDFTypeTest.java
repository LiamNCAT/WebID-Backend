package edu.ncat.webid.util.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.ncat.webid.util.RDFTypes;

public class RDFTypeTest {
	
	@Test
	public void RDFXMLTest() {
		String rdfType = RDFTypes.getType(RDFTypes.RDFXML);
		assertEquals(rdfType, "RDF/XML");
	}
	
	@Test
	public void NTriplesTest() {
		String rdfType = RDFTypes.getType(RDFTypes.NTriples);
		assertEquals(rdfType, "N-TRIPLE");
	}
	
	@Test
	public void N3Test() {
		String rdfType = RDFTypes.getType(RDFTypes.N3);
		assertEquals(rdfType, "N3");
	}
	
	@Test
	public void TurtleTest() {
		String rdfType = RDFTypes.getType(RDFTypes.Turtle);
		assertEquals(rdfType, "TURTLE");
	}

}
