package edu.ncat.webid.util.test;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import org.junit.Before;
import org.junit.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import edu.ncat.webid.util.Biometrics;

public class BiometricsTest {
	
	Biometrics bio;
	
	WireMockServer wm = new WireMockRule(options().port(8090).bindAddress("localhost"));

	@Before
	public void setup() {
		
	}
	
	@Test
	public void validDistance() {
		
	}
}
