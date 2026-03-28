package platform.qa.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import platform.qa.email.entities.response.UserMail;
import platform.qa.email.entities.response.UserMailMessage;
import platform.qa.email.service.EmailService;
import platform.qa.entities.User;

class EmailClientStepsTest {

  private EmailService emailService;
  private EmailClientSteps steps;

  private User user;

  @BeforeEach
  void setup() {
    emailService = mock(EmailService.class);
    steps = new EmailClientSteps(emailService);

    user = new User("login123", "pwd");
  }

  @Test
  void testGetAllUserMails_success() {
    // mocks for rest-assured chain
    ValidatableResponse validatable = mock(ValidatableResponse.class);
    ExtractableResponse<Response> extractable = mock(ExtractableResponse.class);
    Response response = mock(Response.class);

    List<UserMail> mails = List.of(new UserMail(), new UserMail());

    when(emailService.getAllUserMails("login123")).thenReturn(validatable);

    when(validatable.statusCode(200)).thenReturn(validatable);
    when(validatable.extract()).thenReturn(extractable);

    when(extractable.body()).thenReturn(response);
    when(response.as(any(TypeRef.class))).thenReturn(mails);

    List<UserMail> result = steps.getAllUserMails(user);

    assertThat(result).hasSize(2);
  }

  @Test
  void testGetUserMailContent_success() {
    ValidatableResponse validatable = mock(ValidatableResponse.class);
    ExtractableResponse<Response> extractable = mock(ExtractableResponse.class);
    Response response = mock(Response.class);

    UserMailMessage.Body body = new UserMailMessage.Body();
    body.setHtml("<p>Hello!</p>");
    body.setText("Hello!");

    UserMailMessage msg = new UserMailMessage();
    msg.setBody(body);

    when(emailService.getUserMailById("login123", "mail1")).thenReturn(validatable);

    when(validatable.statusCode(200)).thenReturn(validatable);
    when(validatable.extract()).thenReturn(extractable);

    when(extractable.body()).thenReturn(response);
    when(response.as(UserMailMessage.class)).thenReturn(msg);

    UserMailMessage result = steps.getUserMailContent(user, "mail1");

    assertThat(result.getBody().getText()).isEqualTo("Hello!");
  }
}
