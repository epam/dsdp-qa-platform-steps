package platform.qa.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;

class CreatedFormResponseTest {

  private final ObjectMapper mapper = new ObjectMapper();

  private static CreatedFormResponse.Component getComponent() {
    CreatedFormResponse.Component.Validate validate = new CreatedFormResponse.Component.Validate();
    validate.setRequired(true);
    validate.setMinLength(1);
    validate.setMaxLength(10);
    validate.setPattern("[0-9]+");
    validate.setCustom("cust");
    validate.setCustomPrivate(false);

    CreatedFormResponse.Component.Conditional conditional =
        new CreatedFormResponse.Component.Conditional();
    conditional.setShow("true");
    conditional.setWhen("x");
    conditional.setEq("1");

    CreatedFormResponse.Component.Properties properties =
        new CreatedFormResponse.Component.Properties();

    CreatedFormResponse.Component comp = new CreatedFormResponse.Component();
    comp.setInput(true);
    comp.setTableView(true);
    comp.setInputType("text");
    comp.setInputMask("###");
    comp.setLabel("Label");
    comp.setKey("key1");
    comp.setPlaceholder("ph");
    comp.setPrefix("$");
    comp.setSuffix("*");
    comp.setMultiple(false);
    comp.setDefaultValue("def");
    comp.set_protected(true);
    comp.setUnique(true);
    comp.setPersistent(true);
    comp.setValidate(validate);
    comp.setConditional(conditional);
    comp.setType("textfield");
    comp.setTags(List.of("tag1"));
    comp.setLockKey(false);
    comp.setAutofocus(true);
    comp.setHidden(false);
    comp.setClearOnHide(true);
    comp.setSpellcheck(true);
    comp.setLabelPosition("top");
    comp.setInputFormat("plain");
    comp.setProperties(properties);
    comp.setSize("md");
    comp.setLeftIcon("left");
    comp.setRightIcon("right");
    comp.setBlock(true);
    comp.setAction("submit");
    comp.setDisableOnInvalid(false);
    comp.setTheme("primary");
    return comp;
  }

  @Test
  void testNoArgsConstructor_andSettersGetters() {
    CreatedFormResponse resp = new CreatedFormResponse();

    resp.setId("ID1");
    resp.setType("form");
    resp.setTags(List.of("t1", "t2"));
    resp.setOwner("owner");
    resp.setTitle("MyTitle");
    resp.setDisplay("form");
    resp.setName("FormName");
    resp.setPath("/my-form");
    resp.setCreated("2025-01-01");
    resp.setModified("2025-01-02");
    resp.setMachineName("machine-x");

    CreatedFormResponse.Access access = new CreatedFormResponse.Access();
    access.setRoles(List.of("admin", "user"));
    access.setType("read");

    CreatedFormResponse.Component comp = getComponent();

    resp.setComponents(List.of(comp));
    resp.setAccess(List.of(access));
    resp.setSubmissionAccess(List.of("sub1"));

    // ASSERTIONS
    assertThat(resp.getId()).isEqualTo("ID1");
    assertThat(resp.getType()).isEqualTo("form");
    assertThat(resp.getTags()).contains("t1");
    assertThat(resp.getOwner()).isEqualTo("owner");
    assertThat(resp.getComponents()).hasSize(1);
    assertThat(resp.getAccess().get(0).getRoles()).contains("admin");
    assertThat(resp.getComponents().get(0).getValidate().getPattern()).isEqualTo("[0-9]+");
    assertThat(resp.getComponents().get(0).getConditional().getEq()).isEqualTo("1");
    assertThat(resp.getComponents().get(0).getProperties()).isNotNull();
    assertThat(resp.getMachineName()).isEqualTo("machine-x");
  }

  @Test
  void testAllArgsConstructor() {
    CreatedFormResponse resp =
        new CreatedFormResponse(
            "IDX",
            "form",
            List.of(),
            "ownerX",
            List.of(),
            "titleX",
            "displayX",
            "nameX",
            "/pathX",
            List.of(),
            List.of(),
            "createdX",
            "modifiedX",
            "machineX");

    assertThat(resp.getId()).isEqualTo("IDX");
    assertThat(resp.getName()).isEqualTo("nameX");
    assertThat(resp.getMachineName()).isEqualTo("machineX");
  }

  @Test
  void testJsonProperty_idDeserialization() throws Exception {
    String json =
        """
                {
                  "_id": "JSON123",
                  "name": "MyForm"
                }
                """;

    CreatedFormResponse resp = mapper.readValue(json, CreatedFormResponse.class);

    assertThat(resp.getId()).isEqualTo("JSON123");
    assertThat(resp.getName()).isEqualTo("MyForm");
  }
}
