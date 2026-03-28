package platform.qa.settings.enumerations;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ChannelTypeTest {

  @Test
  void enumValuesShouldBeCorrect() {
    ChannelType[] values = ChannelType.values();

    assertThat(values)
        .hasSize(3)
        .containsExactly(ChannelType.INBOX, ChannelType.EMAIL, ChannelType.DIIA);
  }

  @Test
  void getTypeShouldReturnCorrectValue() {
    assertThat(ChannelType.INBOX.getType()).isEqualTo("inbox");
    assertThat(ChannelType.EMAIL.getType()).isEqualTo("email");
    assertThat(ChannelType.DIIA.getType()).isEqualTo("diia");
  }

  @Test
  void valueOfShouldReturnCorrectEnum() {
    assertThat(ChannelType.valueOf("INBOX")).isEqualTo(ChannelType.INBOX);
    assertThat(ChannelType.valueOf("EMAIL")).isEqualTo(ChannelType.EMAIL);
    assertThat(ChannelType.valueOf("DIIA")).isEqualTo(ChannelType.DIIA);
  }

  @Test
  void toStringShouldContainEnumName() {
    String toStringValue = ChannelType.EMAIL.toString();

    assertThat(toStringValue).contains("EMAIL").contains("email");
  }
}
