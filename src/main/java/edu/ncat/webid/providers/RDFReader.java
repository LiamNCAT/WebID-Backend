package edu.ncat.webid.providers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

import edu.ncat.webid.util.RDFTypes;


@Provider
@Consumes({RDFTypes.N3, RDFTypes.NTriples, RDFTypes.RDFXML})
public class RDFReader implements MessageBodyReader<Model>{

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type.isInstance(Model.class);
	}

	@Override
	public Model readFrom(Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
		Model m = ModelFactory.createDefaultModel();
		m.read(entityStream, null, RDFTypes.getType(mediaType.getType()));
		return m;
	}

}
