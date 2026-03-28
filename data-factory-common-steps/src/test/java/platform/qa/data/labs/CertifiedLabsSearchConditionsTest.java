package platform.qa.data.labs;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import java.util.List;
import org.junit.jupiter.api.*;
import platform.qa.entities.Service;
import platform.qa.entities.User;
import platform.qa.pojo.labs.*;
import platform.qa.pojo.labs.searchConditions.*;

class CertifiedLabsSearchConditionsTest {

  private WireMockServer wireMock;
  private CertifiedLabsSearchConditions api;

  @BeforeEach
  void setup() {
    wireMock = new WireMockServer(0);
    wireMock.start();
    configureFor("localhost", wireMock.port());

    User user = new User("usr", "pwd");
    user.setToken("token");

    Service service = new Service("http://localhost:" + wireMock.port(), user);
    api = new CertifiedLabsSearchConditions(service);
  }

  @AfterEach
  void teardown() {
    wireMock.stop();
  }

  // ---------------------------------------------------------
  // 1) laboratoryEqualEdrpouNameCount
  // ---------------------------------------------------------

  @Test
  void testLaboratoryEqualEdrpouNameCount_success() {
    stubFor(
        get(urlPathEqualTo("/laboratory-equal-edrpou-name-count"))
            .withQueryParam("name", equalTo("Test"))
            .withQueryParam("edrpou", equalTo("123"))
            .willReturn(okJson("{\"cnt\": \"[9]\"}")));

    String cnt = api.laboratoryEqualEdrpouNameCount("Test", "123");
    assertThat(cnt).isEqualTo("9");
  }

  @Test
  void testLaboratoryEqualEdrpouNameCount_null() {
    assertThat(api.laboratoryEqualEdrpouNameCount(null, "123")).isNull();
    assertThat(api.laboratoryEqualEdrpouNameCount("A", null)).isNull();
  }

  // ---------------------------------------------------------
  // 2) laboratoryStartWithEdrpouContainsName
  // ---------------------------------------------------------
  @Test
  void testLaboratoryStartWithEdrpouContainsName_success() {
    stubFor(
        get(urlPathEqualTo("/laboratory-start-with-edrpou-contains-name"))
            .withQueryParam("name", equalTo("Lab"))
            .withQueryParam("edrpou", equalTo("77"))
            .willReturn(okJson("[{\"laboratoryId\":\"L1\",\"name\":\"TestLab\"}]")));

    List<Laboratory> out = api.laboratoryStartWithEdrpouContainsName("Lab", "77");
    assertThat(out).hasSize(1);
    assertThat(out.get(0).getLaboratoryId()).isEqualTo("L1");
  }

  @Test
  void testLaboratoryStartWithEdrpouContainsName_null() {
    assertThat(api.laboratoryStartWithEdrpouContainsName(null, "1")).isNull();
    assertThat(api.laboratoryStartWithEdrpouContainsName("A", null)).isNull();
  }

  // ---------------------------------------------------------
  // 3) koatuuOblContainsName
  // ---------------------------------------------------------
  @Test
  void testKoatuuOblContainsName_success() {
    stubFor(
        get(urlPathEqualTo("/koatuu-obl-contains-name"))
            .withQueryParam("name", equalTo("Київ"))
            .willReturn(okJson("[{\"koatuuId\":\"111\",\"name\":\"Київська\"}]")));

    List<Koatuu> list = api.koatuuOblContainsName("Київ");
    assertThat(list).hasSize(1);
  }

  @Test
  void testKoatuuOblContainsName_null() {
    assertThat(api.koatuuOblContainsName(null)).isNull();
  }

  // ---------------------------------------------------------
  // 4) koatuuNpStartsWithName
  // ---------------------------------------------------------
  @Test
  void testKoatuuNpStartsWithName_success() {
    stubFor(
        get(urlPathEqualTo("/koatuu-np-starts-with-name-by-obl"))
            .withQueryParam("level1", equalTo("1"))
            .withQueryParam("name", equalTo("Бор"))
            .willReturn(okJson("[{\"name\":\"Бориспіль\"}]")));

    List<SearchKoatuuNp> list = api.koatuuNpStartsWithName("1", "Бор");
    assertThat(list).isNotEmpty();
  }

  @Test
  void testKoatuuNpStartsWithName_null() {
    assertThat(api.koatuuNpStartsWithName(null, "A")).isNull();
    assertThat(api.koatuuNpStartsWithName("1", null)).isNull();
  }

  // ---------------------------------------------------------
  // 5) ownershipContainsName
  // ---------------------------------------------------------
  @Test
  void testOwnershipContainsName_success() {
    stubFor(
        get(urlPathEqualTo("/ownership-contains-name"))
            .withQueryParam("name", equalTo("приват"))
            .willReturn(okJson("[{\"ownershipId\":\"O1\",\"name\":\"Приватна\"}]")));

    List<Ownership> list = api.ownershipContainsName("приват");
    assertThat(list).hasSize(1);
  }

  @Test
  void testOwnershipContainsName_null() {
    assertThat(api.ownershipContainsName(null)).isNull();
  }

  // ---------------------------------------------------------
  // 6) kopfgContainsName
  // ---------------------------------------------------------
  @Test
  void testKopfgContainsName_success() {
    stubFor(
        get(urlPathEqualTo("/kopfg-contains-name"))
            .withQueryParam("name", equalTo("держ"))
            .willReturn(okJson("[{\"kopfgId\":\"K1\",\"name\":\"Держ\"}]")));

    List<Kopfg> list = api.kopfgСontainsName("держ");
    assertThat(list).hasSize(1);
  }

  @Test
  void testKopfgContainsName_null() {
    assertThat(api.kopfgСontainsName(null)).isNull();
  }

  // ---------------------------------------------------------
  // 7) refusalReasonEqualConstantCode
  // ---------------------------------------------------------
  @Test
  void testRefusalReasonEqualConstantCode_success() {
    stubFor(
        get(urlPathEqualTo("/refusal-reason-equal-constant-code-contains-name"))
            .withQueryParam("constantCode", equalTo("R1"))
            .withQueryParam("name", equalTo("Прич"))
            .willReturn(okJson("[{\"refusalReasonId\":\"RR1\",\"name\":\"Причина\"}]")));

    List<RefusalReason> list = api.refusalReasonEqualConstantCode("R1", "Прич");
    assertThat(list).hasSize(1);
  }

  @Test
  void testRefusalReasonEqualConstantCode_null() {
    assertThat(api.refusalReasonEqualConstantCode(null, "A")).isNull();
    assertThat(api.refusalReasonEqualConstantCode("B", null)).isNull();
  }

  // ---------------------------------------------------------
  // 8) staffContainsName
  // ---------------------------------------------------------
  @Test
  void testStaffContainsName_success() {
    stubFor(
        get(urlPathEqualTo("/staff-contains-name"))
            .withQueryParam("name", equalTo("Ів"))
            .willReturn(okJson("[{\"staffStatusId\":\"S1\",\"name\":\"Іван\"}]")));

    assertThat(api.staffContainsName("Ів")).hasSize(1);
  }

  @Test
  void testStaffContainsName_null() {
    assertThat(api.staffContainsName(null)).isNull();
  }

  // ---------------------------------------------------------
  // 9) staffEqualConstantCode
  // ---------------------------------------------------------
  @Test
  void testStaffEqualConstantCode_success() {
    stubFor(
        get(urlPathEqualTo("/staff-equal-constant-code"))
            .withQueryParam("constantCode", equalTo("A1"))
            .willReturn(okJson("[{\"staffStatusId\":\"SS1\"}]")));

    assertThat(api.staffEqualConstantCode("A1")).hasSize(1);
  }

  @Test
  void testStaffEqualConstantCode_null() {
    assertThat(api.staffEqualConstantCode(null)).isNull();
  }

  // ---------------------------------------------------------
  // 10) factorLabourContainsName
  // ---------------------------------------------------------
  @Test
  void testFactorLabourContainsName_success() {
    stubFor(
        get(urlPathEqualTo("/factor-labour-contains-name"))
            .withQueryParam("name", equalTo("Шум"))
            .willReturn(okJson("[{\"factorId\":\"F1\"}]")));

    assertThat(api.factorLabourContainsName("Шум")).hasSize(1);
  }

  @Test
  void testFactorLabourContainsName_null() {
    assertThat(api.factorLabourContainsName(null)).isNull();
  }

  // ---------------------------------------------------------
  // 11) factorChemicalObrbContainsName
  // ---------------------------------------------------------
  @Test
  void testFactorChemicalObrbContainsName_success() {
    stubFor(
        get(urlPathEqualTo("/factor-chemical-obrb-contains-name"))
            .withQueryParam("name", equalTo("Газ"))
            .willReturn(okJson("[{\"factorId\":\"C1\"}]")));

    assertThat(api.factorChemicalObrbContainsName("Газ")).hasSize(1);
  }

  @Test
  void testFactorChemicalObrbContainsName_null() {
    assertThat(api.factorChemicalObrbContainsName(null)).isNull();
  }

  // ---------------------------------------------------------
  // 12) factorChemicalArbitraryContainsName
  // ---------------------------------------------------------
  @Test
  void testFactorChemicalArbitraryContainsName_success() {
    stubFor(
        get(urlPathEqualTo("/factor-chemical-arbitrary-contains-name"))
            .withQueryParam("name", equalTo("Тест"))
            .willReturn(okJson("[{\"factorId\":\"ARB1\"}]")));

    assertThat(api.factorChemicalArbitraryContainsName("Тест")).hasSize(1);
  }

  @Test
  void testFactorChemicalArbitraryContainsName_null() {
    assertThat(api.factorChemicalArbitraryContainsName(null)).isNull();
  }

  // ---------------------------------------------------------
  // 13) factorEqualFactorTypeNameCount
  // ---------------------------------------------------------
  @Test
  void testFactorEqualFactorTypeNameCount_success() {
    stubFor(
        get(urlPathEqualTo("/factor-equal-factor-type-name-count"))
            .withQueryParam("name", equalTo("Test"))
            .willReturn(okJson("{\"cnt\": \"[3]\"}")));

    assertThat(api.factorEqualFactorTypeNameCount("Test")).isEqualTo("3");
  }

  @Test
  void testFactorEqualFactorTypeNameCount_null() {
    assertThat(api.factorEqualFactorTypeNameCount(null)).isNull();
  }

  // ---------------------------------------------------------
  // 14) factorChemicalHostContainsName
  // ---------------------------------------------------------
  @Test
  void testFactorChemicalHostContainsName_success() {
    stubFor(
        get(urlPathEqualTo("/factor-chemical-host-contains-name"))
            .withQueryParam("name", equalTo("Host"))
            .willReturn(okJson("[{\"factorId\":\"H1\"}]")));

    assertThat(api.factorChemicalHostContainsName("Host")).hasSize(1);
  }

  @Test
  void testFactorChemicalHostContainsName_null() {
    assertThat(api.factorChemicalHostContainsName(null)).isNull();
  }

  // ---------------------------------------------------------
  // 15) solutionTypeEqualConstantCode
  // ---------------------------------------------------------
  @Test
  void testSolutionTypeEqualConstantCode_success() {
    stubFor(
        get(urlPathEqualTo("/solution-type-equal-constant-code"))
            .withQueryParam("constantCode", equalTo("SOL1"))
            .willReturn(okJson("[{\"constantCode\":\"SOL1\"}]")));

    assertThat(api.solutionTypeEqualConstantCode("SOL1")).hasSize(1);
  }

  @Test
  void testSolutionTypeEqualConstantCode_null() {
    assertThat(api.solutionTypeEqualConstantCode(null)).isNull();
  }

  // ---------------------------------------------------------
  // 16) researchContainsName
  // ---------------------------------------------------------
  @Test
  void testResearchContainsName_success() {
    stubFor(
        get(urlPathEqualTo("/research-contains-name"))
            .withQueryParam("researchType", equalTo("Air"))
            .willReturn(okJson("[{\"researchId\":\"R1\"}]")));

    assertThat(api.researchContainsName("Air")).hasSize(1);
  }

  @Test
  void testResearchContainsName_null() {
    assertThat(api.researchContainsName(null)).isNull();
  }

  // ---------------------------------------------------------
  // 17) koatuuEqualKoatuuIdName
  // ---------------------------------------------------------
  @Test
  void testKoatuuEqualKoatuuIdName_success() {
    stubFor(
        get(urlPathEqualTo("/koatuu-equal-koatuu-id-name"))
            .withQueryParam("koatuuId", equalTo("111"))
            .willReturn(okJson("[{\"koatuuId\":\"111\"}]")));

    assertThat(api.koatuuEqualKoatuuIdName("111")).hasSize(1);
  }

  @Test
  void testKoatuuEqualKoatuuIdName_null() {
    assertThat(api.koatuuEqualKoatuuIdName(null)).isNull();
  }

  // ---------------------------------------------------------
  // 18) staffEqualLaboratoryIdContainsFullName
  // ---------------------------------------------------------
  @Test
  void testStaffEqualLaboratoryIdContainsFullName_success() {
    stubFor(
        get(urlPathEqualTo("/staff-equal-laboratory-id-contains-full-name"))
            .withQueryParam("laboratoryId", equalTo("L1"))
            .withQueryParam("fullName", equalTo("Іван"))
            .willReturn(okJson("[{\"fullName\":\"Іван\"}]")));

    assertThat(api.staffEqualLaboratoryIdContainsFullName("L1", "Іван")).hasSize(1);
  }

  @Test
  void testStaffEqualLaboratoryIdContainsFullName_null() {
    assertThat(api.staffEqualLaboratoryIdContainsFullName(null, "A")).isNull();
    assertThat(api.staffEqualLaboratoryIdContainsFullName("L1", null)).isNull();
  }

  // ---------------------------------------------------------
  // 19) staffEqualLaboratoryIdCount
  // ---------------------------------------------------------
  @Test
  void testStaffEqualLaboratoryIdCount_success() {
    stubFor(
        get(urlPathEqualTo("/staff-equal-laboratory-id-count"))
            .withQueryParam("laboratoryId", equalTo("L1"))
            .willReturn(okJson("{\"cnt\":\"[7]\"}")));

    assertThat(api.staffEqualLaboratoryIdCount("L1")).isEqualTo("7");
  }

  @Test
  void testStaffEqualLaboratoryIdCount_null() {
    assertThat(api.staffEqualLaboratoryIdCount(null)).isNull();
  }

  // ---------------------------------------------------------
  // 20) factorChemicalHygieneContainsName
  // ---------------------------------------------------------
  @Test
  void testFactorChemicalHygieneContainsName_success() {
    stubFor(
        get(urlPathEqualTo("/factor-chemical-hygiene-contains-name"))
            .withQueryParam("name", equalTo("Hyg"))
            .willReturn(okJson("[{\"factorId\":\"HY1\"}]")));

    assertThat(api.factorChemicalHygieneContainsName("Hyg")).hasSize(1);
  }

  @Test
  void testFactorChemicalHygieneContainsName_null() {
    assertThat(api.factorChemicalHygieneContainsName(null)).isNull();
  }

  // ---------------------------------------------------------
  // 21) factorBioContainsName
  // ---------------------------------------------------------
  @Test
  void testFactorBioContainsName_success() {
    stubFor(
        get(urlPathEqualTo("/factor-bio-contains-name"))
            .withQueryParam("name", equalTo("Bio"))
            .willReturn(okJson("[{\"factorId\":\"B1\"}]")));

    assertThat(api.factorBioContainsName("Bio")).hasSize(1);
  }

  @Test
  void testFactorBioContainsName_null() {
    assertThat(api.factorBioContainsName(null)).isNull();
  }

  // ---------------------------------------------------------
  // 22) factorPhysicalContainsName
  // ---------------------------------------------------------
  @Test
  void testFactorPhysicalContainsName_success() {
    stubFor(
        get(urlPathEqualTo("/factor-physical-contains-name"))
            .withQueryParam("name", equalTo("Phys"))
            .willReturn(okJson("[{\"factorId\":\"P1\"}]")));

    assertThat(api.factorPhysicalContainsName("Phys")).hasSize(1);
  }

  @Test
  void testFactorPhysicalContainsName_null() {
    assertThat(api.factorPhysicalContainsName(null)).isNull();
  }

  // ---------------------------------------------------------
  // 23) applicationTypeEqualConstantCode
  // ---------------------------------------------------------
  @Test
  void testApplicationTypeEqualConstantCode_success() {
    stubFor(
        get(urlPathEqualTo("/application-type-equal-constant-code"))
            .withQueryParam("constantCode", equalTo("APP1"))
            .willReturn(okJson("[{\"constantCode\":\"APP1\"}]")));

    assertThat(api.applicationTypeEqualConstantCode("APP1")).hasSize(1);
  }

  @Test
  void testApplicationTypeEqualConstantCode_null() {
    assertThat(api.applicationTypeEqualConstantCode(null)).isNull();
  }

  // ---------------------------------------------------------
  // 24) laboratoryEqualSubject
  // ---------------------------------------------------------
  @Test
  void testLaboratoryEqualSubject_success() {
    stubFor(
        get(urlPathEqualTo("/laboratory-equal-subject-id/"))
            .withQueryParam("subjectId", equalTo("S1"))
            .willReturn(okJson("[{\"laboratoryId\":\"L1\"}]")));

    assertThat(api.laboratoryEqualSubject("S1")).hasSize(1);
  }

  @Test
  void testLaboratoryEqualSubject_null() {
    assertThat(api.laboratoryEqualSubject(null)).isNull();
  }

  // ---------------------------------------------------------
  // 25) getFactorsChemHostDovil
  // ---------------------------------------------------------
  @Test
  void testGetFactorsChemHostDovil_success() {
    stubFor(
        get(urlPathEqualTo("/factors-chem-host-dovil"))
            .withQueryParam("searchConditions", equalTo("{}"))
            .willReturn(okJson("[{\"factorId\":\"D1\"}]")));

    assertThat(api.getFactorsChemHostDovil()).hasSize(1);
  }

  // ---------------------------------------------------------
  // 26) lastLaboratorySolution
  // ---------------------------------------------------------
  @Test
  void testLastLaboratorySolution_success() {
    stubFor(
        get(urlPathEqualTo("/last-laboratory-solution/"))
            .withQueryParam("laboratoryId", equalTo("L1"))
            .willReturn(
                okJson(
                    "[{"
                        + "\"solutionTypeId\":\"SOLID1\","
                        + "\"laboratoryId\":\"L1\","
                        + "\"registrationId\":\"R1\","
                        + "\"applicationTypeId\":\"APP1\","
                        + "\"solutionDate\":\"2024-01-01\""
                        + "}]")));

    var result = api.lastLaboratorySolution("L1");

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getSolutionTypeId()).isEqualTo("SOLID1");
    assertThat(result.get(0).getLaboratoryId()).isEqualTo("L1");
  }

  @Test
  void testLastLaboratorySolution_null() {
    assertThat(api.lastLaboratorySolution(null)).isNull();
  }

  // ---------------------------------------------------------
  // 27) laboratoryEqualSubjectCodeName
  // ---------------------------------------------------------
  @Test
  void testLaboratoryEqualSubjectCodeName_success() {
    stubFor(
        get(urlPathEqualTo("/laboratory-equal-subject-code-name/"))
            .withQueryParam("subjectCode", equalTo("C1"))
            .withQueryParam("subjectType", equalTo("T1"))
            .withQueryParam("subjectId", equalTo("SID"))
            .willReturn(okJson("[{\"laboratoryId\":\"LB1\"}]")));

    assertThat(api.laboratoryEqualSubjectCodeName("C1", "T1", "SID")).hasSize(1);
  }

  @Test
  void testLaboratoryEqualSubjectCodeName_null() {
    assertThat(api.laboratoryEqualSubjectCodeName(null, "A", "B")).isNull();
    assertThat(api.laboratoryEqualSubjectCodeName("A", null, "B")).isNull();
  }

  // ---------------------------------------------------------
  // 28) registrationEqualLaboratoryIdSolution
  // ---------------------------------------------------------
  @Test
  void testRegistrationEqualLaboratoryIdSolution_success() {
    stubFor(
        get(urlPathEqualTo("/registration-equal-laboratory-id-solution/"))
            .withQueryParam("laboratoryId", equalTo("L1"))
            .withQueryParam("solutionCode", equalTo("SC1"))
            .willReturn(okJson("[{\"registrationId\":\"R1\"}]")));

    assertThat(api.registrationEqualLaboratoryIdSolution("L1", "SC1")).hasSize(1);
  }

  @Test
  void testRegistrationEqualLaboratoryIdSolution_null() {
    assertThat(api.registrationEqualLaboratoryIdSolution(null, "X")).isNull();
    assertThat(api.registrationEqualLaboratoryIdSolution("L1", null)).isNull();
  }

  // ---------------------------------------------------------
  // 29) registrationEqualLaboratoryIdSolutionWithOneParameters
  // ---------------------------------------------------------
  @Test
  void testRegistrationEqualLaboratoryIdSolutionWithOneParameters_success() {
    stubFor(
        get(urlPathEqualTo("/registration-equal-laboratory-id-solution/"))
            .withQueryParam("laboratoryId", equalTo("L1"))
            .willReturn(okJson("[{\"registrationId\":\"R1\"}]")));

    assertThat(api.registrationEqualLaboratoryIdSolutionWithOneParameters("L1")).hasSize(1);
  }

  @Test
  void testRegistrationEqualLaboratoryIdSolutionWithOneParameters_null() {
    assertThat(api.registrationEqualLaboratoryIdSolutionWithOneParameters(null)).isNull();
  }

  // ---------------------------------------------------------
  // 30) staffEqualLaboratoryId
  // ---------------------------------------------------------
  @Test
  void testStaffEqualLaboratoryId_success() {
    stubFor(
        get(urlPathEqualTo("/staff-equal-laboratory-id/"))
            .withQueryParam("laboratoryId", equalTo("L1"))
            .willReturn(okJson("[{\"laboratoryId\":\"L1\"}]")));

    assertThat(api.staffEqualLaboratoryId("L1")).hasSize(1);
  }

  @Test
  void testStaffEqualLaboratoryId_null() {
    assertThat(api.staffEqualLaboratoryId(null)).isNull();
  }

  // ---------------------------------------------------------
  // 31) staffFindInSalary
  // ---------------------------------------------------------
  @Test
  void testStaffFindInSalary_success() {
    stubFor(
        get(urlPathEqualTo("/staff-find-in-salary/"))
            .withQueryParam("salary", equalTo("10000"))
            .willReturn(okJson("[{\"salary\":\"10000\"}]")));

    assertThat(api.staffFindInSalary("10000")).hasSize(1);
  }

  @Test
  void testStaffFindInSalary_null() {
    assertThat(api.staffFindInSalary(null)).isNull();
  }

  // ---------------------------------------------------------
  // 32) staffFindNotInSalary
  // ---------------------------------------------------------
  @Test
  void testStaffFindNotInSalary_success() {
    stubFor(
        get(urlPathEqualTo("/staff-find-not-in-salary/"))
            .withQueryParam("salary", equalTo("15000"))
            .willReturn(okJson("[{\"salary\":\"15000\"}]")));

    assertThat(api.staffFindNotInSalary("15000")).hasSize(1);
  }

  @Test
  void testStaffFindNotInSalary_null() {
    assertThat(api.staffFindNotInSalary(null)).isNull();
  }

  // ---------------------------------------------------------
  // 33) ownershipFindBetweenCode
  // ---------------------------------------------------------
  @Test
  void testOwnershipFindBetweenCode_success() {
    stubFor(
        get(urlPathEqualTo("/ownership-find-between-code/"))
            .withQueryParam("codeFrom", equalTo("10"))
            .withQueryParam("codeTo", equalTo("20"))
            .willReturn(okJson("[{\"code\":\"15\"}]")));

    assertThat(api.ownershipFindBetweenCode("10", "20")).hasSize(1);
  }

  @Test
  void testOwnershipFindBetweenCode_null() {
    assertThat(api.ownershipFindBetweenCode(null, "20")).isNull();
    assertThat(api.ownershipFindBetweenCode("10", null)).isNull();
  }
}
