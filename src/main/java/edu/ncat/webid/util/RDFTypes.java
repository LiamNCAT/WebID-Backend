package edu.ncat.webid.util;

public class RDFTypes {
	public static final String N3 = "text/n3";
	public static final String NTriples = "application/n-triples";
	public static final String RDFXML = "application/rdf+xml";
	public static final String Turtle = "text/turtle";
	
	public static String getType(String mimeType) {
		if(mimeType.equalsIgnoreCase(RDFXML)) {
			return "RDF/XML";
		}
		else if(mimeType.equalsIgnoreCase(NTriples)) {
			return "N-TRIPLE";
		}
		else if(mimeType.equalsIgnoreCase(N3)) {
			return "N3";
		}
		else {
			return "TURTLE";
		}
	}
}
