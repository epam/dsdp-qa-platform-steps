/*
 * Copyright 2022 EPAM Systems.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package platform.qa.email;

import io.restassured.common.mapper.TypeRef;
import platform.qa.email.entities.response.UserMail;
import platform.qa.email.entities.response.UserMailMessage;
import platform.qa.email.service.EmailService;
import platform.qa.entities.User;

import java.util.List;

public class EmailClientSteps {
    private final EmailService emailService;

    public EmailClientSteps(EmailService emailService) {
        this.emailService = emailService;
    }

    public List<UserMail> getAllUserMails(User user) {
        return emailService.getAllUserMails(user.getLogin())
                .statusCode(200)
                .extract()
                .body()
                .as(new TypeRef<>() {
                });
    }

    public UserMailMessage getUserMailContent(User user, String mailId) {
        return emailService.getUserMailById(user.getLogin(), mailId)
                .statusCode(200)
                .extract()
                .body()
                .as(UserMailMessage.class);
    }
}
