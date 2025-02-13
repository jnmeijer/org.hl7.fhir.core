package org.hl7.fhir.convertors.txClient;

/*
  Copyright (c) 2011+, HL7, Inc.
  All rights reserved.
  
  Redistribution and use in source and binary forms, with or without modification, 
  are permitted provided that the following conditions are met:
    
   * Redistributions of source code must retain the above copyright notice, this 
     list of conditions and the following disclaimer.
   * Redistributions in binary form must reproduce the above copyright notice, 
     this list of conditions and the following disclaimer in the documentation 
     and/or other materials provided with the distribution.
   * Neither the name of HL7 nor the names of its contributors may be used to 
     endorse or promote products derived from this software without specific 
     prior written permission.
  
  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
  INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
  WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
  POSSIBILITY OF SUCH DAMAGE.
  
 */


import org.hl7.fhir.convertors.conv30_50.resources30_50.TerminologyCapabilities30_50;
import org.hl7.fhir.convertors.factory.VersionConvertorFactory_30_50;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.utils.client.FHIRToolingClient;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.r5.model.*;
import org.hl7.fhir.r5.terminologies.TerminologyClient;
import org.hl7.fhir.utilities.ToolingClientLogger;
import org.hl7.fhir.utilities.Utilities;

import java.net.URISyntaxException;
import java.util.Map;

public class TerminologyClientR3 implements TerminologyClient {

  private final FHIRToolingClient client; // todo: use the R2 client

  public TerminologyClientR3(String address) throws URISyntaxException {
    client = new FHIRToolingClient(address);
  }

  @Override
  public TerminologyCapabilities getTerminologyCapabilities() throws FHIRException {
    return TerminologyCapabilities30_50.convertTerminologyCapabilities(client.getTerminologyCapabilities(), false);
  }

  @Override
  public String getAddress() {
    return client.getAddress();
  }

  @Override
  public ValueSet expandValueset(ValueSet vs, Parameters p, Map<String, String> params) throws FHIRException {
    org.hl7.fhir.dstu3.model.ValueSet vs2 = (org.hl7.fhir.dstu3.model.ValueSet) VersionConvertorFactory_30_50.convertResource(vs);
    org.hl7.fhir.dstu3.model.ExpansionProfile p2 = (org.hl7.fhir.dstu3.model.ExpansionProfile) VersionConvertorFactory_30_50.convertResource(p);
    vs2 = client.expandValueset(vs2, p2, params); // todo: second parameter
    return (ValueSet) VersionConvertorFactory_30_50.convertResource(vs2);
  }

  @Override
  public Parameters validateCS(Parameters pin) throws FHIRException {
    org.hl7.fhir.dstu3.model.Parameters p2 = (org.hl7.fhir.dstu3.model.Parameters) VersionConvertorFactory_30_50.convertResource(pin);
    p2 = client.operateType(org.hl7.fhir.dstu3.model.CodeSystem.class, "validate-code", p2);
    return (Parameters) VersionConvertorFactory_30_50.convertResource(p2);
  }

  @Override
  public Parameters validateVS(Parameters pin) throws FHIRException {
    org.hl7.fhir.dstu3.model.Parameters p2 = (org.hl7.fhir.dstu3.model.Parameters) VersionConvertorFactory_30_50.convertResource(pin);
    p2 = client.operateType(org.hl7.fhir.dstu3.model.ValueSet.class, "validate-code", p2);
    return (Parameters) VersionConvertorFactory_30_50.convertResource(p2);
  }

  @Override
  public TerminologyClient setTimeout(int i) {
    client.setTimeout(i);
    return this;
  }

  @Override
  public TerminologyClient setLogger(ToolingClientLogger txLog) {
    client.setLogger(txLog);
    return this;
  }

  @Override
  public TerminologyClient setRetryCount(int retryCount) throws FHIRException {
    client.setRetryCount(retryCount);
    return this;
  }

  @Override
  public CapabilityStatement getCapabilitiesStatementQuick() throws FHIRException {
    return (CapabilityStatement) VersionConvertorFactory_30_50.convertResource(client.getCapabilitiesStatementQuick());
  }

  @Override
  public Parameters lookupCode(Map<String, String> params) throws FHIRException {
    return (Parameters) VersionConvertorFactory_30_50.convertResource(client.lookupCode(params));
  }

  @Override
  public int getRetryCount() throws FHIRException {
    return client.getRetryCount();
  }

  @Override
  public Bundle validateBatch(Bundle batch) {
    return (Bundle) VersionConvertorFactory_30_50.convertResource(client.transaction((org.hl7.fhir.dstu3.model.Bundle) VersionConvertorFactory_30_50.convertResource(batch)));
  }

  @Override
  public CanonicalResource read(String type, String id) {
    Class<Resource> t;
    try {
      t = (Class<Resource>) Class.forName("org.hl7.fhir.dstu3.model." + type);// todo: do we have to deal with any resource renaming? Use cases are limited...
    } catch (ClassNotFoundException e) {
      throw new FHIRException("Unable to fetch resources of type " + type + " in R2");
    }
    org.hl7.fhir.dstu3.model.Resource r3 = client.read(t, id);
    if (r3 == null) {
      throw new FHIRException("Unable to fetch resource " + Utilities.pathURL(getAddress(), type, id));
    }
    org.hl7.fhir.r5.model.Resource r5 = VersionConvertorFactory_30_50.convertResource(r3);
    if (r5 != null) {
      throw new FHIRException("Unable to convert resource " + Utilities.pathURL(getAddress(), type, id) + " to R5 (internal representation)");
    }
    if (!(r5 instanceof CanonicalResource)) {
      throw new FHIRException("Unable to convert resource " + Utilities.pathURL(getAddress(), type, id) + " to R5 canonical resource (internal representation)");
    }
    return (CanonicalResource) r5;
  }


}