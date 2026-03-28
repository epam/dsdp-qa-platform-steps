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

package platform.qa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatedFormResponse {
    @JsonProperty("_id")
    private String id;
    private String type;
    private List<Object> tags = null;
    private Object owner;
    private List<Component> components = null;
    private String title;
    private String display;
    private String name;
    private String path;
    private List<Access> access = null;
    private List<Object> submissionAccess = null;
    private String created;
    private String modified;
    private String machineName;

    @Getter
    @Setter
    public static class Access {
        private List<String> roles = null;
        private String type;
    }

    @Getter
    @Setter
    public static class Component {
        private Boolean input;
        private Boolean tableView;
        private String inputType;
        private String inputMask;
        private String label;
        private String key;
        private String placeholder;
        private String prefix;
        private String suffix;
        private Boolean multiple;
        private String defaultValue;
        @JsonProperty("protected")
        private Boolean _protected;
        private Boolean unique;
        private Boolean persistent;
        private Validate validate;
        private Conditional conditional;
        private String type;
        private List<Object> tags = null;
        private Boolean lockKey;
        private Boolean autofocus;
        private Boolean hidden;
        private Boolean clearOnHide;
        private Boolean spellcheck;
        private String labelPosition;
        private String inputFormat;
        private Properties properties;
        private String size;
        private String leftIcon;
        private String rightIcon;
        private Boolean block;
        private String action;
        private Boolean disableOnInvalid;
        private String theme;

        @Getter
        @Setter
        public static class Conditional {
            private String show;
            private Object when;
            private String eq;
        }

        @Getter
        @Setter
        public static class Properties {
        }

        @Getter
        @Setter
        public static class Validate {
            private Boolean required;
            private Integer minLength;
            private Integer maxLength;
            private String pattern;
            private String custom;
            private Boolean customPrivate;
        }
    }
}
