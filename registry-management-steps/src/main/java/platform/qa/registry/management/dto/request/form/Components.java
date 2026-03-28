package platform.qa.registry.management.dto.request.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Components {
    public String label;
    public String description;
    public String tooltip;
    public String shortcut;
    public String tabindex;
    public Boolean hidden;
    public Boolean hideLabel;
    public Boolean disabled;
    public String redrawOn;
    public Boolean clearOnHide;
    public String customDefaultValue;
    public String calculateValue;
    public Boolean allowCalculateOverride;
    public Validate validate;
    public String key;
    public Conditional conditional;
    public String customConditional;
    public String type;
    public String name;
    public String value;
    public Boolean input;
    public String placeholder;
    public String prefix;
    public String customClass;
    public String suffix;
    public Boolean multiple;
    @JsonProperty("protected")
    public Boolean isProtected;
    public Boolean unique;
    public Boolean persistent;
    public String refreshOn;
    public Boolean tableView;
    public Boolean modalEdit;
    public Boolean dataGridLabel;
    public String labelPosition;
    public String errorLabel;
    public Boolean autofocus;
    public Boolean dbIndex;
    public Boolean calculateServer;
    public Type widget;
    public Attributes attributes;
    public String validateOn;
    public Overlay overlay;
    public Boolean encrypted;
    public Boolean showCharCount;
    public Boolean showWordCount;
    public Properties properties;
    public Boolean allowMultipleMasks;
    public String inputType;
    public String id;
    public String action;
    public Boolean showValidations;
    public String theme;
    public String size;
    public Boolean block;
    public String leftIcon;
    public String rightIcon;
    public Boolean disableOnInvalid;
    public List<Object> tags;
    public List<Object> logic;
    public Boolean saveOnEnter;
    public String inputMask;
    public String spellcheck;
    public String pattern;
}
