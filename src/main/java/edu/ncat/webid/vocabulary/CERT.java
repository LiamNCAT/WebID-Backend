package edu.ncat.webid.vocabulary;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public class CERT {
	public static final String uri = "http://www.w3.org/ns/auth/cert#";
	
	public static final Resource RSAPublicKey = ResourceFactory.createResource(uri+"RSAPublicKey");
	public static final Property modulus = ResourceFactory.createProperty(uri+"modulus");
	public static final Property exponent = ResourceFactory.createProperty(uri+"exponent");
	public static final Property key = ResourceFactory.createProperty(uri+"key");
}
