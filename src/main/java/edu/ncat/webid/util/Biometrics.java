package edu.ncat.webid.util;

import java.io.InputStream;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class Biometrics {

	private Model bioDat;
	
	private int id;
	
	public Biometrics(X509Certificate webid) throws CertificateParsingException {
		Properties prop = new Properties();
		
		int numExtractors = 12;
		
		Random random = new Random();
		id = random.nextInt(numExtractors);
		
		bioDat = ModelFactory.createDefaultModel();
		
		Collection<List<?>> san = webid.getSubjectAlternativeNames();
		
		Iterator<List<?>> iter = san.iterator();
		
		List<?> SubAltName = null;
		
		if(iter.hasNext()) {
			SubAltName = iter.next();
		}
		
		bioDat.read((String) SubAltName.get(1));
	}
	
	public double GEFE(ArrayList<Double> fv) {
		double distance = 0.0;
		ResultSet rSet = query();
		
		
		return distance;
	}
	
	private ResultSet query() {
		ResultSet rSet = null;
		StringBuffer qstr = new StringBuffer();
		
		return rSet;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
