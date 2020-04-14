/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openmrs.module.hivtestingservices.api.shr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.Attributable;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.GlobalProperty;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.PersonName;
import org.openmrs.api.APIException;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.idgen.service.IdentifierSourceService;
import org.openmrs.util.PrivilegeConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 *
 * @author tedb19
 */
public class SHRUtils {
    public static final String NATIONAL_ID = "49af6cdc-7968-4abb-bf46-de10d7f4859f";
    public static final String ALIEN_NUMBER = "e1e80b5c-6d7e-11ea-bc55-0242ac130003";
    public static final String PASSPORT_NUMBER = "e1e80daa-6d7e-11ea-bc55-0242ac130003";
    public static final String TELEPHONE_CONTACT = "b2c38640-2603-4629-aebd-3b54f33f1e3a";


    public static SHR getSHR(String SHRStr) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        SHR shr = null;
        try {
            shr = mapper.readValue(SHRStr, new TypeReference<SHR>() {
            });
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return shr;
    }

    public static String getJSON(SHR shr) {
        ObjectMapper mapper = new ObjectMapper();
        String JSONStr = "";
        try {
            JSONStr = mapper.writeValueAsString(shr);
        } catch (JsonProcessingException ex) {
            System.out.println(ex.getMessage());
        }
        return JSONStr;
    }

    public static String getHivTestSampleData () {
        return "'HIV_TEST':[{'DATE':'20180101','RESULT':'POSITIVE/NEGATIVE/INCONCLUSIVE','TYPE':" +
                "'SCREENING/CONFIRMATORY','FACILITY':'10829','STRATEGY':'HP/NP/VI/VS/HB/MO/O'," +
                "'PROVIDER_DETAILS':{'NAME':'AFYA JIJINI'}}]";
    }

    public static String getPatientDemographicsSampleData () {
        return  "{\n" +
                "\t\"DEMOGRAPHICS\": {\n" +
                "\t\t\"FIRST_NAME\": \"JOHN\",\n" +
                "\t\t\"MIDDLE_NAME\": \"PARKER\",\n" +
                "\t\t\"LAST_NAME\": \"DBOO\",\n" +
                "\t\t\"DATE_OF_BIRTH\": \"20121010\",\n" +
                "\t\t\"SEX\": \"F\",\n" +
                "\t\t\"PHONE_NUMBER\": \"254720278654\",\n" +
                "\t\t\"MARITAL_STATUS\": \"SINGLE\"\n" +
                "\t}\n" +
                "}";


    }

    public static String getPatientIdentifiersSampleData () {
        return "{\"IDENTIFIERS\":[{\"ID\":\"12345678-ADFGHJY-0987654-NHYI890\",\"TYPE\":\"CARD\",\"FACILITY\":\"40829\"},{\"ID\":\"37645678\",\"TYPE\":\"HEI\",\"FACILITY\":\"10829\"},{\"ID\":\"12345678\",\"TYPE\":\"CCC\",\"FACILITY\":\"10829\"},{\"ID\":\"001\",\"TYPE\":\"HTS\",\"FACILITY\":\"10829\"}]}";
    }

    public static String getPatientAddressSampleData () {
        return "'ADDRESS': {\n" +
                "\t'VILLAGE': 'KWAKIMANI',\n" +
                "        'WARD': 'KIMANINI',\n" +
                "        'SUB_COUNTY': 'KIAMBU EAST',\n" +
                "        'COUNTY': 'KIAMBU',\n" +
                "        'NEAREST_LANDMARK': 'KIAMBU EAST'\n" +
                "}";
    }

    public static String getCardDetails () {
        return "'CARD_DETAILS': {\n" +
                "    'STATUS': 'ACTIVE/INACTIVE',\n" +
                "    'REASON': 'LOST/DEATH/DAMAGED',\n" +
                "    'LAST_UPDATED': '20180101',\n" +
                "    'LAST_UPDATED_FACILITY': '10829'\n" +
                "  }";
    }
   /*public static boolean isValidJSON(String SHRStr) {
        try {
            JsonFactory factory = new JsonFactory();
            JsonParser parser = factory.createParser(SHRStr);

            
            while (!parser.isClosed()) {
                JsonToken jsonToken = parser.nextToken();

                System.out.println("jsonToken = " + jsonToken);
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }*/

    public static String fetchRequestBody(BufferedReader reader) {
        String requestBodyJsonStr = "";
        try {

            BufferedReader br = new BufferedReader(reader);
            String output = "";
            while ((output = reader.readLine()) != null) {
                requestBodyJsonStr += output;
            }


        } catch (IOException e) {

            System.out.println("IOException: " + e.getMessage());

        }
        return requestBodyJsonStr;
    }

    /**
     * Create a new patient
     * @param
     * @param dob
     * @param sex
     * @param idNo
     * @return
     */
    public static Patient createPatient(String fName, String mName, String lName, Date dob, String sex, String idNo, String ppNumber, String alienNumber) {

        Patient patient = null;

        if (fName != null && !fName.equals("") && lName != null && !lName.equals("") ) {

            patient = new Patient();
            if (sex == null || sex.equals("") || StringUtils.isEmpty(sex)) {
                sex = "U";
            }
            patient.setGender(sex);
            PersonName pn = new PersonName();
            pn.setGivenName(fName);
            pn.setFamilyName(lName);
            if (mName != null && !mName.equals("")) {
                pn.setMiddleName(mName);
            }

            patient.addName(pn);
            patient.setBirthdate(dob);
            patient.setBirthdateEstimated(true);

            PatientIdentifier openMRSID = generateOpenMRSID();
            boolean hasPreferredId = false;

            if (ppNumber != null && !ppNumber.equals("")) {
                PatientIdentifierType ppType = Context.getPatientService().getPatientIdentifierTypeByUuid(PASSPORT_NUMBER);

                PatientIdentifier pno = new PatientIdentifier();
                pno.setIdentifierType(ppType);
                pno.setIdentifier(ppNumber);
                pno.setPreferred(true);
                patient.addIdentifier(pno);
                hasPreferredId = true;
            }

            if (alienNumber != null && !alienNumber.equals("")) {
                PatientIdentifierType alienType = Context.getPatientService().getPatientIdentifierTypeByUuid(ALIEN_NUMBER);

                PatientIdentifier alienNo = new PatientIdentifier();
                alienNo.setIdentifierType(alienType);
                alienNo.setIdentifier(idNo);
                if (!hasPreferredId) {
                    alienNo.setPreferred(true);
                }
                patient.addIdentifier(alienNo);
            }

            if (idNo != null && !idNo.equals("")) {
                PatientIdentifierType upnType = Context.getPatientService().getPatientIdentifierTypeByUuid(NATIONAL_ID);

                PatientIdentifier upn = new PatientIdentifier();
                upn.setIdentifierType(upnType);
                upn.setIdentifier(idNo);
                if(!hasPreferredId) {
                    upn.setPreferred(true);
                }
                patient.addIdentifier(upn);
            }
            if (!hasPreferredId) {
                openMRSID.setPreferred(true);
            }
            patient.addIdentifier(openMRSID);

        }
        return patient;
    }

    public static Patient savePatient(Patient patient) {
        Patient p = Context.getPatientService().savePatient(patient);
        return p;

    }

    public static PatientIdentifier generateOpenMRSID() {
        PatientIdentifierType openmrsIDType = Context.getPatientService().getPatientIdentifierTypeByUuid("dfacd928-0370-4315-99d7-6ec1c9f7ae76");
        String generated = Context.getService(IdentifierSourceService.class).generateIdentifier(openmrsIDType, "Registration");
        PatientIdentifier identifier = new PatientIdentifier(generated, openmrsIDType, getDefaultLocation());
        return identifier;
    }

    /**
     * Gets the KenyaEMR default location
     * TODO: can idgen generate ID without location attribute?
     * @return
     */
    public static Location getDefaultLocation() {
        try {
            Context.addProxyPrivilege(PrivilegeConstants.VIEW_LOCATIONS);
            Context.addProxyPrivilege(PrivilegeConstants.VIEW_GLOBAL_PROPERTIES);
            String GP_DEFAULT_LOCATION = "kenyaemr.defaultLocation";
            GlobalProperty gp = Context.getAdministrationService().getGlobalPropertyObject(GP_DEFAULT_LOCATION);
            return gp != null ? ((Location) gp.getValue()) : null;
        }
        finally {
            Context.removeProxyPrivilege(PrivilegeConstants.VIEW_LOCATIONS);
            Context.removeProxyPrivilege(PrivilegeConstants.VIEW_GLOBAL_PROPERTIES);
        }

    }

    /**
     * Checks if a patient already has encounter of same type and form on same date
     * @param enctype
     * @param form
     * @param patient
     * @param date
     * @return
     */
    public static boolean hasEncounterOnDate(EncounterType enctype, Form form, Patient patient, Date date) {
        List<Encounter> encounters = Context.getEncounterService().getEncounters(patient, null, date, date, Collections.singleton(form), Collections.singleton(enctype), null, null, null, false);
        return encounters.size() > 0;

    }

    /**
     * set up person address
     * @param patient
     * @param nationality
     * @param county
     * @param subCounty
     * @param ward
     * @param postaladdress
     * @return
     */
    public static Patient addPersonAddresses(Patient patient, String nationality, String county, String subCounty, String ward, String postaladdress) {

        Set<PersonAddress> patientAddress = patient.getAddresses();
        if (patientAddress.size() > 0) {
            for (PersonAddress address : patientAddress) {
                if (nationality != null) {
                    address.setCountry(nationality);
                }
                if (county != null) {
                    address.setCountyDistrict(county);
                }
                if (subCounty != null) {
                    address.setStateProvince(subCounty);
                }
                if (ward != null) {
                    address.setAddress4(ward);
                }

                if (postaladdress != null) {
                    address.setAddress1(postaladdress);
                }
                patient.addAddress(address);
            }
        } else {
            PersonAddress pa = new PersonAddress();
            if (nationality != null) {
                pa.setCountry(nationality);
            }
            if (county != null) {
                pa.setCountyDistrict(county);
            }
            if (subCounty != null) {
                pa.setStateProvince(subCounty);
            }
            if (ward != null) {
                pa.setAddress4(ward);
            }

            if (postaladdress != null) {
                pa.setAddress1(postaladdress);
            }
            patient.addAddress(pa);
        }
        return patient;
    }

    /**
     * Set patient attributes
     * @param patient
     * @param phone
     * @param nokName
     * @param nokPhone
     * @return
     */
    public static Patient addPersonAttributes(Patient patient, String phone, String nokName, String nokPhone) {

        String NEXT_OF_KIN_CONTACT = "342a1d39-c541-4b29-8818-930916f4c2dc";
        String NEXT_OF_KIN_NAME = "830bef6d-b01f-449d-9f8d-ac0fede8dbd3";
        String TELEPHONE_CONTACT = "b2c38640-2603-4629-aebd-3b54f33f1e3a";


        PersonAttributeType phoneType = Context.getPersonService().getPersonAttributeTypeByUuid(TELEPHONE_CONTACT);
        PersonAttributeType nokNametype = Context.getPersonService().getPersonAttributeTypeByUuid(NEXT_OF_KIN_NAME);
        PersonAttributeType nokContacttype = Context.getPersonService().getPersonAttributeTypeByUuid(NEXT_OF_KIN_CONTACT);

        if (phone != null) {
            PersonAttribute attribute = new PersonAttribute(phoneType, phone);

            try {
                Object hydratedObject = attribute.getHydratedObject();
                if (hydratedObject == null || "".equals(hydratedObject.toString())) {
                    // if null is returned, the value should be blanked out
                    attribute.setValue("");
                } else if (hydratedObject instanceof Attributable) {
                    attribute.setValue(((Attributable) hydratedObject).serialize());
                } else if (!hydratedObject.getClass().getName().equals(phoneType.getFormat())) {
                    // if the classes doesn't match the format, the hydration failed somehow
                    // TODO change the PersonAttribute.getHydratedObject() to not swallow all errors?
                    throw new APIException();
                }
            } catch (APIException e) {
                //.warn("Got an invalid value: " + value + " while setting personAttributeType id #" + paramName, e);
                // setting the value to empty so that the user can reset the value to something else
                attribute.setValue("");
            }
            patient.addAttribute(attribute);
        }

        if (nokName != null) {
            PersonAttribute attribute = new PersonAttribute(nokNametype, nokName);

            try {
                Object hydratedObject = attribute.getHydratedObject();
                if (hydratedObject == null || "".equals(hydratedObject.toString())) {
                    // if null is returned, the value should be blanked out
                    attribute.setValue("");
                } else if (hydratedObject instanceof Attributable) {
                    attribute.setValue(((Attributable) hydratedObject).serialize());
                } else if (!hydratedObject.getClass().getName().equals(nokNametype.getFormat())) {
                    // if the classes doesn't match the format, the hydration failed somehow
                    // TODO change the PersonAttribute.getHydratedObject() to not swallow all errors?
                    throw new APIException();
                }
            } catch (APIException e) {
                //.warn("Got an invalid value: " + value + " while setting personAttributeType id #" + paramName, e);
                // setting the value to empty so that the user can reset the value to something else
                attribute.setValue("");
            }
            patient.addAttribute(attribute);
        }

        if (nokPhone != null) {
            PersonAttribute attribute = new PersonAttribute(nokContacttype, nokPhone);

            try {
                Object hydratedObject = attribute.getHydratedObject();
                if (hydratedObject == null || "".equals(hydratedObject.toString())) {
                    // if null is returned, the value should be blanked out
                    attribute.setValue("");
                } else if (hydratedObject instanceof Attributable) {
                    attribute.setValue(((Attributable) hydratedObject).serialize());
                } else if (!hydratedObject.getClass().getName().equals(nokContacttype.getFormat())) {
                    // if the classes doesn't match the format, the hydration failed somehow
                    // TODO change the PersonAttribute.getHydratedObject() to not swallow all errors?
                    throw new APIException();
                }
            } catch (APIException e) {
                //.warn("Got an invalid value: " + value + " while setting personAttributeType id #" + paramName, e);
                // setting the value to empty so that the user can reset the value to something else
                attribute.setValue("");
            }
            patient.addAttribute(attribute);
        }
        return patient;
    }


    /**
     * get a patient's phone contact
     * @param patient
     * @param personService
     * @return
     */
    private static String getContactPhoneNumber(Patient patient, PersonService personService) {
        PersonAttributeType phoneNumberAttrType = personService.getPersonAttributeTypeByUuid(TELEPHONE_CONTACT);
        return patient.getAttribute(phoneNumberAttrType) != null ? patient.getAttribute(phoneNumberAttrType).getValue() : "";
    }

    public static Date parseDateString(String dateString, String format) {
        if (dateString.equals("") || dateString == null) {
            return null;
        }
        SimpleDateFormat df = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = df.parse(dateString);
        } catch (ParseException e) {
            //e.printStackTrace();
        }
        return date;
    }

    public static Patient checkIfPatientExists(String idNumber, String passportNumber, String alienId) {

        PatientService patientService = Context.getPatientService();
        PatientIdentifierType ALIEN_NUMBER_TYPE = patientService.getPatientIdentifierTypeByUuid(ALIEN_NUMBER);
        PatientIdentifierType PP_NUMBER_TYPE = patientService.getPatientIdentifierTypeByUuid(PASSPORT_NUMBER);
        PatientIdentifierType NATIONAL_ID_TYPE = patientService.getPatientIdentifierTypeByUuid(NATIONAL_ID);

        List<String> ids = new ArrayList<String>();
        if (StringUtils.isNotBlank(idNumber)) {
            ids.add(idNumber);
        }

        if (StringUtils.isNotBlank(passportNumber)) {
            ids.add(passportNumber);
        }

        if (StringUtils.isNotBlank(alienId)) {
            ids.add(alienId);
        }

        for (String identifier : ids) {
            if (identifier != null) {
                List<Patient> patientsAlreadyAssigned = patientService.getPatients(null, identifier.trim(), Arrays.asList(ALIEN_NUMBER_TYPE, PP_NUMBER_TYPE, NATIONAL_ID_TYPE), false);
                if (patientsAlreadyAssigned.size() > 0) {
                    return patientsAlreadyAssigned.get(0);
                }
            }
        }

        return null;
    }
}
