package edu.ncat.webid.util;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;

public class Biometrics {

	private Model bioDat;
	
	private int id;
	private String personURI;
	
	public Biometrics(X509Certificate webid, int id) throws CertificateParsingException {
		Properties prop = new Properties();
		
		this.id = id;
		
		bioDat = ModelFactory.createDefaultModel();
		
		Collection<List<?>> san = webid.getSubjectAlternativeNames();
		
		Iterator<List<?>> iter = san.iterator();
		
		List<?> SubAltName = null;
		
		if(iter.hasNext()) {
			SubAltName = iter.next();
		}
		personURI = (String) SubAltName.get(1);
		bioDat.read(personURI);
	}
	
	public double compareProbeToGallery(ArrayList<Double> fv) {
		double distance = 0.0;
		double maxDist = 0.0;
		ArrayList<Double>gfv = null;
		
		ResultSet rSet = query();
		
		
		RDFNode featvec = null;
		
		while(rSet.hasNext()) {
			QuerySolution sol = rSet.nextSolution();
			featvec = sol.get("?fv"); 
		}
		
		gfv = new ArrayList<Double>();
		System.out.println(featvec.toString());
		
		List<String> temp = Arrays.asList(featvec.toString().split(","));
		
		for(String t: temp) {
			gfv.add(Double.parseDouble(t));
		}
		
		
		for(int i = 0; i<fv.size(); i++) {
			distance += Math.abs(fv.get(i) - gfv.get(i)); //City Patch (Manhattan) Distance
			maxDist += Math.max(fv.get(i), gfv.get(i));
		}
		
		return (distance/maxDist);
	}
	
	private ResultSet query() {
		ResultSet rSet = null;
		StringBuffer qstr = new StringBuffer();
		
		qstr.append("PREFIX bio: <http://webid-willtest.rhcloud.com/dfe/terms#>");
		qstr.append(" SELECT ?fv ");
		
		qstr.append(" WHERE { ?f a bio:FeatureVector.");
		
		qstr.append(" ?f bio:id \""+id+"\".");
		qstr.append(" ?f bio:represents ?b.");
		qstr.append(" ?f bio:value ?fv.}");
		
		Query query = QueryFactory.create(qstr.toString());
		QueryExecution qexec = QueryExecutionFactory.create(query, bioDat);
		
		try {
			   rSet = qexec.execSelect();
			   
		}
		catch(Exception e) {
			return null;
			
		}
		
		
		return rSet;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
